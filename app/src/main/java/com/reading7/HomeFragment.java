package com.reading7;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.FeedAdapter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<Post> posts = new ArrayList<Post>();

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
        ((BottomNavigationView)getActivity().findViewById(R.id.navigation)).setSelectedItemId(R.id.navigation_home);

        initPosts();
    }


    // TODO: create posts according to real information in page.
    //  -   order of posts - SET TO BE ORDERED BY TIMESTAMP
    //  -   take posts from different collections
    private void createPosts(final RecyclerView postsRV) {
        // first: get my followers, and filter reviews and wishlists according to the ids we get
        FirebaseUser mUser = mAuth.getCurrentUser();
        DocumentReference userRef = db.collection("Users").document(mUser.getEmail());
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<String> following = (ArrayList<String>) document.getData().get("following");
                        CollectionReference collection = db.collection("Reviews");
                        CollectionReference collection_wishlist = db.collection("Wishlist");

                        // FIXED BUG if the users has no following
                        if (following.size() > 0) {
                            Query query = collection.whereIn("reviewer_email", following);
                            final Query query_wishlist = collection_wishlist.whereIn("user_email", following);

                            // now we have the reviews we wish to display in feed
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
                                            FeedAdapter adapter = new FeedAdapter(getActivity(), posts);
                                            postsRV.setAdapter(adapter);
                                        }
                                    });
                                }
                            });
                            // create posts according to reviews)
                        } else {
                            FeedAdapter adapter = new FeedAdapter(getActivity(), posts);
                            postsRV.setAdapter(adapter);
                        }

                    }
                }
            }
        });
    }


    private void initPosts() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        RecyclerView postsRV = getActivity().findViewById(R.id.posts);
        postsRV.setLayoutManager(layoutManager);
        createPosts(postsRV);
    }
}


