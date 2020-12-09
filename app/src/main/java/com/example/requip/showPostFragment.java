package com.example.requip;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class showPostFragment extends Fragment {
    private static final String TAG = showPostFragment.class.getName();
    public static String sharedpreferencename = "user_status";

    private IResult mResultCallback = null;
    private api_methods method;
    private Context thiscontext;

    // declaring types and objects of views:-
    private ImageView samanpic;
    private AppCompatButton callbtn;
    private AppCompatButton chatbtn;
    private AppCompatButton flagbtn;
    private TextView price;
    private TextView title;
    private TextView owner;
    private TextView description;
    private TextView mobile;
    private TextView type;
    private TextView tags;

    private String phone_number;
    private String username;

    List<saman> sugesstionsamanlist = new ArrayList<saman>();
    RecyclerView rv_suggestedsamanpost;
    saman_adapter suggestedsamanAdapter;

    String sugesstion_url;

    // progressbar dialog:-
    ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_post, container, false);
        // getting the context for this fragment:-
        thiscontext = container.getContext();

        // assigning the views:-
        samanpic = (ImageView) view.findViewById(R.id.samanpost_image);
        callbtn  = (AppCompatButton) view.findViewById(R.id.call);
        chatbtn  = (AppCompatButton) view.findViewById(R.id.chat);
        flagbtn  = (AppCompatButton) view.findViewById(R.id.flagbtn);
        price    = (TextView) view.findViewById(R.id.samanpost_price);
        title    = (TextView) view.findViewById(R.id.samanpost_title);
        owner    = (TextView) view.findViewById(R.id.samanpost_owner);
        description   = (TextView) view.findViewById(R.id.samanpost_description);
        mobile   = (TextView) view.findViewById(R.id.samanpost_phone);
        type     = (TextView) view.findViewById(R.id.samanpost_type);
        tags     = (TextView) view.findViewById(R.id.samanpost_tags);

        rv_suggestedsamanpost = (RecyclerView) view.findViewById(R.id.rv_suggestedsaman);

        // declaring variables for communicating:-
        final String samanid = getArguments().getString("samanid");
        final String sugestion_type = getArguments().getString("sugesstion_type");
        final String base_url = getResources().getString(R.string.base_url);
        sugesstion_url = base_url + "/saman?type=" + sugestion_type + "&text=";
        final String url = base_url + "/saman/";
        final String finalurl = url + samanid;

        // initialising the callback listener:-
        initVolleyCallback();

        // Showing progress window:-
        dialog = ProgressDialog.show(thiscontext, "",
                "Loading. Please wait...", true);
        method = new api_methods(mResultCallback, thiscontext);
        method.singlesamaninfo("GETCALL", TAG, finalurl, null, null, null, null, null);



        // setting on click listeners on buttons:-
        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialContactPhone(phone_number);
            }
        });

        flagbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Report");
                // I'm using fragment here so I'm using getView() to provide ViewGroup
                // but you can provide here any other instance of ViewGroup from your Fragment / Activity
                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.report_dialog, (ViewGroup) getView(), false);
                // Set up the input
                final EditText input = (EditText) viewInflated.findViewById(R.id.flag_reason);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(viewInflated);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with flagging operation
                        String flagurl = base_url + "/flag/" + samanid;
                        Log.e(TAG, "complete flag url is ==> " + flagurl);
                        method.flagsaman("flagcall", TAG, flagurl, input.getText().toString());
                        dialog.dismiss();
