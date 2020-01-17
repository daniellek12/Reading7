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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.BookFragment;
import com.reading7.MainActivity;
import com.reading7.Objects.Book;
import com.reading7.Objects.Review;
import com.reading7.R;
import com.reading7.ShelfFragment;
import com.reading7.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExpandedShelfAdapter extends RecyclerView.Adapter<ExpandedShelfAdapter.ViewHolder> {

    private Context mContext;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ShelfFragment.ShelfType type;
    private List<String> bookNames;

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
                                    ((MainActivity) mContext).addFragment(new BookFragment(doc.toObject(Book.class)));
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

            switch (this.type) { // TODO: add a case for custom shelf (and implement a function)

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

    /*
     * TODO: deleting a book from my books also deletes my review on the book.
     *       should we provide other options?
     */
    public void deleteMyBooks() {

        if (this.type != ShelfFragment.ShelfType.MYBOOKS)
            throw new AssertionError("Wrong adapter type!");

        for (final String book_name : toDelete) {
            CollectionReference requestsRef = db.collection("Reviews");
            Query requestQuery = requestsRef.whereEqualTo("reviewer_email", mAuth.getCurrentUser().getEmail())
                    .whereEqualTo("book_title", book_name);
            requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            final Review review = document.toObject(Review.class);
                            document.getReference().delete();
                            final DocumentReference bookRef = FirebaseFirestore.getInstance().collection("Books").document(review.getBook_id());

                            bookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        final DocumentSnapshot doc = task.getResult();
                                        if (doc.exists()) {
                                            Book book = (doc.toObject(Book.class));
                                            double newAvg,newAvgAge;
                                            if(book.getRaters_count()  ==1)
                                            {newAvg =0; newAvgAge=0;}
                                            else {
                                                newAvg = ((book.getAvg_rating() * book.getRaters_count()) - review.getRank()) / (book.getRaters_count() - 1);
                                                newAvgAge = ((book.getAvg_age() * book.getRaters_count()) - review.getReviewer_age()) / (book.getRaters_count() - 1);
                                            }
                                            final Map<String, Object> updates = new HashMap<String, Object>();

                                            updates.put("avg_rating", newAvg);
                                            updates.put("avg_age", newAvgAge);
                                            updates.put("raters_count", book.getRaters_count()-1);
                                            bookRef.update(updates);
                                        }
                                    }
                                }
                            });

                        }

                    }
                }
            });
        }
    }




}
