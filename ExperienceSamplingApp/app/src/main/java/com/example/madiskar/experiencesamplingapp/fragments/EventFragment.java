package com.example.madiskar.experiencesamplingapp.fragments;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.madiskar.experiencesamplingapp.data_types.Event;
import com.example.madiskar.experiencesamplingapp.R;
import com.example.madiskar.experiencesamplingapp.data_types.Study;
import com.example.madiskar.experiencesamplingapp.interfaces.OnEventTimeTableChanged;
import com.example.madiskar.experiencesamplingapp.list_adapters.ActiveEventListAdapter;
import com.example.madiskar.experiencesamplingapp.local_database.DBHandler;

import java.util.ArrayList;
import java.util.Calendar;


public class EventFragment extends ListFragment {

    private TextView noEventsTxt;
    private DBHandler db;
    private ActiveEventListAdapter aela;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_event, container, false);

        noEventsTxt = (TextView) view.findViewById(R.id.no_events);
        noEventsTxt.setVisibility(View.GONE);

        new AsyncTask<Void, Void, ArrayList<Event>>() {

            @Override
            public ArrayList<Event> doInBackground(Void... params) {
                db = DBHandler.getInstance(getActivity());
                ArrayList<Event> events = new ArrayList<>();
                ArrayList<Study> studies = db.getAllStudies();
                for (Study s : studies) {
                    for (Event event: s.getEvents()) {
                        Calendar startTime = db.getEventStartTime(event.getId());
                        if (startTime != null) {
                            events.add(event);
                        }
                    }
                }
                return events;
            }

            @Override
            public void onPostExecute(ArrayList<Event> results) {
                aela = new ActiveEventListAdapter(getActivity(), results);
                setListAdapter(aela);
                if(results.size() == 0)
                    noEvents();


                db.setOnEventTimeTableChangedListener(new OnEventTimeTableChanged() {
                    @Override
                    public void onTableChanged() {
                        ArrayList<Event> events = new ArrayList<>();
                        ArrayList<Study> studies = db.getAllStudies();
                        for (Study s : studies) {
                            for (Event event: s.getEvents()) {
                                Calendar startTime = db.getEventStartTime(event.getId());
                                if (startTime != null) {
                                    events.add(event);
                                }
                            }
                        }
                        if(events.size() == 0) {
                            if(aela.getCount() != 0)
                                aela.updateEvents(events);
                            ((TextView) view.findViewById(R.id.no_events)).setVisibility(View.VISIBLE);
                        } else {
                            ((TextView) view.findViewById(R.id.no_events)).setVisibility(View.GONE);
                            if(aela.getCount() != events.size())
                                aela.updateEvents(events);
                        }
                    }
                });
            }
        }.execute();

        return view;
    }

    public void noEvents() {
        noEventsTxt.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        db.setOnEventTimeTableChangedListener(null);
        super.onDestroyView();
    }
}
