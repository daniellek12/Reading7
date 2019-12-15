package com.reading7;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reading7.Adapters.ExpandedShelfAdapter;

import java.util.ArrayList;

public class ShelfFragment extends Fragment {

    public enum ShelfType{WISHLIST, MYBOOKS};

    private FirebaseFirestore db;
    private ShelfType type;
    private ExpandedShelfAdapter adapter;
    private ArrayList<String> book_names;
    private String title;
    private String owner_email;
    private boolean edit_mode;


    public ShelfFragment(ArrayList<String> book_names, String title, String owner_email, ShelfType type){
        this.type = type;
        this.book_names = book_names;
        this.title = title;
        this.owner_email = owner_email;
        this.edit_mode = false;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.shelf_fragment, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView)getActivity().findViewById(R.id.toolbar_title)).setText(title);

        RecyclerView shelfRV = getActivity().findViewById(R.id.shelfRV);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),3);
        shelfRV.setLayoutManager(layoutManager);
        adapter = new ExpandedShelfAdapter(getContext(), book_names, this.type);
        shelfRV.setAdapter(adapter);

        initBackButton();
        initEditButton();
        initDeleteButton();
        initCloseButton();
    }



    private void initBackButton() {

        getActivity().findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

    }

    private void initEditButton() {

        if(!FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(owner_email))
            throw new AssertionError("You can't edit someone else's shelf!");

        ImageView editButton = getActivity().findViewById(R.id.editShelfButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edit_mode)
                    stopEditMode();
                else
                    startEditMode();
            }
        });


    }

    private void initDeleteButton() {

        getActivity().findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.deleteItems();
                stopEditMode();
            }
        });
    }

    private void initCloseButton() {

        getActivity().findViewById(R.id.closeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopEditMode();
            }
        });

    }


    private void startEditMode() {
        edit_mode = true;
        adapter.setEditMode(true);

        ((MainActivity)getActivity()).disableBottomNavigation();
        getActivity().findViewById(R.id.editShelfButton).setVisibility(View.GONE);
        getActivity().findViewById(R.id.backButton).setVisibility(View.GONE);
        getActivity().findViewById(R.id.deleteButton).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.closeButton).setVisibility(View.VISIBLE);
        ((TextView)getActivity().findViewById(R.id.toolbar_title)).setText(getString(R.string.choose_items));

    }

    private void stopEditMode() {
        edit_mode = false;
        adapter.setEditMode(false);

        getActivity().findViewById(R.id.deleteButton).setVisibility(View.GONE);
        getActivity().findViewById(R.id.closeButton).setVisibility(View.GONE);
        getActivity().findViewById(R.id.editShelfButton).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.backButton).setVisibility(View.VISIBLE);
        ((TextView)getActivity().findViewById(R.id.toolbar_title)).setText(title);
        ((MainActivity)getActivity()).enableBottomNavigation();

    }

}
