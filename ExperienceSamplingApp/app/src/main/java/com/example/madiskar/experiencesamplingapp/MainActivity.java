package com.example.madiskar.experiencesamplingapp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteReadOnlyDatabaseException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.preference.PreferenceManager;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements BeepfreePeriodPickerFragment.BeepFreePeriodListener{

    private static int periodID = 0;

    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    ArrayList<BeepFerePeriod> bfpArrayList = new ArrayList<BeepFerePeriod>();
    BeepFreePeriodListAdapter adapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    ArrayList<MenuItem> mMenuItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences spref = getApplicationContext().getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);
        String username = spref.getString("username", "none");
        TextView usernameField = (TextView) findViewById(R.id.userName_email);
        usernameField.setText(username);


        mMenuItems.add(new MenuItem(getString(R.string.studies), getString(R.string.viewstudies), R.drawable.ic_study));
        mMenuItems.add(new MenuItem(getString(R.string.join), getString(R.string.browsestudies), R.drawable.ic_add));
        mMenuItems.add(new MenuItem(getString(R.string.events), getString(R.string.activeeents), R.drawable.ic_events));
        mMenuItems.add(new MenuItem(getString(R.string.action_settings), getString(R.string.changesettings), R.drawable.ic_settings));
        mMenuItems.add(new MenuItem(getString(R.string.logout), getString(R.string.logcurrent), R.drawable.ic_logout));
        mMenuItems.add(new MenuItem(getString(R.string.exit), getString(R.string.runbackground), R.drawable.ic_exit));

        BeepFerePeriod bfp = new BeepFerePeriod(0,3,30,5,30);
        bfpArrayList.add(bfp);
        NotificationService.addBeepFreePeriod(bfp);
        periodID = 1;
        // DrawerLayout

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the drawer menu with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.menuList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mMenuItems);
        mDrawerList.setAdapter(adapter);

        // Drawer menu click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MenuItem menuItem = mMenuItems.get(position);

                loadFragment(menuItem.mSubtitle, false);
                selectItemFromDrawer(position);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //Log.d(TAG, "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);


        setTitle("My Studies");
        loadFragment("My Studies", false);


        //TODO: Check for problems in dbhandler methods

        //TODO: got error when logging out ~ Madis

        //TODO: look over db querys and maybe use asynctask with the bigger ones


    }


    /*
    * Called when a particular item from the navigation drawer
    * is selected.
    * */
    private void selectItemFromDrawer(int position) {
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerPane);
        String itemName = mMenuItems.get(position).mTitle;
        loadFragment(itemName, true);
    }

    private void loadFragment(String itemName, boolean from_menu) {
        FragmentManager fragmentManager = getFragmentManager();
        if(itemName.equals("My Studies")) {
        	setTitle(itemName);
            Fragment fragment = new StudyFragment();
            Bundle args = new Bundle();
            args.putBoolean("fromNav", from_menu);
            fragment.setArguments(args);
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContent, fragment)
                    .commit();
        }
        else if (itemName.equals("Beepfree period")) {
            Fragment fragment = new BeepFreeFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContent, fragment)
                    .commit();

        }
	    else if (itemName.equals("Settings")) {
       	    getFragmentManager().beginTransaction()
                    .replace(R.id.mainContent, new SettingsFragment())
                    .commit();
        }
        else if (itemName.equals("Log Out")) {
            DBHandler mydb = DBHandler.getInstance(getApplicationContext());
            final ArrayList<Study> studylist = mydb.getAllStudies();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            boolean anyEvents = false;
            for (Study s: studylist) {
                if (EventDialogFragment.studyToNotificationIdMap.get((int)s.getId()) == null || EventDialogFragment.studyToNotificationIdMap.get((int)s.getId()).size() < 1) {}
                else
                    anyEvents = true;
            }

            if (anyEvents) {
                alertDialogBuilder.setMessage("You have some active events which will be discarded. Are you sure you want to log out?");
                alertDialogBuilder.setNegativeButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt("LoggedIn", 0);
                                editor.putString("username", "none");
                                editor.putString("token", "none");
                                editor.apply();
                                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                DBHandler.getInstance(getApplicationContext()).clearTables();
                                try {
                                    for (Study s : studylist) {
                                        //Log.v("OPSTI", "olen siin");
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) s.getId(), new Intent(getApplicationContext(), ResponseReceiver.class), 0);
                                        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                        am.cancel(pendingIntent);
                                    }
                                    for (Study s : studylist) {
                                        NotificationService.cancelNotification(getApplicationContext(), (int) s.getId());
                                    }
                                } catch (Exception e) {
                                    //Log.v("OPSTI2", "olen siin");
                                    e.printStackTrace();
                                }

                                try {
                                    for (Study s : studylist) {
                                        EventDialogFragment.cancelEvents(getApplicationContext(), (int) s.getId());
                                    }
                                } catch (Exception e) {
                                    Log.v("OPSTI2", "olen siin");
                                    e.printStackTrace();
                                }
                                try {
                                    Intent intent = new Intent(getBaseContext(), QuestionnaireActivity.class);
                                    for (Study s : studylist) {
                                        ResponseReceiver.cancelExistingAlarm(getBaseContext(), intent, Integer.valueOf((s.getId()+1) + "00002"), false);
                                    }
                                } catch (Exception e) {
                                    Log.v("OPSTI2", "olen siin");
                                    e.printStackTrace();
                                }
                                EventDialogFragment.studyToNotificationIdMap.clear();
                                dialog.dismiss();
                                startActivity(i);
                                finish();
                            }
                        });
                alertDialogBuilder.setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
            else {
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("LoggedIn", 0);
                editor.putString("username", "none");
                editor.putString("token", "none");
                editor.apply();
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                DBHandler.getInstance(getApplicationContext()).clearTables();
                try {
                    for (Study s : studylist) {
                        //Log.v("OPSTI", "olen siin");
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) s.getId(), new Intent(getApplicationContext(), ResponseReceiver.class), 0);
                        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                        am.cancel(pendingIntent);
                    }
                    for (Study s : studylist) {
                        NotificationService.cancelNotification(this, (int) s.getId());
                    }
                } catch (Exception e) {
                    //Log.v("OPSTI2", "olen siin");
                    e.printStackTrace();
                }

                try {
                    for (Study s : studylist) {
                        EventDialogFragment.cancelEvents(this, (int) s.getId());
                    }
                } catch (Exception e) {
                    Log.v("OPSTI2", "olen siin");
                    e.printStackTrace();
                }
                startActivity(i);
                finish();
            }
        }
        else if (itemName.equals("Exit")) {
            finish();
        }
        else if(itemName.equals("Join Studies")) {
            //TODO: launch study join activity here
        }
        else if(itemName.equals("My Events")) {
            //TODO: launch activity where one can see active events and press stop
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (id == R.id.menu_settings) {
            setUpBeepFreePeriods(getWindow().getDecorView().getRootView());
        }

        return super.onOptionsItemSelected(item);
    }

    public void setUpBeepFreePeriods(View v) {
        adapter = new BeepFreePeriodListAdapter(this, bfpArrayList);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set beepfree periods");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

            }
        });
        FragmentActivity activity = (FragmentActivity)(this);
        final android.support.v4.app.FragmentManager fm = activity.getSupportFragmentManager();
        builder.setPositiveButton("Add new",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BeepfreePeriodPickerFragment dialogFragment = new BeepfreePeriodPickerFragment();
                        Bundle b = new Bundle();
                        ArrayList<Integer> existingStartHours = new ArrayList<Integer>();
                        ArrayList<Integer> existingStartMinutes = new ArrayList<Integer>();
                        ArrayList<Integer> existingEndHours = new ArrayList<Integer>();
                        ArrayList<Integer> existingEndMinutes = new ArrayList<Integer>();
                        for (BeepFerePeriod bfp: NotificationService.beepFreePeriods) {
                            existingStartHours.add(bfp.getStartTimeHour());
                        }
                        for (BeepFerePeriod bfp: NotificationService.beepFreePeriods) {
                            existingStartMinutes.add(bfp.getStartTimeMinute());
                        }
                        for (BeepFerePeriod bfp: NotificationService.beepFreePeriods) {
                            existingEndHours.add(bfp.getEndTimeHour());
                        }
                        for (BeepFerePeriod bfp: NotificationService.beepFreePeriods) {
                            existingEndMinutes.add(bfp.getEndTimeMinute());
                        }
                        b.putIntegerArrayList("existingStartHours", existingStartHours);
                        b.putIntegerArrayList("existingStartMinutes", existingStartMinutes);
                        b.putIntegerArrayList("existingEndHours", existingEndHours);
                        b.putIntegerArrayList("existingEndMinutes", existingEndMinutes);
                        b.putBoolean("new", true);
                        b.putInt("identificator", periodID);
                        dialogFragment.setArguments(b);
                        dialogFragment.show(fm, "timePicker");
                        //periods.add(dialogFragment.getCreatedBeepFreePeriod());
                        //adapter.updateAdapter(periods);
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();





        //DialogFragment newFragment = new BeepfreePeriodPickerFragment();
        //newFragment.
        // newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onDialogPositiveClick(BeepfreePeriodPickerFragment dialog) {
        BeepFerePeriod bfp = dialog.getCreatedBeepFreePeriod();
        bfpArrayList.add(bfp);
        NotificationService.addBeepFreePeriod(bfp);
        adapter.updateAdapter(bfpArrayList);
        periodID += 1;
    }

    @Override
    public void onDialogNegativeClick(BeepfreePeriodPickerFragment dialog) {
    }

    public static void removeItem(int position) {
        periodID -= 1;
        NotificationService.removeBeepFreePeriod(position);
    }

    @Override
    public void onDialogUpdateObject(BeepfreePeriodPickerFragment dialog) {
        BeepFerePeriod bfp = dialog.getEditedBeepFreePeriod();
        adapter.indexBasedUpdateAdapter(bfp.getId(), bfp);
        NotificationService.modifyBeepFreePeriod(bfp.getId(), bfp);
    }
}
