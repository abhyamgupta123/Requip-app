package com.example.requip;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class mypostFragment extends Fragment {
    private static final String TAG = mypostFragment.class.getName();
    public static String sharedpreferencename = "user_status";
    List<saman> usersamanList = new ArrayList<saman>();
    RecyclerView rvusersamanpost;
    saman_adapter samanAdapter;

    // progressbar dialog:-
    ProgressDialog dialog;
    ProgressDialog dialog2;

    boolean swipeflag = false;
    private Context thiscontext;
    private SwipeRefreshLayout swipeContainer;

    private IResult mResultCallback = null;
    private api_methods method;

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
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.mypost_swipeContainer);

        initVolleyCallback();
        // Showing progress window:-
        dialog = ProgressDialog.show(thiscontext, "",
                "Loading. Please wait...", true);
        final String base_url = this.getResources().getString(R.string.base_url);
        final String url = base_url + "/myposts";

        // calling the desired method:-
        method = new api_methods(mResultCallback, thiscontext);
        method.list_user_saman("GETCALL", TAG, url);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                usersamanList.clear();
                swipeflag = true;
                method.list_user_saman("GETCALL", TAG, url);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvusersamanpost.addOnItemTouchListener(new RecyclerTouchListener(thiscontext, rvusersamanpost, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
//                Toast.makeText(thiscontext, "Single Click on position :" + position, Toast.LENGTH_SHORT).show();
                // getting saman id to pass to other acivity:-
                String samanid = usersamanList.get(position).getId();

                // adding values to pass to ther activity:-
                Bundle args = new Bundle();
                args.putString("samanid", samanid);

                Fragment fragment_postedit = new postEditFragment();
                fragment_postedit.setArguments(args);

                // setting the title of toolbar accordingly:-
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.makeinvisible();
                mainActivity.toolbar.setTitle("Edit Post");

                //Values are passing to activity & to fragment as well
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.fraagment_view, fragment_postedit);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }


            @Override
            public void onLongClick(View view, final int position) {
                AlertDialog alertDialog = new AlertDialog.Builder(thiscontext)
                        .setTitle("Delete this post")
                        .setMessage("Are you sure you want to delete this Post ?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog2 = ProgressDialog.show(thiscontext, "Deleting Post", "Please wait...", true);
                                String samanid = usersamanList.get(position).getId();
                                String delete_url = thiscontext.getResources().getString(R.string.base_url);
                                String final_url = delete_url + "/saman/" + samanid;
                                method.delete_saman("DELETE", TAG, final_url);
                                usersamanList.clear();
                                method.list_user_saman("GETCALL", TAG, url);

                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }));

        return view;
    }


