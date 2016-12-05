package com.example.madiskar.experiencesamplingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;


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
