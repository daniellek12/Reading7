package com.reading7.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.reading7.LoginActivity;
import com.reading7.R;
import com.reading7.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class AdminCredentialsDialog extends AppCompatDialogFragment {

    private FirebaseAuth mAuth;
    private View dialog_view;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialog_view = getActivity().getLayoutInflater().inflate(R.layout.admin_credentials_dialog, null);
        final EditText editText = dialog_view.findViewById(R.id.password);
        dialog_view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                final String password = editText.getText().toString();
                if (password.trim().isEmpty())
                    return;

                showProgressBar();
                dialog_view.findViewById(R.id.wrong_details_txt).setVisibility(View.GONE);

                mAuth.signInWithEmailAndPassword(getResources().getString(R.string.admin_email), password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Utils.isAdmin = true;
                                    ((LoginActivity) getActivity()).redirectToMain();
//                                    Intent intent = new Intent(getActivity(), AdminActivity.class);
//                                    startActivity(intent);
//                                    getActivity().finish();
                                } else {
                                    //Toast.makeText(getContext(), "סיסמה שגויה", Toast.LENGTH_LONG).show();
                                    dialog_view.findViewById(R.id.wrong_details_txt).setVisibility(View.VISIBLE);
                                    hideProgressBar();
                                }
                            }
                        });
            }
        });

        dialog_view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        builder.setView(dialog_view);
        return builder.create();
    }


    private void hideProgressBar() {
        dialog_view.findViewById(R.id.progress_background).setVisibility(View.GONE);
        dialog_view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        dialog_view.findViewById(R.id.lock).setVisibility(View.VISIBLE);
        //enableClicks();
        dialog_view.findViewById(R.id.ok).setEnabled(true);
        dialog_view.findViewById(R.id.cancel).setEnabled(true);
    }

    private void showProgressBar() {
        dialog_view.findViewById(R.id.lock).setVisibility(View.GONE);
        dialog_view.findViewById(R.id.progress_background).setVisibility(View.VISIBLE);
        dialog_view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        //disableClicks();
        dialog_view.findViewById(R.id.ok).setEnabled(false);
        dialog_view.findViewById(R.id.cancel).setEnabled(false);
    }
}