//    @Override
//    public void setMenuVisibility(final boolean visible) {
//        super.setMenuVisibility(visible);
//        if (visible) {
//            Log.e(TAG, "yes it is visible=======>>>>>>>>>>>>???????????????????");
//            Toast.makeText(thiscontext, "yes visible again", Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
//            Log.e(TAG, "nope not vsible=======>>>>>>>>>>>>???????????????????");
//            Toast.makeText(thiscontext, "no not", Toast.LENGTH_SHORT).show();
//        }
//    }


    private void refresh() {
        Log.d(TAG, "refreshing the post lists...");
        samanAdapter = new saman_adapter(usersamanList, thiscontext);
        rvusersamanpost.setAdapter(samanAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(thiscontext, 2);
        rvusersamanpost.setLayoutManager(gridLayoutManager);
    }

    void initVolleyCallback() {
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                try {
                    Log.d(TAG, "Volley requester " + requestType);
                    String message = response.getString("message");
                    if (message.contains("You cannot edit this saman")) {
                        Toast.makeText(thiscontext, "You Don't have permission to delete this saman.", Toast.LENGTH_SHORT).show();
                    } else if (message.contains("Your saaman's post has been deleted successfully")) {
                        Toast.makeText(thiscontext, "Deleted Sucessfully!", Toast.LENGTH_SHORT).show();
                    } else if (message.contains("Post is not deleted successfully")) {
                        Toast.makeText(thiscontext, "Try again later, Some unknown error occured.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "error occureed from server side , the error is ==>" + message);
                    } else {
                        Toast.makeText(thiscontext, "Try again later, Some unknown error occured.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Response doesnot contains any desires message parameter. And the response is ==>" + message);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "error occured while decoding respnse message for delete saman method.");
                    e.printStackTrace();
                }
                try {
                    dialog.hide();
                    dialog2.hide();
                    swipeContainer.setRefreshing(false);
                } catch (Exception e) {
                    Log.e(TAG, "Any one of the dialog is null referenced. Handling error");
                }
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
                            String _price = jsonobject.getString("price");
                            String _type = jsonobject.getString("type");
                            String _images = jsonobject.getString("images");

                            usersamanList.add(new saman(_id, _title, _price, _type, _images));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        dialog.hide();
                        dialog2.hide();
                        swipeContainer.setRefreshing(false);
                    } catch (Exception e) {
                        Log.e(TAG, "Any one of the dialog is null referenced. Handling error");
                    }
                    if (swipeflag == true) {
                        samanAdapter.addAll(usersamanList);
                        samanAdapter.notifyDataSetChanged();
                        swipeContainer.setRefreshing(false);
                        Toast.makeText(thiscontext, "Refreshed successfully....", Toast.LENGTH_SHORT).show();
                    } else {
                        refresh();
                    }

                } catch (Exception e2) {
                    try {
                        dialog.hide();
                        dialog2.hide();
                    } catch (Exception e) {
                        Log.e(TAG, "Any one of the dialog is null referenced. Handling error");
                    }
                    Toast.makeText(thiscontext, "Something went wrong.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Response is not in form of jsonarray");
                    Log.d(TAG, "error reason is -> " + e2);
                }

                try {
                    dialog.hide();
                    dialog2.hide();
                    swipeContainer.setRefreshing(false);
                } catch (Exception e) {
                    Log.e(TAG, "Any one of the dialog is null referenced. Handling error");
                }
                Log.d(TAG, "Volley requester " + requestType);
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                try {
                    dialog.hide();
                    dialog2.hide();
                    swipeContainer.setRefreshing(false);
                } catch (Exception e) {
                    Log.e(TAG, "Any one of the dialog is null referenced. Handling error");
                }
                if(error.toString().contains("com.android.volley.NoConnectionError")){
                    Toast.makeText(thiscontext, "Couldn't able to connect to server, Check your internet connection.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Couldn't able to connect to server, internet connection is not up");
                    try{
                        swipeContainer.setRefreshing(false);
                    } catch (Exception e) {
                        Log.e(TAG, "swipecontainer is not active");
                    }
                }
                try{
                    // handline auth fail error:-
                    if (error.networkResponse.statusCode != 401){
                        Toast.makeText(thiscontext, "Some error occured while Loading...", Toast.LENGTH_SHORT).show();
                    } else{
                        Log.e(TAG, "Maybe Internet connection is not available, or something else problem occured.");
                    }
                } catch(Exception e) {
                    Log.e(TAG , "Internet connection is ");
                    Toast.makeText(thiscontext, "Check your internet connection!", Toast.LENGTH_SHORT).show();
                }
                try{
                    swipeContainer.setRefreshing(false);
                } catch (Exception e) {
                    Log.e(TAG, "swipecontainer is not active");
                }
                Log.e(TAG, "Volley requester " + requestType);
                Log.e(TAG, "Volley error===>>>" + "That didn't work!");
            }

            @Override
            public void ErrorString(String requestType, String error) {
                try {
                    dialog.hide();
                    dialog2.hide();
                    swipeContainer.setRefreshing(false);
                } catch (Exception e) {
                    Log.e(TAG, "Any one of the dialog is null referenced. Handling error");
                }
                Log.e(TAG, "Volley requester " + requestType);
                Log.e(TAG, "Volley string error ===>>" + error);
            }
        };
    }

    // this part of code is to set onsingle click or or longclick listener on items of recycleview:-
    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }


    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener) {

            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clicklistener != null) {
                        clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
                clicklistener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}