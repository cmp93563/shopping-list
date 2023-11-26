package edu.uga.cs.shoppinglist;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SplashPage extends AppCompatActivity {


    public static final String TAG = "ShoppingList";

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private ShoppingListRecyclerAdapter recyclerAdapter;

    private List<ListItem> itemsList;

    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_page);


        // assigning ID of the toolbar to a variable
        toolbar = findViewById(R.id.toolbar);

        // using toolbar as ActionBar
        setSupportActionBar(toolbar);

        // Find our drawer view
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();

        // Connect DrawerLayout events to the ActionBarToggle
        drawerLayout.addDrawerListener(drawerToggle);

        // Find the drawer view
        navigationView = findViewById(R.id.nvView);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            selectDrawerItem(menuItem);
            return true;
        });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;

        // Create a new fragment based on the used selection in the nav drawer
//        switch( menuItem.getItemId() ) {
        if (menuItem.getItemId() == R.id.menu_list) {
            Intent intent = new Intent(getApplicationContext(), ShoppingList.class);
            startActivity(intent);
        } else if (menuItem.getItemId() == R.id.menu_purchased) {
            Intent intent = new Intent(getApplicationContext(), ShoppingList.class);
            startActivity(intent);
        } else if (menuItem.getItemId() == R.id.menu_settle) {
            Intent intent = new Intent(getApplicationContext(), ShoppingList.class);
            startActivity(intent);
        } else if (menuItem.getItemId() == R.id.menu_close) {
            Intent intent = new Intent(getApplicationContext(), ShoppingList.class);
            startActivity(intent);
        } else return;
//        }

        // Set up the fragment by replacing any existing fragment in the main activity
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().

                replace(R.id.fragmentContainerView, fragment).

                addToBackStack("main screen").

                commit();

        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
    }
}