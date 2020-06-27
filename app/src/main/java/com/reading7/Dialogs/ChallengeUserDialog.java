package com.reading7.Dialogs;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.reading7.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ChallengeUserDialog extends AppCompatDialogFragment {

    private String book_title;
    private View dialogView;

    private EditText question_content;
    private EditText possible_answer1;
    private EditText possible_answer2;
    private EditText possible_answer3;
    private EditText possible_answer4;

    private String to_email;
    private ArrayList<String> possibleAnswers = new ArrayList<>();
    private String question;
    private String right_answer;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        book_title = getArguments().getString("book_title");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialogView = getActivity().getLayoutInflater().inflate(R.layout.challenge_user_dialog, null);
        question_content = dialogView.findViewById(R.id.question_content);
        possible_answer1 = dialogView.findViewById(R.id.possible_answer1);
        possible_answer2 = dialogView.findViewById(R.id.possible_answer2);
        possible_answer3 = dialogView.findViewById(R.id.possible_answer3);
        possible_answer4 = dialogView.findViewById(R.id.possible_answer4);

        builder.setView(dialogView);
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        initChooseUserLayout();
        initTextWatchers();
        initTouchAnimation();
        initOkButton();

        return dialog;
    }


// ====================================== ChooseUserLayout ====================================== //

    private void initChooseUserLayout() {

        dialogView.findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showProgressbar();
                hideErrorMessages();

                to_email = ((EditText) dialogView.findViewById(R.id.user_email)).getText().toString().trim();

                if (to_email.equals("")) {
                    dialogView.findViewById(R.id.enter_email).setVisibility(View.VISIBLE);
                    hideProgressbar();
                    return;
                }

                if (to_email.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                    dialogView.findViewById(R.id.cant_send_yourself).setVisibility(View.VISIBLE);
                    hideProgressbar();
                    return;
                }

                Query requestQuery = FirebaseFirestore.getInstance().collection("Users").whereEqualTo("email", to_email);
                requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                dialogView.findViewById(R.id.user_doesnt_exist).setVisibility(View.VISIBLE);
                                hideProgressbar();
                            } else {
                                showCreateChallengeLayout();
                            }
                        }
                    }
                });
            }
        });

    }

    private void showProgressbar() {
        dialogView.findViewById(R.id.continue_button).setVisibility(View.INVISIBLE);
        dialogView.findViewById(R.id.progressbar_layout).setVisibility(View.VISIBLE);
    }

    private void hideProgressbar() {
        dialogView.findViewById(R.id.continue_button).setVisibility(View.VISIBLE);
        dialogView.findViewById(R.id.progressbar_layout).setVisibility(View.INVISIBLE);
    }

    private void showCreateChallengeLayout() {
        dialogView.findViewById(R.id.select_user_layout).setVisibility(View.GONE);
        dialogView.findViewById(R.id.create_challenge_layout).setVisibility(View.VISIBLE);
        updateTitle(getString(R.string.creating_question_challenge), Utils.getDrawable(getContext(), "number_two"));
    }


