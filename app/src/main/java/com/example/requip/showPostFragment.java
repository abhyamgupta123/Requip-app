package com.example.requip;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class showPostFragment extends Fragment {
    private static final String TAG = showPostFragment.class.getName();
    public static String sharedpreferencename = "user_status";

    private IResult mResultCallback = null;
    private api_methods method;
    private Context thiscontext;

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
        return view;
    }
}