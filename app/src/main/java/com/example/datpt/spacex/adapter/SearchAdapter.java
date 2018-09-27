package com.example.datpt.spacex.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.datpt.spacex.R;
import com.example.datpt.spacex.inter.InterfaceSearchCustom;
import com.example.datpt.spacex.item.SongSearch;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    Context context;
    ArrayList<SongSearch> arrayList_Search;
    InterfaceSearchCustom playlistCustom = null;

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_search, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistCustom.playSearch(viewHolder.getAdapterPosition());
            }
        });

        return viewHolder;
    }

    public SearchAdapter(Context mContext, ArrayList<SongSearch> arrayList_Search, InterfaceSearchCustom playlistCustom) {
        this.context = mContext;
        this.arrayList_Search = arrayList_Search;
        this.playlistCustom = playlistCustom;
    }

    public SearchAdapter(ArrayList<SongSearch> arrayList_Search) {
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_tenAlbum, tv_countSong, name;
        public CircularImageView thumbnail;


        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_ten);
            tv_tenAlbum = itemView.findViewById(R.id.tv_tenAlbum);
            tv_countSong = itemView.findViewById(R.id.tv_numOfSong);
            thumbnail = itemView.findViewById(R.id.img_thumnail);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SongSearch songSearch = arrayList_Search.get(position);
        holder.name.setText(songSearch.getName());
        holder.tv_tenAlbum.setText(songSearch.getNameAlbum());
        holder.tv_countSong.setText(songSearch.getNumOfSong() + " songs");
        Glide.with(context).load(songSearch.getThumbnail()).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return arrayList_Search.size();
    }

}
