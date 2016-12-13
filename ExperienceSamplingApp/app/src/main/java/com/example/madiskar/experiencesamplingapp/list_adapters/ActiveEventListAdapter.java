package com.example.madiskar.experiencesamplingapp.list_adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.madiskar.experiencesamplingapp.local_database.DBHandler;
import com.example.madiskar.experiencesamplingapp.data_types.Event;
import com.example.madiskar.experiencesamplingapp.fragments.EventFragment;
import com.example.madiskar.experiencesamplingapp.R;
import com.example.madiskar.experiencesamplingapp.receivers.StopReceiver;
import com.example.madiskar.experiencesamplingapp.data_types.Study;

import java.util.ArrayList;
import java.util.Calendar;


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
        Button stopButton = (Button) view.findViewById(R.id.stop_button);

        nameView.setText(events.get(position).getName());



        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stopIntent = new Intent(mActivity, StopReceiver.class);
                stopIntent.putExtra("start", DBHandler.calendarToString(DBHandler.getInstance(mActivity).getEventStartTime(events.get(position).getId())));
                stopIntent.putExtra("notificationId", ((int)events.get(position).getId())*-1);
                stopIntent.putExtra("studyId", events.get(position).getStudyId());
                stopIntent.putExtra("controlNotificationId", ((int)events.get(position).getId())*-100);
                stopIntent.putExtra("eventId", events.get(position).getId());
                updateEvents(position);
                mActivity.sendBroadcast(stopIntent);
            }
        });

    return view;
    }

    private void updateEvents(final int position) {
        events.remove(position);
        notifyDataSetChanged();
    }

    public void updateEvents(ArrayList<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

}
