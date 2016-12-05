package com.example.madiskar.experiencesamplingapp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;


public class ControlTimeReceiver extends BroadcastReceiver {

    private final static int MAX_VOLUME = 100;
    private static int index = -2000000;

    @Override
    public void onReceive(Context context, Intent intent) {
        long eventId = intent.getLongExtra("eventId", 0);
        int notificationId = intent.getIntExtra("notificationId", 0);
        int controlTime = intent.getIntExtra("controlTime", 0);
        String eventName = intent.getStringExtra("eventName");

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sharedPref = context.getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);
        int alarmType = Integer.valueOf(settings.getString("alarm_type", ""));
        int alarmTone = Integer.valueOf(settings.getString("alarm_tone",""));
        int soundVolume = sharedPref.getInt("volume",0);
        MediaPlayer mediaPlayer;

        if (alarmType == 0) {
            if (alarmTone == 0) {
                mediaPlayer = MediaPlayer.create(context, R.raw.chime_1);
            }
            else if (alarmTone == 1)
                mediaPlayer = MediaPlayer.create(context, R.raw.chime_2);
            else
                mediaPlayer = MediaPlayer.create(context, R.raw.chime_2);
            float volume = (float) (1 - (Math.log(MAX_VOLUME - soundVolume) / Math.log(MAX_VOLUME)));
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.start();
        }
        else if (alarmType == 1) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 1000 milliseconds
            v.vibrate(1000);
        }

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setContentTitle(context.getString(R.string.controltime))
                .setOngoing(false)
                .setAutoCancel(true)
                .setColor(context.getResources().getColor(R.color.colorAccent))
                .setContentText(context.getString(R.string.control_event)  + " \"" + eventName + "\" " + context.getString(R.string.passed))
                .setSmallIcon(R.drawable.ic_events);

        final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(index, builder.build());
        index--;
    }

}
