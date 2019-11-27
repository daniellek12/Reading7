package com.reading7.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.reading7.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private ArrayList<String> names; //TODO: should be a list of playlists
    private ArrayList<Integer> covers;
    private Context mContext;


    public PlaylistAdapter(Context context, ArrayList<String> names, ArrayList<Integer> covers){

        this.names = names;
        this.covers = covers;
        this.mContext = context;
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

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.playlist_item, viewGroup, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.playlistName.setText(names.get(i));
        viewHolder.cover.setImageResource(mContext.getResources().getIdentifier("cover"+(i+1), "mipmap", mContext.getPackageName()));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

}
