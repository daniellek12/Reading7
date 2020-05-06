package com.reading7;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Objects.Notification;
import com.reading7.Objects.User;

import java.util.ArrayList;

public class ChallengeFragment extends Fragment {

    private String book_title;
    private String question_content;
    private ArrayList<String> possible_answers;
    private String right_answer;
    private String selected_answer = "";
    private Timestamp notification_time;

    private TextView choice1;
    private TextView choice2;
    private TextView choice3;
    private TextView choice4;


    public ChallengeFragment(String book_title, String question_content, ArrayList<String> possible_answers, String right_answer, Timestamp notification_time) {
        this.book_title = book_title;
        this.question_content = question_content;
        this.possible_answers = possible_answers;
        this.right_answer = right_answer;
        this.notification_time = notification_time;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.challenge_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((TextView) getView().findViewById(R.id.book_name_title)).setText(book_title);

        ((TextView) getView().findViewById(R.id.question)).setText(question_content);

        choice1 = (TextView) getView().findViewById(R.id.choice1);
        choice1.setText(possible_answers.get(0));
        choice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkSelected();
                choice1.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                selected_answer = choice1.getText().toString();
            }
        });

        choice2 = (TextView) getView().findViewById(R.id.choice2);
        choice2.setText(possible_answers.get(1));
        choice2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkSelected();
                choice2.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                selected_answer = choice2.getText().toString();
            }
        });

        choice3 = (TextView) getView().findViewById(R.id.choice3);
        choice3.setText(possible_answers.get(2));
        choice3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkSelected();
                choice3.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                selected_answer = choice3.getText().toString();
            }
        });

        choice4 = (TextView) getView().findViewById(R.id.choice4);
        choice4.setText(possible_answers.get(3));
        choice4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkSelected();
                choice4.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                selected_answer = choice4.getText().toString();
            }
        });

        FloatingActionButton send_answer = getView().findViewById(R.id.send_answer);
        send_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Utils.ChallengeState ans;
                if (selected_answer.equals(right_answer)) {
                    ans = Utils.ChallengeState.Right;
                    String msg = "תשובה נכונה! זכית ב50 נקודות ";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                    User user = ((MainActivity) getActivity()).getCurrentUser();
                    user.addPoints(50); //Add points for answering correctly the question
                    FirebaseFirestore.getInstance().collection("Users").document(user.getEmail()).update("points", user.getPoints());
                } else {
                    ans = Utils.ChallengeState.Wrong;
                    String msg = "תשובה לא נכונה! אולי בפעם הבאה ";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                }
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("Notifications").document(id).update("")
                CollectionReference requestsRef = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("Notifications");
                Query requestQuery = requestsRef.whereEqualTo("time", notification_time);
                requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // if (!document.toObject(Notification.class).getType().equals(getString(R.string.like_notificiation)))
                                document.getReference().update("challengeState", ans).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        getActivity().onBackPressed();
                                        sendResult(808, notification_time);
                                        //getActivity().onBackPressed();
                                        //((MainActivity) getActivity()).addFragment(new NotificationsFragment());

                                    }
                                });
                            }
                        }
                    }
                });
                                    }});
                    }





    public void checkSelected() {
        if (!(selected_answer.isEmpty())) {
            if (selected_answer.equals(possible_answers.get(0)))
                choice1.setBackground(getActivity().getResources().getDrawable(R.drawable.edittext_border_background));
            if (selected_answer.equals(possible_answers.get(1)))
                choice2.setBackground(getActivity().getResources().getDrawable(R.drawable.edittext_border_background));
            if (selected_answer.equals(possible_answers.get(2)))
                choice3.setBackground(getActivity().getResources().getDrawable(R.drawable.edittext_border_background));
            if (selected_answer.equals(possible_answers.get(3)))
                choice4.setBackground(getActivity().getResources().getDrawable(R.drawable.edittext_border_background));
        }
    }


    public void sendResult(int REQUEST_CODE, Timestamp time) {
        Intent intent = new Intent();
        intent.putExtra("time", time.getSeconds()+","+time.getNanoseconds());

        if (getTargetFragment() != null)
            getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_CODE, intent);
    }
}


