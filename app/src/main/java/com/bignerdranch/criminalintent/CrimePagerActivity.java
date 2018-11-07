package com.bignerdranch.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bignerdranch.criminalintent.pojo.Crime;
import com.bignerdranch.criminalintent.database.CrimeLab;

import java.util.List;
import java.util.UUID;

/**
 * CrimePagerActivity
 *
 * @author Ksenya Kaysheva (murrcha@me.com)
 * @since 05.11.2018
 */
public class CrimePagerActivity extends AppCompatActivity {

    private static final String EXTRA_CRIME_ID = "crime_id";

    private ViewPager viewPager;
    private List<Crime> crimes;
    private Button firstPageButton;
    private Button lastPageButton;

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        UUID crimeId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_CRIME_ID);

        viewPager = findViewById(R.id.crime_view_pager);
        crimes = CrimeLab.get(this).getCrimes();
        firstPageButton = findViewById(R.id.first_page_button);
        firstPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
            }
        });
        lastPageButton = findViewById(R.id.last_page_button);
        lastPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    firstPageButton.setEnabled(false);
                } else {
                    firstPageButton.setEnabled(true);
                }
                if (i == viewPager.getAdapter().getCount() - 1) {
                    lastPageButton.setEnabled(false);
                } else {
                    lastPageButton.setEnabled(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = crimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return crimes.size();
            }
        });
        for (int index = 0; index < crimes.size(); index++) {
            if (crimes.get(index).getId().equals(crimeId)) {
                viewPager.setCurrentItem(index);
                if (index == 0) {
                    firstPageButton.setEnabled(false);
                }
                break;
            }
        }
    }
}
