package com.example.madiskar.experiencesamplingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Calendar;

public class BeepfreePeriodPickerFragment extends DialogFragment {

    private static int startTimeHour = 0;
    private static int startTimeMinute = 0;
    private static int endTimeHour = 0;
    private static int endTimeMinute = 0;
    private boolean newValue;
    private BeepFerePeriod beepFerePeriod;
    private BeepFerePeriod editBeepFerePeriod;

    public BeepfreePeriodPickerFragment() {
        beepFerePeriod = new BeepFerePeriod();
        editBeepFerePeriod = new BeepFerePeriod();
    }

    BeepFreePeriodListener mListener;

    public interface BeepFreePeriodListener {
        public void onDialogPositiveClick(BeepfreePeriodPickerFragment dialog);
        public void onDialogNegativeClick(BeepfreePeriodPickerFragment dialog);
        public void onDialogUpdateObject(BeepfreePeriodPickerFragment dialog);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (BeepFreePeriodListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Bundle args = getArguments();

        final boolean newValue = args.getBoolean("new");
        int identificator = args.getInt("identificator");



        if (newValue) {
            beepFerePeriod.setId(identificator);
        }
        else {
            int id2 = args.getInt("id");
            int startHour = args.getInt("startHour");
            int startMinute = args.getInt("startMinute");
            int endHour = args.getInt("endHour");
            int endMinute = args.getInt("endMinute");

            editBeepFerePeriod = new BeepFerePeriod(id2, startHour, startMinute, endHour, endMinute);
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.time_picker_layout, null);
        TimePicker timePicker1 = (TimePicker) view.findViewById(R.id.startTimePicker);
        timePicker1.setIs24HourView(true);
        if (!newValue) {
            int startHour = args.getInt("startHour");
            int startMinute = args.getInt("startMinute");
            timePicker1.setCurrentHour(startHour);
            timePicker1.setCurrentMinute(startMinute);
        }
        else {
            timePicker1.setCurrentHour(0);
            timePicker1.setCurrentMinute(0);
        }

        TimePicker timePicker2 = (TimePicker) view.findViewById(R.id.endTimePicker);
        timePicker2.setIs24HourView(true);
        if (!newValue) {
            int endHour = args.getInt("endHour");
            int endMinute = args.getInt("endMinute");
            timePicker2.setCurrentHour(endHour);
            timePicker2.setCurrentMinute(endMinute);
        }
        else {
            timePicker2.setCurrentHour(0);
            timePicker2.setCurrentMinute(0);
        }

        timePicker1.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                startTimeHour = hourOfDay;
                startTimeMinute = minute;
                if (newValue) {
                    beepFerePeriod.setStartTimeHour(hourOfDay);
                    beepFerePeriod.setStartTimeMinute(minute);
                }
                else {
                    editBeepFerePeriod.setStartTimeHour(hourOfDay);
                    editBeepFerePeriod.setStartTimeMinute(minute);
                }
            }
        });

        timePicker2.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                endTimeHour = hourOfDay;
                endTimeMinute = minute;
                if (newValue) {
                    beepFerePeriod.setEndTimeHour(hourOfDay);
                    beepFerePeriod.setEndTimeMinute(minute);
                }
                else {
                    editBeepFerePeriod.setEndTimeHour(hourOfDay);
                    editBeepFerePeriod.setEndTimeMinute(minute);
                }
            }
        });

        builder.setView(view);

        builder.setTitle("Set beepfree period");


        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mListener.onDialogNegativeClick(BeepfreePeriodPickerFragment.this);
                        getDialog().dismiss();
                    }
                }
        );
        builder.setPositiveButton("Set",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!newValue)
                            mListener.onDialogUpdateObject(BeepfreePeriodPickerFragment.this);
                        else
                            mListener.onDialogPositiveClick(BeepfreePeriodPickerFragment.this);

                        getDialog().dismiss();
                    }
                });


        return  builder.create();
    }

    public BeepFerePeriod getCreatedBeepFreePeriod() {
        return beepFerePeriod;
    }

    public BeepFerePeriod getEditedBeepFreePeriod() {
        return editBeepFerePeriod;
    }
}
