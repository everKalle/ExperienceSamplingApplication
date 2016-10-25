package com.example.madiskar.experiencesamplingapp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
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
    private static String TAG = MainActivity.class.getSimpleName();

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

        //print token
        //String token = spref.getString("token", "none");
        //Log.i("TOKEN", token);


        mMenuItems.add(new MenuItem("My Studies", "View my current studies", R.drawable.ic_study));
        mMenuItems.add(new MenuItem("Join Studies", "Browse and join available \nstudies", R.drawable.ic_add));
        mMenuItems.add(new MenuItem("My Events", "View my active events", R.drawable.ic_events));
        mMenuItems.add(new MenuItem("Settings", "Change my settings", R.drawable.ic_settings));
        mMenuItems.add(new MenuItem("Log Out", "Log out from current account", R.drawable.ic_logout));
        mMenuItems.add(new MenuItem("Exit", "Active studies will continue \nto run in background", R.drawable.ic_exit));

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
                Log.d(TAG, "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

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

        Study study1 = new Study(0, "Study 1", qnaire1, c1, c2, 30, 3, 1, 2, true, 1, eventsArray1);
        Study study2 = new Study(1, "Study 2", qnaire2, c1, c2, 30, 3, 1, 2, true, 1, eventsArray2);

        //getApplicationContext().deleteDatabase("ActiveStudies.db"); // recreate database every time for testing purposes

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
        */

        setTitle("My Studies");
        loadFragment("My Studies", false);


        ArrayList<Study> studylist = mydb.getAllStudies();

        for(Study s : studylist) {
            ResponseReceiver rR = new ResponseReceiver(s);
            rR.setupAlarm(getApplicationContext(), true);
        }

        Log.i("EQUIVALENCE", Boolean.toString(study1.equals(studylist.get(0))) + " AND " + Boolean.toString(study2.equals(studylist.get(1))));

        //TODO: Check for problems in dbhandler methods


        /*
        ResponseReceiver responseReceiver1 = new ResponseReceiver(study1);
        ResponseReceiver responseReceiver2 = new ResponseReceiver(study2);

        responseReceiver1.setupAlarm(getApplicationContext(), true);
        responseReceiver2.setupAlarm(getApplicationContext(), true);
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
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("com.example.madiskar.ExperienceSampler", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("LoggedIn", 0);
            editor.putString("username", "none");
            editor.putString("token", "none");
            editor.apply();
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            DBHandler.getInstance(getApplicationContext()).clearTables();
            try{
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(getApplicationContext(), ResponseReceiver.class), 0);
                AlarmManager am=(AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                am.cancel(pendingIntent);
            }catch (Exception e){
                e.printStackTrace();
            }
            finish();
            startActivity(i);
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
