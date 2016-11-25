package com.example.madiskar.experiencesamplingapp;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Joosep on 12.11.2016.
 */

public class SearchFragment extends ListFragment {

    private ArrayList<Study> filteredStudies;
    private ArrayList<Study> studies;

    private boolean keywordsCheckboxIsChecked;
    private boolean endDateCheckBoxIsChecked;
    private boolean matchAllCheckboxIsChecked;
    private boolean startDateCheckBoxIsChecked;
    private String keywordsEditText;
    private int startYear;
    private int startMonth;
    private int startDay;
    private int endYear;
    private int endMonth;
    private int endDay;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        filteredStudies = new ArrayList<>();

        Bundle bundle = getArguments();
        keywordsCheckboxIsChecked = bundle.getBoolean("keywordsCheckboxIsChecked");
        endDateCheckBoxIsChecked = bundle.getBoolean("endDateCheckBoxIsChecked");
        matchAllCheckboxIsChecked = bundle.getBoolean("matchAllCheckboxIsChecked");
        startDateCheckBoxIsChecked = bundle.getBoolean("startDateCheckBoxIsChecked");
        keywordsEditText = bundle.getString("keywordsEditText");
        startYear = bundle.getInt("startYear");
        startMonth = bundle.getInt("startMonth");
        startDay = bundle.getInt("startDay");
        endYear = bundle.getInt("endYear");
        endMonth = bundle.getInt("endMonth");
        endDay = bundle.getInt("endDay");


        /*  ListView generates its own "loading element", this should not be needed
        final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Searching, Please Wait...");
        progressDialog.show();
        */

        GetPublicStudiesTask getPublicStudiesTask = new GetPublicStudiesTask(new RunnableResponseArray() {
            @Override
            public void processFinish(String message, ArrayList<Study> study_list) {
                //TODO: maybe handle server responses here later?
                Log.i("PUBLIC STUDIES", message);
                studies = study_list;

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        if (keywordsCheckboxIsChecked)
                            filterKeyWord();
                        if (startDateCheckBoxIsChecked && endDateCheckBoxIsChecked)
                            filterDate();
                        else {
                            if (startDateCheckBoxIsChecked)
                                filterStartDate();
                            if (endDateCheckBoxIsChecked)
                                filterEndDate();
                        }
                        for (Study study: filteredStudies)
                            Log.v("filtered", study.getName());
                        if (filteredStudies.isEmpty()) {
                            return "failure";
                        } else {
                            return "success";
                        }
                    }
                    @Override
                    protected void onPostExecute(String response) {
                        //progressDialog.dismiss();  See above
                        if(response.equals("failure")) {
                            Toast.makeText(getActivity().getApplicationContext(), R.string.no_studies, Toast.LENGTH_SHORT).show();
                            SearchResultsListAdapter srla = new SearchResultsListAdapter(getActivity(), filteredStudies);
                            setListAdapter(srla);
                        } else {
                            SearchResultsListAdapter srla = new SearchResultsListAdapter(getActivity(), filteredStudies);
                            setListAdapter(srla);
                        }
                    }

                }.execute();
            }
        });

        ExecutorSupplier.getInstance().forBackgroundTasks().execute(getPublicStudiesTask);

    }


    public void filterKeyWord() {

        String keywords = keywordsEditText.toLowerCase();
        String[] keywordsArray = keywords.split(",");
        Set<String> keywordsSet = new HashSet<String>(Arrays.asList(keywordsArray));

        for (Study s: studies) {
            String studyName = s.getName().toLowerCase();

            /*String[] keywordsInStudy = studyName.split("\\W");
            Set<String> studyKeywordsSet = new HashSet<String>(Arrays.asList(keywordsInStudy));

            if (!Collections.disjoint(keywordsSet,studyKeywordsSet)) // if the two sets have any keywords in common, add the study to the filteredStudies list
                filteredStudies.add(s);
            */

            if (!matchAllCheckboxIsChecked) {
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

        if (!keywordsCheckboxIsChecked || keywordsEditText.equals(""))
            filteredStudies = new ArrayList<>(studies);

        ArrayList<Study> filteredStudiesClone = new ArrayList<>(filteredStudies);

        try {
            for (Study s : filteredStudiesClone) {
                Calendar studyStartDate = s.getBeginDate();
                int studyYear = studyStartDate.get(Calendar.YEAR);
                int studyMonth = studyStartDate.get(Calendar.MONTH);
                int studyDay = studyStartDate.get(Calendar.DAY_OF_MONTH);

                if (startYear != studyYear || startMonth != studyMonth || startDay != studyDay)
                    filteredStudies.remove(s);
            }
        } catch (Exception e) {
        }
    }


    private void filterEndDate() {

        if (!keywordsCheckboxIsChecked && !startDateCheckBoxIsChecked || !keywordsEditText.equals("") && !startDateCheckBoxIsChecked )
            filteredStudies = new ArrayList<>(studies);

        ArrayList<Study> filteredStudiesClone = new ArrayList<>(filteredStudies);


        try {
            for (Study s : filteredStudiesClone) {
                Calendar studyEndDate = s.getEndDate();
                int studyYear = studyEndDate.get(Calendar.YEAR);
                int studyMonth = studyEndDate.get(Calendar.MONTH);
                int studyDay = studyEndDate.get(Calendar.DAY_OF_MONTH);

                if (endYear != studyYear || endMonth != studyMonth || endDay != studyDay) {
                    filteredStudies.remove(s);
                }
            }
        } catch (Exception e) {
        }
    }


    private void filterDate() {

        if (!keywordsCheckboxIsChecked || keywordsEditText.equals(""))
            filteredStudies = new ArrayList<>(studies);

        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();

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
