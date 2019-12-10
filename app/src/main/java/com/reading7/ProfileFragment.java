package com.reading7;

import android.content.Intent;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.ReadShelfAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.reading7.Utils.calculateAge;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<Review> usersReviews;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        usersReviews = new ArrayList<>();
        return inflater.inflate(R.layout.profile_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getUserInformation();
        initEditBtn();
        initLogOutBtn();
        initWishlist();
        initMyBookslist();

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
                        User user = new User();

                        TextView userName = getActivity().findViewById(R.id.userName);
                        userName.setText(document.getData().get("full_name").toString());

                        TextView userAge = getActivity().findViewById(R.id.age);
                        userAge.setText("גיל: " + calculateAge(document.getData().get("birth_date").toString()));

                        TextView followers = getActivity().findViewById(R.id.followers);
                        ArrayList<String> arr = (ArrayList<String>) document.getData().get("followers");
                        followers.setText(Integer.toString(arr.size()));

                        TextView following = getActivity().findViewById(R.id.following);
                        arr = (ArrayList<String>) document.getData().get("following");
                        following.setText(Integer.toString(arr.size()));
                    } else
                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

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

    private void initEditBtn() {

        final Button edit_btn = getActivity().findViewById(R.id.edit);
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                disableClicks();
                Toast.makeText(getContext(), "EDIT PROFILE", Toast.LENGTH_SHORT).show();
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
        FirebaseUser mUser = mAuth.getCurrentUser();
        CollectionReference collection =  db.collection("Reviews");
        Query query = collection.whereEqualTo("reviwer_email", mUser.getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        usersReviews.add(doc.toObject(Review.class));
                    }
                }
            }
        });
    }
}