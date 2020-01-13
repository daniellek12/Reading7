package com.reading7;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
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
import com.reading7.Adapters.ProfileShelfAdapter;
import com.reading7.Objects.Review;
import com.reading7.Objects.User;
import com.reading7.Objects.WishList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private ArrayList<String> usersReviewBookNames = new ArrayList<>();
    private ArrayList<String> usersWishListBookNames = new ArrayList<>();
    private String user_email;
    private User user;
    private ProfileShelfAdapter adapterReviews;
    private ProfileShelfAdapter adapterWishList;

    public PublicProfileFragment(String user_email) {
        this.user_email = user_email;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.public_profile_fragment, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getUserInformation();
    }

    private void getUserInformation() {

        getActivity().findViewById(R.id.private_alert).setVisibility(View.GONE);
        getActivity().findViewById(R.id.classified_data).setVisibility(View.GONE);

        DocumentReference userRef = db.collection("Users").document(this.user_email);
        Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), false);
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

                        ArrayList<String> follow_requests = (ArrayList<String>) document.getData().get("follow_requests");

                        ArrayList<String> favouriteBooks = (ArrayList<String>) document.getData().get("favourite_books");

                        ArrayList<String> lastSearches = (ArrayList<String>) document.getData().get("last_searches");

                        ArrayList<String> favouriteGenres = (ArrayList<String>) document.getData().get("favourite_genres");

                        ArrayList<String> likedReviews = (ArrayList<String>) document.getData().get("liked_reviews");

                        Boolean is_private = (Boolean) document.getData().get("is_private");


                        user = new User(userName, user_email, birthDate, followers, following, follow_requests, lastSearches,
                                favouriteBooks, favouriteGenres, likedReviews, avatar_details, is_private);

                        check_private();
                        initFollowButton();
                        initWishlist();
                        initMyBookslist();

                    } else
                        Toast.makeText(getActivity(), "Account does not exist", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void check_private() {
        if (!user.getIs_private() || user.getFollowers().contains(mAuth.getCurrentUser().getEmail())) {
            getActivity().findViewById(R.id.private_alert).setVisibility(View.GONE);
            getActivity().findViewById(R.id.classified_data).setVisibility(View.VISIBLE);
        } else {
            getActivity().findViewById(R.id.private_alert).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.classified_data).setVisibility(View.GONE);
        }
    }


    private void initFollowButton() {

        Button follow = getActivity().findViewById(R.id.follow);
        final String follow_string = getResources().getString(R.string.follow_button);
        final String unfollow_string = getResources().getString(R.string.unfollow_button);
        final String request_string = getResources().getString(R.string.request_string);

        final FirebaseUser user_me = mAuth.getCurrentUser();

        if (user.getFollowers().contains(user_me.getEmail())) {
            follow.setText(unfollow_string);
            follow.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));

        } else if (user.getFollow_requests().contains(user_me.getEmail())) {
            follow.setText(request_string);
            follow.setBackgroundTintList(getResources().getColorStateList(R.color.black));
        } else {
            follow.setText(follow_string);
            follow.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
        }

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button follow = getActivity().findViewById(R.id.follow);
                if (follow.getText().equals(follow_string)) { // follow the user
                    if (user_me.getEmail().equals(user.getEmail()))
                        throw new AssertionError("YOU CANT FOLLOW YOURSELF!!!!");
                    if (!user.getIs_private()) {

                        follow.setText(unfollow_string);
                        follow.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));

                        DocumentReference userRef = db.collection("Users").document(user_me.getEmail());
                        userRef.update("following", FieldValue.arrayUnion(user.getEmail()));

                        userRef = db.collection("Users").document(user.getEmail());
                        userRef.update("followers", FieldValue.arrayUnion(user_me.getEmail()));

                        /* currently commented until notifications will work */
                        //Notification
