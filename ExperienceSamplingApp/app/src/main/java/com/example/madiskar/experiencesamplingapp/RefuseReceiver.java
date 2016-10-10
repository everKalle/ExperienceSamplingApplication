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
        long studyId = intent.getLongExtra("StudyId", 0);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
        DBHandler mydb = DBHandler.getInstance(context);
        Log.v("Wokring", "it is");
        mydb.insertAnswer(studyId, "not answered", DBHandler.calendarToString(Calendar.getInstance()));
    }
}
