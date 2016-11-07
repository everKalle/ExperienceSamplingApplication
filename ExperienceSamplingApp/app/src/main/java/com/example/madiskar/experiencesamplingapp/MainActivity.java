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

    //TODO - DISALLOW CLICKING ON NOTIFICATIONS AND THUS STARTING AN EVIL ALL-DESTROYING ACTIVITY
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

        DBHandler mydb = DBHandler.getInstance(getApplicationContext());
        ArrayList<BeepFerePeriod> bfps = mydb.getBeepFreePeriods();
        Log.v("KONTROLL", String.valueOf(bfps.size()));
        adapter = new BeepFreePeriodListAdapter(this, bfps);
        adapter.updateAdapter(bfps);


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


        setTitle("My Studies");
        loadFragment("My Studies", false);


        //TODO: Check for problems in dbhandler methods

        //TODO: look over db querys and maybe use asynctask with the bigger ones
        /*

        Question q4 = new FreeTextQuestion(0, "Is it easy?");
        Question q2 = new FreeTextQuestion(0, "Is it still easy?");
        Question q3 = new FreeTextQuestion(1, "Is it easy or is it easy?");
        Question q1 = new MultipleChoiceQuestion(0, 1, "How would you rate the difficulty of this question?", new String[]{"easy", "medium", "hard"});
        Question q5 = new MultipleChoiceQuestion(0, 0, "How would you rate the difficulty of this question?", new String[]{"pretty easy", "medium, I think", "hard", "impossible"});

        Question[] batch1 = {q4,q2,q5,q1};
        //Log.v("TESTING", String.valueOf(q4 instanceof FreeTextQuestion));

        Question[] batch2 = {q3};

        Questionnaire qnaire1 = new Questionnaire(0, batch1);
        Questionnaire qnaire2 = new Questionnaire(1, batch2);

        //yyyy-MM-dd HH:mm:ss
        String c1s = "2016-2-10";
        String c2s = "2016-3-10";
        Calendar c1 = DBHandler.stringToCalendar(c1s);
        //Log.i("BEGINDATE", DBHandler.calendarToString(c1));
        //c1.set(2016, 2, 20, 10, 0);
        Calendar c2 = DBHandler.stringToCalendar(c2s);
        //Log.i("ENDDATE", DBHandler.calendarToString(c2));
        //c2.set(2016, 3, 20, 10, 0);
        Event event1 = new Event(0,1,"Running",5, "m");
        Event event2 = new Event(1,1, "Cooking",5, "m");
        Event event3 = new Event(2,1, "Swimming",5, "m");
        Event event4 = new Event(3,1, "Dancing",7, "m");
        Event event5 = new Event(4,1, "Sleeping",2, "m");
        Event event6 = new Event(5,1,"Cycling",3, "m");
        Event event7 = new Event(6,1, "Boxing",1, "m");
        Event event8 = new Event(7,1, "Eating",4, "m");
        Event event9 = new Event(8, 1, "Gaming", 5, "m");
        Event event14 = new Event(13, 1, "Drinking Vodka", 5, "m");
        Event event15 = new Event(14, 1, "Trying to get a girlfriend", 5, "m");

        Event event10 = new Event(9,2, "Cooking",3, "m");
        Event event11 = new Event(10,2, "Dancing",5, "m");
        Event event12 = new Event(11,2, "Eating",1, "m");
        Event event13 = new Event(12,2, "Gaming",2, "m");

        Event[] eventsArray1 = {event1, event2, event3, event4, event5, event6, event7, event8, event9, event14, event15};
        Event[] eventsArray2 = {event10, event11, event12, event13};

        Study study1 = new Study(0, "Study 1", qnaire1, c1, c2, 30, 5, 1, 2, true, 2, eventsArray1, new BeepFerePeriod(1000,16,20,17,11));
        Study study2 = new Study(1, "Study 2", qnaire2, c1, c2, 30, 6, 2, 2, true, 1, eventsArray2, new BeepFerePeriod(10001,10,10,11,11));

        getApplicationContext().deleteDatabase("ActiveStudies.db"); // recreate database every time for testing purposes

        DBHandler mydb = DBHandler.getInstance(getApplicationContext());
        mydb.clearTables();

        try {
            mydb.insertStudy(study1);
            mydb.insertStudy(study2);
        } catch (SQLiteReadOnlyDatabaseException e) {
            Log.i("Error", "Clicked on notification, catch error");
        }


        //Intent msgIntent = new Intent(this, NotificationService.class);
        //msgIntent.putExtra(NotificationService.NOTIFICATION_TEXT, study.getNotificationInterval());
        //startService(msgIntent);


        /*
        DBHandler mydb = DBHandler.getInstance(getApplicationContext());
        //System.out.println("lisatud");
        ArrayList<Study> currentStudies = mydb.getAllStudies();
        Study study1 = null;
        for(Study s : currentStudies)
            if(s.getName().equals("Study 1"))
                study1 = s;


        setTitle("My Studies");
        loadFragment("My Studies", false);


        ArrayList<Study> studylist = mydb.getAllStudies();

        for(Study s : studylist) {
            ResponseReceiver rR = new ResponseReceiver(s);
            rR.setupAlarm(getApplicationContext(), true);
        }

        */

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
                }
                try {
                    for (Study s : studylist) {
                        Intent intent = new Intent(getApplicationContext(), QuestionnaireActivity.class);
                        ResponseReceiver.cancelExistingAlarm(getApplicationContext(), intent, Integer.valueOf((s.getId() + 1) + "00002"), false);
                    }
                }catch (Exception e) {
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
        DBHandler myDb = DBHandler.getInstance(getApplicationContext());
        adapter = new BeepFreePeriodListAdapter(this, myDb.getBeepFreePeriods());
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
                                beepFreeId++; // TODO - Kontrollida, kas andmebaasiga ei teki jama, kui ntx pannakse mõni int mitmendat korda
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
}
