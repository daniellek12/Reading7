package com.reading7.Adapters;


import android.content.Context;
import android.icu.text.RelativeDateTimeFormatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reading7.MainActivity;
import com.reading7.Objects.User;
import com.reading7.ProfileFragment;
import com.reading7.PublicProfileFragment;
import com.reading7.R;
import com.reading7.Objects.Review;
import com.reading7.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.reading7.Utils.RelativeDateDisplay;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {


    List<Review> reviews; //TODO: should be the actual Review class
    Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    // More Fields to support Like button
    private final FirebaseUser mUser;
    private final DocumentReference userRef;
    private ArrayList<String> LikedReviews;


    public ReviewListAdapter(Context context, List<Review> reviews) {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        this.reviews = reviews;
        this.mContext = context;

        this.mUser = mAuth.getCurrentUser();
        this.userRef = mFirestore.collection("Users").document(mUser.getEmail());

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        final User user = document.toObject(User.class);
                        LikedReviews = user.getLiked_reviews(); // Update Local array
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public ReviewListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_item, viewGroup, false);
        return new ReviewListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewListAdapter.ViewHolder viewHolder, int i) {

        final Review review = reviews.get(i);

        Utils.loadAvatar(mContext, viewHolder.profileImage, review.getReviewer_avatar());
        viewHolder.userName.setText(review.getReviewer_name());

        Date date = review.getReview_time().toDate();
        String strDate = RelativeDateDisplay(Timestamp.now().toDate().getTime() - date.getTime());
        viewHolder.postTime.setText(strDate);

        viewHolder.ratingBar.setRating(review.getRank());

        viewHolder.reviewTitle.setText(review.getReview_title());
        viewHolder.reviewContent.setText((review.getReview_content()));

        viewHolder.profileImage.setOnClickListener(new OpenProfileOnClick(review.getReviewer_email()));
        viewHolder.userName.setOnClickListener(new OpenProfileOnClick(review.getReviewer_email()));


        // Actions added to support Like button mechanics
        final String id = review.getReview_id();

        if (LikedReviews.contains(id)) viewHolder.likeBtn.setBackground(mContext.getResources().getDrawable(R.drawable.like_colored));
        else viewHolder.likeBtn.setBackground(mContext.getResources().getDrawable(R.drawable.like));
        viewHolder.likeNum.setText(Integer.toString(review.getLikes_count()));
        viewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean liked = LikedReviews.contains(id);
                final DocumentReference ReviewRef = FirebaseFirestore.getInstance().collection("Reviews").document(review.getReview_id());
                int curr_num = review.getLikes_count();
                if (liked) {
                    LikedReviews.remove(id);
                    review.setLikes_count(curr_num-1);
                    userRef.update("liked_reviews", LikedReviews);
                    ReviewRef.update("likes_count", curr_num - 1);

                    viewHolder.likeBtn.setBackground(mContext.getResources().getDrawable(R.drawable.like));
                    viewHolder.likeNum.setText(String.valueOf(curr_num - 1));

                } else {
                    LikedReviews.add(id);
                    review.setLikes_count(curr_num+1);
                    userRef.update("liked_reviews", LikedReviews);
                    ReviewRef.update("likes_count", curr_num + 1);

                    viewHolder.likeBtn.setBackground(mContext.getResources().getDrawable(R.drawable.like_colored));
                    viewHolder.likeNum.setText(String.valueOf(curr_num + 1));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        RatingBar ratingBar;
        TextView userName;
        TextView postTime;
        TextView reviewTitle;
        TextView reviewContent;
        CircleImageView profileImage;
        TextView likeNum;
        Button likeBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            userName = itemView.findViewById(R.id.userName);
            postTime = itemView.findViewById(R.id.postTime);
            reviewTitle = itemView.findViewById(R.id.title);
            reviewContent = itemView.findViewById(R.id.review);
            likeNum = itemView.findViewById(R.id.likeNum);
            likeBtn = itemView.findViewById(R.id.likeBtn);
        }
    }


    private class OpenProfileOnClick implements View.OnClickListener {

        private String user_email;

        public OpenProfileOnClick(String user_email){
            this.user_email = user_email;
        }

        @Override
        public void onClick(View v) {
            if(user_email.equals(mAuth.getCurrentUser().getEmail()))
                ((MainActivity) mContext).loadFragment(new ProfileFragment());
            else
                ((MainActivity) mContext).addFragment(new PublicProfileFragment(user_email));
        }
    }
}
