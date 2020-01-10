package com.reading7;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.reading7.Adapters.TabsPagerAdapter;
import com.reading7.Objects.User;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ViewPager viewPager;
    private User user;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        mAuth = FirebaseAuth.getInstance();
        initTabs();

        ImageButton backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpActivity.this.onBackPressed();
            }
        });

        initSignUpButton();
        initPreviousButton();
    }

    private void initTabs() {

        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        tabsPagerAdapter.addFragment(new SignUpStep1(), null);
        tabsPagerAdapter.addFragment(new SignUpStep2(), null);
        tabsPagerAdapter.addFragment(new SignUpStep3(), null);

        viewPager = findViewById(R.id.signup_ViewPager);
        viewPager.setAdapter(tabsPagerAdapter);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        TabLayout tabs = findViewById(R.id.signup_Tabs);
        tabs.setupWithViewPager(viewPager, true);

        LinearLayout tabStrip = ((LinearLayout) tabs.getChildAt(0));
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void initSignUpButton() {
        final Button signup = findViewById(R.id.signup_btn);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment frag = ((TabsPagerAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
                switch (viewPager.getCurrentItem()) {

                    case 0:
                        ((SignUpStep1) frag).dissapearErrorMsgs();
                        user = ((SignUpStep1) frag).getUser();
                        if (user != null) {
                            password = ((SignUpStep1) frag).getPassword();
                            setFragmnet(1);
                        }
                        break;

                    case 1:
                        user.setFavourite_books(((SignUpStep2) frag).getFavouriteBookID());
                        setFragmnet(2);
                        break;

                    case 2:
                        user.setFavourite_genres(((SignUpStep3) frag).getFavourite_genres());
                        signUpUser(user);
                        break;
                }
            }
        });
    }

    private void initPreviousButton() {

        final Button previous = findViewById(R.id.previousButton);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment frag = ((TabsPagerAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
                switch (viewPager.getCurrentItem()) {

                    case 1:
                        user.setFavourite_books(((SignUpStep2) frag).getFavouriteBookID());
                        setFragmnet(0);
                        break;

                    case 2:
                        user.setFavourite_genres(((SignUpStep3) frag).getFavourite_genres());
                        setFragmnet(1);
                        break;
                }
            }
        });


    }

    private void signUpUser(final User user) {

        if (user == null) return;

        showProgressBar();

        mAuth.createUserWithEmailAndPassword(user.getEmail(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    String token_id = FirebaseInstanceId.getInstance().getToken();
                    user.setToken_id(token_id);
                    DocumentReference newUser = FirebaseFirestore.getInstance().collection("Users").document(user.getEmail());
                    newUser.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.putExtra("NEW_USER", true);
                                startActivity(intent);
                                finish();
                            } else
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {

                    if (task.getException().getMessage().equals("The email address is already in use by another account.")) {
                        hideProgressBar();
                        findViewById(R.id.email_exists).setVisibility(View.VISIBLE);
                        setFragmnet(0);
                    } else {
                        Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        hideProgressBar();
                    }
                }
            }
        });

    }


    private void setFragmnet(int position){
        switch (position){
            case 0:
                findViewById(R.id.previousButton).setVisibility(View.GONE);
                ((Button) findViewById(R.id.signup_btn)).setText(getString(R.string.cont));
                viewPager.setCurrentItem(0);
                break;

            case 1:
                findViewById(R.id.previousButton).setVisibility(View.VISIBLE);
                ((Button) findViewById(R.id.signup_btn)).setText(getString(R.string.cont));
                viewPager.setCurrentItem(1);
                break;

            case 2:
                findViewById(R.id.previousButton).setVisibility(View.VISIBLE);
                ((Button) findViewById(R.id.signup_btn)).setText(getString(R.string.signup));
                viewPager.setCurrentItem(2);
                break;


        }
    }

    private void disableClicks() {
        findViewById(R.id.backButton).setEnabled(false);
        findViewById(R.id.signup_btn).setEnabled(false);
    }

    private void enableClicks() {
        findViewById(R.id.backButton).setEnabled(true);
        findViewById(R.id.signup_btn).setEnabled(true);
    }

    private void showProgressBar() {
        disableClicks();
        findViewById(R.id.signup_btn).setVisibility(View.GONE);
        findViewById(R.id.previousButton).setVisibility(View.GONE);
        findViewById(R.id.progress_background).setVisibility(View.VISIBLE);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        findViewById(R.id.progress_background).setVisibility(View.GONE);
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        findViewById(R.id.signup_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.previousButton).setVisibility(View.VISIBLE);
        enableClicks();
    }

//    @Override
//    public void getAvatarDetails(ArrayList<Integer> details) {
//        Fragment frag = ((TabsPagerAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
//        ((SignUpStep1) frag).getAvatarDetails(details);
//    }
}