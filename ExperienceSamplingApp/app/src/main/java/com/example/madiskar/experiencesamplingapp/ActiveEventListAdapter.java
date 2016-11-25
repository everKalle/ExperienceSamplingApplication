package com.example.madiskar.experiencesamplingapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Joosep on 12.11.2016.
 */

public class ActiveEventListAdapter extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<Event> events;

    public ActiveEventListAdapter(Activity activity, ArrayList<Event> events) {
        this.mActivity = activity;
        this.events = events;
    }
    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return events.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activeevent_item, null);
        }

        TextView nameView = (TextView) view.findViewById(R.id.active_event);
        final TextView timeView = (TextView) view.findViewById(R.id.event_duration);
        Button stopButton = (Button) view.findViewById(R.id.stop_button);

        nameView.setText(events.get(position).getName());


        Thread t = new Thread() {

            long hours = 0;
            long minutes = 0;
            long seconds = 0;

            @Override
            public void run() {
                try {

                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (EventDialogFragment.activeEvents.size() > 0) {
                                        Event event = events.get(position);
                                        long newTime = (Calendar.getInstance().getTimeInMillis() - event.getStartTimeInMillis());
                                        hours = newTime / (60 * 60 * 1000);
                                        minutes = newTime / (60 * 1000) - 60 * hours;
                                        seconds = newTime / 1000 - 60 * minutes;
                                        timeView.setText(hours + " : " + minutes + " : " + seconds);
                                    }
                                } catch (Exception e) {}
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stopIntent = new Intent(mActivity, StopReceiver.class);
                stopIntent.putExtra("start", events.get(position).getStartTimeCalendar());
                stopIntent.putExtra("notificationId", EventDialogFragment.uniqueValueMap.get((int) events.get(position).getId()));
                stopIntent.putExtra("studyId", events.get(position).getStudyId());
                stopIntent.putExtra("controlNotificationId", EventDialogFragment.uniqueControlValueMap.get((int) events.get(position).getId()));
                stopIntent.putExtra("eventId", events.get(position).getId());
                mActivity.sendBroadcast(stopIntent);
            }
        });

    return view;
    }

    public void updateEvents() {
        notifyDataSetChanged();
    }

}
