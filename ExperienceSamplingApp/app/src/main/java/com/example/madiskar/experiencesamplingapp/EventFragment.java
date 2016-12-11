package com.example.madiskar.experiencesamplingapp;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Calendar;


public class EventFragment extends ListFragment {

    private static ActiveEventListAdapter aela;
    private TextView noEventsTxt;
    ArrayList<Study> studies;
    private static ArrayList<Event> events;
    private DBHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DBHandler.getInstance(getActivity());
        events = new ArrayList<>();
        studies = db.getAllStudies();
        for (Study s : studies) {
            for (Event event: s.getEvents()) {
                Calendar startTime = db.getEventStartTime(event.getId());
                if (startTime != null) {
                    events.add(event);
                }
            }
        }
        aela = new ActiveEventListAdapter(getActivity(), events, EventFragment.this);
        setListAdapter(aela);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_event, container, false);

        noEventsTxt = (TextView) view.findViewById(R.id.no_events);
        noEventsTxt.setVisibility(View.GONE);

        boolean anyEvents = false;

        for (Study s: studies) {
            for (Event event: s.getEvents()) {
                Calendar startTime = db.getEventStartTime(event.getId());
                if (startTime != null) {
                    anyEvents = true;
                }
            }
        }

        if (!anyEvents)
           noEvents();

        return view;
    }

    public static void removeEvent(long eventId, Context context) {
        DBHandler.getInstance(context).deleteEventTimeEntry(eventId);
        try {
            for (Event event : events) {
                if (event.getId() == eventId) {
                    events.remove(event);
                    break;
                }
            }
        } catch (Exception e) {}
        try {
            aela.updateEvents();
        } catch (Exception e) {}
    }

    public void noEvents() {
        noEventsTxt.setVisibility(View.VISIBLE);
    }
}
