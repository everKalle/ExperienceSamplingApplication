package com.example.madiskar.experiencesamplingapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


public class ResponseReceiver extends WakefulBroadcastReceiver {

    private static String NOTIFICATION_INTERVAL = "INTERVAL";
    private static String NOTIFICATION_NAME = "STUDY";
    private static String STUDY_QUESTIONS = "QUESTIONS";
    private static String DAILY_NOTIFICATION_LIMIT = "LIMIT";
    private Study study;
    private SharedPreferences sharedPref;
    public static ArrayList<Study> studies = new ArrayList<>();

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


        if (studies.isEmpty()) {
            DBHandler mydb = DBHandler.getInstance(context);
            studies = mydb.getAllStudies();
        }

        int interval = study.getMinTimeBetweenNotifications();
        Intent intent = new Intent(context, ResponseReceiver.class);
        long id = study.getId();

        PendingIntent alarmIntent = getPendingIntent(context, intent, id);

        //cancelExistingAlarm(context, intent, 0);

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
