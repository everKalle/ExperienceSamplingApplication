package com.example.madiskar.experiencesamplingapp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Joosep on 23.10.2016.
 */
public class StopReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long startTime = intent.getLongExtra("start", 0);
        int notificationId = intent.getIntExtra("notificationId", 0);
        long eventId = intent.getLongExtra("eventId", 0);

        long elapsedStop = SystemClock.elapsedRealtime();
        long elapsedTime = (elapsedStop - startTime)/1000;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
        DBHandler mydb = DBHandler.getInstance(context);
        mydb.insertEventResult(eventId, (int)(elapsedTime/60.0));

    }
}
