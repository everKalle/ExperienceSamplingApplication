package com.example.madiskar.experiencesamplingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Joosep on 12.11.2016.
 */

public class SearchResultsListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Study> studies;

    public SearchResultsListAdapter(Context context, ArrayList<Study> studies) {
        this.mContext = context;
        this.studies = studies;
    }

    @Override
    public int getCount() {
        return studies.size();
    }

    @Override
    public Object getItem(int position) {
        return studies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return studies.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.search_item, null);
        }

        TextView nameView = (TextView) view.findViewById(R.id.study_name);
        TextView durationView = (TextView) view.findViewById(R.id.study_duration);

        nameView.setText(studies.get(position).getName());
        String duration = DBHandler.calendarToString(studies.get(position).getBeginDate()) + " - " + DBHandler.calendarToString(this.studies.get(position).getEndDate());
        durationView.setText(duration);

        Button joinBtn = (Button) view.findViewById(R.id.join_button);

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO -- implement join functionality
            }
        });

        return view;
    }
}