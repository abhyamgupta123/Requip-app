package com.example.requip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class profileFragment extends Fragment {
    private static final String TAG = profileFragment.class.getName();
    public static String sharedpreferencename = "user_status";


    private Context thiscontext;

    private IResult mResultCallback = null;
    private api_methods method;

    ImageView profile_image;
    TextView profile_name;
    TextView profile_username;
    TextView profile_email;
    TextView profile_descript;
    TextView profile_phone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        // getting the context for this fragment:-
        thiscontext = container.getContext();

        profile_image = view.findViewById(R.id.profile_user_image);
        profile_name = view.findViewById(R.id.profile_name);
        profile_username = view.findViewById(R.id.profile_username);
        profile_email = view.findViewById(R.id.profile_email);
        profile_descript = view.findViewById(R.id.profile_description);
        profile_phone = view.findViewById(R.id.profile_phone);

        initVolleyCallback();
        final String base_url = this.getResources().getString(R.string.base_url);
        final String url = base_url + "/profile";

        // calling the desired method:-
        api_methods method = new api_methods(mResultCallback, thiscontext);
        method.userprofile("GETCALL", TAG, url);

        return view;
    }

    void initVolleyCallback(){
        mResultCallback = new IResult() {
            @SuppressLint("SetTextI18n")
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSONObjest post" + response.toString());
                try {
                    String message = response.getString("message");
                    Log.e(TAG, message);
                    profile_name.setVisibility(View.INVISIBLE);
                    profile_username.setVisibility(View.INVISIBLE);
                    profile_email.setVisibility(View.INVISIBLE);
                    profile_phone.setVisibility(View.INVISIBLE);
                    profile_descript.setVisibility(View.VISIBLE);
                    profile_descript.setText("USER DOES NOT EXSIST");
                } catch (JSONException e) {
                    try {
                        String _name = response.getString("name");
                        String _username = response.getString("username");
                        String _email = response.getString("email");
                        String _description = response.getString("about");
                        String _phone = response.getString("phone");
                        String _image = response.getString("image");

                        profile_name.setText("Name : " +_name);
                        profile_username.setText("Username : " + _username);
                        profile_email.setText("Email : " + _email);
                        profile_descript.setText("About : " + _description);
                        profile_phone.setText("Phone : " + _phone);

                        String _imageurl = thiscontext.getResources().getString(R.string.base_url_samanImage) + _image;
                        Picasso.get().load( _imageurl ).placeholder( R.drawable.iitjammu )
                                .error( R.drawable.iitjammu )
                                .into( profile_image );

                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        Log.e(TAG, "Some error occured while fetching information..!!");
                    }
                }
            }

            @Override
            public void notifySuccessArray(String requestType, JSONArray response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSONArray post" + response.toString());
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Toast.makeText(thiscontext, "Some error occured while Loading...", Toast.LENGTH_SHORT).show();
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