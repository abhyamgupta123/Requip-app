package com.example.requip;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class homeFragment extends Fragment {
    private static final String TAG = homeFragment.class.getName();
    public static String sharedpreferencename = "user_status";

    private Context thiscontext;
    private SwipeRefreshLayout swipeContainer;

    // flag to check if instance is created for 1st time or resumed;-
    private boolean onresume = false;

    IResult mResultCallback = null;
    api_methods method;

    List<saman> samanList = new ArrayList<saman>();
    RecyclerView rvsamanpost;
    saman_adapter samanAdapter;

    // progressbar dialog:-
    ProgressDialog dialog;

    boolean swipeflag = false;

    // setting this for enable lazy load:-
    JSONArray totaljsonarray;
    int lastIndex;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view  = inflater.inflate(R.layout.fragment_home, container, false);

        // getting the context for this fragment:-
        thiscontext = container.getContext();

        rvsamanpost = (RecyclerView) view.findViewById(R.id.rvsaman);
//        rvsamanpost.setItemAnimator(null);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.home_swipeContainer);

        initVolleyCallback();

        // Showing progress window:-
        dialog = ProgressDialog.show(thiscontext, "",
                "Loading. Please wait...", true);
        final String base_url = this.getResources().getString(R.string.base_url);
        String url = base_url + "/saman?type=papers,books,notes,others";

        try{
            String flag = getArguments().getString("flag");
            if (flag.contains("search")){
                String query = getArguments().getString("text");
                url = url + "&text=" + query;
            }
        } catch(Exception e){
            Log.e(TAG, "args value doesn't contains any flags this time");
            Log.e(TAG, "error occured and handled successfully");
        }


        // calling the desired method:-
        method = new api_methods(mResultCallback, thiscontext);
        method.list_saman("GETCALL", TAG, url);


        final String finalUrl = url;
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                samanList.clear();
                swipeflag = true;
                method.list_saman("GETCALL", TAG, finalUrl);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvsamanpost.addOnItemTouchListener(new homeFragmentRecyclerTouchListener(thiscontext, rvsamanpost, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                //Values are passing to activity & to fragment as well
//                Toast.makeText(thiscontext, "saman id is " + samanList.get(position).getId(), Toast.LENGTH_SHORT).show();
                swipeflag = true;

                // adding values to pass to ther activity:-
                Bundle args = new Bundle();
                args.putString("samanid", samanList.get(position).getId());
                args.putString("sugesstion_type", samanList.get(position).getType());

                Fragment showpostfragment = new showPostFragment();
                showpostfragment.setArguments(args);

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.fraagment_view, showpostfragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

            @Override
            public void onLongClick(View view, int position) {
//                Toast.makeText(thiscontext, "Long press on position :"+ position, Toast.LENGTH_LONG).show();
//                Toast.makeText(thiscontext, "the price is " + samanList.get(position).getId(), Toast.LENGTH_SHORT).show();
            }
        }));

        // litener for when the recycleview reaches the end:-
        rvsamanpost.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)  && newState==RecyclerView.SCROLL_STATE_IDLE ) {
                    addSamanOnEndReached();
                }
            }
        });

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

    private void refresh(){
        Log.d( TAG, "refreshing the post lists..." );
        samanAdapter = new saman_adapter( samanList, thiscontext);
        rvsamanpost.setAdapter( samanAdapter );

        GridLayoutManager gridLayoutManager = new GridLayoutManager(thiscontext, 2 );
        rvsamanpost.setLayoutManager( gridLayoutManager );
    }

    void initVolleyCallback(){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                try {
                    dialog.hide();
                    swipeContainer.setRefreshing(false);
                } catch (Exception e) {
                    Log.e(TAG, "Any one of the dialog is null referenced. Handling error");
                }
            }

            @Override
            public void notifySuccessArray(String requestType, JSONArray response) {
                try {
                    dialog.hide();
                    swipeContainer.setRefreshing(false);
                } catch (Exception e) {
                    Log.e(TAG, "Any one of the dialog is null referenced. Handling error");
                }
                try {
                    totaljsonarray = response;
                    if (response.length() >6){
                        for (int i = 0; i < 6; i++) {
                            JSONObject jsonobject = null;
                            try {
                                jsonobject = response.getJSONObject(i);
                                String _id = jsonobject.getString("_id");
                                String _title = jsonobject.getString("title");
                                String _price = jsonobject.getString("price");
                                String _type = jsonobject.getString("type");
                                String _images = jsonobject.getString("images");

                                samanList.add(new saman(_id, _title, _price, _type, _images));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        lastIndex = 6;
                    } else if (response.length() <= 6){
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonobject = null;
                            try {
                                jsonobject = response.getJSONObject(i);
                                String _id = jsonobject.getString("_id");
                                String _title = jsonobject.getString("title");
                                String _price = jsonobject.getString("price");
                                String _type = jsonobject.getString("type");
                                String _images = jsonobject.getString("images");

                                samanList.add(new saman(_id, _title, _price, _type, _images));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        lastIndex = -1;
                    }
                    if (swipeflag == true){
                        samanAdapter.addAll(samanList);
                        samanAdapter.notifyDataSetChanged();
                        swipeContainer.setRefreshing(false);
                        Toast.makeText(thiscontext, "Refreshed successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        refresh();
                    }
                } catch (Exception e2 ) {
                    dialog.hide();
                    Toast.makeText(thiscontext, "Something went wrong.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Response is not in form of jsonarray");
                    Log.e(TAG, "error reason is -> " + e2);
                    swipeContainer.setRefreshing(false);
                }
                try {
                    dialog.hide();
                    swipeContainer.setRefreshing(false);
                } catch (Exception e) {
                    Log.e(TAG, "Any one of the dialog is null referenced. Handling error");
                }
                swipeContainer.setRefreshing(false);
                Log.d(TAG, "Volley requester " + requestType);
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                try {
                    dialog.hide();
                    swipeContainer.setRefreshing(false);
                } catch (Exception e) {
                    Log.e(TAG, "Any one of the dialog is null referenced. Handling error");
                }
                Toast.makeText(thiscontext, "Some error occured while Loading...", Toast.LENGTH_SHORT).show();
                Toast.makeText(thiscontext, "Check your internet conection..!!", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Volley requester " + requestType);
                Log.e(TAG, "Volley error===>>>" + "That didn't work!");
            }

            @Override
            public void ErrorString(String requestType, String error) {
                try {
                    dialog.hide();
                    swipeContainer.setRefreshing(false);
                } catch (Exception e) {
                    Log.e(TAG, "Any one of the dialog is null referenced. Handling error");
                }
                Log.e(TAG, "Volley requester " + requestType);
                Log.e(TAG, "Volley string error ===>>" + error);
            }
        };
    }

    private void addSamanOnEndReached(){
        if(lastIndex != -1){
            if (totaljsonarray.length() >= 14){
                for (int i = lastIndex; i <lastIndex+8 ; i++) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = totaljsonarray.getJSONObject(i);
                        String _id = jsonobject.getString("_id");
                        String _title = jsonobject.getString("title");
                        String _price = jsonobject.getString("price");
                        String _type = jsonobject.getString("type");
                        String _images = jsonobject.getString("images");

                        samanList.add(new saman(_id, _title, _price, _type, _images));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                samanAdapter.notifyDataSetChanged();
                lastIndex = lastIndex + 8;
            } else{
                for (int i = lastIndex; i <totaljsonarray.length() ; i++) {
                    JSONObject jsonobject = null;
                    try {
                        jsonobject = totaljsonarray.getJSONObject(i);
                        String _id = jsonobject.getString("_id");
                        String _title = jsonobject.getString("title");
                        String _price = jsonobject.getString("price");
                        String _type = jsonobject.getString("type");
                        String _images = jsonobject.getString("images");

                        samanList.add(new saman(_id, _title, _price, _type, _images));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    samanAdapter.notifyDataSetChanged();
                }
                lastIndex = -1;
            }
        }
    }

    // this part of code is to set onsingle click or or longclick listener on items of recycleview:-
    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }


    static class homeFragmentRecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public homeFragmentRecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener) {

            this.clicklistener = (ClickListener) clicklistener;
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