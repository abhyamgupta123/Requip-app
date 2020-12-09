package com.example.requip;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class chatmain extends Fragment {
    private static final String TAG = chatmain.class.getName();
    public static String sharedpreferencename = "user_status";

    private Context thiscontext;


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
        return view;
    }
}