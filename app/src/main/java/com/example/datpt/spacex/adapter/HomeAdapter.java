package com.example.datpt.spacex.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.datpt.spacex.R;
import com.example.datpt.spacex.inter.InterfaceAlbumCustom;
import com.example.datpt.spacex.item.Album;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
    private Context mContext;
    private List<Album> albumsList;
    private InterfaceAlbumCustom clickAlbum = null;


    public HomeAdapter(Context mfrHome, List<Album> albumList, InterfaceAlbumCustom interfaceAlbumCustom) {
        this.mContext = mfrHome;
        this.albumsList = albumList;
        this.clickAlbum = interfaceAlbumCustom;
    }

    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_card, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAlbum.onAlbmclick(viewHolder.getAdapterPosition());
            }
        });

        return viewHolder;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name, numOfSong;

        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.title);
            numOfSong =  view.findViewById(R.id.count);
            thumbnail =  view.findViewById(R.id.thumbnail);
        }

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Album album = albumsList.get(position);
        holder.name.setText(album.getNameAlbum());
        holder.numOfSong.setText(album.getNumOfSong() + " songs");
//        Loading album cover using Glide library
        Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);
    }


    @Override
    public int getItemCount() {
        return albumsList.size();
    }
}
