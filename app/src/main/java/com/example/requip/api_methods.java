package com.example.requip;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.http.HttpResponseCache;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class api_methods {

    public static String sharedpreferencename = "user_status";
    public static String classname = "API METHODS";

    IResult mResultCallback = null;
    Context context;


    api_methods(IResult resultCallback, Context context){
        this.mResultCallback = resultCallback;
        this.context = context;
    }

    public void register_user(final String TAG, String url, final String Username, final String Name, final String Email, final String Phone, final String Password){
        RequestQueue queue = Volley.newRequestQueue(context);  // passing context is neccessary.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d(TAG, "respose -> " + response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d(TAG, "respose messsage is -->" + jsonObject.getString("message"));
                            mResultCallback.notifySuccess("POSTCALL", jsonObject);
                        } catch (JSONException e) {
                            Log.e(TAG + classname, "Some error is parsing and sending respose message to activity class");
                            e.printStackTrace();
                        }
                        Log.d(TAG + classname, response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        mResultCallback.notifyError("POSTCALL", error);
                        Log.d(TAG,"Error.Response -> " + error);
                        Log.d(TAG,"Error.Response -> " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", Username);
                params.put("name", Name);
                params.put("email", Email);
                params.put("phone_number", Phone);
                params.put("password", Password);
                return params;
            }
        };
        queue.add(postRequest);
    }

    public void login_user(final String requestType, final String TAG, String url, final String id, final String pass){
        RequestQueue queue = Volley.newRequestQueue(context);  // passing context is neccessary.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d(TAG, "respose -> " + response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d(TAG, jsonObject.getString("message"));
                            mResultCallback.notifySuccess(requestType, jsonObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mResultCallback.ErrorString(requestType, e.toString());
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d(TAG,"Error.Response -> " + error);
                        mResultCallback.notifyError(requestType, error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("password", pass);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void list_saman(final String requestType, final String TAG, String url){
        RequestQueue queue = Volley.newRequestQueue(context);  // passing context is neccessary.
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        Log.d(TAG, "respose -> " + response.toString());
                        mResultCallback.notifySuccessArray(requestType, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,"Error.Response -> " + error);
                        mResultCallback.notifyError(requestType, error);
                    }
                });
        queue.add(getRequest);
    }

    public void list_user_saman(final String requestType, final String TAG, final String url){
        // using shared preference values to get request:-
        final SharedPreferences sharedPreferences = context.getSharedPreferences(sharedpreferencename, MODE_PRIVATE);
        final String _access_token = sharedPreferences.getString("access_token", null);
        String _refresh_token = sharedPreferences.getString("refresh_token", null);

        RequestQueue queue = Volley.newRequestQueue(context);  // passing context is neccessary.
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d(TAG + classname, "respose -> " + response.toString());
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            mResultCallback.notifySuccessArray(requestType, jsonArray);
                            Log.d(TAG + classname, "this is result i want" + jsonArray.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG + classname,"Error.Response -> " + error);
                        if (error.networkResponse.statusCode == 401){
                            Log.d(TAG + classname, "Handline autherror failure");
                            refresh_token(sharedPreferences, TAG);
                            list_user_saman(requestType, TAG, url);
                        }
                        mResultCallback.notifyError(requestType, error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + _access_token);

                return params;
            }
        };
        queue.add(getRequest);
    }

    public void userprofile(final String requestType, final String TAG, final String url){
        // using shared preference values to get request:-
        final SharedPreferences sharedPreferences = context.getSharedPreferences(sharedpreferencename, MODE_PRIVATE);
        final String _access_token = sharedPreferences.getString("access_token", null);
        String _refresh_token = sharedPreferences.getString("refresh_token", null);

        RequestQueue queue = Volley.newRequestQueue(context);  // passing context is neccessary.
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d(TAG, "respose -> " + response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            mResultCallback.notifySuccess(requestType, jsonObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mResultCallback.ErrorString(requestType, e.toString());
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e(TAG + classname,"Error.Response -> " + error);
                        try{
                            if (error.networkResponse.statusCode == 401){
                                Log.d(TAG + classname, "Handline autherror failure");
                                refresh_token(sharedPreferences, TAG);
                                userprofile(requestType, TAG, url);
                            }
                            mResultCallback.notifyError(requestType, error);
                        } catch (Exception e){
                            e.printStackTrace();
                            Log.e(TAG + classname, "Some unknown error, maybe network problem...");
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + _access_token);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void refresh_token(final SharedPreferences SHAREDPREF, final String TAG){
        // using shared preference values to get request:-
        final String _access_token = SHAREDPREF.getString("access_token", null);
        final String _refresh_token = SHAREDPREF.getString("refresh_token", null);

        final String base_url = context.getResources().getString(R.string.base_url);
        final String url = base_url + "/refreshToken";

        RequestQueue queue = Volley.newRequestQueue(context);  // passing context is neccessary.
        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d(TAG, response);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String acess_key = jsonObject.getString("access_token");
                            Log.d(TAG, "refreshed token");
                            SharedPreferences.Editor editor = SHAREDPREF.edit();
                            editor.putString("access_token",acess_key);
                            editor.commit();
                            Log.d(TAG, "Access key Refreshed Sucessfully..!!");
                        } catch (JSONException e) {
                            Log.e(TAG, e.toString());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.e(TAG,"Some error occured while refreshing the acess key.");
                        Log.e(TAG,"error => "+ error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + _refresh_token);
                return params;
            }
        };
        queue.add(getRequest);
    }

}
