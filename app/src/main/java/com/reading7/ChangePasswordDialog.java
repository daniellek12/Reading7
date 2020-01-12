package com.reading7;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;


public class ChangePasswordDialog extends AppCompatDialogFragment {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.change_password_dialog, null);
        builder.setView(view);

        initOkButton(view);
        initCancelButton(view);
        initHideShowCharacters(view);

        return builder.create();
    }


    private void initOkButton(final View dialogView) {
        dialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setLoadingMode(dialogView, true);
                final String oldPassword = ((TextView) dialogView.findViewById(R.id.old_password)).getText().toString();
                final String newPassword = ((TextView) dialogView.findViewById(R.id.new_password)).getText().toString();

                if (newPassword.length() < 6) {
                    dialogView.findViewById(R.id.illegal_password).setVisibility(View.VISIBLE);
                    setLoadingMode(dialogView, false);
                    return;
                }

                if (oldPassword.equals("")) {
                    dialogView.findViewById(R.id.enter_password).setVisibility(View.VISIBLE);
                    setLoadingMode(dialogView, false);
                    return;
                }

                mAuth.getCurrentUser().reauthenticate(EmailAuthProvider.getCredential(mAuth.getCurrentUser().getEmail(), oldPassword))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mAuth.getCurrentUser().updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), getString(R.string.password_change_success), Toast.LENGTH_LONG).show();
                                                dismiss();
                                            } else {
                                                Toast.makeText(getContext(), getString(R.string.try_again), Toast.LENGTH_LONG).show();
                                                setLoadingMode(dialogView, false);
                                            }
                                        }
                                    });
                                } else {
                                    dialogView.findViewById(R.id.wrong_password).setVisibility(View.VISIBLE);
                                    setLoadingMode(dialogView, false);
                                }
                            }
                        });
            }
        });
    }

    private void initCancelButton(View dialogView) {
        dialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }


    /**
     * Toggle the visibility of the characters in the EditText by clicking the eye icon.
     */
    private void initHideShowCharacters(final View dialogView) {
        final ImageView oldPassEye = dialogView.findViewById(R.id.old_password_eye);
        oldPassEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText oldPassTxt = dialogView.findViewById(R.id.old_password);
                if(oldPassTxt.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    oldPassTxt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    oldPassEye.setImageDrawable(getResources().getDrawable(R.drawable.hide_password_eye));
                } else {
                    oldPassTxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    oldPassEye.setImageDrawable(getResources().getDrawable(R.drawable.show_password_eye));
                }
            }
        });

        final ImageView newPassEye = dialogView.findViewById(R.id.new_password_eye);
        newPassEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText newPassTxt = dialogView.findViewById(R.id.new_password);
                if(newPassTxt.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    newPassTxt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    newPassEye.setImageDrawable(getResources().getDrawable(R.drawable.hide_password_eye));
                } else {
                    newPassTxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    newPassEye.setImageDrawable(getResources().getDrawable(R.drawable.show_password_eye));
                }
            }
        });
    }

    /**
     * Enter/Exit loading mode.
     * Handle visibility of error messages and progressBar.
     */
    private void setLoadingMode(View view, boolean isLoading) {

        if (isLoading) {
            view.findViewById(R.id.illegal_password).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.enter_password).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.wrong_password).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.ok).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.cancel).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.ok).setVisibility(View.VISIBLE);
            view.findViewById(R.id.cancel).setVisibility(View.VISIBLE);
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
    }
}
