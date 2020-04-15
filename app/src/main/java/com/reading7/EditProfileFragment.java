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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Dialogs.ChangePasswordDialog;
import com.reading7.Dialogs.EditAvatarDialog;
import com.reading7.Objects.Avatar;
import com.reading7.Objects.Review;
import com.reading7.Objects.User;
import com.reading7.Objects.WishList;

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
        initChangePasswordDialog();
        initSaveButton();
    }

    private void getUserDetails() {

        User user = ((MainActivity) getActivity()).getCurrentUser();

        TextView userName = getActivity().findViewById(R.id.name_edit);
        userName.setText(user.getFull_name());

        TextView userBirthday = getActivity().findViewById(R.id.birth_date_edit);
        userBirthday.setText(user.getBirth_date());

        CircleImageView profileImage = getActivity().findViewById(R.id.profile_image);
        user.getAvatar().loadIntoImage(getContext(),profileImage);
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

                updateReviews(name, Utils.calculateAge(birthday), user.getAvatar());
                updateWishlists(name, user.getAvatar());
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
                EditAvatarDialog dialog = new EditAvatarDialog(user.getAvatar());
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


    private void updateReviews(final String newName, final int newAge, final Avatar newAvatar) {

        CollectionReference reviews = FirebaseFirestore.getInstance().collection("Reviews");
        Query query = reviews.whereEqualTo("reviewer_email", user.getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot doc: task.getResult()){
                        Review review = doc.toObject(Review.class);
                        FirebaseFirestore.getInstance()
                                .collection("Reviews")
                                .document(review.getReview_id())
                                .update("reviewer_name", newName, "reviewer_age", newAge, "reviewer_avatar", newAvatar);
                    }
                }
            }
        });
    }

    private void updateWishlists(final String newName, final Avatar newAvatar) {

        CollectionReference wishlists = FirebaseFirestore.getInstance().collection("Wishlist");
        Query query = wishlists.whereEqualTo("user_email", user.getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot doc: task.getResult()){
                        WishList wishlist = doc.toObject(WishList.class);
                        FirebaseFirestore.getInstance()
                                .collection("Wishlist")
                                .document(wishlist.getId())
                                .update("user_name", newName,"user_avatar", newAvatar);
                    }
                }
            }
        });
    }

    private void updateUser(String newName, String newBirthday) {

        DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users").document(user.getEmail());
        userRef.update("full_name", newName, "birth_date", newBirthday, "avatar", user.getAvatar());

        user.setBirth_date(newBirthday);
        user.setFull_name(newName);
        ((MainActivity)getActivity()).setCurrentUser(user);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure fragment codes match up
        if (requestCode == 100) {
            user.setAvatar((Avatar)data.getSerializableExtra("Avatar"));
            user.getAvatar().loadIntoImage(getContext(),(CircleImageView)getView().findViewById(R.id.profile_image));
        }
    }


    private void dissapearErrorMasseges() {
        getActivity().findViewById(R.id.illegal_name).setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.illegal_birth_date).setVisibility(View.INVISIBLE);
    }

    private void setLoadingMode(Boolean isLoading) {

        if(isLoading){
            Utils.enableDisableClicks(getActivity(), (ViewGroup)getView(), false);
            dissapearErrorMasseges();
            getView().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.save).setVisibility(View.GONE);
        } else {
            Utils.enableDisableClicks(getActivity(), (ViewGroup)getView(), true);
            getView().findViewById(R.id.progressBar).setVisibility(View.GONE);
            getView().findViewById(R.id.save).setVisibility(View.VISIBLE);
        }
    }

}
