package com.reading7;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SearchBooksFragment extends Fragment {


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

        ListView list = getActivity().findViewById(R.id.listView);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String title = ((TextView)view.findViewById(R.id.title)).getText().toString();
                CollectionReference requestCollectionRef = FirebaseFirestore.getInstance().collection("Books");
                Query requestQuery = requestCollectionRef.whereEqualTo("title",title);
                requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            Book book = new Book();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                book = document.toObject(Book.class);
                            }

                            ((MainActivity)getActivity()).loadBookFragment(new BookFragment(), book);
                        }
                    }
                });
            }
        });
    }
}
