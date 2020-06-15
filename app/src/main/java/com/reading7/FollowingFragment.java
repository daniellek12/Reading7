package com.reading7;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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
import com.reading7.Adapters.FollowingAdapter;
import com.reading7.Objects.Notification;
import com.reading7.Objects.User;

import java.util.ArrayList;

public class FollowingFragment extends Fragment {


    private RecyclerView recyclerView;
    private FollowingAdapter myAdapter;
    private String type;
    private TextView title;

    private ArrayList<String> usersList;
    FirebaseUser firebaseUser;

    FollowingFragment(String type){
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.following_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = getView().findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        initBackButton();

        title = getView().findViewById(R.id.my_title);

        usersList = new ArrayList<String>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Query query = FirebaseFirestore.getInstance().collection("Users").whereEqualTo("email", firebaseUser.getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        if(type == "following"){
                            title.setText("נעקבים");
                            usersList = user.getFollowing();
                        }
                        else {
                            title.setText("עוקבים");
                            usersList = user.getFollowers();
                        }
                        myAdapter = new FollowingAdapter(getContext(), usersList, type);
                        recyclerView.setAdapter(myAdapter);
                    }
                }
            }
        });
    }

    private void initBackButton() {
        getActivity().findViewById(R.id.followingBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }


}