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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RankBookDialog extends AppCompatDialogFragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Review mReview;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        final String book_id = getArguments().getString("book_id");
        final String book_title = getArguments().getString("book_title");
        final float currAvg = getArguments().getFloat("avg");
        final int numOfRaters = getArguments().getInt("countRaters");


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.rank_book_dialog, null);
        final RatingBar rateStar = (RatingBar) view.findViewById(R.id.ratingBar);
        final EditText titleText = (EditText) view.findViewById(R.id.FeedbackTitle);
        final EditText contentText = (EditText) view.findViewById(R.id.FeedbackContent);
        mReview = null;
        CollectionReference requestCollectionRef = db.collection("Reviews");
        Query requestQuery = requestCollectionRef.whereEqualTo("reviewer_email",mAuth.getCurrentUser().getEmail()).whereEqualTo("book_id",book_id);
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                     @Override
                                                     public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                         if (task.isSuccessful()) {
                                                             for (QueryDocumentSnapshot document : task.getResult()) {
                                                                 mReview = document.toObject(Review.class);
                                                             }
                                                            if(mReview!= null){
                                                                rateStar.setRating(mReview.getRank());
                                                                titleText.setText(mReview.getReview_title());
                                                                contentText.setText(mReview.getReview_content());
                                                            }

                                                         }
                                                     }
                                                 });


        builder.setView(view).setPositiveButton("הוסף ביקורת", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final int rank = (int) rateStar.getRating();
                final String review_title = titleText.getText().toString();
                final String review_content = contentText.getText().toString();


                CollectionReference requestCollectionRef = db.collection("Users");
                Query requestQuery = requestCollectionRef.whereEqualTo("email",mAuth.getCurrentUser().getEmail());
                requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){
                            DocumentReference newReview = db.collection("Reviews").document();
                            if(mReview==null){
                            User user = new User();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                user = document.toObject(User.class);
                            }


                            mReview = new Review("", book_id, mAuth.getCurrentUser().getEmail(), rank, review_title, review_content, Timestamp.now(), user.getFull_name(), book_title);
                            mReview.setReview_id(newReview.getId());
                            final float newAvg =  ((numOfRaters*currAvg)+rank)/(numOfRaters+1);
                                newReview.set(mReview).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    UpdateBook(newAvg,book_id);
                                                                                }

                                                                            }


                                                                        }


                            );

                            }
                        else{

                            DocumentReference ref = db.collection("Reviews").document(mReview.getReview_id());
                            final Map<String, Object> updates = new HashMap<String,Object>();

                            updates.put("rank", rank);
                            updates.put("review_title",review_title);
                            updates.put("review_content",review_content);
                            final float newAvg;
                            if (numOfRaters == 0){
                                newAvg = 0;
                            }
                            else{
                                newAvg =  ((numOfRaters*currAvg)+rank-mReview.getRank())/(numOfRaters);
                            }

                                ref.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        UpdateBook(newAvg,book_id);
                                    }
                                }
                            });
                        }

                    }
        }});}}).setNegativeButton("בטל", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return builder.create();
    }

    public void UpdateBook(final float newAvg,String book_id){

        DocumentReference ref = db.collection("Books").document(book_id);
        final Map<String, Object> updates = new HashMap<String,Object>();


        ref.update("avg_rating",newAvg).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                }
            }
        });
    }


}
