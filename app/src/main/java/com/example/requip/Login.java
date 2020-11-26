package com.example.requip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.requip.MainActivity.sharedpreferencename;

public class Login extends AppCompatActivity {

    private static final String TAG = Login.class.getName();
    public static String sharedpreferencename = MainActivity.sharedpreferencename;

    IResult mResultCallback = null;
    api_methods method;

    TextInputLayout id;
    TextInputLayout pass;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        androidx.appcompat.widget.Toolbar toolbar = (Toolbar) findViewById( R.id.login_toolbar);
        setSupportActionBar( toolbar );

        initVolleyCallback();
        final String base_url = this.getResources().getString(R.string.base_url);
        final String url = base_url + "/login";



        id = (TextInputLayout) findViewById(R.id.loginId);
        pass = (TextInputLayout) findViewById(R.id.loginpass);
        Button login_btn = (Button) findViewById(R.id.btn_login);
        TextView registration_ = (TextView) findViewById(R.id.goToRegistraion);


        sharedPreferences = getSharedPreferences(sharedpreferencename, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Showing progress window:-
                ProgressDialog dialog = ProgressDialog.show(Login.this, "",
                        "Loading. Please wait...", true);

                // getting all the values from fields.
                String _id = id.getEditText().getText().toString().trim();
                String _pass = pass.getEditText().getText().toString();

                if(TextUtils.isEmpty(_id)){
                    id.setError("required");
                }
                if(TextUtils.isEmpty(_pass)){
                    pass.setError("required");
                }

                // calling the desired method:-
                method = new api_methods(mResultCallback, Login.this);
                method.login_user("POSTCALL", TAG, url, _id, _pass);
            }
        });

        registration_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(Login.this, Registration.class));
            }
        });

    }

    void initVolleyCallback(){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                try {
                    String _message = response.getString("message");
                    if (_message.contains("User does not exists")){
                        id.setError("User Doesn't Exsist");
                    }else if(_message.contains("Invalid Credentials")){
                        id.setError("Invalid Credentials");
                        pass.setError("Invalid Credentials");
                    }
                    else{
                        String _name = response.getString("name");
                        String _username = response.getString("username");
                        String _image = response.getString("image");
                        String _access_token = response.getString("access_token");
                        String _refresh_tocken = response.getString("refresh_token");
                        editor.putBoolean("isloggedin", true);
                        editor.putString("access_token", _access_token);
                        editor.putString("refresh_token", _refresh_tocken);
                        editor.putString("name", _name);
                        editor.putString("username", _username);
                        editor.putString("image", _image);
                        editor.commit();
                        Toast.makeText(Login.this, "Login Successfull!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, MainActivity.class));
                        finishAffinity();
                    }
                } catch (JSONException e) {
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
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Toast.makeText(Login.this, "Some error occured while Logging in...", Toast.LENGTH_SHORT).show();
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