//                        m_Text = input.getText().toString();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.show();
            }
        });

        owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // adding values to pass to ther activity:-
                Bundle args = new Bundle();
                args.putString("owner", username);

                Fragment generalprofile = new generalUserProfileFragment();
                generalprofile.setArguments(args);

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.fraagment_view, generalprofile);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    private void filterate_rvsuggestion(){
        Log.d( TAG, "refreshing the post lists..." );
        suggestedsamanAdapter = new saman_adapter( sugesstionsamanlist, thiscontext);
        rv_suggestedsamanpost.setAdapter( suggestedsamanAdapter );

//        GridLayoutManager gridLayoutManager = new GridLayoutManager(thiscontext, 2 );
        rv_suggestedsamanpost.setLayoutManager( new LinearLayoutManager(thiscontext, LinearLayoutManager.HORIZONTAL, true) );
    }

    void initVolleyCallback() {
        mResultCallback = new IResult() {
            @SuppressLint({"SetTextI18n", "WrongConstant"})
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                try {
                    dialog.hide();
                } catch (Exception e) {
                    Log.e(TAG, "Dialog object is not referenced to show before.");
                }
                Log.d(TAG, "value of response is ===>>>>>" + response);
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSONObjest post request successfull.");
                if (requestType.contains("GETCALL")){
                    try {
                        String _title = response.getString("title");
                        String _price = response.getString("price");
                        String _type = response.getString("type");
                        String _description = response.getString("description");
                        String _phone = response.getString("phone");
                        String _owner = response.getString("username");
                        String _tags = response.getString("tags");
                        String _image = response.getString("images");

                        JSONArray jsonArray_tags = response.getJSONArray("tags");
                        String sugesstion_words = "";
                        for (int i = 0; i < jsonArray_tags.length(); i++){
                            sugesstion_words = sugesstion_words + jsonArray_tags.get(i).toString() + " ";
                        }
                        sugesstion_words = sugesstion_words.trim();
                        sugesstion_url = sugesstion_url + sugesstion_words + "&limit=4";
                        // calling for sugesstion list:-
                        Log.d(TAG, "calling suggestion post lists");
                        Log.e(TAG, "sugesstion url is =====>>>>>>>>>>>" + sugesstion_url);
                        method.list_saman("SUGESTIONGETCALL", TAG, sugesstion_url);

                        // setting the data values to objects:-
                        price.setText("₹ " + _price);
                        title.setText(_title);
                        description.setText(_description);
                        owner.setText("posted by " + _owner);
                        username = _owner;                                                      // this is for passing username value to generaluserprofilefragment.
                        if (_phone.contains("xxxx")){
                            mobile.setVisibility(View.GONE);
                            callbtn.setVisibility(View.GONE);
                            chatbtn.setVisibility(View.GONE);
                        }else{
                            mobile.setText("Mobile: " + _phone);
                            phone_number = _phone;
                        }
                        tags.setText("Tags : " + _tags);
                        type.setText("Type : " + _type);

                        String _imageurl = thiscontext.getResources().getString(R.string.base_url_samanImage) + _image;
                        Picasso.get().load(_imageurl).placeholder(R.drawable.iitjammu)
                                .error(R.drawable.iitjammu)
                                .into(samanpic);

                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        Log.e(TAG, "Some error occured while fetching and setting information for edit saman post...");
                    }
                } else if (requestType.contains("flagcall")){
                    String message = null;
                    try {
                        message = response.getString("message");
                        if(message.contains("Your have already flagged this object")){
                            Log.d(TAG, "User flagged this object earlier.");
                            Toast.makeText(thiscontext, "You Already Reported.", Toast.LENGTH_SHORT).show();
                        } else if (message.contains("Object flagged!")){
                            Log.d(TAG, "saman post flagged by the user.");
                            Toast.makeText(thiscontext, "Reported Successfully!", Toast.LENGTH_SHORT).show();
                        } else if (message.contains("error occured while flagging")){
                            Log.e(TAG, "some error occured while flagging this saman post from server side.");
                            Toast.makeText(thiscontext, "Some unknown error occured at server side, Please try again later!", 3).show();
                        } else{
                            Log.e(TAG, "Some unknown message resposnse recieved");
                            Log.e(TAG, "messgae response is -> " + message);
                            Toast.makeText(thiscontext, "Some unknown error occured, Please try again later!", 2).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Some error occured while decoding response with message parameter, see the log files.");
                        e.printStackTrace();
                        Toast.makeText(thiscontext, "Some unknown error occured, Please try again later!", 2).show();
                    }
                }
            }

            @Override
            public void notifySuccessArray(String requestType, JSONArray response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSONArray post" + response.toString());
                try {
                    dialog.hide();
                } catch (Exception e) {
                    Log.e(TAG, "Dialog object is not referenced to show before.");
                }
                if (requestType.contains("SUGESTIONGETCALL")){
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonobject = null;
                        try {
                            jsonobject = response.getJSONObject(i);
                            String _id = jsonobject.getString("_id");
                            String _title = jsonobject.getString("title");
                            String _price = jsonobject.getString("price");
                            String _type = jsonobject.getString("type");
                            String _images = jsonobject.getString("images");

                            sugesstionsamanlist.add(new saman(_id, _title, _price, _type, _images));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "some error occured while obtaiing sugettion list.");
                        }
                    }
                    filterate_rvsuggestion();
                }
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                dialog.hide();
                if(error.toString().contains("com.android.volley.NoConnectionError")){
                    Toast.makeText(thiscontext, "Couldn't able to connect to server, Check your internet connection.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Couldn't able to connect to server, internet connection is not up");
                }
                if (requestType.contains("GETCALL")){
                    try{
                        Log.e(TAG, "value of status code is ===>>>>>" + String.valueOf(error.networkResponse.statusCode));
                        Toast.makeText(thiscontext, "Some error occured Try again", Toast.LENGTH_SHORT).show();
                        if(error.networkResponse.statusCode == 404){
                            Toast.makeText(thiscontext, "This post doesn't exsist..!!", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Post doesn't exsist");
                        } else if (error.networkResponse.statusCode == 401){
                            Log.e(TAG, "Token expired, refreshing again");
                        }
                    } catch(Exception e) {
                        Log.e(TAG , "Internet connection is ");
                        Toast.makeText(thiscontext, "Check your internet connection!", Toast.LENGTH_SHORT).show();
                    }
                } else if (requestType.contains("SUGESTIONGETCALL")){
                    Toast.makeText(thiscontext, "Some error occured while Loading...", Toast.LENGTH_SHORT).show();
                    Toast.makeText(thiscontext, "Check your internet conection..!!", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Volley requester " + requestType);
                    Log.e(TAG, "Volley error===>>>" + "That didn't work!");
                }

                Log.e(TAG, "Volley requester " + requestType);
                Log.e(TAG, "Volley error===>>>" + "That didn't work!");
            }

            @Override
            public void ErrorString(String requestType, String error) {
                dialog.hide();
                Toast.makeText(thiscontext, "Some error occured while Loading, Try again later", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Volley requester " + requestType);
                Log.e(TAG, "Volley string error ===>>" + error);
                if(requestType.contains("")){
                    if(error.contains("token absent")){
                        Log.e(TAG, "user not logged in and trying to flag, aborted.");
                        Toast.makeText(thiscontext, "Log in first to flag this post!", Toast.LENGTH_LONG).show();
                    } else if(error.contains("wrong token")){
                        Log.e(TAG, "user trying to flag saman post with wrong access token, task aborted@");
                        Toast.makeText(thiscontext, "Your login credentials are wrong maybe, Relogin and try again!", 3).show();
                    } else if(error.contains("saman post not found")){
                        Log.e(TAG, "saman post is not found on server and user trying to report or flag this saman, Task aborted.");
                        Toast.makeText(thiscontext, "The post you are trying to report not found!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
    }

    private void dialContactPhone(final String phoneNumber) {
//        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
        try {
//            Intent callIntent = new Intent(Intent.ACTION_CALL);
//            callIntent.setData(Uri.parse(phoneNumber));
//            startActivity(callIntent);
            startActivityForResult(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)), 7);
//            startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
        } catch (ActivityNotFoundException activityException) {
            Log.e("myphone dialer", "Call failed", activityException);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case 7:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(thiscontext, "fine", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}