package com.example.datpt.spacex.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.example.datpt.spacex.FeedImageView;
import com.example.datpt.spacex.LikeController;
import com.example.datpt.spacex.R;
import com.example.datpt.spacex.item.Like;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.MyViewHolder> {
    private Context context;
    private ArrayList likeList;
    ImageLoader imageLoader = LikeController.getmController().getmImageLoader();

    public LikeAdapter(Context context, ArrayList<Like> likeList) {
        this.context = context;
        this.likeList = likeList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_like, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        //click item as position

        return viewHolder;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, timestamp, statusMsg, url;
        CircularImageView profilePic;
        FeedImageView feedImageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            statusMsg = (TextView) itemView.findViewById(R.id.txtStatusMsg);
            url = (TextView) itemView.findViewById(R.id.txtUrl);
            profilePic = (CircularImageView) itemView.findViewById(R.id.profilePic);
            feedImageView = (FeedImageView) itemView.findViewById(R.id.feedImage1);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        if (imageLoader == null)
//            imageLoader = LikeController.getmController().getmImageLoader();
        Like like = (Like) likeList.get(position);

        holder.name.setText(like.getName());

        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(like.getTimeStamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        holder.timestamp.setText(timeAgo);

        // Chcek for empty status message
        if (!TextUtils.isEmpty(like.getStatus())) {
            holder.statusMsg.setText(like.getStatus());
            holder.statusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            holder.statusMsg.setVisibility(View.GONE);
        }
        // Checking for null feed url
        if (like.getUrl() != null) {
            holder.url.setText(Html.fromHtml("<a href=\"" + like.getUrl() + "\">"
                    + like.getUrl() + "</a> "));
            // Making url clickable
            holder.url.setMovementMethod(LinkMovementMethod.getInstance());
            holder.url.setVisibility(View.VISIBLE);
        } else {
            // url is null, remove from the view
            holder.url.setVisibility(View.GONE);
        }
        // user profile pic
        Glide.with(context).load(like.getProfilePic()).into(holder.profilePic);
//         Feed image
        if (like.getImage() != null) {
            holder.feedImageView.setImageUrl(like.getImage(), imageLoader);
            holder.feedImageView.setVisibility(View.VISIBLE);
            holder.feedImageView
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            holder.feedImageView.setVisibility(View.GONE);
        }
    }
    @Override
    public int getItemCount() {
        return likeList.size();
    }
}
