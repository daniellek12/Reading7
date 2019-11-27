package com.reading7;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        mAuth = FirebaseAuth.getInstance();

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpActivity.this.onBackPressed();
            }
        });

        final Button signup = findViewById(R.id.signup_btn);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dissapearErrorMsgs();
                disableClicks();
                User user = getUser();
                signUpUser(user);
                hideProgressBar();

            }


        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private User getUser() {

        String name = getName();

        String email = getEmail();

        String password = getPassword();

        int age = getAge();

        String school_name = getSchoolName();

        if (name == null || email == null || password == null || age == 0 || school_name == null)
            return null;

        return new User(name, email, age, school_name);
    }

    private String getName() {

        String name = ((EditText) findViewById(R.id.name_edit)).getText().toString();


        if (name.equals("") || !(name.matches("[\u0590-\u05fe]+(( )[\u0590-\u05fe]+)*"))) {
            findViewById(R.id.illegal_name).setVisibility(View.VISIBLE);
            return null;
        }

        return name;
    }


    private String getEmail() {

        String email = ((EditText) findViewById(R.id.email_edit)).getText().toString();
        if (email.equals("") || !(email.matches("[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"))) {
            findViewById(R.id.illegal_mail).setVisibility(View.VISIBLE);
            return null;
        }

        return email;
    }

    private String getPassword() {

        String password = ((EditText) findViewById(R.id.password_edit)).getText().toString();
        if (password.length() < 6) {
            findViewById(R.id.illegal_password).setVisibility(View.VISIBLE);
            return null;
        }

        return password;
    }

    private int getAge() {
        String str_age = ((EditText) findViewById(R.id.age_edit)).getText().toString();
        if (str_age.equals("")){
            findViewById(R.id.illegal_age).setVisibility(View.VISIBLE);
            return 0;
        }
        int age = Integer.parseInt(str_age);
        if (age <= 0) {
            findViewById(R.id.illegal_age).setVisibility(View.VISIBLE);
            return 0;
        }
        return age;
    }


    private String getSchoolName() {

        String school_name = ((EditText) findViewById(R.id.school_name_edit)).getText().toString();

        if (school_name.equals("") || !(school_name.matches("[\u0590-\u05fe]+(( )[\u0590-\u05fe]+)*"))) {
            findViewById(R.id.illegal_school_name).setVisibility(View.VISIBLE);
            return null;
        }

        return school_name;
    }


    private void signUpUser(final User user) {

        if (user == null) return;
        String password = ((EditText) findViewById(R.id.password_edit)).getText().toString();

        Button signup = findViewById(R.id.signup_btn);
        signup.setVisibility(View.GONE);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(user.getEmail(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    mAuth = FirebaseAuth.getInstance();

                    //String token_id =FirebaseInstanceId.getInstance().getToken();

//                    user.setToken_id(token_id);


                    DocumentReference newUser = db.collection("Users").document(user.getEmail());

                    newUser.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.putExtra("NEW_USER", true);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {

                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    findViewById(R.id.signup_btn).setVisibility(View.VISIBLE);
                    enableClicks();

                    if (task.getException().getMessage().equals("The email address is badly formatted."))
                        findViewById(R.id.illegal_mail).setVisibility(View.VISIBLE);

                    if (task.getException().getMessage().equals("The given password is invalid. [ Password should be at least 6 characters ]"))
                        findViewById(R.id.illegal_password).setVisibility(View.VISIBLE);

                    if (task.getException().getMessage().equals("The email address is already in use by another account."))
                        findViewById(R.id.email_exists).setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void disableClicks(){

        findViewById(R.id.school_name_edit).setEnabled(false);
        findViewById(R.id.name_edit).setEnabled(false);
        findViewById(R.id.password_edit).setEnabled(false);
        findViewById(R.id.email_edit).setEnabled(false);
        findViewById(R.id.age_edit).setEnabled(false);
        findViewById(R.id.backBtn).setEnabled(false);

    }

    private void enableClicks(){

        findViewById(R.id.school_name_edit).setEnabled(true);
        findViewById(R.id.name_edit).setEnabled(true);
        findViewById(R.id.password_edit).setEnabled(true);
        findViewById(R.id.email_edit).setEnabled(true);
        findViewById(R.id.age_edit).setEnabled(true);
        findViewById(R.id.backBtn).setEnabled(true);

    }

    private void dissapearErrorMsgs() {
        findViewById(R.id.illegal_mail).setVisibility(View.INVISIBLE);
        findViewById(R.id.illegal_password).setVisibility(View.INVISIBLE);
        findViewById(R.id.illegal_name).setVisibility(View.INVISIBLE);
        findViewById(R.id.illegal_school_name).setVisibility(View.INVISIBLE);
        findViewById(R.id.illegal_age).setVisibility(View.INVISIBLE);

    }

//    private void shake(View view){
//
//        Animation shake;
//        if(view instanceof TextView)
//            shake = AnimationUtils.loadAnimation(this, R.anim.shake_text);
//
//        else shake = AnimationUtils.loadAnimation(this, R.anim.shake_animation);
//
//        view.startAnimation(shake);
//    }

    private void hideProgressBar(){
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        findViewById(R.id.signup_btn).setVisibility(View.VISIBLE);
        enableClicks();
    }

}
