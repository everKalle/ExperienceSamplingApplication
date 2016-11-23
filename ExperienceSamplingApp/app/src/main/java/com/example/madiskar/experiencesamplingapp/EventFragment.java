package com.example.madiskar.experiencesamplingapp;

import android.app.ListFragment;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by Joosep on 20.11.2016.
 */

public class EventFragment extends ListFragment {

    private static ActiveEventListAdapter aela;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<Event> events = EventDialogFragment.activeEvents;
        aela = new ActiveEventListAdapter(getActivity(), events);
        setListAdapter(aela);
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
}
