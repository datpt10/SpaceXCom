package com.example.datpt.spacex.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datpt.spacex.R;
import com.example.datpt.spacex.inter.InterfaceSongClickCustom;
import com.example.datpt.spacex.item.Song;

import java.util.ArrayList;

public class PlayMusicAdapter extends RecyclerView.Adapter<PlayMusicAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Song> arrayList;
    private InterfaceSongClickCustom clickSong = null;


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_playmusic, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSong.onSongClick(arrayList, viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }

    public PlayMusicAdapter(Context mContext, ArrayList<Song> arrayList, InterfaceSongClickCustom songClickCustom) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.clickSong = songClickCustom;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView id, nameBH, nameCasi;
        public ImageView overflow_item;

        public MyViewHolder(final View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.tv_id_item);
            nameBH = itemView.findViewById(R.id.tv_tenbaihat_item);
            nameCasi = itemView.findViewById(R.id.tv_casi_item);
            overflow_item = itemView.findViewById(R.id.overflow_item);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        Song song = arrayList.get(position);
        holder.id.setText(song.getId_song());
        holder.nameBH.setText(song.getName());
        holder.nameCasi.setText(song.getSinger());
        holder.overflow_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(holder.overflow_item);
            }
        });
    }

    public void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_overflow_item, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClick());
        popup.show();
    }

    class MyMenuItemClick implements PopupMenu.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()) {
                case R.id.play:
                    Toast.makeText(mContext, "Play", Toast.LENGTH_LONG).show();
                    break;
                case R.id.addPlaylist:
                    Toast.makeText(mContext, "addPlaylist", Toast.LENGTH_LONG).show();
                    break;
                case R.id.delete:
                    Toast.makeText(mContext, "deleted", Toast.LENGTH_LONG).show();

                    break;
                default:
            }


            return false;
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

}
