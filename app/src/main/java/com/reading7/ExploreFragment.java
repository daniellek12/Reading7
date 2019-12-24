package com.reading7;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.ExploreAdapter;
import com.reading7.Adapters.StoryPlaylistAdapter;
import com.reading7.Objects.Book;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ExploreFragment extends Fragment {

    private RecyclerView exploreRV;
    private ExploreAdapter myAdapter;
    private int limit = 11;
    private boolean loading = true;


    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;

    private final List<Book> bookList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.explore_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((BottomNavigationView) getActivity().findViewById(R.id.navigation)).setSelectedItemId(R.id.navigation_explore);

        getActivity().findViewById(R.id.notifications).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Book book = new Book("id", "title", new ArrayList<String>(), "author", "publisher", 2, "summary", 3, 3);
                ((MainActivity) getActivity()).addFragment(new BookFragment(book));
            }
        });
        initPlaylists();
        initExplore();
        initAppBar();
    }


    private void initAppBar() {

        getActivity().findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).loadFragment(new SearchFragment());
                Utils.openKeyboard(getContext());
            }
        });

    }

    private ArrayList<String> getPlaylistsNames() {

        ArrayList<String> names = new ArrayList<String>();
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

        ArrayList<Float> ratings = new ArrayList<Float>();
        ratings.add((float) 3.5);
        ratings.add((float) 4);
        ratings.add((float) 1.12);
        ratings.add((float) 5);
        ratings.add((float) 4.5);
        ratings.add((float) 3);
        ratings.add((float) 3.5);
        ratings.add((float) 4);
        ratings.add((float) 2);
        ratings.add((float) 5);
        ratings.add((float) 4);
        ratings.add((float) 2);
        ratings.add((float) 3.5);
        ratings.add((float) 4);
        ratings.add((float) 2.5);
        ratings.add((float) 5);
        ratings.add((float) 3.5);
        ratings.add((float) 2);
        ratings.add((float) 3.5);
        ratings.add((float) 4);
        ratings.add((float) 3);
        ratings.add((float) 5);
        ratings.add((float) 4);
        ratings.add((float) 5);

        return ratings;
    }

    private ArrayList<Integer> getCovers() {

        ArrayList<Integer> covers = new ArrayList<Integer>();
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


    private void initPlaylists() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView playlistsRV = getActivity().findViewById(R.id.playlistsRV);
        playlistsRV.setLayoutManager(layoutManager);
        StoryPlaylistAdapter adapter = new StoryPlaylistAdapter(getActivity(), getPlaylistsNames(), getCovers());
        playlistsRV.setAdapter(adapter);
    }

    private void initExplore() {

        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 6);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // 11 is the sum of items in one repeated section
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

        exploreRV = getActivity().findViewById(R.id.exploreRV);
        exploreRV.setLayoutManager(layoutManager);

        myAdapter = new ExploreAdapter(getContext(), getActivity(), bookList);
        myAdapter.setHasStableIds(true);

        exploreRV.setAdapter(myAdapter);

        exploreRV.setHasFixedSize(true);
        exploreRV.setItemViewCacheSize(22);
        exploreRV.setDrawingCacheEnabled(true);
        exploreRV.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        exploreRV.setNestedScrollingEnabled(false);

        NestedScrollView nestedScrollView = getActivity().findViewById(R.id.nested_test);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                Toast.makeText(getContext(), "scrolling", Toast.LENGTH_SHORT).show();
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    if (loading) {
                        load_books();
                    }
                    loading = false;
                }
            }
        });

//        Button load_more = getActivity().findViewById(R.id.load_more_btn);
//        load_more.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                load_books();
//            }
//        });

        final List<Book> newlist = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference requestCollectionRef = db.collection("Books");
        Query requestQuery = requestCollectionRef.limit(limit);
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Book book = document.toObject(Book.class);
                        newlist.add(book);
                    }
                    bookList.addAll(newlist);
                    myAdapter.notifyDataSetChanged();
                    lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                }
            }
        });


//        exploreRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });


//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (DocumentSnapshot document : task.getResult()) {
//                        Book productModel = document.toObject(Book.class);
//                        bookList.add(productModel);
//                    }
//                    myAdapter.notifyDataSetChanged();
//                }
//            }
//        });

    }

    private void load_books() {
        final List<Book> newlist = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference requestCollectionRef = db.collection("Books");
        Query requestQuery = requestCollectionRef.limit(limit);

        GridLayoutManager linearLayoutManager = ((GridLayoutManager) exploreRV.getLayoutManager());
        int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        int visibleItemCount = linearLayoutManager.getChildCount();
        final int totalItemCount = linearLayoutManager.getItemCount();

        if ((firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
            Query nextQuery = requestCollectionRef.startAfter(lastVisible).limit(limit);
            nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> t) {
                    if (t.isSuccessful()) {
                        for (DocumentSnapshot d : t.getResult()) {
                            Book book = d.toObject(Book.class);
                            bookList.add(book);
                        }
//                        bookList.addAll(newlist);
//                        myAdapter.notifyDataSetChanged();
                        for (int i = totalItemCount ; i < totalItemCount + t.getResult().size() ; i ++){
                            myAdapter.notifyItemInserted(i);
                        }
                        //myAdapter.getItemCount();
                        lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);
                        Toast.makeText(getContext(), "notify", Toast.LENGTH_SHORT).show();
                        loading = true;

                        if (t.getResult().size() < limit) {
                            isLastItemReached = true;
                        }
                    }
                }
            });
        }
    }


    public void getBooks() {
        final List<Book> newlist = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference requestCollectionRef = db.collection("Books");

        Query requestQuery = requestCollectionRef.limit(limit);

        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Book book = document.toObject(Book.class);
                        newlist.add(book);
                    }
                    bookList.addAll(newlist);
                    myAdapter.notifyDataSetChanged();
                    lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);

                    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                isScrolling = true;
                            }
                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                            int visibleItemCount = linearLayoutManager.getChildCount();
                            int totalItemCount = linearLayoutManager.getItemCount();

                            if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                                isScrolling = false;
                                Query nextQuery = requestCollectionRef.startAfter(lastVisible).limit(limit);
                                nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                        if (t.isSuccessful()) {
                                            for (DocumentSnapshot d : t.getResult()) {
                                                Book book = d.toObject(Book.class);
                                                newlist.add(book);
                                            }
                                            bookList.addAll(newlist);
                                            myAdapter.notifyDataSetChanged();
                                            lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);

                                            if (t.getResult().size() < limit) {
                                                isLastItemReached = true;
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    };
                }
            }
        });
    }
}
