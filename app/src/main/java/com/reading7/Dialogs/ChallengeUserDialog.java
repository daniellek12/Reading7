package com.reading7.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import static java.lang.Integer.parseInt;

public class ChallengeUserDialog extends AppCompatDialogFragment {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private View view;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        final String book_title = getArguments().getString("book_title");

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        view = getActivity().getLayoutInflater().inflate(R.layout.challenge_user_dialog, null);
        final EditText emailText = view.findViewById(R.id.user_email);
        final EditText questionContentText = view.findViewById(R.id.question_content);
        final EditText possible_answer1 = view.findViewById(R.id.possible_answer1);
        final EditText possible_answer2 = view.findViewById(R.id.possible_answer2);
        final EditText possible_answer3 = view.findViewById(R.id.possible_answer3);
        final EditText possible_answer4 = view.findViewById(R.id.possible_answer4);
        final EditText right_answer_txt = view.findViewById(R.id.right_answer);
        final ArrayList<String> possibleAnswers = new ArrayList<>();


        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String to_email = emailText.getText().toString().trim();
                String ans1 = possible_answer1.getText().toString().trim();
                String ans2 = possible_answer2.getText().toString().trim();
                String ans3 = possible_answer3.getText().toString().trim();
                String ans4 = possible_answer4.getText().toString().trim();
                possibleAnswers.add(ans1);
                possibleAnswers.add(ans2);
                possibleAnswers.add(ans3);
                possibleAnswers.add(ans4);


                final String questionContent = questionContentText.getText().toString().trim();

                if (to_email.isEmpty()) {
                    String error = to_email + "עדיין לא בחרת משתמש ...";
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    return;
                }
                if (to_email.equals(mAuth.getCurrentUser().getEmail())) {
                    String error = "לא ניתן לשלוח אתגר לעצמך ...";
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    return;

                }

                if (ans1.isEmpty() && ans2.isEmpty() && ans3.isEmpty() && ans4.isEmpty()) {
                    String error = "לא ניתן לשלוח אתגר ללא תשובות אפשריות ...";
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    return;

                }


                if (questionContent.isEmpty()) {
                    String error = "לא ניתן לשלוח אתגר ללא שאלה ...";
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    return;

                }

                String right_answer_trimed = right_answer_txt.getText().toString().trim();
                if (right_answer_trimed.isEmpty()) {
                    String error = "לא ניתן לשלוח אתגר ללא תשובה נכונה ...";
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    return;

                } else {
                    try {
                        int x = Integer.parseInt(right_answer_trimed);
                        if (x < 1 || x > 4) {
                            String error = "לא ניתן לשלוח אתגר עם תשובה נכונה לא תקנית ...";
                            Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                            return;
                        }

                    } catch (NumberFormatException e) {
                        String error = "לא ניתן לשלוח אתגר ללא תשובה נכונה ...";
                        Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                final String right_answer = possibleAnswers.get(parseInt(right_answer_trimed) - 1);

                Query requestQuery = db.collection("Users").whereEqualTo("email", to_email);
                requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                String error = "אופס! לא קיים משתמש עם המייל " + to_email;
                                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                                return;
                            } else {

                                addNotificationChallengeUser(to_email, book_title, questionContent,
                                        possibleAnswers, right_answer);
                            }
                        }
                    }
                });


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

    private void addNotificationChallengeUser(String to_email, String book_title, String question_content, ArrayList<String> possible_answers, String right_answer) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if ((!(to_email.equals(mAuth.getCurrentUser().getEmail())))) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> notificationMessegae = new HashMap<>();

            notificationMessegae.put("type", getActivity().getResources().getString(R.string.challenge_notificiation));
            notificationMessegae.put("from", mAuth.getCurrentUser().getEmail());
            notificationMessegae.put("book_title", book_title);
            notificationMessegae.put("time", Timestamp.now());
            notificationMessegae.put("question_content", question_content);
            notificationMessegae.put("possible_answers", possible_answers);
            notificationMessegae.put("right_answer", right_answer);

            db.collection("Users/" + to_email + "/Notifications").add(notificationMessegae).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    //String m = " ההזמנה נשלחה למייל" + to_email;

                    Toast.makeText(getContext(), "האתגר נשלח ", Toast.LENGTH_LONG).show();

                    dismiss();

                }
            });
        }
    }


}
