package com.example.madiskar.experiencesamplingapp;

import android.app.Fragment;
import android.app.FragmentManager;
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

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                Fragment fragment = new SearchFragment();
                Bundle args = new Bundle();
                args.putBoolean("keywordsCheckboxIsChecked", keywordsCheckbox.isChecked());
                args.putBoolean("matchAllCheckboxIsChecked", matchAllCheckbox.isChecked());
                args.putBoolean("startDateCheckBoxIsChecked", startDateCheckBox.isChecked());
                args.putBoolean("endDateCheckBoxIsChecked", endDateCheckBox.isChecked());
                args.putInt("startYear", startDatePicker.getYear());
                args.putInt("startMonth", startDatePicker.getMonth());
                args.putInt("startDay", startDatePicker.getDayOfMonth());
                args.putInt("endYear", endDatePicker.getYear());
                args.putInt("endMonth", endDatePicker.getMonth());
                args.putInt("endDay", endDatePicker.getDayOfMonth());
                args.putString("keywordsEditText", keywordsEditText.getText().toString());
                fragment.setArguments(args);
                fragmentManager.beginTransaction()
                        .replace(R.id.mainContent, fragment)
                        .commit();
            }
        });

        return view;

    }

}