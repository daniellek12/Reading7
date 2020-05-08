package com.reading7;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reading7.Adapters.CommentsAdapter;
import com.reading7.Dialogs.AddCommentDialog;
import com.reading7.Objects.Comment;
import com.reading7.Objects.Review;
import com.reading7.Objects.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewCommentsFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Review mReview;
    private User mUser;


    public ReviewCommentsFragment(Review review) {
        this.mReview = review;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = ((MainActivity) getActivity()).getCurrentUser();
        return inflater.inflate(R.layout.review_comments_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        initBackButton();
        initReviewDetails();
        initComments();

        if (Utils.isAdmin){
            getView().findViewById(R.id.deleteLayout).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.likeLayout).setVisibility(View.GONE);
        }
        else {
            initLikeMechanics();
            initAddCommentButton();
        }


        Animation slide_up = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        getView().findViewById(R.id.layout).startAnimation(slide_up);

    }


    private void initReviewDetails() {

        initReviewUser(mReview.getReviewer_email());

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

        ((RatingBar) getView().findViewById(R.id.ratingBar)).setRating(mReview.getRank());

        String reviewTime = Utils.RelativeDateDisplay(Timestamp.now().toDate().getTime() -
                mReview.getReview_time().toDate().getTime());
        ((TextView) getView().findViewById(R.id.postTime)).setText(reviewTime);

        ((TextView) getView().findViewById(R.id.likeNum)).setText(String.valueOf(mReview.getLikes_count()));
        ((TextView) getView().findViewById(R.id.commentsNum)).setText(String.valueOf(mReview.getComments().size()));

    }


    private void initReviewUser(final String email) {

        DocumentReference userReference = db.collection("Users").document(email);
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User reviewer = task.getResult().toObject(User.class);

                TextView userName = getView().findViewById(R.id.userName);
                userName.setText(reviewer.getFull_name());

                CircleImageView profileImage = getView().findViewById(R.id.profileImage);
                reviewer.getAvatar().loadIntoImage(getContext(), profileImage);

                Utils.OpenProfileOnClick profileListener = new Utils.OpenProfileOnClick(getContext(), email);
                profileImage.setOnClickListener(profileListener);
                userName.setOnClickListener(profileListener);
            }
        });
    }


    private void initComments() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        RecyclerView commentsRV = getActivity().findViewById(R.id.commentsRV);
        commentsRV.setLayoutManager(layoutManager);

        ArrayList<Comment> comments = mReview.getComments();
        Collections.sort(comments);

        CommentsAdapter adapter = new CommentsAdapter(getActivity(), this, comments);
        commentsRV.setAdapter(adapter);
    }


    private void initBackButton() {
        getView().findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResult(303, mReview.getReview_id());
                getActivity().onBackPressed();
            }
        });
    }


    private void initLikeMechanics() {

        final String id = mReview.getReview_id();

        if (mUser.getLiked_reviews().contains(id))
            setLikeButton(true);
        else
            setLikeButton(false);

        ((TextView) getView().findViewById(R.id.likeNum)).setText(Integer.toString(mReview.getLikes_count()));

        getView().findViewById(R.id.likeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mUser.getLiked_reviews().contains(id)) {
                    mUser.remove_like(id);
                    mReview.reduceLike();
                    db.collection("Users").document(mUser.getEmail()).update("liked_reviews", mUser.getLiked_reviews());
                    db.collection("Reviews").document(mReview.getReview_id()).update("likes_count", mReview.getLikes_count());

                    setLikeButton(false);
                    ((TextView) getView().findViewById(R.id.likeNum)).setText(String.valueOf(mReview.getLikes_count()));

                } else {
                    mUser.add_like(id);
                    mReview.addLike();
                    db.collection("Users").document(mUser.getEmail()).update("liked_reviews", mUser.getLiked_reviews());
                    db.collection("Reviews").document(mReview.getReview_id()).update("likes_count", mReview.getLikes_count());

                    setLikeButton(true);
                    ((TextView) getView().findViewById(R.id.likeNum)).setText(String.valueOf(mReview.getLikes_count()));

                    addNotificationLike(mReview.getReviewer_email(), mReview.getBook_title());
                }

                ((MainActivity) getActivity()).setCurrentUser(mUser);
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


    private void addNotificationLike(String to_email, String book_title) {
        if ((!(to_email.equals(mAuth.getCurrentUser().getEmail())))) {

            Map<String, Object> notificationMessegae = new HashMap<>();

            notificationMessegae.put("type", getResources().getString(R.string.like_notificiation));
            notificationMessegae.put("from", mAuth.getCurrentUser().getEmail());
            notificationMessegae.put("book_title", book_title);
            notificationMessegae.put("time", Timestamp.now());

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


    public void sendResult(int REQUEST_CODE, String review_id) {
        Intent intent = new Intent();
        intent.putExtra("review_id", review_id);

        if (getTargetFragment() != null)
            getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_CODE, intent);
    }


    public String getReviewId() {
        return mReview.getReview_id();
    }


    public void updateCommentsNum() {
        ((TextView) getView().findViewById(R.id.commentsNum)).setText(String.valueOf(mReview.getComments().size()));
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 303:
                Comment comment = (Comment) data.getSerializableExtra("comment");
                CommentsAdapter adapter = (CommentsAdapter) ((RecyclerView) getActivity().findViewById(R.id.commentsRV)).getAdapter();
                mReview.addComment(comment);
                adapter.notifyAddComment();
                updateCommentsNum();
                break;
        }
    }

}
