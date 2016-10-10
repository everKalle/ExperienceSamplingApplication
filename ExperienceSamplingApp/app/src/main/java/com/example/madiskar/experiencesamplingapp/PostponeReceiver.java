package com.example.madiskar.experiencesamplingapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by Joosep on 4.10.2016.
 */
public class PostponeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        /*
        Intent scheduledIntent;
        String type = intent.getStringExtra("TYPE");
        String question = intent.getStringExtra("QUESTION");
        int interval = intent.getIntExtra("INTERVAL", 0);

        if (type.equals("free")) {
            scheduledIntent = new Intent(context, FreeTextQuestionActivity.class);
        }
        else  {
            String[] choices = intent.getStringArrayExtra("CHOICES");
            scheduledIntent = new Intent(context, MultipleChoiceQuestionActivity.class);
            scheduledIntent.putExtra("CHOICES", choices);
        }
        scheduledIntent.putExtra("QUESTION", question);
        scheduledIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, scheduledIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + interval * 60 * 1000,
                pendingIntent);
        //context.startActivity(scheduledIntent);
        */
    }
}
