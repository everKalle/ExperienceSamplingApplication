package com.example.madiskar.experiencesamplingapp;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import java.util.ArrayList;


public class StudyFragment extends ListFragment {

    private Handler mHandler = new Handler();
    private Boolean from_menu;
    //private ActiveStudyListAdapter asla;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        from_menu = args.getBoolean("fromNav");

        new AsyncTask<Void, Void, ArrayList<Study>>() {
            @Override
            protected ArrayList<Study> doInBackground(Void... params) {
                DBHandler mydb = DBHandler.getInstance(getActivity());
                return mydb.getAllStudies();
            }
            @Override
            protected void onPostExecute(final ArrayList<Study> results) {
                if(from_menu) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ActiveStudyListAdapter asla = new ActiveStudyListAdapter(getActivity(), results);
                            setListAdapter(asla);
                        }
                    }, 230); //Time for nav driver to close, for nice animations
                } else {
                    ActiveStudyListAdapter asla = new ActiveStudyListAdapter(getActivity(), results);
                    setListAdapter(asla);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }


}