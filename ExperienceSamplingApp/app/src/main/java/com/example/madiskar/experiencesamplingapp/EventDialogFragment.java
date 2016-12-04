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
import android.widget.Toast;

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
    public static ArrayList<Event> activeEvents = new ArrayList<>();
    public static Map<Integer, Integer> uniqueValueMap = new HashMap<>();
    public static Map<Integer, ArrayList<Integer>> studyToNotificationIdMap = new HashMap<>();
    public static Map<Integer, Integer> uniqueControlValueMap = new HashMap<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Log.v("activeEvents", String.valueOf(activeEvents.size()));

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

                        boolean alreadyExists = false;

                        for (Event event : activeEvents) {
                            if (events[selectedItemId].getStudyId() == event.getStudyId() && events[selectedItemId].getName().equals(event.getName())) {
                                alreadyExists = true;
                            }
                        }

                        if (selectedItem != null && !alreadyExists) {
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
                                uniqueValueMap.put((int) events[selectedItemId].getId(), uniqueValue);
                                Log.v("VALUES", Arrays.toString(values.toArray()));
                                studyToNotificationIdMap.put((int) studyId, values);

                                //Log.v("IDDD", String.valueOf(selectedItemId));
                                Intent stopIntent = new Intent(getContext(), StopReceiver.class);

                                final int uniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
                                uniqueControlValueMap.put((int) events[selectedItemId].getId(), uniqueId);

                                String calendarString = DBHandler.calendarToString(Calendar.getInstance());

                                stopIntent.putExtra("start", calendarString);
                                stopIntent.putExtra("notificationId", uniqueValue);
                                stopIntent.putExtra("controlNotificationId", uniqueId);
                                stopIntent.putExtra("studyId", studyId);
                                //Log.v("EVENT", events[selectedItemId].getName());
                                stopIntent.putExtra("eventId", events[selectedItemId].getId());
                                Calendar calendar = Calendar.getInstance();
                                events[selectedItemId].setStartYear(calendar.get(calendar.YEAR));
                                events[selectedItemId].setStartMonth(calendar.get(calendar.MONTH));
                                events[selectedItemId].setStartDayOfMonth(calendar.get(calendar.DAY_OF_MONTH));
                                events[selectedItemId].setStartTimeHour(calendar.get(calendar.HOUR_OF_DAY));
                                events[selectedItemId].setStartTimeMinute(calendar.get(calendar.MINUTE));
                                events[selectedItemId].setStartTimeInMillis(calendar.getTimeInMillis());
                                events[selectedItemId].setStartTimeCalendar(calendarString);
                                activeEvents.add(events[selectedItemId]);

                                PendingIntent stopPendingIntent = PendingIntent.getBroadcast(getActivity(), uniqueValue, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                NotificationCompat.Builder mBuilder =
                                        new NotificationCompat.Builder(getContext())
                                                .setSmallIcon(R.drawable.ic_events)
                                                .setContentTitle(getString(R.string.active_event))
                                                .setContentText(selectedItem)
                                                .setWhen(System.currentTimeMillis())
                                                .setUsesChronometer(true)
                                                .addAction(R.drawable.ic_stop, getString(R.string.stop), stopPendingIntent)
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
                                    if (unit.equals("h")) {
                                        multiplier = controlTime * 60 * 60 * 1000;
                                    } else if (unit.equals("m")) {
                                        multiplier = controlTime * 60 * 1000;
                                    } else if (unit.equals("d")) {
                                        multiplier = controlTime * 24 * 60 * 60 * 1000;
                                    }
                                    Log.v("controlTimeMillis", String.valueOf(multiplier));

                                    Intent controltimeIntent = new Intent(getContext(), ControlTimeReceiver.class);
                                    //controltimeIntent.putExtra("notificationId", uniqueValue);
                                    controltimeIntent.putExtra("eventId", events[selectedItemId].getId());
                                    controltimeIntent.putExtra("controlTime", (int) controlTime);
                                    controltimeIntent.putExtra("eventName", events[selectedItemId].getName());

                                    controltimeIntent.putExtra("notificationId", uniqueId);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), uniqueId, controltimeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + multiplier, pendingIntent);
                                }
                            } catch (Exception e) {
                            }
                        }
                        else
                          if (selectedItem != null)
                            Toast.makeText(getContext(), "The selected event " + selectedItem + " is already active!", Toast.LENGTH_SHORT).show();
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
        //TODO: error here when quitting study, doesnt crash the app but shows in logcat
        for (int notifyId : notifIdArrayList)
            nMgr.cancel(notifyId);
    }
}