package com.bignerdranch.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.bignerdranch.criminalintent.pojo.Crime;
import com.bignerdranch.criminalintent.pojo.CrimeLab;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import static android.widget.CompoundButton.*;

/**
 * CrimeFragment
 *
 * @author Ksenya Kaysheva (murrcha@me.com)
 * @since 05.11.2018
 */
public class CrimeFragment extends Fragment {

    private static final String DATE_FORMAT = "EEE dd.MM.yyyy";
    private static final String TIME_FORMAT = "hh:mm:ss";
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;

    private Crime crime;
    private EditText titleField;
    private Button dateButton;
    private Button timeButton;
    private Button sendReportButton;
    private Button chooseSuspectButton;
    private CheckBox solvedCheckBox;
    private Button callSuspectButton;
    private UUID crimeId;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void updateDate() {
        dateButton.setText(DateFormat.format(DATE_FORMAT, crime.getDate()).toString());
    }

    private void updateTime() {
        timeButton.setText(DateFormat.format(TIME_FORMAT, crime.getDate()).toString());
    }

    private String getCrimeReport() {
        String solved;
        if (crime.isSolved()) {
            solved = getString(R.string.crime_report_solved);
        } else {
            solved = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String date = DateFormat.format(dateFormat, crime.getDate()).toString();
        String suspect = crime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        return getString(R.string.crime_report, crime.getTitle(), date, solved, suspect);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crimeId = (UUID) Objects.requireNonNull(getArguments()).getSerializable(ARG_CRIME_ID);
        crime = CrimeLab.get(getActivity()).getCrime(crimeId);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(crime);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        titleField = v.findViewById(R.id.crime_title);
        titleField.setText(crime.getTitle());
        titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                crime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dateButton = v.findViewById(R.id.crime_date);
        updateDate();
        dateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(crime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });
        timeButton = v.findViewById(R.id.crime_time);
        updateTime();
        timeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment
                        .newInstance(crime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });
        solvedCheckBox = v.findViewById(R.id.crime_solved);
        solvedCheckBox.setChecked(crime.isSolved());
        solvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                crime.setSolved(isChecked);
            }
        });
        sendReportButton = v.findViewById(R.id.send_report);
        sendReportButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ShareCompat.IntentBuilder.from(Objects.requireNonNull(getActivity()))
                        .setType("text/plain")
                        .getIntent()
                        .setAction(Intent.ACTION_SEND)
                        .putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                        .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                intent = Intent.createChooser(intent, getString(R.string.send_report_via));
                startActivity(intent);
            }
        });
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        chooseSuspectButton = v.findViewById(R.id.choose_suspect);
        chooseSuspectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if (crime.getSuspect() != null) {
            chooseSuspectButton.setText(crime.getSuspect());
        }
        callSuspectButton = v.findViewById(R.id.call_suspect);
        if (crime.getNumber() != null) {
            callSuspectButton.setText(crime.getNumber());
        } else {
            callSuspectButton.setText(R.string.no_number);
        }
        callSuspectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri number = Uri.parse(String.format("tel:%s", callSuspectButton.getText()));
                Intent intent = ShareCompat.IntentBuilder.from(Objects.requireNonNull(getActivity()))
                        .getIntent()
                        .setAction(Intent.ACTION_DIAL)
                        .setData(number);
                startActivity(intent);
            }
        });
        PackageManager packageManager = Objects.requireNonNull(getActivity()).getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            chooseSuspectButton.setEnabled(false);
            callSuspectButton.setEnabled(false);
        }
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        Date date;
        if (requestCode == REQUEST_DATE) {
            date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            crime.setDate(date);
            updateDate();
        }
        if (requestCode == REQUEST_TIME) {
            date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            crime.setDate(date);
            updateTime();
        }
        if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[] {ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID};
            try (Cursor contactCursor = Objects.requireNonNull(getActivity())
                    .getContentResolver()
                    .query(Objects.requireNonNull(contactUri), queryFields, null, null, null)
            ) {
                if (Objects.requireNonNull(contactCursor).getCount() == 0) {
                    return;
                }
                contactCursor.moveToFirst();
                String suspect = contactCursor.getString(0);
                String contactId = contactCursor.getString(1); //for query phone number
                crime.setSuspect(suspect);
                chooseSuspectButton.setText(suspect);
                try (Cursor phoneCursor = getActivity()
                        .getContentResolver()
                        .query(Phone.CONTENT_URI,
                                null,
                                String.format("%s = %s", Phone.CONTACT_ID, contactId),
                                null,
                                null)) {
                    if (Objects.requireNonNull(phoneCursor).getCount() == 0) {
                        return;
                    }
                    phoneCursor.moveToFirst();
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER));
                    if (number != null) {
                        callSuspectButton.setText(number);
                        crime.setNumber(number);
                    } else {
                        callSuspectButton.setText(R.string.no_number);
                    }
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(crime);
                Objects.requireNonNull(getActivity()).finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
