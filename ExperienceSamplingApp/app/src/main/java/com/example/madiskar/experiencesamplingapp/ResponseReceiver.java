package com.example.madiskar.experiencesamplingapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;


public class ResponseReceiver extends WakefulBroadcastReceiver {

    private static String NOTIFICATION_INTERVAL = "INTERVAL";
    private static String NOTIFICATION_NAME = "STUDY";
    private static String STUDY_QUESTIONS = "QUESTIONS";

    @Override
    public void onReceive(Context context, Intent intent) {
        int interval = intent.getIntExtra(NOTIFICATION_INTERVAL,0);
        String name = intent.getStringExtra(NOTIFICATION_NAME);
        String[] textQuestions = intent.getStringArrayExtra(STUDY_QUESTIONS);
        Intent serviceIntent = NotificationService.createIntentNotificationService(context, interval, name, textQuestions);
        if (serviceIntent != null) {
            startWakefulService(context, serviceIntent);
        }
    }


    public static void setupAlarm(Context context, Study study) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int interval = study.getNotificationInterval();
        String name = study.getName();
        String[] textQuestions = study.questionsAsText();
        PendingIntent alarmIntent = getPendingIntent(context, interval, name, textQuestions);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + interval * 60 * 1000,
                interval * 60 * 1000,
                alarmIntent);
    }

    public static PendingIntent getPendingIntent(Context context, int interval, String name, String[] questions) {
        Intent intent = new Intent(context, ResponseReceiver.class);
        intent.putExtra(NOTIFICATION_INTERVAL, interval);
        intent.putExtra(NOTIFICATION_NAME, name);
        intent.putExtra(STUDY_QUESTIONS, questions);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
