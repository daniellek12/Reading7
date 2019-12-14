package com.reading7;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.SearchFriendsAdapter;

import java.util.ArrayList;


public class SearchFriendsFragment extends Fragment implements androidx.appcompat.widget.SearchView.OnQueryTextListener {

    private SearchFriendsAdapter adapter;
    private ArrayList<User> users = new ArrayList<User>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_friends_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        initListView();
        super.onViewCreated(view, savedInstanceState);
    }


    private void initListView() {

        final ListView list = getActivity().findViewById(R.id.usersListView);

        if(adapter != null){
            list.setAdapter(adapter);
            onQueryTextChange(((androidx.appcompat.widget.SearchView)getActivity().findViewById(R.id.searchView)).getQuery().toString());
            return;
        }

        CollectionReference requestUsersRef = FirebaseFirestore.getInstance().collection("Users");
        requestUsersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    users.add(user);
                }

                adapter = new SearchFriendsAdapter(getContext(), users);
                list.setAdapter(adapter);
                onQueryTextChange(((androidx.appcompat.widget.SearchView)getActivity().findViewById(R.id.searchView)).getQuery().toString());
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String string) {
        if(adapter == null) return false;
        Filter filter = adapter.getFilter();
        filter.filter(string);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String string) {
        if(adapter == null) return false;
        Filter filter = adapter.getFilter();
        filter.filter(string);
        return true;
    }
}
