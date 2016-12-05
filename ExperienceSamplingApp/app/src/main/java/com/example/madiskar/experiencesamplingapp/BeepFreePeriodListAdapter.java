package com.example.madiskar.experiencesamplingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class BeepFreePeriodListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<BeepFerePeriod> beepFerePeriods;

    public BeepFreePeriodListAdapter(Context mContext, ArrayList<BeepFerePeriod> beepFreePeriods) {
        this.mContext = mContext;
        this.beepFerePeriods = beepFreePeriods;
    }

    @Override
    public int getCount() {
        return beepFerePeriods.size();
    }

    @Override
    public Object getItem(int position) {
        return beepFerePeriods.get(position);
    }

    public void indexBasedUpdateAdapter(int id, BeepFerePeriod bfp) {
        beepFerePeriods.set(id, bfp);
        notifyDataSetChanged();
    }


    public void updateAdapter(ArrayList<BeepFerePeriod> arrylst) {
        this.beepFerePeriods = arrylst;

        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return beepFerePeriods.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.beepfree_period_item, null);
        }

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        DBHandler myDb = DBHandler.getInstance(mContext);
        final ArrayList<BeepFerePeriod> bfps = myDb.getBeepFreePeriods();

        TextView durationView = (TextView) convertView.findViewById(R.id.beepfree_duration);

        String startHour;
        String startMinute;
        String endHour;
        String endMinute;

        if (beepFerePeriods.get(position).getStartTimeHour() <= 9)
            startHour = "0" + beepFerePeriods.get(position).getStartTimeHour();
        else
            startHour = "" + beepFerePeriods.get(position).getStartTimeHour();
        if (beepFerePeriods.get(position).getStartTimeMinute() <= 9)
            startMinute = "0" + beepFerePeriods.get(position).getStartTimeMinute();
        else
            startMinute = "" + beepFerePeriods.get(position).getStartTimeMinute();
        if (beepFerePeriods.get(position).getEndTimeHour() <= 9)
            endHour = "0" + beepFerePeriods.get(position).getEndTimeHour();
        else
            endHour = "" + beepFerePeriods.get(position).getEndTimeHour();
        if (beepFerePeriods.get(position).getEndTimeMinute() <= 9)
            endMinute = "0" + beepFerePeriods.get(position).getEndTimeMinute();
        else
            endMinute = "" + beepFerePeriods.get(position).getEndTimeMinute();

        String duration =  startHour + ":" + startMinute + " - "
                + endHour + ":" + endMinute;
        durationView.setText(duration);


        Button editBtn = (Button) convertView.findViewById(R.id.beepfree_edit);
        Button disableBtn = (Button) convertView.findViewById(R.id.beepfree_disable);

        FragmentActivity activity = (FragmentActivity)(mContext);
        final FragmentManager fm = activity.getSupportFragmentManager();

        editBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new BeepfreePeriodPickerFragment();
                dialogFragment.setTargetFragment(dialogFragment, 1);
                BeepFerePeriod bfp = beepFerePeriods.get(position);
                Bundle b = new Bundle();
                b.putBoolean("new", false);
                b.putInt("id", bfp.getId());
                b.putInt("startHour", bfp.getStartTimeHour());
                b.putInt("startMinute", bfp.getStartTimeMinute());
                b.putInt("endHour", bfp.getEndTimeHour());
                b.putInt("endMinute", bfp.getEndTimeMinute());

                ArrayList<Integer> existingStartHours = new ArrayList<Integer>();
                ArrayList<Integer> existingStartMinutes = new ArrayList<Integer>();
                ArrayList<Integer> existingEndHours = new ArrayList<Integer>();
                ArrayList<Integer> existingEndMinutes = new ArrayList<Integer>();
                for (BeepFerePeriod bfp2: bfps) {
                    existingStartHours.add(bfp2.getStartTimeHour());
                }
                for (BeepFerePeriod bfp2: bfps) {
                    existingStartMinutes.add(bfp2.getStartTimeMinute());
                }
                for (BeepFerePeriod bfp2: bfps) {
                    existingEndHours.add(bfp2.getEndTimeHour());
                }
                for (BeepFerePeriod bfp2: bfps) {
                    existingEndMinutes.add(bfp2.getEndTimeMinute());
                }
                b.putIntegerArrayList("existingStartHours", existingStartHours);
                b.putIntegerArrayList("existingStartMinutes", existingStartMinutes);
                b.putIntegerArrayList("existingEndHours", existingEndHours);
                b.putIntegerArrayList("existingEndMinutes", existingEndMinutes);
                dialogFragment.setArguments(b);
                dialogFragment.show(fm, "timePicker");

            }
        });

        disableBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // quit study here //
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle(R.string.disable_beepfree_period);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                beepFerePeriods.remove(position);

                                for (int i = position; i < beepFerePeriods.size(); i++)
                                    beepFerePeriods.get(i).setId(beepFerePeriods.get(i).getId()-1);
                                notifyDataSetChanged();

                                DBHandler.getInstance(mContext).deleteBeepFreeEntry(position);
                                Toast.makeText(mContext, R.string.beepfree_removed, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, mContext.getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        return convertView;

    }
}
