package com.example.madiskar.experiencesamplingapp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;


public class ControlTimeReceiver extends BroadcastReceiver {

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

        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        SharedPreferences.Editor editor = sharedPref.edit();
        int soundVolume = sharedPref.getInt("volume", -1);
        if (soundVolume == -1) {
            editor.putInt("volume", audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)/2);
            editor.apply();
            soundVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)/2;
        }


        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Uri ringtone = null;
        final int previousAlarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);

        if (alarmType == 0) {
            if (alarmTone == 0) {
                ringtone = Uri.parse("android.resource://com.example.madiskar.experiencesamplingapp/raw/chime_1");
            } else if (alarmTone == 1)
                ringtone = Uri.parse("android.resource://com.example.madiskar.experiencesamplingapp/raw/chime_2");
            else {
                ringtone = Uri.parse("android.resource://com.example.madiskar.experiencesamplingapp/raw/chime_3");
            }

            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, soundVolume, 0);

            builder.setContentTitle(context.getString(R.string.controltime))
                    .setOngoing(false)
                    .setAutoCancel(true)
                    .setSound(ringtone, AudioManager.STREAM_ALARM)
                    .setColor(context.getResources().getColor(R.color.colorAccent))
                    .setContentText(context.getString(R.string.control_event)  + " \"" + eventName + "\" " + context.getString(R.string.passed))
                    .setSmallIcon(R.drawable.ic_events);


        } else if (alarmType == 1) {
            builder.setContentTitle(context.getString(R.string.controltime))
                    .setOngoing(false)
                    .setAutoCancel(true)
                    .setColor(context.getResources().getColor(R.color.colorAccent))
                    .setContentText(context.getString(R.string.control_event)  + " \"" + eventName + "\" " + context.getString(R.string.passed))
                    .setSmallIcon(R.drawable.ic_events);

            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 1000 milliseconds
            v.vibrate(1000);
        }

        Handler h = new Handler(Looper.getMainLooper());
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, previousAlarmVolume, 0);
            }
        }, 2500);

        final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(index, builder.build());
        index--;
    }

}
