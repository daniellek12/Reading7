package com.reading7.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Book;
import com.reading7.BookFragment;
import com.reading7.MainActivity;
import com.reading7.PublicProfileFragment;
import com.reading7.R;
import com.reading7.Utils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ViewHolder>   {

    private List<Book> books;
    private Context mContext;
    private Activity mActivity;


    public ExploreAdapter(Context context, Activity activity, List<Book> books){

        //get me some books
        this.books= books;
        this.mContext = context;
        this.mActivity= activity;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        RatingBar ratingBar;
        ImageView cover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ratingBar = itemView.findViewById(R.id.ratingBar);
            cover = itemView.findViewById(R.id.coverImage);
        }
    }


    @NonNull
    @Override
    public ExploreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.explore_item, viewGroup, false);
        return new ExploreAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        //set height of rows with 2 items
        if( i%11 == 0 || i%11 == 1 )
            viewHolder.cover.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpToPx(250)));

        //set ratingbar color
        Drawable rating = viewHolder.ratingBar.getProgressDrawable();
        rating.setColorFilter(Color.parseColor("#FFC21C"), PorterDuff.Mode.SRC_ATOP);

        viewHolder.ratingBar.setRating(books.get(i).getAvg_rating());
        Utils.showImage(books.get(i).getTitle(),viewHolder.cover,mActivity);
        //viewHolder.cover.setImageResource(mContext.getResources().getIdentifier("cover"+(i+1), "mipmap", mContext.getPackageName()));


        final int j = i;
        viewHolder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) mContext).loadBookFragment(new BookFragment(), books.get(j));
            }
        });
    }


    @Override
    public int getItemCount() {
        return books.size();
    }


    public int DpToPx(int dp){
        Resources r  = mContext.getResources();
        int px    = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;

    }

}
