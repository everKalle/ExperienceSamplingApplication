package com.example.madiskar.experiencesamplingapp.fragments;

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
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.madiskar.experiencesamplingapp.data_types.Event;
import com.example.madiskar.experiencesamplingapp.background_tasks.ExecutorSupplier;
import com.example.madiskar.experiencesamplingapp.activities.MainActivity;
import com.example.madiskar.experiencesamplingapp.interfaces.OnStudyTableChangedListener;
import com.example.madiskar.experiencesamplingapp.services.NotificationService;
import com.example.madiskar.experiencesamplingapp.activities.QuestionnaireActivity;
import com.example.madiskar.experiencesamplingapp.R;
import com.example.madiskar.experiencesamplingapp.receivers.ResponseReceiver;
import com.example.madiskar.experiencesamplingapp.receivers.StopReceiver;
import com.example.madiskar.experiencesamplingapp.data_types.Study;
import com.example.madiskar.experiencesamplingapp.background_tasks.SyncStudyDataTask;
import com.example.madiskar.experiencesamplingapp.interfaces.StudyDataSyncResponse;
import com.example.madiskar.experiencesamplingapp.list_adapters.ActiveStudyListAdapter;
import com.example.madiskar.experiencesamplingapp.local_database.DBHandler;

import java.util.ArrayList;
import java.util.Calendar;


public class StudyFragment extends ListFragment {

    private Handler mHandler = new Handler();
    private Boolean from_menu;
    private ActiveStudyListAdapter asla;
    private TextView noStudiesTxt;
    private ProgressBar progress;
    private TextView progressText;
    private SharedPreferences spref;
    private DBHandler mydb;
    private boolean fabUpdate = false;
    private boolean filtering = false;


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

