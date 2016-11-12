package com.example.madiskar.experiencesamplingapp;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import java.util.ArrayList;

/**
 * Created by Joosep on 12.11.2016.
 */

public class SearchFragment extends ListFragment {

    private Handler mHandler = new Handler();
    private Boolean from_menu;
    //private ActiveStudyListAdapter asla;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SearchResultsListAdapter srla = new SearchResultsListAdapter(getActivity(), JoinStudyFragment.filteredStudies);
        setListAdapter(srla);

    }
}
