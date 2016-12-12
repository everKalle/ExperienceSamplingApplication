package com.example.madiskar.experiencesamplingapp.fragments;

import android.app.ListFragment;
import android.os.Bundle;

import com.example.madiskar.experiencesamplingapp.data_types.BeepFerePeriod;
import com.example.madiskar.experiencesamplingapp.list_adapters.BeepFreePeriodListAdapter;

import java.util.ArrayList;

public class BeepFreeFragment extends ListFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BeepFreePeriodListAdapter bfpl = new BeepFreePeriodListAdapter(getActivity(), new ArrayList<BeepFerePeriod>());
        setListAdapter(bfpl);

    }



}
