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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Dialogs.AddCommentDialog;
import com.reading7.BookFragment;
import com.reading7.MainActivity;
import com.reading7.Objects.Book;
import com.reading7.Objects.User;
import com.reading7.ProfileFragment;
import com.reading7.PublicProfileFragment;
import com.reading7.R;
import com.reading7.Objects.Review;
import com.reading7.ReviewCommentsFragment;
import com.reading7.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.reading7.Utils.RelativeDateDisplay;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {

    private Context mContext;
    private List<Review> reviews;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Fragment fragment;

    private User real_user;
    // More Fields to support Like button
    private final FirebaseUser mUser;
    private final DocumentReference userRef;
    private ArrayList<String> LikedReviews;


    public ReviewListAdapter(Context context, List<Review> reviews, Fragment fragment, User real_user, ArrayList<String> likedReviews) {

        this.mContext = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.reviews = reviews;
        this.fragment = fragment;
        this.real_user = real_user;
        this.LikedReviews = likedReviews;
        this.mUser = mAuth.getCurrentUser();
        this.userRef = db.collection("Users").document(mUser.getEmail());
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

        if (i == 0 && ((BookFragment) fragment).isReviewed()) {
            viewHolder.relativeLayout.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            viewHolder.deleteBtn.setVisibility(View.VISIBLE);
            viewHolder.deleteBtn.setOnClickListener(new DeleteReviewOnClick(review.getReviewer_email(), review.getBook_title()));
        }

        Utils.loadAvatar(mContext, viewHolder.profileImage, review.getReviewer_avatar());
        viewHolder.userName.setText(review.getReviewer_name());

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

        viewHolder.profileImage.setOnClickListener(new OpenProfileOnClick(review.getReviewer_email()));
        viewHolder.userName.setOnClickListener(new OpenProfileOnClick(review.getReviewer_email()));
        viewHolder.commentsNum.setText(String.valueOf(review.getComments().size()));

        initAddCommentButton(viewHolder,i);
        initLikeMechanics(viewHolder, i);

        viewHolder.countersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mContext).addFragment(new ReviewCommentsFragment(review, real_user));
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
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
        }
    }


    private class OpenProfileOnClick implements View.OnClickListener {

        private String user_email;

        public OpenProfileOnClick(String user_email) {
            this.user_email = user_email;
        }

        @Override
        public void onClick(View v) {
            if (user_email.equals(mAuth.getCurrentUser().getEmail()))
                ((MainActivity) mContext).loadFragment(new ProfileFragment());
            else
                ((MainActivity) mContext).addFragment(new PublicProfileFragment(user_email));
        }
    }

    private class DeleteReviewOnClick implements View.OnClickListener {

        private String user_email;
        private String book_title;

        public DeleteReviewOnClick(String user_email, String book_title) {
            this.user_email = user_email;
            this.book_title = book_title;
        }

        @Override
        public void onClick(View v) {
            ((BookFragment) fragment).setReviewed(false);
            reviews.remove(0);
            notifyItemRemoved(0);
            CollectionReference requestsRef = db.collection("Reviews");
            Query requestQuery = requestsRef.whereEqualTo("reviewer_email", user_email)
                    .whereEqualTo("book_title", book_title);
            requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            final Review review = document.toObject(Review.class);
                            document.getReference().delete();
                            final DocumentReference bookRef = FirebaseFirestore.getInstance().collection("Books").document(review.getBook_id());

                            bookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        final DocumentSnapshot doc = task.getResult();
                                        if (doc.exists()) {
                                            Book book = (doc.toObject(Book.class));
                                            final float newAvg, newAvgAge;
                                            if (book.getRaters_count() == 1) {
                                                newAvg = 0;
                                                newAvgAge = 0;
                                            } else {
                                                newAvg = ((book.getAvg_rating() * book.getRaters_count()) - review.getRank()) / (book.getRaters_count() - 1);
                                                newAvgAge = ((book.getAvg_age() * book.getRaters_count()) - review.getReviewer_age()) / (book.getRaters_count() - 1);
                                            }
                                            final Map<String, Object> updates = new HashMap<String, Object>();
                                            final int countRaters = book.getRaters_count() - 1;
                                            updates.put("avg_rating", newAvg);
                                            updates.put("avg_age", newAvgAge);
                                            updates.put("raters_count", book.getRaters_count() - 1);
                                            bookRef.update(updates);
                                            ((BookFragment) fragment).updateUIAfterDeleteReview(newAvg, newAvgAge, countRaters);
                                        }
                                    }
                                }
                            });


                        }
                    }
                }
            });
        }
    }

    private void addNotificationLike(String to_email, String book_title, boolean is_notify) {
        if (is_notify && (!(to_email.equals(mAuth.getCurrentUser().getEmail())))) {
            db = FirebaseFirestore.getInstance();

            Map<String, Object> notificationMessegae = new HashMap<>();

            notificationMessegae.put("type", mContext.getResources().getString(R.string.like_notificiation));
            notificationMessegae.put("from", mAuth.getCurrentUser().getEmail());
            notificationMessegae.put("user_name", real_user.getFull_name());
            notificationMessegae.put("book_title", book_title);
            notificationMessegae.put("time", Timestamp.now());
            notificationMessegae.put("user_avatar", real_user.getAvatar_details());


            db.collection("Users/" + to_email + "/Notifications").add(notificationMessegae).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                }
            });
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

        final String id = review.getReview_id();
        final String book_title = review.getBook_title();

        if (LikedReviews.contains(id))
            setLikeButton(viewHolder, true);
        else
            setLikeButton(viewHolder, false);

        viewHolder.likeNum.setText(Integer.toString(review.getLikes_count()));
        viewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean liked = LikedReviews.contains(id);
                final DocumentReference ReviewRef = FirebaseFirestore.getInstance().collection("Reviews").document(review.getReview_id());
                int curr_num = review.getLikes_count();
                if (liked) {
                    LikedReviews.remove(id);
                    review.setLikes_count(curr_num - 1);
                    userRef.update("liked_reviews", LikedReviews);
                    ReviewRef.update("likes_count", curr_num - 1);

                    setLikeButton(viewHolder, false);
                    int likesNum = Integer.valueOf(viewHolder.likeNum.getText().toString());
                    viewHolder.likeNum.setText("" + (likesNum - 1));

                } else {
                    LikedReviews.add(id);
                    review.setLikes_count(curr_num + 1);
                    userRef.update("liked_reviews", LikedReviews);
                    ReviewRef.update("likes_count", curr_num + 1);

                    setLikeButton(viewHolder, true);
                    int likesNum = Integer.valueOf(viewHolder.likeNum.getText().toString());
                    viewHolder.likeNum.setText("" + (likesNum + 1));

                    addNotificationLike(review.getReviewer_email(), book_title, review.getIs_notify());
                }
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