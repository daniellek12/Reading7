package com.reading7.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.reading7.ShelfFragment;
import com.reading7.Utils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileShelfAdapter extends RecyclerView.Adapter<ProfileShelfAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<String> bookNames;
    private ShelfFragment shelfFragment;
    private ViewGroup viewGroup;
    private Activity mActivity;

    private static final int DISPLAY_LIMIT = 5;


    public ProfileShelfAdapter(Context context, ArrayList<String> bookNames, ShelfFragment shelfFragment,
                               ViewGroup viewGroup,Activity activity) {
        this.mContext = context;
        this.bookNames = bookNames;
        this.shelfFragment = shelfFragment;
        this.viewGroup=viewGroup;
        this.mActivity= activity;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        RelativeLayout limitLayout;
        TextView numItemsLeft;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.coverImage);
            limitLayout = itemView.findViewById(R.id.limitLayout);
            numItemsLeft = itemView.findViewById(R.id.numItemsLeft);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.playlist_item, viewGroup, false);
        return new ProfileShelfAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        final String bookName = bookNames.get(i);
        Utils.showImage(bookName, viewHolder.cover, (Activity) mContext);

        if (i == DISPLAY_LIMIT - 1 && bookNames.size() > DISPLAY_LIMIT) {
            viewHolder.limitLayout.setVisibility(View.VISIBLE);
            viewHolder.numItemsLeft.setText("+" + (bookNames.size() - 5));
            viewHolder.limitLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)mContext).addFragment(shelfFragment);
                }
            });
        } else {
            viewHolder.limitLayout.setVisibility(View.GONE);
            viewHolder.cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.enableDisableClicks(mActivity,viewGroup,false);
                    Query bookRef = FirebaseFirestore.getInstance().collection("Books").whereEqualTo("title", bookName);
                    bookRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (final DocumentSnapshot doc : task.getResult()) {
                                    Book b = doc.toObject(Book.class);
                                    ((MainActivity) mContext).loadFragment(new BookFragment(b));
                                    Utils.enableDisableClicks(mActivity,viewGroup,true);

                                }
                            }
                        }
                    });
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if (bookNames.size() > DISPLAY_LIMIT)
            return DISPLAY_LIMIT;

        return bookNames.size();
    }

}
