package com.reading7;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reading7.Adapters.SearchBooksAdapter;
import com.reading7.Objects.Author;
import com.reading7.Objects.Book;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AuthorFragment extends Fragment {

    private Author author;

    public AuthorFragment(Author author) {
        this.author = author;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.author_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView) view.findViewById(R.id.toolbar_title)).setText(getString(R.string.authors_books, author.getName()));
        initBooks();
        initBackBtn();
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
        ArrayList<Book> books = new ArrayList<Book>();
        GenericSearchFragment fragment = new GenericSearchFragment<>(Book.class, new SearchBooksAdapter(getContext(), books), books, R.layout.search_books_fragment, "Books", "author", false);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.books_container, fragment, fragment.toString())
                .commit();
        fragment.onQueryTextChange(author.getName());
    }
}
