package com.example.madiskar.experiencesamplingapp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by Joosep on 23.10.2016.
 */
public class ControlTimeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("Here i am 2", "ops");
        long eventId = intent.getLongExtra("eventId", 0);
        int notificationId = intent.getIntExtra("notificationId", 0);
        int controlTime = intent.getIntExtra("controlTime", 0);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
        DBHandler mydb = DBHandler.getInstance(context);
        mydb.insertEventResult(eventId, controlTime);
    }

}
