package com.reading7.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.reading7.R;

import java.util.HashMap;
import java.util.Map;

public class AddCommentDialog extends AppCompatDialogFragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Comment mComment;
    private View view;
    private Review mReview = null;
    private Context mContext;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        view = getActivity().getLayoutInflater().inflate(R.layout.add_comment_dialog, null);
        final String review_id = getArguments().getString("review_id");

        isCommentAlreadyExist(view, review_id, mAuth.getCurrentUser().getEmail());
        initSaveButton(view, review_id);
        initCancelButton(view);

        builder.setView(view);
        return builder.create();
    }


    private void isCommentAlreadyExist(final View dialogView, String review_id, final String commenter_email) {

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
                            ((EditText) dialogView.findViewById(R.id.commentContent)).setText(mComment.getComment_content());
                            ((Button) dialogView.findViewById(R.id.ok)).setText("שמור שינויים");
                        }
                    }
                }
            }
        });
    }


    private void initSaveButton(final View dialogView, final String review_id) {
        dialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String comment_content = ((EditText) dialogView.findViewById(R.id.commentContent)).getText().toString().trim();

                Query requestQuery = db.collection("Users").whereEqualTo("email", mAuth.getCurrentUser().getEmail());
                requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = new User();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                user = document.toObject(User.class);
                            }

                            mComment = new Comment(review_id, user.getEmail(), comment_content, Timestamp.now());
                            mReview.addComment(mComment);
                            db.collection("Reviews").document(review_id).update("comments", mReview.getComments());
                            addNotificationComment(user, mReview.getReviewer_email(), mReview.getBook_title());
                            sendResult(303, review_id);

                        }
                    }
                });

                dismiss();
            }
        });




    }


    private void addNotificationComment(User user,String to_email,String book_title){
        if( (!(to_email.equals(mAuth.getCurrentUser().getEmail())))) {
            db = FirebaseFirestore.getInstance();

            Map<String, Object> notificationMessegae = new HashMap<>();

            notificationMessegae.put("type","הגיב על הביקורת שלך");
            notificationMessegae.put("from", user.getEmail());
            notificationMessegae.put("book_title", book_title);
            notificationMessegae.put("time", Timestamp.now());


            db.collection("Users/" + to_email + "/Notifications").add(notificationMessegae).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                }
            });
        }
    }

    private void initCancelButton(View dialogView) {
        dialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }


    private void sendResult(int REQUEST_CODE, String review_id) {
        Intent intent = new Intent();
        intent.putExtra("review_id", review_id);
        getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_CODE, intent);
    }
}
