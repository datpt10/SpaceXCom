package com.example.datpt.spacex;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.datpt.spacex.Fragment.HomeFragment;
import com.example.datpt.spacex.Fragment.LikeFragment;
import com.example.datpt.spacex.Fragment.PersonFragment;
import com.example.datpt.spacex.adapter.SearchAdapter;
import com.example.datpt.spacex.inter.InterfaceSearchCustom;
import com.example.datpt.spacex.item.SongSearch;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements InterfaceSearchCustom {

    RecyclerView recyclerView;
    TextView tv_noData;
    ArrayList<SongSearch> arrayList;
    SearchAdapter adapter;
    SearchView searchView;

    //    private ActionBar toolbar;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mData;


    FirebaseRecyclerAdapter<SongSearch, SearchAdapter.MyViewHolder> firebaseRecyclerAdapter;


    int position;
    ServicePlayBackgound servicePlayBackgound;
    boolean isService = false;

    Toolbar toolbar;

    TextView tv_nameBH_show, tv_singer_show;
    ImageButton btn_previous_show, btn_play_pause_show, btn_next_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        view();

        loadFragment(new HomeFragment());
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.home_nav);

        arrayList = new ArrayList<>();
        adapter = new SearchAdapter(getApplicationContext(), arrayList, this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(true);
        searchView.setMaxWidth(Integer.MAX_VALUE);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mData = mFirebaseDatabase.getReference("Album");

        mData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                SongSearch songSearch = dataSnapshot.getValue(SongSearch.class);

                arrayList.add(songSearch);

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return true;
            }
        });

    }

    private void firebaseSearch(String searchText) {

        mData = mFirebaseDatabase.getReference();
        Query query = mData.child("Album").orderByChild("name").equalTo(searchText);

        FirebaseRecyclerOptions<SongSearch> options =
                new FirebaseRecyclerOptions.Builder<SongSearch>()
                        .setQuery(query, SongSearch.class)
                        .setLifecycleOwner(this)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<SongSearch, SearchAdapter.MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SearchAdapter.MyViewHolder holder, int position, @NonNull SongSearch model) {
                holder.name.setText(model.getName());
                holder.tv_tenAlbum.setText(model.getNameAlbum());
                holder.tv_countSong.setText(model.getNumOfSong() + " songs ");
                Glide.with(getApplicationContext()).load(model.getThumbnail()).into(holder.thumbnail);
            }

            @NonNull
            @Override
            public SearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_search, parent, false);
                final SearchAdapter.MyViewHolder viewHolder = new SearchAdapter.MyViewHolder(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playSearch(viewHolder.getAdapterPosition());
                    }
                });
                return viewHolder;
            }
        };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        recyclerView.setVisibility(View.VISIBLE);

    }

    private void view() {
        recyclerView = findViewById(R.id.recycler_view_search);
        tv_noData = findViewById(R.id.tv_nodata);
        searchView = findViewById(R.id.searchView);

        //play_mp3_show
        tv_nameBH_show = findViewById(R.id.tv_nameSong_show);
        tv_singer_show = findViewById(R.id.tv_singer_show);
        btn_previous_show = findViewById(R.id.img_previous_show);
        btn_play_pause_show = findViewById(R.id.img_play_pause_show);
        btn_next_show = findViewById(R.id.img_next_show);
    }

    //
//    private void controlSong() {
//        //control music
//        btn_play_pause_show.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (servicePlayBackgound.isPlaying()) {
//                    servicePlayBackgound.pause();
//                    btn_play_pause_show.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
//                } else {
//                    servicePlayBackgound.start();
//                    btn_play_pause_show.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
//                }
//            }
//        });
//        btn_next_show.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                position++;
//                if (position > arrayList.size() - 1) {
//                    position = 0;
//                }
//                if (servicePlayBackgound.isPlaying()) {
//                    servicePlayBackgound.stop();
//                }
//
//            }
//        });
//        btn_previous_show.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                position--;
//                if (position < 0) {
//                    position = arrayList.size() - 1;
//                }
//                if (servicePlayBackgound.isPlaying()) {
//                    servicePlayBackgound.stop();
//                }
//                btn_play_pause_show.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_48dp);
//            }
//        });
//
//    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
            searchView.clearFocus();
            switch (item.getItemId()) {
                case R.id.home_nav:
                    fragment = new HomeFragment();
                    recyclerView.setVisibility(View.GONE);
                    loadFragment(fragment);
                    return true;
                case R.id.person_nav:
                    fragment = new PersonFragment();
                    recyclerView.setVisibility(View.GONE);
                    loadFragment(fragment);
                    return true;
                case R.id.like_nav:
                    fragment = new LikeFragment();
                    recyclerView.setVisibility(View.GONE);
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
//        searchView.setQuery("", false);
//        rootView.requestFocus();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        searchView.clearFocus();
        recyclerView.setVisibility(View.GONE);


    }

    @Override
    public void playSearch(int position) {
        String nameAlbum = String.valueOf(arrayList.get(position).getNameAlbum());
        Log.d("NAME LAAAAA ----------", nameAlbum);
        Intent intent = new Intent(getApplicationContext(), ActivityPlay.class);
        intent.putExtra("name", nameAlbum);
        startActivity(intent);
    }
}
