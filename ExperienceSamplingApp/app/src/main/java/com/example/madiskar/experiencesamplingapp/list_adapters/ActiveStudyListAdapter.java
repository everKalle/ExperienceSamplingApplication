package com.example.madiskar.experiencesamplingapp.list_adapters;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.madiskar.experiencesamplingapp.local_database.DBHandler;
import com.example.madiskar.experiencesamplingapp.data_types.Event;
import com.example.madiskar.experiencesamplingapp.fragments.EventDialogFragment;
import com.example.madiskar.experiencesamplingapp.background_tasks.ExecutorSupplier;
import com.example.madiskar.experiencesamplingapp.services.NotificationService;
import com.example.madiskar.experiencesamplingapp.activities.QuestionnaireActivity;
import com.example.madiskar.experiencesamplingapp.R;
import com.example.madiskar.experiencesamplingapp.receivers.ResponseReceiver;
import com.example.madiskar.experiencesamplingapp.interfaces.RunnableResponse;
import com.example.madiskar.experiencesamplingapp.data_types.Study;
import com.example.madiskar.experiencesamplingapp.fragments.StudyFragment;
import com.example.madiskar.experiencesamplingapp.background_tasks.LeaveStudyTask;

import java.util.ArrayList;
import java.util.Calendar;


public class ActiveStudyListAdapter extends BaseAdapter  {
    private Context mContext;
    private ArrayList<Study> studies;
    private String token;

    public ActiveStudyListAdapter(Context context, ArrayList<Study> studies) {
        this.mContext = context;
        this.studies = studies;
        SharedPreferences spref = mContext.getApplicationContext().getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);
        token = spref.getString("token", "none");
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


        nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setTitle(studies.get(position).getName());
                String[] beepfree = studies.get(position).getDefaultBeepFree().getPeriodAsString().split(" ");
                String[] startSplit = beepfree[0].split(":");
                String[] endSplit = beepfree[1].split(":");
                if(startSplit[1].equals("0")) {
                    startSplit[1] = "00";
                } if(endSplit[1].equals("0")) {
                    endSplit[1] = "00";
                } if(startSplit[0].equals("0")) {
                    startSplit[0] = "00";
                } if(endSplit[0].equals("0")) {
                    endSplit[0] = "00";
                }
                if(studies.get(position).isPublic()) {
                    alertDialogBuilder.setMessage(mContext.getString(R.string.public_study) + "\n\n" + mContext.getString(R.string.study_active_hours) + " "
                            + endSplit[0] + ":" + endSplit[1] + ":" + endSplit[2] + " - " + startSplit[0] + ":" + startSplit[1] + ":" + startSplit[2] + "\n\n" +
                            "Postpone time is " + studies.get(position).getPostponeTime() + "\n\n" +
                            "Minimum time between notifications is " + studies.get(position).getMinTimeBetweenNotifications() + "\n\n" +
                            "Postpone allowed: " + String.valueOf(studies.get(position).getPostponable())  + "\n\n" +
                            "Maximum number of notifications per day: " + studies.get(position).getNotificationsPerDay());
                } else {
                    alertDialogBuilder.setMessage(mContext.getString(R.string.private_study) + "\n\n" + mContext.getString(R.string.study_active_hours) + " "
                            + endSplit[0] + ":" + endSplit[1] + ":" + endSplit[2] + " - " + startSplit[0] + ":" + startSplit[1] + ":" + startSplit[2] + "\n\n" +
                            "Postpone time is " + studies.get(position).getPostponeTime() + "\n\n" +
                            "Minimum time between notifications is " + studies.get(position).getMinTimeBetweenNotifications() + "\n\n" +
                            "Postpone allowed: " + String.valueOf(studies.get(position).getPostponable())  + "\n\n" +
                            "Maximum number of notifications per day: " + studies.get(position).getNotificationsPerDay());
                }
                alertDialogBuilder.setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        eventBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Study study = (Study) getItem(position);
                if (!Calendar.getInstance().before(study.getBeginDate())) {
                    FragmentActivity activity = (FragmentActivity) (mContext);
                    final android.support.v4.app.FragmentManager fm = activity.getSupportFragmentManager();
                    EventDialogFragment edf = new EventDialogFragment();
                    Bundle b = new Bundle();
                    b.putParcelableArray("EVENTS", study.getEvents());
                    b.putLong("studyId", study.getId());
                    edf.setArguments(b);
                    edf.show(fm, "eventChooser");
                }
                else
                    Toast.makeText(mContext, "This study is not active yet!", Toast.LENGTH_SHORT).show();
            }
        });

        quitBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // quit study here //
                if(isNetworkAvailable()) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                    final Study studyRef = studies.get(position);

                    SharedPreferences sp = mContext.getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt(String.valueOf(studyRef.getId()), 0);
                    editor.apply();
                    boolean anyEvents = false;
                    for (Event event: studyRef.getEvents()) {
                        if (DBHandler.getInstance(mContext).getEventStartTime(event.getId()) != null)
                            anyEvents = true;
                    }
                    if (!anyEvents)
                        alertDialogBuilder.setMessage(mContext.getString(R.string.quit_study) +" \"" + studyRef.getName() + "\"?");
                    else
                        alertDialogBuilder.setMessage(mContext.getString(R.string.quit_event) + "\n \"" + studyRef.getName() + "\"?");
                    alertDialogBuilder.setNegativeButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, (int) studyRef.getId(), new Intent(mContext, ResponseReceiver.class), 0);
                                    AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                                    am.cancel(pendingIntent);

                                    try {
                                        NotificationService.cancelNotification(mContext, (int) studyRef.getId());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        EventDialogFragment.cancelEvents(mContext, (int) studyRef.getId());
                                    } catch (Exception e) {
                                    }
                                    try {
                                        Intent intent = new Intent(mContext, QuestionnaireActivity.class);
                                        ResponseReceiver.cancelExistingAlarm(mContext, intent, Integer.valueOf((studyRef.getId() + 1) + "00002"), false);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    final Long study_id = studyRef.getId();
                                    LeaveStudyTask leaveStudyTask = new LeaveStudyTask(token, Long.toString(study_id), DBHandler.getInstance(mContext), new RunnableResponse() {
                                        @Override
                                        public void processFinish(String output) {
                                            if(output.equals("success")) {
                                                DBHandler.getInstance(mContext).deleteStudyEntry(study_id);
                                            }
                                        }
                                    });
                                    ExecutorSupplier.getInstance().forBackgroundTasks().execute(leaveStudyTask);

                                    studies.remove(position);
                                    Toast.makeText(mContext, mContext.getString(R.string.study_left), Toast.LENGTH_SHORT).show();
                                    notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            });
                    alertDialogBuilder.setPositiveButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.no_network), Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }


    public void updateStudies(ArrayList<Study> studies) {
        this.studies = studies;
        notifyDataSetChanged();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}