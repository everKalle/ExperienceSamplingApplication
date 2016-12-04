package com.example.madiskar.experiencesamplingapp;

import android.app.AlarmManager;
import android.app.ListFragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.design.widget.FloatingActionButton;
import android.test.suitebuilder.TestMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;


public class StudyFragment extends ListFragment {

    private Handler mHandler = new Handler();
    private Boolean from_menu;
    private ActiveStudyListAdapter asla;
    private TextView noStudiesTxt;
    private ProgressBar progress;
    private TextView progressText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        from_menu = args.getBoolean("fromNav");

        MainActivity.justStarted = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_study, container, false);

        progress = (ProgressBar) view.findViewById(R.id.myStudiesProgressBar);
        final FloatingActionButton floatUpdate = (FloatingActionButton) view.findViewById(R.id.floatingUpdateButton);
        progressText = (TextView) view.findViewById(R.id.myStudiesProgressBarText);
        noStudiesTxt = (TextView) view.findViewById(R.id.no_studies);
        progress.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        floatUpdate.setVisibility(View.GONE);
        noStudiesTxt.setVisibility(View.GONE);

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
                            ArrayList<Study> filtered = filterStudies(results);
                            if(filtered.size() > 0) {
                                progress.setVisibility(View.GONE);
                                progressText.setVisibility(View.GONE);
                                floatUpdate.setVisibility(View.VISIBLE);
                                asla = new ActiveStudyListAdapter(getActivity(), filtered, StudyFragment.this);
                                setListAdapter(asla);
                            } else {
                                progress.setVisibility(View.GONE);
                                progressText.setVisibility(View.GONE);
                                noStudies();
                            }
                        }
                    }, 230); //Time for nav driver to close, for nice animations
                } else {
                    ArrayList<Study> filtered = filterStudies(results);
                    if(filtered.size() > 0) {
                        progress.setVisibility(View.GONE);
                        progressText.setVisibility(View.GONE);
                        floatUpdate.setVisibility(View.VISIBLE);
                        asla = new ActiveStudyListAdapter(getActivity(), filtered, StudyFragment.this);
                        setListAdapter(asla);
                    } else {
                        progress.setVisibility(View.GONE);
                        progressText.setVisibility(View.GONE);
                        noStudies();
                    }
                }
            }
        }.execute();

        floatUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    SharedPreferences pref = view.getContext().getApplicationContext().getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);

                    final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage(getString(R.string.update_studies));
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    SyncStudyDataTask syncStudyDataTask = new SyncStudyDataTask(pref.getString("token", "none"), DBHandler.getInstance(view.getContext()), false, new StudyDataSyncResponse() {
                        @Override
                        public void processFinish(String output, ArrayList<Study> newStudies, ArrayList<Study> allStudies, ArrayList<Study> updatedStudies, ArrayList<Study> oldStudies, ArrayList<Study> cancelledStudies) {
                            if (output.equals("invalid_token")) {
                                Log.i("Study Sync", getString(R.string.auth_sync_fail));
                                //Toast.makeText(view.getContext(), view.getContext().getString(R.string.auth_sync_fail), Toast.LENGTH_LONG).show();
                            } else if (output.equals("nothing")) {
                                Log.i("Study Sync", getString(R.string.fetch_sync_fail));
                                //Toast.makeText(view.getContext(), view.getContext().getString(R.string.fetch_sync_fail), Toast.LENGTH_LONG).show();
                            } else if(!output.equals("dberror")){
                                for (Study s : newStudies) {
                                    Log.i("StudyFragment", "Setting up alarms for " + newStudies.size() + " studies");
                                    setUpNewStudyAlarms(s);
                                }
                                for (int i=0; i<updatedStudies.size(); i++) {
                                    Log.i("STUDIES MODIFIED", "notification data changed for " + updatedStudies.size() + " studies");
                                    cancelStudy(oldStudies.get(i), false, false);
                                    setUpNewStudyAlarms(updatedStudies.get(i));
                                }
                                for (Study s : cancelledStudies) {
                                    Log.i("STUDIES CANCELLED", "removed " + cancelledStudies.size() + " studies");
                                    for(Study ks : allStudies) {
                                        if(ks.getId() == s.getId()) {
                                            allStudies.remove(ks);
                                            break;
                                        }
                                    }
                                    cancelStudy(s, true, true);
                                }
                                progressDialog.dismiss();
                                updateUI(allStudies);
                                Log.i("Study sync:", getString(R.string.update_success));
                            } else {
                                Log.i("FINISHED SYNC:", "study sync failed");
                            }
                        }
                    });
                    ExecutorSupplier.getInstance().forBackgroundTasks().execute(syncStudyDataTask);
                } else {
                    Toast.makeText(view.getContext(), view.getContext().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

    public ArrayList<Study> filterStudies(ArrayList<Study> studies) {
        ArrayList<Study> studiesClone = new ArrayList<>(studies);
        for (Study study: studiesClone) {
            if (Calendar.getInstance().after(study.getEndDate())) {
                NotificationService.cancelNotification(getActivity().getApplicationContext(), (int) study.getId());
                Intent intent = new Intent(getActivity().getApplicationContext(), QuestionnaireActivity.class);
                ResponseReceiver.cancelExistingAlarm(getActivity(), intent, Integer.valueOf((study.getId() + 1) + "00002"), false);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), (int) study.getId(), new Intent(getActivity().getApplicationContext(), ResponseReceiver.class), 0);
                AlarmManager am = (AlarmManager) getActivity().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                am.cancel(pendingIntent);
                for (Event event: EventDialogFragment.activeEvents) {
                    if (event.getStudyId() == study.getId()) {
                        Intent stopIntent = new Intent(getActivity().getApplicationContext(), StopReceiver.class);
                        stopIntent.putExtra("start", event.getStartTimeCalendar());
                        stopIntent.putExtra("notificationId", EventDialogFragment.uniqueValueMap.get((int) event.getId()));
                        stopIntent.putExtra("studyId", event.getStudyId());
                        stopIntent.putExtra("controlNotificationId", EventDialogFragment.uniqueControlValueMap.get((int) event.getId()));
                        stopIntent.putExtra("eventId", event.getId());
                        getActivity().getApplicationContext().sendBroadcast(stopIntent);
                    }
                }
                DBHandler.getInstance(getActivity().getApplicationContext()).deleteStudyEntry(study.getId());
                studiesClone.remove(study);
            }
        }
        return studiesClone;
    }

