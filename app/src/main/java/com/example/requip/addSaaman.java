package com.example.requip;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class addSaaman extends AppCompatActivity {

    private static final String TAG = addSaaman.class.getName();
    public static String sharedpreferencename = MainActivity.sharedpreferencename;

    private TextInputLayout title;
    private TextInputLayout description;
    private TextInputLayout price;
    private TextInputLayout samanType;
    private TextInputLayout phone;

    IResult mResultCallback = null;
    api_methods method;

    // this intenet is for openeing photo select activity:-
    Intent intent;
    String tosend_imageString = "";

    // progressbar dialog:-
    ProgressDialog dialog;

    ImageView image;
    Button image_select;
    Button add_saman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_saaman);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // initialising the callback listener:-
        initVolleyCallback();


        // registering the views.
        image_select = findViewById(R.id.select_saman_image);
        image = findViewById(R.id.saman_image);
        add_saman = findViewById(R.id.addsaman);
        title = findViewById(R.id.saman_title);
        description = findViewById(R.id.saman_description);
        price = findViewById(R.id.saman_price);
        samanType = findViewById(R.id.saman_type);
        phone = findViewById(R.id.saman_phone);

        image.setVisibility(View.INVISIBLE);

        image_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 7);
            }
        });

        add_saman.setOnClickListener(new View.OnClickListener() {
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
                if(tosend_imageString.isEmpty()){
                    Toast.makeText(addSaaman.this, "Please select your Saman's Image", Toast.LENGTH_LONG).show();
                }

                if(!(_title.isEmpty() || _description.isEmpty() || _price.isEmpty() || _type.isEmpty() || _phone.isEmpty() || tosend_imageString.isEmpty())){
                    final String base_url = getResources().getString(R.string.base_url);
                    final String url = base_url + "/add";

                    // calling the desired method:-
                    method = new api_methods(mResultCallback, addSaaman.this);
                    // Showing progress window:-
                    dialog = ProgressDialog.show(addSaaman.this, "",
                            "Loading. Please wait...", true);
                    method.addsaman("POSTCALL", TAG, url, _title, _price, _type, _description, _phone, tosend_imageString);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case 7:
                if (resultCode == RESULT_OK) {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream;
                    try {
                        imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        Bitmap resizedImage = getResizedBitmap(selectedImage, 700, 700);
                        String encodedImage = encodeImage(resizedImage);
                        tosend_imageString = "Image is comming, " + encodedImage;
                        Log.d(TAG, "encoded image string is ==>> " + tosend_imageString);
                        image.setVisibility(View.VISIBLE);
                        image.setImageURI(imageUri);
                        image_select.setVisibility(View.INVISIBLE);
                        image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("*/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent, 7);
                            }
                        });
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, "Failed to get imageStream");
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

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

    void initVolleyCallback(){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                dialog.hide();
                try {
                    String message = response.getString("message");
                    Log.d(TAG, "response message is -> " + message);
                    if(message.contains("new post of saaman is created successfully")){
                        Log.d(TAG, "Post of saaman created succesfully.!!");
                        Toast.makeText(addSaaman.this, "Post created successfully..!!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else{
                        Toast.makeText(addSaaman.this, "Post created successfully..!!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Some error occureed, and response does not contains the message parameter maybe look for response message");
                        Log.e(TAG, "response here we get is -> " + response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(addSaaman.this, "Some error occured, Try again please after some time", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "some error occured while decoding the message parameter, see log");
                    Log.e(TAG, "response here we get is -> " + response);
                }
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSONObjest post successfull to add saman.");
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
                Log.e(TAG, "value of status code is ===>>>>>" + String.valueOf(error.networkResponse.statusCode));
                Toast.makeText(addSaaman.this, "Some error occured while Logging in...", Toast.LENGTH_SHORT).show();
                if(error.networkResponse.statusCode == 404){
                    Toast.makeText(addSaaman.this, "User doesn't exsist..!!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "User doesn't exsist");
                } else if (error.networkResponse.statusCode == 500){
                    Toast.makeText(addSaaman.this, "Some internal server error occured, Please try again later.", Toast.LENGTH_LONG).show();
                }
                Log.e(TAG, "Volley requester " + requestType);
                Log.e(TAG, "Volley error===>>>" + "That didn't work!");
            }

            @Override
            public void ErrorString(String requestType, String error) {
                dialog.hide();
                Log.e(TAG, "response object is not jsonobject type myabe cause there is error happened in encoding the message , maybe the message is not recieved sucessfully");
                Log.e(TAG, "please check the log to trace back the error");
                Log.e(TAG, "Volley requester " + requestType);
                Log.e(TAG, "Volley string error ===>>" + error);
            }
        };
    }
}