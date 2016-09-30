package com.example.madiskar.experiencesamplingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by madiskar on 26/09/2016.
 */
public class ActiveStudyListAdapter extends BaseAdapter  {
    private Context mContext;
    private ArrayList<Study> studies;

    public ActiveStudyListAdapter(Context context, ArrayList<Study> studies) {
        System.out.println("siin");
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activestudy_item, null);
        }

        TextView nameView = (TextView) view.findViewById(R.id.activestudy_name);
        TextView durationView = (TextView) view.findViewById(R.id.activestudy_duration);

        nameView.setText(studies.get(position).getName());
        String duration = DBHandler.calendarToString(studies.get(position).getBeginDate()) + " - " + DBHandler.calendarToString(this.studies.get(position).getEndDate());
        durationView.setText(duration);


        Button eventBtn = (Button) view.findViewById(R.id.event_button);
        Button quitBtn = (Button) view.findViewById(R.id.quit_button);

        eventBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // start event here
                System.out.println("event button clicked");
                notifyDataSetChanged();
            }
        });
        quitBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // quit study here
                System.out.println("quit button clicked");
                notifyDataSetChanged();
            }
        });


        return view;
    }
}
