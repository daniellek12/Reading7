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
import com.google.android.gms.tasks.OnSuccessListener;
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
    private FirebaseFirestore db;

    private DocumentSnapshot lastVisible = null;
    private boolean isLastItemReached = false;
    private int limit = 5;

    private String search_txt = null;

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
        super.onViewCreated(view, savedInstanceState);
        initListView();
//        load_results("");

//        Button button = getActivity().findViewById(R.id.search_load_more_btn);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                load_results(((androidx.appcompat.widget.SearchView) getActivity().findViewById(R.id.searchView)).getQuery().toString());
//            }
//        });
    }


    private void initListView() {

        db = FirebaseFirestore.getInstance();
        Button load_btn = getActivity().findViewById(R.id.search_load_more_btn);
        load_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load_items();
            }
        });
        initItems();
        initAdapter();

//        if(adapter != null){
//            list.setAdapter(adapter);
//            onQueryTextChange(((androidx.appcompat.widget.SearchView)getActivity().findViewById(R.id.searchView)).getQuery().toString());
//            return;
//        }

//        CollectionReference requestBooksRef = FirebaseFirestore.getInstance().collection("Books"); // FIXME make generic!
//        requestBooksRef.whereEqualTo("title", "").limit(2).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() { // FIXME get limit
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        T t = (T) document.toObject(class_type);
//
//                        if (!adapter_list.contains(t))
//                            adapter_list.add(t);
//                    }
//
////                adapter = new SearchBooksAdapter(getContext(), books);
//                    list.setAdapter(adapter);
//                    int last_index = task.getResult().size();
//                    if (last_index > 0) {
//                        lastVisible = task.getResult().getDocuments().get(last_index - 1);
//                    } else {
//                        lastVisible = null;
//                    }
////                onQueryTextChange(((androidx.appcompat.widget.SearchView) getActivity().findViewById(R.id.searchView)).getQuery().toString());
//                }
//            }
//        });
//        lastVisible = null;
//        isLastItemReached = false;
//        ((SearchFragment) getParentFragment()).initViewPagerOnPageChanged();
    }

    private void initAdapter() {
        list = getActivity().findViewById(R.id.booksListView); // FIXME make generic!
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void load_items() {
        final ArrayList<T> newlist = new ArrayList<>();
        final CollectionReference requestCollectionRef = db.collection("Books");
        Query requestQuery = requestCollectionRef.orderBy("title").limit(limit);

        int firstVisibleItemPosition = list.getFirstVisiblePosition();
        int visibleItemCount = list.getChildCount();
        final int totalItemCount = list.getCount();

        if ((firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
            Query nextQuery = requestCollectionRef.startAfter(lastVisible).limit(limit);
            nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> t) {
                    if (t.isSuccessful()) {
                        for (DocumentSnapshot d : t.getResult()) {
                            T ob = (T) d.toObject(class_type);
                            newlist.add(ob);
                        }
                        adapter_list.addAll(newlist);
//                        for (int i = totalItemCount; i < totalItemCount + t.getResult().size(); i++) {
////                            adapter.notifyItemInserted(i);
//                        }
                        adapter.notifyDataSetChanged();
                        lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);
                        if (t.getResult().size() < limit) {
                            isLastItemReached = true;
                        }
                    }
                }
            });
        }
    }

    private void initItems() {
        final ArrayList<T> newlist = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference requestCollectionRef = db.collection("Books");
        Query requestQuery = requestCollectionRef.orderBy("title").limit(7); // init limit
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        T ob = (T) document.toObject(class_type);
                        newlist.add(ob);
                    }
                    adapter_list.addAll(newlist);
                    adapter.notifyDataSetChanged();//no problem cause this is the first update
                    lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                }
            }
        });
    }

    private void searchItems(String txt) {
        adapter_list.clear();
        final ArrayList<T> newlist = new ArrayList<>();
        String txtEnd;
        if (txt.isEmpty()) {
            txtEnd = "\uf8ff";
        } else {
            txtEnd = txt.substring(0, txt.length() - 1) + (char) (txt.charAt(txt.length() - 1) + 1);
        }
        final CollectionReference requestCollectionRef = db.collection("Books");
        Query requestQuery = requestCollectionRef.orderBy("title").whereGreaterThanOrEqualTo("title", txt).whereLessThan("title", txtEnd).limit(limit);

        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        T ob = (T) document.toObject(class_type);
                        newlist.add(ob);
                    }
                    adapter_list.addAll(newlist);
                    adapter.notifyDataSetChanged();//no problem cause this is the first update
                }
            }
        });
    }

//    private void load_results(String string) {
//        adapter_list.clear();
//        final ArrayList<T> newlist = new ArrayList<>();
//        String txtEnd;
//        if (string.isEmpty()) {
//            txtEnd = "\uf8ff";
//        } else {
//            txtEnd = string.substring(0, string.length() - 1) + (char) (string.charAt(string.length() - 1) + 1);
//        }
//
//        final CollectionReference requestCollectionRef = db.collection("Books");
//        Query requestQuery = requestCollectionRef.orderBy("title").whereGreaterThanOrEqualTo("title", string).whereLessThan("title", txtEnd).limit(limit);
//
//
//        int firstVisibleItemPosition = list.getFirstVisiblePosition();
//        int visibleItemCount = list.getChildCount();
//        final int totalItemCount = list.getCount();
//
//
//        if ((firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
//            Query query;
//            if (lastVisible == null) {
//                query = requestBooksRef.orderBy("title").startAt(string).endAt(string + "\uf8ff").limit(4);
//            } else {
//                query = requestBooksRef.orderBy("title").startAt(string).endAt(string + "\uf8ff").startAfter(lastVisible).limit(4);
//            }
//            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() { // FIXME get limit
//                @Override
//                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    if (task.isSuccessful()) {
////                        adapter_list.clear();
//
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            T t = (T) document.toObject(class_type);
//
////                    if (!books.contains(book))
////                        books.add(book);
//                            newlist.add(t);
//                        }
//                        adapter_list.addAll(newlist);
////                    for (int i = totalItemCount; i < totalItemCount + task.getResult().size(); i++) {
////                        adapter.notifyItemInserted(i);
////                    }
////                Toast.makeText(getContext(), "list size: ".concat(Integer.toString(books.size())), Toast.LENGTH_SHORT).show();
//                        if (adapter != null) {
//                            adapter.notifyDataSetChanged();
//                        }
//
//                        int last_index = task.getResult().size();
//                        if (last_index > 0) {
//                            lastVisible = task.getResult().getDocuments().get(last_index - 1);
//                        } else {
//                            lastVisible = null;
//                        }
//                        if (task.getResult().size() < 4) {
//                            isLastItemReached = true;
//                        }
//                    }
//                }
//            });
//        }
//    }

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

        if (search_timer != null) search_timer.cancel();
        search_timer = new CountDownTimer(500, 1000) { //1st parameter in msec
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                searchItems(string);
            }
        };

        search_timer.start();
        return false;
    }

}

