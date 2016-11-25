package com.example.madiskar.experiencesamplingapp;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class StudyFragment extends ListFragment {

    private Handler mHandler = new Handler();
    private Boolean from_menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        from_menu = args.getBoolean("fromNav");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_study, container, false);

        final ProgressBar progress = (ProgressBar) view.findViewById(R.id.myStudiesProgressBar);
        final FloatingActionButton floatUpdate = (FloatingActionButton) view.findViewById(R.id.floatingUpdateButton);
        final TextView progressText = (TextView) view.findViewById(R.id.myStudiesProgressBarText);
        progress.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        floatUpdate.setVisibility(View.GONE);

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
                            progress.setVisibility(View.GONE);
                            progressText.setVisibility(View.GONE);
                            floatUpdate.setVisibility(View.VISIBLE);
                            ActiveStudyListAdapter asla = new ActiveStudyListAdapter(getActivity(), results);
                            setListAdapter(asla);
                        }
                    }, 230); //Time for nav driver to close, for nice animations
                } else {
                    progress.setVisibility(View.GONE);
                    progressText.setVisibility(View.GONE);
                    floatUpdate.setVisibility(View.VISIBLE);
                    ActiveStudyListAdapter asla = new ActiveStudyListAdapter(getActivity(), results);
                    setListAdapter(asla);
                }
            }
        }.execute();

        floatUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences pref = view.getContext().getApplicationContext().getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);

                final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage(getString(R.string.update_studies));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                SyncStudyDataTask syncStudyDataTask = new SyncStudyDataTask(pref.getString("token", "none"), DBHandler.getInstance(view.getContext()), new StudyDataSyncResponse() {
                    @Override
                    public void processFinish(String output, ArrayList<Study> newStudies) {
                        progressDialog.dismiss();
                        if(output.equals("invalid_token")) {
                            Toast.makeText(view.getContext(), view.getContext().getString(R.string.auth_sync_fail), Toast.LENGTH_LONG).show();
                        } else if(output.equals("nothing")) {
                            Toast.makeText(view.getContext(), view.getContext().getString(R.string.fetch_sync_fail), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(view.getContext(), view.getContext().getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                            Log.i("FINISHED SYNC:", "Study info");
                            for(Study s : newStudies) {
                                Log.i("NetworkChangeReceiver", "Setting up alarms for " + newStudies.size() + " studies");
                                ResponseReceiver rR = new ResponseReceiver(s);
                                rR.setupAlarm(view.getContext().getApplicationContext(), true);
                            }
                        }
                    }
                });

                ExecutorSupplier.getInstance().forBackgroundTasks().execute(syncStudyDataTask);

            }
        });
        return view;
    }


}