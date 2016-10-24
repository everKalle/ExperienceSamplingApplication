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

/**
 * Created by Joosep on 22.10.2016.
 */
public class EventDialogFragment extends DialogFragment {
    String selectedItem = null;
    long elapsedTime = 0;
    static int notificationID = 10;
    int selectedItemId = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Bundle args = getArguments();
        final Event[] events = (Event[]) args.getParcelableArray("EVENTS");
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
                        Log.v("IDDD", String.valueOf(selectedItemId));
                        Intent stopIntent = new Intent(getContext(), StopReceiver.class);

                        long startTime = SystemClock.elapsedRealtime();
                        stopIntent.putExtra("start", startTime);
                        stopIntent.putExtra("notificationId", notificationID);
                        Log.v("EVENT", events[selectedItemId].getName());
                        stopIntent.putExtra("eventId", events[selectedItemId].getId());

                        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(getContext())
                                        .setSmallIcon(R.drawable.ic_events)
                                        .setContentTitle("Active Event")
                                        .setContentText(selectedItem)
                                        .setWhen(System.currentTimeMillis())
                                        .setUsesChronometer(true)
                                        .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent);

                        NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        if (selectedItem != null) {
                            manager.notify(notificationID++, mBuilder.build());
                            int controlTime = events[selectedItemId].getControlTime();
                            String unit = events[selectedItemId].getUnit();

                            int multiplier = 0;
                            double minuteMultiplier = 1;
                            if (unit.equals("h")) {
                                multiplier = controlTime * 60 * 60 * 1000;
                                minuteMultiplier = 60;
                            }
                            else if (unit.equals("m")) {
                                multiplier = controlTime * 60 * 1000;
                                Log.v("HERE I AM", String.valueOf(multiplier));
                            }
                            else if (unit.equals("s")) {
                                multiplier = controlTime * 1000;
                                minuteMultiplier = 1/60.0;
                            }

                            Intent controltimeIntent = new Intent(getContext(), ControlTimeReceiver.class);
                            controltimeIntent.putExtra("notificationId", notificationID);
                            controltimeIntent.putExtra("eventId", events[selectedItemId].getId());
                            controltimeIntent.putExtra("controlTime", (int)(controlTime * minuteMultiplier));


                            int uniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), uniqueId, controltimeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + multiplier, pendingIntent);
                        }
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
}