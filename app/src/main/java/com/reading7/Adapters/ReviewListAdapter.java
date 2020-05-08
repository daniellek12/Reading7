package com.reading7.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.BookFragment;
import com.reading7.Dialogs.AddCommentDialog;
import com.reading7.MainActivity;
import com.reading7.Objects.Avatar;
import com.reading7.Objects.Book;
import com.reading7.Objects.Review;
import com.reading7.Objects.User;
import com.reading7.R;
import com.reading7.ReviewCommentsFragment;
import com.reading7.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.reading7.Utils.RelativeDateDisplay;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {

    private Context mContext;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private List<Review> reviews;
    private Fragment fragment;


    public ReviewListAdapter(Context context, List<Review> reviews, Fragment fragment) {

        this.mContext = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.reviews = reviews;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ReviewListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_item, viewGroup, false);
        return new ReviewListAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewListAdapter.ViewHolder viewHolder, int i) {

        final Review review = reviews.get(i);

        if (i == 0 && ((BookFragment) fragment).isReviewedWithContent()) {
            viewHolder.relativeLayout.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            viewHolder.deleteBtn.setVisibility(View.VISIBLE);
            viewHolder.deleteBtn.setOnClickListener(new DeleteReviewOnClick(review));
        }

        bindReviewUser(viewHolder, review.getReviewer_email());

        Date date = review.getReview_time().toDate();
        String strDate = RelativeDateDisplay(Timestamp.now().toDate().getTime() - date.getTime());
        viewHolder.postTime.setText(strDate);

        viewHolder.ratingBar.setRating(review.getRank());

        if (review.getReview_title().equals(""))
            viewHolder.reviewTitle.setVisibility(View.GONE);
        else {
            viewHolder.reviewTitle.setVisibility(View.VISIBLE);
            viewHolder.reviewTitle.setText(review.getReview_title());
        }

        if (review.getReview_content().equals(""))
            viewHolder.reviewContent.setVisibility(View.GONE);
        else {
            viewHolder.reviewContent.setVisibility(View.VISIBLE);
            viewHolder.reviewContent.setText((review.getReview_content()));
        }

        viewHolder.commentsNum.setText(String.valueOf(review.getComments().size()));

        if (Utils.isAdmin){
            viewHolder.deleteLayout.setVisibility(View.VISIBLE);
            viewHolder.likeLayout.setVisibility(View.GONE);
        }
        else {
            initAddCommentButton(viewHolder, i);
            initLikeMechanics(viewHolder, i);
        }

        viewHolder.countersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReviewCommentsFragment mReviewCommentsFragment = new ReviewCommentsFragment(review);
                mReviewCommentsFragment.setTargetFragment(fragment, 303);
                ((MainActivity) mContext).addFragment(mReviewCommentsFragment);
            }
        });
    }


    private void bindReviewUser(final ViewHolder holder, final String email) {

        DocumentReference userReference = db.collection("Users").document(email);
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User reviewer = task.getResult().toObject(User.class);

                holder.userName.setText(reviewer.getFull_name());

                Avatar avatar = reviewer.getAvatar();
                avatar.loadIntoImage(mContext, holder.profileImage);

                Utils.OpenProfileOnClick profileListener = new Utils.OpenProfileOnClick(mContext, email);
                holder.profileImage.setOnClickListener(profileListener);
                holder.userName.setOnClickListener(profileListener);
            }
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        RatingBar ratingBar;
        TextView userName;
        TextView postTime;
        TextView reviewTitle;
        TextView reviewContent;
        CircleImageView profileImage;
        ImageButton deleteBtn;
        TextView likeNum;
        TextView commentsNum;
        RelativeLayout countersLayout;
        LinearLayout likeBtn;
        LinearLayout addCommentBtn;
        RelativeLayout likeLayout;
        RelativeLayout deleteLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.review_item);
            profileImage = itemView.findViewById(R.id.profileImage);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            userName = itemView.findViewById(R.id.userName);
            postTime = itemView.findViewById(R.id.postTime);
            reviewTitle = itemView.findViewById(R.id.title);
            reviewContent = itemView.findViewById(R.id.review);
            likeNum = itemView.findViewById(R.id.likeNum);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            commentsNum = itemView.findViewById(R.id.commentsNum);
            addCommentBtn = itemView.findViewById(R.id.button_add_comment);
            countersLayout = itemView.findViewById(R.id.activityCountersLayout);
            likeLayout = itemView.findViewById(R.id.likeLayout);
            deleteLayout = itemView.findViewById(R.id.deleteLayout);

        }
    }


    private class DeleteReviewOnClick implements View.OnClickListener {

        private Review review;

        public DeleteReviewOnClick(Review review) {
            this.review = review;
        }

        @Override
        public void onClick(View v) {
            ((BookFragment) fragment).setReviewed(false);
            ((BookFragment) fragment).setReviewedWithContent(false);

            reviews.remove(0);
            notifyItemRemoved(0);

            DocumentReference reviewReference = db.collection("Reviews").document(review.getReview_id());
            reviewReference.delete();

            updateBookParams();
        }

        private void updateBookParams() {

            Book book = ((BookFragment) fragment).getBook();

            final float newAvg, newAvgAge;
            if (book.getRaters_count() == 1) {
                newAvg = 0;
                newAvgAge = 0;
            } else {
                newAvg = ((book.getAvg_rating() * book.getRaters_count()) - review.getRank()) / (book.getRaters_count() - 1);
                newAvgAge = ((book.getAvg_age() * book.getRaters_count()) - review.getReviewer_age()) / (book.getRaters_count() - 1);
            }
            final int countRaters = book.getRaters_count() - 1;
            db.collection("Books").document(review.getBook_id()).update("avg_rating", newAvg, "avg_age", newAvgAge, "raters_count", countRaters);
            ((BookFragment) fragment).updateUIAfterDeleteReview(newAvg, newAvgAge, countRaters);
        }
    }


    private void addNotificationLike(String to_email, String book_title) {
        if ( (!(to_email.equals(mAuth.getCurrentUser().getEmail())))) {

            Map<String, Object> notificationMessegae = new HashMap<>();

            notificationMessegae.put("type", mContext.getResources().getString(R.string.like_notificiation));
            notificationMessegae.put("from", mAuth.getCurrentUser().getEmail());
            notificationMessegae.put("book_title", book_title);
            notificationMessegae.put("time", Timestamp.now());

            db.collection("Users/" + to_email + "/Notifications").add(notificationMessegae);
        }

    }


    private void setLikeButton(ViewHolder viewHolder, boolean liked) {

        ImageView buttonIcon = viewHolder.likeBtn.findViewById(R.id.likeIcon);
        TextView buttonText = viewHolder.likeBtn.findViewById(R.id.likeText);

        if (liked) {
            buttonText.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            buttonIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.like_colored));
        } else {
            buttonText.setTextColor(mContext.getResources().getColor(R.color.darkGrey));
            buttonIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.like));
        }

    }


    private void initAddCommentButton(ViewHolder viewHolder, final int i) {

        final Review review = reviews.get(i);

        viewHolder.addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddCommentDialog dialog = new AddCommentDialog();
                Bundle args = new Bundle();
                args.putString("review_id", review.getReview_id());
                dialog.setArguments(args);
                dialog.setTargetFragment(fragment, 303);
                dialog.show(fragment.getActivity().getSupportFragmentManager(), "example dialog");
            }
        });
    }


    private void initLikeMechanics(final ViewHolder viewHolder, int i) {

        final Review review = reviews.get(i);
        final User user = ((MainActivity) mContext).getCurrentUser();
        final String id = review.getReview_id();

        if (user.getLiked_reviews().contains(id))
            setLikeButton(viewHolder, true);
        else
            setLikeButton(viewHolder, false);

        viewHolder.likeNum.setText(Integer.toString(review.getLikes_count()));
        viewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (user.getLiked_reviews().contains(id)) {
                    user.remove_like(id);
                    review.reduceLike();
                    db.collection("Users").document(user.getEmail()).update("liked_reviews", user.getLiked_reviews());
                    db.collection("Reviews").document(review.getReview_id()).update("likes_count", review.getLikes_count());

                    setLikeButton(viewHolder, false);
                    viewHolder.likeNum.setText(Integer.toString(review.getLikes_count()));

                } else {
                    user.add_like(id);
                    review.addLike();
                    db.collection("Users").document(user.getEmail()).update("liked_reviews", user.getLiked_reviews());
                    db.collection("Reviews").document(review.getReview_id()).update("likes_count", review.getLikes_count());

                    setLikeButton(viewHolder, true);
                    viewHolder.likeNum.setText(Integer.toString(review.getLikes_count()));

                    addNotificationLike(review.getReviewer_email(), review.getBook_title());
                }

                ((MainActivity) mContext).setCurrentUser(user);
            }
        });

    }


    public void notifyReviewCommentsChanged(String review_id) {

        for (int i = 0; i < getItemCount(); i++) {

            final int finalI = i;
            final Review review = reviews.get(i);

            if (review.getReview_id().equals(review_id)) {

                Query query = db.collection("Reviews").whereEqualTo("review_id", review_id);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Review mReview = null;
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                mReview = doc.toObject(Review.class);
                            }

                            review.setComments(mReview.getComments());
                            notifyItemChanged(finalI);
                        }
                    }
                });

                break;
            }
        }
    }

}