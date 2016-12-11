package com.example.madiskar.experiencesamplingapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.NotificationCompat;
import android.os.Vibrator;

import java.util.ArrayList;
import java.util.Calendar;

public class NotificationService extends IntentService {

    private int alarmType;
    private int alarmTone;
    private SharedPreferences sharedPref;
    private static Context mContext = null;
    public NotificationService() {
        super(NotificationService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            long studyId = intent.getLongExtra("studyId", 0);
            processNotification((int)studyId);
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    public static Intent createIntentNotificationService(Context context, Study study) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.putExtra("studyId", study.getId());
        return intent;
    }

    private void processNotification(final int index) {
        mContext = getApplicationContext();
        Study study = DBHandler.getInstance(getApplicationContext()).getStudy(index);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        alarmType = Integer.valueOf(settings.getString("alarm_type", ""));
        alarmTone = Integer.valueOf(settings.getString("alarm_tone",""));

        if (!Calendar.getInstance().before(study.getBeginDate())) {

            sharedPref = getApplicationContext().getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            int dailyNotCount = sharedPref.getInt(String.valueOf(index), 0);
            Calendar rightNow = Calendar.getInstance();
            int hour = rightNow.get(Calendar.HOUR_OF_DAY);
            int minutes = rightNow.get(Calendar.MINUTE);

            String time = sharedPref.getString("TIME" + String.valueOf(study.getId()), "");
            if (!time.equals("")) {
                String[] parts = time.split(":");
                int hourPart = Integer.valueOf(parts[0]);
                int minutePart = Integer.valueOf(parts[1]);

                if (hour < hourPart) {

                    editor.putInt(String.valueOf(study.getId()), 0);
                    editor.apply();
                    dailyNotCount = 0;
                }
            }
            // STORE THE TIME OF THE NOTIFICATION
            editor.putString("TIME" + String.valueOf(study.getId()), hour + ":" + minutes);
            editor.apply();


            if (!beepFreePeriod(study)) {

                final AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

                int soundVolume = sharedPref.getInt("volume", -1);
                if (soundVolume == -1) {
                    editor.putInt("volume", audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)/2);
                    editor.apply();
                    soundVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)/2;
                }

                if (!Calendar.getInstance().after(study.getEndDate())) {
                    if (dailyNotCount < study.getNotificationsPerDay()) {
                        editor = sharedPref.edit();
                        editor.putInt(String.valueOf(study.getId()), dailyNotCount + 1);
                        editor.apply();

                        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                        String uniqueValue = (index + 1) + "00000";
                        String uniqueValue2 = (index + 1) + "00001";
                        String uniqueValue3 = (index + 1) + "00002";
                        Intent okIntent = new Intent(NotificationService.this, QuestionnaireActivity.class);
                        Intent postponeIntent = new Intent(NotificationService.this, PostponeReceiver.class);
                        Intent refuseIntent = new Intent(NotificationService.this, RefuseReceiver.class);
                        refuseIntent.putExtra("notificationId", index);
                        refuseIntent.putExtra("StudyId", index);

                        okIntent.putExtra("StudyId", study.getId());
                        okIntent.putExtra("notificationId", index);
                        postponeIntent.putExtra("notificationId", index);
                        postponeIntent.putExtra("StudyId", study.getId());
                        postponeIntent.putExtra("uniqueValue", Integer.valueOf(uniqueValue3));


                        PendingIntent okPendingIntent = PendingIntent.getActivity(this, Integer.valueOf(uniqueValue), okIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        PendingIntent refusePendingIntent = PendingIntent.getBroadcast(this, Integer.valueOf(uniqueValue2), refuseIntent, 0);
                        PendingIntent postponePendingIntent = PendingIntent.getBroadcast(this, -Integer.valueOf(uniqueValue3), postponeIntent, 0);

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

                            builder.setContentTitle(study.getName())
                                    .setPriority(Notification.PRIORITY_MAX)
                                    .setWhen(0)
                                    .setOngoing(true)
                                    .setColor(getResources().getColor(R.color.colorAccent))
                                    .setContentText(getString(R.string.questionnaire))
                                    .setSmallIcon(R.drawable.ic_questionnaire)
                                    .addAction(R.drawable.ic_ok, getString(R.string.ok2), okPendingIntent)
                                    .setSound(ringtone, AudioManager.STREAM_ALARM)
                                    .addAction(R.drawable.ic_refuse, getString(R.string.refuse), refusePendingIntent);


                        } else if (alarmType == 1) {
                            builder.setContentTitle(study.getName())
                                    .setPriority(Notification.PRIORITY_MAX)
                                    .setWhen(0)
                                    .setOngoing(true)
                                    .setColor(getResources().getColor(R.color.colorAccent))
                                    .setContentText(getString(R.string.questionnaire))
                                    .setSmallIcon(R.drawable.ic_questionnaire)
                                    .addAction(R.drawable.ic_ok, getString(R.string.ok2), okPendingIntent)
                                    .addAction(R.drawable.ic_refuse, getString(R.string.refuse), refusePendingIntent);

                            Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                            // Vibrate for 1000 milliseconds
                            v.vibrate(1000);
                        }

                        if (study.getPostponable()) {
                            builder.addAction(R.drawable.ic_postpone, getString(R.string.postpone), postponePendingIntent);
                        }

                        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                                index,
                                new Intent(),
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentIntent(pendingIntent);

                        final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        manager.notify(index, builder.build());

                        Handler h = new Handler(Looper.getMainLooper());
                        long delay = 300000;
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                manager.cancel(index);
                            }
                        }, delay);

                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, previousAlarmVolume, 0);
                            }
                        }, 2500);
                    }
                } else {
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


    public static boolean beepFreePeriod(Study study) {
        Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        boolean beepfree = false;
        ArrayList<BeepFerePeriod> beepFrees = new ArrayList<>(DBHandler.getInstance(mContext).getBeepFreePeriods());
        beepFrees.add(study.getDefaultBeepFree());
        for (int i = 0; i < beepFrees.size(); i++) {
            BeepFerePeriod bfp = beepFrees.get(i);
            int startHour = bfp.getStartTimeHour();
            int startMinute = bfp.getStartTimeMinute();
            int endHour = bfp.getEndTimeHour();
            int endMinute = bfp.getEndTimeMinute();
            if (hours >= startHour && hours <= endHour) {
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
        return beepfree;
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }
}
