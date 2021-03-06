package com.example.madiskar.experiencesamplingapp.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.example.madiskar.experiencesamplingapp.services.NotificationService;
import com.example.madiskar.experiencesamplingapp.data_types.Study;
import com.example.madiskar.experiencesamplingapp.local_database.DBHandler;

import java.util.ArrayList;
import java.util.Calendar;



public class ResponseReceiver extends WakefulBroadcastReceiver {

    private Study study;
    private SharedPreferences sharedPref;

    public ResponseReceiver(Study study) {
        this.study = study;
    }
    public ResponseReceiver () {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra("studyId", 0);

        Study studyParam = null;
        for (Study s: DBHandler.getInstance(context).getAllStudies()) {
            if (s.getId() == (int) id) {
                studyParam = s;
            }
        }
        Intent serviceIntent = NotificationService.createIntentNotificationService(context, studyParam);
        if (serviceIntent != null) {
            startWakefulService(context, serviceIntent);
        }
    }

    public void setupAlarm(Context context, boolean firstTime) {

        sharedPref = context.getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);

        if (sharedPref.getInt(String.valueOf(study.getId()),0) == 0) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(String.valueOf(study.getId()), 0);
            editor.apply();
        }


        int interval = study.getMinTimeBetweenNotifications();
        Intent intent = new Intent(context, ResponseReceiver.class);
        long id = study.getId();

        PendingIntent alarmIntent = getPendingIntent(context, intent, id);


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (firstTime) {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + interval * 60 * 1000,
                    interval * 60 * 1000,
                    alarmIntent);
        }
        else {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(String.valueOf(study.getId()), 0);
            editor.apply();

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), interval * 60 * 1000 , alarmIntent);
        }
    }

    public static void cancelExistingAlarm(Context context, Intent intent, int i, boolean broadcast) {
        try{
            PendingIntent pendingIntent = null;
            if (broadcast)
                pendingIntent = PendingIntent.getBroadcast(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT); //VB SIIN 0 PANNA VIIMASEKS FLAG.. ASEMEL
            else
                pendingIntent = PendingIntent.getActivity(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT); //VB SIIN 0 PANNA VIIMASEKS FLAG.. ASEMEL
            AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            am.cancel(pendingIntent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static PendingIntent getPendingIntent(Context context, Intent intent, long id) {
        intent.putExtra("studyId", id);
        return PendingIntent.getBroadcast(context, (int) id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
