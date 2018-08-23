package com.example.datpt.spacex.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.datpt.spacex.R;
import com.example.datpt.spacex.inter.InterfaceSongClickCustom;
import com.example.datpt.spacex.item.Song;
import com.example.datpt.spacex.inter.InterfaceSongClickCustom;
import com.example.datpt.spacex.item.Song;

import java.util.ArrayList;

public class PlayAdapter extends RecyclerView.Adapter<PlayAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList arrayList;
    private InterfaceSongClickCustom clickSong = null;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Gettttt-------------", String.valueOf(+viewHolder.getPosition()));
                // khi click vào item sẽ unhide acti lên và run url
                clickSong.onSongClick(viewHolder.getPosition());
            }
        });
        return viewHolder;
    }


    public PlayAdapter(Context mContext, ArrayList<Song> arrayList, InterfaceSongClickCustom customWhenClickASong) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.clickSong = customWhenClickASong;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nameBH, nameCasi, duration, urlSong;

        public MyViewHolder(View itemView) {
            super(itemView);

            nameBH = (TextView) itemView.findViewById(R.id.tv_tenbaihat);
            nameCasi = (TextView) itemView.findViewById(R.id.tv_casi);
            duration = (TextView) itemView.findViewById(R.id.tv_duration);
            urlSong = (TextView) itemView.findViewById(R.id.tv_urlSong);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Song song = (Song) arrayList.get(position);
        holder.nameBH.setText(song.getName());
        holder.nameCasi.setText(song.getSinger());
        holder.duration.setText(song.getDuration());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

}
