package com.example.madiskar.experiencesamplingapp;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Calendar;


public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        ArrayList<Study> studies = DBHandler.getInstance(context).getAllStudies();
        for(Study s : studies) {
            ResponseReceiver rR = new ResponseReceiver(s);
            rR.setupAlarm(context, true);

            for (Event event: s.getEvents()) {
                DBHandler myDb = DBHandler.getInstance(context);
                Calendar starttime = myDb.getEventStartTime(event.getId());
                if (starttime != null) {

                    int uniqueValue = ((int)event.getId())*-1;
                    int uniqueId = ((int)event.getId())*-100;
                    Intent stopIntent = new Intent(context, StopReceiver.class);
                    stopIntent.putExtra("start", starttime);
                    stopIntent.putExtra("notificationId", uniqueValue);
                    stopIntent.putExtra("controlNotificationId", uniqueId);
                    stopIntent.putExtra("studyId", s.getId());
                    stopIntent.putExtra("eventId", event.getId());

                    PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, uniqueValue, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_events)
                                    .setContentTitle(context.getString(R.string.active_event))
                                    .setContentText(event.getName())
                                    .setWhen(System.currentTimeMillis())
                                    .setUsesChronometer(true)
                                    .addAction(R.drawable.ic_stop, context.getString(R.string.stop), stopPendingIntent)
                                    .setOngoing(true);

                    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(uniqueValue, mBuilder.build());

                    int controlTime = event.getControlTime();
                    String unit = event.getUnit();

                    int multiplier = 0;
                    if (unit.equals("h")) {
                        multiplier = controlTime * 60 * 60 * 1000;
                    } else if (unit.equals("m")) {
                        multiplier = controlTime * 60 * 1000;
                    } else if (unit.equals("d")) {
                        multiplier = controlTime * 24 * 60 * 60 * 1000;
                    }

                    Intent controltimeIntent = new Intent(context, ControlTimeReceiver.class);
                    controltimeIntent.putExtra("eventId", event.getId());
                    controltimeIntent.putExtra("controlTime", (int) controlTime);
                    controltimeIntent.putExtra("eventName", event.getName());

                    controltimeIntent.putExtra("notificationId", uniqueId);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, uniqueId, controltimeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + multiplier, pendingIntent);

                }
            }
        }


    }
}