        spref = getActivity().getApplicationContext().getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);
        progress = (ProgressBar) view.findViewById(R.id.myStudiesProgressBar);
        final FloatingActionButton floatUpdate = (FloatingActionButton) view.findViewById(R.id.floatingUpdateButton);
        floatUpdate.setVisibility(View.VISIBLE);
        progressText = (TextView) view.findViewById(R.id.myStudiesProgressBarText);
        noStudiesTxt = (TextView) view.findViewById(R.id.no_studies);
        progress.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        noStudiesTxt.setVisibility(View.GONE);
        mydb = DBHandler.getInstance(getActivity());

        new AsyncTask<Void, Void, ArrayList<Study>>() {
            @Override
            protected ArrayList<Study> doInBackground(Void... params) {
                return mydb.getAllStudies();
            }
            @Override
            protected void onPostExecute(final ArrayList<Study> results) {
                if (from_menu) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            filtering = true;
                            ArrayList<Study> filtered = filterStudies(results);
                            asla = new ActiveStudyListAdapter(getActivity(), filtered);
                            progress.setVisibility(View.GONE);
                            progressText.setVisibility(View.GONE);
                            setListAdapter(asla);
                            if (filtered.size() == 0)
                                noStudies();
                            filtering = false;
                        }
                    }, 230); //Time for nav driver to close, for nice animations
                } else {
                    filtering = true;
                    ArrayList<Study> filtered = filterStudies(results);
                    asla = new ActiveStudyListAdapter(getActivity(), filtered);
                    progress.setVisibility(View.GONE);
                    progressText.setVisibility(View.GONE);
                    setListAdapter(asla);
                    if (filtered.size() == 0)
                        noStudies();
                    filtering = false;
                }

                mydb.setOnStudyTableChangedListener(new OnStudyTableChangedListener() {
                    @Override
                    public void onTableChanged() {
                        if (!fabUpdate && !filtering) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (!fabUpdate && !filtering) {
                                        ArrayList<Study> studies_ = mydb.getAllStudies();
                                        if (studies_.size() == 0) {
                                            if (asla.getCount() != 0) {
                                                asla.updateStudies(studies_);
                                            }
                                            ((TextView) view.findViewById(R.id.no_studies)).setVisibility(View.VISIBLE);
                                        } else {
                                            ((TextView) view.findViewById(R.id.no_studies)).setVisibility(View.GONE);
                                            asla.updateStudies(studies_);
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }.execute();

        floatUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    String lastFabSync = spref.getString("lastFabSync", "none");
                    Calendar current = Calendar.getInstance();
                    long difference = 60002;
                    if(!lastFabSync.equals("none")) {
                        difference = current.getTimeInMillis() - DBHandler.stringToCalendar(lastFabSync).getTimeInMillis(); //Time from last sync, right now the limit is 5 minutes
                    }

                    if (lastFabSync.equals("none") || difference > 60000) {
                        SharedPreferences pref = view.getContext().getApplicationContext().getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = spref.edit();
                        editor.putString("lastFabSync", DBHandler.calendarToString(current));
                        editor.apply();

                        final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage(getString(R.string.update_studies));
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        fabUpdate = true;

                        SyncStudyDataTask syncStudyDataTask = new SyncStudyDataTask(pref.getString("token", "none"), DBHandler.getInstance(view.getContext()), false, new StudyDataSyncResponse() {
                            @Override
                            public void processFinish(String output, ArrayList<Study> newStudies, ArrayList<Study> allStudies, ArrayList<Study> updatedStudies, ArrayList<Study> oldStudies, ArrayList<Study> cancelledStudies) {
                                try {
                                    if (output.equals("invalid_token")) {
                                        //do nothing
                                    } else if (output.equals("nothing")) {
                                        //do nothing
                                    } else if (!output.equals("dberror")) {
                                        for (Study s : newStudies) {
                                            setUpNewStudyAlarms(s);
                                        }
                                        for (int i = 0; i < updatedStudies.size(); i++) {
                                            cancelStudy(oldStudies.get(i), false, false);
                                            setUpNewStudyAlarms(updatedStudies.get(i));
                                        }
                                        for (Study s : cancelledStudies) {
                                            for (Study ks : allStudies) {
                                                if (ks.getId() == s.getId()) {
                                                    allStudies.remove(ks);
                                                    break;
                                                }
                                            }
                                            cancelStudy(s, true, true);
                                        }
                                        progressDialog.dismiss();
                                        updateUI(allStudies);
                                    } else {
                                        //sync failed, do nothing
                                    }
                                } catch (Exception e) {
                                    // probably server connection went bad
                                } finally {
                                    fabUpdate = false;
                                }
                            }
                        });
                        ExecutorSupplier.getInstance().forBackgroundTasks().execute(syncStudyDataTask);
                    } else {
                        Toast.makeText(view.getContext(), view.getContext().getString(R.string.sync_too_often), Toast.LENGTH_SHORT).show();
                    }
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

                DBHandler dbHandler = DBHandler.getInstance(getActivity());
                ArrayList<Event> activeEvents = new ArrayList<>();
                for (Study s: dbHandler.getAllStudies()) {
                    for (Event e: s.getEvents()) {
                        Calendar startTime = dbHandler.getEventStartTime(e.getId());
                        if (startTime != null) {
                            e.setStartTimeCalendar(DBHandler.calendarToString(startTime));
                            activeEvents.add(e);
                        }
                    }
                }

                for (Event event: activeEvents) {
                    if (event.getStudyId() == study.getId()) {
                        Intent stopIntent = new Intent(getActivity().getApplicationContext(), StopReceiver.class);
                        stopIntent.putExtra("start", event.getStartTimeCalendar());
                        stopIntent.putExtra("notificationId", ((int) event.getId())*-1);
                        stopIntent.putExtra("studyId", event.getStudyId());
                        stopIntent.putExtra("controlNotificationId", ((int) event.getId())*-100);
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

    private void cancelStudy(final Study study, final boolean cancelEvents, final boolean removeStudy) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                NotificationService.cancelNotification(getActivity().getApplicationContext(), (int) study.getId());
                Intent intent = new Intent(getActivity().getApplicationContext(), QuestionnaireActivity.class);
                ResponseReceiver.cancelExistingAlarm(getActivity().getApplicationContext(), intent, Integer.valueOf((study.getId() + 1) + "00002"), false);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), (int) study.getId(), new Intent(getActivity().getApplicationContext(), ResponseReceiver.class), 0);
                AlarmManager am = (AlarmManager) getActivity().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                am.cancel(pendingIntent);

                DBHandler dbHandler = DBHandler.getInstance(getActivity());
                ArrayList<Event> activeEvents = new ArrayList<>();
                for (Study s: dbHandler.getAllStudies()) {
                    for (Event e: s.getEvents()) {
                        Calendar startTime = dbHandler.getEventStartTime(e.getId());
                        if (startTime != null) {
                            e.setStartTimeCalendar(DBHandler.calendarToString(startTime));
                            activeEvents.add(e);
                        }
                    }
                }

                if(cancelEvents) {
                    for (Event event : activeEvents) {
                        if (event.getStudyId() == study.getId()) {
                            Intent stopIntent = new Intent(getActivity().getApplicationContext(), StopReceiver.class);
                            stopIntent.putExtra("start", event.getStartTimeCalendar());
                            stopIntent.putExtra("notificationId", ((int) event.getId())*-1);
                            stopIntent.putExtra("studyId", event.getStudyId());
                            stopIntent.putExtra("controlNotificationId", ((int) event.getId())*-100);
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
                asla.updateStudies(newList);
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

    @Override
    public void onDestroyView() {
        mydb.setOnStudyTableChangedListener(null);
        super.onDestroyView();
    }

}