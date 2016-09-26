package com.example.madiskar.experiencesamplingapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.NotificationCompat;

import java.util.Date;

public class NotificationService extends IntentService {

    private static int questionCounter = 0;
    private static final int NOTIFICATION_ID = 1;
    private static String NOTIFICATION_NAME = "STUDY";
    private static String NOTIFICATION_INTERVAL = "INTERVAL";
    private static String STUDY_QUESTIONS = "QUESTIONS";


    public NotificationService() {
        super(NotificationService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String action = intent.getAction();
            String name = intent.getStringExtra(NOTIFICATION_NAME);
            int interval = intent.getIntExtra(NOTIFICATION_INTERVAL,0);
            String[] textQuestions = intent.getStringArrayExtra(STUDY_QUESTIONS);
            processNotification(name, textQuestions, interval);
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    public static Intent createIntentNotificationService(Context context, int interval, String name, String[] questions) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.putExtra(NOTIFICATION_NAME, name);
        intent.putExtra(NOTIFICATION_INTERVAL, interval);
        intent.putExtra(STUDY_QUESTIONS, questions);
        return intent;
    }

    private void processNotification(String name, String[] questions, int interval) {
        // Do something. For example, fetch fresh data from backend to create a rich notification?
        if (questionCounter < questions.length) {
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            Intent okIntent = new Intent(NotificationService.this, QuestionActivity.class);
            Intent refuseIntent = new Intent(NotificationService.this, MainActivity.class);
            Intent postponeIntent = new Intent(NotificationService.this, MainActivity.class);

            PendingIntent okPendingIntent = PendingIntent.getActivity(this, 1, okIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent refusePendingIntent = PendingIntent.getActivity(this, 1, refuseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent postponePendingIntent = PendingIntent.getActivity(this, 1, postponeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentTitle(name)
                    .setAutoCancel(true)
                    .setColor(getResources().getColor(R.color.colorAccent))
                    .setContentText(questions[questionCounter++])
                    .setSmallIcon(R.drawable.ic_events)
                    .addAction(R.drawable.ic_ok, "Ok", okPendingIntent)
                    .addAction(R.drawable.ic_refuse, "Refuse", refusePendingIntent)
                    .addAction(R.drawable.ic_postpone, "Postpone", postponePendingIntent).build();;

            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    NOTIFICATION_ID,
                    new Intent(this, MainActivity.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);

            final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_ID, builder.build());

            Handler h = new Handler(Looper.getMainLooper());
            long delay = 30000;
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    manager.cancel(NOTIFICATION_ID);
                }
            }, delay);
        }
        else {
            PendingIntent alarmIntent = ResponseReceiver.getPendingIntent(getApplicationContext(), interval, name, questions);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(alarmIntent);
        }


    }
}
