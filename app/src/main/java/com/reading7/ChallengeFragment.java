package com.reading7;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Objects.User;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import nl.dionsegijn.konfetti.KonfettiView;

import static nl.dionsegijn.konfetti.models.Shape.CIRCLE;
import static nl.dionsegijn.konfetti.models.Shape.RECT;

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

    private CountDownTimer countDownTimer;
    private static int CHALLENGE_TIME_IN_SECONDS = 30;
    private boolean isDone = false; // true iff times up or answer was chosen


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

        ((TextView) getView().findViewById(R.id.book_name_title)).setText(getString(R.string.question_about_book) + " \"" + book_title + "\"");
        ((TextView) getView().findViewById(R.id.question)).setText(question_content);

        initTimer();
        initDismissButtons();
        initSendAnswerButton();

        choice1 = getView().findViewById(R.id.choice1);
        choice1.setText(possible_answers.get(0));
        choice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_answer = choice1.getText().toString();
                checkSelected(choice1);
            }
        });

        choice2 = getView().findViewById(R.id.choice2);
        choice2.setText(possible_answers.get(1));
        choice2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_answer = choice2.getText().toString();
                checkSelected(choice2);
            }
        });

        choice3 = getView().findViewById(R.id.choice3);
        choice3.setText(possible_answers.get(2));
        choice3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_answer = choice3.getText().toString();
                checkSelected(choice3);
            }
        });

        choice4 = getView().findViewById(R.id.choice4);
        choice4.setText(possible_answers.get(3));
        choice4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_answer = choice4.getText().toString();
                checkSelected(choice4);
            }
        });
    }


    private void sendResult(int REQUEST_CODE, Timestamp time) {
        Intent intent = new Intent();
        intent.putExtra("time", time.getSeconds() + "," + time.getNanoseconds());

        if (getTargetFragment() != null)
            getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_CODE, intent);
    }

    private void updateNotification(final Utils.ChallengeState state) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference requestsRef = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("Notifications");
        Query requestQuery = requestsRef.whereEqualTo("time", notification_time);
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().update("challengeState", state).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                sendResult(808, notification_time);
                            }
                        });
                    }
                }
            }
        });
    }


    private void initTimer() {

        final ProgressBar progressBar = getView().findViewById(R.id.progressbar);
        progressBar.setMax(CHALLENGE_TIME_IN_SECONDS * 1000);

        countDownTimer = new CountDownTimer(CHALLENGE_TIME_IN_SECONDS * 1000, 10) {

            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress((int) (CHALLENGE_TIME_IN_SECONDS * 1000 - millisUntilFinished));
                ((TextView) getView().findViewById(R.id.clock)).setText(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1));
            }

            @Override
            public void onFinish() {
                isDone = true;
                progressBar.setProgress(CHALLENGE_TIME_IN_SECONDS * 1000);
                ((TextView) getView().findViewById(R.id.clock)).setText(String.valueOf(0));
                startTimesUpAnimations();
                updateNotification(Utils.ChallengeState.OUT_OF_TIME);
            }
        };
        countDownTimer.start();
    }

    private void initSendAnswerButton() {
        Button send_answer = getView().findViewById(R.id.send_answer);
        send_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                countDownTimer.cancel();
                isDone = true;

                final Utils.ChallengeState state;
                if (selected_answer.equals(right_answer)) {
                    state = Utils.ChallengeState.RIGHT;
                    updateCorrectLayoutDetails();
                    startCorrectAnimations();
                    User user = ((MainActivity) getActivity()).getCurrentUser();
                    user.addPoints(calculatePoints()); //Add points for answering correctly the question
                    FirebaseFirestore.getInstance().collection("Users").document(user.getEmail()).update("points", user.getPoints());
                    ((MainActivity) getActivity()).setCurrentUser(user);
                } else {
                    state = Utils.ChallengeState.WRONG;
                    startWrongAnimations();
                }

                updateNotification(state);
            }
        });
    }

    private void initDismissButtons() {
        getView().findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateDialogOut();
            }
        });
        getView().findViewById(R.id.dismiss2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateDialogOut();
            }
        });
        getView().findViewById(R.id.dismiss3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateDialogOut();
            }
        });
    }


    private void checkSelected(TextView checked) {
        if (!(selected_answer.isEmpty())) {

            checked.setBackgroundTintList(ColorStateList.valueOf(Utils.getColor(getContext(), "colorPrimary")));
            checked.setTextColor(Utils.getColor(getContext(), "white"));

            if (choice1 != checked) {
                choice1.setBackgroundTintList(null);
                choice1.setTextColor(Utils.getColor(getContext(), "black"));
            }

            if (choice2 != checked) {
                choice2.setBackgroundTintList(null);
                choice2.setTextColor(Utils.getColor(getContext(), "black"));
            }

            if (choice3 != checked) {
                choice3.setBackgroundTintList(null);
                choice3.setTextColor(Utils.getColor(getContext(), "black"));
            }

            if (choice4 != checked) {
                choice4.setBackgroundTintList(null);
                choice4.setTextColor(Utils.getColor(getContext(), "black"));
            }
        }
    }

    private TextView getRightAnswerTextView() {
        if (choice1.getText().toString().equals(right_answer))
            return choice1;
        if (choice2.getText().toString().equals(right_answer))
            return choice2;
        if (choice3.getText().toString().equals(right_answer))
            return choice3;
        if (choice4.getText().toString().equals(right_answer))
            return choice4;

        return null;
    }

    private TextView getSelectedAnswerTextView() {
        if (choice1.getText().toString().equals(selected_answer))
            return choice1;
        if (choice2.getText().toString().equals(selected_answer))
            return choice2;
        if (choice3.getText().toString().equals(selected_answer))
            return choice3;
        if (choice4.getText().toString().equals(selected_answer))
            return choice4;

        return null;
    }

    private void updateCorrectLayoutDetails() {
        String points = String.valueOf(calculatePoints());
        ((TextView) getView().findViewById(R.id.points)).setText(points);

        String congrats = getString(R.string.good_job) + ", " + ((MainActivity) getActivity()).getCurrentUser().getFull_name() + "!";
        ((TextView) getView().findViewById(R.id.congrats)).setText(congrats);
    }

    private int calculatePoints() {
        int secondsLeft = Integer.parseInt(((TextView) getView().findViewById(R.id.clock)).getText().toString());
        return 50 + secondsLeft;
    }

    public boolean isDone() {
        return isDone;
    }


