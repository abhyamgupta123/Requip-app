package com.example.requip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


    IResult mResultCallback = null;
    api_methods method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initVolleyCallback();
        final String base_url = this.getResources().getString(R.string.base_url);
        final String url = base_url + "/registration";

        final TextInputLayout name = (TextInputLayout) findViewById(R.id.name);
        final TextInputLayout username = (TextInputLayout) findViewById(R.id.username);
        final TextInputLayout Email = (TextInputLayout) findViewById(R.id.email);
        final TextInputLayout phone = (TextInputLayout) findViewById(R.id.phone);
        final TextInputLayout pass = (TextInputLayout) findViewById(R.id.password);
        Button btn_register = (Button) findViewById(R.id.register);

        // seeting click listener on register button:-
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getting all the values from fields.
                String _username = username.getEditText().getText().toString();
                String _name     = name.getEditText().getText().toString();
                String _email = Email.getEditText().getText().toString();
                String _phone = phone.getEditText().getText().toString();
                String _pass = pass.getEditText().getText().toString();

                // calling the desired method:-
                api_methods method = new api_methods(mResultCallback, Registration.this);
                method.register_user(TAG, url, _username, _name, _email, _phone, _pass);
            }
        });
    }

    void initVolleyCallback(){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSONObjest post" + response.toString());
            }

            @Override
            public void notifySuccessArray(String requestType, JSONArray response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSONArray post" + response.toString());
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Toast.makeText(Registration.this, "Some error occured while Registering...", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Volley requester " + requestType);
                Log.e(TAG, "Volley error===>>>" + "That didn't work!");
            }

            @Override
            public void ErrorString(String requestType, String error) {
                Log.e(TAG, "Volley requester " + requestType);
                Log.e(TAG, "Volley string error ===>>" + error);
            }
        };
    }
}