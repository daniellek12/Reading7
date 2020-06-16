package com.reading7.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reading7.FollowingFragment;
import com.reading7.MainActivity;
import com.reading7.Objects.Avatar;
import com.reading7.Objects.User;
import com.reading7.R;
import com.reading7.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.viewHolder> {

    private Context mContext;
    private List<String> mData;
    private FollowingFragment mFragment;

    public FollowingAdapter(Context mContext, FollowingFragment mFragment, List<String> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.mFragment = mFragment;
        setHasStableIds(true);
    }

    @NotNull
    @Override
    public viewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.following_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return mData.get(position).hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public void onBindViewHolder(@NotNull final viewHolder holder, final int position) {
        FirebaseFirestore.getInstance().collection("Users").document(mData.get(position))
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);

                    final String name = user.getFull_name();
                    holder.user_name.setText(name);

                    Avatar avatar = user.getAvatar();
                    avatar.loadIntoImage(mContext, holder.profile_image);

                    Utils.OpenProfileOnClick profileListener = new Utils.OpenProfileOnClick(mContext, user.getEmail());
                    holder.profile_image.setOnClickListener(profileListener);
                    holder.user_name.setOnClickListener(profileListener);

                    initFollowBackButton(holder, user);
                    initRemoveButton(holder, user, position);
                }
            }
        });
    }

    private void initFollowBackButton(final viewHolder holder, final User user) {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final User user_me = ((MainActivity) mContext).getCurrentUser();

        //Display button only for users who follow me and I don't follow back.
        if (user.getFollow_requests().contains(user_me.getEmail())) {
            holder.follow_back.setVisibility(View.VISIBLE);
            holder.follow_back.setText(mContext.getResources().getString(R.string.request_string));
            holder.follow_back.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.black));
        } else if (user.getFollowers().contains(user_me.getEmail())) {
            holder.follow_back.setVisibility(View.GONE);
        } else {
            holder.follow_back.setVisibility(View.VISIBLE);
        }

        holder.follow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!user.getIs_private()) {

                    holder.follow_back.setVisibility(View.GONE);

                    DocumentReference userRef = db.collection("Users").document(user_me.getEmail());
                    userRef.update("following", FieldValue.arrayUnion(user.getEmail()));

                    userRef = db.collection("Users").document(user.getEmail());
                    userRef.update("followers", FieldValue.arrayUnion(user_me.getEmail()));

                    user_me.getFollowing().add(user.getEmail());
                    mFragment.increaseFollowingNum();

                } else {
                    holder.follow_back.setText(mContext.getResources().getString(R.string.request_string));
                    holder.follow_back.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.black));

                    DocumentReference userRef = db.collection("Users").document(user.getEmail());
                    userRef.update("follow_requests", FieldValue.arrayUnion(user_me.getEmail()));
                }

                addNotificationFollow(user);
            }

        });
    }

    private void initRemoveButton(final viewHolder holder, final User user, final int position) {
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                final User user_me = ((MainActivity) mContext).getCurrentUser();

                //Remove a follower
                if (mFragment.getCurrentTabType().equals(FollowingFragment.FollowingFragmentType.FOLLOWERS)) {
                    DocumentReference userRef = db.collection("Users").document(user_me.getEmail());
                    userRef.update("followers", FieldValue.arrayRemove(user.getEmail()));

                    userRef = db.collection("Users").document(user.getEmail());
                    userRef.update("following", FieldValue.arrayRemove(user_me.getEmail()));

                    user_me.getFollowers().remove(user.getEmail());
                    ((MainActivity) mContext).setCurrentUser(user_me);
                    mFragment.updateFollowersNum();
                }

                //Stop follow another user
                if (mFragment.getCurrentTabType().equals(FollowingFragment.FollowingFragmentType.FOLLOWING)) {
                    DocumentReference userRef = db.collection("Users").document(user_me.getEmail());
                    userRef.update("following", FieldValue.arrayRemove(user.getEmail()));

                    userRef = db.collection("Users").document(user.getEmail());
                    userRef.update("followers", FieldValue.arrayRemove(user_me.getEmail()));

                    user_me.getFollowing().remove(user.getEmail());
                    ((MainActivity) mContext).setCurrentUser(user_me);
                    mFragment.decreaseFollowingNum();
                }

                notifyItemRemoved(position);
            }
        });
    }

    private void addNotificationFollow(User user) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> notificationMessage = new HashMap<>();

        if (!user.getIs_private()) {
            notificationMessage.put("type", mContext.getResources().getString(R.string.follow_notificiation_public));
            notificationMessage.put("book_title", "follow_notification_public");//not relvant
        } else {
            notificationMessage.put("type", mContext.getResources().getString(R.string.follow_notificiation_private));
            notificationMessage.put("book_title", "follow_notification_private");//not relvant
        }

        notificationMessage.put("from", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        notificationMessage.put("time", Timestamp.now());

        db.collection("Users/" + user.getEmail() + "/Notifications").add(notificationMessage);
    }


    public static class viewHolder extends RecyclerView.ViewHolder {

        TextView user_name;
        CircleImageView profile_image;
        Button remove;
        Button follow_back;

        public viewHolder(View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.userName);
            profile_image = itemView.findViewById(R.id.profileImage);
            remove = itemView.findViewById(R.id.remove_button);
            follow_back = itemView.findViewById(R.id.followBack_button);
        }
    }

}

