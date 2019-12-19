package com.reading7;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.reading7.Adapters.ReadShelfAdapter;
import com.reading7.Objects.Review;
import com.reading7.Objects.User;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.reading7.Utils.calculateAge;

public class PublicProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<Review> usersReviews;
    private String user_email;
    private User user;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        usersReviews = new ArrayList<>();
        return inflater.inflate(R.layout.public_profile_fragment, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getUserInformation();
        initWishlist();
        initMyBookslist();
    }

    public void setUser(String user_email) {
        this.user_email = user_email;
    }

    private void getUserInformation() {

        DocumentReference userRef = db.collection("Users").document(this.user_email);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String userName = document.getData().get("full_name").toString();
                        ((TextView) getActivity().findViewById(R.id.publicProfile_userName)).setText(userName);

                        String birthDate = document.getData().get("birth_date").toString();
                        ((TextView) getActivity().findViewById(R.id.publicProfile_age)).setText("גיל: " + calculateAge(birthDate));

                        CircleImageView profileImage = getActivity().findViewById(R.id.publicProfile_profileImage);
                        ArrayList<Integer> avatar_details = (ArrayList<Integer>) document.getData().get("avatar_details");
                        Utils.loadAvatar(getContext(), profileImage, avatar_details);

                        ArrayList<String> followers = (ArrayList<String>) document.getData().get("followers");
                        ((TextView) getActivity().findViewById(R.id.publicProfile_followers)).setText(Integer.toString(followers.size()));

                        ArrayList<String> following = (ArrayList<String>) document.getData().get("following");
                        ((TextView) getActivity().findViewById(R.id.publicProfile_following)).setText(Integer.toString(following.size()));

                        ArrayList<String> favouriteBooks = (ArrayList<String>) document.getData().get("favourite_books");

                        ArrayList<String> lastSearches = (ArrayList<String>) document.getData().get("last_searches");

                        ArrayList<String> favouriteGenres = (ArrayList<String>) document.getData().get("favourite_genres");

                        ArrayList<String> likedReviews = (ArrayList<String>) document.getData().get("liked_reviews");


                        user = new User(userName, user_email, birthDate, followers, following, lastSearches,
                                favouriteBooks, favouriteGenres, likedReviews);

                        initFollowButton();
                    } else
                        Toast.makeText(getActivity(), "Account does not exist", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    private void initFollowButton() {

        Button follow = getActivity().findViewById(R.id.follow);
        final String follow_string = getResources().getString(R.string.follow_button);
        final String unfollow_string = getResources().getString(R.string.unfollow_button);

        final FirebaseUser user_me = mAuth.getCurrentUser();

        if (user.getFollowers().contains(user_me.getEmail())) {
            follow.setText(unfollow_string);
            follow.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));

        } else {
            follow.setText(follow_string);
            follow.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
        }

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button follow = getActivity().findViewById(R.id.follow);
                if (follow.getText().equals(follow_string)) {

                    if (user_me.getEmail().equals(user.getEmail()))
                        throw new AssertionError("YOU CANT FOLLOW YOURSELF!!!!");

                    follow.setText(unfollow_string);
                    follow.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));

                    DocumentReference userRef = db.collection("Users").document(user_me.getEmail());
                    userRef.update("following", FieldValue.arrayUnion(user.getEmail()));

                    userRef = db.collection("Users").document(user.getEmail());
                    userRef.update("followers", FieldValue.arrayUnion(user_me.getEmail()));

                } else {

                    follow.setText(follow_string);
                    follow.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));

                    DocumentReference userRef = db.collection("Users").document(user_me.getEmail());
                    userRef.update("following", FieldValue.arrayRemove(user.getEmail()));

                    userRef = db.collection("Users").document(user.getEmail());
                    userRef.update("followers", FieldValue.arrayRemove(user_me.getEmail()));
                }
                //update screen correctly!
                DocumentReference userRef = db.collection("Users").document(user.getEmail());

                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                ArrayList<String> followers = (ArrayList<String>) document.getData().get("followers");
                                ((TextView) getActivity().findViewById(R.id.publicProfile_followers)).setText(Integer.toString(followers.size()));

                            } else
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void initWishlist() {
        getUserReviews();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView wishlistRV = getActivity().findViewById(R.id.wishlistRV);
        wishlistRV.setLayoutManager(layoutManager);
        ReadShelfAdapter adapter = new ReadShelfAdapter(usersReviews, getActivity());
        wishlistRV.setAdapter(adapter);
    }

    private void initMyBookslist() {
        getUserReviews();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView myBooksRV = getActivity().findViewById(R.id.myBooksRV);
        myBooksRV.setLayoutManager(layoutManager);
        ReadShelfAdapter adapter = new ReadShelfAdapter(usersReviews, getActivity());
        myBooksRV.setAdapter(adapter);

    }

    private void disableClicks() {

    }

    private void enableClicks() {

    }

    private void getUserReviews() {
        FirebaseUser mUser = mAuth.getCurrentUser();
        CollectionReference collection = db.collection("Reviews");
        Query query = collection.whereEqualTo("reviewer_email", mUser.getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        usersReviews.add(doc.toObject(Review.class));
                    }
                }
            }
        });
    }
}