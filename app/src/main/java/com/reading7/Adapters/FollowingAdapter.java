package com.reading7.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Objects.Avatar;
import com.reading7.Objects.Notification;
import com.reading7.Objects.User;
import com.reading7.R;
import com.reading7.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.MyViewHolder> {

    private Context mContext;
    private List<String> mData;
    private String type;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public FollowingAdapter(Context mContext, List<String> mData, String type) {
        setHasStableIds(true);
        this.mContext = mContext;
        this.mData = mData;
        this.type = type;
    }




    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.following_item,parent,false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        CollectionReference requestCollectionRef = db.collection("Users");
        Query requestQuery = requestCollectionRef.whereEqualTo("email", mData.get(position));
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    User user = new User();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        user = document.toObject(User.class);
                    }

                    final String name = user.getFull_name();
                    holder.user_name.setText(name);

                    Avatar avatar = user.getAvatar();
                    avatar.loadIntoImage(mContext, holder.profile_image);
                    Utils.OpenProfileOnClick profileListener = new Utils.OpenProfileOnClick(mContext, user.getEmail());
                    holder.profile_image.setOnClickListener(profileListener);
                    holder.user_name.setOnClickListener(profileListener);

//                    if(type == "followers"){
//                        if (!user.getFollowers().contains(FirebaseAuth.getInstance().getCurrentUser())){
//                            holder.follow.setText("עקוב");
//                            holder.follow.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorAccent));
//                        }
//                    }

                    final FirebaseUser user_me = mAuth.getCurrentUser();
                    initFollowButton(mContext, user, user_me, holder.follow, mAuth);

                }
            }
        });


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

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView user_name;
        CircleImageView profile_image;
        Button follow;


        public MyViewHolder(View itemView) {
            super(itemView);

            user_name = itemView.findViewById(R.id.userName);
            profile_image = itemView.findViewById(R.id.profileImage);
            follow = itemView.findViewById(R.id.follow_button);

        }
    }

    private static void initFollowButton(final Context mContext, final User user, final FirebaseUser user_me, final Button follow, final FirebaseAuth mAuth) {

        final String follow_string = mContext.getResources().getString(R.string.follow_button);
        final String unfollow_string = mContext.getResources().getString(R.string.unfollow_button);
        final String request_string = mContext.getResources().getString(R.string.request_string);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();


        if (user.getFollowers().contains(user_me.getEmail())) {
            follow.setText(unfollow_string);
            follow.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorPrimary));

        } else if (user.getFollow_requests().contains(user_me.getEmail())) {
            follow.setText(request_string);
            follow.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.black));
        } else {
            follow.setText(follow_string);
            follow.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorAccent));
        }

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (follow.getText().equals(follow_string)) { // follow the user
                    if (user_me.getEmail().equals(user.getEmail()))
                        throw new AssertionError("YOU CANT FOLLOW YOURSELF!!!!");
                    if (!user.getIs_private()) {

                        follow.setText(unfollow_string);
                        follow.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorPrimary));

                        DocumentReference userRef = db.collection("Users").document(user_me.getEmail());
                        userRef.update("following", FieldValue.arrayUnion(user.getEmail()));

                        userRef = db.collection("Users").document(user.getEmail());
                        userRef.update("followers", FieldValue.arrayUnion(user_me.getEmail()));

                    } else { //user is private!
                        follow.setText(request_string);
                        follow.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.black));

                        DocumentReference userRef;

                        userRef = db.collection("Users").document(user.getEmail());
                        userRef.update("follow_requests", FieldValue.arrayUnion(user_me.getEmail()));
                    }
                    addNotificationFollow(user.getEmail(), user.getIs_private(), mContext, mAuth);

                } else { // already following / requested
                    follow.setText(follow_string);
                    follow.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorAccent));

                    DocumentReference userRef = db.collection("Users").document(user_me.getEmail());
                    userRef.update("following", FieldValue.arrayRemove(user.getEmail()));

                    userRef = db.collection("Users").document(user.getEmail());
                    userRef.update("followers", FieldValue.arrayRemove(user_me.getEmail()));

                    userRef = db.collection("Users").document(user.getEmail());
                    userRef.update("follow_requests", FieldValue.arrayRemove(user_me.getEmail()));

                    //if user is private and i stopped following him/wait for him to approve my request- add deletion of notifications
                    //may need to add enable disable..
                    CollectionReference requestsRef = db.collection("Users").document(user.getEmail()).collection("Notifications");
                    Query requestQuery = requestsRef.whereEqualTo("from", user_me.getEmail());
                    requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.toObject(Notification.class).getType().equals(mContext.getResources().getString(R.string.follow_notificiation_public))
                                            ||document.toObject(Notification.class).getType().equals(mContext.getResources().getString(R.string.follow_notificiation_private)))
                                        document.getReference().delete();
                                }
                            }
                        }
                    });


                }

            }
        });
    }

    private static void addNotificationFollow(String to_email, boolean is_private, Context mContext, FirebaseAuth mAuth) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> notificationMessegae = new HashMap<>();

        if (!is_private) {
            notificationMessegae.put("type", mContext.getResources().getString(R.string.follow_notificiation_public));
            notificationMessegae.put("book_title", "follow_notification_public");//not relvant

        } else {
            notificationMessegae.put("type", mContext.getResources().getString(R.string.follow_notificiation_private));
            notificationMessegae.put("book_title", "follow_notification_private");//not relvant

        }

        notificationMessegae.put("from", mAuth.getCurrentUser().getEmail());
        notificationMessegae.put("time", Timestamp.now());


        db.collection("Users/" + to_email + "/Notifications").add(notificationMessegae).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
            }
        });

    }

}