// ==================================== CreateChallengeLayout =================================== //

    private void initTextWatchers() {

        final TextView questionCounter = dialogView.findViewById(R.id.question_counter);
        question_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int charsLeft = 100 - charSequence.length();
                questionCounter.setText(String.valueOf(charsLeft));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        question_content.setImeOptions(EditorInfo.IME_ACTION_DONE);
        question_content.setRawInputType(InputType.TYPE_CLASS_TEXT);

        final TextView answer1Counter = dialogView.findViewById(R.id.answer1_counter);
        possible_answer1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int charsLeft = 75 - charSequence.length();
                answer1Counter.setText(String.valueOf(charsLeft));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        possible_answer1.setImeOptions(EditorInfo.IME_ACTION_DONE);
        possible_answer1.setRawInputType(InputType.TYPE_CLASS_TEXT);

        final TextView answer2Counter = dialogView.findViewById(R.id.answer2_counter);
        possible_answer2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int charsLeft = 75 - charSequence.length();
                answer2Counter.setText(String.valueOf(charsLeft));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        possible_answer2.setImeOptions(EditorInfo.IME_ACTION_DONE);
        possible_answer2.setRawInputType(InputType.TYPE_CLASS_TEXT);

        final TextView answer3Counter = dialogView.findViewById(R.id.answer3_counter);
        possible_answer3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int charsLeft = 75 - charSequence.length();
                answer3Counter.setText(String.valueOf(charsLeft));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        possible_answer3.setImeOptions(EditorInfo.IME_ACTION_DONE);
        possible_answer3.setRawInputType(InputType.TYPE_CLASS_TEXT);

        final TextView answer4Counter = dialogView.findViewById(R.id.answer4_counter);
        possible_answer4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int charsLeft = 75 - charSequence.length();
                answer4Counter.setText(String.valueOf(charsLeft));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        possible_answer4.setImeOptions(EditorInfo.IME_ACTION_DONE);
        possible_answer4.setRawInputType(InputType.TYPE_CLASS_TEXT);
    }

    private void initTouchAnimation() {

        final Animator animator = ObjectAnimator.ofPropertyValuesHolder(possible_answer1,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 1.1f, 1),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 1.1f, 1))
                .setDuration(150);
        animator.setInterpolator(new LinearInterpolator());

        possible_answer1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if (focused) {
                    animator.setTarget(possible_answer1);
                    animator.start();
                }
            }
        });
        possible_answer2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if (focused) {
                    animator.setTarget(possible_answer2);
                    animator.start();
                }
            }
        });
        possible_answer3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if (focused) {
                    animator.setTarget(possible_answer3);
                    animator.start();
                }
            }
        });
        possible_answer4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if (focused) {
                    animator.setTarget(possible_answer4);
                    animator.start();
                }
            }
        });
        question_content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if (focused) {
                    animator.setTarget(question_content);
                    animator.start();
                }
            }
        });
    }

    private void initOkButton() {

        dialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideErrorMessages();

                getAnswersIntoArray();
                question = question_content.getText().toString().trim();

                if (question.isEmpty()) {
                    dialogView.findViewById(R.id.enter_question).setVisibility(View.VISIBLE);
                    return;
                }

                if (!allAnswersExist()) {
                    dialogView.findViewById(R.id.enter_possible_answers).setVisibility(View.VISIBLE);
                    return;
                }

                showSelectRightAnswerLayout();
            }
        });
    }

    private boolean allAnswersExist() {
        for (String answer : possibleAnswers) {
            if (answer.isEmpty())
                return false;
        }
        return true;
    }

    private void getAnswersIntoArray() {

        possibleAnswers.clear();
        possibleAnswers.add(possible_answer1.getText().toString().trim());
        possibleAnswers.add(possible_answer2.getText().toString().trim());
        possibleAnswers.add(possible_answer3.getText().toString().trim());
        possibleAnswers.add(possible_answer4.getText().toString().trim());
    }

    private void showSelectRightAnswerLayout() {

        dialogView.findViewById(R.id.answer1_counter).setVisibility(View.GONE);
        dialogView.findViewById(R.id.answer2_counter).setVisibility(View.GONE);
        dialogView.findViewById(R.id.answer3_counter).setVisibility(View.GONE);
        dialogView.findViewById(R.id.answer4_counter).setVisibility(View.GONE);
        dialogView.findViewById(R.id.question_counter).setVisibility(View.GONE);

        setupRightAnswerColoring();
        setupFinishButton();

        ObjectAnimator.ofPropertyValuesHolder(question_content,
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 1, -100))
                .setDuration(200)
                .start();

        ObjectAnimator.ofPropertyValuesHolder(dialogView.findViewById(R.id.answers1),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 1.1f, 1),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 1.1f, 1),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 1, -200))
                .setDuration(200)
                .start();

        ObjectAnimator.ofPropertyValuesHolder(dialogView.findViewById(R.id.answers2),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 1.1f, 1),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 1.1f, 1),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 1, -200))
                .setDuration(200)
                .start();

        ObjectAnimator.ofPropertyValuesHolder(dialogView.findViewById(R.id.ok),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 1, -200))
                .setDuration(200)
                .start();

        ObjectAnimator.ofPropertyValuesHolder(dialogView.findViewById(R.id.choose_right_answer),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 1, -200))
                .setDuration(200)
                .start();

        ValueAnimator va = ValueAnimator.ofInt(0, 200).setDuration(200);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                dialogView.findViewById(R.id.back).setBottom(dialogView.getBottom() - value);
            }
        });
        va.start();

        updateTitle(getString(R.string.choosing_right_answer), Utils.getDrawable(getContext(), "number_three"));
    }


