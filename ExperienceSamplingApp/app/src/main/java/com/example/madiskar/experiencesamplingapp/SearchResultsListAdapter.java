package com.example.madiskar.experiencesamplingapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchResultsListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Study> studies;
    private Handler mHandler;

    public SearchResultsListAdapter(Context context, ArrayList<Study> studies) {
        this.mContext = context;
        this.studies = studies;
        mHandler = new Handler();
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
                                            DBHandler.getInstance(mContext).insertStudy(studies.get(position));
                                            ResponseReceiver rR = new ResponseReceiver(studies.get(position));
                                            rR.setupAlarm(mContext.getApplicationContext(), true);
                                            updateUI(position);
                                            showSuccessToast();
                                        } else if(output.equals("invalid_study")) {
                                            showFailToast();
                                        } else if(output.equals("invalid_token")) {
                                            showFailToast();
                                        } else if(output.equals("nothing")) {
                                            showFailToast();
                                        } else if(output.equals("exists")) {
                                            showAlreadyFinishedToast();
                                        } else {
                                            showFailToast();
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


    private void updateUI(final int position) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                studies.remove(position);
                notifyDataSetChanged();
            }
        });
    }


    private void showSuccessToast() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, mContext.getApplicationContext().getString(R.string.study_join_success), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showAlreadyFinishedToast() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, mContext.getApplicationContext().getString(R.string.already_finished), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFailToast() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, mContext.getApplicationContext().getString(R.string.study_join_fail), Toast.LENGTH_SHORT).show();
            }
        });
    }
}