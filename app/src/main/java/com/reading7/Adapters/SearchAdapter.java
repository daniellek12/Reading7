package com.reading7.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reading7.Book;
import com.reading7.R;
import com.reading7.Utils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private ArrayList<Book> books;
    private Context mContext;


    public SearchAdapter(Context context, ArrayList<Book> books){

        this.books = books;
        this.mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView cover;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cover = itemView.findViewById(R.id.coverImage);
            title = itemView.findViewById(R.id.title);
        }
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_item, viewGroup, false);
        return new SearchAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder viewHolder, int i) {

        Book book = books.get(i);
        Utils.showImage(book.getTitle(), viewHolder.cover, (Activity)mContext);
        viewHolder.title.setText(book.getTitle());
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

}
