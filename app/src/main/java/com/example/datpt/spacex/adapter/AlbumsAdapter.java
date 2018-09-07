package com.example.datpt.spacex.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.datpt.spacex.R;
import com.example.datpt.spacex.inter.InterfaceAlbumCustom;
import com.example.datpt.spacex.item.Album;

import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder> {
    private Context mContext;
    private List<Album> albumsList;
    private InterfaceAlbumCustom clickAlbum = null;


    public AlbumsAdapter(Context mfrHome, List<Album> albumList , InterfaceAlbumCustom interfaceAlbumCustom) {
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
                clickAlbum.onAlbmclick(viewHolder.getPosition());
            }
        });

        return viewHolder;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public View v;
        public TextView name, numOfSong;

        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.title);
            numOfSong = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Album album = albumsList.get(position);
        holder.name.setText(album.getName());
        holder.numOfSong.setText(album.getNumOfSong() + " songs");
//        Loading album cover using Glide library
        Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);
    }


    @Override
    public int getItemCount() {
        return albumsList.size();
    }
}
