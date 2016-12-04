package com.example.madiskar.experiencesamplingapp;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Joosep on 20.11.2016.
 */

public class EventFragment extends ListFragment {

    private static ActiveEventListAdapter aela;
    private TextView noEventsTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<Event> events = EventDialogFragment.activeEvents;
        aela = new ActiveEventListAdapter(getActivity(), events, EventFragment.this);
        setListAdapter(aela);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_event, container, false);
        noEventsTxt = (TextView) view.findViewById(R.id.no_events);
        noEventsTxt.setVisibility(View.GONE);

        if (EventDialogFragment.activeEvents.size() == 0)
           noEvents();

        return view;
    }

    public static void removeEvent(long eventId) {
        for (Event event: new ArrayList<Event>(EventDialogFragment.activeEvents)) {
            if (event.getId() == eventId) {
                EventDialogFragment.activeEvents.remove(event);
            }
        }
        try {
            aela.updateEvents();
        } catch (Exception e) {}
    }

    public void noEvents() {
        noEventsTxt.setVisibility(View.VISIBLE);
    }
}
