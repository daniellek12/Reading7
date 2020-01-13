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

public class NotificationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


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






    @Override
    public int getItemCount() {
        return notifications.size();
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
                ((MainActivity) mContext).loadFragment(new PublicProfileFragment(user_email));
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
        int i;

        public DeleteNotificationOnClick(Notification notification, int i) {
            this.notification = notification;
            this.i = i;
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
                            notifications.remove(document.toObject(Notification.class));
                            notifyItemRemoved(i);

                        }
                    } else
                        Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


    @Override
    public int getItemViewType(int i) {

       if(notifications.get(i).getType().equals(mContext.getResources().getString(R.string.follow_notificiation_private))) {
           return 0;
       }
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
                return new PrivateNotificationListAdapter(view);

            case 1:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_item, viewGroup, false);
                return new PublicNotificationListAdapter(view);

        }

        return null;
    }
/*________________________BINDS_______________________*/

    public void bindPrivate(RecyclerView.ViewHolder viewHolder, int i) {

        NotificationListAdapter.PrivateNotificationListAdapter holder = (NotificationListAdapter.PrivateNotificationListAdapter) viewHolder;

        final Notification notification = notifications.get(i);

        Utils.loadAvatar(mContext, holder.profileImage, notification.getUser_avatar());
        holder.userName.setText(notification.getUser_name());

        Date date = notification.getTime().toDate();
        String strDate = RelativeDateDisplay(Timestamp.now().toDate().getTime() - date.getTime());
        holder.addingTime.setText(strDate);

        holder.title.setText("Notification from "+(notification.getUser_name()));
        holder.clickNotificationBtn.setOnClickListener(new OpenProfileOnClick(notification.getFrom()));
        holder.content.setText((notification.getType()));


        holder.profileImage.setOnClickListener(new OpenProfileOnClick(notification.getFrom()));
        holder.userName.setOnClickListener(new OpenProfileOnClick(notification.getFrom()));
        holder.deleteBtn.setOnClickListener(new DeleteNotificationOnClick(notification,i));


    }

    public void bindNormal(RecyclerView.ViewHolder viewHolder, int i) {
        NotificationListAdapter.PublicNotificationListAdapter holder = (NotificationListAdapter.PublicNotificationListAdapter) viewHolder;

        final Notification notification = notifications.get(i);

        Utils.loadAvatar(mContext, holder.profileImage, notification.getUser_avatar());
        holder.userName.setText(notification.getUser_name());

        Date date = notification.getTime().toDate();
        String strDate = RelativeDateDisplay(Timestamp.now().toDate().getTime() - date.getTime());
        holder.addingTime.setText(strDate);

        holder.title.setText("Notification from "+(notification.getUser_name()));
        if(notification.getBook_title().equals("follow_notification_public")||notification.getBook_title().equals("follow_notification_private")) {
            holder.clickNotificationBtn.setOnClickListener(new OpenProfileOnClick(notification.getFrom()));
            holder.content.setText((notification.getType()));

        }
        else {
            holder.clickNotificationBtn.setOnClickListener(new OpenBookOnClick(notification.getBook_title()));
            holder.content.setText((notification.getType())+" על הספר "+notification.getBook_title());

        }
        holder.profileImage.setOnClickListener(new OpenProfileOnClick(notification.getFrom()));
        holder.userName.setOnClickListener(new OpenProfileOnClick(notification.getFrom()));
        holder.deleteBtn.setOnClickListener(new DeleteNotificationOnClick(notification,i));


    }

  /*_____________________________________________________________________________________*/


    /*-------------------------------------- View Holders ----------------------------------------*/


    public class PrivateNotificationListAdapter extends RecyclerView.ViewHolder {

        TextView userName;
        TextView addingTime;
        TextView title;
        TextView content;
        CircleImageView profileImage;
        ImageButton deleteBtn;
        ImageButton clickNotificationBtn;

        public PrivateNotificationListAdapter(@NonNull View itemView) {
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

    public class PublicNotificationListAdapter extends RecyclerView.ViewHolder {

        TextView userName;
        TextView addingTime;
        TextView title;
        TextView content;
        CircleImageView profileImage;
        ImageButton deleteBtn;
        ImageButton clickNotificationBtn;

        public PublicNotificationListAdapter(@NonNull View itemView) {
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


}
