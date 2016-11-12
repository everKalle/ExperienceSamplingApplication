package com.example.madiskar.experiencesamplingapp;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Joosep on 12.11.2016.
 */

public class JoinStudyFragment extends Fragment {

    private EditText keywordsEditText;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private CheckBox keywordsCheckbox;
    private CheckBox matchAllCheckbox;
    private CheckBox startDateCheckBox;
    private CheckBox endDateCheckBox;
    private Button searchButton;
    private ArrayList<Study> studies;
    private ArrayList<Study> filteredStudies;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_studies_layout, null);

        keywordsEditText = (EditText) view.findViewById(R.id.keywords_input);
        startDatePicker = (DatePicker) view.findViewById(R.id.startDatePicker);
        endDatePicker = (DatePicker) view.findViewById(R.id.endDatePicker);
        keywordsCheckbox = (CheckBox) view.findViewById(R.id.keywordsCheckBox);
        matchAllCheckbox = (CheckBox) view.findViewById(R.id.keywordsCheckBoxAll);
        startDateCheckBox = (CheckBox) view.findViewById(R.id.startDateCheckBox);
        endDateCheckBox = (CheckBox) view.findViewById(R.id.endDateCheckBox);
        searchButton = (Button) view.findViewById(R.id.button_search);

        DBHandler myDb = DBHandler.getInstance(getActivity().getApplicationContext());

        studies = myDb.getAllStudies();
        filteredStudies = new ArrayList<>();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (keywordsCheckbox.isChecked())
                    filterKeyWord();
                if (startDateCheckBox.isChecked() && endDateCheckBox.isChecked())
                    filterDate();
                else {
                    if (startDateCheckBox.isChecked())
                        filterStartDate();
                    if (endDateCheckBox.isChecked())
                        filterEndDate();
                }
                for (Study study: filteredStudies)
                    Log.v("filtered", study.getName());
                if (filteredStudies.isEmpty())
                    Toast.makeText(getActivity().getApplicationContext(), "No such studies found", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    public void filterKeyWord() {

        String keywords = keywordsEditText.getText().toString().toLowerCase();
        String[] keywordsArray = keywords.split(",");
        Set<String> keywordsSet = new HashSet<String>(Arrays.asList(keywordsArray));

        for (Study s: studies) {
            String studyName = s.getName().toLowerCase();

            /*String[] keywordsInStudy = studyName.split("\\W");
            Set<String> studyKeywordsSet = new HashSet<String>(Arrays.asList(keywordsInStudy));

            if (!Collections.disjoint(keywordsSet,studyKeywordsSet)) // if the two sets have any keywords in common, add the study to the filteredStudies list
                filteredStudies.add(s);
            */

            if (!matchAllCheckbox.isChecked()) {
                for (String keyword : keywordsArray) {
                    if (studyName.matches(".*" + keyword + ".*")) {
                        if (!filteredStudies.contains(s))
                            filteredStudies.add(s);
                    }
                }
            }
            else {
                boolean matches = true;
                for (String keyword : keywordsArray) {
                    if (!(studyName.matches(".*" + keyword + ".*"))) {
                        matches = false;
                    }
                }
                if (matches)
                    if (!filteredStudies.contains(s))
                        filteredStudies.add(s);
            }
        }

    }

    private void filterStartDate() {

        int year = startDatePicker.getYear();
        int month = startDatePicker.getMonth();
        int day = startDatePicker.getDayOfMonth();

        if (!keywordsCheckbox.isChecked() || keywordsEditText.getText().toString().equals(""))
            filteredStudies = studies;

        ArrayList<Study> filteredStudiesClone = new ArrayList<>(filteredStudies);

        try {
            for (Study s : filteredStudiesClone) {
                Calendar studyStartDate = s.getBeginDate();
                int studyYear = studyStartDate.get(Calendar.YEAR);
                int studyMonth = studyStartDate.get(Calendar.MONTH);
                int studyDay = studyStartDate.get(Calendar.DAY_OF_MONTH);

                if (year != studyYear || month != studyMonth || day != studyDay)
                    filteredStudies.remove(s);
            }
        } catch (Exception e) {
        }
    }

    private void filterEndDate() {

        if (!keywordsCheckbox.isChecked() && !startDateCheckBox.isChecked() || !keywordsEditText.getText().toString().equals("") && !startDateCheckBox.isChecked())
            filteredStudies = studies;

        int year = endDatePicker.getYear();
        int month = endDatePicker.getMonth();
        int day = endDatePicker.getDayOfMonth();

        ArrayList<Study> filteredStudiesClone = new ArrayList<>(filteredStudies);


        try {
            for (Study s : filteredStudiesClone) {
                Calendar studyEndDate = s.getEndDate();
                int studyYear = studyEndDate.get(Calendar.YEAR);
                int studyMonth = studyEndDate.get(Calendar.MONTH);
                int studyDay = studyEndDate.get(Calendar.DAY_OF_MONTH);

                if (year != studyYear || month != studyMonth || day != studyDay) {
                    filteredStudies.remove(s);
                }
            }
        } catch (Exception e) {
        }
    }

    private void filterDate() {

        if (!keywordsCheckbox.isChecked() || keywordsEditText.getText().toString().equals(""))
            filteredStudies = studies;

        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();

        int startYear = startDatePicker.getYear();
        int startMonth = startDatePicker.getMonth();
        int startDay = startDatePicker.getDayOfMonth();

        int endYear = endDatePicker.getYear();
        int endMonth = endDatePicker.getMonth();
        int endDay = endDatePicker.getDayOfMonth();

        startCalendar.set(startYear, startMonth, startDay, 0, 0);
        endCalendar.set(endYear, endMonth, endDay, 0, 0);

        ArrayList<Study> filteredStudiesClone = new ArrayList<>(filteredStudies);

        try {
            for (Study s : filteredStudiesClone) {
                Calendar studyStartDate = s.getBeginDate();
                Calendar studyEndDate = s.getEndDate();

                studyStartDate.set(studyStartDate.get(Calendar.YEAR), studyStartDate.get(Calendar.MONTH), studyStartDate.get(Calendar.DAY_OF_MONTH),0 , 0);
                studyEndDate.set(studyEndDate.get(Calendar.YEAR), studyEndDate.get(Calendar.MONTH), studyEndDate.get(Calendar.DAY_OF_MONTH),0 , 0);

                if (!((studyStartDate.after(startCalendar) || (!studyStartDate.after(startCalendar) && !studyStartDate.before(startCalendar))) && (studyEndDate.before(endCalendar)
                        || (!studyEndDate.before(endCalendar) && !studyEndDate.after(endCalendar))) || (!studyStartDate.after(startCalendar) && !studyStartDate.before(startCalendar))
                && (!studyEndDate.after(endCalendar) && !studyEndDate.before(endCalendar)))){
                    filteredStudies.remove(s);
                }
            }
        } catch (Exception e) {
        }
    }
}