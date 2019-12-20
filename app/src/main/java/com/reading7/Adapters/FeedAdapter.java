package com.reading7.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.BookFragment;
import com.reading7.MainActivity;
import com.reading7.Objects.Book;
import com.reading7.Objects.Post;
import com.reading7.PublicProfileFragment;
import com.reading7.R;
import com.reading7.Objects.User;
import com.reading7.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    ArrayList<Post> posts;
    Context mContext;

    /*-------------------------------------- View Holders ----------------------------------------*/


    public class ReviewPostHolder extends RecyclerView.ViewHolder {

        RatingBar ratingBar;
        TextView rating;
        ImageView cover;
        ImageView coverBackground;
        TextView userName;
        TextView postTime;
        Button likeButton;
        TextView likesNum;
        CircleImageView profileImage;

        TextView review_content;
        TextView review_title;

        public ReviewPostHolder(@NonNull View itemView) {
            super(itemView);

            ratingBar = itemView.findViewById(R.id.ratingBar);
            rating = itemView.findViewById(R.id.rating);
            cover = itemView.findViewById(R.id.coverImage);
            coverBackground = itemView.findViewById(R.id.coverBackground);
            userName = itemView.findViewById(R.id.userName);
            postTime = itemView.findViewById(R.id.postTime);
            likeButton = itemView.findViewById(R.id.likeButton);
            likesNum = itemView.findViewById(R.id.likesNum);
            profileImage = itemView.findViewById(R.id.profileImage);
            review_content = itemView.findViewById(R.id.comment);
            review_title = itemView.findViewById(R.id.title);
        }
    }

    public class WishListPostHolder extends RecyclerView.ViewHolder {

        ImageView cover;
        TextView userName;
        TextView postTime;
        TextView bookName;
        CircleImageView profileImage;


        public WishListPostHolder(@NonNull View itemView) {
            super(itemView);

            cover = itemView.findViewById(R.id.coverImage);
            userName =itemView.findViewById(R.id.userName);
            bookName =itemView.findViewById(R.id.bookName);
            postTime =itemView.findViewById(R.id.postTime);
            profileImage = itemView.findViewById(R.id.profileImage);
        }
    }

    //TODO: NewBookPostHolder

    /*--------------------------------------------------------------------------------------------*/


    public FeedAdapter(Context context, ArrayList<Post> posts) {
        this.posts = posts;
        this.mContext = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = null;
        switch (viewType) {

            case 0: // ReviewPost Holder
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


    /*-------------------------------------- View Binders ----------------------------------------*/


    private void bindReview(RecyclerView.ViewHolder viewHolder, int i) {

        final ReviewPostHolder holder = (ReviewPostHolder)viewHolder;

        //Set ratingbar color
        Drawable rating = holder.ratingBar.getProgressDrawable();
        rating.setColorFilter(Color.parseColor("#FFC21C"), PorterDuff.Mode.SRC_ATOP);

        final Post post = posts.get(i);

        holder.ratingBar.setRating(post.getRank());
        holder.rating.setText(String.valueOf(post.getRank()));

        holder.userName.setText(post.getUser_name());

        ArrayList<Integer> avatar_details = post.getUser_avatar();
        Utils.loadAvatar(mContext, holder.profileImage, avatar_details);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(post.getPost_time().toDate());
        holder.postTime.setText(strDate); // FIXME check this

        holder.review_content.setText(post.getReview_content());
        holder.review_title.setText(post.getReview_title());
        Utils.showImage(post.getBook_title(), ((ReviewPostHolder) viewHolder).cover,(Activity)mContext);
        Utils.showImage(post.getBook_title(), ((ReviewPostHolder) viewHolder).coverBackground,(Activity)mContext);

        //TODO: deal with profile  image
        //TODO: load images correctly
        //holder.cover.setImageResource(mContext.getResources().getIdentifier("cover"+(i+1), "mipmap", mContext.getPackageName()));
        //holder.coverBackground.setImageResource(mContext.getResources().getIdentifier("cover"+(i+1), "mipmap", mContext.getPackageName()));

        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        final DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users").document(mUser.getEmail());

        // set Buttons
        final String id = post.getReview_id();
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        final User user = document.toObject(User.class);
                        if (user.getLiked_reviews().contains(id))
                            holder.likeButton.setBackground(mContext.getResources().getDrawable(R.drawable.like_colored));
                        else holder.likeButton.setBackground(mContext.getResources().getDrawable(R.drawable.like));
                        holder.likesNum.setText(Integer.toString(post.getLikes_count()));
                        holder.likeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Boolean liked = user.getLiked_reviews().contains(id);
                                final DocumentReference PostRef = FirebaseFirestore.getInstance().collection("Reviews").document(post.getReview_id());
                                int curr_num = post.getLikes_count();
                                if (liked) {
                                    user.remove_like(id);

                                    post.setLikes_count(curr_num-1);
                                    userRef.update("liked_reviews", user.getLiked_reviews());
                                    PostRef.update("likes_count", curr_num - 1);

                                    holder.likeButton.setBackground(mContext.getResources().getDrawable(R.drawable.like));
                                    holder.likesNum.setText(String.valueOf(curr_num - 1));

                                } else {
                                    user.add_like(id);

                                    post.setLikes_count(curr_num+1);
                                    userRef.update("liked_reviews", user.getLiked_reviews());
                                    PostRef.update("likes_count", curr_num + 1);

                                    holder.likeButton.setBackground(mContext.getResources().getDrawable(R.drawable.like_colored));
                                    holder.likesNum.setText(String.valueOf(curr_num + 1));
                                }
                            }
                        });

                    }
                    else Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mContext).loadPublicProfileFragment(new PublicProfileFragment(), post.getReviewer_email());
            }
        });

        holder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query bookRef = FirebaseFirestore.getInstance().collection("Books").whereEqualTo("title",post.getBook_title());
                bookRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                ((MainActivity) mContext).loadBookFragment(new BookFragment(), doc.toObject(Book.class));
                                break;
                            }
                        }
                    }
                });
            }
        });


    }


    private void bindWishList(RecyclerView.ViewHolder viewHolder, int i) {

        WishListPostHolder holder = (WishListPostHolder) viewHolder;
        Post post = posts.get(i);

        holder.bookName.setText(post.getBook_title());
        Utils.showImage(post.getBook_title(), holder.cover,(Activity)mContext);
        holder.userName.setText(post.getUser_name());

        ArrayList<Integer> avatar_details = post.getUser_avatar();
        Utils.loadAvatar(mContext, holder.profileImage, avatar_details);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(post.getPost_time().toDate());
        holder.postTime.setText(strDate);

        // TODO: deal with profile image

    }

    /*--------------------------------------------------------------------------------------------*/


}
