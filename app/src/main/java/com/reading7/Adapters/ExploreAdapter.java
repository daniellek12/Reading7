package com.reading7.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.Fade;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.reading7.AdminActivity;
import com.reading7.Dialogs.DeleteBookDialog;
import com.reading7.ExploreFragment;
import com.reading7.Objects.Book;
import com.reading7.BookFragment;
import com.reading7.MainActivity;
import com.reading7.R;
import com.reading7.ReviewCommentsFragment;
import com.reading7.Utils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ViewHolder> {

    private List<Book> books;
    private Context mContext;
    private Activity mActivity;
    private Fragment fragment;

    public ExploreAdapter(Context context, Activity activity, List<Book> books, Fragment fragment) {

        //get me some books
        this.books = books;
        this.mContext = context;
        this.mActivity = activity;
        this.fragment = fragment;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        RatingBar ratingBar;
        ImageView cover;
        ImageView delete_background;
        ImageView delete_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ratingBar = itemView.findViewById(R.id.ratingBar);
            cover = itemView.findViewById(R.id.coverImage);
            delete_background = itemView.findViewById(R.id.delete_background);
            delete_button = itemView.findViewById(R.id.delete_button);
        }
    }


    @NonNull
    @Override
    public ExploreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.explore_item, viewGroup, false);
        return new ExploreAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        //set height of rows with 2 items
        if (i % 11 == 0 || i % 11 == 1)
            viewHolder.cover.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpToPx(250)));

        //set ratingbar color
        Drawable rating = viewHolder.ratingBar.getProgressDrawable();
        rating.setColorFilter(Color.parseColor("#FFC21C"), PorterDuff.Mode.SRC_ATOP);

        viewHolder.ratingBar.setRating(books.get(i).getAvg_rating());
        Utils.showImage(books.get(i).getTitle(), viewHolder.cover, mActivity);

        viewHolder.cover.setTransitionName(books.get(i).getTitle());
        viewHolder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookFragment bookFragment = new BookFragment(books.get(i));
                bookFragment.setTargetFragment(fragment, 101);
                ((MainActivity) mContext).addFragment(bookFragment);
                //((MainActivity) mContext).addFragment(new BookFragment(books.get(i)));
            }
        });

        if (Utils.isAdmin){
            viewHolder.delete_background.setVisibility(View.VISIBLE);
            viewHolder.delete_button.setVisibility(View.VISIBLE);
            viewHolder.delete_background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Book book = books.get(i);
                    DeleteBookDialog dialog = new DeleteBookDialog();

                    Bundle args = new Bundle();
                    args.putString("book_id", book.getId());
                    args.putString("book_title", book.getTitle());
                    args.putString("context", "explore fragment");

                    dialog.setArguments(args);
                    dialog.setTargetFragment(fragment, 404);
                    dialog.show(((MainActivity)mContext).getSupportFragmentManager(), "example dialog");
//                    Utils.deleteBookFromDB(book.getId(), book.getTitle());
//                    notifyDataSetChanged();
//                    ((ExploreFragment) fragment).loadAgain();
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return books.size();
    }


    public int DpToPx(int dp) {
        Resources r = mContext.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;

    }

}
