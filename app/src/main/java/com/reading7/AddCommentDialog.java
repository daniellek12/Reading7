package com.reading7;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Objects.Comment;
import com.reading7.Objects.Review;
import com.reading7.Objects.User;

import java.util.HashMap;
import java.util.Map;

public class AddCommentDialog extends AppCompatDialogFragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Comment mComment;
    private View view;
    private Review mReview;
    private Context mContext;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        final String review_id = getArguments().getString("review_id");
        final String commenter_email = mAuth.getCurrentUser().getEmail();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        view = getActivity().getLayoutInflater().inflate(R.layout.add_comment_dialog, null);
        final EditText contentText = view.findViewById(R.id.commentContent);

        mReview = null;

        CollectionReference requestCollectionRef = db.collection("Reviews");
        Query requestQuery = requestCollectionRef.whereEqualTo("review_id", review_id);
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mReview = document.toObject(Review.class);
                    }
                    if (mReview != null) {
                        mComment = mReview.getComments().get(commenter_email);
                        if (mComment != null) {
                            contentText.setText(mComment.getComment_content());
                            ((Button) view.findViewById(R.id.ok)).setText("שמור שינויים");
                        }
                    }
                }
            }
        });

        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String comment_content = contentText.getText().toString().trim();

                CollectionReference requestCollectionRef = db.collection("Users");
                Query requestQuery = requestCollectionRef.whereEqualTo("email", commenter_email);
                requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = new User();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                user = document.toObject(User.class);
                            }
                            // Creating new comment
                            mComment = new Comment(review_id, commenter_email, comment_content , Timestamp.now(), user.getFull_name(), user.getAvatar_details(), user.getIs_notify());

                            // Update comment in DB
                            DocumentReference ref = db.collection("Reviews").document(review_id);
                            final Map<String, Object> updates = new HashMap<String, Object>();
                            mReview.addComment(mComment);
                            updates.put("comments", mReview.getComments());
                            ref.update(updates);
                            addNotificationComment(user,mReview.getReviewer_email(),mReview.getBook_title(),mReview.getIs_notify());
                        }
                    }
                });

                dismiss();
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    private void addNotificationComment(User user,String to_email,String book_title,boolean is_notify){
        if(is_notify&& (!(to_email.equals(mAuth.getCurrentUser().getEmail())))) {
            db = FirebaseFirestore.getInstance();

            Map<String, Object> notificationMessegae = new HashMap<>();

            notificationMessegae.put("type","הגיב על הביקורת שלך");
            notificationMessegae.put("from", user.getEmail());
            notificationMessegae.put("user_name",user.getFull_name());
            notificationMessegae.put("book_title", book_title);
            notificationMessegae.put("time", Timestamp.now());
            notificationMessegae.put("user_avatar", user.getAvatar_details());


            db.collection("Users/" + to_email + "/Notifications").add(notificationMessegae).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                }
            });
        }
    }
}
