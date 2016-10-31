package com.example.madiskar.experiencesamplingapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Joosep on 28.09.2016.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        // TODO: correct this implementation

        ArrayList<Study> studies = DBHandler.getInstance(context).getAllStudies();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for(Study s : studies) {
            int interval = s.getNotificationInterval();
            String name = s.getName();
            String[] textQuestions = s.questionsAsText();
            int notificationsPerDay = s.getNotificationsPerDay();
            PendingIntent alarmIntent = ResponseReceiver.getPendingIntent(context, intent, interval, name, textQuestions, notificationsPerDay, s.getId());

            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + interval * 60 * 1000,
                    interval * 60 * 1000,
                    alarmIntent);
        }
    }
}
