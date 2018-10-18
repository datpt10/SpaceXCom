package com.example.datpt.spacex.Fragment;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.datpt.spacex.R;
import com.example.datpt.spacex.adapter.HomeAdapter;
import com.example.datpt.spacex.inter.InterfaceAlbumCustom;
import com.example.datpt.spacex.item.Album;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements InterfaceAlbumCustom {
    DatabaseReference mData;
    FirebaseDatabase mFirebaseDatabase;
    RecyclerView recyclerView;
    HomeAdapter adapter;
    List<Album> albumList;


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        albumList = new ArrayList<>();

        adapter = new HomeAdapter(getActivity(), albumList, this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addItemDecoration(new HomeFragment.GridSpacingItemDecoration(2, dpTopx(1), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(true);


        prepareAlbum();

        return view;
    }

    private void prepareAlbum() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mData = mFirebaseDatabase.getReference("Album");
        mData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Album album = dataSnapshot.getValue(Album.class);
                albumList.add(album);

                Log.d(" ALBUM LIST  - - - ", String.valueOf(albumList.size()));

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

    @Override
    public void onAlbmclick(int position) {
        String nameAlbum = String.valueOf(albumList.get(position).getNameAlbum());
        PlayMusicFragment fragment = new PlayMusicFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", nameAlbum);
        fragment.setArguments(bundle);
        FragmentTransaction manager = getFragmentManager().beginTransaction();
        manager.replace(R.id.frame_container, fragment);
        manager.addToBackStack(null);
        manager.commit();

        Log.d("Namee----", nameAlbum);
    }

    //    RecyclerView item decoration - give equal margin around grid item
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {

            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount; //item column
            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;// spacing - column * ((1f / spanCount)* spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * (( 1f/spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;

                }
                outRect.bottom = spacing; // item bottom

            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;

                if (position >= spanCount) {
                    outRect.top = spacing;
                }

            }
        }

    }

    private int dpTopx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


}
