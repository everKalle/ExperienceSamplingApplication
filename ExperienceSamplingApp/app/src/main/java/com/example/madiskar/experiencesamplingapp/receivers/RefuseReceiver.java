package com.example.madiskar.experiencesamplingapp.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.madiskar.experiencesamplingapp.background_tasks.ExecutorSupplier;
import com.example.madiskar.experiencesamplingapp.background_tasks.SaveAnswersTask;
import com.example.madiskar.experiencesamplingapp.interfaces.RunnableResponse;
import com.example.madiskar.experiencesamplingapp.local_database.DBHandler;


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
            }
        });
        ExecutorSupplier.getInstance().forBackgroundTasks().execute(saveAnswersTask);
    }


}
