package com.example.madiskar.experiencesamplingapp.list_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.madiskar.experiencesamplingapp.R;
import com.example.madiskar.experiencesamplingapp.data_types.MenuItem;

import java.util.ArrayList;

public class DrawerListAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<MenuItem> mMenuItems;

    public DrawerListAdapter(Context context, ArrayList<MenuItem> menuItems) {
        mContext = context;
        mMenuItems = menuItems;
    }

    @Override
    public int getCount() {
        return mMenuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mMenuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_item, null);
        }
        else {
            view = convertView;
        }

        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);

        titleView.setText(mMenuItems.get(position).mTitle);
        subtitleView.setText(mMenuItems.get(position).mSubtitle);
        iconView.setImageResource(mMenuItems.get(position).mIcon);

        return view;
    }
}