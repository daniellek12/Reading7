package com.reading7;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditAvatarDialog extends AppCompatDialogFragment {

    private ArrayList<Integer> avatar_details;
//    private EditAvatarDialogListener listener;

    private CircleImageView avatar;
    private LayerDrawable layer;

    private Button bodyBtn;
    private Button hairBtn;
    private Button accessoriesBtn;

    private ScrollView bodyLayout;
    private ScrollView hairLayout;
    private ScrollView accessoriesLayout;

    private RadioGroup skinButtons;
    private RadioGroup eyesButtons;
    private RadioGroup hairColorButtons;


    public EditAvatarDialog(ArrayList<Integer> avatar_details) {
        this.avatar_details = avatar_details;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.edit_avatar_dialog, null);

        builder.setView(view).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sendResult(100);
            }
        });

        avatar = view.findViewById(R.id.image);
        layer = (LayerDrawable) avatar.getDrawable();

        Utils.loadAvatar(getContext(), avatar, avatar_details);
        changeHairButtonsColor(view, Utils.getColor(getContext(), "hair" + avatar_details.get(3)));

        initTabs(view);
        initSkinButtons(view);
        initEyesButtons(view);
        initShirtButtons(view);
        initHairColorButtons(view);
        initHairTypeButtons(view);

        return builder.create();
    }


    private void initTabs(View view) {

        bodyBtn = view.findViewById(R.id.bodyBtn);
        hairBtn = view.findViewById(R.id.hairBtn);
        accessoriesBtn = view.findViewById(R.id.accessoriesBtn);

        bodyLayout = view.findViewById(R.id.bodyLayout);
        hairLayout = view.findViewById(R.id.hairLayout);
        accessoriesLayout = view.findViewById(R.id.accessoriesLayout);

        bodyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTab(0);
            }
        });
        hairBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTab(1);
            }
        });
        accessoriesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTab(2);
            }
        });
    }


    private void changeTab(int position) {

        switch (position) {
            case 0:
                bodyLayout.setVisibility(View.VISIBLE);
                hairLayout.setVisibility(View.GONE);
                accessoriesLayout.setVisibility(View.GONE);
                bodyBtn.setTextColor(getResources().getColor(R.color.colorAccent));
                hairBtn.setTextColor(getResources().getColor(R.color.black));
                accessoriesBtn.setTextColor(getResources().getColor(R.color.black));
                break;

            case 1:
                bodyLayout.setVisibility(View.GONE);
                hairLayout.setVisibility(View.VISIBLE);
                accessoriesLayout.setVisibility(View.GONE);
                bodyBtn.setTextColor(getResources().getColor(R.color.black));
                hairBtn.setTextColor(getResources().getColor(R.color.colorAccent));
                accessoriesBtn.setTextColor(getResources().getColor(R.color.black));
                break;

            case 2:
                bodyLayout.setVisibility(View.GONE);
                hairLayout.setVisibility(View.GONE);
                accessoriesLayout.setVisibility(View.VISIBLE);
                bodyBtn.setTextColor(getResources().getColor(R.color.black));
                hairBtn.setTextColor(getResources().getColor(R.color.black));
                accessoriesBtn.setTextColor(getResources().getColor(R.color.colorAccent));
                break;
        }
    }


    private void initSkinButtons(View view) {
        skinButtons = view.findViewById(R.id.skinButtons);
        skinButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checked) {

                switch (checked) {
                    case R.id.skin1:
                        layer.setDrawableByLayerId(R.id.skin, getResources().getDrawable(R.drawable.skin1));
                        avatar_details.set(0, 1);
                        break;
                    case R.id.skin2:
                        layer.setDrawableByLayerId(R.id.skin, getResources().getDrawable(R.drawable.skin2));
                        avatar_details.set(0, 2);
                        break;
                    case R.id.skin3:
                        layer.setDrawableByLayerId(R.id.skin, getResources().getDrawable(R.drawable.skin3));
                        avatar_details.set(0, 3);
                        break;
                    case R.id.skin4:
                        layer.setDrawableByLayerId(R.id.skin, getResources().getDrawable(R.drawable.skin4));
                        avatar_details.set(0, 4);
                        break;
                    case R.id.skin5:
                        layer.setDrawableByLayerId(R.id.skin, getResources().getDrawable(R.drawable.skin5));
                        avatar_details.set(0, 5);
                        break;
                }
                avatar.setImageDrawable(layer);
            }
        });
    }


    private void initEyesButtons(View view) {
        eyesButtons = view.findViewById(R.id.eyesButtons);
        eyesButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checked) {

                Drawable eyes = layer.findDrawableByLayerId(R.id.eyes);
                switch (checked) {
                    case R.id.eyes1:
                        eyes.setTint(getResources().getColor(R.color.eyes1));
                        avatar_details.set(1, 1);
                        break;
                    case R.id.eyes2:
                        eyes.setTint(getResources().getColor(R.color.eyes2));
                        avatar_details.set(1, 2);
                        break;
                    case R.id.eyes3:
                        eyes.setTint(getResources().getColor(R.color.eyes3));
                        avatar_details.set(1, 3);
                        break;
                    case R.id.eyes4:
                        eyes.setTint(getResources().getColor(R.color.eyes4));
                        avatar_details.set(1, 4);
                        break;
                    case R.id.eyes5:
                        eyes.setTint(getResources().getColor(R.color.eyes5));
                        avatar_details.set(1, 5);
                        break;
                }
                avatar.setImageDrawable(layer);
            }
        });
    }


    private void initShirtButtons(View view) {
        for (int i = 1; i < 11; i++) {
            String tag = "shirt" + i;
            ImageButton shirtButton = view.findViewWithTag(tag);
            shirtButton.setOnClickListener(new ShirtColorOnClickListener(i));
        }
    }


    private void initHairColorButtons(final View view) {
        hairColorButtons = view.findViewById(R.id.hairColorButtons);
        hairColorButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checked) {

                Drawable hair = layer.findDrawableByLayerId(R.id.hair);
                switch (checked) {
                    case R.id.hairColor1:
                        hair.setTint(getResources().getColor(R.color.hair1));
                        changeHairButtonsColor(view, getResources().getColor(R.color.hair1));
                        avatar_details.set(3, 1);
                        break;
                    case R.id.hairColor2:
                        hair.setTint(getResources().getColor(R.color.hair2));
                        changeHairButtonsColor(view, getResources().getColor(R.color.hair2));
                        avatar_details.set(3, 2);
                        break;
                    case R.id.hairColor3:
                        hair.setTint(getResources().getColor(R.color.hair3));
                        changeHairButtonsColor(view, getResources().getColor(R.color.hair3));
                        avatar_details.set(3, 3);
                        break;
                    case R.id.hairColor4:
                        hair.setTint(getResources().getColor(R.color.hair4));
                        changeHairButtonsColor(view, getResources().getColor(R.color.hair4));
                        avatar_details.set(3, 4);
                        break;
                    case R.id.hairColor5:
                        hair.setTint(getResources().getColor(R.color.hair5));
                        changeHairButtonsColor(view, getResources().getColor(R.color.hair5));
                        avatar_details.set(3, 5);
                        break;
                }
                avatar.setImageDrawable(layer);
            }
        });
    }


    private void changeHairButtonsColor(View view, int color) {
        for (int i = 1; i < 13; i++) {
            String tag = "hair" + i;
            ImageButton hairButton = view.findViewWithTag(tag);
            hairButton.setColorFilter(color);
        }
    }


    private void initHairTypeButtons(View view) {
        for (int i = 1; i < 13; i++) {
            String tag = "hair" + i;
            ImageButton hairButton = view.findViewWithTag(tag);
            hairButton.setOnClickListener(new HairTypeOnClickListener(hairButton.getDrawable(), i));
        }
    }


    private class HairTypeOnClickListener implements View.OnClickListener {

        Drawable drawable;
        int hair_index;

        public HairTypeOnClickListener(Drawable drawable, int hair_index) {
            this.drawable = drawable;
            this.hair_index = hair_index;
        }

        @Override
        public void onClick(View v) {
            avatar_details.set(2, hair_index);
            layer.setDrawableByLayerId(R.id.hair, drawable);
            avatar.setImageDrawable(layer);
        }
    }


    private class ShirtColorOnClickListener implements View.OnClickListener {

        int color_index;

        public ShirtColorOnClickListener(int color_index) {
            this.color_index = color_index;
        }

        @Override
        public void onClick(View v) {
            avatar_details.set(4, color_index);
            Drawable shirt = layer.findDrawableByLayerId(R.id.shirt);
            shirt.setTint(Utils.getColor(getContext(),"shirt"+color_index));
            avatar.setImageDrawable(layer);
        }
    }


    private void sendResult(int REQUEST_CODE) {

        Intent intent = new Intent();
        intent.putExtra("avatar_details", avatar_details);

        getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_CODE, intent);
    }



//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//
//        try {
//            listener = (EditAvatarDialogListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException();
//        }
//    }

//    public interface EditAvatarDialogListener {
//        void getAvatarDetails(ArrayList<Integer> details);
//    }

}
