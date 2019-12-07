package com.reading7.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.reading7.HomeFragment;
import com.reading7.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder>  {


    ArrayList<HomeFragment.Post> posts; //TODO: should be the actual Post class
    Context mContext;

    public FeedAdapter(Context context, ArrayList<HomeFragment.Post> posts) {
        this.posts = posts;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recommendation_post_item, viewGroup, false);
        return new FeedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        HomeFragment.Post post = posts.get(i);
        viewHolder.ratingBar.setRating(post.rating);
        viewHolder.rating.setText(String.format("%.1f", post.rating));
        viewHolder.cover.setImageResource(mContext.getResources().getIdentifier("cover"+(i+1), "mipmap", mContext.getPackageName()));
        viewHolder.coverBackground.setImageResource(mContext.getResources().getIdentifier("cover"+(i+1), "mipmap", mContext.getPackageName()));
        viewHolder.userName.setText(post.userName);
        viewHolder.postTime.setText(post.postTime);

        //set ratingbar color
        Drawable rating = viewHolder.ratingBar.getProgressDrawable();
        rating.setColorFilter(Color.parseColor("#FFC21C"), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RatingBar ratingBar;
        TextView rating;
        ImageView cover;
        ImageView coverBackground;
        TextView userName;
        TextView postTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ratingBar = itemView.findViewById(R.id.ratingBar);
            rating = itemView.findViewById(R.id.rating);
            cover = itemView.findViewById(R.id.coverImage);
            coverBackground = itemView.findViewById(R.id.coverBackground);
            userName =itemView.findViewById(R.id.userName);
            postTime =itemView.findViewById(R.id.postTime);

        }
    }
}
