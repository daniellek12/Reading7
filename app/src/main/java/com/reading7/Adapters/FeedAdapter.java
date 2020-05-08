package com.reading7.Adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Dialogs.AddCommentDialog;
import com.reading7.MainActivity;
import com.reading7.Objects.Avatar;
import com.reading7.Objects.Post;
import com.reading7.Objects.PostType;
import com.reading7.Objects.Review;
import com.reading7.Objects.User;
import com.reading7.R;
import com.reading7.ReviewCommentsFragment;
import com.reading7.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;
import static com.reading7.Utils.RelativeDateDisplay;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Post> posts;
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Fragment fragment;


    public FeedAdapter(Context context, Fragment fragment, ArrayList<Post> posts) {
        this.posts = posts;
        this.mContext = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.fragment = fragment;
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

        switch (posts.get(i).getType()) {
            case Review:
                return 0;
            case WishList:
                return 1;
            case Recommendation:
                return 2;
            case NewBook:
                return 3;
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


    /**
     * ************************************** BINDERS ********************************************
     */

    private void bindReview(RecyclerView.ViewHolder viewHolder, int i) {

        final ReviewPostHolder holder = (ReviewPostHolder) viewHolder;
        final Post post = posts.get(i);

        bindReviewUser(holder, post.getReviewer_email());

        String strDate = RelativeDateDisplay(Timestamp.now().toDate().getTime() - post.getPost_time().toDate().getTime());
        holder.postTime.setText(strDate);

        holder.ratingBar.setRating(post.getRank());
        holder.rating.setText(String.valueOf(post.getRank()));
        holder.bookName.setText("\"" + post.getBook_title() + "\"");
        holder.authorName.setText(post.getBook_author());
        holder.likesNum.setText(Integer.toString(post.getLikes_count()));
        holder.commentsNum.setText(String.valueOf(post.getComments().size()));

        if (post.getReview_content().equals(""))
            holder.review_content.setVisibility(View.GONE);
        else {
            holder.review_content.setVisibility(View.VISIBLE);
            holder.review_content.setText(post.getReview_content());
        }

        if (post.getReview_title().equals(""))
            holder.review_title.setVisibility(View.GONE);
        else {
            holder.review_title.setVisibility(View.VISIBLE);
            holder.review_title.setText(post.getReview_title());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.review_content.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }

        Utils.showImage(post.getBook_title(), ((ReviewPostHolder) viewHolder).cover, (Activity) mContext);
        Utils.showImage(post.getBook_title(), ((ReviewPostHolder) viewHolder).coverBackground, (Activity) mContext);

        initLikeMechanics(holder, i);
        initAddCommentButton(holder, i);

        Utils.OpenBookOnClick bookListener = new Utils.OpenBookOnClick(mContext, post.getBook_title());
        holder.cover.setOnClickListener(bookListener);
        holder.coverBackground.setOnClickListener(bookListener);
        holder.bookName.setOnClickListener(bookListener);

        holder.countersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReviewCommentsFragment mReviewCommentsFragment = new ReviewCommentsFragment(post.toReview());
                mReviewCommentsFragment.setTargetFragment(fragment, 303);
                ((MainActivity) mContext).addFragment(mReviewCommentsFragment);
            }
        });
    }

    private void bindReviewUser(final FeedAdapter.ReviewPostHolder holder, final String email) {
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

    private void bindWishList(RecyclerView.ViewHolder viewHolder, int i) {

        WishListPostHolder holder = (WishListPostHolder) viewHolder;

        Post post = posts.get(i);

        bindWishlistUser(holder, post.getUser_email());

        String strDate = RelativeDateDisplay(Timestamp.now().toDate().getTime() - post.getPost_time().toDate().getTime());
        holder.postTime.setText(strDate);

        holder.bookName.setText("\"" + post.getBook_title() + "\"");
        Utils.showImage(post.getBook_title(), holder.cover, (Activity) mContext);
        Utils.showImage(post.getBook_title(), holder.coverBackground, (Activity) mContext);

        // Set Click Listeners //
        Utils.OpenBookOnClick bookListener = new Utils.OpenBookOnClick(mContext, post.getBook_title());
        holder.cover.setOnClickListener(bookListener);
        holder.coverBackground.setOnClickListener(bookListener);
        holder.bookName.setOnClickListener(bookListener);
    }

    private void bindWishlistUser(final FeedAdapter.WishListPostHolder holder, final String email) {
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


    /**
     * ********************************** View Holders *******************************************
     */

    public class ReviewPostHolder extends RecyclerView.ViewHolder {

        TextView postTime;
        TextView userName;
        CircleImageView profileImage;
        RatingBar ratingBar;
        TextView rating;
        ImageView cover;
        ImageView coverBackground;
        TextView bookName;
        TextView authorName;
        LinearLayout addCommentButton;
        LinearLayout likeButton;
        TextView likesNum;
        TextView commentsNum;
        TextView review_content;
        TextView review_title;
        RelativeLayout countersLayout;

        public ReviewPostHolder(@NonNull View itemView) {
            super(itemView);

            postTime = itemView.findViewById(R.id.postTime);
            userName = itemView.findViewById(R.id.userName);
            profileImage = itemView.findViewById(R.id.profileImage);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            rating = itemView.findViewById(R.id.rating);
            cover = itemView.findViewById(R.id.coverImage);
            coverBackground = itemView.findViewById(R.id.coverBackground);
            bookName = itemView.findViewById(R.id.bookTitle);
            authorName = itemView.findViewById(R.id.authorName);
            addCommentButton = itemView.findViewById(R.id.button_add_comment);
            likeButton = itemView.findViewById(R.id.likeButton);
            likesNum = itemView.findViewById(R.id.likeNum);
            commentsNum = itemView.findViewById(R.id.commentsNum);
            review_content = itemView.findViewById(R.id.review);
            review_title = itemView.findViewById(R.id.title);
            countersLayout = itemView.findViewById(R.id.activityCountersLayout);
        }
    }

    public class WishListPostHolder extends RecyclerView.ViewHolder {

        ImageView cover;
        ImageView coverBackground;
        TextView userName;
        TextView postTime;
        TextView bookName;
        CircleImageView profileImage;


        public WishListPostHolder(@NonNull View itemView) {
            super(itemView);

            cover = itemView.findViewById(R.id.coverImage);
            coverBackground = itemView.findViewById(R.id.coverBackground);
            userName = itemView.findViewById(R.id.userName);
            bookName = itemView.findViewById(R.id.bookName);
            postTime = itemView.findViewById(R.id.postTime);
            profileImage = itemView.findViewById(R.id.profileImage);
        }
    }


    /**
     * ********************************** Other Functions ****************************************
     */

    private void addNotificationLike(String to_email, String book_title) {
        if ((!(to_email.equals(mAuth.getCurrentUser().getEmail())))) {
            db = FirebaseFirestore.getInstance();

            Map<String, Object> notificationMessegae = new HashMap<>();

            notificationMessegae.put("type", mContext.getResources().getString(R.string.like_notificiation));
            notificationMessegae.put("from", mAuth.getCurrentUser().getEmail());
            notificationMessegae.put("book_title", book_title);
            notificationMessegae.put("time", Timestamp.now());


            db.collection("Users/" + to_email + "/Notifications").add(notificationMessegae).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                }
            });
        }
    }

    private void setLikeButton(FeedAdapter.ReviewPostHolder viewHolder, boolean liked) {

        ImageView buttonIcon = viewHolder.likeButton.findViewById(R.id.likeIcon);
        TextView buttonText = viewHolder.likeButton.findViewById(R.id.likeText);

        if (liked) {
            buttonText.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            buttonIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.like_colored));
        } else {
            buttonText.setTextColor(mContext.getResources().getColor(R.color.darkGrey));
            buttonIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.like));
        }
    }

    private void initAddCommentButton(final FeedAdapter.ReviewPostHolder holder, int position) {

        final Post post = posts.get(position);

        holder.addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReviewCommentsFragment mReviewCommentsFragment = new ReviewCommentsFragment(post.toReview());
                mReviewCommentsFragment.setTargetFragment(fragment, 303);
                ((MainActivity) mContext).addFragment(mReviewCommentsFragment);

                AddCommentDialog dialog = new AddCommentDialog();
                Bundle args = new Bundle();
                args.putString("review_id", post.getReview_id());
                dialog.setArguments(args);
                dialog.setTargetFragment(mReviewCommentsFragment, 303);
                dialog.show(fragment.getActivity().getSupportFragmentManager(), "example dialog");

            }
        });
    }

    private void initLikeMechanics(final FeedAdapter.ReviewPostHolder holder, int position) {

        final Post post = posts.get(position);
        final User user = ((MainActivity) mContext).getCurrentUser();

        if (user.getLiked_reviews().contains(post.getReview_id()))
            setLikeButton(holder, true);
        else
            setLikeButton(holder, false);

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.getLiked_reviews().contains(post.getReview_id())) {
                    user.remove_like(post.getReview_id());
                    post.reduceLike();

                    db.collection("Users").document(user.getEmail()).update("liked_reviews", user.getLiked_reviews());
                    db.collection("Reviews").document(post.getReview_id()).update("likes_count", post.getLikes_count());

                    holder.likesNum.setText(String.valueOf(post.getLikes_count()));
                    setLikeButton(holder, false);

                } else {
                    user.add_like(post.getReview_id());
                    post.addLike();

                    db.collection("Users").document(user.getEmail()).update("liked_reviews", user.getLiked_reviews());
                    db.collection("Reviews").document(post.getReview_id()).update("likes_count", post.getLikes_count());

                    holder.likesNum.setText(String.valueOf(post.getLikes_count()));
                    setLikeButton(holder, true);
                    addNotificationLike(post.getReviewer_email(), post.getBook_title());
                }

                ((MainActivity) mContext).setCurrentUser(user);
            }
        });

    }

    public void notifyReviewCommentsChanged(String review_id) {

        for (int i = 0; i < getItemCount(); i++) {

            final int finalI = i;
            final Post post = posts.get(i);

            if (post.getType().equals(PostType.Review) && post.getReview_id().equals(review_id)) {

                final Review review = post.toReview();
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
