package com.example.requip;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class postEditFragment extends Fragment {
    private static final String TAG = postEditFragment.class.getName();
    public static String sharedpreferencename = MainActivity.sharedpreferencename;

    private TextInputLayout title;
    private TextInputLayout description;
    private TextInputLayout price;
    private TextInputLayout samanType;
    private TextInputLayout phone;
    private SwitchMaterial switchbox;

    // flag to check if instance is created for 1st time or resumed;-
    private boolean onresume = false;

    private Context thiscontext;
    private IResult mResultCallback = null;
    private api_methods method;

    // progressbar dialog:-
    ProgressDialog dialog;

    ImageView image;
    Button update;

    public postEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_edit, container, false);

        // getting the context for this fragment:-
        thiscontext = container.getContext();

        // registering the views.
        image = view.findViewById(R.id.editsaman_image);
        update = view.findViewById(R.id.updatesamaninfo);
        title = view.findViewById(R.id.editsaman_title);
        description = view.findViewById(R.id.editsaman_description);
        price = view.findViewById(R.id.editsaman_price);
        samanType = view.findViewById(R.id.editsaman_type);
        phone = view.findViewById(R.id.editsaman_phone);
        switchbox = view.findViewById(R.id.editSaman_switch);

        image.setVisibility(View.VISIBLE);

        // declaring variables for communicating:-
        final String samanid = getArguments().getString("samanid");
        final String base_url = getResources().getString(R.string.base_url);
        final String url = base_url + "/saman/";
        final String finalurl = url + samanid;

//        final String base_url = getResources().getString(R.string.base_url);
//        // calling the desired method:-
//        method = new api_methods(mResultCallback, thiscontext);

        switchbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    makeVisible();
                } else{
                    makeInvisible();
                }
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _title = title.getEditText().getText().toString();
                String _description = description.getEditText().getText().toString().trim();
                String _price = price.getEditText().getText().toString();
                String _type = samanType.getEditText().getText().toString();
                String _phone = phone.getEditText().getText().toString();

                if(_title.isEmpty()){
                    title.setError("Required!");
                }
                if(_description.isEmpty()){
                    description.setError("Required!");
                }
                if(_price.isEmpty()){
                    price.setError("Required!");
                }
                if(_type.isEmpty()){
                    samanType.setError("Required!");
                }
                if(_phone.isEmpty()){
                    phone.setError("Required!");
                }

                if(!(_title.isEmpty() || _description.isEmpty() || _price.isEmpty() || _type.isEmpty() || _phone.isEmpty())){


                    // Showing progress window:-
                    dialog = ProgressDialog.show(thiscontext, "",
                            "Loading. Please wait...", true);
                    method.singlesamaninfo("POSTCALL", TAG, finalurl, _title, _price, _type, _description, _phone);
                }
            }
        });

        // initialising the callback listener:-
        initVolleyCallback();

        // Showing progress window:-
        dialog = ProgressDialog.show(thiscontext, "",
                "Loading. Please wait...", true);
        method = new api_methods(mResultCallback, thiscontext);
        method.singlesamaninfo("GETCALL", TAG, finalurl, null, null, null, null, null);
        makeInvisible();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        onresume = true;
    }

    @Override
    public void onResume() {
        if(dialog.isShowing() && onresume) {
            dialog.dismiss();
            Log.e(TAG, "Dialog dissmissed is called");
        }
        super.onResume();
    }


//    @Override
//    public void onResume() {
//        if(dialog.isShowing()){
//            dialog.dismiss();
//        }
//        super.onResume();
//    }

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
                if (requestType.contains("GETCALL")) {
                    Log.d(TAG, "Volley requester " + requestType);
                    Log.d(TAG, "Volley JSONObjest post request successfull.");
                    try {
                        String _title = response.getString("title");
                        String _price = response.getString("price");
                        String _type = response.getString("type");
                        String _description = response.getString("description");
                        String _phone = response.getString("phone");
                        String _image = response.getString("images");

                        title.getEditText().setText(_title);
                        description.getEditText().setText(_description);
                        price.getEditText().setText(_price);
                        samanType.getEditText().setText(_type);
                        phone.getEditText().setText(_phone);

                        String _imageurl = thiscontext.getResources().getString(R.string.base_url_samanImage) + _image;
                        Picasso.get().load(_imageurl).placeholder(R.drawable.iitjammu)
                                .error(R.drawable.iitjammu)
                                .into(image);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        Log.e(TAG, "Some error occured while fetching and setting information for edit saman post...");
                    }
                } else if (requestType.contains("POSTCALL")) {
                    Log.d(TAG, "Volley requester " + requestType);
                    Log.d(TAG, "Volley JSONObjest post" + response.toString());
                    try {
                        String message = response.getString("message");
                        Log.e(TAG, message);
                        if (message.contains("Information of your saman updated successfully")) {
                            Log.d(TAG, "information is updated successfully..!!");
                            MainActivity mainActivity = (MainActivity) getActivity();
                            mainActivity.makevisible();
                            mainActivity.toolbar.setTitle("My Posts");
                            getActivity().onBackPressed();
                            Toast.makeText(thiscontext, "Information Updated Successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Log.e(TAG, "information is not updated successfully.");
                            Toast.makeText(thiscontext, "Try again, Some error occured.", 2).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Some error occured while updating the saman.");
                        Toast.makeText(thiscontext, "Error occured, Please try again.", Toast.LENGTH_LONG).show();
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
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                dialog.hide();
                if(error.toString().contains("com.android.volley.NoConnectionError")){
                    Toast.makeText(thiscontext, "Couldn't able to connect to server, Check your internet connection.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Couldn't able to connect to server, internet connection is not up");
                }
                if(requestType.contains("GETCALL")){
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
                } else if(requestType.contains("POSTCALL")){
                    try{
                        Log.e(TAG, "value of status code is ===>>>>>" + String.valueOf(error.networkResponse.statusCode));
                        if(error.networkResponse.statusCode == 504){
                            Toast.makeText(thiscontext, "Internal server error, Please try again later.", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Internal server error, Please try again later.");
                        } else if (error.networkResponse.statusCode == 403){
                            Toast.makeText(thiscontext, "You don't have permission to edit this saman, Contact administrator..!!", 3).show();
                            Log.e(TAG, "logged user is not permitted to edit this saman, contact administrator.");
                        }
                    } catch(Exception e) {
                        Log.e(TAG , "Internet connection is ");
                        Toast.makeText(thiscontext, "Check your internet connection!", Toast.LENGTH_LONG).show();
                    }
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
            }
        };
    }

    // these two set the edit mode to be able to be edited or not:-
    private void makeVisible(){
        title.setEnabled(true);
        description.setEnabled(true);
        price.setEnabled(true);
        samanType.setEnabled(true);
        phone.setEnabled(true);
    }
    private void makeInvisible(){
        title.setEnabled(false);
        description.setEnabled(false);
        price.setEnabled(false);
        samanType.setEnabled(false);
        phone.setEnabled(false);
    }
}