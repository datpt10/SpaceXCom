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

import java.util.ArrayList;

public class ActivityPlayAdapter extends RecyclerView.Adapter<ActivityPlayAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList arrayList;
    private InterfaceSongClickCustom clickSong = null;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Gettttt-------------", String.valueOf(+viewHolder.getPosition()));
                // khi click vào item sẽ unhide acti lên và run url
                clickSong.onSongClick(viewHolder.getAdapterPosition());

            }
        });
        return viewHolder;
    }


    public ActivityPlayAdapter(Context mContext, ArrayList<Song> arrayList, InterfaceSongClickCustom customWhenClickASong) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.clickSong = customWhenClickASong;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView id, nameBH, nameCasi;

        public MyViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.tv_id);
            nameBH = itemView.findViewById(R.id.tv_tenbaihat);
            nameCasi = itemView.findViewById(R.id.tv_casi);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Song song = (Song) arrayList.get(position);
        holder.id.setText(song.getId_song());
        holder.nameBH.setText(song.getName());
        holder.nameCasi.setText(song.getSinger());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

}
