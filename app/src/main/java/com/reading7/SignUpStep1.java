package com.reading7;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import com.reading7.Dialogs.EditAvatarDialog;
import com.reading7.Objects.Avatar;
import com.reading7.Objects.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class SignUpStep1 extends Fragment {

    private Avatar avatar = new Avatar(1,1,1,1,1);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sign_up_step1_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initAvatarDetails();
        setupDatePicker();
        setupAvatarDialog();
    }


    public User getUser() {

        String name = getName();
        if(name == null) {
            getView().findViewById(R.id.illegal_name).setVisibility(View.VISIBLE);
            return null;
        }

        String email = getEmail();
        if(email == null) {
            getView().findViewById(R.id.illegal_mail).setVisibility(View.VISIBLE);
            return null;
        }

        String password = getPassword();
        if(password == null) {
            getView().findViewById(R.id.illegal_password).setVisibility(View.VISIBLE);
            return null;
        }

        String birth_date = getBirthDate();
        if(birth_date == null) {
            getView().findViewById(R.id.illegal_birth_date).setVisibility(View.VISIBLE);
            return null;
        }

        return new User(name, email, birth_date, avatar);
    }

    private String getName() {

        String name = ((EditText) getView().findViewById(R.id.name_edit)).getText().toString();

        //should be not empty and only hebrew
        if (name.equals("") || !(name.matches("[\u0590-\u05fe]+(( )[\u0590-\u05fe]+)*")))
            return null;

        return name;
    }

    private String getEmail() {

        String email = ((EditText) getView().findViewById(R.id.email_edit)).getText().toString();
        if (email.equals("") || !(email.matches("[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")))
            return null;

        return email.toLowerCase();
    }

    public String getPassword() {

        String password = ((EditText) getView().findViewById(R.id.password_edit)).getText().toString();
        if (password.length() < 6)
            return null;

        return password;
    }

    private String getBirthDate () {
        String birth_date = ((EditText) getView().findViewById(R.id.birth_date_edit)).getText().toString();
        if (birth_date.equals("")){
            return "";
        }
        return birth_date;
    }

    private void initAvatarDetails(){

        avatar.loadIntoImage(getContext(),(CircleImageView)getView().findViewById(R.id.profile_image));
    }

    private void setupAvatarDialog() {

        getView().findViewById(R.id.profile_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditAvatarDialog dialog = new EditAvatarDialog(avatar);
                dialog.setTargetFragment(SignUpStep1.this, 100);
                dialog.show(getActivity().getSupportFragmentManager(), "edit avater");
            }
        });

        getView().findViewById(R.id.editIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditAvatarDialog dialog = new EditAvatarDialog(avatar);
                dialog.setTargetFragment(SignUpStep1.this, 100);
                dialog.show(getActivity().getSupportFragmentManager(), "edit avater");
            }
        });
    }

    private void setupDatePicker() {

        final EditText birth_date_edit = getView().findViewById(R.id.birth_date_edit);
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
                DatePickerDialog dialog = new DatePickerDialog(getContext(),R.style.MySpinnerDatePickerStyle, date,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                dialog.show();
            }
        });
    }

    public void dissapearErrorMsgs() {
        getActivity().findViewById(R.id.email_exists).setVisibility(View.INVISIBLE);
        getView().findViewById(R.id.illegal_mail).setVisibility(View.INVISIBLE);
        getView().findViewById(R.id.illegal_password).setVisibility(View.INVISIBLE);
        getView().findViewById(R.id.illegal_name).setVisibility(View.INVISIBLE);
        getView().findViewById(R.id.illegal_birth_date).setVisibility(View.INVISIBLE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            this.avatar = (Avatar)data.getSerializableExtra("Avatar");
            avatar.loadIntoImage(getContext(),(CircleImageView) getView().findViewById(R.id.profile_image));
        }
    }

}
