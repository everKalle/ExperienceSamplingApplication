package com.example.madiskar.experiencesamplingapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
        String duration = DBHandler.calendarToString(studies.get(position).getBeginDate()) + " - \n" + DBHandler.calendarToString(this.studies.get(position).getEndDate());
        durationView.setText(duration);

        Button joinBtn = (Button) view.findViewById(R.id.join_button);

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setMessage("Are you sure you want to join this study?");
                alertDialogBuilder.setNegativeButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final ProgressDialog progressDialog = new ProgressDialog(mContext, R.style.AppTheme_Dark_Dialog);
                                progressDialog.setIndeterminate(true);
                                progressDialog.setMessage("Joining Study, Please Wait...");
                                progressDialog.show();

                                SharedPreferences pref = mContext.getApplicationContext().getApplicationContext().getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);
                                String token = pref.getString("token", "none");
                                JoinStudyTask joinStudyTask = new JoinStudyTask(token, Long.toString(studies.get(position).getId()), new RunnableResponse() {
                                    @Override
                                    public void processFinish(String output) {
                                        progressDialog.dismiss();
                                        if(output.equals("success")) {
                                            Log.i("JOINED STUDY", studies.get(position).getName());
                                            DBHandler.getInstance(mContext).insertStudy(studies.get(position));
                                            ResponseReceiver rR = new ResponseReceiver(studies.get(position));
                                            rR.setupAlarm(mContext.getApplicationContext(), true);
                                            //Toast.makeText(mContext, "Joining successful", Toast.LENGTH_SHORT).show();
                                        } else if(output.equals("invalid_study")) {
                                            Log.i("JOINED STUDY", "INVALID STUDY");
                                            //Toast.makeText(mContext, "This study doesn't exist", Toast.LENGTH_LONG).show();
                                        } else if(output.equals("invalid_token")) {
                                            Log.i("JOINED STUDY", "INVALID TOKEN");
                                            //Toast.makeText(mContext, "Account authentication failed", Toast.LENGTH_LONG).show();
                                        } else if(output.equals("nothing")) {
                                            Log.i("JOINED STUDY", "FAULTY QUERY");
                                            //Toast.makeText(mContext, "Faulty query", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                ExecutorSupplier.getInstance().forBackgroundTasks().execute(joinStudyTask);
                            }
                        });
                alertDialogBuilder.setPositiveButton("NO",
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