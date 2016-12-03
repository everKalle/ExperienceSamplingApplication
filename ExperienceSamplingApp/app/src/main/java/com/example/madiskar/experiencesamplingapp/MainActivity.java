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

    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    ArrayList<BeepFerePeriod> bfpArrayList = new ArrayList<BeepFerePeriod>();
    BeepFreePeriodListAdapter adapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    public static boolean justStarted = true;

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

        DBHandler mydb = DBHandler.getInstance(getApplicationContext());
        ArrayList<BeepFerePeriod> bfps = mydb.getBeepFreePeriods();
        Log.v("KONTROLL", String.valueOf(bfps.size()));
        adapter = new BeepFreePeriodListAdapter(this, bfps);
        adapter.updateAdapter(bfps);

        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment f = getFragmentManager().findFragmentById(R.id.mainContent);
                if (f instanceof StudyFragment) { // here we listen for changes in backstack and then rename the title accordingly
                    setTitle(f.getTag());
                }
            }
        });


        mMenuItems.add(new MenuItem(getString(R.string.studies), getString(R.string.viewstudies), R.drawable.ic_study));
        mMenuItems.add(new MenuItem(getString(R.string.join), getString(R.string.browsestudies), R.drawable.ic_add));
        mMenuItems.add(new MenuItem(getString(R.string.events), getString(R.string.activeeents), R.drawable.ic_events));
        mMenuItems.add(new MenuItem(getString(R.string.action_settings), getString(R.string.changesettings), R.drawable.ic_settings));
        mMenuItems.add(new MenuItem(getString(R.string.logout), getString(R.string.logcurrent), R.drawable.ic_logout));
        mMenuItems.add(new MenuItem(getString(R.string.exit), getString(R.string.runbackground), R.drawable.ic_exit));

        //BeepFerePeriod bfp = new BeepFerePeriod(0,3,30,5,30);
        //bfpArrayList.add(bfp);
        //DBHandler dbHandler = DBHandler.getInstance(getApplicationContext());
        //dbHandler.insertBeepFreePeriod(bfp);
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


        setTitle(getString(R.string.studies));
        loadFragment(getString(R.string.studies), false);

    }

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
                    .replace(R.id.mainContent, fragment, "My Studies")
                    .addToBackStack("My Studies")
                    .commit();
        }
        else if (itemName.equals("Beepfree period")) {
            Log.v("wotm8", "m9");
            Fragment fragment = new BeepFreeFragment();

            fragmentManager.beginTransaction()
                    .replace(R.id.mainContent, fragment)
                    .commit();
        }
	    else if (itemName.equals("Settings")) {
            setTitle(itemName);
       	    getFragmentManager().beginTransaction()
                    .replace(R.id.mainContent, new SettingsFragment(), "Settings")
                    .addToBackStack(null)
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
                alertDialogBuilder.setMessage(R.string.events_log_out);
                alertDialogBuilder.setNegativeButton(getString(R.string.ok),
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
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) s.getId(), new Intent(getApplicationContext(), ResponseReceiver.class), 0);
                                        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                        am.cancel(pendingIntent);
                                    }
                                    for (Study s : studylist) {
                                        NotificationService.cancelNotification(getApplicationContext(), (int) s.getId());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    for (Study s : studylist) {
                                        EventDialogFragment.cancelEvents(getApplicationContext(), (int) s.getId());
                                    }
                                } catch (Exception e) {
                                }
                                try {
                                    Intent intent = new Intent(getBaseContext(), QuestionnaireActivity.class);
                                    for (Study s : studylist) {
                                        ResponseReceiver.cancelExistingAlarm(getBaseContext(), intent, Integer.valueOf((s.getId()+1) + "00002"), false);
                                    }
                                } catch (Exception e) {
                                }
                                try {
                                    for (Study s : studylist) {
                                        Intent intent = new Intent(getApplicationContext(), QuestionnaireActivity.class);
                                        ResponseReceiver.cancelExistingAlarm(getApplicationContext(), intent, Integer.valueOf((s.getId() + 1) + "00002"), false);
                                    }
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                                EventDialogFragment.studyToNotificationIdMap.clear();
                                dialog.dismiss();
                                startActivity(i);
                                finish();
                            }
                        });
                alertDialogBuilder.setPositiveButton(getString(R.string.cancel),
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
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) s.getId(), new Intent(getApplicationContext(), ResponseReceiver.class), 0);
                        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                        am.cancel(pendingIntent);
                    }
                    for (Study s : studylist) {
                        NotificationService.cancelNotification(this, (int) s.getId());
                    }
                } catch (Exception e) {
                }

                try {
                    for (Study s : studylist) {
                        EventDialogFragment.cancelEvents(this, (int) s.getId());
                    }
                } catch (Exception e) {
                }
                try {
                    for (Study s : studylist) {
                        Intent intent = new Intent(getApplicationContext(), QuestionnaireActivity.class);
                        ResponseReceiver.cancelExistingAlarm(getApplicationContext(), intent, Integer.valueOf((s.getId() + 1) + "00002"), false);
                    }
                }catch (Exception e) {
                }
                startActivity(i);
                finish();
            }
        }
        else if (itemName.equals("Exit")) {
            finish();
        }
        else if(itemName.equals("Join Studies")) {
            setTitle(itemName);
            Fragment fragment = new JoinStudyFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContent, fragment, "Join Studies")
                    .addToBackStack(null)
                    .commit();
        }
        else if(itemName.equals("My Events")) {
            setTitle(itemName);
            Fragment fragment = new EventFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContent, fragment, "My Events")
                    .addToBackStack(null)
                    .commit();
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
        DBHandler myDb = DBHandler.getInstance(getApplicationContext());
        adapter = new BeepFreePeriodListAdapter(this, myDb.getBeepFreePeriods());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.set_beepfrees));
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

            }
        });
        FragmentActivity activity = (FragmentActivity)(this);
        final android.support.v4.app.FragmentManager fm = activity.getSupportFragmentManager();
        builder.setPositiveButton(R.string.add_new,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BeepfreePeriodPickerFragment dialogFragment = new BeepfreePeriodPickerFragment();
                        DBHandler dbHandler = DBHandler.getInstance(getApplicationContext());
                        ArrayList<BeepFerePeriod> beepFerePeriods = dbHandler.getBeepFreePeriods();
                        Bundle b = new Bundle();
                        ArrayList<Integer> existingStartHours = new ArrayList<Integer>();
                        ArrayList<Integer> existingStartMinutes = new ArrayList<Integer>();
                        ArrayList<Integer> existingEndHours = new ArrayList<Integer>();
                        ArrayList<Integer> existingEndMinutes = new ArrayList<Integer>();
                        for (BeepFerePeriod bfp: beepFerePeriods) {
                            existingStartHours.add(bfp.getStartTimeHour());
                        }
                        for (BeepFerePeriod bfp: beepFerePeriods) {
                            existingStartMinutes.add(bfp.getStartTimeMinute());
                        }
                        for (BeepFerePeriod bfp: beepFerePeriods) {
                            existingEndHours.add(bfp.getEndTimeHour());
                        }
                        for (BeepFerePeriod bfp: beepFerePeriods) {
                            existingEndMinutes.add(bfp.getEndTimeMinute());
                        }
                        b.putIntegerArrayList("existingStartHours", existingStartHours);
                        b.putIntegerArrayList("existingStartMinutes", existingStartMinutes);
                        b.putIntegerArrayList("existingEndHours", existingEndHours);
                        b.putIntegerArrayList("existingEndMinutes", existingEndMinutes);
                        b.putBoolean("new", true);
                        int beepFreeId = 0;
                        for (BeepFerePeriod beepFerePeriod: beepFerePeriods) {
                            if (beepFreeId == (int) beepFerePeriod.getId())
                                beepFreeId++;
                        }
                        Log.v("identificatior", String.valueOf(beepFreeId));
                        b.putInt("identificator", beepFreeId);
                        dialogFragment.setArguments(b);
                        dialogFragment.show(fm, "timePicker");
                        //periods.add(dialogFragment.getCreatedBeepFreePeriod());
                        //adapter.updateAdapter(periods);
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(R.string.cancel,
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
        //bfpArrayList.add(bfp);
        DBHandler mydb = DBHandler.getInstance(getApplicationContext());
        mydb.insertBeepFreePeriod(bfp);
        Log.v("MUUTUS", String.valueOf(mydb.getBeepFreePeriods().size()));
        //NotificationService.addBeepFreePeriod(bfp);
        adapter.updateAdapter(mydb.getBeepFreePeriods());
    }

    @Override
    public void onDialogNegativeClick(BeepfreePeriodPickerFragment dialog) {
    }

    public static void removeItem(int position) {
        //NotificationService.removeBeepFreePeriod(position);
    }

    @Override
    public void onDialogUpdateObject(BeepfreePeriodPickerFragment dialog) {
        BeepFerePeriod bfp = dialog.getEditedBeepFreePeriod();
        DBHandler mydb = DBHandler.getInstance(getApplicationContext());
        mydb.editBeepFree(bfp);
        adapter.indexBasedUpdateAdapter(bfp.getId(), bfp);

        //  NotificationService.modifyBeepFreePeriod(bfp.getId(), bfp);
    }


    @Override
    public void onBackPressed() {

        Fragment f = getFragmentManager().findFragmentById(R.id.mainContent);
        if (f instanceof StudyFragment) {
            Log.i("Finish activity", "jah");
            finish();
        }

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            finish();
        } else {
            getFragmentManager().popBackStack();
        }

    }


}
