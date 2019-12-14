package com.reading7;

import android.os.Bundle;
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
import com.reading7.Adapters.SearchBooksAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SearchBooksFragment extends Fragment implements androidx.appcompat.widget.SearchView.OnQueryTextListener {

    SearchBooksAdapter adapter;
    private ArrayList<Book> books = new ArrayList<Book>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_books_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        initListView();
        super.onViewCreated(view, savedInstanceState);
    }


    private void initListView() {

        final ListView list = getActivity().findViewById(R.id.booksListView);

        if(adapter != null){
            list.setAdapter(adapter);
            onQueryTextChange(((androidx.appcompat.widget.SearchView)getActivity().findViewById(R.id.searchView)).getQuery().toString());
            return;
        }

        CollectionReference requestBooksRef = FirebaseFirestore.getInstance().collection("Books");
        requestBooksRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Book book = document.toObject(Book.class);

                    if(!books.contains(book))
                        books.add(book);
                }

                adapter = new SearchBooksAdapter(getContext(), books);
                list.setAdapter(adapter);
                onQueryTextChange(((androidx.appcompat.widget.SearchView)getActivity().findViewById(R.id.searchView)).getQuery().toString());
            }
        });

        ((SearchFragment)getParentFragment()).initViewPagerOnPageChanged();
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
