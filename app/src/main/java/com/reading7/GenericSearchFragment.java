package com.reading7;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
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


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


public class GenericSearchFragment<T> extends Fragment implements androidx.appcompat.widget.SearchView.OnQueryTextListener {

    private static final String TAG = "GenericSearchFragment";
    private BaseAdapter adapter;
    private ArrayList<T> adapter_list;
    private Class class_type;
    private ListView list;
    private int layout;
    private boolean is_keyword;

    private FirebaseFirestore db;

    private DocumentSnapshot lastVisible = null;
    private boolean isLastItemReached = false;
    private int limit = 7;

    private String collection_name;
    private String field_name;

    private String search_txt = null;

    CountDownTimer search_timer;

    Button load_btn;


    public GenericSearchFragment(Class class_type, BaseAdapter baseAdapter, ArrayList<T> list, int layout, String collection_name, String field_name, boolean is_keyword) {
        this.adapter = baseAdapter;
        this.adapter_list = list;
        this.class_type = class_type;
        this.layout = layout;
        this.collection_name = collection_name;
        this.field_name = field_name;
        this.is_keyword = is_keyword;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(this.layout, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListView();
    }


    private void initListView() {
        db = FirebaseFirestore.getInstance();
        load_btn = getView().findViewById(R.id.search_load_more_btn);
        load_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, search_txt);
                load_items();
            }
        });
//        load_btn.bringToFront();
        load_btn.setVisibility(View.GONE);
//        initItems();
        initAdapter();
    }

    private void initItems() {
        final ArrayList<T> newlist = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference requestCollectionRef = db.collection(collection_name);
        Query requestQuery = requestCollectionRef.orderBy(field_name).limit(limit); // init limit
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        T ob = (T) document.toObject(class_type);
                        newlist.add(ob);
                    }
                    adapter_list.addAll(newlist);
                    adapter.notifyDataSetChanged();//no problem because this is the first update
                    lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                }
            }
        });
    }

    private void initAdapter() {
        list = getView().findViewById(R.id.search_list);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void load_items() {
        final ArrayList<T> newlist = new ArrayList<>();
        String txt = search_txt;
        String txtEnd;
        if (txt.isEmpty()) {
            txtEnd = "\uf8ff";
        } else {
            txtEnd = txt.substring(0, txt.length() - 1) + (char) (txt.charAt(txt.length() - 1) + 1);
        }
        final CollectionReference requestCollectionRef = db.collection(collection_name);
        Query requestQuery;
        if (!is_keyword) {
            requestQuery = requestCollectionRef.orderBy(field_name).whereGreaterThanOrEqualTo(field_name, txt).whereLessThan(field_name, txtEnd).limit(limit);
        } else {
            requestQuery = requestCollectionRef.orderBy(field_name).whereArrayContains("keywords", search_txt).limit(limit);
        }

        int firstVisibleItemPosition = list.getFirstVisiblePosition();
        int visibleItemCount = list.getChildCount();
        final int totalItemCount = list.getCount();

        if ((firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
            Query nextQuery = requestQuery.startAfter(lastVisible).limit(limit);
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
                        int res_size = t.getResult().size();
                        if (res_size < limit) {
                            isLastItemReached = true;
                            load_btn.setVisibility(View.GONE);
                        }
                        if (res_size != 0) {
                            lastVisible = t.getResult().getDocuments().get(res_size - 1);
                        }
                    }
                }
            });
        }
    }


    private void newSearchItems(String txt) {
        adapter_list.clear();
        lastVisible = null;
        isLastItemReached = false;
        search_txt = txt;
        final ArrayList<T> newlist = new ArrayList<>();
        String txtEnd;
        if (txt.isEmpty()) {
            txtEnd = "\uf8ff";
        } else {
            txtEnd = txt.substring(0, txt.length() - 1) + (char) (txt.charAt(txt.length() - 1) + 1);
        }
        final CollectionReference requestCollectionRef = db.collection(collection_name);
        Query requestQuery;
        if (!is_keyword) {
            requestQuery = requestCollectionRef.orderBy(field_name).whereGreaterThanOrEqualTo(field_name, txt).whereLessThan(field_name, txtEnd).limit(limit);
        } else {
            requestQuery = requestCollectionRef.orderBy(field_name).whereArrayContains("keywords", search_txt).limit(limit);
        }
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        T ob = (T) document.toObject(class_type);
                        newlist.add(ob);
                    }
                    if (task.getResult().size() <= 0) { // No results
                        load_btn.setVisibility(View.GONE);
                    } else {
                        load_btn.setVisibility(View.VISIBLE);
                        lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        if (task.getResult().size() < limit) {
                            isLastItemReached = true;
                            load_btn.setVisibility(View.GONE);
                        }
                    }
                    adapter_list.addAll(newlist);
                    adapter.notifyDataSetChanged();//no problem cause this is the first update
                }
            }
        });
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

        if (search_timer != null) search_timer.cancel();
        search_timer = new CountDownTimer(500, 1000) { //1st parameter in msec
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                newSearchItems(string);
            }
        };

        search_timer.start();
        return false;
    }

}

