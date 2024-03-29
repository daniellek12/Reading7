package com.reading7;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.ExploreAdapter;
import com.reading7.Adapters.StoryPlaylistAdapter;
import com.reading7.Objects.Book;
import com.reading7.Objects.Recommendation;

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

    private final List<Book> bookList = new ArrayList<>();
    private RecyclerView exploreRV;
    private ExploreAdapter myAdapter;
    private int limit = 11;
    private boolean loading = true;
    private String mGenre;
    private int first;
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.explore_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((BottomNavigationView) getActivity().findViewById(R.id.navigation)).setSelectedItemId(R.id.navigation_explore);

        mGenre = "בשבילך";
        first = 0;
        showProgressBar();
        initAppBar();

        if (Utils.isAdmin) {
            getActivity().findViewById(R.id.playlistsRV).setVisibility(View.GONE);
        } else {
            initPlaylists();
        }

        initExplore();
        first_load_books();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (resultCode) {
            case 101:
                String book_id = data.getStringExtra("book_id");
                loadAgain();
        }
    }

    public void loadAgain() {
        first = 0;
        showProgressBar();
        bookList.clear();
        myAdapter.notifyDataSetChanged();
        first_load_books();
    }

    private void initAppBar() {

        getActivity().findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).loadFragment(new SearchFragment());
                Utils.openKeyboard(getContext());
            }
        });

        if (Utils.isAdmin) {
            getActivity().findViewById(R.id.store).setVisibility(View.GONE);
            getActivity().findViewById(R.id.reports).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.reports).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) getActivity()).loadFragment(new ReportsFragment());
                }
            });
            getActivity().findViewById(R.id.statistics).setVisibility(View.GONE);
        } else {
            getActivity().findViewById(R.id.store).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) getActivity()).addFragment(new StoreFragment());
                }
            });

            getActivity().findViewById(R.id.statistics).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StatisticsDialog dialog = new StatisticsDialog();
                    dialog.show(getActivity().getSupportFragmentManager(), "statistics dialog");
                }
            });
        }
    }

    private ArrayList<String> getPlaylistsNames() {

        ArrayList<String> names = new ArrayList<String>();
        names.add("בשבילך");
        names.add("הרפתקאות");
        names.add("מדע בדיוני");
        names.add("היסטוריה");
        names.add("דרמה");
        names.add("אהבה");
        names.add("אימה");
        names.add("מדע");
        names.add("קומדיה");
        names.add("מתח");

        return names;
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
        StoryPlaylistAdapter adapter = new StoryPlaylistAdapter(getActivity(), getPlaylistsNames(), bookList, myAdapter, this);
        playlistsRV.setAdapter(adapter);
    }

    private void initExplore() {
        isLastItemReached = false;

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

        myAdapter = new ExploreAdapter(getContext(), getActivity(), bookList, this);
        myAdapter.setHasStableIds(true);

        exploreRV.setAdapter(myAdapter);

        exploreRV.setHasFixedSize(true);
        exploreRV.setItemViewCacheSize(22);
        exploreRV.setDrawingCacheEnabled(true);
        exploreRV.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        exploreRV.setNestedScrollingEnabled(false);
        showProgressBar();

        NestedScrollView nestedScrollView = getActivity().findViewById(R.id.nested_test);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
//                    Toast.makeText(getContext(), "scrolling", Toast.LENGTH_SHORT).show();
                    load_books();
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
        // this will load the first block of books for initialization of the explore
//        hideProgressBar();

    }


    private void first_load_books() {
        mGenre = "בשבילך";
        showProgressBar();

        //mGenre=genre;
        final List<Book> newlist = new ArrayList<>();
        final List<String> newlstBooksIds = new ArrayList<>();
        final List<String> lstBooksIds = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference requestCollectionRef = db.collection("Recommendations");
        Query requestQuery = requestCollectionRef.whereEqualTo("user_id", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Recommendation recommendation = document.toObject(Recommendation.class);
                        if (!lstBooksIds.contains(recommendation.getBook_id()))
                            newlstBooksIds.add(recommendation.getBook_id());

                    }
                }

                int count = 0;
                for (String b : newlstBooksIds) {
                    if (count < 10) {
                        lstBooksIds.add(b);
                        count++;
                    }
                }

                final CollectionReference bookRef = FirebaseFirestore.getInstance().collection("Books");
                lstBooksIds.add("dummy");
                int size = Math.min(lstBooksIds.size(), 9);
                Query booksQuery = bookRef.whereIn("id", lstBooksIds.subList(0, size));
                booksQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Book b = document.toObject(Book.class);
                                if (!newlist.contains(b))
                                    newlist.add(b);

                            }
                        }
                        Query booksQuery1 = bookRef.limit(limit - newlist.size());
                        booksQuery1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        Book b1 = document.toObject(Book.class);
                                        if (!newlist.contains(b1))
                                            newlist.add(b1);

                                    }
                                }

                                bookList.addAll(newlist);
                                newlist.clear();

                                myAdapter.notifyDataSetChanged();//no problem cause this is the first update
                                lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                hideProgressBar();
                            }
                        });
                    }
                });
            }
        });
    }

    public void first_load_genre_books(final String genre) {
        initExplore();

        //changed genre
        if (!mGenre.equals(genre)) {
            showProgressBar();
            first = 0;
            if (genre.equals("בשבילך")) {
                initExplore();
                first_load_books();
                return;
            }

        } else {
            hideProgressBar();
            return;
        }

        //pressed the same genre


        mGenre = genre;


        final List<Book> newlist = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference requestCollectionRef = db.collection("Books");
        Query requestQuery;

        if (first == 0) {
            bookList.clear();
            requestQuery = requestCollectionRef.whereArrayContains("actual_genres", mGenre).limit(limit);
            first = 1;
        } else
            requestQuery = requestCollectionRef.whereArrayContains("actual_genres", mGenre).startAfter(lastVisible).limit(limit);

        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Book book = document.toObject(Book.class);
                        newlist.add(book);
                    }
                    bookList.addAll(newlist);
                    lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                    myAdapter.notifyDataSetChanged(); //no problem cause this is the first update
                    hideProgressBar();
                }
            }
        });
    }

    private void load_books() {

        final List<Book> newlist = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference requestCollectionRef = db.collection("Books");
        Query requestQuery;
        requestQuery = requestCollectionRef.limit(limit);


        GridLayoutManager gridLayoutManager = ((GridLayoutManager) exploreRV.getLayoutManager());
        int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
        int visibleItemCount = gridLayoutManager.getChildCount();
        final int totalItemCount = gridLayoutManager.getItemCount();

        if (((firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached)) {
            showProgressBar();
            Query nextQuery;
            if (mGenre.equals("בשבילך"))
                nextQuery = requestCollectionRef.startAfter(lastVisible).limit(limit);
            else
                nextQuery = requestCollectionRef.whereArrayContains("actual_genres", mGenre).startAfter(lastVisible).limit(limit);
            nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> t) {
                    if (t.isSuccessful()) {
                        if (t.getResult().size() == 0) {
                            hideProgressBar();

                            return;
                        }
                        for (DocumentSnapshot d : t.getResult()) {
                            Book book = d.toObject(Book.class);
                            if (!bookList.contains(book))
                                newlist.add(book);

                        }
                        bookList.addAll(newlist);
                        for (int i = totalItemCount; i < totalItemCount + t.getResult().size(); i++) {
                            myAdapter.notifyItemInserted(i);//notify updated book ONLY
                        }

                        lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);
                        loading = true;

                        if (t.getResult().size() < limit) {
                            isLastItemReached = true;
                        }
                    } else {
                        Log.d("Explore", "Load books failed");
                    }
                    hideProgressBar();
                }
            });
        }


        // hideProgressBar();


    }


    public void showProgressBar() {
        Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), false);
//        getActivity().findViewById(R.id.explore_progress_background).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.explore_progress_bar).setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), true);
//        getActivity().findViewById(R.id.explore_progress_background).setVisibility(View.GONE);
        getActivity().findViewById(R.id.explore_progress_bar).setVisibility(View.GONE);
    }

    private void disableClicks() {
        ((MainActivity) getActivity()).setBottomNavigationEnabled(false);
        getActivity().findViewById(R.id.search).setEnabled(false);
        getActivity().findViewById(R.id.playlistsRV).setEnabled(false);
//        getActivity().findViewById(R.id.notifications).setEnabled(false);


    }

    private void enableClicks() {
        getActivity().findViewById(R.id.search).setEnabled(true);
        getActivity().findViewById(R.id.playlistsRV).setEnabled(true);
//        getActivity().findViewById(R.id.notifications).setEnabled(true);
        ((MainActivity) getActivity()).setBottomNavigationEnabled(true);
    }
}
