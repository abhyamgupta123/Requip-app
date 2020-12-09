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
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class generalUserProfileFragment extends Fragment {
    private static final String TAG = generalUserProfileFragment.class.getName();
    public static String sharedpreferencename = "user_status";

    private Context thiscontext;
    private IResult mResultCallback = null;
    private api_methods method;

    // this intent is for openeing photo select activity:--
    Intent intent;

    private de.hdodenhof.circleimageview.CircleImageView generalprofile_image;
    private TextInputLayout generalprofile_name;
    private TextInputLayout generalprofile_username;
    private TextInputLayout generalprofile_email;
    private TextInputLayout generalprofile_descript;

    ProgressDialog dialog;

    public generalUserProfileFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_general_user_profile, container, false);

        // getting the context for this fragment:-
        thiscontext = container.getContext();

        generalprofile_image = view.findViewById(R.id.generalprofile_user_image);
        generalprofile_name = view.findViewById(R.id.generalprofile_name);
        generalprofile_username = view.findViewById(R.id.generalprofile_username);
        generalprofile_email = view.findViewById(R.id.generalprofile_email);
        generalprofile_descript = view.findViewById(R.id.generalprofile_description);

        // initialising the callback function for api request listener:-
        initVolleyCallback();
        final String base_url = this.getResources().getString(R.string.base_url);
        final String owner = getArguments().getString("owner");
        final String url = base_url + "/profile/" + owner;
        Log.e(TAG, "someplete url is ===>> " + url);
        // calling the desired method:-
        method = new api_methods(mResultCallback, thiscontext);
        // Showing progress window:-
        dialog = ProgressDialog.show(thiscontext, "",
                "Loading. Please wait...", true);
        method.generaluserprofile("GETCALL", TAG, url);

        return view;
    }

    void initVolleyCallback() {
        mResultCallback = new IResult() {
            @SuppressLint({"SetTextI18n", "WrongConstant"})
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "value of response is ===>>>>>" + response);
                if (requestType.contains("GETCALL")) {
                    Log.d(TAG, "Volley requester " + requestType);
                    Log.d(TAG, "Volley JSONObjest post request successfull." );
                    try {
                        String message = response.getString("message");
                        Log.e(TAG, message);
                        Log.e(TAG, "user doesn't exsist handling this situation.");
                        Toast.makeText(thiscontext, "User doesn't exsist!", Toast.LENGTH_SHORT).show();
                        generalprofile_name.setVisibility(View.GONE);
                        generalprofile_username.setVisibility(View.GONE);
                        generalprofile_email.setVisibility(View.GONE);
                        generalprofile_descript.setVisibility(View.VISIBLE);
                        generalprofile_descript.getEditText().setText("USER DOES NOT EXSIST");
                    } catch (JSONException e) {
                        try {
                            String _name = response.getString("name");
                            String _username = response.getString("username");
                            String _email = response.getString("email");
                            String _description = response.getString("about");
                            String _image = response.getString("image");

                            generalprofile_name.getEditText().setText(_name);
                            generalprofile_username.getEditText().setText(_username);
                            generalprofile_email.getEditText().setText(_email);
                            generalprofile_descript.getEditText().setText(_description);

                            String _imageurl = thiscontext.getResources().getString(R.string.base_url_samanImage) + _image;
                            Picasso.get().load(_imageurl).placeholder(R.drawable.iitjammu)
                                    .error(R.drawable.iitjammu)
                                    .into(generalprofile_image);

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            Log.e(TAG, "Some error occured while fetching information..!!");
                        }
                    }
                }
                try {
                    dialog.hide();
                } catch (Exception e) {
                    Log.e(TAG, "Dialog object is not referenced to show before.");
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
                Toast.makeText(thiscontext, "Some error occured while loading.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "error occured while getting user data");
                Log.e(TAG, "Volley requester " + requestType);
                Log.e(TAG, "Volley error===>>>" + "That didn't work!");
                getActivity().onBackPressed();
            }

            @Override
            public void ErrorString(String requestType, String error) {
                dialog.hide();
                Toast.makeText(thiscontext, "Some error occured while Loading...", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Volley requester " + requestType);
                Log.e(TAG, "Volley string error ===>>" + error);
            }
        };
    }
}