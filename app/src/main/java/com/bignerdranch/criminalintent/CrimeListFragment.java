package com.bignerdranch.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * CrimeListFragment
 *
 * @author Ksenya Kaysheva (murrcha@me.com)
 * @since 05.11.2018
 */
public class CrimeListFragment extends Fragment {

    private static final String DEFAULT_FORMAT = "EEE dd.MM.yyyy hh:mm:ss";
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView crimeRecyclerView;
    private CrimeAdapter crimeAdapter;
    private TextView emptyView;
    private int position;
    private boolean subtitleVisible;
    List<Crime> crimes;

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        crimes = crimeLab.getCrimes();
        if (crimeAdapter == null) {
            crimeAdapter = new CrimeAdapter(crimes);
            crimeRecyclerView.setAdapter(crimeAdapter);
        } else {
            crimeAdapter.setCrimes(crimes);
            crimeAdapter.notifyItemRemoved(position);
            crimeAdapter.notifyItemChanged(position);
        }
        updateSubtitle();
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        int crimeSize = CrimeLab.get(getContext()).getCrimes().size();
        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural, crimeSize, crimeSize);
        if (!subtitleVisible) {
            subtitle = null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (subtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                subtitleVisible = !subtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        emptyView = view.findViewById(R.id.empty_view);
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        crimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (savedInstanceState != null) {
            subtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        updateUI();
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, subtitleVisible);
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView titleTextView;
        private TextView dateTextView;
        private ImageView solvedImageView;
        private Crime crime;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            itemView.setOnClickListener(this);
            titleTextView = itemView.findViewById(R.id.crime_title);
            dateTextView = itemView.findViewById(R.id.crime_date);
            solvedImageView = itemView.findViewById(R.id.crime_solved);
        }

        public CrimeHolder(View view) {
            super(view);
            itemView.setOnClickListener(this);
            titleTextView = itemView.findViewById(R.id.crime_title);
            dateTextView = itemView.findViewById(R.id.crime_date);
            solvedImageView = itemView.findViewById(R.id.crime_solved);
        }

        private void bind(Crime crime) {
            this.crime = crime;
            titleTextView.setText(this.crime.getTitle());
            dateTextView.setText(DateFormat.format(DEFAULT_FORMAT, this.crime.getDate()).toString());
            solvedImageView.setVisibility(this.crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
            position = getAdapterPosition();
            Intent intent = CrimePagerActivity.newIntent(getActivity(), this.crime.getId());
            startActivity(intent);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private static final int ITEM_TYPE_DEFAULT = 0;
        private static final int ITEM_TYPE_POLICE = 1;

        private List<Crime> crimes;

        public CrimeAdapter(List<Crime> crimes) {
            this.crimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == ITEM_TYPE_POLICE) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_crime_police, parent, false);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_crime, parent, false);
            }
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder crimeHolder, int position) {
            Crime crime = crimes.get(position);
            crimeHolder.bind(crime);
        }

        @Override
        public int getItemCount() {
            if (crimes.size() == 0) {
                crimeRecyclerView.setVisibility(View.INVISIBLE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                crimeRecyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.INVISIBLE);
            }
            return crimes.size();
        }

        public void setCrimes(List<Crime> crimes) {
            this.crimes = crimes;
        }

        @Override
        public int getItemViewType(int position) {
            int itemType = ITEM_TYPE_DEFAULT;
            if (crimes.get(position).isRequiresPolice()) {
                itemType = ITEM_TYPE_POLICE;
            }
            return itemType;
        }
    }
 }
