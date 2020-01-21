package com.reading7;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.CommentsAdapter;
import com.reading7.Adapters.FeedAdapter;
import com.reading7.Dialogs.AddCommentDialog;
import com.reading7.Objects.Comment;
import com.reading7.Objects.Review;
import com.reading7.Objects.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewCommentsFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Review mReview;
    private User mUser;


    public ReviewCommentsFragment(Review review, User user) {
        this.mReview = review;
        this.mUser = user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        return inflater.inflate(R.layout.review_comments_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ((TextView) getView().findViewById(R.id.reviewTitle)).setText(getString(R.string.review_fragment_title) + " " + mReview.getReviewer_name());
        initBackButton();
        initReviewDetails();
        initComments();
        initLikeMechanics();
        initAddCommentButton();
    }


    private void initReviewDetails() {

        if (mReview.getReview_title().equals(""))
            getView().findViewById(R.id.title).setVisibility(View.GONE);
        else {
            getView().findViewById(R.id.title);
            ((TextView) getView().findViewById(R.id.title)).setText(mReview.getReview_title());
        }

        if (mReview.getReview_content().equals(""))
            getView().findViewById(R.id.review).setVisibility(View.GONE);
        else {
            getView().findViewById(R.id.review).setVisibility(View.VISIBLE);
            ((TextView) getView().findViewById(R.id.review)).setText(mReview.getReview_content());
        }

        ((TextView) getView().findViewById(R.id.userName)).setText(mReview.getReviewer_name());
        ((RatingBar) getView().findViewById(R.id.ratingBar)).setRating(mReview.getRank());

        CircleImageView profileImage = getView().findViewById(R.id.profileImage);
        Utils.loadAvatar(getContext(), profileImage, mReview.getReviewer_avatar());

        String reviewTime = Utils.RelativeDateDisplay(Timestamp.now().toDate().getTime() -
                mReview.getReview_time().toDate().getTime());
        ((TextView) getView().findViewById(R.id.postTime)).setText(reviewTime);

        ((TextView) getView().findViewById(R.id.likeNum)).setText(String.valueOf(mReview.getLikes_count()));
        ((TextView) getView().findViewById(R.id.commentsNum)).setText(String.valueOf(mReview.getComments().size()));

    }

    private void initComments() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        RecyclerView commentsRV = getActivity().findViewById(R.id.commentsRV);
        commentsRV.setLayoutManager(layoutManager);

        List<Comment> commentsList = new ArrayList<>();
        Map<String, Comment> commentsMap = mReview.getComments();

        for (Comment comment : commentsMap.values()) {
            commentsList.add(comment);
        }

        CommentsAdapter adapter = new CommentsAdapter(getActivity(), commentsList);
        commentsRV.setAdapter(adapter);
    }

    private void initBackButton() {
        getView().findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }


    private void initLikeMechanics() {

        final String id = mReview.getReview_id();
        final String book_title = mReview.getBook_title();
        final ArrayList<String> likedReviews = mUser.getLiked_reviews();

        if (likedReviews.contains(id))
            setLikeButton(true);
        else
            setLikeButton(false);

        ((TextView)getView().findViewById(R.id.likeNum)).setText(Integer.toString(mReview.getLikes_count()));
        getView().findViewById(R.id.likeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean liked = likedReviews.contains(id);
                int curr_num = mReview.getLikes_count();

                if (liked) {
                    likedReviews.remove(id);
                    mReview.setLikes_count(curr_num - 1);
                    db.collection("Users").document(mUser.getEmail()).update("liked_reviews", likedReviews);
                    db.collection("Reviews").document(mReview.getReview_id()).update("likes_count", curr_num - 1);

                    setLikeButton(false);
                    ((TextView)getView().findViewById(R.id.likeNum)).setText("" + (curr_num - 1));

                } else {
                    likedReviews.add(id);
                    mReview.setLikes_count(curr_num + 1);
                    db.collection("Users").document(mUser.getEmail()).update("liked_reviews", likedReviews);
                    db.collection("Reviews").document(mReview.getReview_id()).update("likes_count", curr_num + 1);

                    setLikeButton(true);
                    ((TextView)getView().findViewById(R.id.likeNum)).setText("" + (curr_num + 1));

                    addNotificationLike(mReview.getReviewer_email(), book_title, mReview.getIs_notify());
                }
            }
        });

    }

    private void setLikeButton(boolean liked) {

        ImageView buttonIcon = getView().findViewById(R.id.likeBtn).findViewById(R.id.likeIcon);
        TextView buttonText = getView().findViewById(R.id.likeBtn).findViewById(R.id.likeText);


        if (liked) {
            buttonText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            buttonIcon.setImageDrawable(getResources().getDrawable(R.drawable.like_colored));
        } else {
            buttonText.setTextColor(getResources().getColor(R.color.darkGrey));
            buttonIcon.setImageDrawable(getResources().getDrawable(R.drawable.like));
        }
    }

    private void addNotificationLike(String to_email, String book_title, boolean is_notify) {
        if (is_notify && (!(to_email.equals(mAuth.getCurrentUser().getEmail())))) {

            Map<String, Object> notificationMessegae = new HashMap<>();

            notificationMessegae.put("type", getResources().getString(R.string.like_notificiation));
            notificationMessegae.put("from", mAuth.getCurrentUser().getEmail());
            notificationMessegae.put("user_name", mUser.getFull_name());
            notificationMessegae.put("book_title", book_title);
            notificationMessegae.put("time", Timestamp.now());
            notificationMessegae.put("user_avatar", mUser.getAvatar_details());

            db.collection("Users/" + to_email + "/Notifications").add(notificationMessegae);
        }
    }


    private void initAddCommentButton() {

        getView().findViewById(R.id.button_add_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddCommentDialog dialog = new AddCommentDialog();
                Bundle args = new Bundle();
                args.putString("review_id", mReview.getReview_id());
                dialog.setArguments(args);
                dialog.setTargetFragment(ReviewCommentsFragment.this, 303);
                dialog.show(getActivity().getSupportFragmentManager(), "example dialog");
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){

            case 303:
                updateReviewAndComments();
                break;

        }
    }


    private void updateReviewAndComments() {
        Query query = db.collection("Reviews").whereEqualTo("review_id", mReview.getReview_id());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Review review = null;
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        review = doc.toObject(Review.class);
                    }

                    mReview.setComments(review.getComments());
                    initComments();
                }
            }
        });
    }



}
