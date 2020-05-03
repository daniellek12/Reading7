package com.reading7;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.FeedAdapter;
import com.reading7.Objects.Post;
import com.reading7.Objects.Review;
import com.reading7.Objects.User;
import com.reading7.Objects.WishList;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class HomeFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ArrayList<Post> posts = new ArrayList<Post>();
    private SwipeRefreshLayout swipeRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.home_fragment, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((BottomNavigationView) getActivity().findViewById(R.id.navigation)).setSelectedItemId(R.id.navigation_home);
        initRefreshLayout();
        initPosts();
    }


    private void createPosts(final RecyclerView postsRV) {

        Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), false);

        User user = ((MainActivity) getActivity()).getCurrentUser();
        ArrayList<String> following = user.getFollowing();
        CollectionReference collection_reviews = db.collection("Reviews");
        CollectionReference collection_wishlist = db.collection("Wishlist");

        if (!following.isEmpty()) {
            Query query = collection_reviews.whereIn("reviewer_email", following);
            final Query query_wishlist = collection_wishlist.whereIn("user_email", following);

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            posts.add(new Post(doc.toObject(Review.class)));
                        }
                    }
                    query_wishlist.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> tasky) {
                            if (tasky.isSuccessful()) {
                                for (QueryDocumentSnapshot doc : tasky.getResult()) {
                                    posts.add(new Post(doc.toObject(WishList.class)));
                                }
                            }

                            Collections.sort(posts, new Post.SortByDate());
                            FeedAdapter adapter = new FeedAdapter(getActivity(), HomeFragment.this, posts);
                            postsRV.setAdapter(adapter);
                            Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), true);
                            getView().findViewById(R.id.home_progress_bar).setVisibility(View.GONE);
                            swipeRefresh.setRefreshing(false);
                        }

                    });
                }
            });

        } else {

            FeedAdapter adapter = new FeedAdapter(getActivity(), HomeFragment.this, posts);
            postsRV.setAdapter(adapter);
            Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), true);
            getView().findViewById(R.id.home_progress_bar).setVisibility(View.GONE);
            swipeRefresh.setRefreshing(false);
        }
    }


    private void initPosts() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        RecyclerView postsRV = getActivity().findViewById(R.id.posts);
        postsRV.setLayoutManager(layoutManager);
        createPosts(postsRV);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case 303:
                String review_id = data.getStringExtra("review_id");
                RecyclerView postsRV = getView().findViewById(R.id.posts);
                ((FeedAdapter) postsRV.getAdapter()).notifyReviewCommentsChanged(review_id);
                break;

        }
    }


    private void initRefreshLayout() {

        swipeRefresh = getView().findViewById(R.id.swipeRefresh);

        swipeRefresh.setColorSchemeColors(getActivity().getResources().getColor(R.color.colorPrimaryDark),
                getActivity().getResources().getColor(R.color.colorPrimaryDark),
                getActivity().getResources().getColor(R.color.colorPrimaryDark));

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                posts.clear();
                initPosts();
            }
        });
    }

}


