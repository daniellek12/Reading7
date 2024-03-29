package com.reading7;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reading7.Dialogs.ChangePasswordDialog;
import com.reading7.Dialogs.EditAvatarDialog;
import com.reading7.Objects.Avatar;
import com.reading7.Objects.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {

    private User user;
    private Avatar avatar;

    private boolean openCloset;


    public EditProfileFragment() {}

    public EditProfileFragment(boolean openCloset) {
            this.openCloset = openCloset;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        user = ((MainActivity) getActivity()).getCurrentUser();
        avatar = new Avatar(user.getAvatar());
        return inflater.inflate(R.layout.edit_profile_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getUserDetails();
        initBackButton();
        initAvatarDialog();
        initDatePicker();
        initChangePasswordDialog();
        initSaveButton();

        if(openCloset)
            showEditAvatarDialog();
    }

    private void getUserDetails() {

        User user = ((MainActivity) getActivity()).getCurrentUser();

        TextView userName = getActivity().findViewById(R.id.name_edit);
        userName.setText(user.getFull_name());

        TextView userBirthday = getActivity().findViewById(R.id.birth_date_edit);
        userBirthday.setText(user.getBirth_date());

        CircleImageView profileImage = getActivity().findViewById(R.id.profile_image);
        user.getAvatar().loadIntoImage(getContext(), profileImage);
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

                final String name = getNewName(), birthday = getNewBirthDate();
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

                updateUser(name, birthday);

                setLoadingMode(false);
                getActivity().onBackPressed();
            }
        });

    }

    private void initAvatarDialog() {

        getView().findViewById(R.id.profile_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditAvatarDialog();
            }
        });
    }

    public void showEditAvatarDialog() {
        EditAvatarDialog dialog = new EditAvatarDialog(avatar);
        dialog.setTargetFragment(EditProfileFragment.this, 100);
        dialog.show(getActivity().getSupportFragmentManager(), "edit avater");
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

    private void initChangePasswordDialog() {

        getView().findViewById(R.id.changePasswordButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePasswordDialog dialog = new ChangePasswordDialog();
                dialog.show(getActivity().getSupportFragmentManager(), "change password dialog");
            }
        });
    }


    private String getNewName() {
        String name = ((EditText) getView().findViewById(R.id.name_edit)).getText().toString();
        if (name.equals("") || !(name.matches("[\u0590-\u05fe]+(( )[\u0590-\u05fe]+)*")))
            return null;

        return name;
    }

    private String getNewBirthDate() {
        String birth_date = ((EditText) getView().findViewById(R.id.birth_date_edit)).getText().toString();
        if (birth_date.equals("")) {
            return "";
        }
        return birth_date;
    }


    private void updateUser(String newName, String newBirthday) {

        user.setBirth_date(newBirthday);
        user.setFull_name(newName);
        user.setAvatar(avatar);
        ((MainActivity) getActivity()).setCurrentUser(user);

        DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users").document(user.getEmail());
        userRef.update("full_name", newName, "birth_date", newBirthday, "avatar", user.getAvatar());
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure fragment codes match up
        if (requestCode == 100) {
            avatar = (Avatar) data.getSerializableExtra("Avatar");
            avatar.loadIntoImage(getContext(), (CircleImageView) getView().findViewById(R.id.profile_image));
        }
    }


    private void dissapearErrorMasseges() {
        getActivity().findViewById(R.id.illegal_name).setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.illegal_birth_date).setVisibility(View.INVISIBLE);
    }

    private void setLoadingMode(Boolean isLoading) {

        if (isLoading) {
            Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), false);
            dissapearErrorMasseges();
            getView().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.save).setVisibility(View.GONE);
        } else {
            Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), true);
            getView().findViewById(R.id.progressBar).setVisibility(View.GONE);
            getView().findViewById(R.id.save).setVisibility(View.VISIBLE);
        }
    }

}
