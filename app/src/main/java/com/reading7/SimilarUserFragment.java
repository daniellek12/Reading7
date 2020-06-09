package com.reading7;

import android.app.Activity;
import android.content.Context;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.SimilarUsersAdapter;
import com.reading7.Objects.SimilarUser;
import com.reading7.Objects.User;

import java.util.ArrayList;
import java.util.List;

public class SimilarUserFragment extends Fragment {




    List<String> lstUsers ;
    SimilarUsersAdapter myAdapter;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Activity mActivity;
    SwipeRefreshLayout mySwipeRefreshLayout;


    public void getRequests(){
        lstUsers.clear();
        final List<String> newlist = new ArrayList<>();

        CollectionReference collectionRef = db.collection("SimilarUsers");
        Query q= collectionRef.whereEqualTo("user_id",mAuth.getCurrentUser().getEmail());
       q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    RelativeLayout progress = mActivity.findViewById(R.id.progress_layout);
                    RecyclerView myRv = mActivity.findViewById(R.id.my_requests_rv);
                    TextView noRequestsMsg = mActivity.findViewById(R.id.no_requests_msg);


                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String email = ((SimilarUser)(document.toObject(SimilarUser.class))).getSimilar_user();
                       newlist.add(email);



                    }




                    lstUsers.addAll(newlist);
                    myAdapter.notifyDataSetChanged();

                    getActivity().findViewById(R.id.backBtn).setEnabled(true);

                    if(newlist.isEmpty()) {

                        progress.setVisibility(View.GONE);
                        myRv.setVisibility(View.GONE);
                        noRequestsMsg.setVisibility(View.VISIBLE);

                    }

                    else {

                        progress.setVisibility(View.GONE);
                        noRequestsMsg.setVisibility(View.GONE);
                        myRv.setVisibility(View.VISIBLE);
                    }
                }

                else Toast.makeText(mActivity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.similar_users_fragment, null);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpBackBtn();
        lstUsers = new ArrayList<>();
        RecyclerView myrv = getActivity().findViewById(R.id.my_requests_rv);
        myAdapter = new SimilarUsersAdapter(getContext(),lstUsers, mAuth,this);

        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        myrv.setLayoutManager(MyLayoutManager);

        getActivity().findViewById(R.id.backBtn).setEnabled(false);
        myrv.setAdapter(myAdapter);
        getRequests();


        mySwipeRefreshLayout =  (SwipeRefreshLayout)getActivity().findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setColorSchemeColors(getActivity().getResources().getColor(R.color.colorAccent),
                getActivity().getResources().getColor(R.color.colorAccent),
                getActivity().getResources().getColor(R.color.colorAccent));

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getRequests();
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity)context;
    }


    private void setUpBackBtn(){
        ImageView backBtn = getActivity().findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    public void reload(){
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

}
