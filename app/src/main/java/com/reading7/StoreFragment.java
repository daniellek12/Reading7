package com.reading7;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.reading7.Adapters.StoreAdapter;
import com.reading7.Objects.Avatar;
import com.reading7.Objects.Product;
import com.reading7.Objects.User;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class StoreFragment extends Fragment {

    StoreAdapter adapter;
    ArrayList<Product> products = new ArrayList<Product>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.store_fragment, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getProducts();
        getUserDetails();
        initBackButton();

        RecyclerView storeRV = getActivity().findViewById(R.id.storeRV);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        storeRV.setLayoutManager(layoutManager);
        adapter = new StoreAdapter(getContext(), this, products);
        storeRV.setAdapter(adapter);

        Animation slide_down = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
        getView().findViewById(R.id.storeRV).startAnimation(slide_down);
        getView().findViewById(R.id.imageView8).startAnimation(slide_down);

    }

    private void getProducts() {

        // Shirts - hoodies
        for (int i = 2; i < 8; i++) {
            products.add(new Product(70, new Avatar.Item(Avatar.ItemType.SHIRT, i)));
        }

        // Shirts - stripes
        for (int i = 1; i < 11; i++) {
            products.add(new Product(70, new Avatar.Item(Avatar.ItemType.SHIRT, 8, i)));
        }

        // Shirts - stars
        for (int i = 1; i < 11; i++) {
            products.add(new Product(70, new Avatar.Item(Avatar.ItemType.SHIRT, 9, i)));
        }

        // Shirts - flames
        products.add(new Product(70, new Avatar.Item(Avatar.ItemType.SHIRT, 10, 1)));
        products.add(new Product(70, new Avatar.Item(Avatar.ItemType.SHIRT, 10, 3)));

        // Glasses
        for (int i = 1; i < 11; i++) {
            products.add(new Product(100, new Avatar.Item(Avatar.ItemType.GLASSES, i)));
        }

        // Mustaches
        for (int i = 1; i < 5; i++) {
            int hairColor = ((MainActivity) getActivity()).getCurrentUser().getAvatar().getHairColor();
            products.add(new Product(70, new Avatar.Item(Avatar.ItemType.MUSTACHE, i, hairColor)));
        }

        // Hair accessories
        for (int i = 1; i < 11; i++) {
            products.add(new Product(70, new Avatar.Item(Avatar.ItemType.HAIR_ACCESSORY, i)));
        }
    }

    private void getUserDetails() {
        User user = ((MainActivity) getActivity()).getCurrentUser();
        user.getAvatar().loadIntoImage(getContext(), (CircleImageView) getView().findViewById(R.id.profileImage));
        ((TextView) getView().findViewById(R.id.myPoints)).setText(String.valueOf(user.getPoints()));
    }

    private void initBackButton() {

        getActivity().findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 404) {
            getUserDetails();
            adapter.notifyDataSetChanged();
        }
    }
}
