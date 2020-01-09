package com.reading7;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class PrivacySettingsFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        return inflater.inflate(R.layout.privacy_settings_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initPrivateBtn();
        initBackButton();
    }

    private void initBackButton() {
        getView().findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void initPrivateBtn() {
        final Switch sw = getActivity().findViewById(R.id.private_profile_switch);
        DocumentReference userRef = db.collection("Users").document(mAuth.getCurrentUser().getEmail());
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Boolean is_private = (Boolean) document.getData().get("is_private");
                        sw.setChecked(is_private);
                        sw.setEnabled(true);
                    } else
                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        sw.bringToFront();
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utils.enableDisableClicks(getActivity(),(ViewGroup)getView(),false);
                if (isChecked) {
                    db.collection("Users").document(mAuth.getCurrentUser().getEmail()).update("is_private", true);
                    Utils.enableDisableClicks(getActivity(),(ViewGroup)getView(),true);
                } else {
                    db.collection("Users").document(mAuth.getCurrentUser().getEmail()).update("is_private", false);
                    Utils.enableDisableClicks(getActivity(),(ViewGroup)getView(),true);
                }
            }
        });
    }
}
