package com.reading7.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.reading7.ExploreFragment;
import com.reading7.Objects.Book;
import com.reading7.R;
import com.reading7.Utils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class StoryPlaylistAdapter extends RecyclerView.Adapter<StoryPlaylistAdapter.ViewHolder> {

    private ArrayList<String> names;
    private List<Book> books;
    private ExploreAdapter expAdapter;
    private Context mContext;
    private Fragment mFragment;
    private String pressedGenre = "בשבילך";


    public StoryPlaylistAdapter(Context context, ArrayList<String> names, List<Book> books,
                                ExploreAdapter expAdapter, Fragment fragment) {

        this.names = names;
        this.mContext = context;
        this.books = books;
        this.expAdapter = expAdapter;
        this.mFragment = fragment;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView playlistName;
        ImageView cover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            playlistName = itemView.findViewById(R.id.playlistName);
            cover = itemView.findViewById(R.id.coverImage);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.story_playlist_item, viewGroup, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        final String genre = names.get(i);
        viewHolder.playlistName.setText(genre);
        viewHolder.cover.setImageDrawable(Utils.getDrawableForGenre(mContext, genre, true));
        viewHolder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                books.clear();
                ((ExploreFragment) mFragment).first_load_genre_books(genre);
                pressedGenre = genre;
                notifyDataSetChanged();
            }
        });

        updatePressedGenre(viewHolder, genre);
    }

    @Override
    public int getItemCount() {
        return names.size();
    }


    private void updatePressedGenre(ViewHolder viewHolder, String genre) {

        if (pressedGenre.equals(genre)) {
            viewHolder.cover.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorPrimary)));
            viewHolder.cover.setClickable(false);
        } else {
            viewHolder.cover.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.grey)));
            viewHolder.cover.setClickable(true);
        }
    }

}
