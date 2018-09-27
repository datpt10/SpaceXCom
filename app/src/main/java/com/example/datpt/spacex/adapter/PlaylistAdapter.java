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
import com.example.datpt.spacex.inter.InterfacePlaylistCustom;
import com.example.datpt.spacex.item.LikeSong;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder> {

    private Context context;
    private ArrayList arrayList;
    private InterfacePlaylistCustom clickCustom = null;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_playlist, parent, false);

        final MyViewHolder viewHolder = new MyViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCustom.playList(viewHolder.getAdapterPosition());
            }
        });

        return viewHolder;
    }

    public PlaylistAdapter(Context mContext, ArrayList<LikeSong> arrayList, InterfacePlaylistCustom playlistCustom) {
        this.context = mContext;
        this.arrayList = arrayList;
        this.clickCustom = playlistCustom;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView nameBH_list, singer_list;
        public ImageView overFlow;

        public MyViewHolder(View itemView) {
            super(itemView);
            nameBH_list = itemView.findViewById(R.id.tv_tenbaihat_playlist);
            singer_list = itemView.findViewById(R.id.tv_casi_playylist);
            overFlow = itemView.findViewById(R.id.overflow);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        LikeSong likeSong = (LikeSong) arrayList.get(position);
        holder.nameBH_list.setText(likeSong.getName());
        holder.singer_list.setText(likeSong.getSinger());
        holder.overFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(holder.overFlow);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_playlist, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {


        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    Toast.makeText(context, "DELETE ", Toast.LENGTH_LONG).show();

                    return true;
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
