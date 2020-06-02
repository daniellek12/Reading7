package com.reading7.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.R;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class InviteUserDialog extends AppCompatDialogFragment {

    private String book_title;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        book_title = getArguments().getString("book_title");

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.invite_user_dialog, null);

        initShareButtons(dialogView);
        initInAppInviteButton(dialogView);

        builder.setView(dialogView);
        return builder.create();
    }


    private void initInAppInviteButton(final View dialogView) {
        dialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText emailText = dialogView.findViewById(R.id.user_email);
                final String to_email = emailText.getText().toString().trim();

                if (to_email.isEmpty()) {
                    String error = to_email + "עדיין לא בחרת משתמש ...";
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    return;
                }

                if (to_email.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                    String error = "לא ניתן לשלוח הזמנה לעצמך ...";
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    return;
                }

                Query requestQuery = FirebaseFirestore.getInstance().collection("Users").whereEqualTo("email", to_email);
                requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                String error = "אופס! לא קיים משתמש עם המייל " + to_email;
                                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                addNotificationInviteToRead(to_email, book_title);
                            }
                        }
                    }
                });
            }
        });
    }

    private void addNotificationInviteToRead(String to_email, String book_title) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if ((!(to_email.equals(mAuth.getCurrentUser().getEmail())))) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> notificationMessegae = new HashMap<>();

            notificationMessegae.put("type", getActivity().getResources().getString(R.string.invite_notificiation));
            notificationMessegae.put("from", mAuth.getCurrentUser().getEmail());
            notificationMessegae.put("book_title", book_title);
            notificationMessegae.put("time", Timestamp.now());


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


    private void initShareButtons(View view) {
        view.findViewById(R.id.facebook).setOnClickListener(new ShareOutsideAppOnClick("com.facebook.orca"));
        view.findViewById(R.id.instagram).setOnClickListener(new ShareOutsideAppOnClick("com.instagram.android"));
        view.findViewById(R.id.whatsapp).setOnClickListener(new ShareOutsideAppOnClick("com.whatsapp"));
        view.findViewById(R.id.email).setOnClickListener(new ShareOutsideAppOnClick("email"));
    }

    private class ShareOutsideAppOnClick implements View.OnClickListener {

        private String intentPackage;

        public ShareOutsideAppOnClick(String intentPackage) {
            this.intentPackage = intentPackage;
        }

        @Override
        public void onClick(View v) {

            if (intentPackage.equals("email")) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setType("text/plain");
                String shareBody = "כדאי לך לנסות את הספר " + book_title;
                String shareSubject = "אני רוצה להמליץ לך על ספר שווה קריאה";
                emailIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                startActivity(Intent.createChooser(emailIntent, "שלח דואר אלקטרוני"));
            } else {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.setPackage(intentPackage);
                String shareBody = "כדאי לך לנסות את הספר " + book_title;
                String shareSubject = "אני רוצה להמליץ לך על ספר שווה קריאה";
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                startActivity(sharingIntent);
            }
        }
    }

}
