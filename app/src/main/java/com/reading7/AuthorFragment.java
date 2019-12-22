package com.reading7;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.SearchBooksAdapter;
import com.reading7.Objects.Book;

import java.util.ArrayList;

public class AuthorFragment  extends Fragment {

    private FirebaseFirestore db;
    private ArrayList<Book> books = new ArrayList<Book>();
    private String author_name;
    private SearchBooksAdapter adapter;


    public AuthorFragment(String author_name){
        this.author_name = author_name;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.author_fragment, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBooks();
        initBackBtn();
        ((TextView)view.findViewById(R.id.toolbar_title)).setText("הספרים של "+author_name);
    }


    private void initBackBtn() {
        getActivity().findViewById(R.id.af_backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }


    private void initBooks() {

        final ListView list = getActivity().findViewById(R.id.booksRV);

        CollectionReference requestBooksRef = FirebaseFirestore.getInstance().collection("Books");
        requestBooksRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Book book = document.toObject(Book.class);

                    if(!books.contains(book) && book.getAuthor().equals(author_name))
                        books.add(book);
                }

                adapter = new SearchBooksAdapter(getContext(), books);
                list.setAdapter(adapter);
            }
        });


    }

}
