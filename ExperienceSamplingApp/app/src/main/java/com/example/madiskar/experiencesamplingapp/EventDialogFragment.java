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
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class EventDialogFragment extends DialogFragment {
    String selectedItem = null;
    int selectedItemId = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Bundle args = getArguments();
        final Event[] events = (Event[]) args.getParcelableArray("EVENTS");
        final long studyId = args.getLong("studyId");

        final String[] items = new String[events.length];
        for (int i = 0; i < events.length; i++) {
            items[i] = events[i].getName();
        }

        DBHandler dbHandler = DBHandler.getInstance(getContext());
        ArrayList<Event> activeEventsClone = new ArrayList<>();
        for (Study s: dbHandler.getAllStudies()) {
            for (Event e: s.getEvents()) {
                Calendar startTime = dbHandler.getEventStartTime(e.getId());
                if (startTime != null) {
                    activeEventsClone.add(e);
                }
            }
        }
        final ArrayList<Event> activeEvents = new ArrayList<>(activeEventsClone);

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

                            try {
                                Intent stopIntent = new Intent(getContext(), StopReceiver.class);

                                final int uniqueValue = ((int) events[selectedItemId].getId())*-1;
                                final int uniqueId = (int) (events[selectedItemId].getId())*-100;

                                String calendarString = DBHandler.calendarToString(Calendar.getInstance());

                                stopIntent.putExtra("start", calendarString);
                                stopIntent.putExtra("notificationId", uniqueValue);
                                stopIntent.putExtra("controlNotificationId", uniqueId);
                                stopIntent.putExtra("studyId", studyId);
                                stopIntent.putExtra("eventId", events[selectedItemId].getId());
                                Calendar calendar = Calendar.getInstance();
                                events[selectedItemId].setStartYear(calendar.get(calendar.YEAR));
                                events[selectedItemId].setStartMonth(calendar.get(calendar.MONTH));
                                events[selectedItemId].setStartDayOfMonth(calendar.get(calendar.DAY_OF_MONTH));
                                events[selectedItemId].setStartTimeHour(calendar.get(calendar.HOUR_OF_DAY));
                                events[selectedItemId].setStartTimeMinute(calendar.get(calendar.MINUTE));
                                events[selectedItemId].setStartTimeInMillis(calendar.getTimeInMillis());
                                events[selectedItemId].setStartTimeCalendar(calendarString);

                                DBHandler.getInstance(getContext()).insertEventTime(events[selectedItemId].getId(), events[selectedItemId].getStartTimeCalendar());

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
                                    manager.notify(uniqueValue, mBuilder.build());
                                    int controlTime = events[selectedItemId].getControlTime();
                                    String unit = events[selectedItemId].getUnit();

                                    int multiplier = 0;
                                    if (unit.equals("h")) {
                                        multiplier = controlTime * 60 * 60 * 1000;
                                    } else if (unit.equals("m")) {
                                        multiplier = controlTime * 60 * 1000;
                                    } else if (unit.equals("d")) {
                                        multiplier = controlTime * 24 * 60 * 60 * 1000;
                                    }

                                    Intent controltimeIntent = new Intent(getContext(), ControlTimeReceiver.class);
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
        try {
            DBHandler db = DBHandler.getInstance(ctx);
            Study study = db.getStudy(studyId);
            NotificationManager nMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            for (Event event : study.getEvents()) {
                Calendar startTime = db.getEventStartTime(event.getId());
                if (startTime != null) {
                    nMgr.cancel((int)(event.getId()*-1));
                    Intent controltimeIntent = new Intent(ctx, ControlTimeReceiver.class);
                    controltimeIntent.putExtra("eventId", event.getId());
                    controltimeIntent.putExtra("controlTime", (int) event.getControlTime());
                    controltimeIntent.putExtra("eventName", event.getName());
                    controltimeIntent.putExtra("notificationId", ((int)event.getId())*-100);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, ((int)event.getId())*-100, controltimeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    db.deleteEventTimeEntry(event.getId());
                }
            }
        } catch (Exception e) {}
    }
}