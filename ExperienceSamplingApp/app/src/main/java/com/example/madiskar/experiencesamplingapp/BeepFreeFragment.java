package com.example.madiskar.experiencesamplingapp;

import android.app.ListFragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class BeepFreeFragment extends ListFragment {

    private Handler mHandler = new Handler();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BeepFreePeriodListAdapter bfpl = new BeepFreePeriodListAdapter(getActivity(), new ArrayList<BeepFerePeriod>());
        setListAdapter(bfpl);

    }



}
