package com.reading7;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.reading7.Dialogs.AdminCredentialsDialog;
import com.reading7.Dialogs.EditAvatarDialog;
import com.reading7.Objects.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

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

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null && !currentUser.getEmail().equals("admin@admin.com")) //fixme: redirect when admin too?
            redirectAgain();

        setUpLoginBtn();
        setUpSignupBtn();
        setAdminBtn();

        /*try {
            Utils.convertTxtToBook(this);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private void setAdminBtn() {
        Button admin_button = findViewById(R.id.admin_button);
        admin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdminCredentialsDialog dialog = new AdminCredentialsDialog();
                dialog.show(LoginActivity.this.getSupportFragmentManager(), "admin credentials");
//                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
//                startActivity(intent);
//                finish();
            }
        });
    }

    protected void redirectAgain() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    public void redirectToMain(){
        final Intent intent = new Intent(this, MainActivity.class);
        CollectionReference requestCollectionRef = db.collection("Users");
        Query requestQuery = requestCollectionRef.whereEqualTo("email",mAuth.getCurrentUser().getEmail());
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    User user = new User();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        user = document.toObject(User.class);
                    }

                    editTokenId();
                    startActivity(intent);
                        finish();
                }

            }
        });

    }

    private boolean checkEnteredDetails(String email, String password){

        if(email.equals("")) {
//            findViewById(R.id.mail_icon).startAnimation(shake);
//
//            if(password.equals(""))
//                findViewById(R.id.password_icon).startAnimation(shake);

            findViewById(R.id.enter_details).setVisibility(View.VISIBLE);
            return false;
        }

        if(password.equals("")) {
            //findViewById(R.id.password_icon).startAnimation(shake);
            findViewById(R.id.enter_details).setVisibility(View.VISIBLE);
            return false;
        }

        return true;
    }


    private void setUpLoginBtn(){
        Button login = findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findViewById(R.id.wrong_details).setVisibility(View.INVISIBLE);
                findViewById(R.id.no_internet).setVisibility(View.INVISIBLE);
                findViewById(R.id.already_logged).setVisibility(View.INVISIBLE);
                findViewById(R.id.enter_details).setVisibility(View.INVISIBLE);

                showProgressBar();

                String email = ((EditText)findViewById(R.id.email)).getText().toString();
                String password = ((EditText)findViewById(R.id.password)).getText().toString();

                if(checkEnteredDetails(email,password)) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                redirectToMain();
                            }

                            else {
                                hideProgressBar();

                                if(task.getException().getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")
                                        || task.getException().getMessage().equals("The email address is badly formatted.")
                                        || task.getException().getMessage().equals("The password is invalid or the user does not have a password."))
                                    findViewById(R.id.wrong_details).setVisibility(View.VISIBLE);

                                else if(task.getException().getMessage().equals("A network error (such as timeout, interrupted connection or unreachable host) has occurred.")
                                        || task.getException().getMessage().equals("An internal error has occurred. [7:]"))
                                    findViewById(R.id.no_internet).setVisibility(View.VISIBLE);


                                else Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                else hideProgressBar();

            }
        });
    }




    private void setUpSignupBtn(){

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


    private void disableClicks(){

        findViewById(R.id.email).setEnabled(false);
        findViewById(R.id.password).setEnabled(false);
        findViewById(R.id.signup_btn).setEnabled(false);
    }

    private void enableClicks(){

        findViewById(R.id.email).setEnabled(true);
        findViewById(R.id.password).setEnabled(true);
        findViewById(R.id.signup_btn).setEnabled(true);
    }


    private void showProgressBar(){
        findViewById(R.id.login_button).setVisibility(View.GONE);
        disableClicks();
        findViewById(R.id.progress_background).setVisibility(View.VISIBLE);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(){
        findViewById(R.id.login_button).setVisibility(View.VISIBLE);
        enableClicks();
        findViewById(R.id.progress_background).setVisibility(View.GONE);
        findViewById(R.id.progressBar).setVisibility(View.GONE);
    }


    private void editTokenId(){

        String token_id = FirebaseInstanceId.getInstance().getToken();
        String user_email = mAuth.getCurrentUser().getEmail();

        Map<String,Object> tokenMap = new HashMap<>();
        tokenMap.put("token_id", token_id);
        db.collection("Users").document(user_email).update(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

    }

}
