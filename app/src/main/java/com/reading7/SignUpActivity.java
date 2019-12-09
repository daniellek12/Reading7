package com.reading7;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reading7.Adapters.AutoCompleteSchoolsAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    /*----- For schools autocomplete -----*/
    private AutoCompleteSchoolsAdapter adapter;
    private List<String> filtered;
    private List<String> schools;
    private EditText school_edit;
    private String school;
    private ListView autoCompleteList;
    private View dummy;

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

                showProgressBar();
                dissapearErrorMsgs();
                User user = getUser();
                signUpUser(user);
                hideProgressBar();
            }
        });


        school = null;
        school_edit = findViewById(R.id.school_name_edit);
        autoCompleteList = findViewById(R.id.autoCompleteList);
        dummy = findViewById(R.id.dummy);
        setupSchoolsAutoComplete();

        setupDatePicker();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private User getUser() {

        String name = getName();
        if(name == null) {
            findViewById(R.id.illegal_name).setVisibility(View.VISIBLE);
            return null;
        }

        String email = getEmail();
        if(email == null) {
            findViewById(R.id.illegal_mail).setVisibility(View.VISIBLE);
            return null;
        }

        String password = getPassword();
        if(password == null) {
            findViewById(R.id.illegal_password).setVisibility(View.VISIBLE);
            return null;
        }

        int age = getAge();
        if(age <= 0) {
            findViewById(R.id.illegal_age).setVisibility(View.VISIBLE);
            return null;
        }

        String school_name = getSchoolName();
        if(school_name == null) {
            findViewById(R.id.illegal_school_name).setVisibility(View.VISIBLE);
            return null;
        }

        return new User(name, email, age, school_name);
    }

    private String getName() {

        String name = ((EditText) findViewById(R.id.name_edit)).getText().toString();

        //should be not empty and only hebrew
        if (name.equals("") || !(name.matches("[\u0590-\u05fe]+(( )[\u0590-\u05fe]+)*")))
            return null;

        return name;
    }

    private String getEmail() {

        String email = ((EditText) findViewById(R.id.email_edit)).getText().toString();
        if (email.equals("") || !(email.matches("[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")))
            return null;

        return email;
    }

    private String getPassword() {

        String password = ((EditText) findViewById(R.id.password_edit)).getText().toString();
        if (password.length() < 6)
            return null;

        return password;
    }

    private void setupDatePicker() {

        final EditText age_edit = findViewById(R.id.age_edit);
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

                age_edit.setText(sdf.format(myCalendar.getTime()));
            }

        };

        age_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(SignUpActivity.this,R.style.MySpinnerDatePickerStyle, date,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                dialog.show();
            }
        });
    }

    private int getAge() {

        String str_age = ((EditText) findViewById(R.id.age_edit)).getText().toString();

        if (str_age.equals(""))
            return 0;

        int age = Utils.calculateAge(str_age);
        if (age <= 0)
            return 0;

        return age;
    }

    private void setupSchoolsAutoComplete() {

        schools = Arrays.asList(getResources().getStringArray(R.array.schools));
        filtered = new ArrayList<>();

        school_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { alterAutocompleteAdapter(); }
            @Override
            public void afterTextChanged(Editable arg0) { }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
        });

        adapter = new AutoCompleteSchoolsAdapter(this, filtered);
        autoCompleteList.setAdapter(adapter);
        alterAutocompleteAdapter();

        autoCompleteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {

                school = filtered.get(position);
                school_edit.setText(school);

                dummy.setVisibility(View.GONE);
                autoCompleteList.setVisibility(View.GONE);

                Utils.closeKeyboard(SignUpActivity.this);
                school_edit.clearFocus();
            }
        });

        dummy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoCompleteList.setVisibility(View.GONE);
                dummy.setVisibility(View.GONE);

            }
        });

        school_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoCompleteList.setVisibility(View.VISIBLE);
                dummy.setVisibility(View.VISIBLE);
            }
        });

        school_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    autoCompleteList.setVisibility(View.VISIBLE);
                    dummy.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void alterAutocompleteAdapter(){

        filtered.clear();
        adapter.notifyDataSetChanged();

        if (!school_edit.getText().toString().isEmpty()) {
            for (int i = 0; i < schools.size(); i++) {
                if (schools.get(i).contains(school_edit.getText().toString()))
                    filtered.add(schools.get(i));
                adapter.notifyDataSetChanged();
            }
        }

        if(filtered.isEmpty()){
            autoCompleteList.setVisibility(View.GONE);
            dummy.setVisibility(View.GONE);
        }

        else {
            autoCompleteList.setVisibility(View.VISIBLE);
            dummy.setVisibility(View.VISIBLE);
        }
    }

    private String getSchoolName() {

        String school_name = ((EditText) findViewById(R.id.school_name_edit)).getText().toString();
        schools = Arrays.asList(getResources().getStringArray(R.array.schools));

        if (school_name.equals("") || !(school_name.matches("[\u0590-\u05fe]+(( )[\u0590-\u05fe]+)*"))
                || !schools.contains(school_name))
            return null;

        return school_name;
    }

    private void signUpUser(final User user) {

        if (user == null) return;

        String password = ((EditText) findViewById(R.id.password_edit)).getText().toString();
        mAuth.createUserWithEmailAndPassword(user.getEmail(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    mAuth = FirebaseAuth.getInstance();

                    DocumentReference newUser = db.collection("Users").document(user.getEmail());

                    newUser.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.putExtra("NEW_USER", true);
                                startActivity(intent);
                                finish();
                            }

                            else Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {

                    if (task.getException().getMessage().equals("The email address is badly formatted."))
                        findViewById(R.id.illegal_mail).setVisibility(View.VISIBLE);

                    if (task.getException().getMessage().equals("The given password is invalid. [ Password should be at least 6 characters ]"))
                        findViewById(R.id.illegal_password).setVisibility(View.VISIBLE);

                    if (task.getException().getMessage().equals("The email address is already in use by another account."))
                        findViewById(R.id.email_exists).setVisibility(View.VISIBLE);

                    else Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    hideProgressBar();
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
        findViewById(R.id.email_exists).setVisibility(View.INVISIBLE);
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

    private void showProgressBar(){

        disableClicks();
        findViewById(R.id.signup_btn).setVisibility(View.INVISIBLE);
        findViewById(R.id.progress_background).setVisibility(View.VISIBLE);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(){

        findViewById(R.id.progress_background).setVisibility(View.GONE);
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        findViewById(R.id.signup_btn).setVisibility(View.VISIBLE);
        enableClicks();
    }

}
