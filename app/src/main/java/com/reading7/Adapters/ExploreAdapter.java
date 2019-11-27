package com.reading7.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import com.reading7.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ViewHolder>   {

    private ArrayList<Float> ratings; //TODO: should be a list of books
    private ArrayList<Integer> covers;
    private Context mContext;


    public ExploreAdapter(Context context, ArrayList<Float> ratings, ArrayList<Integer> covers){

        this.ratings = ratings;
        this.covers = covers;
        this.mContext = context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        RatingBar ratingBar;
        ImageView cover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ratingBar = itemView.findViewById(R.id.ratingBar);
            cover = itemView.findViewById(R.id.coverImage);
        }
    }


    @NonNull
    @Override
    public ExploreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.explore_item, viewGroup, false);
        return new ExploreAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        //set height of rows with 2 items
        if( i%11 == 0 || i%11 == 1 )
            viewHolder.cover.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpToPx(250)));

        //set ratingbar color
        Drawable rating = viewHolder.ratingBar.getProgressDrawable();
        rating.setColorFilter(Color.parseColor("#FFC21C"), PorterDuff.Mode.SRC_ATOP);

        viewHolder.ratingBar.setRating(ratings.get(i));
        viewHolder.cover.setImageResource(mContext.getResources().getIdentifier("cover"+(i+1), "mipmap", mContext.getPackageName()));

    }


    @Override
    public int getItemCount() {
        return ratings.size();
    }


    public int DpToPx(int dp){
        Resources r  = mContext.getResources();
        int px    = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;

    }

}
