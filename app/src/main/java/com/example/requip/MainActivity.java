package com.example.requip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
//import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getName();
    public static String sharedpreferencename = "user_status";
    private static int addsamandelay = 8;
    // floating action bar:-
    FloatingActionButton fab;

    // instantiation drawer and navigationview
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    // for credentials:-
    String access_token;
    String refresh_tocken;

    // for navigationbar:-
    LinearLayout user_profile_layout;
    Button login_button;
    TextView user_name;
    TextView user_username;
    de.hdodenhof.circleimageview.CircleImageView profileImage;
    Menu menu;

    ImageView collegeLogo;
    MenuItem searchmenu;

    // for search functionality:-
    Bundle args;

    // global toolbar variable:-
    androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // adding toolbar:
        toolbar = (Toolbar) findViewById(R.id.mainactivity_toolbar);
        setSupportActionBar(toolbar);

        // adding fab button:-
        fab = (FloatingActionButton) findViewById(R.id.fab_addSaman);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // to animate the smooth transition between activities:-
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MainActivity.this, addSaaman.class));
                    }
                }, addsamandelay);
//                startActivity(new Intent(MainActivity.this, addSaaman.class));
            }
        });

        // setting navigation and app drawer:-
        drawerLayout = findViewById(R.id.main_drawer);
        navigationView = findViewById(R.id.nav_view);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(ColorStateList.valueOf(Color.rgb(0, 0, 100)));
        navigationView.setCheckedItem(R.id.nav_home);

        // adding values to pass to ther activity:-
        args = new Bundle();
        args.putString("flag", "fresh");

        Fragment homefragment = new homeFragment();
        homefragment.setArguments(args);

        // to start the activity with home fragment:-
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fraagment_view, homefragment).commit();
        fab.setVisibility(View.VISIBLE);

        // checking if user is loggedin and performing actinons accordingly:-
        SharedPreferences sharedPreferences = getSharedPreferences(sharedpreferencename, MODE_PRIVATE);
        Boolean isloggedin = sharedPreferences.getBoolean("isloggedin", false);
        access_token = sharedPreferences.getString("access_token", null);
        refresh_tocken = sharedPreferences.getString("refresh_token", null);
        String image = sharedPreferences.getString("image", null);
        String name = sharedPreferences.getString("name", null);
        String username = sharedPreferences.getString("username", null);


        // for assigning logo:-
        collegeLogo = (ImageView) findViewById(R.id.mainactivity_logo);


        user_profile_layout = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.user_profile_view);
        login_button = (Button) navigationView.getHeaderView(0).findViewById(R.id.main_login);
        user_name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name);
        user_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_username);
        profileImage = (de.hdodenhof.circleimageview.CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.user_image);

        menu = navigationView.getMenu();

        if (isloggedin) {
            user_profile_layout.setVisibility(View.VISIBLE);
            login_button.setVisibility(View.INVISIBLE);
            user_name.setText(name);
            user_username.setText(username);
            Log.d(TAG, "yes logged in");
            String profile_url = this.getResources().getString(R.string.base_url_samanImage) + image;
            Picasso.get().load(profile_url).placeholder(R.drawable.iitjammulogo)
                    .error(R.drawable.iitjammulogo)
                    .into(profileImage);
            menu.findItem(R.id.nav_profile_section).setVisible(true);

            user_profile_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment profile_fragment = new profileFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fraagment_view, profile_fragment).commit();
                    navigationView.setCheckedItem(R.id.nav_editprofile);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    fab.setVisibility(View.INVISIBLE);
                }
            });

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // to animate the smooth transition between activities:-
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(MainActivity.this, addSaaman.class));
                        }
                    }, addsamandelay);
//                startActivity(new Intent(MainActivity.this, addSaaman.class));
                }
            });

        } else {
//            fab.setVisibility(View.INVISIBLE);
            user_profile_layout.setVisibility(View.INVISIBLE);
            login_button.setVisibility(View.VISIBLE);
            menu.findItem(R.id.nav_profile_section).setVisible(false);
            Log.d(TAG, "no not logged in ");
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // to animate the smooth transition between activities:-
                    Toast.makeText(MainActivity.this, "Login to sell your item!", Toast.LENGTH_LONG).show();
                }
            });
        }

        //
        collegeLogo.setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        searchmenu = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) searchmenu.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // handle this when parameter is passed and search button is clicked.
                // adding values to pass to ther activity:-
                args = new Bundle();
                args.putString("flag", "search");
                args.putString("text", query);

                Fragment homefragment = new homeFragment();
                homefragment.setArguments(args);

                // to start the activity with home fragment:-
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.fraagment_view, homefragment).addToBackStack(null).commit();
                fab.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (getFragmentManager().getBackStackEntryCount() == 0) {
//                Log.e(TAG, "part1=========>>>>");
                super.onBackPressed();
            } else if (getFragmentManager().getBackStackEntryCount() == 1) {
//                Log.e(TAG, "part2=========>>>>");
                moveTaskToBack(false);
            } else {
//                Log.e(TAG, "part3=========>>>>");
                getFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment selectedfragment = null;

        switch (item.getItemId()) {
            case R.id.nav_home:
                toolbar.setTitle("Requip");
                collegeLogo.setVisibility(View.GONE);
                searchmenu.setVisible(true);
                selectedfragment = new homeFragment();
                fab.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_logout:
                try {
                    user_profile_layout.setVisibility(View.INVISIBLE);
                    login_button.setVisibility(View.VISIBLE);
                    SharedPreferences.Editor editor = getSharedPreferences(sharedpreferencename, MODE_PRIVATE).edit();
                    editor.putBoolean("isloggedin", false);
                    editor.putString("access_token", null);
                    editor.putString("refresh_token", null);
                    editor.putString("name", null);
                    editor.putString("username", null);
                    editor.putString("image", null);
                    editor.commit();
                    menu.findItem(R.id.nav_profile_section).setVisible(false);
                    Toast.makeText(this, "Logged out successfully...", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "user logged out");
                    selectedfragment = new homeFragment();
                } catch (Exception e) {
                    Toast.makeText(this, "Not logged out.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "Try again....", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.toString());
                }
                break;
            case R.id.nav_editprofile:
                searchmenu.setVisible(false);
                collegeLogo.setVisibility(View.VISIBLE);
                toolbar.setTitle("My Profile");
                selectedfragment = new profileFragment();
                fab.setVisibility(View.INVISIBLE);
                break;
            case R.id.nav_myposts:
                searchmenu.setVisible(false);
                collegeLogo.setVisibility(View.VISIBLE);
                toolbar.setTitle("My Posts");
                selectedfragment = new mypostFragment();
                fab.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_chat:
                selectedfragment = new chatmain();
                fab.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_share:
                ShareCompat.IntentBuilder.from(this)
                        .setType("text/plain")
                        .setText("something")
                        .startChooser();
                break;
            case R.id.nav_conctribute:
                selectedfragment = new chatmain();
                fab.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_dev:
                selectedfragment = new chatmain();
                fab.setVisibility(View.VISIBLE);
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fraagment_view, selectedfragment).commit();
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void gotologinactivity(View view) {
        this.startActivity(new Intent(MainActivity.this, Login.class));
    }


    // these two method is to be used by other fragmnets or classes to hide or unhide the floatingactionbutton:-
    public void makevisible(){
        fab.setVisibility(View.VISIBLE);
    }

    public void makeinvisible() {
        fab.setVisibility(View.INVISIBLE);
    }

}