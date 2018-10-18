package com.example.datpt.spacex.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.datpt.spacex.R;
import com.example.datpt.spacex.adapter.PlayMusicAdapter;
import com.example.datpt.spacex.inter.InterfaceSongClickCustom;
import com.example.datpt.spacex.item.Song;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class PlayMusicFragment extends Fragment {
    private DatabaseReference mData;
    private FirebaseDatabase mFirebaseDatabase;
    private RecyclerView view_Playlist;
    private PlayMusicAdapter playAdapter;
    private ArrayList<Song> arrayList;
    private static String mSinger = "";
    private boolean isSinger = false;

    private InterfaceSongClickCustom songClickCustom;


    public PlayMusicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playmusic_list, container, false);

        viewControl(view);

        if (isSinger) {
            isSinger = false;
            Bundle bundle = getArguments();
            mSinger = bundle.getString("name");
        } else {
            isSinger = true;
            mSinger = getArguments().getString("name");
        }
        view_Playlist.setHasFixedSize(true);
        view_Playlist.setLayoutManager(new LinearLayoutManager(getActivity()));
        arrayList = new ArrayList<>();
        view_Playlist.setItemAnimator(new DefaultItemAnimator());
        playAdapter = new PlayMusicAdapter(getActivity(), arrayList, (InterfaceSongClickCustom) getActivity());
        view_Playlist.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        view_Playlist.setAdapter(playAdapter);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mData = mFirebaseDatabase.getReference("Song").child(mSinger);

        showData();

        return view;
    }

    public void viewControl(View view) {
        view_Playlist = view.findViewById(R.id.recycler_view_list);
    }

    private void showData() {
        mData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Song song = dataSnapshot.getValue(Song.class);
                arrayList.add(song);
                playAdapter.notifyDataSetChanged();
                Log.d("SONG", String.valueOf(arrayList.size()));
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InterfaceSongClickCustom) {
            songClickCustom = (InterfaceSongClickCustom) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        songClickCustom = null;
    }

}
