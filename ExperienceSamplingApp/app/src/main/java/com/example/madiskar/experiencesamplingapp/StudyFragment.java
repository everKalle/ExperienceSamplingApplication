package com.example.madiskar.experiencesamplingapp;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class StudyFragment extends ListFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_study, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DBHandler mydb = new DBHandler(getActivity());

        ArrayList<Study> studies = mydb.getAllStudies();

        ActiveStudyListAdapter asla = new ActiveStudyListAdapter(getActivity(), studies);
        setListAdapter(asla);

    }


}