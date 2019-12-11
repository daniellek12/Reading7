package com.reading7.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reading7.BookFragment;
import com.reading7.HomeFragment;
import com.reading7.R;
import com.reading7.Review;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {

    List<Review> reviews; //TODO: should be the actual Review class
    Context mContext;

    public ReviewListAdapter(Context context, List<Review> reviews) {
        this.reviews = reviews;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ReviewListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_item, viewGroup, false);
        return new ReviewListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewListAdapter.ViewHolder viewHolder, int i) {

        Review review = reviews.get(i);
        viewHolder.ratingBar.setRating(review.getRank());
        viewHolder.userName.setText(review.getReviewer_name());
        Date date = review.getReview_time().toDate();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm");
        String strDate = dateFormat.format(date);

        viewHolder.postTime.setText(strDate);
        viewHolder.reviewContent.setText((review.getReview_content()));
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RatingBar ratingBar;
        TextView userName;
        TextView postTime;
        TextView reviewContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ratingBar = itemView.findViewById(R.id.rating);
            userName = itemView.findViewById(R.id.userName);
            postTime = itemView.findViewById(R.id.postTime);
            reviewContent = itemView.findViewById(R.id.review);
        }
    }
}
