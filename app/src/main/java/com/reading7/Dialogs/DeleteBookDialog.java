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
import com.reading7.ExploreFragment;
import com.reading7.LoginActivity;
import com.reading7.R;
import com.reading7.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

public class DeleteBookDialog extends AppCompatDialogFragment {

    private View dialog_view;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialog_view = getActivity().getLayoutInflater().inflate(R.layout.delete_book_dialog, null);

        final String book_id = getArguments().getString("book_id");
        final String book_title = getArguments().getString("book_title");
        final String context = getArguments().getString("context");

        dialog_view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                dismiss();
                Utils.deleteBookFromDB(book_id, book_title);
                if (context.equals("explore fragment")) {
                    ((ExploreFragment) getTargetFragment()).loadAgain();
                }
                else if (context.equals("book fragment")){
                    ((BookFragment) getTargetFragment()).admin_delete = true;
                    getActivity().onBackPressed();
                }
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
}
