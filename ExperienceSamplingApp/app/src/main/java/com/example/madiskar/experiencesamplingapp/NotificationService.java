package com.example.madiskar.experiencesamplingapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.NotificationCompat;
import android.os.Vibrator;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NotificationService extends IntentService {

    private int NOTIFICATION_ID;
    private static String NOTIFICATION_NAME = "STUDY";
    private static String NOTIFICATION_INTERVAL = "INTERVAL";
    private static String STUDY_QUESTIONS = "QUESTIONS";
    private static String DAILY_NOTIFICATION_LIMIT = "LIMIT";
    public static ArrayList<BeepFerePeriod> beepFreePeriods = new ArrayList<BeepFerePeriod>();
    private int alarmType;
    private int alarmTone;
    public static final String PREFS_NAME = "preferences";
    private boolean started = false;

    public NotificationService() {
        super(NotificationService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            //Log.v("handling", "1 or 2");
            String action = intent.getAction();
            String name = intent.getStringExtra(NOTIFICATION_NAME);
            int interval = intent.getIntExtra(NOTIFICATION_INTERVAL,0);
            String[] textQuestions = intent.getStringArrayExtra(STUDY_QUESTIONS);
            int notificationsPerDay = intent.getIntExtra(DAILY_NOTIFICATION_LIMIT, 0);
            long studyId = intent.getLongExtra("studyId", 0);

            ArrayList<Study> studies = ResponseReceiver.studies;
            Study studyParam = null;
            for (Study s: studies) {
                if (s.getId() == (int) studyId) {
                    studyParam = s;
                }
            }
            processNotification(name, studyParam.getQuesstionnaire().getQuestions(), interval, notificationsPerDay, (int) studyId);
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    public static Intent createIntentNotificationService(Context context, int interval, String name, String[] questions, int notificationsPerDay, Study study) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.putExtra(NOTIFICATION_NAME, name);
        intent.putExtra(NOTIFICATION_INTERVAL, interval);
        intent.putExtra(STUDY_QUESTIONS, questions);
        intent.putExtra(DAILY_NOTIFICATION_LIMIT, notificationsPerDay);
        intent.putExtra("studyId", study.getId());
        return intent;
    }

    private void processNotification(String name, Question[] questions, int interval, int notificationsPerDay, final int index) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        alarmType = Integer.valueOf(settings.getString("alarm_type", ""));
        alarmTone = Integer.valueOf(settings.getString("alarm_tone",""));
        //final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.opa);
        if (!beepFreePeriod()) {
            //Log.v("DailyNotValue", String.valueOf(ResponseReceiver.dailyNotificationCount.get(index)));
            if (ResponseReceiver.dailyNotificationCount.get(index) < notificationsPerDay) {
                ResponseReceiver.dailyNotificationCount.put(index, ResponseReceiver.dailyNotificationCount.get(index) + 1);
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                if (alarmType == 0) {
                    if (alarmTone == 0) {
                        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300);
                        if (!started) {
                            //mediaPlayer.start();
                            started = true;
                        }
                    }
                    else if (alarmTone == 1)
                        toneG.startTone(ToneGenerator.TONE_PROP_BEEP, 300);
                    else
                        toneG.startTone(ToneGenerator.TONE_PROP_PROMPT, 300);
                }
                else if (alarmType == 1) {
                    Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 1000 milliseconds
                    v.vibrate(1000);
                }


                final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                String uniqueValue = (index+1) + "00000";
                String uniqueValue2 = (index+1) + "00001";
                String uniqueValue3 = (index+1) + "00002";
                Intent okIntent = new Intent(NotificationService.this, QuestionnaireActivity.class);
                Intent postponeIntent = new Intent(getBaseContext(), PostponeReceiver.class);
                Intent refuseIntent = new Intent(NotificationService.this, RefuseReceiver.class);
                refuseIntent.putExtra("notificationId",index);
                refuseIntent.putExtra("StudyId", ResponseReceiver.studies.get(index).getId());

                okIntent.putExtra("QUESTIONNAIRE", ResponseReceiver.studies.get(index).getQuesstionnaire());
                okIntent.putExtra("notificationId", index);
                postponeIntent.putExtra(NOTIFICATION_INTERVAL, interval);
                postponeIntent.putExtra("QUESTIONNAIRE", ResponseReceiver.studies.get(index).getQuesstionnaire());
                postponeIntent.putExtra("postpone", ResponseReceiver.studies.get(index).getPostponeTime());
                postponeIntent.putExtra("INTERVAL", ResponseReceiver.studies.get(index).getNotificationInterval());
                postponeIntent.putExtra("notificationId", index);
                postponeIntent.putExtra("uniqueValue", Integer.valueOf(uniqueValue3));


                PendingIntent okPendingIntent = PendingIntent.getActivity(this, Integer.valueOf(uniqueValue), okIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent refusePendingIntent = PendingIntent.getBroadcast(this, Integer.valueOf(uniqueValue2), refuseIntent, 0);
                //PendingIntent postponePendingIntent = PendingIntent.getActivity(this, 1, postponeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent postponePendingIntent = PendingIntent.getBroadcast(getBaseContext(), -Integer.valueOf(uniqueValue3), postponeIntent, 0);

                builder.setContentTitle(name)
                        .setOngoing(true)
                        .setAutoCancel(true)
                        .setColor(getResources().getColor(R.color.colorAccent))
                        .setContentText(ResponseReceiver.studies.get(index).getName() + " questionnaire")
                        .setSmallIcon(R.drawable.ic_events)
                        .addAction(R.drawable.ic_ok, "Ok", okPendingIntent)
                        .addAction(R.drawable.ic_refuse, "Refuse", refusePendingIntent)
                        .addAction(R.drawable.ic_postpone, "Postpone", postponePendingIntent).build();
                ;

                PendingIntent pendingIntent = PendingIntent.getActivity(this,
                        index,
                        new Intent(this, MainActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);

                final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                Log.v("Unique notification", String.valueOf(index));
                manager.notify(index, builder.build());

                Handler h = new Handler(Looper.getMainLooper());
                long delay = 30000;
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        manager.cancel(index);
                    }
                }, delay);
            } else {
                if (ResponseReceiver.dailyNotificationCount.get(index) == notificationsPerDay) {
                    Study studyParam = null;
                    for(Study s : ResponseReceiver.studies) {
                        if (s.getId() == index) {
                            studyParam = s;
                        }
                    }
                    //Log.v("CANCEL ALARM", "TRUE");
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) studyParam.getId(), new Intent(getApplicationContext(), ResponseReceiver.class), 0);
                    AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    am.cancel(pendingIntent);

                    //TODO: add daily notification resets
                }
                if (ResponseReceiver.dailyNotificationCount.get(index) < questions.length) {
                    //ResponseReceiver.setupAlarm(getApplicationContext(), study, false);
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
        int hours = c.get(Calendar.HOUR_OF_DAY);
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
                else {
                    beepfree = true;
                }
            }
        }
        return beepfree;
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }
}
