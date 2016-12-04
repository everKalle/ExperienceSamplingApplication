package com.example.madiskar.experiencesamplingapp;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private String token;
    private DBHandler mydb;
    private Context mContext;
    private Handler mHandler = new Handler();

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i("NETWORK STATE CHANGED", "CHECK PARAMETERS");

        mContext = context;
        mydb = DBHandler.getInstance(context);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        boolean mobileSyncAllowed = settings.getBoolean("pref_sync", false);
        Log.i("mobile sync", String.valueOf(mobileSyncAllowed));

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
        long difference = 300002;
        if(!lastSync.equals("none")) {
            difference = current.getTimeInMillis() - DBHandler.stringToCalendar(lastSync).getTimeInMillis(); //Time from last sync, right now the limit is 5 minutes
        }
        Log.i("difference", String.valueOf(difference));
        if(lastSync.equals("none") || difference > 300000) {
            if (networkInfo != null && networkInfo.isConnected() && !token.equals("none")) {
                if (((networkType == ConnectivityManager.TYPE_MOBILE || networkType == ConnectivityManager.TYPE_MOBILE_DUN) && !mobileSyncAllowed) || networkType == ConnectivityManager.TYPE_DUMMY) {
                    Log.i("NETWORK STATE CHANGED", "DON'T SYNC");
                } else {
                    Log.i("NETWORK STATE CHANGED", "TRY TO SYNC DATA");
                    SharedPreferences.Editor editor = spref.edit();
                    editor.putString("lastSync", DBHandler.calendarToString(current));
                    editor.apply();

                    SyncResultDataTask syncResultDataTask = new SyncResultDataTask(true, mydb, token, new RunnableResponse() {
                        @Override
                        public void processFinish(String output) {
                            Log.i("UPLOADED DATA:", output);
                            Log.i("STARTING SYNC:", "Study info");


                            SyncStudyDataTask syncStudyDataTask = new SyncStudyDataTask(token, mydb, true, new StudyDataSyncResponse() {
                                @Override
                                public void processFinish(String output, ArrayList<Study> newStudies, ArrayList<Study> allStudies, ArrayList<Study> updatedStudies, ArrayList<Study> oldStudies, ArrayList<Study> cancelledStudies) {
                                    if (output.equals("invalid_token")) {
                                        Log.i("FINISHED SYNC:", context.getApplicationContext().getString(R.string.auth_sync_fail));
                                    } else if (output.equals("nothing")) {
                                        Log.i("FINISHED SYNC:", context.getApplicationContext().getString(R.string.fetch_sync_fail));
                                    } else if (!output.equals("dberror")) {
                                        for (int i = 0; i < updatedStudies.size(); i++) {
                                            Log.i("STUDIES MODIFIED", "notification data changed for " + updatedStudies.size() + " studies");
                                            cancelStudy(oldStudies.get(i), false, false);
                                            setUpNewStudyAlarms(updatedStudies.get(i));
                                        }
                                        for (Study s : cancelledStudies) {
                                            Log.i("STUDIES CANCELLED", "removed " + cancelledStudies.size() + " studies");
                                            for (Study ks : allStudies) {
                                                if (ks.getId() == s.getId()) {
                                                    allStudies.remove(ks);
                                                    break;
                                                }
                                            }
                                            cancelStudy(s, true, true);
                                        }
                                        Log.i("Study sync:", context.getApplicationContext().getString(R.string.update_success));
                                    } else {
                                        Log.i("FINISHED SYNC:", context.getApplicationContext().getString(R.string.fetch_sync_fail));
                                    }
                                }
                            });
                            ExecutorSupplier.getInstance().forBackgroundTasks().execute(syncStudyDataTask);

                        }
                    });

                    ExecutorSupplier.getInstance().forBackgroundTasks().execute(syncResultDataTask);
                }

            } else {
                Log.i("NETWORK STATE CHANGED", "DON'T SYNC");
            }
        }
    }

    public void cancelStudy(final Study study, final boolean cancelEvents, final boolean removeStudy) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.i("Deleting study", study.getName());
                NotificationService.cancelNotification(mContext.getApplicationContext(), (int) study.getId());
                Intent intent = new Intent(mContext.getApplicationContext(), QuestionnaireActivity.class);
                ResponseReceiver.cancelExistingAlarm(mContext.getApplicationContext(), intent, Integer.valueOf((study.getId() + 1) + "00002"), false);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), (int) study.getId(), new Intent(mContext.getApplicationContext(), ResponseReceiver.class), 0);
                AlarmManager am = (AlarmManager) mContext.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                am.cancel(pendingIntent);
                if(cancelEvents) {
                    for (Event event : EventDialogFragment.activeEvents) {
                        if (event.getStudyId() == study.getId()) {
                            Intent stopIntent = new Intent(mContext.getApplicationContext(), StopReceiver.class);
                            stopIntent.putExtra("start", event.getStartTimeCalendar());
                            stopIntent.putExtra("notificationId", EventDialogFragment.uniqueValueMap.get((int) event.getId()));
                            stopIntent.putExtra("studyId", event.getStudyId());
                            stopIntent.putExtra("controlNotificationId", EventDialogFragment.uniqueControlValueMap.get((int) event.getId()));
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
