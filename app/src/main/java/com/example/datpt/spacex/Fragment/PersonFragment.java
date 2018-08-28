package com.example.datpt.spacex.Fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.datpt.spacex.R;
import com.example.datpt.spacex.adapter.SlideAdapter;

import java.util.Timer;
import java.util.TimerTask;


public class PersonFragment extends Fragment {

    private static final int NUM_PAGES = 4;
    private ViewPager viewPager;

    private SlideAdapter adapter;

    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions

    public PersonFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);

        adapter = new SlideAdapter(getContext());

        viewPager.setAdapter(adapter);


        /*After setting the adapter use the timer */
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                if (currentPage == NUM_PAGES - 1) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() {
                           @Override
                           public void run() {
                               handler.post(update);
                           }
                       }, DELAY_MS, PERIOD_MS
        );


        return view;
    }

}
