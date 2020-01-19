package com.reading7.Adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.BookFragment;
import com.reading7.MainActivity;
import com.reading7.Objects.Book;
import com.reading7.Objects.Post;
import com.reading7.PublicProfileFragment;
import com.reading7.R;
import com.reading7.Objects.User;
import com.reading7.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;
import static com.reading7.Utils.RelativeDateDisplay;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    ArrayList<Post> posts;
    Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    User mUser;


    public FeedAdapter(Context context, ArrayList<Post> posts,User mUser) {
        this.posts = posts;
        this.mContext = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.mUser = mUser;


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


/**
     *************************************** BINDERS ********************************************
     */

    private void bindReview(RecyclerView.ViewHolder viewHolder, int i) {

        final ReviewPostHolder holder = (ReviewPostHolder)viewHolder;
        final Post post = posts.get(i);

        String strDate = RelativeDateDisplay(Timestamp.now().toDate().getTime() - post.getPost_time().toDate().getTime());
        holder.postTime.setText(strDate); // FIXME check this

        ArrayList<Integer> avatar_details = post.getUser_avatar();
        Utils.loadAvatar(mContext, holder.profileImage, avatar_details);

        holder.ratingBar.setRating(post.getRank());
        holder.rating.setText(String.valueOf(post.getRank()));
        holder.userName.setText(post.getUser_name());
        holder.bookName.setText("\""+post.getBook_title()+"\"");
        holder.authorName.setText(post.getBook_author());

        if(post.getReview_content().equals(""))
            holder.review_content.setVisibility(View.GONE);
        else {
            holder.review_content.setVisibility(View.VISIBLE);
            holder.review_content.setText(post.getReview_content());
        }

        if(post.getReview_title().equals(""))
            holder.review_title.setVisibility(View.GONE);
        else {
            holder.review_title.setVisibility(View.VISIBLE);
            holder.review_title.setText(post.getReview_title());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.review_content.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }

        Utils.showImage(post.getBook_title(), ((ReviewPostHolder) viewHolder).cover,(Activity)mContext);
        Utils.showImage(post.getBook_title(), ((ReviewPostHolder) viewHolder).coverBackground,(Activity)mContext);

        // set Buttons

        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        final DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users").document(mUser.getEmail());
        final String id = post.getReview_id();
        final String book_title = post.getBook_title();
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
                                    //add like
                                    user.add_like(id);

                                    post.setLikes_count(curr_num+1);
                                    userRef.update("liked_reviews", user.getLiked_reviews());
                                    PostRef.update("likes_count", curr_num + 1);

                                    holder.likeButton.setBackground(mContext.getResources().getDrawable(R.drawable.like_colored));
                                    holder.likesNum.setText(String.valueOf(curr_num + 1));

                                    addNotificationLike(post.getReviewer_email(),book_title,post.getIs_notify());
                                }
                            }
                        });

                    }
                    else Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        OpenProfileOnClick profileListener = new OpenProfileOnClick(post.getReviewer_email());
        holder.profileImage.setOnClickListener(profileListener);
        holder.userName.setOnClickListener(profileListener);

        OpenBookOnClick bookListener = new OpenBookOnClick(post.getBook_title());
        holder.cover.setOnClickListener(bookListener);
        holder.coverBackground.setOnClickListener(bookListener);
        holder.bookName.setOnClickListener(bookListener);
    }


    private void bindWishList(RecyclerView.ViewHolder viewHolder, int i) {

        WishListPostHolder holder = (WishListPostHolder) viewHolder;

        Post post = posts.get(i);

        String strDate = RelativeDateDisplay(Timestamp.now().toDate().getTime() - post.getPost_time().toDate().getTime());
        holder.postTime.setText(strDate);

        ArrayList<Integer> avatar_details = post.getUser_avatar();
        Utils.loadAvatar(mContext, holder.profileImage, avatar_details);

        holder.userName.setText(post.getUser_name());
        holder.bookName.setText("\""+post.getBook_title()+"\"");
        Utils.showImage(post.getBook_title(), holder.cover,(Activity)mContext);
        Utils.showImage(post.getBook_title(), holder.coverBackground,(Activity)mContext);

        // Set Click Listeners //
        OpenProfileOnClick profileListener = new OpenProfileOnClick(post.getUser_email());
        holder.profileImage.setOnClickListener(profileListener);
        holder.userName.setOnClickListener(profileListener);

        OpenBookOnClick bookListener = new OpenBookOnClick(post.getBook_title());
        holder.cover.setOnClickListener(bookListener);
        holder.coverBackground.setOnClickListener(bookListener);
        holder.bookName.setOnClickListener(bookListener);
    }


/**
     *********************************** View Holders *******************************************
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
        Button likeButton;
        TextView likesNum;
        TextView review_content;
        TextView review_title;

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
            likeButton = itemView.findViewById(R.id.likeButton);
            likesNum = itemView.findViewById(R.id.likesNum);
            review_content = itemView.findViewById(R.id.review);
            review_title = itemView.findViewById(R.id.title);
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
            userName =itemView.findViewById(R.id.userName);
            bookName =itemView.findViewById(R.id.bookName);
            postTime =itemView.findViewById(R.id.postTime);
            profileImage = itemView.findViewById(R.id.profileImage);
        }
    }

    //TODO: NewBookPostHolder

/**
     ********************************* OnClick Listeners ****************************************
     */

    private class OpenBookOnClick implements View.OnClickListener {

        private String book_title;

        public OpenBookOnClick(String book_title){
            this.book_title = book_title;
        }

        @Override
        public void onClick(View v) {
            Query bookRef = FirebaseFirestore.getInstance().collection("Books").whereEqualTo("title",this.book_title);
            bookRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            ((MainActivity) mContext).addFragment(new BookFragment(doc.toObject(Book.class)));
                            break;
                        }
                    }
                }
            });
        }
    }

    private class OpenProfileOnClick implements View.OnClickListener {

        private String user_email;

        public OpenProfileOnClick(String user_email){
            this.user_email = user_email;
        }

        @Override
        public void onClick(View v) {
            ((MainActivity)mContext).addFragment(new PublicProfileFragment(user_email));
        }
    }


/**
     *********************************** Other Functions ****************************************
     */

    private void addNotificationLike(String to_email,String book_title,boolean is_notify){
        if(is_notify&& (!(to_email.equals(mAuth.getCurrentUser().getEmail())))) {
            db = FirebaseFirestore.getInstance();

            Map<String, Object> notificationMessegae = new HashMap<>();

            notificationMessegae.put("type", mContext.getResources().getString(R.string.like_notificiation));
            notificationMessegae.put("from", mAuth.getCurrentUser().getEmail());
            notificationMessegae.put("user_name", mUser.getFull_name());
            notificationMessegae.put("book_title", book_title);
            notificationMessegae.put("time", Timestamp.now());
            notificationMessegae.put("user_avatar", mUser.getAvatar_details());


            db.collection("Users/" + to_email + "/Notifications").add(notificationMessegae).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                }
            });
        }
    }
}
