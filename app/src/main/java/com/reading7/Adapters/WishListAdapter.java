package com.reading7.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.R;
import com.reading7.Utils;
import com.reading7.Objects.WishList;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {

    private List<WishList> usersList;
    private Context mContext;
    private Activity currentActivity;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean flagEdit;


    public WishListAdapter(List<WishList> list, Activity activity, boolean flag){
        this.usersList = list;
        this.mContext = activity.getApplicationContext();
        this.currentActivity = activity;
        this.flagEdit= flag;

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        ImageView delete_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.coverImage);
            delete_button = itemView.findViewById(R.id.deleteBtn);
            if(flagEdit == false)
                delete_button.setVisibility(View.INVISIBLE);
            else
                delete_button.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.playlist_item, viewGroup,false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return new WishListAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final String book_name = usersList.get(i).getBook_title();
        Utils.showImage(book_name, viewHolder.cover, currentActivity);
        viewHolder.delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteWishList(book_name);

            }
        });
    }


    public void deleteWishList(String book_title) {

        CollectionReference requestsRef = db.collection("Wishlist");
        Query requestQuery = requestsRef.whereEqualTo("user_email", mAuth.getCurrentUser().getEmail()).whereEqualTo("book_title",book_title);
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    for(QueryDocumentSnapshot document: task.getResult()){
                        document.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }


                else Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

}
