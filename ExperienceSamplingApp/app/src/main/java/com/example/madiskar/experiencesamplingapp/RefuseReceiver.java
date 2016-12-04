package com.example.madiskar.experiencesamplingapp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class RefuseReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("notificationId", 0);
        int studyId = intent.getIntExtra("StudyId", 0);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);


        DBHandler mydb = DBHandler.getInstance(context);

        SharedPreferences spref = context.getApplicationContext().getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);
        String token = spref.getString("token", "none");


        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        SaveAnswersTask saveAnswersTask = new SaveAnswersTask(token, Long.toString(studyId), "user-refused-this-questionnaire",
                (activeNetworkInfo != null && activeNetworkInfo.isConnected()), mydb, new RunnableResponse() {
            @Override
            public void processFinish(String output) {
                if(output.equals("invalid_study")) {
                    Log.i("Answers to server: ", "This study no longer exists");
                } else if(output.equals("invalid_token")) {
                    Log.i("Answers to server: ", "Account authentication failed");
                } else if(output.equals("nothing")) {
                    Log.i("Answers to server: ", "Faulty query");
                } else if(output.equals("success")) {
                    Log.i("Answers to server: ", "Success");
                } else if(output.equals("saved-to-local")) {
                    Log.i("Answers to server: ", "Internet connection unavailable, saving to local storage");
                }
            }
        });
        ExecutorSupplier.getInstance().forBackgroundTasks().execute(saveAnswersTask);
    }


}
