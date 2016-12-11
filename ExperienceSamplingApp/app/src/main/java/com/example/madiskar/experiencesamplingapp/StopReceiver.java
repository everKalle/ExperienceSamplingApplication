package com.example.madiskar.experiencesamplingapp;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


public class StopReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences spref = context.getApplicationContext().getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);
        String token = spref.getString("token", "none");

        String startTime = intent.getStringExtra("start");
        String endTime = DBHandler.calendarToString(Calendar.getInstance());
        int notificationId = intent.getIntExtra("notificationId", 0);
        int controlNotificationId = intent.getIntExtra("controlNotificationId",0);
        long eventId = intent.getLongExtra("eventId", 0);
        long studyId = intent.getLongExtra("studyId", 0);

        Log.v("startTime", startTime);
        Log.v("endTime", endTime);
        Log.v("notificationId",notificationId + "");
        Log.v("controlNotificationId", controlNotificationId + "");
        Log.v("eventId", eventId + "");
        Log.v("studyId", studyId + "");
        EventFragment.removeEvent(eventId, context);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);

        Intent i = new Intent(context, ControlTimeReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, controlNotificationId, i, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(sender);


        DBHandler mydb = DBHandler.getInstance(context);
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        SaveEventResultTask saveEventResultTask = new SaveEventResultTask(token, Long.toString(eventId), startTime, endTime,
                (activeNetworkInfo != null && activeNetworkInfo.isConnected()), mydb, new RunnableResponse() {
            @Override
            public void processFinish(String output) {
            }
        });
        ExecutorSupplier.getInstance().forBackgroundTasks().execute(saveEventResultTask);

    }
}
