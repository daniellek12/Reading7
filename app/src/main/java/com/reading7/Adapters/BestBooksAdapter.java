package com.reading7.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reading7.BookFragment;
import com.reading7.MainActivity;
import com.reading7.Objects.Avatar;
import com.reading7.Objects.Book;
import com.reading7.Objects.Comment;
import com.reading7.Objects.User;
import com.reading7.R;
import com.reading7.ReviewCommentsFragment;
import com.reading7.Utils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.reading7.Utils.RelativeDateDisplay;

public class BestBooksAdapter extends RecyclerView.Adapter<BestBooksAdapter.ViewHolder> {

        Context mContext;
        Fragment mFragment;
        List<Book> books;


    public BestBooksAdapter(Context context, Fragment fragment, List<Book> books) {
            this.mContext = context;
            this.mFragment = fragment;
            this.books = books;
        }

        @NonNull
        @Override
        public BestBooksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.best_book_item, parent, false);
            return new BestBooksAdapter.ViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return books.size();
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final int pos = position;

            holder.title.setText(books.get(position).getTitle());
            holder.author.setText(books.get(position).getAuthor());
            Utils.showImage(books.get(position).getTitle(), holder.cover, (Activity) mContext);

            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)mContext).addFragment(new BookFragment(books.get(pos)));
                    Utils.closeKeyboard(mContext);
                }
            });}




        public class ViewHolder extends RecyclerView.ViewHolder {

            RelativeLayout container;
            CircleImageView cover;
            TextView title;
            TextView author;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                //convertView = LayoutInflater.from(mContext).inflate(R.layout.search_item, parent, false);
                this.container = itemView.findViewById(R.id.container_book);
                this.title = itemView.findViewById(R.id.title_book);
                this.author = itemView.findViewById(R.id.author_book);
                this.cover = itemView.findViewById(R.id.cover_book);
            }
        }

}

