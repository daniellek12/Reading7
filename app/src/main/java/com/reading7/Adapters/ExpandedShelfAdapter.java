package com.reading7.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.util.Assert;
import com.reading7.BookFragment;
import com.reading7.MainActivity;
import com.reading7.Objects.Book;
import com.reading7.R;
import com.reading7.ShelfFragment;
import com.reading7.Utils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExpandedShelfAdapter extends RecyclerView.Adapter<ExpandedShelfAdapter.ViewHolder> {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ShelfFragment.ShelfType type;
    private List<String> bookNames;
    private Context mContext;

    private boolean editMode;
    private List<String> toDelete;

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView cover;
        ImageView checked;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cover = itemView.findViewById(R.id.coverImage);
            checked = itemView.findViewById(R.id.checked);
        }
    }


    public ExpandedShelfAdapter(Context context, List<String> bookNames, ShelfFragment.ShelfType type) {

        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        this.type = type;
        this.bookNames = bookNames;
        this.mContext = context;
        this.editMode = false;
        this.toDelete = new ArrayList<String>();
    }

    @NonNull
    @Override
    public ExpandedShelfAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.expanded_shelf_item, viewGroup, false);
        return new ExpandedShelfAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ExpandedShelfAdapter.ViewHolder holder, final int position) {

        Utils.showImage(bookNames.get(position), holder.cover, (Activity) mContext);
        holder.cover.setAlpha((float) 1);
        holder.checked.setVisibility(View.GONE);

        if (!editMode) {
            holder.cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Query bookRef = FirebaseFirestore.getInstance().collection("Books").whereEqualTo("title", bookNames.get(position));
                    bookRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot doc : task.getResult()) {
                                    ((MainActivity) mContext).loadBookFragment(new BookFragment(), doc.toObject(Book.class));
                                    break;
                                }
                            }
                        }
                    });
                }
            });
        } else {

            holder.cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (toDelete.contains(bookNames.get(position))) {
                        toDelete.remove(bookNames.get(position));
                        holder.cover.setAlpha((float) 1);
                        holder.checked.setVisibility(View.GONE);
                    } else {
                        toDelete.add(bookNames.get(position));
                        holder.cover.setAlpha((float) 0.4);
                        holder.checked.setVisibility(View.VISIBLE);
                    }
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return bookNames.size();
    }


    public void setEditMode(boolean edit_mode) {
        this.editMode = edit_mode;
        if (!edit_mode)
            toDelete.clear();
        notifyDataSetChanged();
    }


    public void deleteItems() {
        if (!toDelete.isEmpty()) {

            switch (this.type) {

                case WISHLIST:
                    deleteWishlists();
                    break;

                case MYBOOKS:
                    deleteMyBooks();
                    break;
            }

            bookNames.removeAll(toDelete);
        }

        setEditMode(false);
    }


    public void deleteWishlists() {

        if (this.type != ShelfFragment.ShelfType.WISHLIST)
            throw new AssertionError("Wrong adapter type!");

        for (String book_name : toDelete) {
            CollectionReference requestsRef = db.collection("Wishlist");
            Query requestQuery = requestsRef.whereEqualTo("user_email", mAuth.getCurrentUser().getEmail())
                    .whereEqualTo("book_title", book_name);
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


    //TODO: deleting a book from my books also deletes my review on the book.
    //      should we provide other options?
    public void deleteMyBooks() {

        if (this.type != ShelfFragment.ShelfType.MYBOOKS)
            throw new AssertionError("Wrong adapter type!");

        for (String book_name : toDelete) {
            CollectionReference requestsRef = db.collection("Reviews");
            Query requestQuery = requestsRef.whereEqualTo("reviewer_email", mAuth.getCurrentUser().getEmail())
                                            .whereEqualTo("book_title", book_name);
            requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                    }

                    else Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
