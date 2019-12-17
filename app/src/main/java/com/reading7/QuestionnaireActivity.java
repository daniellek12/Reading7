package com.reading7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.AutoCompleteAdapter;
import com.reading7.Adapters.QuestionnaireAdapter;
import com.reading7.Objects.Book;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireActivity extends AppCompatActivity {

    private ArrayList<MultiSpinner> list = new ArrayList<>();
    private ArrayList<Book> favourite_books = new ArrayList<Book>();
    private ArrayList<Book> books = new ArrayList<Book>();
    AutoCompleteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questionnaire_activity);

        final AutoCompleteTextView edit_text =(AutoCompleteTextView) findViewById(R.id.auto_complete);
        Button submit_button = findViewById(R.id.submit);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                for (int i = 0; i < list.size(); i++)
                {
                    if(list.get(i).isSelected() == true){
                        DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users").document(user.getEmail());
                        userRef.update("favourite_genres", FieldValue.arrayUnion(list.get(i).getTitle()));
                    }

                }
                DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users").document(user.getEmail());
                userRef.update("favourite_books", FieldValue.arrayUnion(edit_text.getText().toString()));
                Intent intent = new Intent(QuestionnaireActivity.this, LoginActivity.class);
                intent.putExtra("NEW_USER", true);
                startActivity(intent);
                finish();
            }
        });

        CollectionReference requestCollectionRef = FirebaseFirestore.getInstance().collection("Books");
        requestCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                for(QueryDocumentSnapshot document: task.getResult()){
                    Book book = document.toObject(Book.class);
                    books.add(book);
                }
                adapter = new AutoCompleteAdapter(QuestionnaireActivity.this, books);
                AutoCompleteTextView edit_text = findViewById(R.id.auto_complete);
                edit_text.setAdapter(adapter);
                edit_text.setThreshold(1);
            }
        });




        final String[] select_qualification = {
                "בחר ז'אנרים",
                "פרוזה מקור",
                "פרוזה תרגום",
                "מתח ופעולה",
                "רומן רומנטי",
                "ילדים",
                "פעוטות",
                "ילדי גן",
                "ראשית קריאה",
                "נוער צעיר",
                "נוער בוגר",
                "מד\"ב ופנטזיה"}; //TODO GENRES
        Spinner spinner = (Spinner) findViewById(R.id.spinner);


        for (int i = 0; i < select_qualification.length; i++) {
            MultiSpinner multi_spinner = new MultiSpinner();
            multi_spinner.setTitle(select_qualification[i]);
            multi_spinner.setSelected(false);
            list.add(multi_spinner);
        }
        QuestionnaireAdapter myAdapter = new QuestionnaireAdapter(QuestionnaireActivity.this, 0,
                list);
        spinner.setAdapter(myAdapter);




    }




}
