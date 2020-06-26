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
import com.reading7.Adapters.SearchAuthorsAdapter;
import com.reading7.Objects.Book;

import java.util.ArrayList;

public class SearchAuthorsFragment extends Fragment implements androidx.appcompat.widget.SearchView.OnQueryTextListener {

    private SearchAuthorsAdapter adapter;
    private ArrayList<String> authors = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_authors_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        initListView();
        super.onViewCreated(view, savedInstanceState);
    }


    private void initListView() {

        final ListView list = getView().findViewById(R.id.search_list);

        if(adapter != null){
            list.setAdapter(adapter);
            onQueryTextChange(((androidx.appcompat.widget.SearchView)getActivity().findViewById(R.id.searchView)).getQuery().toString());
            return;
        }
        //TODO reimplement this, ask Rotem

//        CollectionReference requestUsersRef = FirebaseFirestore.getInstance().collection("Books");
//        requestUsersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    Book book = document.toObject(Book.class);
//                    if(!authors.contains(book.getAuthor()))
//                        authors.add(book.getAuthor());
//                }
//
//                adapter = new SearchAuthorsAdapter(getContext(), authors);
//                list.setAdapter(adapter);
//                onQueryTextChange(((androidx.appcompat.widget.SearchView)getActivity().findViewById(R.id.searchView)).getQuery().toString());
//            }
//        });
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
