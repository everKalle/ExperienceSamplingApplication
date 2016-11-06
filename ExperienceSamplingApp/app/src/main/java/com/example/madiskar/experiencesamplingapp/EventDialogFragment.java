package com.example.madiskar.experiencesamplingapp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joosep on 22.10.2016.
 */
public class EventDialogFragment extends DialogFragment {
    String selectedItem = null;
    long elapsedTime = 0;
    int selectedItemId = 0;
    public static Map<Integer, ArrayList<Integer>> studyToNotificationIdMap = new HashMap<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Bundle args = getArguments();
        final Event[] events = (Event[]) args.getParcelableArray("EVENTS");
        final long studyId = args.getLong("studyId");
        Log.v("STUDYID", String.valueOf(studyId));

        if (studyToNotificationIdMap.get((int)studyId) == null)
            studyToNotificationIdMap.put((int)studyId, new ArrayList<Integer>());

        //View view = getActivity().getLayoutInflater().inflate(R.layout.event_dialog_layout, null);
        final String[] items = new String[events.length];
        for (int i = 0; i < events.length; i++) {
            items[i] = events[i].getName();
        }

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_event)
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedItem = (String) items[i];
                        selectedItemId = i;
                    }
                })
                .setPositiveButton(R.string.start, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int uniqueValue = 10000 + (int) studyId;
                        Log.v("UniqueValue", String.valueOf(uniqueValue));

                        boolean unique = false;
                        while (!unique) {
                            unique = true;
                            for (Map.Entry<Integer, ArrayList<Integer>> entry : studyToNotificationIdMap.entrySet()) {
                                for (int i = 0; i < entry.getValue().size(); i++) {
                                    if (uniqueValue == entry.getValue().get(i)) {
                                        Log.v("SIIA EI JOUA", "jah");
                                        unique = false;
                                        uniqueValue += 1;
                                    }
                                }
                            }
                        }

                        try {
                            ArrayList<Integer> values = studyToNotificationIdMap.get((int) studyId);
                            values.add(uniqueValue);
                            Log.v("VALUES", Arrays.toString(values.toArray()));
                            studyToNotificationIdMap.put((int)studyId, values);

                            //Log.v("IDDD", String.valueOf(selectedItemId));
                            Intent stopIntent = new Intent(getContext(), StopReceiver.class);

                            stopIntent.putExtra("start", DBHandler.calendarToString(Calendar.getInstance()));
                            stopIntent.putExtra("notificationId", uniqueValue);
                            stopIntent.putExtra("studyId", studyId);
                            //Log.v("EVENT", events[selectedItemId].getName());
                            stopIntent.putExtra("eventId", events[selectedItemId].getId());

                            PendingIntent stopPendingIntent = PendingIntent.getBroadcast(getActivity(), uniqueValue, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(getContext())
                                            .setSmallIcon(R.drawable.ic_events)
                                            .setContentTitle("Active Event")
                                            .setContentText(selectedItem)
                                            .setWhen(System.currentTimeMillis())
                                            .setUsesChronometer(true)
                                            .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
                                            .setOngoing(true);

                            NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            if (selectedItem != null) {
                                //Log.v("Unique", String.valueOf(uniqueValue));
                                manager.notify(uniqueValue, mBuilder.build());
                                int controlTime = events[selectedItemId].getControlTime();
                                Log.v("controlTime", String.valueOf(controlTime));
                                Log.v("unit", String.valueOf(events[selectedItemId].getUnit()));
                                String unit = events[selectedItemId].getUnit();

                                int multiplier = 0;
                                double minuteMultiplier = 1;
                                if (unit.equals("h")) {
                                    multiplier = controlTime * 60 * 60 * 1000;
                                    minuteMultiplier = 60;
                                } else if (unit.equals("m")) {
                                    multiplier = controlTime * 60 * 1000;
                                } else if (unit.equals("s")) {
                                    multiplier = controlTime * 1000;
                                    minuteMultiplier = 1 / 60.0;
                                }
                                Log.v("controlTimeMillis", String.valueOf(multiplier * minuteMultiplier));

                                Intent controltimeIntent = new Intent(getContext(), ControlTimeReceiver.class);
                                controltimeIntent.putExtra("notificationId", uniqueValue);
                                controltimeIntent.putExtra("eventId", events[selectedItemId].getId());
                                controltimeIntent.putExtra("controlTime", (int) (controlTime * minuteMultiplier));
                                controltimeIntent.putExtra("eventName", events[selectedItemId].getName());


                                int uniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), uniqueId, controltimeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + multiplier, pendingIntent);
                            }
                        } catch (Exception e) {}
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        //builder.setView(view);
        return builder.create();
    }

    public static void cancelEvents(Context ctx, int studyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        ArrayList<Integer> notifIdArrayList = studyToNotificationIdMap.get(studyId);
        for (int notifyId : notifIdArrayList)
            nMgr.cancel(notifyId);
    }
}