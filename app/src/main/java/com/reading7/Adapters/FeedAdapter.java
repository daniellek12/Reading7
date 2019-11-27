package com.reading7.Adapters;

import android.content.Context;
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_item, viewGroup, false);
        return new FeedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        HomeFragment.Post post = posts.get(i);
        viewHolder.ratingBar.setRating(post.rating);
        viewHolder.cover.setImageResource(mContext.getResources().getIdentifier("cover"+(i+1), "mipmap", mContext.getPackageName()));
        viewHolder.userName.setText(post.userName);
        viewHolder.bookName.setText(post.bookName);
        viewHolder.postTime.setText(post.postTime);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RatingBar ratingBar;
        ImageView cover;
        TextView bookName;
        TextView userName;
        TextView postTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ratingBar = itemView.findViewById(R.id.rating);
            cover = itemView.findViewById(R.id.coverImage);
            bookName = itemView.findViewById(R.id.bookName);
            userName =itemView.findViewById(R.id.userName);
            postTime =itemView.findViewById(R.id.postTime);

        }
    }
}
