package com.reading7.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.reading7.Post;
import com.reading7.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    ArrayList<Post> posts;
    Context mContext;

    /*************************************** View Holders *****************************************/


    public class ReviewPostHolder extends RecyclerView.ViewHolder {

        RatingBar ratingBar;
        TextView rating;
        ImageView cover;
        ImageView coverBackground;
        TextView userName;
        TextView postTime;

        RadioGroup likeButtons;

        public ReviewPostHolder(@NonNull View itemView) {
            super(itemView);

            ratingBar = itemView.findViewById(R.id.ratingBar);
            rating = itemView.findViewById(R.id.rating);
            cover = itemView.findViewById(R.id.coverImage);
            coverBackground = itemView.findViewById(R.id.coverBackground);
            userName = itemView.findViewById(R.id.userName);
            postTime = itemView.findViewById(R.id.postTime);
            likeButtons = itemView.findViewById(R.id.likeButtons);
        }
    }

    public class WishListPostHolder extends RecyclerView.ViewHolder {

        ImageView cover;
        TextView userName;
        TextView postTime;
        TextView bookName;

        public WishListPostHolder(@NonNull View itemView) {
            super(itemView);

            cover = itemView.findViewById(R.id.coverImage);
            userName =itemView.findViewById(R.id.userName);
            bookName =itemView.findViewById(R.id.bookName);
            postTime =itemView.findViewById(R.id.postTime);

        }
    }

    //TODO: RecommedationPostHolder

    //TODO: NewBookPostHolder

    /**********************************************************************************************/


    public FeedAdapter(Context context, ArrayList<Post> posts) {
        this.posts = posts;
        this.mContext = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = null;
        switch (viewType) {

            case 0:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_post_item, viewGroup, false);
                return new ReviewPostHolder(view);

            case 1:
                 view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wishlist_post_item, viewGroup, false);
                return new WishListPostHolder(view);

            case 2:
                //TODO: return new RecommendationPostHolder();

            case 3:
                //TODO: return new NewBookPostHolder();
        }

        return null;
    }


    @Override
    public int getItemViewType(int i) {

        switch (posts.get(i).getType()){
            case Review: return 0;
            case WishList: return 1;
            case Recommendation: return 2;
            case NewBook: return 3;
        }

        return -1;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        switch (viewHolder.getItemViewType()) {

            case 0:
                bindReview(viewHolder, i);
                break;

            case 1:
                bindWishList(viewHolder, i);
                break;

            case 2:
                //TODO: bindRecommendation
                break;

            case 3:
                //TODO: bindNewBook
                break;
        }
    }


    @Override
    public int getItemCount() {
        return posts.size();
    }


    /*************************************** View Binders *****************************************/

    private void bindReview(RecyclerView.ViewHolder viewHolder, int i) {

        final ReviewPostHolder holder = (ReviewPostHolder)viewHolder;

        //Set ratingbar color
        Drawable rating = holder.ratingBar.getProgressDrawable();
        rating.setColorFilter(Color.parseColor("#FFC21C"), PorterDuff.Mode.SRC_ATOP);

        Post post = posts.get(i);
        holder.ratingBar.setRating(post.getRank());
        holder.rating.setText(String.valueOf(post.getRank()));
        holder.userName.setText(post.getUser_name());
        //TODO: holder.postTime.setText(post.getPost_time());

        //TODO: deal with profile  image
        //TODO: load images correctly
        holder.cover.setImageResource(mContext.getResources().getIdentifier("cover"+(i+1), "mipmap", mContext.getPackageName()));
        holder.coverBackground.setImageResource(mContext.getResources().getIdentifier("cover"+(i+1), "mipmap", mContext.getPackageName()));

        setLikeButtons(holder);

    }


    private void setLikeButtons(final ReviewPostHolder holder){
         //TODO: update the corresponding counter

        holder.likeButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            int prev = -1;
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checked) {

                if(prev != -1){

                    RadioButton prevBtn = radioGroup.findViewById(prev);
                    prevBtn.setText(String.valueOf(Integer.parseInt(prevBtn.getText().toString())-1));

                    if(prev==checked) {
                        prevBtn.setChecked(false);
                        prev = -1;
                        return;
                    }
                }

                RadioButton btn = radioGroup.findViewById(checked);
                btn.setText(String.valueOf(Integer.parseInt(btn.getText().toString())+1));
                prev = checked;
            }
        });

    }

    private void bindWishList(RecyclerView.ViewHolder viewHolder, int i) {

        WishListPostHolder holder = (WishListPostHolder) viewHolder;

        Post post = posts.get(i);

        holder.userName.setText(post.getUser_name());
        holder.bookName.setText(post.getBook_title());
        //TODO: holder.postTime.setText(post.getPost_time());

        //TODO: deal with profile  image
        //TODO: load the images correctly
        holder.cover.setImageResource(mContext.getResources().getIdentifier("cover"+(i+1), "mipmap", mContext.getPackageName()));

    }

    /**********************************************************************************************/


}
