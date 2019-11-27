package com.reading7;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        return inflater.inflate(R.layout.profile_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        setUpLogOutBtn();
    }

    private void setUpLogOutBtn(){

        final TextView logout = getActivity().findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                disableClicks();
                logout.setVisibility(View.GONE);
                getActivity().findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
                signOut();
            }
        });
    }

    private void signOut(){
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
        return;
    }

    private void disableClicks() {
        //getActivity().findViewById(R.id.settings).setEnabled(false);
        getActivity().findViewById(R.id.logout).setEnabled(false);
    }

    private void enableClicks() {
        //getActivity().findViewById(R.id.settings).setEnabled(true);
        getActivity().findViewById(R.id.logout).setEnabled(true);
    }
}