// ======================================= Animations =========================================== //

    private void startCorrectAnimations() {

        Animator scale = ObjectAnimator.ofPropertyValuesHolder(getRightAnswerTextView(),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 0.9f, 1f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0.9f, 1f)
        );
        scale.setInterpolator(new OvershootInterpolator());
        scale.setDuration(600);

        scale.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                int delay = 100;
                for (TextView answer : new TextView[]{choice1, choice2, choice3, choice4}) {

                    final Animator move = ObjectAnimator.ofPropertyValuesHolder(answer,
                            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0, 1500f));
                    move.setInterpolator(new AnticipateInterpolator());
                    move.setDuration(1000);

                    if (answer != getRightAnswerTextView()) {
                        move.setStartDelay(delay);
                        delay += 100;
                    }
                    move.start();
                }

                animateTitles();
                animateCorrectLayout();
                animateConfetti();
                animateCoinRotation();
                animateTimer();
                animateSendAnswerButton();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        scale.start();
        getRightAnswerTextView().setBackgroundTintList(ColorStateList.valueOf(Utils.getColor(getContext(), "green")));

    }

    private void startWrongAnimations() {

        getSelectedAnswerTextView().setBackgroundTintList(ColorStateList.valueOf(Utils.getColor(getContext(), "red")));

        Animator shake = ObjectAnimator.ofPropertyValuesHolder(getSelectedAnswerTextView(),
                PropertyValuesHolder.ofFloat(View.ROTATION, 0, 1, -1, 1, -1, 1, -1, 0),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 0.95f, 1f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0.95f, 1f))
                .setDuration(600);

        final Animator scale = ObjectAnimator.ofPropertyValuesHolder(getRightAnswerTextView(),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 1.1f, 1f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 1.1f, 1f))
                .setDuration(600);

        scale.setInterpolator(new OvershootInterpolator());
        scale.setStartDelay(500);

        shake.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                scale.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        scale.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                getRightAnswerTextView().setTextColor(Utils.getColor(getContext(), "white"));
                getRightAnswerTextView().setBackgroundTintList(ColorStateList.valueOf(Utils.getColor(getContext(), "green")));
            }

            @Override
            public void onAnimationEnd(Animator animator) {

                int delay = 100;
                for (TextView answer : new TextView[]{choice1, choice2, choice3, choice4}) {

                    final Animator move = ObjectAnimator.ofPropertyValuesHolder(answer,
                            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0, 1500f));
                    move.setInterpolator(new AnticipateInterpolator());
                    move.setDuration(1000);

                    if (answer != getRightAnswerTextView()) {
                        move.setStartDelay(delay);
                        delay += 100;
                    }
                    move.start();
                }

                animateTitles();
                animateWrongLayout();
                animateTimer();
                animateSendAnswerButton();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        shake.start();
    }

    private void startTimesUpAnimations() {

        animateTimesUpTimer();
        ((Button) getView().findViewById(R.id.send_answer)).setEnabled(false);
        ((Button) getView().findViewById(R.id.send_answer)).setBackgroundTintList(ColorStateList.valueOf(Utils.getColor(getContext(), "lightGrey")));

        ((TextView) getView().findViewById(R.id.question)).setTextColor(Utils.getColor(getContext(), "lightGrey"));

        int delay = 1200;
        Animator move = null;
        for (TextView answer : new TextView[]{choice1, choice2, choice3, choice4}) {

            answer.setEnabled(false);
            answer.setTextColor(Utils.getColor(getContext(), "lightGrey"));
            answer.setBackgroundTintList(null);

            move = ObjectAnimator.ofPropertyValuesHolder(answer,
                    PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0, 1500f))
                    .setDuration(1000);
            move.setInterpolator(new AnticipateInterpolator());
            move.setStartDelay(delay);
            delay += 100;

            move.start();
        }

        move.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                animateTitles();
                animateTimer();
                animateSendAnswerButton();
                animateTimesUpLayout();
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void animateCorrectLayout() {
        RelativeLayout correctLayout = getView().findViewById(R.id.correctLayout);
        correctLayout.setVisibility(View.VISIBLE);
        Animator scale_in = ObjectAnimator.ofPropertyValuesHolder(correctLayout,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 0, 1),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0, 1),
                PropertyValuesHolder.ofFloat(View.ALPHA, 1)
        );
        scale_in.setInterpolator(new OvershootInterpolator());
        scale_in.setDuration(500);
        scale_in.setStartDelay(1000);
        scale_in.start();
    }

    private void animateWrongLayout() {
        RelativeLayout wrongLayout = getView().findViewById(R.id.wrongLayout);
        wrongLayout.setVisibility(View.VISIBLE);
        Animator scale_in = ObjectAnimator.ofPropertyValuesHolder(wrongLayout,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 0, 1),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0, 1),
                PropertyValuesHolder.ofFloat(View.ALPHA, 1)
        );
        scale_in.setInterpolator(new OvershootInterpolator());
        scale_in.setDuration(500);
        scale_in.setStartDelay(1000);
        scale_in.start();
    }

    private void animateTimesUpLayout() {
        RelativeLayout wrongLayout = getView().findViewById(R.id.timesUpLayout);
        wrongLayout.setVisibility(View.VISIBLE);
        Animator scale_in = ObjectAnimator.ofPropertyValuesHolder(wrongLayout,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 0, 1),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0, 1),
                PropertyValuesHolder.ofFloat(View.ALPHA, 1)
        );
        scale_in.setInterpolator(new OvershootInterpolator());
        scale_in.setDuration(500);
        scale_in.setStartDelay(1000);
        scale_in.start();
    }

    private void animateSendAnswerButton() {
        Button send_answer = getView().findViewById(R.id.send_answer);
        Animator scale_out = ObjectAnimator.ofPropertyValuesHolder(send_answer,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 0),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0));
        scale_out.setDuration(300);
        scale_out.setStartDelay(500);
        scale_out.start();
    }

    private void animateTitles() {
        LinearLayout bookDetails = getView().findViewById(R.id.book_details);
        View divider = getView().findViewById(R.id.divider21);
        TextView question = getView().findViewById(R.id.question);

        ObjectAnimator
                .ofPropertyValuesHolder(divider, PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 0))
                .setDuration(300)
                .start();
        ObjectAnimator.ofPropertyValuesHolder(bookDetails,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 0),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0, -10f))
                .setDuration(300)
                .start();
        ObjectAnimator.ofPropertyValuesHolder(question,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 0),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0, 10f))
                .setDuration(300)
                .start();
    }

    private void animateConfetti() {
        KonfettiView confetti = getView().findViewById(R.id.viewConfetti);
        confetti.build()
                .addColors(Utils.getColor(getContext(), "colorPrimaryDark"),
                        Utils.getColor(getContext(), "colorPrimary"),
                        Utils.getColor(getContext(), "colorAccent"))
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(RECT, CIRCLE)
                .setPosition(-50f, confetti.getWidth() + 50f, -50f, -50f)
                .streamFor(70, 1000L);
    }

    private void animateCoinRotation() {
        ImageView coin = getView().findViewById(R.id.coin);

        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(5000);
        rotate.setRepeatCount(3);
        rotate.setRepeatMode(Animation.RESTART);
        rotate.setInterpolator(new LinearInterpolator());
        coin.startAnimation(rotate);
    }

    private void animateTimer() {
        RelativeLayout timer_layout = getView().findViewById(R.id.timer_layout);

        ObjectAnimator
                .ofPropertyValuesHolder(timer_layout,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 0),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0))
                .setDuration(300)
                .start();
    }

    private void animateDialogOut() {
        RelativeLayout backLayout = getView().findViewById(R.id.backLayout);
        RelativeLayout layout = getView().findViewById(R.id.layout);
        RelativeLayout correctLayout = getView().findViewById(R.id.correctLayout);
        RelativeLayout wrongLayout = getView().findViewById(R.id.wrongLayout);


        final Animator scale_out_correct = ObjectAnimator
                .ofPropertyValuesHolder(correctLayout,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 0),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0))
                .setDuration(300);

        final Animator scale_out_wrong = ObjectAnimator
                .ofPropertyValuesHolder(wrongLayout,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 0),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0))
                .setDuration(300);

        final Animator scale_out_background = ObjectAnimator
                .ofPropertyValuesHolder(layout,
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0))
                .setDuration(300);

        final Animator fade_out = ObjectAnimator
                .ofPropertyValuesHolder(backLayout,
                        PropertyValuesHolder.ofFloat(View.ALPHA, 1, 0))
                .setDuration(300);

        scale_out_background.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                scale_out_correct.start();
                scale_out_wrong.start();
                fade_out.start();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                getActivity().onBackPressed();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        scale_out_background.start();
    }

    private void animateTimesUpTimer() {
        ProgressBar progressBar = getView().findViewById(R.id.progressbar);
        progressBar.setProgressTintList(ColorStateList.valueOf(Utils.getColor(getContext(), "red")));

        RelativeLayout timer_layout = getView().findViewById(R.id.timer_layout);
        Animator shake = ObjectAnimator.ofPropertyValuesHolder(timer_layout,
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0, 10, -10, 10, -10, 10, -10, 0),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 0.95f, 1f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0.95f, 1f))
                .setDuration(600);

        shake.start();
    }

// ============================================================================================== //

}


