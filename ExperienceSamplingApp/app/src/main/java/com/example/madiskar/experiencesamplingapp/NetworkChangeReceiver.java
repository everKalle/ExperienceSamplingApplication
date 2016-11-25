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


                    SyncStudyDataTask syncStudyDataTask = new SyncStudyDataTask(token, mydb, true, new StudyDataSyncResponse() {
                        @Override
                        public void processFinish(String output, ArrayList<Study> newStudies, ArrayList<Study> allStudies) {
                            if(output.equals("invalid_token")) {
                                Log.i("FINISHED SYNC:", context.getApplicationContext().getString(R.string.auth_sync_fail));
                            } else if(output.equals("nothing")) {
                                Log.i("FINISHED SYNC:", context.getApplicationContext().getString(R.string.fetch_sync_fail));
                            } else {
                                Log.i("FINISHED SYNC:", "Study info: success");
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