public void cancelStudy(final Study study, final boolean cancelEvents, final boolean removeStudy) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.i("Deleting study", study.getName());
                NotificationService.cancelNotification(getActivity().getApplicationContext(), (int) study.getId());
                Intent intent = new Intent(getActivity().getApplicationContext(), QuestionnaireActivity.class);
                ResponseReceiver.cancelExistingAlarm(getActivity().getApplicationContext(), intent, Integer.valueOf((study.getId() + 1) + "00002"), false);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), (int) study.getId(), new Intent(getActivity().getApplicationContext(), ResponseReceiver.class), 0);
                AlarmManager am = (AlarmManager) getActivity().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                am.cancel(pendingIntent);
                if(cancelEvents) {
                    for (Event event : EventDialogFragment.activeEvents) {
                        if (event.getStudyId() == study.getId()) {
                            Intent stopIntent = new Intent(getActivity().getApplicationContext(), StopReceiver.class);
                            stopIntent.putExtra("start", event.getStartTimeCalendar());
                            stopIntent.putExtra("notificationId", EventDialogFragment.uniqueValueMap.get((int) event.getId()));
                            stopIntent.putExtra("studyId", event.getStudyId());
                            stopIntent.putExtra("controlNotificationId", EventDialogFragment.uniqueControlValueMap.get((int) event.getId()));
                            stopIntent.putExtra("eventId", event.getId());
                            getActivity().getApplicationContext().sendBroadcast(stopIntent);
                        }
                    }
                }
                if(removeStudy) {
                    DBHandler.getInstance(getActivity().getApplicationContext()).deleteStudyEntry(study.getId());
                }
            }
        });
    }


    private void setUpNewStudyAlarms(final Study s) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ResponseReceiver rR = new ResponseReceiver(s);
                rR.setupAlarm(getActivity().getApplicationContext(), true);
            }
        });
    }


    private void updateUI(final ArrayList<Study> newList) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(newList.size() == 0)
                    noStudies();
                if(newList.size() > 0)
                    noStudiesTxt.setVisibility(View.GONE);
                asla = new ActiveStudyListAdapter(getActivity(), newList, StudyFragment.this);
                setListAdapter(asla);
            }
        });
    }

    public void noStudies() {
        noStudiesTxt.setVisibility(View.VISIBLE);
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
