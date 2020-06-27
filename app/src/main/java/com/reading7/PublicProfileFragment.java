package com.reading7;

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
import com.reading7.Adapters.CustomShelvesAdapter;
import com.reading7.Adapters.ProfileShelfAdapter;
import com.reading7.Objects.Notification;
import com.reading7.Objects.Review;
import com.reading7.Objects.Shelf;
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
import static com.reading7.Utils.isAdmin;

public class PublicProfileFragment extends Fragment {

    final private ArrayList<String> shelfNames = new ArrayList<String>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<String> usersReviewBookNames = new ArrayList<>();
    private ArrayList<String> usersWishListBookNames = new ArrayList<>();
    private String user_email;
    private User user;
    private ProfileShelfAdapter adapterReviews;
    private ProfileShelfAdapter adapterWishList;
    private CustomShelvesAdapter adapterCustomShelves;


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

        getView().findViewById(R.id.private_alert).setVisibility(View.GONE);
        getView().findViewById(R.id.classified_data).setVisibility(View.GONE);

        DocumentReference userRef = db.collection("Users").document(this.user_email);
        Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), false);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        user = document.toObject(User.class);

                        TextView userName = getView().findViewById(R.id.publicProfile_userName);
                        userName.setText(user.getFull_name());

                        TextView birthDate = getView().findViewById(R.id.publicProfile_age);
                        birthDate.setText("גיל: " + calculateAge(user.getBirth_date()));

                        CircleImageView profileImage = getView().findViewById(R.id.publicProfile_profileImage);
                        user.getAvatar().loadIntoImage(getContext(), profileImage);

                        TextView followers = getView().findViewById(R.id.publicProfile_followers);
                        followers.setText(Integer.toString(user.getFollowers().size()));

                        TextView following = getView().findViewById(R.id.publicProfile_following);
                        following.setText(Integer.toString(user.getFollowing().size()));

                        if (isAdmin) {
                            getView().findViewById(R.id.follow).setVisibility(View.GONE);
                        } else {
                            initFollowButton();
                        }

                        check_private();
                        initWishlist();
                        initMyBookslist();
                        initCustomShelves();

                    } else
                        Toast.makeText(getActivity(), "Account does not exist", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initCustomShelves() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        final RecyclerView customShelvesRV = getView().findViewById(R.id.customShelvesRV);
        customShelvesRV.setLayoutManager(layoutManager);
        adapterCustomShelves = new CustomShelvesAdapter(shelfNames, getActivity(), user_email, (ViewGroup) getView(), getActivity());
        customShelvesRV.setAdapter(adapterCustomShelves);

        getUserShelves();

    }

    private void getUserShelves() {
        db.collection("Users").document(user_email)
                .collection("Shelves").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    shelfNames.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        shelfNames.add(doc.toObject(Shelf.class).getShelf_name());
                    }
                    adapterCustomShelves.notifyDataSetChanged();
                }
            }
        });
    }

    private void check_private() {
        if (!user.getIs_private() || user.getFollowers().contains(mAuth.getCurrentUser().getEmail()) || Utils.isAdmin) {
            getView().findViewById(R.id.private_alert).setVisibility(View.GONE);
            getView().findViewById(R.id.classified_data).setVisibility(View.VISIBLE);
        } else {
            getView().findViewById(R.id.private_alert).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.classified_data).setVisibility(View.GONE);
        }
    }


    private void initFollowButton() {

        Button follow = getView().findViewById(R.id.follow);
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
                Button follow = getView().findViewById(R.id.follow);
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

                    } else { //user is private!
                        follow.setText(request_string);
                        follow.setBackgroundTintList(getResources().getColorStateList(R.color.black));

                        DocumentReference userRef;

                        userRef = db.collection("Users").document(user.getEmail());
                        userRef.update("follow_requests", FieldValue.arrayUnion(user_me.getEmail()));
                    }
                    addNotificationFollow(user.getEmail(), user.getIs_private());

                } else { // already following / requested
                    follow.setText(follow_string);
                    follow.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));

                    DocumentReference userRef = db.collection("Users").document(user_me.getEmail());
                    userRef.update("following", FieldValue.arrayRemove(user.getEmail()));

                    userRef = db.collection("Users").document(user.getEmail());
                    userRef.update("followers", FieldValue.arrayRemove(user_me.getEmail()));

                    userRef = db.collection("Users").document(user.getEmail());
                    userRef.update("follow_requests", FieldValue.arrayRemove(user_me.getEmail()));

                    //if user is private and i stopped following him/wait for him to approve my request- add deletion of notifications
                    //may need to add enable disable..
                    CollectionReference requestsRef = db.collection("Users").document(user.getEmail()).collection("Notifications");
                    Query requestQuery = requestsRef.whereEqualTo("from", user_me.getEmail());
                    requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.toObject(Notification.class).getType().equals(getString(R.string.follow_notificiation_public))
                                            || document.toObject(Notification.class).getType().equals(getString(R.string.follow_notificiation_private)))
                                        document.getReference().delete();
                                }
                            }
                        }
                    });


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
                                user.setFollowers(followers);
                                ((TextView) getView().findViewById(R.id.publicProfile_followers)).setText(Integer.toString(followers.size()));
                                check_private();

                            } else
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void addNotificationFollow(String to_email, boolean is_private) {

        db = FirebaseFirestore.getInstance();

        Map<String, Object> notificationMessegae = new HashMap<>();

        if (!is_private) {
            notificationMessegae.put("type", getContext().getResources().getString(R.string.follow_notificiation_public));
            notificationMessegae.put("book_title", "follow_notification_public");//not relvant

        } else {
            notificationMessegae.put("type", getContext().getResources().getString(R.string.follow_notificiation_private));
            notificationMessegae.put("book_title", "follow_notification_private");//not relvant

        }

        notificationMessegae.put("from", mAuth.getCurrentUser().getEmail());
        notificationMessegae.put("time", Timestamp.now());


        db.collection("Users/" + to_email + "/Notifications").add(notificationMessegae).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
            }
        });

    }


    private void initWishlist() {

        String title = getString(R.string.public_my_wishlist) + " " + user.getFull_name();
        final ShelfFragment wishlistShelf = new ShelfFragment(usersWishListBookNames, title, user.getEmail(), ShelfFragment.ShelfType.WISHLIST);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        final RecyclerView wishlistRV = getView().findViewById(R.id.wishlistRV);
        wishlistRV.setLayoutManager(layoutManager);
        adapterWishList = new ProfileShelfAdapter(getActivity(), usersWishListBookNames, wishlistShelf, (ViewGroup) getView(), getActivity());
        wishlistRV.setAdapter(adapterWishList);

        getUserWishList();

        getView().findViewById(R.id.wishlistTitle).setOnClickListener(new View.OnClickListener() {
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
                        getView().findViewById(R.id.wishlistRV).setVisibility(View.INVISIBLE);
                        getView().findViewById(R.id.publicProfile_emptyWishlist).setVisibility(View.VISIBLE);
                    } else {
                        getView().findViewById(R.id.wishlistRV).setVisibility(View.VISIBLE);
                        getView().findViewById(R.id.publicProfile_emptyWishlist).setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    private void initMyBookslist() {

        String title = getString(R.string.public_my_books) + " " + user.getFull_name();
        final ShelfFragment myBooksShelf = new ShelfFragment(usersReviewBookNames, title, user.getEmail(), ShelfFragment.ShelfType.MYBOOKS);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView myBooksRV = getView().findViewById(R.id.myBooksRV);
        myBooksRV.setLayoutManager(layoutManager);
        adapterReviews = new ProfileShelfAdapter(getActivity(), usersReviewBookNames, myBooksShelf, (ViewGroup) getView(), getActivity());
        myBooksRV.setAdapter(adapterReviews);

        getUserReviews();

        getView().findViewById(R.id.mybooksTitle).setOnClickListener(new View.OnClickListener() {
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

                    TextView reviews_num = getView().findViewById(R.id.publicProfile_recommendations);
                    reviews_num.setText(Integer.toString(usersReviewBookNames.size()));

                    if (usersReviewBookNames.isEmpty()) {
                        getView().findViewById(R.id.myBooksRV).setVisibility(View.INVISIBLE);
                        getView().findViewById(R.id.publicProfile_emptyMyBooks).setVisibility(View.VISIBLE);
                    } else {
                        getView().findViewById(R.id.myBooksRV).setVisibility(View.VISIBLE);
                        getView().findViewById(R.id.publicProfile_emptyMyBooks).setVisibility(View.INVISIBLE);
                    }
                }
                Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), true);
            }
        });
    }

}