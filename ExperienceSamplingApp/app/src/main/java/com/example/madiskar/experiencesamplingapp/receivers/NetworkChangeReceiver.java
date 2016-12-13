package com.example.madiskar.experiencesamplingapp.receivers;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.example.madiskar.experiencesamplingapp.background_tasks.ExecutorSupplier;
import com.example.madiskar.experiencesamplingapp.services.NotificationService;
import com.example.madiskar.experiencesamplingapp.activities.QuestionnaireActivity;
import com.example.madiskar.experiencesamplingapp.background_tasks.SyncResultDataTask;
import com.example.madiskar.experiencesamplingapp.background_tasks.SyncStudyDataTask;
import com.example.madiskar.experiencesamplingapp.data_types.Event;
import com.example.madiskar.experiencesamplingapp.data_types.Study;
import com.example.madiskar.experiencesamplingapp.interfaces.RunnableResponse;
import com.example.madiskar.experiencesamplingapp.interfaces.StudyDataSyncResponse;
import com.example.madiskar.experiencesamplingapp.local_database.DBHandler;

import java.util.ArrayList;
import java.util.Calendar;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private String token;
    private DBHandler mydb;
    private Context mContext;
    private Handler mHandler = new Handler();

    @Override
    public void onReceive(final Context context, Intent intent) {

        mContext = context;
        mydb = DBHandler.getInstance(context);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        boolean mobileSyncAllowed = settings.getBoolean("pref_sync", false);

        SharedPreferences spref = context.getApplicationContext().getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);
        token = spref.getString("token", "none");
        String lastSync = spref.getString("lastSync", "none");

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        int networkType = -1;
        try {
            networkType = networkInfo.getType();
        } catch (NullPointerException e) {
            // do nothing
        }

        Calendar current = Calendar.getInstance();
        long difference = 120002;
        if(!lastSync.equals("none")) {
            difference = current.getTimeInMillis() - DBHandler.stringToCalendar(lastSync).getTimeInMillis(); //Time from last sync, right now the limit is 2 minutes
        }
        if(lastSync.equals("none") || difference > 120000) {
            if (networkInfo != null && networkInfo.isConnected() && !token.equals("none")) {
                if (((networkType == ConnectivityManager.TYPE_MOBILE || networkType == ConnectivityManager.TYPE_MOBILE_DUN) && !mobileSyncAllowed) || networkType == ConnectivityManager.TYPE_DUMMY) {
                        //do nothing, connection not allowed
                } else {
                    SharedPreferences.Editor editor = spref.edit();
                    editor.putString("lastSync", DBHandler.calendarToString(current));
                    editor.apply();

                    SyncResultDataTask syncResultDataTask = new SyncResultDataTask(true, mydb, token, new RunnableResponse() {
                        @Override
                        public void processFinish(String output) {


                            SyncStudyDataTask syncStudyDataTask = new SyncStudyDataTask(token, mydb, true, new StudyDataSyncResponse() {
                                @Override
                                public void processFinish(String output, ArrayList<Study> newStudies, ArrayList<Study> allStudies, ArrayList<Study> updatedStudies, ArrayList<Study> oldStudies, ArrayList<Study> cancelledStudies) {
                                    try {
	                                    if (output.equals("invalid_token")) {
	                                        //do nothing
	                                    } else if (output.equals("nothing")) {
	                                        //do nothing
	                                    } else if (!output.equals("dberror")) {
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
	                                    } else {
	                                        //authentication failed, do nothing
	                                    }
	                                } catch (Exception e) {
	                                	// something went wrong, probably server connection timed out
	                                }
                            }
                            });
                            ExecutorSupplier.getInstance().forBackgroundTasks().execute(syncStudyDataTask);

                        }
                    });

                    ExecutorSupplier.getInstance().forBackgroundTasks().execute(syncResultDataTask);
                }

            } else {
                //nothing
            }
        }
    }

    public void cancelStudy(final Study study, final boolean cancelEvents, final boolean removeStudy) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                NotificationService.cancelNotification(mContext.getApplicationContext(), (int) study.getId());
                Intent intent = new Intent(mContext.getApplicationContext(), QuestionnaireActivity.class);
                ResponseReceiver.cancelExistingAlarm(mContext.getApplicationContext(), intent, Integer.valueOf((study.getId() + 1) + "00002"), false);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), (int) study.getId(), new Intent(mContext.getApplicationContext(), ResponseReceiver.class), 0);
                AlarmManager am = (AlarmManager) mContext.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                am.cancel(pendingIntent);

                DBHandler dbHandler = DBHandler.getInstance(mContext);
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
                            Intent stopIntent = new Intent(mContext.getApplicationContext(), StopReceiver.class);
                            stopIntent.putExtra("start", event.getStartTimeCalendar());
                            stopIntent.putExtra("notificationId", ((int) event.getId())*-1);
                            stopIntent.putExtra("studyId", event.getStudyId());
                            stopIntent.putExtra("controlNotificationId", ((int) event.getId())*-100);
                            stopIntent.putExtra("eventId", event.getId());
                            mContext.getApplicationContext().sendBroadcast(stopIntent);
                        }
                    }
                }
                if(removeStudy) {
                    DBHandler.getInstance(mContext.getApplicationContext()).deleteStudyEntry(study.getId());
                }
            }
        });
    }


    private void setUpNewStudyAlarms(final Study s) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ResponseReceiver rR = new ResponseReceiver(s);
                rR.setupAlarm(mContext.getApplicationContext(), true);
            }
        });
    }
}