// =================================== SelectRightAnswerLayout ================================== //

    private void setupFinishButton() {
        dialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideErrorMessages();

                if (right_answer == null) {
                    dialogView.findViewById(R.id.choose_right_answer).setVisibility(View.VISIBLE);
                    return;
                }

                addNotificationChallengeUser(to_email, book_title, question, possibleAnswers, right_answer);
                dismiss();
            }
        });
    }

    private void setupRightAnswerColoring() {

        possible_answer1.setFocusable(false);
        possible_answer2.setFocusable(false);
        possible_answer3.setFocusable(false);
        possible_answer4.setFocusable(false);

        possible_answer1.setClickable(true);
        possible_answer2.setClickable(true);
        possible_answer3.setClickable(true);
        possible_answer4.setClickable(true);

        possible_answer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                possible_answer1.setBackgroundTintList(ColorStateList.valueOf(Utils.getColor(getContext(), "green")));
                possible_answer2.setBackgroundTintList(null);
                possible_answer3.setBackgroundTintList(null);
                possible_answer4.setBackgroundTintList(null);
                right_answer = possible_answer1.getText().toString().trim();
            }
        });
        possible_answer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                possible_answer1.setBackgroundTintList(null);
                possible_answer2.setBackgroundTintList(ColorStateList.valueOf(Utils.getColor(getContext(), "green")));
                possible_answer3.setBackgroundTintList(null);
                possible_answer4.setBackgroundTintList(null);
                right_answer = possible_answer2.getText().toString().trim();
            }
        });
        possible_answer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                possible_answer1.setBackgroundTintList(null);
                possible_answer2.setBackgroundTintList(null);
                possible_answer3.setBackgroundTintList(ColorStateList.valueOf(Utils.getColor(getContext(), "green")));
                possible_answer4.setBackgroundTintList(null);
                right_answer = possible_answer3.getText().toString().trim();
            }
        });
        possible_answer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                possible_answer1.setBackgroundTintList(null);
                possible_answer2.setBackgroundTintList(null);
                possible_answer3.setBackgroundTintList(null);
                possible_answer4.setBackgroundTintList(ColorStateList.valueOf(Utils.getColor(getContext(), "green")));
                right_answer = possible_answer4.getText().toString().trim();
            }
        });
    }



    private void hideErrorMessages() {
        dialogView.findViewById(R.id.cant_send_yourself).setVisibility(View.INVISIBLE);
        dialogView.findViewById(R.id.enter_email).setVisibility(View.INVISIBLE);
        dialogView.findViewById(R.id.user_doesnt_exist).setVisibility(View.INVISIBLE);
        dialogView.findViewById(R.id.enter_possible_answers).setVisibility(View.INVISIBLE);
        dialogView.findViewById(R.id.enter_question).setVisibility(View.INVISIBLE);
        dialogView.findViewById(R.id.choose_right_answer).setVisibility(View.INVISIBLE);
    }

    private void updateTitle(String title, Drawable number) {
        ((ImageView) dialogView.findViewById(R.id.number)).setImageDrawable(number);
        ((TextView) dialogView.findViewById(R.id.title)).setText(title);
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
                    dismiss();
                }
            });
        }
    }

}
