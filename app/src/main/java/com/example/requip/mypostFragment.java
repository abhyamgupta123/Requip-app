package com.example.requip;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link mypostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class mypostFragment extends Fragment {
    private static final String TAG = mypostFragment.class.getName();
    public static String sharedpreferencename = "user_status";


    private Context thiscontext;

    private IResult mResultCallback = null;
    private api_methods method;

    List<saman> usersamanList = new ArrayList<saman>();
    RecyclerView rvusersamanpost;
    // TODO: Rename and change types of parameters


    public mypostFragment() {
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
        final View view = inflater.inflate(R.layout.fragment_mypost, container, false);

        // getting the context for this fragment:-
        thiscontext = container.getContext();

        rvusersamanpost = (RecyclerView) view.findViewById(R.id.rvusersaman);

        initVolleyCallback();
        final String base_url = this.getResources().getString(R.string.base_url);
        final String url = base_url + "/saman/user";

        // calling the desired method:-
        api_methods method = new api_methods(mResultCallback, thiscontext);
        method.list_user_saman("GETCALL", TAG, url);
        return view;
    }

    private void refresh(){
        Log.d( TAG, "refreshing the post lists..." );
        saman_adapter samanAdapter = new saman_adapter( usersamanList, thiscontext);
        rvusersamanpost.setAdapter( samanAdapter );

        GridLayoutManager gridLayoutManager = new GridLayoutManager(thiscontext, 2 );
        rvusersamanpost.setLayoutManager( gridLayoutManager );
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
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonobject = null;
                        try {
                            jsonobject = response.getJSONObject(i);
                            String _id = jsonobject.getString("_id");
                            String _title = jsonobject.getString("title");
                            String _username = jsonobject.getString("username");
                            String _price = jsonobject.getString("price");
                            String _type = jsonobject.getString("type");
                            String _description = jsonobject.getString("description");
                            String _phone = jsonobject.getString("phone");
                            String _images = jsonobject.getString("images");

                            usersamanList.add(new saman(_id, _title, _username, _price, _type, _description, _phone, _images));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    refresh();
                    Log.d(TAG, "respose testing ========>>>>>>>>>>" + response.toString());

                } catch (Exception e2 ) {
                    Log.e(TAG, "Response is not in form of jsonarray");
                    Log.d(TAG, "error reason is -> " + e2);

                }
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