package com.reading7;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.NotificationListAdapter;
import com.reading7.Objects.Notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<Notification> notifications = new ArrayList<Notification>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.notification_fragment, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initNotifications();
    }


    private void createNotifications(final RecyclerView notificationsRV) {

        disableClicks();
        final ArrayList<Notification> newlist = new ArrayList<>();
        FirebaseUser mUser = mAuth.getCurrentUser();
        final CollectionReference requestCollectionRef = db.collection("Users").document(mUser.getEmail()).collection("Notifications");
        Query requestQuery = requestCollectionRef.limit(100); //TODO change to chunks with limit
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Notification n = document.toObject(Notification.class);
                        newlist.add(n);
                    }
                    notifications.addAll(newlist);
                    Collections.sort(notifications, new Notification.SortByDate());
                    NotificationListAdapter adapter = new NotificationListAdapter(getActivity(), notifications,getActivity());
                    notificationsRV.setAdapter(adapter);
                    enableClicks();
                }
            }
        });

    }


    private void initNotifications() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        RecyclerView notificationsRV = getActivity().findViewById(R.id.notifications);
        notificationsRV.setLayoutManager(layoutManager);
        createNotifications(notificationsRV);
    }

    private void disableClicks() {
        // getActivity().findViewById(R.id.search).setEnabled(false);
//        getActivity().findViewById(R.id.notifications).setEnabled(false);
        ((MainActivity)getActivity()).setBottomNavigationEnabled(false);


    }

    private void enableClicks() {
        //    getActivity().findViewById(R.id.search).setEnabled(true);
//        getActivity().findViewById(R.id.notifications).setEnabled(true);
        ((MainActivity)getActivity()).setBottomNavigationEnabled(true);
    }

}


