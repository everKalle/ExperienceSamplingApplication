package com.example.madiskar.experiencesamplingapp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by joosep41 on 9.10.2016.
 */

public class RefuseReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("notificationId", 0);
        int studyId = intent.getIntExtra("StudyId", 0); //TODO: Need to get long, but since in notificationservice we put in int, so we get from here aswell
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
        DBHandler mydb = DBHandler.getInstance(context);
        mydb.insertAnswer(studyId, "not answered", DBHandler.calendarToString(Calendar.getInstance()));
    }
}
