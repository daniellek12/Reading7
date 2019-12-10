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
//        setupSchoolsAutoComplete();

        setupDatePicker();

        findViewById(R.id.profile_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditAvatarDialog();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    public void openEditAvatarDialog(){

        EditAvatarDialog editAvatarDialog = new EditAvatarDialog();
        editAvatarDialog.show(getSupportFragmentManager(), "edit avatar dialog");

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

        String birth_date = getBirthDate();
        if(birth_date == null) {
            findViewById(R.id.illegal_birth_date).setVisibility(View.VISIBLE);
            return null;
        }

        return new User(name, email, birth_date);
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

        final EditText birth_date_edit = findViewById(R.id.birth_date_edit);
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

                birth_date_edit.setText(sdf.format(myCalendar.getTime()));
            }

        };

        birth_date_edit.setOnClickListener(new View.OnClickListener() {
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

<<<<<<< HEAD
    private int getAge() {

        String str_age = ((EditText) findViewById(R.id.age_edit)).getText().toString();

        if (str_age.equals(""))
            return 0;

        int age = Utils.calculateAge(str_age);
        if (age <= 0)
            return 0;

        return age;
    }

