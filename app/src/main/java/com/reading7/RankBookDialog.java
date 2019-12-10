package com.reading7;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class RankBookDialog extends AppCompatDialogFragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        final String book_id = getArguments().getString("book_id");
        final String book_title = getArguments().getString("book_name");


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.rank_book_dialog, null);
        final RatingBar rateStar = (RatingBar) view.findViewById(R.id.ratingBar);
        final EditText titleText = (EditText) view.findViewById(R.id.FeedbackTitle);
        final EditText contentText = (EditText) view.findViewById(R.id.FeedbackContent);


        builder.setView(view).setPositiveButton("הוסף ביקורת", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int rank = (int) rateStar.getRating();
                String review_title = titleText.getText().toString();
                String review_content = contentText.getText().toString();
                Review review = new Review("", book_id, mAuth.getCurrentUser().getEmail(), rank, review_title, review_content, Timestamp.now(), mAuth.getCurrentUser().getDisplayName(), book_title);
                DocumentReference newReview = db.collection("Reviews").document();
                review.setReview_id(newReview.getId());
                newReview.set(review).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {

                                                                    }
                                                                }
                                                            }

                );
            }
        }).setNegativeButton("בטל", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return builder.create();
    }

}
