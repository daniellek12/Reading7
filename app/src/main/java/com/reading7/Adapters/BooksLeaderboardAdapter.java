package com.reading7.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.reading7.BookFragment;
import com.reading7.MainActivity;
import com.reading7.Objects.Book;
import com.reading7.R;
import com.reading7.StatisticsDialog;
import com.reading7.Utils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class BooksLeaderboardAdapter extends RecyclerView.Adapter<BooksLeaderboardAdapter.ViewHolder> {

    public enum BooksLeaderboardType {
        MAX_READS,
        MAX_RATED
    }

    private Context mContext;
    private StatisticsDialog mDialog;

    private List<Book> books;
    private BooksLeaderboardType type;

    public BooksLeaderboardAdapter(Context context, StatisticsDialog dialog, List<Book> books, BooksLeaderboardType type) {
        this.mContext = context;
        this.mDialog = dialog;
        this.books = books;
        this.type = type;
    }

    @NonNull
    @Override
    public BooksLeaderboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_book_item, parent, false);
        return new BooksLeaderboardAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        Book book = books.get(position);

        Utils.showImage(book.getTitle(), holder.cover, (Activity) mContext);
        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());

        holder.place.setText(String.valueOf(position + 1));
        if(type.equals(BooksLeaderboardType.MAX_RATED)) {
            holder.rating.setVisibility(View.VISIBLE);
            holder.rating.setText(String.valueOf(book.getAvg_rating()));
        } else if(type.equals(BooksLeaderboardType.MAX_READS)) {
            holder.readers.setVisibility(View.VISIBLE);
            holder.readers.setText(mContext.getString(R.string.x_readers, book.getRaters_count()));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) mContext).addFragment(new BookFragment(books.get(position)));
                mDialog.dismiss();
            }
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView cover;
        TextView title;
        TextView author;
        TextView place;
        TextView readers;
        TextView rating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.title_book);
            this.author = itemView.findViewById(R.id.author_book);
            this.cover = itemView.findViewById(R.id.cover_book);
            this.place = itemView.findViewById(R.id.place);
            this.readers = itemView.findViewById(R.id.readers_num);
            this.rating = itemView.findViewById(R.id.rating);

        }
    }

}

