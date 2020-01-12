package com.reading7;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.HashMap;
import java.util.Map;

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
    final private ArrayList<String> usersReviewBookNames = new ArrayList<String>();
    final private ArrayList<String> usersWishlistBookNames = new ArrayList<String>();
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
        ((BottomNavigationView) getActivity().findViewById(R.id.navigation)).setSelectedItemId(R.id.navigation_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        getUserInformation();

        initOptionsMenu();
//        ImageButton dc = (ImageButton) getActivity().findViewById(R.id.settings);
//        dc.setVisibility(View.GONE);
        /*dc.bringToFront();
        dc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                try {
//                    Utils.convertTxtToBook(getContext());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                Toast.makeText(getActivity(), "IF YOU TOUCH THIS BUTTON EVER AGAIN ROTEM WILL KILL YOU.", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private void getUserInformation() {

        FirebaseUser mUser = mAuth.getCurrentUser();
        DocumentReference userRef = db.collection("Users").document(mUser.getEmail());
        disableClicks();

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

                        //initPrivateBtn();
                        initLogOutBtn();
                        initWishlist();
                        initMyBookslist();


                    } else
                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initWishlist() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        final RecyclerView wishlistRV = getActivity().findViewById(R.id.wishlistRV);
        wishlistRV.setLayoutManager(layoutManager);
        adapterWishList = new WishListAdapter(usersWishlistBookNames, getActivity());
        wishlistRV.setAdapter(adapterWishList);

        getUserWishList();

        getActivity().findViewById(R.id.wishlistTitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).addFragment(new ShelfFragment(usersWishlistBookNames, getString(R.string.my_wishlist), mAuth.getCurrentUser().getEmail(), ShelfFragment.ShelfType.WISHLIST));
            }
        });
    }

    private void initMyBookslist() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView myBooksRV = getActivity().findViewById(R.id.myBooksRV);
        myBooksRV.setLayoutManager(layoutManager);
        adapterReviews = new ReadShelfAdapter(usersReviewBookNames, getActivity());
        myBooksRV.setAdapter(adapterReviews);

        getUserReviews();

        getActivity().findViewById(R.id.mybooksTitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).addFragment(new ShelfFragment(usersReviewBookNames, getString(R.string.my_books), mAuth.getCurrentUser().getEmail(), ShelfFragment.ShelfType.MYBOOKS));
            }
        });

    }

//    private void initPrivateBtn() {
//        final Switch sw = (Switch) getActivity().findViewById(R.id.private_switch);
//        DocumentReference userRef = db.collection("Users").document(mAuth.getCurrentUser().getEmail());
//        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        Boolean is_private = (Boolean) document.getData().get("is_private");
//                        sw.setChecked(is_private);
//                        sw.setVisibility(View.VISIBLE);
//                    } else
//                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//
//                } else
//                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        sw.bringToFront();
//        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                disableClicks();
//                if (isChecked) {
//                    db.collection("Users").document(mAuth.getCurrentUser().getEmail()).update("is_private", true);
//                    enableClicks();
//                } else {
//                    db.collection("Users").document(mAuth.getCurrentUser().getEmail()).update("is_private", false);
//                    enableClicks();
//                }
//            }
//        });
//    }

    private void initLogOutBtn() {

        final RelativeLayout logoutButton = getActivity().findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                disableClicks();
                getActivity().findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
                removeTokenId();

            }
        });
    }

    private void removeTokenId(){

        Map<String,Object> removeToken = new HashMap<>();
        removeToken.put("token_id","");
        db.collection("Users").document(mAuth.getCurrentUser().getEmail()).update(removeToken).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
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

    private void initOptionsMenu(){

        final RelativeLayout optionsLayout = getActivity().findViewById(R.id.optionsMenuLayout);
        final ImageButton optionsButton = getActivity().findViewById(R.id.options);

        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionsLayout.setVisibility(View.VISIBLE);
            }
        });

        getActivity().findViewById(R.id.optionsDummy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionsLayout.setVisibility(View.GONE);
            }
        });

        initLogOutBtn();

        final RelativeLayout editProfileButton = getActivity().findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).loadFragment(new EditProfileFragment());
                optionsLayout.setVisibility(View.GONE);
            }
        });

        final RelativeLayout privacyButton = getActivity().findViewById(R.id.privacyButton);
        privacyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).loadFragment(new PrivacySettingsFragment());
                optionsLayout.setVisibility(View.GONE);
            }
        });

        ImageButton notificationBtn = getActivity().findViewById(R.id.notificationsBtn);
        notificationBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).loadFragment(new NotificationsFragment());
                optionsLayout.setVisibility(View.GONE);
            }

        });
    }

    private void disableClicks() {
        getActivity().findViewById(R.id.options).setEnabled(false);
        ((MainActivity) getActivity()).setBottomNavigationEnabled(false);
        getActivity().findViewById(R.id.logoutButton).setEnabled(false);
        getActivity().findViewById(R.id.privacyButton).setEnabled(false);
        getActivity().findViewById(R.id.editProfileButton).setEnabled(false);
        getActivity().findViewById(R.id.notificationsBtn).setEnabled(false);
    }

    private void enableClicks() {
        getActivity().findViewById(R.id.options).setEnabled(true);
        ((MainActivity) getActivity()).setBottomNavigationEnabled(true);
        getActivity().findViewById(R.id.logoutButton).setEnabled(true);
        getActivity().findViewById(R.id.privacyButton).setEnabled(true);
        getActivity().findViewById(R.id.editProfileButton).setEnabled(true);
        getActivity().findViewById(R.id.notificationsBtn).setEnabled(true);
    }

    private void getUserReviews() {

        CollectionReference collection = db.collection("Reviews");
        Query query = collection.whereEqualTo("reviewer_email", mAuth.getCurrentUser().getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    usersReviewBookNames.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        usersReviewBookNames.add(doc.toObject(Review.class).getBook_title());
                    }
                    adapterReviews.notifyDataSetChanged();

                    TextView reviews_num = getActivity().findViewById(R.id.recommendations);
                    reviews_num.setText(Integer.toString(usersReviewBookNames.size()));

                    if (usersReviewBookNames.isEmpty()) {
                        getActivity().findViewById(R.id.myBooksRV).setVisibility(View.INVISIBLE);
                        getActivity().findViewById(R.id.emptyMyBooks).setVisibility(View.VISIBLE);
                    } else {
                        getActivity().findViewById(R.id.myBooksRV).setVisibility(View.VISIBLE);
                        getActivity().findViewById(R.id.emptyMyBooks).setVisibility(View.INVISIBLE);
                    }
                }
                enableClicks();
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
                    usersWishlistBookNames.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        usersWishlistBookNames.add(doc.toObject(WishList.class).getBook_title());
                    }
                    adapterWishList.notifyDataSetChanged();

                    if (usersWishlistBookNames.isEmpty()) {
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

    public void refreshAdapters() {
        if (adapterWishList != null)
            adapterWishList.notifyDataSetChanged();
        if (adapterReviews != null)
            adapterReviews.notifyDataSetChanged();
    }


    @Override
    public String toString() {
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }



}