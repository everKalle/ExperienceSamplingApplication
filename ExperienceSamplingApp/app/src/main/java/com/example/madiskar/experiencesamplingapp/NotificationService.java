package com.example.madiskar.experiencesamplingapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.os.Vibrator;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class NotificationService extends IntentService {

    private int NOTIFICATION_ID;
    private static String NOTIFICATION_NAME = "STUDY";
    private static String NOTIFICATION_INTERVAL = "INTERVAL";
    private static String STUDY_QUESTIONS = "QUESTIONS";
    private static String DAILY_NOTIFICATION_LIMIT = "LIMIT";
    private int alarmType;
    private int alarmTone;
    public static final String PREFS_NAME = "preferences";
    private boolean started = false;
    private SharedPreferences sharedPref;
    private static Context mContext = null;
    private final static int MAX_VOLUME = 100;
    private MediaPlayer mediaPlayer;
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
            long studyId = intent.getLongExtra("studyId", 0);

            ArrayList<Study> studies = DBHandler.getInstance(getApplicationContext()).getAllStudies();
            Study studyParam = null;
            for (Study s: studies) {
                if (s.getId() == (int) studyId) {
                    studyParam = s;
                }
            }
            processNotification(name, studyParam.getQuesstionnaire().getQuestions(), interval, notificationsPerDay, (int)studyId); //TODO: casting long to int this way might be problematic, should switch to long
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
        mContext = getApplicationContext();
        Study study = DBHandler.getInstance(getApplicationContext()).getStudy(index);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        alarmType = Integer.valueOf(settings.getString("alarm_type", ""));
        alarmTone = Integer.valueOf(settings.getString("alarm_tone",""));

        Log.v("vark", String.valueOf(study.getDefaultBeepFree().getStartTimeHour()));
        // Log.v("SIIN", "");
        if (!Calendar.getInstance().before(study.getBeginDate())) {
            if (!beepFreePeriod(study)) {
                //Log.v("DailyNotValue", String.valueOf(ResponseReceiver.dailyNotificationCount.get(index)));
                // Log.v("COUNTER", String.valueOf(ResponseReceiver.dailyNotificationCount.get(index)));
                // Log.v("ALLOWED COUNTER", String.valueOf(notificationsPerDay));


                sharedPref = getApplicationContext().getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);
                int dailyNotCount = sharedPref.getInt(String.valueOf(index), 0);

                Log.v("PAEVANE NOTTIDE ARV", String.valueOf(dailyNotCount));

                int soundVolume = sharedPref.getInt("volume", -1);
                if (soundVolume == -1) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("volume", 50);
                    editor.apply();
                    soundVolume = 50;
                }

                Log.v("sound vol", String.valueOf(soundVolume));
                //Log.v("DAILYNOTS", String.valueOf(study.getName()) + " " + String.valueOf(dailyNotCount));

                Calendar rightNow = Calendar.getInstance();
                int hour = rightNow.get(Calendar.HOUR_OF_DAY);
                int minutes = rightNow.get(Calendar.MINUTE);

                String time = sharedPref.getString("TIME" + String.valueOf(study.getId()), "");
                if (!time.equals("")) {
                    String[] parts = time.split(":");
                    int hourPart = Integer.valueOf(parts[0]);
                    int minutePart = Integer.valueOf(parts[1]);

                    if (hour < hourPart) {

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(String.valueOf(study.getId()), 0);
                        editor.apply();
                        dailyNotCount = 0;
                    }
                }


                SharedPreferences.Editor editor = sharedPref.edit(); // STORE THE TIME OF THE NOTIFICATION
                editor.putString("TIME" + String.valueOf(study.getId()), hour + ":" + minutes);
                editor.apply();

                Log.v("nimi", study.getName());
                Log.v("aja teema", String.valueOf(!Calendar.getInstance().after(study.getEndDate())));
                Log.v("tapsem aja teema", String.valueOf(Calendar.getInstance()));
                Log.v("tapsem aja teema 2", String.valueOf(study.getEndDate()));

                if (!Calendar.getInstance().after(study.getEndDate())) {
                    if (dailyNotCount < notificationsPerDay) {

                        editor = sharedPref.edit();
                        editor.putInt(String.valueOf(study.getId()), dailyNotCount + 1);
                        editor.apply();


                        if (alarmType == 0) {

                            if (alarmTone == 0) {
                                mediaPlayer = MediaPlayer.create(this, R.raw.chime_1);
                            } else if (alarmTone == 1)
                                mediaPlayer = MediaPlayer.create(this, R.raw.chime_2);
                            else
                                mediaPlayer = MediaPlayer.create(this, R.raw.chime_2);
                            if (!started) {
                                float volume = (float) (1 - (Math.log(MAX_VOLUME - soundVolume) / Math.log(MAX_VOLUME)));
                                mediaPlayer.setVolume(volume, volume);
                                mediaPlayer.start();
                                started = true;
                            }
                        } else if (alarmType == 1) {
                            Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                            // Vibrate for 1000 milliseconds
                            v.vibrate(1000);
                        }

                        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                        String uniqueValue = (index + 1) + "00000";
                        String uniqueValue2 = (index + 1) + "00001";
                        String uniqueValue3 = (index + 1) + "00002";
                        Intent okIntent = new Intent(NotificationService.this, QuestionnaireActivity.class);
                        Intent postponeIntent = new Intent(NotificationService.this, PostponeReceiver.class);
                        Intent refuseIntent = new Intent(NotificationService.this, RefuseReceiver.class);
                        refuseIntent.putExtra("notificationId", index);
                        refuseIntent.putExtra("StudyId", index);

                        //okIntent.putExtra("QUESTIONNAIRE", study.getQuesstionnaire());
                        okIntent.putExtra("StudyId", study.getId());
                        okIntent.putExtra("notificationId", index);
                        postponeIntent.putExtra(NOTIFICATION_INTERVAL, interval);
                        postponeIntent.putExtra("postpone", study.getPostponeTime());
                        postponeIntent.putExtra("notificationId", index);
                        postponeIntent.putExtra("StudyId", study.getId());
                        postponeIntent.putExtra("uniqueValue", Integer.valueOf(uniqueValue3));

                        PendingIntent okPendingIntent = PendingIntent.getActivity(this, Integer.valueOf(uniqueValue), okIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        PendingIntent refusePendingIntent = PendingIntent.getBroadcast(this, Integer.valueOf(uniqueValue2), refuseIntent, 0);
                        PendingIntent postponePendingIntent = PendingIntent.getBroadcast(getBaseContext(), -Integer.valueOf(uniqueValue3), postponeIntent, 0);

                        builder.setContentTitle(name)
                                .setOngoing(true)
                                .setColor(getResources().getColor(R.color.colorAccent))
                                .setContentText(getString(R.string.questionnaire))
                                .setSmallIcon(R.drawable.ic_events)
                                .addAction(R.drawable.ic_ok, getString(R.string.ok2), okPendingIntent)
                                .addAction(R.drawable.ic_refuse, getString(R.string.refuse), refusePendingIntent)
                                .addAction(R.drawable.ic_postpone, getString(R.string.postpone), postponePendingIntent).build();
                        ;

                        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                                index,
                                new Intent(),
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentIntent(pendingIntent);

                        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                        manager.notify(index, builder.build());

                        Handler h = new Handler(Looper.getMainLooper());
                        long delay = 300000;
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                manager.cancel(index);
                            }
                        }, delay);
                    } else {

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) study.getId(), new Intent(getApplicationContext(), ResponseReceiver.class), 0);
                        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                        am.cancel(pendingIntent);

                        ResponseReceiver rr = new ResponseReceiver(study);
                        rr.setupAlarm(mContext, false);

                        //TODO: add daily notification resets
                    }
                } else {
                    Log.v("Jah, jouab", "siia");
                    cancelNotification(getApplicationContext(), (int) study.getId());
                    Intent intent = new Intent(mContext, QuestionnaireActivity.class);
                    ResponseReceiver.cancelExistingAlarm(mContext, intent, Integer.valueOf((study.getId() + 1) + "00002"), false);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) study.getId(), new Intent(getApplicationContext(), ResponseReceiver.class), 0);
                    AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    am.cancel(pendingIntent);
                    for (Event event : EventDialogFragment.activeEvents) {
                        if (event.getStudyId() == study.getId()) {
                            Intent stopIntent = new Intent(getApplicationContext(), StopReceiver.class);
                            stopIntent.putExtra("start", event.getStartTimeCalendar());
                            stopIntent.putExtra("notificationId", EventDialogFragment.uniqueValueMap.get((int) event.getId()));
                            stopIntent.putExtra("studyId", event.getStudyId());
                            stopIntent.putExtra("controlNotificationId", EventDialogFragment.uniqueControlValueMap.get((int) event.getId()));
                            stopIntent.putExtra("eventId", event.getId());
                            sendBroadcast(stopIntent);
                        }
                    }
                    DBHandler.getInstance(getApplicationContext()).deleteStudyEntry(study.getId());
                }
            }
        }
    }


    /*
    public static void modifyBeepFreePeriod(int index, BeepFerePeriod bfp) {
        beepFreePeriods.set(index, bfp);
    }

    /*
    public static void removeBeepFreePeriod(int index) {
        beepFreePeriods.remove(index);
        for (int i = index; i < beepFreePeriods.size(); i++)
            beepFreePeriods.get(i).setId(beepFreePeriods.get(i).getId()-1);
    }
    */

    public static boolean beepFreePeriod(Study study) {
        Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        boolean beepfree = false;
        ArrayList<BeepFerePeriod> beepFrees = new ArrayList<>(DBHandler.getInstance(mContext).getBeepFreePeriods());
        beepFrees.add(study.getDefaultBeepFree());
        // Log.v("SIZE", String.valueOf(beepFrees.size()));
        Log.v("jajajajajajajajaj", "ei");
        for (int i = 0; i < beepFrees.size(); i++) {
            BeepFerePeriod bfp = beepFrees.get(i);
            int startHour = bfp.getStartTimeHour();
            int startMinute = bfp.getStartTimeMinute();
            int endHour = bfp.getEndTimeHour();
            int endMinute = bfp.getEndTimeMinute();
            Log.v("kontroll", String.valueOf(hours) + " >= " + String.valueOf(startHour) + " && " + String.valueOf(hours) + " <= " + String.valueOf(endHour));
            if (hours >= startHour && hours <= endHour) {
                Log.v("kontroll2_1", String.valueOf(hours) + " == " + String.valueOf(startHour) + " && " + String.valueOf(minutes) + " < " + String.valueOf(startMinute));
                Log.v("kontroll2_2", String.valueOf(hours) + " == " + String.valueOf(endHour) + " && " + String.valueOf(minutes) + " > " + String.valueOf(endMinute));
                if ((hours == startHour && minutes < startMinute && hours != endHour) || (hours == endHour && minutes > endMinute && hours != startHour)) {
                }
                else if (hours == startHour && hours == endHour && startMinute > endMinute && minutes > endMinute && minutes < startMinute) {
                }
                else if (hours == startHour && hours == endHour && startMinute < endMinute && (minutes < startMinute || minutes > endMinute)) {
                }
                else {
                    beepfree = true;
                }
            }
            else if (hours >= startHour && startHour > endHour) {
                if (hours == startHour && minutes < startMinute) {
                }
                else
                 beepfree = true;
            }
            else if (hours < startHour && hours <= endHour && startHour > endHour) {
                if (hours == endHour && minutes > endMinute) {
                }
                else
                 beepfree = true;
            }
        }
        Log.v("jajajajajajajajaj", "ei");
        Log.v("Beepfree", String.valueOf(beepfree) + " for study " + study.getName());
        return beepfree;
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }
}
