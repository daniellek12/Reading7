package com.reading7;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.SearchBooksAdapter;
import com.reading7.Objects.Book;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

public class GenericSearchFragment<T> extends Fragment implements androidx.appcompat.widget.SearchView.OnQueryTextListener {

    private BaseAdapter adapter;
    private ArrayList<T> adapter_list;
    private Class class_type;
    private ListView list;

    private DocumentSnapshot lastVisible = null;
    private boolean isLastItemReached = false;

    CountDownTimer search_timer;

    public GenericSearchFragment(Class class_type, BaseAdapter baseAdapter, ArrayList<T> list) {
        this.adapter = baseAdapter;
        this.adapter_list = list;
        this.class_type = class_type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_books_fragment, null); // FIXME make generic!
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initListView();
        super.onViewCreated(view, savedInstanceState);

        Button button = getActivity().findViewById(R.id.search_load_more_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load_results(((androidx.appcompat.widget.SearchView) getActivity().findViewById(R.id.searchView)).getQuery().toString());
            }
        });
    }


    private void initListView() {

        list = getActivity().findViewById(R.id.booksListView); // FIXME make generic!

//        if(adapter != null){
//            list.setAdapter(adapter);
//            onQueryTextChange(((androidx.appcompat.widget.SearchView)getActivity().findViewById(R.id.searchView)).getQuery().toString());
//            return;
//        }

        CollectionReference requestBooksRef = FirebaseFirestore.getInstance().collection("Books"); // FIXME make generic!
        requestBooksRef.whereEqualTo("title", "").limit(2).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() { // FIXME get limit
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        T t = (T) document.toObject(class_type);

                        if (!adapter_list.contains(t))
                            adapter_list.add(t);
                    }

//                adapter = new SearchBooksAdapter(getContext(), books);
                    list.setAdapter(adapter);
                    int last_index = task.getResult().size();
                    if (last_index > 0) {
                        lastVisible = task.getResult().getDocuments().get(last_index - 1);
                    } else {
                        lastVisible = null;
                    }
//                onQueryTextChange(((androidx.appcompat.widget.SearchView) getActivity().findViewById(R.id.searchView)).getQuery().toString());
                }
            }
        });

        ((SearchFragment) getParentFragment()).initViewPagerOnPageChanged();
    }

    @Override
    public boolean onQueryTextSubmit(String string) {
//        if(adapter == null) return false;
//        Filter filter = adapter.getFilter();
//        filter.filter(string);
//        return true;
        return false;
    }

    @Override
    public boolean onQueryTextChange(final String string) {
//        if(adapter == null) return false;
//        Filter filter = adapter.getFilter();
//        filter.filter(string);
//        return true;
//        Toast.makeText(getContext(), "TEXT CHANGED", Toast.LENGTH_SHORT).show();

//        search_timer.cancel();

        search_timer = new CountDownTimer(500, 1000) { //1st parameter in msec
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                load_results(string);
            }
        };

        search_timer.start();
        return false;
    }

    private void load_results(String string) {
        if (string.equals("")) {
            adapter.notifyDataSetChanged();
            return;
        }
        CollectionReference requestBooksRef = FirebaseFirestore.getInstance().collection("Books");
//        requestBooksRef.whereEqualTo("title", string).limit(2).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        final ArrayList<T> newlist = new ArrayList<>();


        int firstVisibleItemPosition = list.getFirstVisiblePosition();
        int visibleItemCount = list.getChildCount();
        final int totalItemCount = list.getCount();


        if ((firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
            Query query;
            if (lastVisible == null) {
                query = requestBooksRef.orderBy("title").startAt(string).endAt(string + "\uf8ff").limit(4);
            } else {
                query = requestBooksRef.orderBy("title").startAt(string).endAt(string + "\uf8ff").startAfter(lastVisible).limit(4);
            }
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() { // FIXME get limit
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
//                        adapter_list.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            T t = (T) document.toObject(class_type);

//                    if (!books.contains(book))
//                        books.add(book);
                            newlist.add(t);
                        }
                        adapter_list.addAll(newlist);
//                    for (int i = totalItemCount; i < totalItemCount + task.getResult().size(); i++) {
//                        adapter.notifyItemInserted(i);
//                    }
//                Toast.makeText(getContext(), "list size: ".concat(Integer.toString(books.size())), Toast.LENGTH_SHORT).show();
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }

                        int last_index = task.getResult().size();
                        if (last_index > 0) {
                            lastVisible = task.getResult().getDocuments().get(last_index - 1);
                            if (task.getResult().size() < 4) {
                                isLastItemReached = true;
                            }
                        } else {
                            lastVisible = null;
                        }
                    }
                }
            });
        }
    }
}

