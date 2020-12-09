package com.example.requip;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    private static final String TAG = Registration.class.getName();
    public static String sharedpreferencename = MainActivity.sharedpreferencename;

    // for registration:-
    IResult mResultCallback = null;
    api_methods method;

    // for login:-
    IResult mResultCallbackLogin = null;
    api_methods methodLogin;


    SharedPreferences sharedPreferences;

    private TextInputLayout name;
    private TextInputLayout username;
    private TextInputLayout Email;
    private TextInputLayout phone;
    private TextInputLayout pass;

    // variables to get values of the fields:-
    String _username;
    String _name;
    String _email;
    String _phone;
    String _pass;

    // progressbar dialog:-
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initVolleyCallback();
        final String base_url = this.getResources().getString(R.string.base_url);
        final String url = base_url + "/registration";

        // instantiating sharedpref to save data:-
        sharedPreferences = getSharedPreferences(sharedpreferencename, MODE_PRIVATE);

        name = (TextInputLayout) findViewById(R.id.name);
        username = (TextInputLayout) findViewById(R.id.username);
        Email = (TextInputLayout) findViewById(R.id.email);
        phone = (TextInputLayout) findViewById(R.id.phone);
        pass = (TextInputLayout) findViewById(R.id.password);
        Button btn_register = (Button) findViewById(R.id.register);
        TextView gotologin = (TextView) findViewById(R.id.goToLogin);

        // seeting click listener on register button:-
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getting all the values from fields.
                _username = username.getEditText().getText().toString();
                _name     = name.getEditText().getText().toString();
                _email = Email.getEditText().getText().toString();
                _phone = phone.getEditText().getText().toString();
                _pass = pass.getEditText().getText().toString();

                if (_username.isEmpty()){
                    username.setError("Required");
                }
                if(_name.isEmpty()){
                    name.setError("Required");
                }
                if(_email.isEmpty()){
                    Email.setError("Required");
                }
                if(_pass.isEmpty()){
                    pass.setError("Required");
                }
                if(_phone.isEmpty()){
                    phone.setError("Required");
                }

                if ( !(_username.isEmpty() || _name.isEmpty() || _email.isEmpty() || _pass.isEmpty() || _phone.isEmpty())){
                    if(_email.contains("@iitjammu.ac.in") && _email.length() >= 15){
                        dialog = ProgressDialog.show(Registration.this, "Thank you for Registration.!!",
                                "Registering you", true);
                        // calling the desired method:-
                        method = new api_methods(mResultCallback, Registration.this);
                        method.register_user(TAG, url, _username, _name, _email, _phone, _pass);
                    } else{
                        Email.setError("Must be campus mail.!!");
                        Toast.makeText(Registration.this, "Institut email required.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        gotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(Registration.this, "",
                        "Loading. Please wait...", true);
                startActivity( new Intent(Registration.this, Login.class));
                finish();
            }
        });


    }

    void initVolleyCallback(){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSONObjest post request result =>" + response.toString());
                try {
                    String message = response.getString("message");
                    if (message.contains("User already exists")){
                        dialog.hide();
                        Log.d(TAG, "User already exsists...");
                        username.setError("User Already Exsists.");
                        Email.setError("User Already Exsits.");
                    } else if (message.contains("Some Error")){
                        dialog.hide();
                        Log.e(TAG, "Some error occured while savinig the details from server side, Have to try again");
                        username.setError("Try agaiin");
                        Toast.makeText(Registration.this, "Error occured, TRY AGAIN..!!", Toast.LENGTH_SHORT).show();
                    } else if(message.contains("is created")){
                        dialog.hide();
                        Toast.makeText(Registration.this, "User Registered Successfully, Now check yyour mail and verify yourself.", 4).show();
                        startActivity(new Intent(Registration.this, MainActivity.class));
                        finishAffinity();

//                        dialog = ProgressDialog.show(Registration.this, "Logging In",
//                                "Your patience is appriciated!.", true);

//                        // performing login action.
//                        initVolleyCallbackLogin();
//                        String base_url_login = Registration.this.getResources().getString(R.string.base_url);
//                        String url_login = base_url_login + "/login";
//
//                        // calling the desired method:-
//                        methodLogin = new api_methods(mResultCallbackLogin, Registration.this);
//                        methodLogin.login_user("POSTCALL", TAG, url_login, _username, _pass);


                    } else if(message.contains("invalid params")){
                        Toast.makeText(Registration.this, "Maybe your phone number is invalid, Try with valid credentials!", 3).show();
                        Log.e(TAG, "user credentials are invalid..!!");
                    } else{
                        dialog.hide();
                        Log.e(TAG, "Some unwanted error occured, Try again.");
                        Toast.makeText(Registration.this, "Some error occured, Try again please.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    dialog.hide();
                    e.printStackTrace();
                    Log.e(TAG, "this doesn;t contained any message parameter in response, Some error occured while handling response type.");
                }
            }

            @Override
            public void notifySuccessArray(String requestType, JSONArray response) {
                dialog.hide();
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSONArray post" + response.toString());
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                dialog.hide();
                Toast.makeText(Registration.this, "Some error occured while Registering...", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Volley requester " + requestType);
                Log.e(TAG, "Volley error===>>>" + "That didn't work!");
            }

            @Override
            public void ErrorString(String requestType, String error) {
                dialog.hide();
                Log.e(TAG, "Volley requester " + requestType);
                Log.e(TAG, "Volley string error ===>>" + error);
            }
        };
    }

    void initVolleyCallbackLogin(){
        mResultCallbackLogin = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                try {
                    String _message = response.getString("message");
                    if (_message.contains("User does not exists") || _message.contains("Invalid Credentials")){
                        // handling cases which are rare, but may error due to some other reason.
                        Log.e(TAG, "Some error occured while logging in, Redirecting to Login activity.");
                        dialog.hide();
                        Toast.makeText(Registration.this, "Error occured While logging in.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Registration.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        // saving the user information
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String _nameLogin = response.getString("name");
                        String _usernameLogin = response.getString("username");
                        String _imageLogin = response.getString("image");
                        String _access_tokenLogin = response.getString("access_token");
                        String _refresh_tockenLogin = response.getString("refresh_token");
                        editor.putBoolean("isloggedin", true);
                        editor.putString("access_token", _access_tokenLogin);
                        editor.putString("refresh_token", _refresh_tockenLogin);
                        editor.putString("name", _usernameLogin);
                        editor.putString("username", _usernameLogin);
                        editor.putString("image", _imageLogin);
                        editor.commit();

                        Log.d(TAG, "Login Successfull.");
                        dialog.hide();
                        Toast.makeText(Registration.this, "Login Successfull!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Registration.this, MainActivity.class));
                        finishAffinity();
                    }
                } catch (JSONException e) {
                    dialog.hide();
                    Log.e(TAG, "Has not contained message field...");
                    e.printStackTrace();
                }
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSONObjest post" + response.toString());
            }

            @Override
            public void notifySuccessArray(String requestType, JSONArray response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSONArray post" + response.toString());
                dialog.hide();
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.e(TAG, "Volley requester " + requestType);
                Log.e(TAG, "Volley error===>>>" + "That didn't work!");
                dialog.hide();
                Toast.makeText(Registration.this, "Some error occured while Logging in...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void ErrorString(String requestType, String error) {
                Log.e(TAG, "Volley requester " + requestType);
                Log.e(TAG, "Volley string error ===>>" + error);
                dialog.hide();
            }
        };
    }
}