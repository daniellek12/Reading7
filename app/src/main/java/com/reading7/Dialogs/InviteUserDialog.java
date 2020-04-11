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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.MainActivity;
import com.reading7.Objects.Shelf;
import com.reading7.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InviteUserDialog extends AppCompatDialogFragment {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private View view;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        view = getActivity().getLayoutInflater().inflate(R.layout.invite_user_dialog, null);
        final EditText emailText = view.findViewById(R.id.user_email);

        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String to_email = emailText.getText().toString().trim();

                if (to_email.isEmpty()){
                    String error = to_email + "עדיין לא בחרת משתמש ...";
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    return;
                }
                if(to_email.equals(mAuth.getCurrentUser().getEmail()))
                {
                    String error = "לא ניתן לשלוח הזמנה לעצמך ...";
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    return;

                }

                Query requestQuery = db.collection("Users").whereEqualTo("email", to_email);
                requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
                            String error = "אופס! לא קיים משתמש עם המייל " + to_email;
                            Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                            return;}
                            else {

                                String book_title = getArguments().getString("book_title");

                                addNotificationInviteToRead(to_email, book_title);
                            }
                        }
                    }});


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

    private void addNotificationInviteToRead(String to_email, String book_title) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if ( (!(to_email.equals(mAuth.getCurrentUser().getEmail())))) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> notificationMessegae = new HashMap<>();

            notificationMessegae.put("type", getActivity().getResources().getString(R.string.invite_notificiation));
            notificationMessegae.put("from", mAuth.getCurrentUser().getEmail());
            notificationMessegae.put("user_name", ((MainActivity)getActivity()).getUser().getFull_name());//need to think about restarting main activity after updating details
            notificationMessegae.put("book_title", book_title);
            notificationMessegae.put("time", Timestamp.now());
            notificationMessegae.put("user_avatar", ((MainActivity) getContext()).getCurrentUser().getAvatar_details());




            db.collection("Users/" + to_email + "/Notifications").add(notificationMessegae).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    //String m = " ההזמנה נשלחה למייל" + to_email;

                    Toast.makeText(getContext(), "ההזמנה נשלחה ", Toast.LENGTH_LONG).show();

                    dismiss();

                }
            });
        }
    }


}
