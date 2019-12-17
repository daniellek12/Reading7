package com.reading7;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditAvatarDialog extends AppCompatDialogFragment {

    private CircleImageView avatar;
    private LayerDrawable layer;
    private RadioGroup skinButtons;
    private RadioGroup hairButtons;
    private RadioGroup eyesButtons;  //TODO
    private RadioGroup shirtButtons; //TODO

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_avatar_dialog,null);

        builder.setView(view).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        avatar = view.findViewById(R.id.image);
        layer = (LayerDrawable)avatar.getDrawable();
        skinButtons = view.findViewById(R.id.skinButtons);
        hairButtons = view.findViewById(R.id.hairButtons);
        eyesButtons = view.findViewById(R.id.eyesButtons);
        shirtButtons = view.findViewById(R.id.shirtButtons);

        skinButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checked) {

                Drawable skin = layer.findDrawableByLayerId(R.id.skin);
                switch (checked){
                    case R.id.skin1:
                        layer.setDrawableByLayerId(R.id.skin, getResources().getDrawable(R.drawable.skin1));
                        break;
                    case R.id.skin2:
                        layer.setDrawableByLayerId(R.id.skin, getResources().getDrawable(R.drawable.skin2));
                        break;
                    case R.id.skin3:
                        layer.setDrawableByLayerId(R.id.skin, getResources().getDrawable(R.drawable.skin3));
                        break;
                    case R.id.skin4:
                        layer.setDrawableByLayerId(R.id.skin, getResources().getDrawable(R.drawable.skin4));
                        break;
                    case R.id.skin5:
                        layer.setDrawableByLayerId(R.id.skin, getResources().getDrawable(R.drawable.skin5));
                        break;
                }
                avatar.setImageDrawable(layer);
            }
        });

        hairButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checked) {

                Drawable hair = layer.findDrawableByLayerId(R.id.hair);
                switch (checked){
                    case R.id.hair1:
                        hair.setTint(getResources().getColor(R.color.hair1));
                        break;
                    case R.id.hair2:
                        hair.setTint(getResources().getColor(R.color.hair2));
                        break;
                    case R.id.hair3:
                        hair.setTint(getResources().getColor(R.color.hair3));
                        break;
                    case R.id.hair4:
                        hair.setTint(getResources().getColor(R.color.hair4));
                        break;
                    case R.id.hair5:
                        hair.setTint(getResources().getColor(R.color.hair5));
                        break;
                }
                avatar.setImageDrawable(layer);
            }
        });


        shirtButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checked) {

                Drawable shirt = layer.findDrawableByLayerId(R.id.shirt);
                switch (checked){
                    case R.id.shirt1:
                        shirt.setTint(getResources().getColor(R.color.shirt1));
                        break;
                    case R.id.shirt2:
                        shirt.setTint(getResources().getColor(R.color.shirt2));
                        break;
                    case R.id.shirt3:
                        shirt.setTint(getResources().getColor(R.color.shirt3));
                        break;
                    case R.id.shirt4:
                        shirt.setTint(getResources().getColor(R.color.shirt4));
                        break;
                    case R.id.shirt5:
                        shirt.setTint(getResources().getColor(R.color.shirt5));
                        break;
                }
                avatar.setImageDrawable(layer);
            }
        });

        return builder.create();
    }
}
