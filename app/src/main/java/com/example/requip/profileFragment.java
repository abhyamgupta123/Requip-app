package com.example.requip;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

public class profileFragment extends Fragment {
    private static final String TAG = profileFragment.class.getName();
    public static String sharedpreferencename = "user_status";

    private Context thiscontext;
    private IResult mResultCallback = null;
    private api_methods method;

    // this intenet is for openeing photo select activity:--
    Intent intent;
    String tosend_imageString = "";

    de.hdodenhof.circleimageview.CircleImageView profile_image;
    TextInputLayout profile_name;
    TextInputLayout profile_username;
    TextInputLayout profile_email;
    TextInputLayout profile_descript;
    TextInputLayout profile_phone;
    Button update;

    TextInputEditText p_name;
    TextInputEditText p_username;
    TextInputEditText p_email;
    TextInputEditText p_about;
    TextInputEditText p_phone;
    ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // getting the context for this fragment:-
        thiscontext = container.getContext();

        profile_image = view.findViewById(R.id.profile_user_image);
        profile_name = view.findViewById(R.id.profile_name);
        profile_username = view.findViewById(R.id.profile_username);
        profile_email = view.findViewById(R.id.profile_email);
        profile_descript = view.findViewById(R.id.profile_description);
        profile_phone = view.findViewById(R.id.profile_phone);
        update = view.findViewById(R.id.profile_updatebutton);

        p_name = view.findViewById(R.id.profile_nameedit);
        p_username = view.findViewById(R.id.profile_usernameedit);
        p_email = view.findViewById(R.id.profile_emailedit);
        p_about = view.findViewById(R.id.profile_abouteedit);
        p_phone = view.findViewById(R.id.profile_phoneedit);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _name = profile_name.getEditText().getText().toString();
                String _about = profile_descript.getEditText().getText().toString().trim();
                String _phone = profile_phone.getEditText().getText().toString();

                // setting values if the string is empty so that error is handled if strng is empty:-
                if(_name.isEmpty()){
                    _name = " ";
                }
                if(_about.isEmpty()){
                    _about = " ";
                }
                if(_phone.isEmpty()){
                    _phone = " ";
                }


                final String base_url = thiscontext.getResources().getString(R.string.base_url);
                final String url = base_url + "/edit/profile";
                String profilepic_url = base_url + "/edit/profile/pic";

