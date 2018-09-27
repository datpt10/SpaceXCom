package com.example.datpt.spacex.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.datpt.spacex.PlaylistMusic;
import com.example.datpt.spacex.R;
import com.example.datpt.spacex.adapter.SlideAdapter;

import java.util.Timer;
import java.util.TimerTask;


public class PersonFragment extends Fragment implements View.OnClickListener {

    private static final int NUM_PAGES = 4;
    private ViewPager viewPager;

    private SlideAdapter adapter;

    private RelativeLayout re_login, re_home, re_song, re_settings, re_about;

    private TextView tv_about_show;


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
        viewPager = view.findViewById(R.id.viewPager);

        tv_about_show = view.findViewById(R.id.tv_about_show);

        re_login = view.findViewById(R.id.re_login);
        re_home = view.findViewById(R.id.re_home);
        re_song = view.findViewById(R.id.re_song);
        re_settings = view.findViewById(R.id.re_setting);
        re_about = view.findViewById(R.id.re_about);

        re_login.setOnClickListener(this);
        re_home.setOnClickListener(this);
        re_song.setOnClickListener(this);
        re_settings.setOnClickListener(this);
        re_about.setOnClickListener(this);


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

    @Override
    public void onClick(View v) {

        Fragment fragment = null;

        switch (v.getId()) {
            case R.id.re_login:
                //login

                break;
            case R.id.re_home:
                fragment = new HomeFragment();
                loadFragment(fragment);
                break;
            case R.id.re_song:
                Intent intent = new Intent(getActivity(), PlaylistMusic.class);
                startActivity(intent);
                break;
            case R.id.re_setting:

                break;
            case R.id.re_about:

                tv_about_show.setText("Develop by ThanhDat" + "\n"
                        + "Email: thanhdatctu@gmail.com");
                break;

        }

    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
