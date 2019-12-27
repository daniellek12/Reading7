package com.reading7;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.AutoCompleteAdapter;
import com.reading7.Objects.Book;

import java.util.ArrayList;


public class SignUpStep2 extends Fragment {

    private ArrayList<Book> books = new ArrayList<Book>();
    private String book_id = "";
    private AutoCompleteAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sign_up_step2_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final AutoCompleteTextView edit_text = getView().findViewById(R.id.auto_complete);
        edit_text.clearFocus();

        //TODO re-implement this, ask Rotem
//        CollectionReference requestCollectionRef = FirebaseFirestore.getInstance().collection("Books");
//        requestCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//
//                for(QueryDocumentSnapshot document: task.getResult()){
//                    Book book = document.toObject(Book.class);
//                    books.add(book);
//                }
//                adapter = new AutoCompleteAdapter(getContext(), books);
//                AutoCompleteTextView edit_text = getView().findViewById(R.id.auto_complete);
//                edit_text.setAdapter(adapter);
//                edit_text.setThreshold(1);
//                edit_text.clearFocus();
//            }
//        });

        edit_text.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                book_id = ((Book) adapterView.getItemAtPosition(position)).getId();
            }
        });
    }

    public ArrayList<String> getFavouriteBookID(){
        ArrayList<String> result = new ArrayList<String>();
        result.add(0,book_id);
        return result;
    }
}
