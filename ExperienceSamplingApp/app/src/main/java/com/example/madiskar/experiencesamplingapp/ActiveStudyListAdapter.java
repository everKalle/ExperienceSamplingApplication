package com.example.madiskar.experiencesamplingapp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by madiskar on 26/09/2016.
 */
public class ActiveStudyListAdapter extends BaseAdapter  {
    private Context mContext;
    private ArrayList<Study> studies;

    public ActiveStudyListAdapter(Context context, ArrayList<Study> studies) {
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
                FragmentActivity activity = (FragmentActivity)(mContext);
                final android.support.v4.app.FragmentManager fm = activity.getSupportFragmentManager();
                EventDialogFragment edf = new EventDialogFragment();
                Study study = (Study) getItem(position);
                Bundle b = new Bundle();
                b.putParcelableArray("EVENTS", study.getEvents());
                b.putLong("studyId", study.getId());
                edf.setArguments(b);
                edf.show(fm, "eventChooser");
            }
        });

        quitBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // quit study here //
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                //alertDialog.setCanceledOnTouchOutside(false);
                Study studyRef = (Study) getItem(position);
                if (EventDialogFragment.studyToNotificationIdMap.get((int)studyRef.getId()) == null || EventDialogFragment.studyToNotificationIdMap.get((int)studyRef.getId()).size() < 1 )
                    alertDialogBuilder.setMessage("Are you sure you want to quit \"" + ((Study)getItem(position)).getName() + "\"?");
                else
                    alertDialogBuilder.setMessage("You have an active event which will be discarded. Are you sure you want to quit\n \"" + ((Study)getItem(position)).getName() + "\"?");
                alertDialogBuilder.setNegativeButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, (int) studies.get(position).getId(), new Intent(mContext, ResponseReceiver.class), 0);
                                AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                                am.cancel(pendingIntent);

                                try {
                                    NotificationService.cancelNotification(mContext, (int) studies.get(position).getId());
                                } catch (Exception e) {
                                }
                                try {
                                    EventDialogFragment.cancelEvents(mContext, (int) studies.get(position).getId());
                                } catch (Exception e) {
                                }
                                try {
                                    Intent intent = new Intent(mContext, QuestionnaireActivity.class);
                                    ResponseReceiver.cancelExistingAlarm(mContext, intent, Integer.valueOf((studies.get(position).getId()+1) + "00002"), false);
                                    Log.v("midagi juhtus", "kden");
                                }catch (Exception e) {
                                }
                                studies.remove(position);
                                DBHandler.getInstance(mContext).deleteStudyEntry(position);
                                Toast.makeText(mContext, "Study left", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                                dialog.dismiss();

                            }
                        });
                alertDialogBuilder.setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        return view;
    }


}
