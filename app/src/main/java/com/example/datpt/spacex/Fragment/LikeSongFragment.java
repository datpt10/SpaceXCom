package com.example.datpt.spacex.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.datpt.spacex.R;
import com.example.datpt.spacex.adapter.PlaylistAdapter;
import com.example.datpt.spacex.inter.InterfacePlaylistCustom;
import com.example.datpt.spacex.item.LikeSong;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class LikeSongFragment extends Fragment {
    DatabaseReference mData;
    FirebaseDatabase mFirebaseDatabase;
    RecyclerView recyclerView;
    PlaylistAdapter adapter;
    ArrayList<LikeSong> arrayList = new ArrayList<>();

    public LikeSongFragment() {
        //contructor

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_playlist_music, container, false);

        recyclerView = view.findViewById(R.id.rec_list);
        adapter = new PlaylistAdapter(getActivity(), arrayList, (InterfacePlaylistCustom) getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        showList();
        return view;
    }
    private void showList() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mData = mFirebaseDatabase.getReference("LikeSong");
        mData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                LikeSong likeSong = dataSnapshot.getValue(LikeSong.class);
                arrayList.add(likeSong);

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

    }
}
