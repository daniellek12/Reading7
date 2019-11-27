package com.reading7;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reading7.Adapters.ExploreAdapter;
import com.reading7.Adapters.PlaylistAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ExploreFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.explore_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initPlaylists();
        initExplore();
        initAppBar();
    }




    private void initAppBar(){

        getActivity().findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).loadFragment(new SearchFragment());
            }
        });

    }

    private ArrayList<String> getPlaylistsNames() {

        ArrayList<String> names =new ArrayList<String>();
        names.add("דרמה");
        names.add("קומיקס");
        names.add("אימה");
        names.add("היסטוריה");
        names.add("מתח");
        names.add("מדע בדיוני");
        names.add("הרפתקאות");

        return names;
    }

    private ArrayList<Float> getRatings() {

        ArrayList<Float> ratings =new ArrayList<Float>();
        ratings.add((float)3.5);
        ratings.add((float)4);
        ratings.add((float)1.12);
        ratings.add((float)5);
        ratings.add((float)4.5);
        ratings.add((float)3);
        ratings.add((float)3.5);
        ratings.add((float)4);
        ratings.add((float)2);
        ratings.add((float)5);
        ratings.add((float)4);
        ratings.add((float)2);
        ratings.add((float)3.5);
        ratings.add((float)4);
        ratings.add((float)2.5);
        ratings.add((float)5);
        ratings.add((float)3.5);
        ratings.add((float)2);
        ratings.add((float)3.5);
        ratings.add((float)4);
        ratings.add((float)3);
        ratings.add((float)5);
        ratings.add((float)4);
        ratings.add((float)5);

        return ratings;
    }

    private ArrayList<Integer> getCovers() {

        ArrayList<Integer> covers =new ArrayList<Integer>();
        covers.add(1);
        covers.add(2);
        covers.add(3);
        covers.add(4);
        covers.add(5);
        covers.add(6);
        covers.add(7);
        covers.add(8);
        covers.add(9);
        covers.add(10);
        covers.add(11);
        covers.add(12);
        covers.add(13);
        covers.add(14);
        covers.add(15);
        covers.add(16);
        covers.add(17);
        covers.add(18);
        covers.add(19);
        covers.add(20);
        covers.add(21);
        covers.add(22);
        covers.add(23);
        covers.add(24);

        return covers;
    }


    private void initPlaylists(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        RecyclerView playlistsRV = getActivity().findViewById(R.id.playlistsRV);
        playlistsRV.setLayoutManager(layoutManager);
        PlaylistAdapter adapter = new PlaylistAdapter(getActivity(),getPlaylistsNames(),getCovers());
        playlistsRV.setAdapter(adapter);
    }

    private void initExplore(){

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),6);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                            @Override
                                            public int getSpanSize(int position) {
                                                // 5 is the sum of items in one repeated section
                                                switch (position % 11) {
                                                    // first two items span 3 columns each
                                                    case 0:
                                                    case 1:
                                                        return 3;
                                                    // next 3 items span 2 columns each
                                                    case 2:
                                                    case 3:
                                                    case 4:
                                                    case 5:
                                                    case 6:
                                                    case 7:
                                                    case 8:
                                                    case 9:
                                                    case 10:
                                                        return 2;
                                                }
                                                throw new IllegalStateException("internal error");
                                            }
                                        });

        RecyclerView exploreRV = getActivity().findViewById(R.id.exploreRV);
        exploreRV.setLayoutManager(layoutManager);
        ExploreAdapter adapter = new ExploreAdapter(getActivity(),getRatings(), getCovers());
        exploreRV.setAdapter(adapter);
    }
}
