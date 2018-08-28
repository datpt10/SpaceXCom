package com.example.datpt.spacex;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.example.datpt.spacex.Fragment.HomeFragment;
import com.example.datpt.spacex.Fragment.LikeFragment;
import com.example.datpt.spacex.Fragment.PersonFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private ActionBar toolbar;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = getSupportActionBar();
        toolbar.getElevation();
        toolbar.setDisplayUseLogoEnabled(true);

        loadFragment(new HomeFragment());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.setSelectedItemId(R.id.home_nav);

//        attaching bottom sheet behaviour - hide/show on scroll
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());


    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.home_nav:
//                    toolbar.setTitle("Trang Chủ");
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.person_nav:
//                    toolbar.setTitle("Cá Nhân");
                    fragment = new PersonFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.like_nav:
//                    toolbar.setTitle("Yêu Thích");
                    fragment = new LikeFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem itemSearch = menu.findItem(R.id.searchView);
        searchView = (SearchView) itemSearch.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Song");
//                firebaseDatabase.addListenerForSingleValueEvent(valueEventListener);

                Query query1 = FirebaseDatabase.getInstance().getReference("Song")
                        .orderByChild("name");
                query1.addListenerForSingleValueEvent(valueEventListener);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                return false;
            }
        });

        return true;
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                //TODO get the data here

            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


}
