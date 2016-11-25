package com.example.madiskar.experiencesamplingapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private String token;
    private DBHandler mydb;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i("NETWORK STATE CHANGED", "CHECK IF CONNECTION IS AVAILABLE");

        mydb = DBHandler.getInstance(context);

        SharedPreferences spref = context.getApplicationContext().getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);
        token = spref.getString("token", "none");

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Log.i("NETWORK STATE CHANGED", "TRY TO SYNC DATA");

            SyncResultDataTask syncResultDataTask = new SyncResultDataTask(true, mydb, token, new RunnableResponse() {
                @Override
                public void processFinish(String output) {
                    Log.i("UPLOADED DATA:", output);
                    Log.i("STARTING SYNC:", "Study info");
                    SyncStudyDataTask syncStudyDataTask = new SyncStudyDataTask(token, mydb, new StudyDataSyncResponse() {
                        @Override
                        public void processFinish(String output, ArrayList<Study> newStudies) {
                            if(output.equals("invalid_token")) {
                                Toast.makeText(context.getApplicationContext(), R.string.auth_sync_fail, Toast.LENGTH_LONG).show();
                            } else if(output.equals("nothing")) {
                                Toast.makeText(context.getApplicationContext(), R.string.fetch_sync_fail, Toast.LENGTH_LONG).show();
                            } else {
                                Log.i("FINISHED SYNC:", "Study info");
                                for(Study s : newStudies) {
                                    Log.i("NetworkChangeReceiver", "Setting up alarms for " + newStudies.size() + " studies");
                                    ResponseReceiver rR = new ResponseReceiver(s);
                                    rR.setupAlarm(context.getApplicationContext(), true);
                                }
                            }
                        }
                    });
                    ExecutorSupplier.getInstance().forBackgroundTasks().execute(syncStudyDataTask);
                }
            });

            ExecutorSupplier.getInstance().forBackgroundTasks().execute(syncResultDataTask);


        } else {
            Log.i("NETWORK STATE CHANGED", "NO CONNECTION AVAILABLE");
        }
    }
}
