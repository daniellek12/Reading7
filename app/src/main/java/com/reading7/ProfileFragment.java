package com.reading7;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.CustomShelvesAdapter;
import com.reading7.Adapters.ProfileShelfAdapter;
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

public class ProfileFragment extends Fragment {

    final private ArrayList<String> usersReviewBookNames = new ArrayList<String>();
    final private ArrayList<String> usersWishlistBookNames = new ArrayList<String>();
    final private ArrayList<String> shelfNames = new ArrayList<String>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProfileShelfAdapter adapterReviews;
    private ProfileShelfAdapter adapterWishList;
    private CustomShelvesAdapter adapterCustomShelves;

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
    }

    private void getUserInformation() {

        FirebaseUser mUser = mAuth.getCurrentUser();
        DocumentReference userRef = db.collection("Users").document(mUser.getEmail());
        Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), false);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        User user = document.toObject(User.class);

                        TextView userName = getActivity().findViewById(R.id.userName);
                        userName.setText(user.getFull_name());

                        TextView userAge = getActivity().findViewById(R.id.age);
                        userAge.setText("גיל: " + calculateAge(user.getBirth_date()));

                        CircleImageView profileImage = getActivity().findViewById(R.id.profileImage);
                        user.getAvatar().loadIntoImage(getContext(), profileImage);

                        TextView followers = getActivity().findViewById(R.id.followers);
                        followers.setText(Integer.toString(user.getFollowers().size()));
                        if (user.getFollowers().size() > 0) {
                            followers.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ((MainActivity) getActivity()).loadFragment(new FollowingFragment(FollowingFragment.FollowingFragmentType.FOLLOWERS));
                                }
                            });
                        }

                        TextView following = getActivity().findViewById(R.id.following);
                        following.setText(Integer.toString(user.getFollowing().size()));
                        if (user.getFollowing().size() > 0) {
                            following.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ((MainActivity) getActivity()).loadFragment(new FollowingFragment(FollowingFragment.FollowingFragmentType.FOLLOWING));
                                }
                            });
                        }


                        //initPrivateBtn();
                        initLogOutBtn();
                        initWishlist();
                        initMyBookslist();
                        initCustomShelves();


                    } else
                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initCustomShelves() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        final RecyclerView customShelvesRV = getActivity().findViewById(R.id.customShelvesRV);
        customShelvesRV.setLayoutManager(layoutManager);
        adapterCustomShelves = new CustomShelvesAdapter(shelfNames, getActivity(), mAuth.getCurrentUser().getEmail(), (ViewGroup) getView(), getActivity());
        customShelvesRV.setAdapter(adapterCustomShelves);

        getUserShelves();

    }

    private void getUserShelves() {
        db.collection("Users").document(mAuth.getCurrentUser().getEmail())
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

    private void initWishlist() {

        final ShelfFragment wishlistShelf = new ShelfFragment(usersWishlistBookNames, getString(R.string.my_wishlist), mAuth.getCurrentUser().getEmail(), ShelfFragment.ShelfType.WISHLIST);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        final RecyclerView wishlistRV = getActivity().findViewById(R.id.wishlistRV);
        wishlistRV.setLayoutManager(layoutManager);
        adapterWishList = new ProfileShelfAdapter(getActivity(), usersWishlistBookNames, wishlistShelf, (ViewGroup) getView(), getActivity());
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

    private void initMyBookslist() {

        final ShelfFragment myBooksShelf = new ShelfFragment(usersReviewBookNames, getString(R.string.my_books), mAuth.getCurrentUser().getEmail(), ShelfFragment.ShelfType.MYBOOKS);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView myBooksRV = getActivity().findViewById(R.id.myBooksRV);
        myBooksRV.setLayoutManager(layoutManager);
        adapterReviews = new ProfileShelfAdapter(getActivity(), usersReviewBookNames, myBooksShelf, (ViewGroup) getView(), getActivity());
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
                Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), true);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), true);
        getActivity().findViewById(R.id.progressBar3).setVisibility(View.GONE);
        getActivity().findViewById(R.id.optionsMenuLayout).setVisibility(View.GONE);
    }

    private void initOptionsMenu() {

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
                ((MainActivity) getActivity()).loadFragment(new EditProfileFragment());
                optionsLayout.setVisibility(View.GONE);
            }
        });

        final RelativeLayout privacyButton = getActivity().findViewById(R.id.privacyButton);
        privacyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).loadFragment(new PrivacySettingsFragment());
                optionsLayout.setVisibility(View.GONE);
            }
        });

        final RelativeLayout reportButton = getActivity().findViewById(R.id.reportButton);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), false);
                getActivity().findViewById(R.id.progressBar3).setVisibility(View.VISIBLE);
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.admin_email)});
                i.putExtra(Intent.EXTRA_SUBJECT, "נושא הפניה");
                i.putExtra(Intent.EXTRA_TEXT, "פירוט על הבעיה");
                try {
                    startActivity(Intent.createChooser(i, "שליחה באמצעות..."));
//                    TODO enable, hide progress, hide menu after return (but in activity result doesn't work)
                    Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), true);
                    getActivity().findViewById(R.id.progressBar3).setVisibility(View.GONE);
                    optionsLayout.setVisibility(View.GONE);

                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getContext(), "לא קיימת אפליקציה שניתן לשלוח באמצעותה דואר אלקטרוני", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton notificationBtn = getActivity().findViewById(R.id.notificationsBtn);
        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).loadFragment(new NotificationsFragment());
                optionsLayout.setVisibility(View.GONE);
            }

        });

        initAddShelfBtn();
    }

    private void initAddShelfBtn() {
        final Context context = getContext();
        Button add_shelf_btn = getActivity().findViewById(R.id.add_custom_list_btn);
        add_shelf_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.new_shelf_name_dialog);
                final EditText shelfNameEditText = dialog.findViewById(R.id.shelf_name);
                Button okBtn = dialog.findViewById(R.id.ok);
                Button cancelBtn = dialog.findViewById(R.id.cancel);
                dialog.show();

                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = shelfNameEditText.getText().toString();
                        if (text.isEmpty()) {
                            String error = "אופס! צריך לבחור שם למדף...";
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (shelfNames.contains(text)) {
                            String error = "כבר יש לך מדף עם השם הזה!";
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                            return;
                        }
                        addNewShelf(text);
                        dialog.dismiss();
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shelfNameEditText.setText("");
                        dialog.dismiss();
                    }
                });
            }
        });

    }

    private void addNewShelf(String shelfName) {
        User currentUser = ((MainActivity) getActivity()).getCurrentUser();
        Shelf shelf = new Shelf("", shelfName);
        DocumentReference newShelf = db.collection("Users").document(currentUser.getEmail())
                .collection("Shelves").document(Timestamp.now().toString());
        shelf.setId(newShelf.getId());
        newShelf.set(shelf);
        shelfNames.add(shelfName);
        adapterCustomShelves.notifyDataSetChanged();
    }

    private void initLogOutBtn() {

        final RelativeLayout logoutButton = getActivity().findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), false);
                getActivity().findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
                removeTokenId();

            }
        });
    }

    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }


    private void removeTokenId() {

        Map<String, Object> removeToken = new HashMap<>();
        removeToken.put("token_id", "");
        db.collection("Users").document(mAuth.getCurrentUser().getEmail()).update(removeToken).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                signOut();
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