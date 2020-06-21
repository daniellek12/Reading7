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
import com.reading7.BookFragment;
import com.reading7.LoginActivity;
import com.reading7.R;
import com.reading7.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

public class SaveEditBookDialog extends AppCompatDialogFragment {

    private View dialog_view;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialog_view = getActivity().getLayoutInflater().inflate(R.layout.save_edit_book_dialog, null);
        dialog_view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Fragment f = getFragmentManager().findFragmentById(R.id.fragmant_container);
                ((BookFragment)f).setEditMode(false);
                ((BookFragment)f).updateBook();
                dismiss();
            }
        });

        dialog_view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment f = getFragmentManager().findFragmentById(R.id.fragmant_container);
                ((BookFragment)f).setEditMode(false);
                dismiss();
            }
        });

        builder.setView(dialog_view);
        return builder.create();
    }
}
