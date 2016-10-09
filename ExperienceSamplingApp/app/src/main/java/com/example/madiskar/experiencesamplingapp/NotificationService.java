package com.example.madiskar.experiencesamplingapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NotificationService extends IntentService {

    private static int dailyNotificationCounter = 0;
    private static final int NOTIFICATION_ID = 1;
    private static String NOTIFICATION_NAME = "STUDY";
    private static String NOTIFICATION_INTERVAL = "INTERVAL";
    private static String STUDY_QUESTIONS = "QUESTIONS";
    private static String DAILY_NOTIFICATION_LIMIT = "LIMIT";
    private static Study studyRef;
    private static ArrayList<BeepFerePeriod> beepFreePeriods = new ArrayList<BeepFerePeriod>();

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
            int notificationsPerDay = intent.getIntExtra(DAILY_NOTIFICATION_LIMIT, 0);
            Log.v("TESTING 3", String.valueOf(studyRef.getQuesstionnaire().getQuestions()[0] instanceof FreeTextQuestion) + " " + studyRef.getQuesstionnaire().getQuestions()[0].getText());
            processNotification(name, studyRef.getQuesstionnaire().getQuestions(), interval, notificationsPerDay);
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    public static Intent createIntentNotificationService(Context context, int interval, String name, String[] questions, int notificationsPerDay, Study study) {
        studyRef = study;
        Intent intent = new Intent(context, NotificationService.class);
        intent.putExtra(NOTIFICATION_NAME, name);
        intent.putExtra(NOTIFICATION_INTERVAL, interval);
        intent.putExtra(STUDY_QUESTIONS, questions);
        intent.putExtra(DAILY_NOTIFICATION_LIMIT, notificationsPerDay);
        return intent;
    }

    private void processNotification(String name, Question[] questions, int interval, int notificationsPerDay) {

        if (!beepFreePeriod()) {
            if (dailyNotificationCounter != notificationsPerDay) {
                dailyNotificationCounter++;

                final int NOTIFICATION_ID = 1;

                final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                Intent okIntent = new Intent(NotificationService.this, QuestionnaireActivity.class);
                Intent postponeIntent = new Intent(getBaseContext(), PostponeReceiver.class);
                Intent refuseIntent = new Intent(NotificationService.this, RefuseReceiver.class);
                refuseIntent.putExtra("notificationId",NOTIFICATION_ID);
                refuseIntent.putExtra("StudyId", studyRef.getId());

                okIntent.putExtra("QUESTIONNAIRE", studyRef.getQuesstionnaire());
                Log.v("TESTING 4", String.valueOf(studyRef.getQuesstionnaire().getQuestions()[0] instanceof FreeTextQuestion) + " " + studyRef.getQuesstionnaire().getQuestions()[0].getText());
                postponeIntent.putExtra(NOTIFICATION_INTERVAL, interval);


                PendingIntent okPendingIntent = PendingIntent.getActivity(this, 1, okIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent refusePendingIntent = PendingIntent.getBroadcast(this, 0, refuseIntent, 0);
                //PendingIntent postponePendingIntent = PendingIntent.getActivity(this, 1, postponeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent postponePendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, postponeIntent, 0);

                builder.setContentTitle(name)
                        .setAutoCancel(true)
                        .setColor(getResources().getColor(R.color.colorAccent))
                        .setContentText(studyRef.getName() + " questionnaire")
                        .setSmallIcon(R.drawable.ic_events)
                        .addAction(R.drawable.ic_ok, "Ok", okPendingIntent)
                        .addAction(R.drawable.ic_refuse, "Refuse", refusePendingIntent)
                        .addAction(R.drawable.ic_postpone, "Postpone", postponePendingIntent).build();
                ;

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
            } else {
                if (dailyNotificationCounter == notificationsPerDay) {
                    dailyNotificationCounter = 0;
                }
                if (dailyNotificationCounter < questions.length) {
                    ResponseReceiver.setupAlarm(getApplicationContext(), studyRef, false);
                }
            }
        }
    }

    public static void addBeepFreePeriod(BeepFerePeriod bfp) {
        beepFreePeriods.add(bfp);

    }
    public static void modifyBeepFreePeriod(int index, BeepFerePeriod bfp) {
        beepFreePeriods.set(index, bfp);
    }

    public static void removeBeepFreePeriod(int index) {
        beepFreePeriods.remove(index);
        for (int i = index; i < beepFreePeriods.size(); i++)
            beepFreePeriods.get(i).setId(beepFreePeriods.get(i).getId()-1);
    }

    public static boolean beepFreePeriod() {
        Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR);
        int minutes = c.get(Calendar.MINUTE);
        boolean beepfree = false;

        for (int i = 0; i < beepFreePeriods.size(); i++) {
            BeepFerePeriod bfp = beepFreePeriods.get(i);
            int startHour = bfp.getStartTimeHour();
            int startMinute = bfp.getStartTimeMinute();
            int endHour = bfp.getEndTimeHour();
            int endMinute = bfp.getEndTimeMinute();
            if (hours >= startHour && hours <= endHour) {
                if ((hours == startHour && minutes < startMinute) || (hours == endHour && minutes > endMinute)) {
                }
                else
                    beepfree = true;
            }
        }
        return beepfree;
    }
}