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

        Event event1 = new Event(0,1,"Running",5, "m");
        Event event2 = new Event(1,1, "Cooking",5, "m");
        Event event3 = new Event(2,1, "Swimming",5, "m");
        Event event4 = new Event(3,1, "Dancing",7, "m");
        Event event5 = new Event(4,1, "Sleeping",2, "m");
        Event event6 = new Event(5,1,"Cycling",3, "m");
        Event event7 = new Event(6,1, "Boxing",1, "m");
        Event event8 = new Event(7,1, "Eating",4, "m");
        Event event9 = new Event(8, 1, "Gaming", 5, "m");
        Event event14 = new Event(13, 1, "Drinking Vodka", 5, "m");
        Event event15 = new Event(14, 1, "Trying to get a girlfriend", 5, "m");

        Event event10 = new Event(9,2, "Cooking",3, "m");
        Event event11 = new Event(10,2, "Dancing",5, "m");
        Event event12 = new Event(11,2, "Eating",1, "m");
        Event event13 = new Event(12,2, "Gaming",2, "m");

        Event[] eventsArray1 = {event1, event2, event3, event4, event5, event6, event7, event8, event9};
        Event[] eventsArray2 = {event10, event11, event12, event13};

        Calendar c1 = Calendar.getInstance();
        c1.set(2016, 2, 20);
        Calendar c2 = Calendar.getInstance();
        c2.set(2016, 3, 20);
        Study study = new Study(0, "Study 1", qnaire1, c1, c2, 30, 3, 1, 5, true, 1, eventsArray1);

        // TODO: get studies from database here

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int interval = study.getNotificationInterval();
        String name = study.getName();
        String[] textQuestions = study.questionsAsText();
        int notificationsPerDay = study.getNotificationsPerDay();
        PendingIntent alarmIntent = ResponseReceiver.getPendingIntent(context, intent, interval, name, textQuestions, notificationsPerDay, study.getId());

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + interval * 60 * 1000,
                interval * 60 * 1000,
                alarmIntent);
    }
}
