package com.example.madiskar.experiencesamplingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


public class ActiveEventListAdapter extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<Event> events;
    private EventFragment eventFragment;

    public ActiveEventListAdapter(Activity activity, ArrayList<Event> events, EventFragment eventFragment) {
        this.mActivity = activity;
        this.events = events;
        this.eventFragment = eventFragment;
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
        Button stopButton = (Button) view.findViewById(R.id.stop_button);

        nameView.setText(events.get(position).getName());



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
        if (EventDialogFragment.activeEvents.size() == 0)
            eventFragment.noEvents();
        notifyDataSetChanged();
    }

}
