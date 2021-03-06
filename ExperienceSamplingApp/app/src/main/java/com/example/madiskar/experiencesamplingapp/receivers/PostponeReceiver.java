package com.example.madiskar.experiencesamplingapp.receivers;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import com.example.madiskar.experiencesamplingapp.activities.QuestionnaireActivity;
import com.example.madiskar.experiencesamplingapp.local_database.DBHandler;

public class PostponeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();

        int notificationId = extras.getInt("notificationId", 0);
        int uniqueValue = extras.getInt("uniqueValue", 0);
        long studyId = extras.getLong("StudyId", 0);

        int postponeTime = DBHandler.getInstance(context).getStudy(studyId).getPostponeTime();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);


        Intent scheduledIntent = new Intent(context, QuestionnaireActivity.class);
        scheduledIntent.putExtra("StudyId", studyId);
        scheduledIntent.putExtra("postpone", postponeTime);

        scheduledIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, uniqueValue, scheduledIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + postponeTime * 60 * 1000,
                pendingIntent);

    }
}