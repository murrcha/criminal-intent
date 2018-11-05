package com.bignerdranch.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * TimePickerFragment
 *
 * @author Ksenya Kaysheva (murrcha@me.com)
 * @since 05.11.2018
 */
public class TimePickerFragment extends DialogFragment {

    public static final String EXTRA_TIME = "time";

    private static final String ARG_TIME = "time";

    private TimePicker timePicker;

    public static TimePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Date date = (Date) getArguments().getSerializable(ARG_TIME);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time, null);
        final TimePicker timePicker = view.findViewById(R.id.dialog_time_picker);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int hour = timePicker.getCurrentHour();
                        int minute = timePicker.getCurrentMinute();
                        calendar.set(Calendar.HOUR, hour);
                        calendar.set(Calendar.MINUTE, minute);
                        date.setTime(calendar.getTimeInMillis());
                        sendResult(Activity.RESULT_OK, date);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
