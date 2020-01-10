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
import com.reading7.Utils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReadShelfAdapter extends RecyclerView.Adapter<ReadShelfAdapter.ViewHolder> {

    private ArrayList<String> bookNames;
    private Context mContext;


    public ReadShelfAdapter(ArrayList<String> bookNames, Context context){
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
        return new ReadShelfAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

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
                                ((MainActivity) mContext).loadFragment(new BookFragment(doc.toObject(Book.class)));
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookNames.size();
    }

}
