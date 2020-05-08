package com.reading7;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.NotificationListAdapter;
import com.reading7.Adapters.ReviewListAdapter;
import com.reading7.Objects.Notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<Notification> notifications = new ArrayList<Notification>();
    private RecyclerView notificationsRV;

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
        initBackButton();
    }


    private void initBackButton(){
        getActivity().findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }


    private void initNotifications() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
         notificationsRV = getActivity().findViewById(R.id.notifications);
        notificationsRV.setLayoutManager(layoutManager);
        createNotifications(notificationsRV);
    }


    private void createNotifications(final RecyclerView notificationsRV) {

        notifications.clear();
        Utils.enableDisableClicks(getActivity(), (ViewGroup)getView(), false);
        final ArrayList<Notification> newlist = new ArrayList<>();
        final FirebaseUser mUser = mAuth.getCurrentUser();
        final CollectionReference requestCollectionRef = db.collection("Users").document(mUser.getEmail()).collection("Notifications");
        Query requestQuery = requestCollectionRef.orderBy("time",com.google.firebase.firestore.Query.Direction.DESCENDING).limit(30); //TODO change to chunks with limit
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Notification n = document.toObject(Notification.class);
                        if (!newlist.contains(n))
                            newlist.add(n);
                    }

                    notifications.addAll(newlist);
                    final NotificationListAdapter adapter = new NotificationListAdapter(getActivity(), notifications,getActivity(),NotificationsFragment.this);
                    notificationsRV.setAdapter(adapter);

                    //Swipe to delete
                    new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

                        @Override
                        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                            return false;
                        }

                        @Override
                        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                            adapter.deleteNotification(viewHolder.getAdapterPosition());

                        }
                    }).attachToRecyclerView(notificationsRV);

                    Utils.enableDisableClicks(getActivity(), (ViewGroup)getView(), true);
                }
            }
        });

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode==808) {


            String stringtime = data.getStringExtra("time");
            ((NotificationListAdapter) notificationsRV.getAdapter()).notifyNotificationChallengedChanged(stringtime);
        }


    }


}


