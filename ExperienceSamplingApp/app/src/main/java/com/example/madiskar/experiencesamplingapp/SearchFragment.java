package com.example.madiskar.experiencesamplingapp;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;


public class SearchFragment extends ListFragment {

    private ArrayList<Study> filteredStudies;
    private ArrayList<Study> studies;

    private TextView noResultsTxt;
    private TextView progressText;
    private ProgressBar progressBar;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_search, container, false);

        noResultsTxt = (TextView) view.findViewById(R.id.no_results);

        noResultsTxt.setVisibility(View.GONE);

        progressBar = (ProgressBar) view.findViewById(R.id.resultsProgressBar);
        progressText = (TextView) view.findViewById(R.id.resultsProgressBarText);
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);

        GetPublicStudiesTask getPublicStudiesTask = new GetPublicStudiesTask(DBHandler.getInstance(getActivity().getApplicationContext()), new RunnableResponseArray() {
            @Override
            public void processFinish(String message, ArrayList<Study> study_list) {
                studies = study_list;
                filteredStudies = new ArrayList<>(studies);

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
                        if (filteredStudies.isEmpty()) {
                            return "failure";
                        } else {
                            return "success";
                        }
                    }
                    @Override
                    protected void onPostExecute(String response) {
                        progressBar.setVisibility(View.GONE);
                        progressText.setVisibility(View.GONE);
                        if(response.equals("failure")) {
                            noResultsTxt.setVisibility(View.VISIBLE);
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


        return view;
    }


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

    }


    public void filterKeyWord() {

        String keywords = keywordsEditText.toLowerCase();
        String[] keywordsArray = keywords.split(",");
        Set<String> keywordsSet = new HashSet<String>(Arrays.asList(keywordsArray));

        for (Study s: studies) {
            String studyName = s.getName().toLowerCase();

            if (!matchAllCheckboxIsChecked) {
                for (String keyword : keywordsArray) {
                    if (!studyName.matches(".*" + keyword + ".*")) {
                        if (filteredStudies.contains(s))
                            filteredStudies.remove(s);
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
                if (!matches)
                    if (filteredStudies.contains(s))
                        filteredStudies.remove(s);
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
