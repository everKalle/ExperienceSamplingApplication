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
    private static String DAILY_NOTIFICATION_LIMIT = "LIMIT";
    private static Study studyRef;

    @Override
    public void onReceive(Context context, Intent intent) {

        int interval = intent.getIntExtra(NOTIFICATION_INTERVAL,0);
        String name = intent.getStringExtra(NOTIFICATION_NAME);
        String[] textQuestions = intent.getStringArrayExtra(STUDY_QUESTIONS);
        int notificationsPerDay = intent.getIntExtra(DAILY_NOTIFICATION_LIMIT, 0);
        Intent serviceIntent = NotificationService.createIntentNotificationService(context, interval, name, textQuestions, notificationsPerDay, studyRef);
        if (serviceIntent != null) {
            startWakefulService(context, serviceIntent);
        }
    }


    public static void setupAlarm(Context context, Study study, boolean firstTime) {
        studyRef = study;
        int interval = study.getNotificationInterval();
        String name = study.getName();
        String[] textQuestions = study.questionsAsText();
        int notificationsPerDay = study.getNotificationsPerDay();
        Intent intent = new Intent(context, ResponseReceiver.class);

        PendingIntent alarmIntent = getPendingIntent(context, intent, interval, name, textQuestions, notificationsPerDay);

        cancelExistingAlarm(context, intent, 0);

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
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 0);
            intent.putExtra(NOTIFICATION_INTERVAL, interval);
            intent.putExtra(NOTIFICATION_NAME, name);
            intent.putExtra(STUDY_QUESTIONS, textQuestions);
            intent.putExtra(DAILY_NOTIFICATION_LIMIT, notificationsPerDay);

            PendingIntent PendingIntentD = PendingIntent.getBroadcast(context, 105, intent, 0);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendar.getTimeInMillis(), interval * 60 * 1000, PendingIntentD);
        }
    }

    private static void cancelExistingAlarm(Context context, Intent intent, int i) {
        try{
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, intent, 0);
            AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            am.cancel(pendingIntent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static PendingIntent getPendingIntent(Context context, Intent intent, int interval, String name, String[] questions, int notificationsPerDay) {
        intent.putExtra(NOTIFICATION_INTERVAL, interval);
        intent.putExtra(NOTIFICATION_NAME, name);
        intent.putExtra(STUDY_QUESTIONS, questions);
        intent.putExtra(DAILY_NOTIFICATION_LIMIT, notificationsPerDay);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }
}