//                        addNotificationFollow(user.getEmail(), user.getIs_notify());
                    } else { //user is private!
                        follow.setText(request_string);
                        follow.setBackgroundTintList(getResources().getColorStateList(R.color.black));

                        DocumentReference userRef;
                        //FIXME should we add who I requested to follow for each user?
//                        userRef = db.collection("Users").document(user_me.getEmail());
//                        userRef.update("following", FieldValue.arrayUnion(user.getEmail()));

                        userRef = db.collection("Users").document(user.getEmail());
                        userRef.update("follow_requests", FieldValue.arrayUnion(user_me.getEmail()));
                    }
                } else { // already following
                    follow.setText(follow_string);
                    follow.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));

                    DocumentReference userRef = db.collection("Users").document(user_me.getEmail());
                    userRef.update("following", FieldValue.arrayRemove(user.getEmail()));

                    userRef = db.collection("Users").document(user.getEmail());
                    userRef.update("followers", FieldValue.arrayRemove(user_me.getEmail()));

                    userRef = db.collection("Users").document(user.getEmail());
                    userRef.update("follow_requests", FieldValue.arrayRemove(user_me.getEmail()));
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

    private void addNotificationFollow(String to_email, boolean is_notify) {
        if (is_notify) {
            db = FirebaseFirestore.getInstance();

            Map<String, Object> notificationMessegae = new HashMap<>();

            notificationMessegae.put("type", getContext().getResources().getString(R.string.follow_notificiation));
            notificationMessegae.put("from", mAuth.getCurrentUser().getEmail());
            notificationMessegae.put("user_name", ((MainActivity) getActivity()).getCurrentUser().getFull_name());
            notificationMessegae.put("book_title", "follow_notification");//not relvant
            notificationMessegae.put("time", Timestamp.now());
            notificationMessegae.put("user_avatar", ((MainActivity) getActivity()).getCurrentUser().getAvatar_details());


            db.collection("Users/" + to_email + "/Notifications").add(notificationMessegae).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                }
            });
        }
    }


    private void initWishlist() {

        String title = getString(R.string.public_my_wishlist) + " " + user.getFull_name();
        final ShelfFragment wishlistShelf = new ShelfFragment(usersWishListBookNames, title, user.getEmail(), ShelfFragment.ShelfType.WISHLIST);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        final RecyclerView wishlistRV = getActivity().findViewById(R.id.wishlistRV);
        wishlistRV.setLayoutManager(layoutManager);
        adapterWishList = new ProfileShelfAdapter(getActivity(), usersWishListBookNames, wishlistShelf);
        wishlistRV.setAdapter(adapterWishList);

        getUserWishList();

        getActivity().findViewById(R.id.wishlistTitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).addFragment(wishlistShelf);
            }
        });
    }

    private void getUserWishList() {

        CollectionReference collection = db.collection("Wishlist");
        Query query = collection.whereEqualTo("user_email", user.getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    usersWishListBookNames.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        usersWishListBookNames.add(doc.toObject(WishList.class).getBook_title());
                    }
                    adapterWishList.notifyDataSetChanged();

                    if (usersWishListBookNames.isEmpty()) {
                        getActivity().findViewById(R.id.wishlistRV).setVisibility(View.INVISIBLE);
                        getActivity().findViewById(R.id.publicProfile_emptyWishlist).setVisibility(View.VISIBLE);
                    } else {
                        getActivity().findViewById(R.id.wishlistRV).setVisibility(View.VISIBLE);
                        getActivity().findViewById(R.id.publicProfile_emptyWishlist).setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    private void initMyBookslist() {

        String title = getString(R.string.public_my_books) + " " + user.getFull_name();
        final ShelfFragment myBooksShelf = new ShelfFragment(usersReviewBookNames, title, user.getEmail(), ShelfFragment.ShelfType.MYBOOKS);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView myBooksRV = getActivity().findViewById(R.id.myBooksRV);
        myBooksRV.setLayoutManager(layoutManager);
        adapterReviews = new ProfileShelfAdapter(getActivity(), usersReviewBookNames, myBooksShelf);
        myBooksRV.setAdapter(adapterReviews);

        getUserReviews();

        getActivity().findViewById(R.id.mybooksTitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).addFragment(myBooksShelf);
            }
        });


    }

    private void getUserReviews() {

        CollectionReference collection = db.collection("Reviews");
        Query query = collection.whereEqualTo("reviewer_email", user.getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    usersReviewBookNames.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        usersReviewBookNames.add(doc.toObject(Review.class).getBook_title());
                    }

                    adapterReviews.notifyDataSetChanged();

                    TextView reviews_num = getActivity().findViewById(R.id.publicProfile_recommendations);
                    reviews_num.setText(Integer.toString(usersReviewBookNames.size()));

                    if (usersReviewBookNames.isEmpty()) {
                        getActivity().findViewById(R.id.myBooksRV).setVisibility(View.INVISIBLE);
                        getActivity().findViewById(R.id.publicProfile_emptyMyBooks).setVisibility(View.VISIBLE);
                    } else {
                        getActivity().findViewById(R.id.myBooksRV).setVisibility(View.VISIBLE);
                        getActivity().findViewById(R.id.publicProfile_emptyMyBooks).setVisibility(View.INVISIBLE);
                    }
                }
                Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), true);
            }
        });
    }
}