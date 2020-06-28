package com.reading7.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Objects.Shelf;
import com.reading7.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AddToShelfDialog extends AppCompatDialogFragment {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private View dialogView;
    private ArrayList<String> shelfNames = new ArrayList<>();
    private String chosenShelfName = "";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        getUserShelves();

        final String book_title = getArguments().getString("book_title");

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialogView = getActivity().getLayoutInflater().inflate(R.layout.add_to_shelf_dialog, null);

        dialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (chosenShelfName.isEmpty()) {
                    String error = "עדיין לא בחרת מדף...";
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    return;
                }

                CollectionReference requestCollectionRef = db.collection("Users")
                        .document(mAuth.getCurrentUser().getEmail()).collection("Shelves");
                Query requestQuery = requestCollectionRef.whereEqualTo("shelf_name", chosenShelfName);
                requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().update("book_names", FieldValue.arrayUnion(book_title));
                                Toast.makeText(getContext(), "הספר נוסף למדף", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });

                dismiss();
            }
        });

        dialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        builder.setView(dialogView);
        return builder.create();
    }

    private void getUserShelves() {
        db.collection("Users").document(mAuth.getCurrentUser().getEmail())
                .collection("Shelves").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    shelfNames.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        shelfNames.add(doc.toObject(Shelf.class).getShelf_name());
                    }

                    RecyclerView chooseShelfRV = dialogView.findViewById(R.id.chooseShelfRV);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    chooseShelfRV.setLayoutManager(layoutManager);
                    ChooseShelfAdapter adapter = new ChooseShelfAdapter(getContext(), shelfNames);
                    chooseShelfRV.setAdapter(adapter);

                }
            }
        });
    }

    private class ChooseShelfAdapter extends RecyclerView.Adapter<ChooseShelfAdapter.ViewHolder> {

        private Context mContext;
        private ArrayList<String> mShelfNames;

        private int chosen = -1;

        public ChooseShelfAdapter(Context context, ArrayList<String> shelfNames) {
            this.mContext = context;
            this.mShelfNames = shelfNames;
        }

        @NonNull
        @Override
        public ChooseShelfAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_shelf_item, parent, false);
            return new ChooseShelfAdapter.ViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return mShelfNames.size();
        }

        @Override
        public void onBindViewHolder(@NonNull final ChooseShelfAdapter.ViewHolder holder, final int position) {

            String shelfName = mShelfNames.get(position);

            holder.shelfName.setText(shelfName);

            if (chosen == position) {
                holder.shelfName.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorPrimaryDark)));
                holder.shelfName.setTextColor(mContext.getResources().getColor(R.color.white));
            } else {
                holder.shelfName.setBackgroundTintList(null);
                holder.shelfName.setTextColor(mContext.getResources().getColor(R.color.darkGrey));
            }


            holder.shelfName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chosen = position;
                    setChosenShelfName();
                    notifyDataSetChanged();
                }
            });
        }

        public void setChosenShelfName() {
            chosenShelfName = mShelfNames.get(chosen);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView shelfName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.shelfName = itemView.findViewById(R.id.shelfName);
            }
        }

    }

}
