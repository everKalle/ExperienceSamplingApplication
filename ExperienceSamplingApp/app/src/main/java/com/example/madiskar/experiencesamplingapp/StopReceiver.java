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
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

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
        Log.v("NOTIFI_ID", String.valueOf(notificationId));
        long eventId = intent.getLongExtra("eventId", 0);
        long studyId = intent.getLongExtra("studyId", 0);

        EventFragment.removeEvent(eventId);

        ArrayList<Integer> values = EventDialogFragment.studyToNotificationIdMap.get((int) studyId);
        Log.v("BEFORE", Arrays.toString(values.toArray()));
        values.remove(new Integer(notificationId));
        Log.v("AFTER", Arrays.toString(values.toArray()));
        EventDialogFragment.studyToNotificationIdMap.put((int)studyId, values);


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
                if (output.equals("invalid_event")) {
                    Log.i("Events to server: ", "This event does not exists");
                } else if (output.equals("invalid_token")) {
                    Log.i("Events to server: ", "Account authentication failed");
                } else if (output.equals("nothing")) {
                    Log.i("Events to server: ", "Faulty query");
                } else if (output.equals("success")) {
                    Log.i("Events to server: ", "Success");
                } else if (output.equals("saved-to-local")) {
                    Log.i("Events to server: ", "Internet connection unavailable, saving to local storage");
                } else {
                    Log.i("Events to server: ", "Something bad happened");
                }
            }
        });
        ExecutorSupplier.getInstance().forBackgroundTasks().execute(saveEventResultTask);
        Log.i("SAVING EVENTS", "START_TIME: " + startTime + ", END_TIME: " + endTime);

    }
}
