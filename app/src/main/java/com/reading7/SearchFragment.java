package com.reading7;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.duolingo.open.rtlviewpager.RtlViewPager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.SearchAdapter;
import com.reading7.Adapters.TabsPagerAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


public class SearchFragment extends Fragment implements androidx.appcompat.widget.SearchView.OnQueryTextListener {

    private FirebaseFirestore db;

    ListView list;
    SearchAdapter adapter;
    androidx.appcompat.widget.SearchView searchView;
    ArrayList<Book> books = new ArrayList<Book>();
    RtlViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.search_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = getActivity().findViewById(R.id.viewPager);
        searchView = getActivity().findViewById(R.id.searchView);

        setBooks();
        initSearchView();
        initBackButton();
        initTabsViewPager();
    }


    private void initSearchView() {

        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();

        //Utils.openKeyboard(getContext());

        //remove underline
        View v = getActivity().findViewById(androidx.appcompat.R.id.search_plate);
        v.setBackgroundColor(getActivity().getResources().getColor(R.color.transparent));

        //remove search icon
        ImageView searchViewIcon = (ImageView) searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        ((ViewGroup) searchViewIcon.getParent()).removeView(searchViewIcon);

    }


    private void initBackButton() {

        ImageButton backButton = getActivity().findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.closeKeyboard(getContext());
                getActivity().onBackPressed();
            }
        });

    }


    private void initTabsViewPager() {

        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());

        //TODO: change this to real fragments
        tabsPagerAdapter.addFragment(new SearchBooksFragment(), "ספרים");
        tabsPagerAdapter.addFragment(new SearchBooksFragment(), "סופרים");
        tabsPagerAdapter.addFragment(new SearchBooksFragment(), "חברים");

        ViewPager viewPager = getActivity().findViewById(R.id.viewPager);
        viewPager.setAdapter(tabsPagerAdapter);

        TabLayout tabs = getActivity().findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    private void setBooks() {

        CollectionReference requestCollectionRef = db.collection("Books");
        requestCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Book book = document.toObject(Book.class);
                    books.add(book);
                }

                adapter = new SearchAdapter(getActivity(), books);
                SearchBooksFragment frag = (SearchBooksFragment) viewPager.getAdapter().instantiateItem(viewPager, 0);
                list = frag.getView().findViewById(R.id.listView);
                list.setAdapter(adapter);
                searchView.setOnQueryTextListener(SearchFragment.this);

            }
        });
    }


    @Override
    public boolean onQueryTextSubmit(String s) {

        SearchAdapter searchAdapter = (SearchAdapter)list.getAdapter();
        Filter filter = searchAdapter.getFilter();

        filter.filter(s);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {

        SearchAdapter searchAdapter = (SearchAdapter)list.getAdapter();
        Filter filter = searchAdapter.getFilter();

        filter.filter(s);
        return true;
    }
}
