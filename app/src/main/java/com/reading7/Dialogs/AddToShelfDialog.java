package com.reading7.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Objects.Shelf;
import com.reading7.R;

import java.util.ArrayList;

public class AddToShelfDialog extends AppCompatDialogFragment {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private View view;
    private ArrayList<String> shelfNames = new ArrayList<>();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        getUserShelves();

        final String book_title = getArguments().getString("book_title");

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        view = getActivity().getLayoutInflater().inflate(R.layout.add_to_shelf_dialog, null);
        final EditText shelfText = view.findViewById(R.id.shelf_name);

        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String shelf_name = shelfText.getText().toString().trim();

                if (shelf_name.isEmpty()){
                    String error = shelf_name + "עדיין לא בחרת מדף...";
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    return;
                }

                if (!shelfNames.contains(shelf_name)){
                    String error = "אופס! אין לך מדף ששמו " + shelf_name;
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    return;
                }

                CollectionReference requestCollectionRef = db.collection("Users")
                        .document(mAuth.getCurrentUser().getEmail()).collection("Shelves");
                Query requestQuery = requestCollectionRef.whereEqualTo("shelf_name", shelf_name);
                requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference shelfRef = document.getReference();
                                shelfRef.update("book_names", FieldValue.arrayUnion(book_title));
                                //Toast.makeText(AddToShelfDialog.this.getContext(),  "הספר נוסף למדף " + shelf_name, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                dismiss();
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    private void getUserShelves(){
        db.collection("Users").document(mAuth.getCurrentUser().getEmail())
                .collection("Shelves").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    shelfNames.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        shelfNames.add(doc.toObject(Shelf.class).getShelf_name());
                    }
                }
            }
        });
    }
}