package com.example.madiskar.experiencesamplingapp.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.madiskar.experiencesamplingapp.R;


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

        keywordsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    matchAllCheckbox.setEnabled(true);
                else
                    matchAllCheckbox.setEnabled(false);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()) {
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
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.no_internet) , Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}