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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
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
import com.reading7.ReviewsFragment;
import com.reading7.Utils;

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

        bindReviewUser(viewHolder, review.getReviewer_email());
        setupMoreMenu(viewHolder, i);

        if (review.getReviewer_email().equals(mAuth.getCurrentUser().getEmail())) {
            viewHolder.relativeLayout.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
        }

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
        viewHolder.likeNum.setText(Integer.toString(review.getLikes_count()));

        if (Utils.isAdmin) {
            viewHolder.deleteLayout.setVisibility(View.VISIBLE);
            viewHolder.likeLayout.setVisibility(View.GONE);
            viewHolder.adminDeleteBtn.setOnClickListener(new DeleteReviewOnClick(review, i));
        } else {
            initAddCommentButton(viewHolder, i);
            initLikeMechanics(viewHolder, i);
            initReportButton(viewHolder, i);
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


    private void setupMoreMenu(final ReviewListAdapter.ViewHolder holder, final int position) {

        final Review review = reviews.get(position);
        User user = ((MainActivity) mContext).getCurrentUser();

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.moreLayout.getVisibility() == View.VISIBLE)
                    holder.moreLayout.setVisibility(View.GONE);
                else holder.moreLayout.setVisibility(View.VISIBLE);
            }
        });

        if (review.getReviewer_email().equals(user.getEmail())) {
            holder.deleteReview.setVisibility(View.VISIBLE);
            holder.deleteReview.setOnClickListener(new DeleteReviewOnClick(review, position));
        }

        initReportButton(holder, position);
    }

    private void initReportButton(final ViewHolder viewHolder, int i) {
        final Review review = reviews.get(i);

        viewHolder.reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReport(review.getReviewer_email(), review.getBook_title());
                Toast.makeText(mContext, "תודה! הדיווח שלך התקבל", Toast.LENGTH_SHORT).show();
//                TODO change to report without email?
//                Intent intent = new Intent(Intent.ACTION_SENDTO);
//                intent.setType("message/rfc822");
//                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mContext.getResources().getString(R.string.admin_email)});
//                intent.putExtra(Intent.EXTRA_SUBJECT, "דיווח על ביקורת");
//                String content = "הביקורת בעלת המזהה " + review.getReview_id() + " של המשתמש " + review.getReviewer_email() + " על הספר " + review.getBook_title() + " איננה הולמת.";
//                intent.putExtra(Intent.EXTRA_TEXT, content);
//                try {
//                    mContext.startActivity(Intent.createChooser(intent, "שליחה באמצעות..."));
//
//                } catch (android.content.ActivityNotFoundException ex) {
//                    Toast.makeText(mContext, "לא קיימת אפליקציה שניתן לשלוח באמצעותה דואר אלקטרוני", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }

    private void addReport(String reviewer_email, String book_title) {
        Map<String, Object> notificationMessegae = new HashMap<>();

        notificationMessegae.put("type", mContext.getResources().getString(R.string.report_notificiation));
        notificationMessegae.put("from", mAuth.getCurrentUser().getEmail());
        notificationMessegae.put("book_title", book_title);
        notificationMessegae.put("time", Timestamp.now());
        notificationMessegae.put("reviewer_email", reviewer_email);

        db.collection("Reports").add(notificationMessegae);
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

    private void addNotificationLike(String to_email, String book_title) {
        if ((!(to_email.equals(mAuth.getCurrentUser().getEmail())))) {

            Map<String, Object> notificationMessegae = new HashMap<>();

            notificationMessegae.put("type", mContext.getResources().getString(R.string.like_notificiation));
            notificationMessegae.put("from", mAuth.getCurrentUser().getEmail());
            notificationMessegae.put("book_title", book_title);
            notificationMessegae.put("time", Timestamp.now());

            db.collection("Users/" + to_email + "/Notifications").add(notificationMessegae);
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


    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        RatingBar ratingBar;
        TextView userName;
        TextView postTime;
        TextView reviewTitle;
        TextView reviewContent;
        CircleImageView profileImage;
        ImageButton more;
        LinearLayout moreLayout;
        RelativeLayout deleteReview;
        LinearLayout reportBtn;
        TextView likeNum;
        TextView commentsNum;
        RelativeLayout countersLayout;
        LinearLayout likeBtn;
        LinearLayout addCommentBtn;
        LinearLayout likeLayout;
        RelativeLayout deleteLayout;
        TextView adminDeleteBtn;

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
            more = itemView.findViewById(R.id.more);
            moreLayout = itemView.findViewById(R.id.moreLayout);
            deleteReview = itemView.findViewById(R.id.deleteReview);
            commentsNum = itemView.findViewById(R.id.commentsNum);
            addCommentBtn = itemView.findViewById(R.id.button_add_comment);
            countersLayout = itemView.findViewById(R.id.activityCountersLayout);
            likeLayout = itemView.findViewById(R.id.likeLayout);
            deleteLayout = itemView.findViewById(R.id.deleteLayout);
            adminDeleteBtn = itemView.findViewById(R.id.adminDeleteBtn);
            reportBtn = itemView.findViewById(R.id.reportBtn);
        }
    }

    private class DeleteReviewOnClick implements View.OnClickListener {

        private Review review;
        private int position;

        public DeleteReviewOnClick(Review review, int position) {
            this.review = review;
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            if (fragment instanceof BookFragment) {
                ((BookFragment) fragment).setReviewed(false);
                ((BookFragment) fragment).setReviewedWithContent(false);
            }

            reviews.remove(position);
            notifyItemRemoved(position);
            db.collection("Reviews").document(review.getReview_id()).delete();

            updateBookParams();
        }

        private void updateBookParams() {
            if (fragment instanceof BookFragment)
                ((BookFragment) fragment).updateAfterReviewDeleted(review.getRank(), review.getReviewer_age());
            if (fragment instanceof ReviewsFragment)
                ((ReviewsFragment) fragment).updateReviewDeletion(review.getRank(), review.getReviewer_age());
        }
    }

}