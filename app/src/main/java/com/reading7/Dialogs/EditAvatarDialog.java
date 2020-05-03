package com.reading7.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.google.android.material.tabs.TabLayout;
import com.reading7.Adapters.EditAvatarAdapter;
import com.reading7.Objects.Avatar;
import com.reading7.R;
import com.reading7.Utils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditAvatarDialog extends AppCompatDialogFragment {

    private Avatar avatar;
    private CircleImageView imageView;
    private List<View> tabViews = new ArrayList<>();

    public EditAvatarDialog(Avatar avatar) {
        this.avatar = avatar;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.edit_avatar_dialog, null);

        imageView = view.findViewById(R.id.image);
        avatar.loadIntoImage(getContext(), imageView);

        initTabs(view);
        initOkButton(view);
        initCancelButton(view);

        initSkinLayout(view);
        initEyesLayout(view);
        initHairColorLayout(view);
        initHairTypeLayout(view);
        initShirtLayout(view);
        initGlassesLayout(view);
        initMustacheLayout(view);
        initHairAccessoriesLayout(view);

        Dialog dialog = builder.setView(view).create();
        dialog.getWindow().setBackgroundDrawable(Utils.getDrawable(getContext(), "blank"));
        return dialog;
    }


    private void initTabs(View view) {

        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.getTabAt(0).getIcon().setTint(Utils.getColor(getContext(), "colorPrimaryDark"));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setTint(Utils.getColor(getContext(), "colorPrimaryDark"));
                for (int i = 0; i < tabViews.size(); i++) {
                    if (tab.getPosition() == i)
                        tabViews.get(i).setVisibility(View.VISIBLE);
                    else
                        tabViews.get(i).setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setTint(Utils.getColor(getContext(), "lightGrey"));

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Views should be added in the right order
        tabViews.add(view.findViewById(R.id.skinLayout));
        tabViews.add(view.findViewById(R.id.eyesLayout));
        tabViews.add(view.findViewById(R.id.hairLayout));
        tabViews.add(view.findViewById(R.id.mustacheLayout));
        tabViews.add(view.findViewById(R.id.shirtLayout));
        tabViews.add(view.findViewById(R.id.glassesLayout));
        tabViews.add(view.findViewById(R.id.hairAccessoriesLayout));
    }

    private void initOkButton(View view) {
        view.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResult(100);
                dismiss();
            }
        });
    }

    private void initCancelButton(View view) {
        view.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }


    private void initSkinLayout(View view) {

        ArrayList<Avatar.Item> skinItems = new ArrayList<>();
        getSkins(skinItems);

        RecyclerView skinRV = view.findViewById(R.id.skinRV);
        skinRV.setOverScrollMode(View.OVER_SCROLL_NEVER);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 5);
        skinRV.setLayoutManager(layoutManager);
        EditAvatarAdapter adapter = new EditAvatarAdapter(getContext(), skinItems, avatar, imageView);
        skinRV.setAdapter(adapter);
    }

    private void initEyesLayout(View view) {

        ArrayList<Avatar.Item> eyesItems = new ArrayList<>();
        getEyes(eyesItems);

        RecyclerView eyesRV = view.findViewById(R.id.eyesRV);
        eyesRV.setOverScrollMode(View.OVER_SCROLL_NEVER);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 5);
        eyesRV.setLayoutManager(layoutManager);
        EditAvatarAdapter adapter = new EditAvatarAdapter(getContext(), eyesItems, avatar, imageView);
        eyesRV.setAdapter(adapter);
    }

    private void initShirtLayout(View view) {

        ArrayList<Avatar.Item> shirtsItems = new ArrayList<>();
        getShirts(shirtsItems);

        RecyclerView shirtsRV = view.findViewById(R.id.shirtsRV);
        shirtsRV.setOverScrollMode(View.OVER_SCROLL_NEVER);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 5);
        shirtsRV.setLayoutManager(layoutManager);
        EditAvatarAdapter adapter = new EditAvatarAdapter(getContext(), shirtsItems, avatar, imageView);
        shirtsRV.setAdapter(adapter);
    }

    private void initHairColorLayout(final View view) {
        RadioGroup hairColorButtons = view.findViewById(R.id.hairColorButtons);
        hairColorButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checked) {

                LayerDrawable layer = (LayerDrawable)imageView.getDrawable();
                Drawable hair = layer.findDrawableByLayerId(R.id.hair);
                Drawable mustache = layer.findDrawableByLayerId(R.id.mustache);

                switch (checked) {
                    case R.id.hairColor1:
                        hair.setTint(getResources().getColor(R.color.hair1));
                        mustache.setTint(getResources().getColor(R.color.hair1));
                        avatar.setHairColor(1);
                        break;
                    case R.id.hairColor2:
                        hair.setTint(getResources().getColor(R.color.hair2));
                        mustache.setTint(getResources().getColor(R.color.hair2));
                        avatar.setHairColor(2);
                        break;
                    case R.id.hairColor3:
                        hair.setTint(getResources().getColor(R.color.hair3));
                        mustache.setTint(getResources().getColor(R.color.hair3));
                        avatar.setHairColor(3);
                        break;
                    case R.id.hairColor4:
                        hair.setTint(getResources().getColor(R.color.hair4));
                        mustache.setTint(getResources().getColor(R.color.hair4));
                        avatar.setHairColor(4);
                        break;
                    case R.id.hairColor5:
                        hair.setTint(getResources().getColor(R.color.hair5));
                        mustache.setTint(getResources().getColor(R.color.hair5));
                        avatar.setHairColor(5);
                        break;
                }

                imageView.setImageDrawable(layer);
                initHairTypeLayout(view);
                initMustacheLayout(view);
            }
        });
    }

    private void initHairTypeLayout(View view) {

        ArrayList<Avatar.Item> hairTypeItems = new ArrayList<>();
        getHairs(hairTypeItems);

        RecyclerView hairRV = view.findViewById(R.id.hairRV);
        hairRV.setOverScrollMode(View.OVER_SCROLL_NEVER);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
        hairRV.setLayoutManager(layoutManager);
        EditAvatarAdapter adapter = new EditAvatarAdapter(getContext(), hairTypeItems, avatar, imageView);
        hairRV.setAdapter(adapter);
    }

    private void initGlassesLayout(View view) {

        ArrayList<Avatar.Item> glassesItems = new ArrayList<>();
        getGlasses(glassesItems);

        if (glassesItems.size() < 2) {
            view.findViewById(R.id.glassesLayout).setVisibility(View.GONE);
            return;
        }

        RecyclerView glassesRV = view.findViewById(R.id.glassesRV);
        glassesRV.setOverScrollMode(View.OVER_SCROLL_NEVER);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 5);
        glassesRV.setLayoutManager(layoutManager);
        EditAvatarAdapter adapter = new EditAvatarAdapter(getContext(), glassesItems, avatar, imageView);
        glassesRV.setAdapter(adapter);
    }

    private void initMustacheLayout(View view) {

        ArrayList<Avatar.Item> mustachesItems = new ArrayList<>();
        getMustaches(mustachesItems);

        if (mustachesItems.size() < 2) {
            view.findViewById(R.id.mustacheLayout).setVisibility(View.GONE);
            return;
        }

        RecyclerView mustacheRV = view.findViewById(R.id.mustacheRV);
        mustacheRV.setOverScrollMode(View.OVER_SCROLL_NEVER);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 5);
        mustacheRV.setLayoutManager(layoutManager);
        EditAvatarAdapter adapter = new EditAvatarAdapter(getContext(), mustachesItems, avatar, imageView);
        mustacheRV.setAdapter(adapter);
    }

    private void initHairAccessoriesLayout(View view) {

        ArrayList<Avatar.Item> hairAccessoriesItems = new ArrayList<>();
        getHairAccessories(hairAccessoriesItems);

        if (hairAccessoriesItems.size() < 2) {
            view.findViewById(R.id.hairAccessoriesLayout).setVisibility(View.GONE);
            return;
        }

        RecyclerView hairAccessoriesRV = view.findViewById(R.id.hairAccessoriesRV);
        hairAccessoriesRV.setOverScrollMode(View.OVER_SCROLL_NEVER);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 5);
        hairAccessoriesRV.setLayoutManager(layoutManager);
        EditAvatarAdapter adapter = new EditAvatarAdapter(getContext(), hairAccessoriesItems, avatar, imageView);
        hairAccessoriesRV.setAdapter(adapter);
    }


    private void getSkins(ArrayList<Avatar.Item> skinItems) {

        skinItems.add(new Avatar.Item(Avatar.ItemType.SKIN, 1));
        skinItems.add(new Avatar.Item(Avatar.ItemType.SKIN, 2));
        skinItems.add(new Avatar.Item(Avatar.ItemType.SKIN, 3));
        skinItems.add(new Avatar.Item(Avatar.ItemType.SKIN, 4));
        skinItems.add(new Avatar.Item(Avatar.ItemType.SKIN, 5));
    }

    private void getEyes(ArrayList<Avatar.Item> eyesItems) {

        for (int i = 1; i < 6; i++) {
            eyesItems.add(new Avatar.Item(Avatar.ItemType.EYES, i));

        }
    }

    private void getHairs(ArrayList<Avatar.Item> hairTypeItems) {

        for (int i = 1; i < 13; i++) {
            hairTypeItems.add(new Avatar.Item(Avatar.ItemType.HAIR, i, avatar.getHairColor()));
        }
    }

    private void getShirts(ArrayList<Avatar.Item> shirtsItems) {

        // Blank t-shirts for everyone
        for (int i = 1; i < 11; i++) {
            shirtsItems.add(new Avatar.Item(Avatar.ItemType.SHIRT, 0, i));
        }

        for (Avatar.Item item : avatar.getUnlockedItems()) {
            if (item.getType() == Avatar.ItemType.SHIRT) {
                shirtsItems.add(item);
            }
        }
    }

    private void getGlasses(ArrayList<Avatar.Item> glassesItems) {

        glassesItems.add(new Avatar.Item(Avatar.ItemType.GLASSES, 0));

        for (Avatar.Item item : avatar.getUnlockedItems()) {
            if (item.getType() == Avatar.ItemType.GLASSES) {
                glassesItems.add(item);
            }
        }
    }

    private void getMustaches(ArrayList<Avatar.Item> mustachesItems) {

        mustachesItems.add(new Avatar.Item(Avatar.ItemType.MUSTACHE, 0));

        for (Avatar.Item item : avatar.getUnlockedItems()) {
            if (item.getType() == Avatar.ItemType.MUSTACHE) {
                item.setColorIndex(avatar.getHairColor());
                mustachesItems.add(item);
            }
        }
    }

    private void getHairAccessories(ArrayList<Avatar.Item> hairAccessoriesItems) {

        hairAccessoriesItems.add(new Avatar.Item(Avatar.ItemType.HAIR_ACCESSORY, 0));

        for (Avatar.Item item : avatar.getUnlockedItems()) {
            if (item.getType() == Avatar.ItemType.HAIR_ACCESSORY) {
                hairAccessoriesItems.add(item);
            }
        }
    }


    private void sendResult(int REQUEST_CODE) {

        Intent intent = new Intent();
        intent.putExtra("Avatar", avatar);
        getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_CODE, intent);
    }

}
