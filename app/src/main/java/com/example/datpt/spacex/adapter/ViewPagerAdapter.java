package com.example.datpt.spacex.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.datpt.spacex.Fragment.HomeFragment;
import com.example.datpt.spacex.Fragment.LikeFragment;
import com.example.datpt.spacex.Fragment.PersonFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                PersonFragment personFragment = new PersonFragment();
                return personFragment;
            case 1:
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 2:
                LikeFragment likeFragment = new LikeFragment();
                return likeFragment;
        }
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
    }
}
