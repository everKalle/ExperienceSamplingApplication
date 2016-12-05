package com.example.madiskar.experiencesamplingapp;

import android.app.ListFragment;
import android.os.Bundle;

import java.util.ArrayList;

public class BeepFreeFragment extends ListFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BeepFreePeriodListAdapter bfpl = new BeepFreePeriodListAdapter(getActivity(), new ArrayList<BeepFerePeriod>());
        setListAdapter(bfpl);

    }



}
