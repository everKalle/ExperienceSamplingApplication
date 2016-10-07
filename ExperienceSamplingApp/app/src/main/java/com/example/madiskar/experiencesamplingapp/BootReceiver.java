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

        Question q4 = new FreeTextQuestion(0, "Is it easy?");
        Question q2 = new FreeTextQuestion(0, "Is it still easy?");

        Question[] batch1 = {q4, q2};

        Questionnaire qnaire1 = new Questionnaire(0, batch1);

        Calendar c1 = Calendar.getInstance();
        c1.set(2016, 2, 20);
        Calendar c2 = Calendar.getInstance();
        c2.set(2016, 3, 20);
        Study study = new Study(0, "Study 1", qnaire1, c1, c2, 30, 3, 1, 5, true, 1);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int interval = study.getNotificationInterval();
        String name = study.getName();
        String[] textQuestions = study.questionsAsText();
        int notificationsPerDay = study.getNotificationsPerDay();
        PendingIntent alarmIntent = ResponseReceiver.getPendingIntent(context, intent, interval, name, textQuestions, notificationsPerDay);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + interval * 60 * 1000,
                interval * 60 * 1000,
                alarmIntent);
    }
}
