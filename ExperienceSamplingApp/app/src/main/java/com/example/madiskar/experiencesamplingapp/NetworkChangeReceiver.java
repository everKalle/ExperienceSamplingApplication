package com.example.madiskar.experiencesamplingapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.regex.Pattern;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private String token;
    private DBHandler mydb;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("NETWORK STATE CHANGED", "CHECK IF CONNECTION IS AVAILABLE");

        mydb = DBHandler.getInstance(context);

        SharedPreferences spref = context.getApplicationContext().getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);
        token = spref.getString("token", "none");

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Log.i("NETWORK STATE CHANGED", "TRY TO SYNC DATA");

            SyncAllDataTask syncAllDataTask = new SyncAllDataTask(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    //String[] parts = output.split(Pattern.quote(","));
                    Log.i("LOG SYNC OUTPUT", output);
                    //TODO: Sync study object data also
                }
            }, true, mydb);

            syncAllDataTask.execute(token);

        } else {
            Log.i("NETWORK STATE CHANGED", "NO CONNECTION AVAILABLE");
        }
    }
}
