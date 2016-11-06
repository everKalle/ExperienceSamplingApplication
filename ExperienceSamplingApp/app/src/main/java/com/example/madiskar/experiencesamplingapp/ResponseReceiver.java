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
import java.util.HashMap;
import java.util.Map;


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
        /*
        Log.v("rorororororororroro","to");
        for (int i = 0; i < studies.size(); i++) {
            Log.v("studies element", String.valueOf(studies.get(i).getMinTimeBetweenNotifications()));
        }
        Log.v("rorororororororroro","to");
        */
        //if (dailyNotificationCount.get((int)study.getId()) == null)
        //    dailyNotificationCount.put((int)study.getId(), 0);
    }
    public ResponseReceiver () {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int interval = intent.getIntExtra(NOTIFICATION_INTERVAL, 0);
        String name = intent.getStringExtra(NOTIFICATION_NAME);
        String[] textQuestions = intent.getStringArrayExtra(STUDY_QUESTIONS);
        int notificationsPerDay = intent.getIntExtra(DAILY_NOTIFICATION_LIMIT, 0);
        long id = intent.getLongExtra("studyId", 0);

        Study studyParam = null;
        for (Study s: DBHandler.getInstance(context).getAllStudies()) {
            Log.v("suurus2","huops");
            if (s.getId() == (int) id) {
                studyParam = s;
            }
        }
        Intent serviceIntent = NotificationService.createIntentNotificationService(context, interval, name, textQuestions, notificationsPerDay, studyParam);
        if (serviceIntent != null) {
            startWakefulService(context, serviceIntent);
        }
    }

    public void setupAlarm(Context context, boolean firstTime) {

        sharedPref = context.getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);

        if (sharedPref.getInt(String.valueOf(study.getId()),0) == 0) {
            Log.v("ILMNE", "on");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(String.valueOf(study.getId()), 0);
            editor.apply();
        }


        if (studies.isEmpty()) {
            DBHandler mydb = DBHandler.getInstance(context);
            studies = mydb.getAllStudies();
        }
        Log.v("suurus", String.valueOf(studies.size()));
        int interval = study.getMinTimeBetweenNotifications();
        String name = study.getName();
        String[] textQuestions = study.questionsAsText();
        int notificationsPerDay = study.getNotificationsPerDay();
        Intent intent = new Intent(context, ResponseReceiver.class);
        long id = study.getId();

        PendingIntent alarmIntent = getPendingIntent(context, intent, interval, name, textQuestions, notificationsPerDay, id);

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
            Calendar now = Calendar.getInstance();
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

    public static PendingIntent getPendingIntent(Context context, Intent intent, int interval, String name, String[] questions, int notificationsPerDay, long id) {
        intent.putExtra(NOTIFICATION_INTERVAL, interval);
        intent.putExtra(NOTIFICATION_NAME, name);
        intent.putExtra(STUDY_QUESTIONS, questions);
        intent.putExtra(DAILY_NOTIFICATION_LIMIT, notificationsPerDay);
        intent.putExtra("studyId", id);
        Log.v("Study ident", String.valueOf(id));
        return PendingIntent.getBroadcast(context, (int) id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
