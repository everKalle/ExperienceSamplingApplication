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

        Question q1 = new FreeTextQuestion("Is it easy?");
        Question q2 = new FreeTextQuestion("Is it still easy?");
        Question q3 = new FreeTextQuestion("Is it easy or is it easy?");

        ArrayList<Question> questions = new ArrayList<>();
        questions.add(q1);
        questions.add(q2);
        questions.add(q3);
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, Calendar.MONTH, 20);
        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.YEAR, Calendar.MONTH + 1, 20);
        Study study = new Study(0, "easy study", questions, c1, c2, 30, 3, 1, 5, true, 1);

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