                method = new api_methods(mResultCallback, thiscontext);
                // Showing progress window:-
                dialog = ProgressDialog.show(thiscontext, "",
                        "Loading. Please wait...", true);
                // now calling the update method:-
                method.userprofile_update("POSTCALL", TAG, url, _name, _about, _phone);
                if(!(tosend_imageString.isEmpty())){
                    Log.e(TAG, "image base64 to send string is -> " + tosend_imageString);
                    method.changeprofilepic("POSTCALLPROFILEPIC", TAG, profilepic_url, tosend_imageString);
                }
            }
        });

        // setting profile change photo on click listener:-
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 7);
            }
        });

        // initialising the callback function for api request listener:-
        initVolleyCallback();
        final String base_url = this.getResources().getString(R.string.base_url);
        final String url = base_url + "/profile";

        // calling the desired method:-
        api_methods method = new api_methods(mResultCallback, thiscontext);
        // Showing progress window:-
        dialog = ProgressDialog.show(thiscontext, "",
                "Loading. Please wait...", true);
        method.userprofile("GETCALL", TAG, url);

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
                        profile_name.setVisibility(View.INVISIBLE);
                        profile_username.setVisibility(View.INVISIBLE);
                        profile_email.setVisibility(View.INVISIBLE);
                        profile_phone.setVisibility(View.INVISIBLE);
                        profile_descript.setVisibility(View.VISIBLE);
                        profile_descript.getEditText().setText("USER DOES NOT EXSIST");
                    } catch (JSONException e) {
                        try {
                            String _name = response.getString("name");
                            String _username = response.getString("username");
                            String _email = response.getString("email");
                            String _description = response.getString("about");
                            String _phone = response.getString("phone");
                            String _image = response.getString("image");

                            p_name.setText(_name);
                            p_username.setText(_username);
                            p_email.setText(_email);
                            p_about.setText(_description);
                            p_phone.setText(_phone);

                            String _imageurl = thiscontext.getResources().getString(R.string.base_url_samanImage) + _image;
                            Picasso.get().load(_imageurl).placeholder(R.drawable.iitjammu)
                                    .error(R.drawable.iitjammu)
                                    .into(profile_image);

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            Log.e(TAG, "Some error occured while fetching information..!!");
                        }
                    }
                    try {
                        dialog.hide();
                    } catch (Exception e) {
                        Log.e(TAG, "Dialog object is not referenced to show before.");
                    }
                } else if (requestType.contains("POSTCALL")) {
                    Log.d(TAG, "Volley requester " + requestType);
                    Log.d(TAG, "Volley JSONObjest post" + response.toString());
                    try {
                        String message = response.getString("message");
                        Log.e(TAG, message);
                        if (message.contains("Information updated successfully")) {
                            Log.d(TAG, "information is updated successfully..!!");
                            Toast.makeText(thiscontext, "Information Updated Successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Log.e(TAG, "information is not updated successfully.");
                            Toast.makeText(thiscontext, "Try again, Some error occured.", 2).show();
                        }

                    } catch (JSONException e) {
                        try {
                            String _name = response.getString("name");
                            String _username = response.getString("username");
                            String _email = response.getString("email");
                            String _description = response.getString("about");
                            String _phone = response.getString("phone");
                            String _image = response.getString("image");

                            profile_name.getEditText().setText(_name);
                            profile_username.getEditText().setText(_username);
                            profile_email.getEditText().setText(_email);
                            profile_descript.getEditText().setText(_description);
                            profile_phone.getEditText().setText(_phone);

                            String _imageurl = thiscontext.getResources().getString(R.string.base_url_samanImage) + _image;
                            Picasso.get().load(_imageurl).placeholder(R.drawable.iitjammu)
                                    .error(R.drawable.iitjammu)
                                    .into(profile_image);

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            Log.e(TAG, "Some error occured while fetching information..!!");
                        }
                    }
                } else if(requestType.equals("POSTCALLPROFILEPIC")) {
                    Log.d(TAG, "Volley requester " + requestType);
                    Log.d(TAG, "Volley JSONObjest post request successfull.");
                    try {
                        String message = response.getString("message");
                        Log.e(TAG, message);
                        if (message.contains("Saved Successfully")) {
                            Toast.makeText(thiscontext, "Image saved successfully", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Image saved successfully");
                        } else {
                            Toast.makeText(thiscontext, "Image not successfully", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Image not saved successfully");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "some error occured at server side while saving profile image");
                        Toast.makeText(thiscontext, "Image is not updated, Try again later!", Toast.LENGTH_LONG).show();
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
                if(requestType.equals("POSTCALL")){
                    try{
                        Log.e(TAG, "value of status code is ===>>>>>" + String.valueOf(error.networkResponse.statusCode));
                        Toast.makeText(thiscontext, "Some error occured Try again", Toast.LENGTH_SHORT).show();
                        if(error.networkResponse.statusCode == 404){
                            Toast.makeText(thiscontext, "User doesn't exsist..!!", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "User doesn't exsist");
                        } else if (error.networkResponse.statusCode == 500){
                            Toast.makeText(thiscontext, "Some internal server error occured, Please try again later.", 3).show();
                        }
                    } catch(Exception e) {
                        Log.e(TAG , "Internet connection is ");
                        Toast.makeText(thiscontext, "Check your internet connection!", Toast.LENGTH_SHORT).show();
                    }
                } else if(requestType.equals("POSTCALLPROFILEPIC")){
                    try{
                        Log.e(TAG, "value of status code is ===>>>>>" + String.valueOf(error.networkResponse.statusCode));
                        if(error.networkResponse.statusCode == 404){
                            Toast.makeText(thiscontext, "User doesn't exsist..!!", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "User doesn't exsist");
                        } else if (error.networkResponse.statusCode == 403){
                            Toast.makeText(thiscontext, "Image size or format is invalid.", 3).show();
                            Log.e(TAG, "server says Image size or format is invalid..!!");
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
                Toast.makeText(thiscontext, "Some error occured while Loading...", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Volley requester " + requestType);
                Log.e(TAG, "Volley string error ===>>" + error);
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case 7:
                if (resultCode == RESULT_OK) {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream;
                    try {
                        imageStream = thiscontext.getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        Bitmap resizedImage = getResizedBitmap(selectedImage, 400, 400);
                        String encodedImage = encodeImage(resizedImage);
                        tosend_imageString = "Image is comming, " + encodedImage;
                        Log.d(TAG, "encoded image string is ==>> " + tosend_imageString);
                        profile_image.setImageURI(imageUri);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(thiscontext, "Image is not loaded due to some error!", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Failed to get imageStream");
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    // to encode the image to base64 string:-
    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    // to resize the seleted image:-
    public static Bitmap getResizedBitmap(Bitmap image, int newHeight, int newWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }
}