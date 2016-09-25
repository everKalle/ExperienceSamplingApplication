package com.example.madiskar.experiencesamplingapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Realiseerida süsteem, mis kasutab uuringu andmeid ning kontrollib nende põhjal (piiksude intervalle kasutades) kas peaks nüüd teate saatma
    // Vajutades OK -> kutsuda välja mingi QuestionnaireActivity
    // Vajutades Postponne -> võtta uuringu andmetest postpone'i kestus ja kutsuda aja möödudes välja QuestionnaireActivity
    // Vajutades Refuse -> eemaldada notifikatsioon

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
    }


    /*
    * Called when a particular item from the navigation drawer
    * is selected.
    * */
    private void selectItemFromDrawer(int position) {

        FragmentManager fragmentManager = getFragmentManager();

        mDrawerList.setItemChecked(position, true);
        String itemName = mMenuItems.get(position).mTitle;
        //if(itemName == "My Studies") {
            Fragment fragment = new StudyFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContent, fragment)
                    .commit();
            setTitle(itemName);
        //}

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
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
