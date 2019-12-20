package com.reading7;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.ReadShelfAdapter;
import com.reading7.Adapters.WishListAdapter;
import com.reading7.Objects.Review;
import com.reading7.Objects.WishList;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.reading7.Utils.calculateAge;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    final private List<Review> usersReviews = new ArrayList<Review>();
    final private List<WishList> usersWishList = new ArrayList<WishList>();
    private ReadShelfAdapter adapterReviews;
    private WishListAdapter adapterWishList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((BottomNavigationView)getActivity().findViewById(R.id.navigation)).setSelectedItemId(R.id.navigation_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        getUserInformation();

        ImageButton dc = (ImageButton)getActivity().findViewById(R.id.settings);
        dc.bringToFront();
        dc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Utils.updateBooks();
                Toast.makeText(getActivity(), "IF YOU TOUCH THIS BUTTON EVER AGAIN ROTEM WILL KILL YOU.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserInformation() {

        FirebaseUser mUser = mAuth.getCurrentUser();
        DocumentReference userRef = db.collection("Users").document(mUser.getEmail());

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        TextView userName = getActivity().findViewById(R.id.userName);
                        userName.setText(document.getData().get("full_name").toString());

                        TextView userAge = getActivity().findViewById(R.id.age);
                        userAge.setText("גיל: " + calculateAge(document.getData().get("birth_date").toString()));

                        CircleImageView profileImage = getActivity().findViewById(R.id.profileImage);
                        ArrayList<Integer> avatar_details = (ArrayList<Integer>) document.getData().get("avatar_details");
                        Utils.loadAvatar(getContext(), profileImage, avatar_details);

                        TextView followers = getActivity().findViewById(R.id.followers);
                        ArrayList<String> arr = (ArrayList<String>) document.getData().get("followers");
                        followers.setText(Integer.toString(arr.size()));

                        TextView following = getActivity().findViewById(R.id.following);
                        arr = (ArrayList<String>) document.getData().get("following");
                        following.setText(Integer.toString(arr.size()));

                        initLogOutBtn();
                        initWishlist();
                        initMyBookslist();

                    } else Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                } else Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initWishlist() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        final RecyclerView wishlistRV = getActivity().findViewById(R.id.wishlistRV);
        wishlistRV.setLayoutManager(layoutManager);
        adapterWishList = new WishListAdapter(usersWishList, getActivity());
        wishlistRV.setAdapter(adapterWishList);

        getUserWishList();

        getActivity().findViewById(R.id.wishlistTitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> book_names = new ArrayList<String>();
                for(WishList wishList: usersWishList){
                    book_names.add(wishList.getBook_title());
                }

                ((MainActivity)getActivity()).loadShelfFragment(new ShelfFragment(book_names,getString(R.string.my_wishlist),mAuth.getCurrentUser().getEmail(), ShelfFragment.ShelfType.WISHLIST));
            }
        });
    }

    private void initMyBookslist() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView myBooksRV = getActivity().findViewById(R.id.myBooksRV);
        myBooksRV.setLayoutManager(layoutManager);
        adapterReviews = new ReadShelfAdapter(usersReviews, getActivity());
        myBooksRV.setAdapter(adapterReviews);

        getUserReviews();

        getActivity().findViewById(R.id.mybooksTitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> book_names = new ArrayList<String>();
                for(Review review: usersReviews){
                    book_names.add(review.getBook_title());
                }

                ((MainActivity)getActivity()).loadShelfFragment(new ShelfFragment(book_names,getString(R.string.my_books),mAuth.getCurrentUser().getEmail(), ShelfFragment.ShelfType.MYBOOKS));
            }
        });

    }

    private void initLogOutBtn() {

        final TextView logout = getActivity().findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                disableClicks();
                logout.setVisibility(View.GONE);
                getActivity().findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
                signOut();
            }
        });
    }

    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void disableClicks() {
        //getActivity().findViewById(R.id.settings).setEnabled(false);
        getActivity().findViewById(R.id.logout).setEnabled(false);
    }

    private void enableClicks() {
        //getActivity().findViewById(R.id.settings).setEnabled(true);
        getActivity().findViewById(R.id.logout).setEnabled(true);
    }

    private void getUserReviews() {

        CollectionReference collection = db.collection("Reviews");
        Query query = collection.whereEqualTo("reviewer_email", mAuth.getCurrentUser().getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        usersReviews.add(doc.toObject(Review.class));
                    }
                    adapterReviews.notifyDataSetChanged();

                    TextView reviews_num = getActivity().findViewById(R.id.recommendations);
                    reviews_num.setText(Integer.toString(usersReviews.size()));

                    if(usersReviews.isEmpty()){
                        getActivity().findViewById(R.id.myBooksRV).setVisibility(View.INVISIBLE);
                        getActivity().findViewById(R.id.emptyMyBooks).setVisibility(View.VISIBLE);
                    } else {
                        getActivity().findViewById(R.id.myBooksRV).setVisibility(View.VISIBLE);
                        getActivity().findViewById(R.id.emptyMyBooks).setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    private void getUserWishList() {

        CollectionReference collection = db.collection("Wishlist");
        Query query = collection.whereEqualTo("user_email", mAuth.getCurrentUser().getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        usersWishList.add(doc.toObject(WishList.class));
                    }
                    adapterWishList.notifyDataSetChanged();

                    if(usersWishList.isEmpty()){
                        getActivity().findViewById(R.id.wishlistRV).setVisibility(View.INVISIBLE);
                        getActivity().findViewById(R.id.emptyWishlist).setVisibility(View.VISIBLE);
                    } else {
                        getActivity().findViewById(R.id.wishlistRV).setVisibility(View.VISIBLE);
                        getActivity().findViewById(R.id.emptyWishlist).setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

}