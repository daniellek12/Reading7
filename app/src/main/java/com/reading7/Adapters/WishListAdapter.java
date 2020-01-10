package com.reading7.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.BookFragment;
import com.reading7.MainActivity;
import com.reading7.Objects.Book;
import com.reading7.R;
import com.reading7.Utils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ArrayList<String> bookNames;
    private Context mContext;


    public WishListAdapter(ArrayList<String> bookNames, Context context){
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.bookNames = bookNames;
        this.mContext = context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.coverImage);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.playlist_item, viewGroup,false);
        return new WishListAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        final String bookName = bookNames.get(i);
        Utils.showImage(bookName, viewHolder.cover, (Activity) mContext);

        viewHolder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query bookRef = FirebaseFirestore.getInstance().collection("Books").whereEqualTo("title",bookName);
                bookRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final DocumentSnapshot doc : task.getResult()) {
                                Book b = doc.toObject(Book.class);
                                ((MainActivity) mContext).loadFragment(new BookFragment(b));
                            }
                        }
                    }
                });
            }
        });
    }


    public void deleteWishList(String bookName) {

        CollectionReference requestsRef = db.collection("Wishlist");
        Query requestQuery = requestsRef
                .whereEqualTo("user_email", mAuth.getCurrentUser().getEmail())
                .whereEqualTo("book_title",bookName);
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        document.getReference().delete();
                    }
                }

                else Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return bookNames.size();
    }

}
