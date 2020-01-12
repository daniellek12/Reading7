package com.reading7.Adapters;


import android.app.Activity;
import android.content.Context;
import android.icu.text.RelativeDateTimeFormatter;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.reading7.BookFragment;
import com.reading7.MainActivity;
import com.reading7.Objects.Book;
import com.reading7.Objects.Notification;
import com.reading7.Objects.User;
import com.reading7.ProfileFragment;
import com.reading7.PublicProfileFragment;
import com.reading7.R;
import com.reading7.Objects.Review;
import com.reading7.Utils;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.reading7.Utils.RelativeDateDisplay;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {


   List<Notification> notifications;
    Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseFirestore db;
    private  Activity mActivity;

    public NotificationListAdapter(Context context, List<Notification> notifications, Activity activity) {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        this.notifications = notifications;
        this.mContext = context;
        this.db = FirebaseFirestore.getInstance();
        this.mActivity= activity;

    }

    @NonNull
    @Override
    public NotificationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_item, viewGroup, false);
        return new NotificationListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationListAdapter.ViewHolder viewHolder, int i) {

        final Notification notification = notifications.get(i);

        Utils.loadAvatar(mContext, viewHolder.profileImage, notification.getUser_avatar());
        viewHolder.userName.setText(notification.getUser_name());

        Date date = notification.getTime().toDate();
        String strDate = RelativeDateDisplay(Timestamp.now().toDate().getTime() - date.getTime());
        viewHolder.addingTime.setText(strDate);

        viewHolder.title.setText("Notification from "+(notification.getUser_name()));
        viewHolder.content.setText((notification.getType()));
        viewHolder.clickNotificationBtn.setOnClickListener(new OpenBookOnClick(notification.getBook_title()));
        viewHolder.profileImage.setOnClickListener(new OpenProfileOnClick(notification.getFrom()));
        viewHolder.userName.setOnClickListener(new OpenProfileOnClick(notification.getFrom()));
        viewHolder.deleteBtn.setOnClickListener(new DeleteNotificationOnClick(notification));


    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView userName;
        TextView addingTime;
        TextView title;
        TextView content;
        CircleImageView profileImage;
        ImageButton deleteBtn;
        ImageButton clickNotificationBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            userName = itemView.findViewById(R.id.userName);
            addingTime = itemView.findViewById(R.id.notificationTime);
            content = itemView.findViewById(R.id.content);
            title = itemView.findViewById(R.id.title);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            clickNotificationBtn = itemView.findViewById(R.id.clickNotificationBtn);
        }
    }


    private class OpenProfileOnClick implements View.OnClickListener {

        private String user_email;

        public OpenProfileOnClick(String user_email){
            this.user_email = user_email;
        }

        @Override
        public void onClick(View v) {
            if(user_email.equals(mAuth.getCurrentUser().getEmail()))
                ((MainActivity) mContext).loadFragment(new ProfileFragment());
            else
                ((MainActivity) mContext).addFragment(new PublicProfileFragment(user_email));
        }
    }


    private class OpenBookOnClick implements View.OnClickListener {

        private String book_title;

        public OpenBookOnClick(String book_title){
            this.book_title = book_title;
        }

        @Override
        public void onClick(View v) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference requestCollectionRef = db.collection("Books");
            Query requestQuery = requestCollectionRef.whereEqualTo("title",book_title);
            requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        Book b = null;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            b = document.toObject(Book.class);
                            ((MainActivity)mActivity).addFragment(new BookFragment(b));
                            return;
                        }

                    }
                }
            });
        }
    }


    private class DeleteNotificationOnClick implements View.OnClickListener {

        Notification notification;

        public DeleteNotificationOnClick(Notification notification) {
            this.notification = notification;
        }

        @Override
        public void onClick(View v) {

            CollectionReference requestsRef = db.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Notifications");
            Query requestQuery = requestsRef.whereEqualTo("time", notification.getTime()).whereEqualTo("from", notification.getFrom()).whereEqualTo("type", notification.getType());
            requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                    } else
                        Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


}
