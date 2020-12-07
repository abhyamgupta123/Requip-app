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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class api_methods {

    public final static String sharedpreferencename = "user_status";
    public final static String classname = "API METHODS";

    IResult mResultCallback = null;
    Context context;


    api_methods(IResult resultCallback, Context context) {
        this.mResultCallback = resultCallback;
        this.context = context;
    }

    public void register_user(final String TAG, String url, final String Username, final String Name, final String Email, final String Phone, final String Password) {
        RequestQueue queue = Volley.newRequestQueue(context);  // passing context is neccessary.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d(TAG, "respose -> " + response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d(TAG + classname, "respose messsage is -->" + jsonObject.getString("message"));
                            mResultCallback.notifySuccess("POSTCALL", jsonObject);
                        } catch (JSONException e) {
                            Log.e(TAG + classname, "Some error is parsing and sending respose message to activity class");
                            e.printStackTrace();
                        }
                        Log.d(TAG + classname, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        mResultCallback.notifyError("POSTCALL", error);
                        Log.d(TAG, "Error.Response -> " + error);
                        Log.d(TAG, "Error.Response -> " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
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

    public void login_user(final String requestType, final String TAG, String url, final String id, final String pass) {
        RequestQueue queue = Volley.newRequestQueue(context);  // passing context is neccessary.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
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
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d(TAG, "Error.Response -> " + error);
                        mResultCallback.notifyError(requestType, error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("password", pass);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void userprofile(final String requestType, final String TAG, final String url) {
        // using shared preference values to get request:-
        final SharedPreferences sharedPreferences = context.getSharedPreferences(sharedpreferencename, MODE_PRIVATE);
        final String _access_token = sharedPreferences.getString("access_token", null);
        String _refresh_token = sharedPreferences.getString("refresh_token", null);

        RequestQueue queue = Volley.newRequestQueue(context);  // passing context is neccessary.
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
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
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e(TAG + classname, "Error.Response -> " + error);
                        try {
                            if (error.networkResponse.statusCode == 401) {
                                Log.d(TAG + classname, "Handline autherror failure");
                                refresh_token(sharedPreferences, TAG);
                                userprofile(requestType, TAG, url);
                            } else{
                                Log.e(TAG + classname, "some network error occured.");
                                Log.e(TAG + classname, "error code is -> " + error.networkResponse.statusCode);
                            }
                            mResultCallback.notifyError(requestType, error);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG + classname, "Some unknown error, maybe network problem...");
                        }
                        mResultCallback.notifyError(requestType, error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + _access_token);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void userprofile_update(final String requestType, final String TAG, final String url, final String name, final String about, final String phone) {
        // using shared preference values to get request:-
        final SharedPreferences sharedPreferences = context.getSharedPreferences(sharedpreferencename, MODE_PRIVATE);
        final String _access_token = sharedPreferences.getString("access_token", null);
        String _refresh_token = sharedPreferences.getString("refresh_token", null);

        RequestQueue queue = Volley.newRequestQueue(context);  // passing context is neccessary.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d(TAG, "respose recieved sucessfully.");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            mResultCallback.notifySuccess(requestType, jsonObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mResultCallback.ErrorString(requestType, e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e(TAG + classname, "Error.Response -> " + error);
                        try {
                            if (error.networkResponse.statusCode == 401) {
                                Log.d(TAG + classname, "Handline autherror failure");
                                refresh_token(sharedPreferences, TAG);
                                userprofile(requestType, TAG, url);
                            }else{
                                Log.e(TAG + classname, "some network error occured.");
                                Log.e(TAG + classname, "error code is -> " + error.networkResponse.statusCode);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mResultCallback.ErrorString(requestType, e.toString());
                            Log.e(TAG + classname, "Some unknown error, maybe network problem...");
                        }
                        mResultCallback.notifyError(requestType, error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + _access_token);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("about", about);
                params.put("phone", phone);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void changeprofilepic(final String requestType, final String TAG, final String url, final String image) {
        // using shared preference values to get request:-
        final SharedPreferences sharedPreferences = context.getSharedPreferences(sharedpreferencename, MODE_PRIVATE);
        final String _access_token = sharedPreferences.getString("access_token", null);
        String _refresh_token = sharedPreferences.getString("refresh_token", null);

        RequestQueue queue = Volley.newRequestQueue(context);  // passing context is neccessary.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d(TAG + classname, "respose recieved sucessfully.");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            mResultCallback.notifySuccess(requestType, jsonObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mResultCallback.ErrorString(requestType, e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e(TAG + classname, "Error.Response -> " + error);
                        try {
                            if (error.networkResponse.statusCode == 401) {
                                Log.d(TAG + classname, "Handline autherror failure");
                                refresh_token(sharedPreferences, TAG);
                                userprofile(requestType, TAG, url);
                            }else{
                                mResultCallback.notifyError(requestType, error);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mResultCallback.ErrorString(requestType, e.toString());
                            Log.e(TAG + classname, "Some unknown error, maybe network problem...");
                        }
                    }
                }) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Authorization", "Bearer " + _access_token);
                        return params;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return image == null ? null : image.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", image, "utf-8");
                    return null;
                }
            }

            //            @Override
//            public byte[] getPostBody() throws AuthFailureError {
//                return super.getPostBody();
//            }

        };
        queue.add(postRequest);
    }

// final String _title, final String _price, final String _type, final String _description, final String _phone
    public void singlesamaninfo(final String requestType, final String TAG, final String url, final String _title, final String _price, final String _type, final String _description, final String _phone) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(sharedpreferencename, MODE_PRIVATE);
        final String _access_token = sharedPreferences.getString("access_token", null);
        String _refresh_token = sharedPreferences.getString("refresh_token", null);

        RequestQueue queue = Volley.newRequestQueue(context);  // passing context is neccessary.

        if(requestType.contains("GETCALL")){
            if (_access_token.length() >= 10){
                StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d(TAG + classname, "respose -> is recieved for particular saman post");
                                try {
                                    JSONObject jsonObject = new JSONObject(response.trim());
                                    mResultCallback.notifySuccess(requestType, jsonObject);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    mResultCallback.ErrorString(requestType, e.toString());
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG + classname, "Error.Response -> " + error);
                                try{
                                    if (error.networkResponse.statusCode == 401) {
                                        Log.d(TAG + classname, "Handling autherror failure");
                                        refresh_token(sharedPreferences, TAG);
                                        singlesamaninfo(requestType, TAG, url, null, null, null,null,null);
                                    } else{
                                        Log.e(TAG + classname, "some network error occured.");
                                        Log.e(TAG + classname, "error code is -> " + error.networkResponse.statusCode);
                                    }
                                } catch(Exception e) {
                                    Log.e(TAG + classname, "Maybe problem in internet connection.");
                                }
                                mResultCallback.notifyError(requestType, error);
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Authorization", "Bearer " + _access_token);

                        return params;
                    }
                };
                queue.add(getRequest);
            } else {
                StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d(TAG + classname, "respose -> is recieved for particular saman post");
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    mResultCallback.notifySuccess(requestType, jsonObject);
                                    Log.d(TAG + classname, "result decoded sucessfully.");
                                    Log.d(TAG + classname, "Desired result is ====>>" + jsonObject.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    mResultCallback.ErrorString(requestType, e.toString());
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG + classname, "Error.Response -> " + error);
                                try{
                                    Log.e(TAG + classname, "some network error occured.");
                                    Log.e(TAG + classname, "error code is -> " + error.networkResponse.statusCode);
                                } catch(Exception e) {
                                    Log.e(TAG + classname, "Maybe problem in internet connection.");
                                }
                                mResultCallback.notifyError(requestType, error);
                            }
                        }
                );
                queue.add(getRequest);
            }
        } else if(requestType.contains("POSTCALL")){
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG + classname, "respose -> is recieved for all the posts");
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                mResultCallback.notifySuccess(requestType, jsonObject);
                                Log.d(TAG + classname, "result decoded sucessfully.");
                                Log.d(TAG + classname, "Desired result is ====>>" + jsonObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                mResultCallback.ErrorString(requestType, e.toString());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG + classname, error.toString());
                            if (error.networkResponse.statusCode == 401) {
                                Log.d(TAG + classname, "Handling autherror failure");
                                refresh_token(sharedPreferences, TAG);
                                singlesamaninfo(requestType, TAG, url, _title, _price, _type, _description, _phone);
                            }
                            mResultCallback.notifyError(requestType, error);
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "Bearer " + _access_token);
                    return params;
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("title", _title);
                    params.put("price", _price);
                    params.put("type", _type);
                    params.put("description", _description);
                    params.put("phone", _phone);

                    return params;
                }
            };
            queue.add(postRequest);
        }
    }

    public void list_saman(final String requestType, final String TAG, String url) {
        RequestQueue queue = Volley.newRequestQueue(context);  // passing context is neccessary.
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        Log.d(TAG + classname, "respose -> is recieved for all the posts");
                        mResultCallback.notifySuccessArray(requestType, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error.Response -> " + error);
                        mResultCallback.notifyError(requestType, error);
                    }
                });
        queue.add(getRequest);
    }

    public void list_user_saman(final String requestType, final String TAG, final String url) {
        // using shared preference values to get request:-
        final SharedPreferences sharedPreferences = context.getSharedPreferences(sharedpreferencename, MODE_PRIVATE);
        final String _access_token = sharedPreferences.getString("access_token", null);
        String _refresh_token = sharedPreferences.getString("refresh_token", null);

        RequestQueue queue = Volley.newRequestQueue(context);  // passing context is neccessary.
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d(TAG + classname, "respose -> is recieved for all the posts");
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            mResultCallback.notifySuccessArray(requestType, jsonArray);
                            Log.d(TAG + classname, "result decoded sucessfully.");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG + classname, "Error.Response -> " + error);
                        try{
                            if (error.networkResponse.statusCode == 401) {
                                Log.d(TAG + classname, "Handling autherror failure");
                                refresh_token(sharedPreferences, TAG);
                                list_user_saman(requestType, TAG, url);
                            } else{
                                Log.e(TAG + classname, "some network error occured.");
                                Log.e(TAG + classname, "error code is -> " + error.networkResponse.statusCode);
                            }
                        } catch(Exception e) {
                            Log.e(TAG + classname, "Maybe problem in internet connection.");
                        }

                        mResultCallback.notifyError(requestType, error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + _access_token);

                return params;
            }
        };
        queue.add(getRequest);
    }

    public void delete_saman(final String requestType, final String TAG, final String url) {
        // using shared preference values to get request:-
        final SharedPreferences sharedPreferences = context.getSharedPreferences(sharedpreferencename, MODE_PRIVATE);
        final String _access_token = sharedPreferences.getString("access_token", null);
        String _refresh_token = sharedPreferences.getString("refresh_token", null);

        RequestQueue queue = Volley.newRequestQueue(context);  // passing context is neccessary.
        StringRequest getRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d(TAG + classname, "respose -> is recieved for all the posts");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            mResultCallback.notifySuccess(requestType, jsonObject);
                            Log.d(TAG + classname, "result decoded sucessfully.");
                            Log.d(TAG + classname, "Desired result is ====>>" + jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG + classname, "Error.Response -> " + error);
                        if (error.networkResponse.statusCode == 401) {
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + _access_token);

                return params;
            }
        };
        queue.add(getRequest);
    }


    public void addsaman(final String requestType, final String TAG, final String url, final String _title, final String _price, final String _type, final String _description, final String _phone, final String _image) {
        // using shared preference values to get request:-
        final SharedPreferences sharedPreferences = context.getSharedPreferences(sharedpreferencename, MODE_PRIVATE);
        final String _access_token = sharedPreferences.getString("access_token", null);
        String _refresh_token = sharedPreferences.getString("refresh_token", null);

        RequestQueue queue = Volley.newRequestQueue(context);  // passing context is neccessary.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG + classname, "respose -> is recieved for all the posts");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            mResultCallback.notifySuccess(requestType, jsonObject);
                            Log.d(TAG + classname, "result decoded sucessfully.");
                            Log.d(TAG + classname, "Desired result is ====>>" + jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mResultCallback.ErrorString(requestType, e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG + classname, error.toString());
                        if (error.networkResponse.statusCode == 401) {
                            Log.d(TAG + classname, "Handling autherror failure");
                            refresh_token(sharedPreferences, TAG);
                            list_user_saman(requestType, TAG, url);
                        }
                        mResultCallback.notifyError(requestType, error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + _access_token);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", _title);
                params.put("price", _price);
                params.put("type", _type);
                params.put("description", _description);
                params.put("phone", _phone);
                params.put("image", _image);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void refresh_token(final SharedPreferences SHAREDPREF, final String TAG) {
        // using shared preference values to get request:-
        final String _access_token = SHAREDPREF.getString("access_token", null);
        final String _refresh_token = SHAREDPREF.getString("refresh_token", null);

        final String base_url = context.getResources().getString(R.string.base_url);
        final String url = base_url + "/refreshToken";

        RequestQueue queue = Volley.newRequestQueue(context);  // passing context is neccessary.
        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d(TAG, "response recieved successfully..!!");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String acess_key = jsonObject.getString("access_token");
                            Log.d(TAG, "refreshed token");
                            SharedPreferences.Editor editor = SHAREDPREF.edit();
                            editor.putString("access_token", acess_key);
                            editor.commit();
                            Log.d(TAG, "Access key Refreshed Sucessfully..!!");
                        } catch (JSONException e) {
                            Log.e(TAG, e.toString());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.e(TAG, "Some error occured while refreshing the acess key.");
                        Log.e(TAG, "error => " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + _refresh_token);
                return params;
            }
        };
        queue.add(getRequest);
    }

    public void test(final String requestType, final String TAG, final String url, final String test1, final String test2) {
        // using shared preference values to get request:-
        final SharedPreferences sharedPreferences = context.getSharedPreferences(sharedpreferencename, MODE_PRIVATE);
        final String _access_token = sharedPreferences.getString("access_token", null);
        String _refresh_token = sharedPreferences.getString("refresh_token", null);

        RequestQueue queue = Volley.newRequestQueue(context);  // passing context is neccessary.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG + classname, response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG + classname, error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + _access_token);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("test1", test1);
                params.put("test2", test2);

                return params;
            }
        };
        queue.add(postRequest);
    }
}
