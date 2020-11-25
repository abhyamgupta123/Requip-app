package com.example.requip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getName();
    public static String sharedpreferencename = "user_status";

    // floating action bar:-
    FloatingActionButton fab;

    // instantiation drawer and navigationview
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    String access_token;
    String refresh_tocken;

    LinearLayout user_profile_layout;
    Button login_button;
    TextView user_name;
    TextView user_username;
    de.hdodenhof.circleimageview.CircleImageView profileImage;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // adding toolbar:
        androidx.appcompat.widget.Toolbar toolbar = (Toolbar) findViewById( R.id.mainactivity_toolbar );
        setSupportActionBar( toolbar );

        // adding fab button:-
        fab = (FloatingActionButton) findViewById( R.id.fab_addSaman );

        // setting navigation and app drawer:-
        drawerLayout = findViewById(R.id.main_drawer);
        navigationView = findViewById(R.id.nav_view);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(ColorStateList.valueOf(Color.rgb(0,0,100)));
        navigationView.setCheckedItem(R.id.nav_home);

        // to start the activity with home fragment:-
        Fragment startfragment = new homeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fraagment_view, startfragment).commit();
        fab.setVisibility(View.VISIBLE);

        // checking if user is loggedin and performing actinons accordingly:-
        SharedPreferences sharedPreferences = getSharedPreferences(sharedpreferencename, MODE_PRIVATE);
        Boolean isloggedin = sharedPreferences.getBoolean("isloggedin", false);
        access_token = sharedPreferences.getString("access_token", null);
        refresh_tocken = sharedPreferences.getString("refresh_token", null);
        String image = sharedPreferences.getString("image", null);
        String name = sharedPreferences.getString("name", null);
        String username = sharedPreferences.getString("username", null);


        user_profile_layout = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.user_profile_view);
        login_button = (Button) navigationView.getHeaderView(0).findViewById(R.id.main_login);
        user_name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name);
        user_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_username);
        profileImage = (de.hdodenhof.circleimageview.CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.user_image);

        menu = navigationView.getMenu();

        if (isloggedin){
            user_profile_layout.setVisibility(View.VISIBLE);
            login_button.setVisibility(View.INVISIBLE);
            user_name.setText(name);
            user_username.setText(username);
            Log.d(TAG,"yes logged in");
            String profile_url = this.getResources().getString(R.string.base_url_samanImage) + image;
            Picasso.get().load( profile_url ).placeholder( R.drawable.iitjammulogo )
                    .error( R.drawable.iitjammulogo )
                    .into( profileImage );
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

        }else{
            user_profile_layout.setVisibility(View.INVISIBLE);
            login_button.setVisibility(View.VISIBLE);
            menu.findItem(R.id.nav_profile_section).setVisible(false);
            Log.d(TAG, "no not logged in ");
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment selectedfragment = null;

        switch (item.getItemId()){
            case R.id.nav_home:
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
                } catch (Exception e){
                    Toast.makeText(this, "Not logged out.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "Try again....", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.toString());
                }
                break;
            case R.id.nav_editprofile:
                selectedfragment = new profileFragment();
                break;
            case R.id.nav_myposts:
                selectedfragment = new mypostFragment();
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

    public void addsaman(View view) {
        ;
    }
}
//        RequestQueue queue = Volley.newRequestQueue(this);  // this = context
//        String url = "http://192.168.1.6:5000/saman/testing";
//
// prepare the Request
//        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.POST, url, null
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        // display response
//                        Log.d(TAG, "respose -> " + response.toString());
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d(TAG,"Error.Response -> " + error);
//                    }
//                });
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>()
//                {
//                    @Override
//                    public void onResponse(String response) {
//                        // response
//                        Log.d(TAG, "respose -> " + response.toString());
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            Log.d(TAG, jsonObject.getString("message"));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
////                        Log.d("activity response", response);
//                    }
//
////                    @Override
////                    public void onResponse(JsonObject response) {
////
////                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // error
//                        Log.d(TAG,"Error.Response -> " + error);
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams()
//            {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("id", "abhyam");
//                params.put("password", "abhyam");
//
//                return params;
//            }
//        };
//        queue.add(postRequest);
//        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, null
//                new Response.Listener<JSONObject>()
//                {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // display response
//                        Log.d(TAG, "respose -> " + response.toString());
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d(TAG,"Error.Response -> " + error.getMessage());
//                    }
//                }
//        ){
//            @Override
//            protected Map<String, String> getParams(){
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("id", "abhyam");
//                params.put("password", "abhyam");
//
//                return params;
//            }
//        };
//
//        // add it to the RequestQueue
//        queue.add(getRequest);
//        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>()
//                {
//                    @Override
//                    public void onResponse(String response) {
//                        // response
//                        Log.d("Response", response);
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // TODO Auto-generated method stub
//                        Log.d("ERROR","error => "+error.toString());
//                    }
//                }
//        ) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String>  params = new HashMap<String, String>();
//                params.put("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MDU3Nzc1NjAsIm5iZiI6MTYwNTc3NzU2MCwianRpIjoiNWRhODMzMDEtMTI3Ny00NmZiLTk2OTgtODljM2QwYmQxZWE1IiwiZXhwIjoxNjA1Nzc4NDYwLCJpZGVudGl0eSI6ImFiaHlhbSIsImZyZXNoIjpmYWxzZSwidHlwZSI6ImFjY2VzcyJ9.3AxIi-yBIKgM5qENkZJyPF1VjQpvsXkyQWOxGK7rIZc");
//
//
//                return params;
//            }
//        };
//        queue.add(getRequest);