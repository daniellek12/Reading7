package com.reading7.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Objects.Review;
import com.reading7.Objects.User;
import com.reading7.R;
import com.reading7.Utils;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class RankBookDialog extends AppCompatDialogFragment {

    RatingBar avg;
    int userAge;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Review mReview;
    private View view;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        final String book_id = getArguments().getString("book_id");
        final String book_title = getArguments().getString("book_title");
        final String book_author = getArguments().getString("book_author");
        final float currAvg = getArguments().getFloat("avg");
        final int numOfRaters = getArguments().getInt("countRaters");
        final float currAge = getArguments().getFloat("avgAge");

        RatingBar avg = getActivity().findViewById(R.id.ratingBar);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        view = getActivity().getLayoutInflater().inflate(R.layout.rank_book_dialog, null);
        final RatingBar rateStar = view.findViewById(R.id.ratingBar);
        final EditText titleText = view.findViewById(R.id.FeedbackTitle);
        final EditText contentText = view.findViewById(R.id.FeedbackContent);

        mReview = null;
        CollectionReference requestCollectionRef = db.collection("Reviews");
        Query requestQuery = requestCollectionRef.whereEqualTo("reviewer_email", mAuth.getCurrentUser().getEmail()).whereEqualTo("book_title", book_title);
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mReview = document.toObject(Review.class);
                    }
                    if (mReview != null) {
                        rateStar.setRating(mReview.getRank());
                        titleText.setText(mReview.getReview_title());
                        contentText.setText(mReview.getReview_content());
                        ((Button) view.findViewById(R.id.ok)).setText("שמור שינויים");
                    }

                }
            }
        });


        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int rank = (int) rateStar.getRating();
                final String review_title = titleText.getText().toString().trim();
                final String review_content = contentText.getText().toString().trim();

                CollectionReference requestCollectionRef = db.collection("Users");
                Query requestQuery = requestCollectionRef.whereEqualTo("email", mAuth.getCurrentUser().getEmail());
                requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            DocumentReference newReview = db.collection("Reviews").document();
                            User user = new User();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                user = document.toObject(User.class);
                            }
                            userAge = Utils.calculateAge(user.getBirth_date());
                            if (mReview == null) {

                                mReview = new Review("", book_id, mAuth.getCurrentUser().getEmail(), Utils.calculateAge(user.getBirth_date()), rank, review_title, review_content, Timestamp.now(), book_title, book_author);
                                mReview.setReview_id(newReview.getId());
                                final float newAvg = ((numOfRaters * currAvg) + rank) / (numOfRaters + 1);
                                final float newAge = ((numOfRaters * currAge) + userAge) / (numOfRaters + 1);
                                newReview.set(mReview).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                 @Override
                                                                                 public void onComplete(@NonNull Task<Void> task) {
                                                                                     if (task.isSuccessful()) {
                                                                                         UpdateBook(newAge, newAvg, book_id, numOfRaters + 1);
                                                                                         DeleteFromWishList(book_title);
                                                                                         dismiss();
                                                                                     }
                                                                                 }
                                                                             }
                                );

                            } else {

                                DocumentReference ref = db.collection("Reviews").document(mReview.getReview_id());
                                final Map<String, Object> updates = new HashMap<String, Object>();

                                updates.put("rank", rank);
                                updates.put("review_title", review_title);
                                updates.put("review_content", review_content);
                                updates.put("review_time", Timestamp.now());

                                final float newAvg;
                                final float newAge;

                                if (numOfRaters == 0) {
                                    newAvg = rank;
                                    newAge = userAge;

                                } else {
                                    newAge = ((numOfRaters * currAge) + userAge - mReview.getReviewer_age()) / (numOfRaters);
                                    newAvg = ((numOfRaters * currAvg) + rank - mReview.getRank()) / (numOfRaters);
                                }

                                ref.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            UpdateBook(newAge, newAvg, book_id, numOfRaters);
                                            DeleteFromWishList(book_title);
                                            dismiss();
                                        }
                                    }
                                });
                            }

                        }
                    }
                });
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


    public void UpdateBook(final float newAge, final float newAvg, String book_id, int numOfRaters) {

        DocumentReference ref = db.collection("Books").document(book_id);
        final Map<String, Object> updates = new HashMap<String, Object>();

        updates.put("avg_rating", newAvg);
        updates.put("avg_age", newAge);
        updates.put("raters_count", numOfRaters);

        ref.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    sendResult(202, newAvg, newAge);
                }
            }
        });
    }


    private void sendResult(int REQUEST_CODE, float newAvg, float newAge) {

        Intent intent = new Intent();
        intent.putExtra("avg", newAvg);
        intent.putExtra("avgAge", newAge);

        getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_CODE, intent);
    }


  /*  @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (RankBookDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }


    public interface RankBookDialogListener {
        void applyAvg(float newAvg);
    }*/

    public void DeleteFromWishList(String book_title) {
        CollectionReference requestsRef = db.collection("Wishlist");
        Query requestQuery = requestsRef.whereEqualTo("user_email", mAuth.getCurrentUser().getEmail())
                .whereEqualTo("book_title", book_title);
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().delete();
                    }
                }
            }
        });

    }

}
