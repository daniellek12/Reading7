package com.reading7.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.BookFragment;
import com.reading7.MainActivity;
import com.reading7.Objects.Book;
import com.reading7.R;
import com.reading7.Objects.Review;
import com.reading7.Utils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReadShelfAdapter extends RecyclerView.Adapter<ReadShelfAdapter.ViewHolder> {

    private List<Review> usersReviews;
    private Context mContext;


    public ReadShelfAdapter(List<Review> reviews, Context context){
        this.usersReviews = reviews;
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
        return new ReadShelfAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final String book_name = usersReviews.get(i).getBook_title();
        Utils.showImage(book_name, viewHolder.cover, (Activity) mContext);
        viewHolder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query bookRef = FirebaseFirestore.getInstance().collection("Books").whereEqualTo("title",book_name);
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
    }

    @Override
    public int getItemCount() {
        return usersReviews.size();
    }

}
