package com.example.madiskar.experiencesamplingapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.madiskar.experiencesamplingapp.data_types.BeepFerePeriod;
import com.example.madiskar.experiencesamplingapp.R;

import java.util.ArrayList;
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
        try {
            mListener = (BeepFreePeriodListener) activity;
        } catch (ClassCastException e) {
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Bundle args = getArguments();

        final boolean newValue = args.getBoolean("new");
        int identificator = args.getInt("identificator");
        final ArrayList<Integer> existingStartHours = args.getIntegerArrayList("existingStartHours");
        final ArrayList<Integer> existingStartMinutes = args.getIntegerArrayList("existingStartMinutes");
        final ArrayList<Integer> existingEndHours = args.getIntegerArrayList("existingEndHours");
        final ArrayList<Integer> existingEndMinutes = args.getIntegerArrayList("existingEndMinutes");

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

        builder.setTitle(R.string.set_beepfree);


        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mListener.onDialogNegativeClick(BeepfreePeriodPickerFragment.this);
                        getDialog().dismiss();
                    }
                }
        );
        builder.setPositiveButton(R.string.set,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!newValue) {
                            boolean overlap = false;
                            try {
                                overlap = checkBeepFreeOverlap(editBeepFerePeriod, existingStartHours, existingEndHours, existingStartMinutes, existingEndMinutes, true);
                            } catch (Exception e) {
                            }
                            if (!overlap) {
                                mListener.onDialogUpdateObject(BeepfreePeriodPickerFragment.this);
                                getDialog().dismiss();
                            }
                            else
                                Toast.makeText(getContext(), R.string.overlap, Toast.LENGTH_LONG).show();
                        }
                        else {
                           boolean overlap = checkBeepFreeOverlap(beepFerePeriod, existingStartHours, existingEndHours, existingStartMinutes, existingEndMinutes, false);
                            if (!overlap) {
                                mListener.onDialogPositiveClick(BeepfreePeriodPickerFragment.this);
                                getDialog().dismiss();
                            }
                            else
                                Toast.makeText(getContext(), R.string.overlap, Toast.LENGTH_LONG).show();
                        }
                    }
                });


        return  builder.create();
    }

    public static boolean checkBeepFreeOverlap(BeepFerePeriod beepFerePeriod, ArrayList<Integer> existingStartHours, ArrayList<Integer> existingEndHours, ArrayList<Integer> existingStartMinutes, ArrayList<Integer> existingEndMinutes, boolean edit) {

        boolean overlap = false;
        for (int i = 0; i < existingStartHours.size(); i++) {

            Calendar calendarBeepStart = Calendar.getInstance();
            Calendar calendarBeepEnd = Calendar.getInstance();
            Calendar calendarExistingStart = Calendar.getInstance();
            Calendar calendarExistingEnd = Calendar.getInstance();

            calendarBeepStart.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, beepFerePeriod.getStartTimeHour(), beepFerePeriod.getStartTimeMinute());
            calendarBeepEnd.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, beepFerePeriod.getEndTimeHour(), beepFerePeriod.getEndTimeMinute());
            calendarExistingStart.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, existingStartHours.get(i), existingStartMinutes.get(i));
            calendarExistingEnd.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, existingEndHours.get(i),existingEndMinutes.get(i));


           if (edit && i == beepFerePeriod.getId()) {
               continue;
           }

            if (existingStartHours.get(i) > existingEndHours.get(i)) { // 22.00 - 2.30
                if (beepFerePeriod.getStartTimeHour() > beepFerePeriod.getEndTimeHour() || beepFerePeriod.getStartTimeHour() == beepFerePeriod.getEndTimeHour() && beepFerePeriod.getStartTimeMinute() > beepFerePeriod.getEndTimeMinute()) { // 20.00 - 4.00
                    overlap = true;
                }
                else { // 1.00 - 13.00
                    if (beepFerePeriod.getStartTimeHour() <= existingEndHours.get(i)) { // 13.00 - 15.00 and 23.00 - 18.0
                        if (beepFerePeriod.getStartTimeHour() == existingEndHours.get(i) && beepFerePeriod.getStartTimeMinute() > existingEndMinutes.get(i)
                                && beepFerePeriod.getEndTimeHour() <= existingStartHours.get(i)) { // 12.55 - 22.40 and 22.30 - 12.50
                            if (beepFerePeriod.getEndTimeHour() == existingStartHours.get(i) && beepFerePeriod.getEndTimeMinute() < existingStartMinutes.get(i)) { // 12.55 - 22.00 and 22.30 - 12.50
                            }
                            else {
                                if (beepFerePeriod.getEndTimeHour() <= existingStartHours.get(i)) {
                                    if (beepFerePeriod.getEndTimeHour() == existingStartHours.get(i) && existingStartMinutes.get(i) < beepFerePeriod.getEndTimeMinute())
                                        overlap = true;
                                }
                                else {
                                    overlap = true;
                                }
                            }
                        }
                        else {
                            overlap = true;
                        }
                    }
                    else {
                        calendarExistingEnd.add(Calendar.DATE, 1);

                        if (beepFerePeriod.getStartTimeHour() > beepFerePeriod.getEndTimeHour()) {
                            calendarBeepEnd.add(Calendar.DATE, 1);
                        }
                        if (calendarBeepStart.after(calendarExistingStart) && calendarBeepStart.before(calendarExistingEnd) ||
                                calendarBeepStart.get(Calendar.HOUR) == calendarExistingStart.get(Calendar.HOUR) &&
                                calendarBeepStart.get(Calendar.MINUTE) == calendarExistingStart.get(Calendar.MINUTE) && calendarBeepStart.before(calendarExistingEnd) ||
                                calendarBeepStart.get(Calendar.HOUR) == calendarExistingStart.get(Calendar.HOUR) &&
                                        calendarBeepStart.get(Calendar.MINUTE) == calendarExistingStart.get(Calendar.MINUTE) && calendarBeepEnd.get(Calendar.HOUR) == calendarExistingEnd.get(Calendar.HOUR) &&
                                calendarBeepEnd.get(Calendar.MINUTE) == calendarExistingEnd.get(Calendar.MINUTE))
                            overlap = true;

                    }
                }
            }
            else { // 13.00 - 18.00 or 18.00 - 18.00
                if (beepFerePeriod.getStartTimeHour() > beepFerePeriod.getEndTimeHour()) { // 23.00 - 1.30
                    if (beepFerePeriod.getStartTimeHour() > existingEndHours.get(i)) {  // 23.00 - 1.30 and 1.00 - 22.00
                        if (beepFerePeriod.getEndTimeHour() < existingStartHours.get(i)) { // 23.00 - 2.00 and  3.00 - 22.00
                        }
                        else { //beepFerePeriod.getEndTimeHour() >= existingStartHours.get(i) -> ... - 3.00 and 1.00 - ...
                            if (beepFerePeriod.getEndTimeHour() == existingStartHours.get(i) && beepFerePeriod.getEndTimeMinute() < existingStartMinutes.get(i)) { // ... - 3.00 and 3.30 - ...
                            }
                            else {
                                overlap = true;
                            }
                        }
                    }
                    else { //beepFerePeriod.getStartTimeHour() <= existingEndHours.get(i) -> 21.00 - 1.30 and 13.00 - 23.00
                        if (beepFerePeriod.getStartTimeHour() == existingEndHours.get(i) && beepFerePeriod.getStartTimeMinute() > existingEndMinutes.get(i)) { // 21.40 - 1.30 and 13.00 - 21.30
                            if (beepFerePeriod.getEndTimeHour() <= existingStartHours.get(i)) { // 21.40 - 1.30 and 13.00 - 21.30
                                if (beepFerePeriod.getEndTimeHour() == existingStartHours.get(i) && beepFerePeriod.getEndTimeMinute() >= existingStartMinutes.get(i)) { // 21.40 - 13.30 and 13.00 - 21.30
                                    overlap = true;
                                }
                            }
                            else { // beepFerePeriod.getEndTimeHour() > existingStartHours.get(i) ->  21.40 - 14.30 and 13.00 - 21.30
                                overlap = true;
                            }
                        }
                        else { //beepFerePeriod.getStartTimeHour() < existingEndHours.get(i) -> 21.00 - 1.30 and 1.00 - 22.00
                            overlap = true;
                        }
                    }
                }
                // 3.00 - 3.29 and 3.30 - 5.30
                else { // beepFerePeriod.getStartTimeHour() <= beepFerePeriod.getEndTimeHour() -> 13.00 - 18.00 | existingStartHours.get(i) <= existingEndHours.get(i)
                    if (beepFerePeriod.getStartTimeHour() >= existingStartHours.get(i) && beepFerePeriod.getEndTimeHour() <= existingEndHours.get(i)) {
                        if (beepFerePeriod.getStartTimeHour() == existingStartHours.get(i) && beepFerePeriod.getEndTimeHour() == existingEndHours.get(i) &&
                                beepFerePeriod.getStartTimeHour() == beepFerePeriod.getEndTimeHour() || beepFerePeriod.getStartTimeHour() == beepFerePeriod.getEndTimeHour()
                                && beepFerePeriod.getStartTimeHour() == existingStartHours.get(i) || beepFerePeriod.getStartTimeHour() == beepFerePeriod.getEndTimeHour()
                                && beepFerePeriod.getEndTimeHour() == existingEndHours.get(i)) {
                            if (beepFerePeriod.getEndTimeHour() < existingStartHours.get(i) || beepFerePeriod.getEndTimeHour() == existingStartHours.get(i)
                                    && beepFerePeriod.getEndTimeMinute() < existingStartMinutes.get(i) || beepFerePeriod.getStartTimeMinute() > existingEndMinutes.get(i)
                                    && beepFerePeriod.getStartTimeHour() == existingEndHours.get(i)) {
                            }
                            else {
                                overlap = true;
                            }
                        }
                        else {
                            overlap = true;
                        }
                    }
                    else { // 1.30 - 3.00 and 1.00 - 2.00
                        if (beepFerePeriod.getStartTimeHour() < existingStartHours.get(i) && beepFerePeriod.getEndTimeHour() >= existingStartHours.get(i)) { //  13.00 - 22.00 and 18.00 - 23.00
                            if (beepFerePeriod.getEndTimeHour() == existingStartHours.get(i)) { // 13.00 - 22.00 and 22.30 - 23.00
                                if (beepFerePeriod.getEndTimeMinute() >= existingStartMinutes.get(i)) {
                                    overlap = true;
                                }
                            }
                            else {
                                overlap = true;
                            }
                        }
                        //  5.33 - 21.56 and 3.30 - 5.30
                        else if (beepFerePeriod.getStartTimeHour() <= existingEndHours.get(i) && beepFerePeriod.getEndTimeHour() >= existingEndHours.get(i)) {
                            if (beepFerePeriod.getStartTimeHour() == existingEndHours.get(i) && beepFerePeriod.getStartTimeMinute() > existingEndMinutes.get(i)) {
                            }
                            else {
                                overlap = true;
                            }
                        }

                        else if (beepFerePeriod.getStartTimeHour() >= existingEndHours.get(i) && beepFerePeriod.getEndTimeHour() >= existingEndHours.get(i)) {
                            if (existingEndHours.get(i) >= beepFerePeriod.getStartTimeHour()) {
                                if (existingEndHours.get(i) == beepFerePeriod.getStartTimeHour()) {
                                    if (existingEndMinutes.get(i) >= beepFerePeriod.getStartTimeMinute()){
                                        overlap = true;
                                    }
                                }
                                else {
                                    overlap = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return overlap;
    }

    public BeepFerePeriod getCreatedBeepFreePeriod() {
        return beepFerePeriod;
    }

    public BeepFerePeriod getEditedBeepFreePeriod() {
        return editBeepFerePeriod;
    }
}
