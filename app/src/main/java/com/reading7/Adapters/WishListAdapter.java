package com.reading7.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.reading7.R;
import com.reading7.Review;
import com.reading7.Utils;
import com.reading7.WishList;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {

    private List<WishList> usersList;
    private Context mContext;
    private Activity currentActivity;


    public WishListAdapter(List<WishList> list, Activity activity){
        this.usersList = list;
        this.mContext = activity.getApplicationContext();
        this.currentActivity = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.coverImage);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.playlist_item, viewGroup,false);
        return new WishListAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        String book_name = usersList.get(i).getBook_title();
        Utils.showImage(book_name, viewHolder.cover, currentActivity);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

}
