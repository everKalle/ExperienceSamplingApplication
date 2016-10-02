package com.example.madiskar.experiencesamplingapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {


    private static String TAG = MainActivity.class.getSimpleName();

    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    ArrayList<MenuItem> mMenuItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMenuItems.add(new MenuItem("My Studies", "View my current studies", R.drawable.ic_study));
        mMenuItems.add(new MenuItem("My Events", "View active events", R.drawable.ic_events));
        mMenuItems.add(new MenuItem("Join Studies", "Browse and join available \nstudies", R.drawable.ic_add));
        mMenuItems.add(new MenuItem("Log Out", "Log out from current account", R.drawable.ic_logout));
        mMenuItems.add(new MenuItem("Exit", "Active studies will continue \nto run in background", R.drawable.ic_exit));

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

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        Question q4 = new FreeTextQuestion(0, "Is it easy?");
        Question q2 = new FreeTextQuestion(0, "Is it still easy?");
        Question q3 = new FreeTextQuestion(1, "Is it easy or is it easy?");
        Question q1  = new MultipleChoiceQuestion(1, "How would you rate the difficulty of this question?", new String[]{"easy", "medium", "hard"});

        ArrayList<Question> batch1 = new ArrayList<>();
        batch1.add(q4);
        batch1.add(q2);
        ArrayList<Question> batch2 = new ArrayList<>();
        batch2.add(q1);
        batch2.add(q3);

        Questionnaire qnaire1 = new Questionnaire(0, batch1);
        Questionnaire qnaire2 = new Questionnaire(0, batch2);

        Calendar c1 = Calendar.getInstance();
        c1.set(2016, 2, 20);
        Calendar c2 = Calendar.getInstance();
        c2.set(2016, 3, 20);
        Study study1 = new Study(0, "Study 1", qnaire1, c1, c2, 30, 3, 1, 5, true, 1);
        Study study2 = new Study(1, "Study 2", qnaire2, c1, c2, 30, 3, 1, 5, true, 1);

        getApplicationContext().deleteDatabase("ActiveStudies.db"); // recreate database every time for testing purposes

        DBHandler mydb = DBHandler.getInstance(getApplicationContext());
        //mydb.clearTables();

        mydb.insertStudy(study1);
        mydb.insertStudy(study2);

        //ArrayList<Study> currentStudies = mydb.getAllStudies();

        //Intent msgIntent = new Intent(this, NotificationService.class);
        //msgIntent.putExtra(NotificationService.NOTIFICATION_TEXT, study.getNotificationInterval());
        //startService(msgIntent);

        setTitle("My Studies");
        loadFragment("My Studies", false);

        ResponseReceiver.setupAlarm(getApplicationContext(), study1, true);

    }


    /*
    * Called when a particular item from the navigation drawer
    * is selected.
    * */
    private void selectItemFromDrawer(int position) {
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerPane);
        String itemName = mMenuItems.get(position).mTitle;
        setTitle(itemName);
        loadFragment(itemName, true);

    }

    private void loadFragment(String itemName, boolean from_menu) {
        FragmentManager fragmentManager = getFragmentManager();
        if(itemName == "My Studies") {
            Fragment fragment = new StudyFragment();
            Bundle args = new Bundle();
            args.putBoolean("fromNav", from_menu);
            fragment.setArguments(args);
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContent, fragment)
                    .commit();
        }
        // TODO: other fragments here
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

}
