package com.example.madiskar.experiencesamplingapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Joosep on 28.09.2016.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        ArrayList<Study> studies = DBHandler.getInstance(context).getAllStudies();

        for(Study s : studies) {
            ResponseReceiver rR = new ResponseReceiver(s);
            rR.setupAlarm(context, true);
        }
    }
}
