package com.reading7;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Utils.isAdmin = false;

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && !currentUser.getEmail().equals(getResources().getString(R.string.admin_email))) // FIXME: redirect when admin too?
            redirectAgain();

        setUpLoginBtn();
        setUpSignupBtn();
    }

    protected void redirectAgain() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    public void redirectToMain() {
        /*final Intent intent = new Intent(this, MainActivity.class);
        CollectionReference requestCollectionRef = db.collection("Users");
        Query requestQuery = requestCollectionRef.whereEqualTo("email", mAuth.getCurrentUser().getEmail());
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    User user = new User();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        user = document.toObject(User.class);
                    }

                    editTokenId();
                    startActivity(intent);
                    finish();
                }

            }
        });*/
        Intent intent = new Intent(this, MainActivity.class);
        editTokenId();
        startActivity(intent);
        finish();

    }

    private boolean checkEnteredDetails(String email, String password) {

        if (email.equals("") || password.equals("")) {
            findViewById(R.id.enter_details).setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }


    private void setUpLoginBtn() {
        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideErrorMessages();
                showProgressBar();

                String email = ((EditText) findViewById(R.id.email)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();

                if (checkEnteredDetails(email, password)) {

                    if (isAdminDetails(email)) {
                        loginAdmin(email, password);
                        return;
                    }

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                redirectToMain();
                            } else {
                                hideProgressBar();
                                showErrorMessage(task.getException());
                            }
                        }
                    });
                } else hideProgressBar();
            }
        });
    }

    private void setUpSignupBtn() {

        Button signup = findViewById(R.id.signup_btn);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }


    private boolean isAdminDetails(String email) {
        return email.equals(getString(R.string.admin_email));
    }

    private void loginAdmin(String email, String password) {

        if (!isAdminDetails(email))
            throw new AssertionError("Wrong Admin Details");

        mAuth.signInWithEmailAndPassword(getResources().getString(R.string.admin_email), password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Utils.isAdmin = true;
                            redirectToMain();
                        } else {
                            findViewById(R.id.wrong_details).setVisibility(View.VISIBLE);
                            hideProgressBar();
                        }
                    }
                });
    }


    private void showErrorMessage(Exception exception) {
        switch (exception.getMessage()) {
            case "There is no user record corresponding to this identifier. The user may have been deleted.":
            case "The email address is badly formatted.":
            case "The password is invalid or the user does not have a password.":
                findViewById(R.id.wrong_details).setVisibility(View.VISIBLE);
                break;
            case "A network error (such as timeout, interrupted connection or unreachable host) has occurred.":
            case "An internal error has occurred. [7:]":
                findViewById(R.id.no_internet).setVisibility(View.VISIBLE);
                break;
            default:
                Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void hideErrorMessages() {
        findViewById(R.id.wrong_details).setVisibility(View.INVISIBLE);
        findViewById(R.id.no_internet).setVisibility(View.INVISIBLE);
        findViewById(R.id.already_logged).setVisibility(View.INVISIBLE);
        findViewById(R.id.enter_details).setVisibility(View.INVISIBLE);
    }


    private void showProgressBar() {
        Utils.enableDisableClicks(this, (ViewGroup) findViewById(android.R.id.content).getRootView(), false);
        findViewById(R.id.login_button).setVisibility(View.GONE);
        findViewById(R.id.progress_background).setVisibility(View.VISIBLE);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        Utils.enableDisableClicks(this, (ViewGroup) findViewById(android.R.id.content).getRootView(), true);
        findViewById(R.id.login_button).setVisibility(View.VISIBLE);
        findViewById(R.id.progress_background).setVisibility(View.GONE);
        findViewById(R.id.progressBar).setVisibility(View.GONE);
    }


    private void editTokenId() {

        String token_id = FirebaseInstanceId.getInstance().getToken();
        String user_email = mAuth.getCurrentUser().getEmail();

        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("token_id", token_id);
        db.collection("Users").document(user_email).update(tokenMap);

    }

}
