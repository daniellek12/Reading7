package com.reading7.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.MainActivity;
import com.reading7.Objects.Avatar;
import com.reading7.Objects.Notification;
import com.reading7.Objects.Review;
import com.reading7.Objects.User;
import com.reading7.R;
import com.reading7.ReviewCommentsFragment;
import com.reading7.Utils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.reading7.Utils.RelativeDateDisplay;

public class NotificationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Notification> notifications;
    Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Activity mActivity;

    public NotificationListAdapter(Context context, List<Notification> notifications, Activity activity) {
        this.mAuth = FirebaseAuth.getInstance();
        this.notifications = notifications;
        this.mContext = context;
        this.db = FirebaseFirestore.getInstance();
        this.mActivity = activity;
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    @Override
    public int getItemViewType(int i) {

        if (notifications.get(i).getType().equals(mContext.getResources().getString(R.string.follow_notificiation_private)))
            return 0;

        return 1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        switch (viewHolder.getItemViewType()) {

            case 0:
                bindPrivate(viewHolder, i);
                break;

            case 1:
                bindNormal(viewHolder, i);
                break;
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = null;
        switch (viewType) {

            case 0:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_private_item, viewGroup, false);
                return new PrivateNotificationHolder(view);

            case 1:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_item, viewGroup, false);
                return new PublicNotificationHolder(view);
        }

        return null;
    }


    /**
     * ************************************** BINDERS ********************************************
     */


    public void bindPrivate(RecyclerView.ViewHolder viewHolder, int i) {

        final int j = i;
        final NotificationListAdapter.PrivateNotificationHolder holder = (NotificationListAdapter.PrivateNotificationHolder) viewHolder;

        final Notification notification = notifications.get(i);

        bindPrivateUser(holder, notification.getFrom());

        Date date = notification.getTime().toDate();
        String strDate = RelativeDateDisplay(Timestamp.now().toDate().getTime() - date.getTime());
        holder.addingTime.setText(strDate);

        holder.clickNotificationBtn.setOnClickListener(new Utils.OpenProfileOnClick(mContext, notification.getFrom()));
        holder.content.setText((notification.getType()));

        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String followed_mail = mAuth.getCurrentUser().getEmail();
                String follower_mail = notification.getFrom();
                DocumentReference userRef;
                userRef = db.collection("Users").document(followed_mail);
                userRef.update("follow_requests", FieldValue.arrayRemove(follower_mail));

                userRef = db.collection("Users").document(followed_mail);
                userRef.update("followers", FieldValue.arrayUnion(follower_mail));

                userRef = db.collection("Users").document(follower_mail);
                userRef.update("following", FieldValue.arrayUnion(followed_mail));

                //update UI
                holder.content.setText(" כעת עוקב אחריך");
                holder.acceptBtn.setVisibility(View.GONE);

                UpdatePrivateNotification(j);

                //add notification for approving
                Query followeRef = db.collection("Users").whereEqualTo("email", follower_mail);
                followeRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                User u = (doc.toObject(User.class));
                                addNotificationRequestApproved(u.getEmail(), u.getIs_notify());
                                break;
                            }
                        }
                    }
                });

            }
        });
    }

    private void bindPrivateUser(final NotificationListAdapter.PrivateNotificationHolder holder, final String email) {

        DocumentReference userReference = db.collection("Users").document(email);
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);

                holder.userName.setText(user.getFull_name());

                Avatar avatar = user.getAvatar();
                avatar.loadIntoImage(mContext, holder.profileImage);

                Utils.OpenProfileOnClick profileListener = new Utils.OpenProfileOnClick(mContext, email);
                holder.profileImage.setOnClickListener(profileListener);
                holder.userName.setOnClickListener(profileListener);
            }
        });
    }


    public void bindNormal(RecyclerView.ViewHolder viewHolder, int i) {

        NotificationListAdapter.PublicNotificationHolder holder = (NotificationListAdapter.PublicNotificationHolder) viewHolder;

        final Notification notification = notifications.get(i);

        bindNormalUser(holder, notification.getFrom());

        Date date = notification.getTime().toDate();
        String strDate = RelativeDateDisplay(Timestamp.now().toDate().getTime() - date.getTime());
        holder.addingTime.setText(strDate);

        if (notification.getBook_title().equals("request_approved_notification") || notification.getBook_title().equals("follow_notification_public") || notification.getBook_title().equals("follow_notification_accepted")) {
            holder.clickNotificationBtn.setOnClickListener(new Utils.OpenProfileOnClick(mContext, notification.getFrom()));
            holder.content.setText((notification.getType()));
            return;

        }
        if (notification.getType().equals(mContext.getResources().getString(R.string.invite_notificiation))) {
            holder.clickNotificationBtn.setOnClickListener(new Utils.OpenBookOnClick(mContext, notification.getBook_title()));
            holder.content.setText((notification.getType()) + " על הספר " + notification.getBook_title());

        } else {
            holder.clickNotificationBtn.setOnClickListener(new OpenReviewOnBookOnClick(notification.getBook_title()));
            holder.content.setText((notification.getType()) + " על הספר " + notification.getBook_title());
        }
    }

    private void bindNormalUser(final NotificationListAdapter.PublicNotificationHolder holder, final String email) {

        DocumentReference userReference = db.collection("Users").document(email);
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);

                holder.userName.setText(user.getFull_name());

                Avatar avatar = user.getAvatar();
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


    public class PrivateNotificationHolder extends RecyclerView.ViewHolder {

        TextView userName;
        TextView addingTime;
        TextView content;
        CircleImageView profileImage;
        Button acceptBtn;
        RelativeLayout clickNotificationBtn;

        public PrivateNotificationHolder(@NonNull final View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            userName = itemView.findViewById(R.id.userName);
            addingTime = itemView.findViewById(R.id.notificationTime);
            content = itemView.findViewById(R.id.content);
            clickNotificationBtn = itemView.findViewById(R.id.clickNotificationBtn);
            acceptBtn = itemView.findViewById(R.id.accept);
        }

    }

    public class PublicNotificationHolder extends RecyclerView.ViewHolder {

        TextView userName;
        TextView addingTime;
        TextView content;
        CircleImageView profileImage;
        RelativeLayout clickNotificationBtn;

        public PublicNotificationHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            userName = itemView.findViewById(R.id.userName);
            addingTime = itemView.findViewById(R.id.notificationTime);
            content = itemView.findViewById(R.id.content);
            clickNotificationBtn = itemView.findViewById(R.id.clickNotificationBtn);
        }
    }


    /**
     * ********************************** Other Functions *******************************************
     */

    private void addNotificationRequestApproved(String to_email, boolean is_notify) {

        if (is_notify) {

            Map<String, Object> notificationMessegae = new HashMap<>();

            notificationMessegae.put("type", mContext.getResources().getString(R.string.request_approved_notificiation));
            notificationMessegae.put("book_title", "request_approved_notification");//not relvant
            notificationMessegae.put("from", mAuth.getCurrentUser().getEmail());
            notificationMessegae.put("user_name", ((MainActivity) mContext).getCurrentUser().getFull_name());
            notificationMessegae.put("time", Timestamp.now());
            notificationMessegae.put("user_avatar", ((MainActivity) mContext).getCurrentUser().getAvatar());

            db.collection("Users/" + to_email + "/Notifications").add(notificationMessegae);
        }
    }

    private class OpenReviewOnBookOnClick implements View.OnClickListener {

        private String book_title;

        public OpenReviewOnBookOnClick(String book_title) {
            this.book_title = book_title;
        }

        @Override
        public void onClick(View v) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference requestCollectionRef = db.collection("Reviews");
            Query requestQuery = requestCollectionRef.whereEqualTo("reviewer_email", mAuth.getCurrentUser().getEmail()).whereEqualTo("book_title", book_title);
            requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Review review = null;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            review = document.toObject(Review.class);
                            ((MainActivity) mActivity).loadFragment(new ReviewCommentsFragment(review));
                        }
                    } else
                        Toast.makeText(mContext, "מחקת את הביקורת שלך על ספר זה", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void deleteNotification(final int position) {

        final Notification notification = notifications.get(position);
        CollectionReference requestsRef = db.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Notifications");
        Query requestQuery = requestsRef.whereEqualTo("book_title", notification.getBook_title()).whereEqualTo("from", notification.getFrom()).whereEqualTo("type", notification.getType());
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().delete();
                        notifications.remove(document.toObject(Notification.class));
                        notifyItemRemoved(position);

                        if (notification.getType().equals(mContext.getResources().getString(R.string.follow_notificiation_private))) {//decline to request
                            DocumentReference userRef = db.collection("Users").document(mAuth.getCurrentUser().getEmail());
                            userRef.update("follow_requests", FieldValue.arrayRemove(notification.getFrom()));
                        }
                    }
                } else Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void UpdatePrivateNotification(final int position) {

        final Notification notification = notifications.get(position);
        /*
        notification.setBook_title("follow_notification_public");
        notification.setType(mContext.getResources().getString(R.string.follow_notificiation_public));
        Collections.sort(notifications, new Notification.SortByDate());
        notifyDataSetChanged();*/

        CollectionReference requestsRef = db.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Notifications");
        Query requestQuery = requestsRef.whereEqualTo("time", notification.getTime()).whereEqualTo("from", notification.getFrom()).whereEqualTo("type", notification.getType());
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int first = 1;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (first == 1) {
                            final Map<String, Object> updates = new HashMap<String, Object>();

                            updates.put("book_title", "follow_notification_accepted");
                            updates.put("type", mContext.getResources().getString(R.string.follow_notificiation_public));
                            document.getReference().update(updates);
                            first = 0;
                        } else {
                            document.getReference().delete();
                        }
                    }
                } else Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
