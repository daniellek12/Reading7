package com.reading7;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reading7.Objects.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EditProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        user = ((MainActivity) getActivity()).getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.edit_profile_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getUserDetails();
        initBackButton();
        initAvatarDialog();
        initDatePicker();
        initSaveButton();
    }

    private void getUserDetails() {

        User user = ((MainActivity) getActivity()).getCurrentUser();

//        TextView userEmail = getActivity().findViewById(R.id.email_edit);
//        userEmail.setText(user.getEmail());

        TextView userName = getActivity().findViewById(R.id.name_edit);
        userName.setText(user.getFull_name());

        TextView userBirthday = getActivity().findViewById(R.id.birth_date_edit);
        userBirthday.setText(user.getBirth_date());

        CircleImageView profileImage = getActivity().findViewById(R.id.profile_image);
        Utils.loadAvatar(getContext(), profileImage, user.getAvatar_details());
    }

    private void initBackButton() {
        getView().findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void initSaveButton() {

        getActivity().findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setLoadingMode(true);
                dissapearErrorMasseges();

                final String name = getName(), birthday = getBirthDate();
                if (name == null) {
                    getActivity().findViewById(R.id.illegal_name).setVisibility(View.VISIBLE);
                    setLoadingMode(false);
                    return;
                }
                if (birthday == null) {
                    getActivity().findViewById(R.id.illegal_birth_date).setVisibility(View.VISIBLE);
                    setLoadingMode(false);
                    return;
                }

                DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users").document(user.getEmail());
                userRef.update("full_name", name, "birth_date", birthday, "avatar_details", user.getAvatar_details());

                user.setBirth_date(birthday);
                user.setFull_name(name);
                ((MainActivity)getActivity()).setCurrentUser(user);
                getActivity().onBackPressed();
            }
        });

    }

    private void initAvatarDialog() {

        getView().findViewById(R.id.profile_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditAvatarDialog dialog = new EditAvatarDialog(user.getAvatar_details());
                dialog.setTargetFragment(EditProfileFragment.this, 100);
                dialog.show(getActivity().getSupportFragmentManager(), "edit avater");
            }
        });
    }

    private void initDatePicker() {

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
                DatePickerDialog dialog = new DatePickerDialog(getContext(), R.style.MySpinnerDatePickerStyle, date,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                dialog.show();
            }
        });
    }

    private String getName() {
        String name = ((EditText) getView().findViewById(R.id.name_edit)).getText().toString();
        if (name.equals("") || !(name.matches("[\u0590-\u05fe]+(( )[\u0590-\u05fe]+)*")))
            return null;

        return name;
    }

    private String getEmail() {
        String email = ((EditText) getView().findViewById(R.id.email_edit)).getText().toString();
        if (email.equals("") || !(email.matches("[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")))
            return null;

        return email;
    }

    private String getBirthDate() {
        String birth_date = ((EditText) getView().findViewById(R.id.birth_date_edit)).getText().toString();
        if (birth_date.equals("")) {
            return "";
        }
        return birth_date;
    }

    private void dissapearErrorMasseges() {
        getActivity().findViewById(R.id.illegal_mail).setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.illegal_name).setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.illegal_birth_date).setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.email_exists).setVisibility(View.INVISIBLE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure fragment codes match up
        if (requestCode == 100) {
            user.setAvatar_details(data.getIntegerArrayListExtra("avatar_details"));
            Utils.loadAvatar(getContext(),(CircleImageView)getView().findViewById(R.id.profile_image), user.getAvatar_details());
        }
    }

    private void setLoadingMode(Boolean isLoading) {

        if(isLoading){
            Utils.enableDisableClicks(getActivity(), (ViewGroup)getView(), false);
            getView().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.save).setVisibility(View.GONE);
        } else {
            Utils.enableDisableClicks(getActivity(), (ViewGroup)getView(), true);
            getView().findViewById(R.id.progressBar).setVisibility(View.GONE);
            getView().findViewById(R.id.save).setVisibility(View.VISIBLE);
        }
    }


}
