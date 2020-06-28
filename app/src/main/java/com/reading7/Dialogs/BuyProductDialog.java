package com.reading7.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.firestore.FirebaseFirestore;
import com.reading7.EditProfileFragment;
import com.reading7.MainActivity;
import com.reading7.Objects.Avatar;
import com.reading7.Objects.Product;
import com.reading7.Objects.User;
import com.reading7.ProfileFragment;
import com.reading7.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class BuyProductDialog extends AppCompatDialogFragment {

    private Avatar avatar;
    private Product product;
    private View dialogView;

    public BuyProductDialog(Product product) {
        this.product = product;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialogView = getActivity().getLayoutInflater().inflate(R.layout.buy_product_dialog, null);

        initAvatarImage();
        initOkButton();
        initCancelButton();

        return builder.setView(dialogView).create();
    }


    private void initAvatarImage() {

        avatar = new Avatar(((MainActivity) getActivity()).getCurrentUser().getAvatar());
        updateAvatar(product.getItem());
        avatar.loadIntoImage(getContext(), (CircleImageView) dialogView.findViewById(R.id.profileImage));

    }

    private void initOkButton() {

        ((Button) dialogView.findViewById(R.id.buyButton)).setText(String.valueOf(product.getPrice()));
        dialogView.findViewById(R.id.buyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = ((MainActivity) getActivity()).getCurrentUser();
                if (user.getPoints() >= product.getPrice()) {
                    Avatar avatar = user.getAvatar();
                    avatar.unlockItem(product.getItem());
                    user.setAvatar(avatar);
                    user.reducePoints(product.getPrice());

                    ((MainActivity) getActivity()).setCurrentUser(user);
                    FirebaseFirestore.getInstance().collection("Users").document(user.getEmail()).update("avatar", avatar, "points", user.getPoints());
                    sendResult(404);
                    showConfirmationLayout();
                }
            }
        });
    }

    private void showConfirmationLayout() {
        dialogView.findViewById(R.id.boughtLayout).setVisibility(View.VISIBLE);
        dialogView.findViewById(R.id.showProductLayout).setVisibility(View.GONE);

        dialogView.findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        dialogView.findViewById(R.id.goToCloset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                ((MainActivity)getActivity()).addFragment(new EditProfileFragment(true));
            }
        });
    }

    private void initCancelButton() {
        dialogView.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void updateAvatar(Avatar.Item item) {

        switch (item.getType()) {
            case SKIN:
                avatar.setSkinColor(item.getDrawableIndex());
                break;
            case EYES:
                avatar.setEyeColor(item.getDrawableIndex());
                break;
            case HAIR:
                avatar.setHairType(item.getDrawableIndex());
                break;
            case SHIRT:
                if (item.getColorIndex() != 0)
                    avatar.setShirtColor(item.getColorIndex());
                avatar.setShirtType(item.getDrawableIndex());
                break;
            case GLASSES:
                avatar.setGlassesType(item.getDrawableIndex());
                break;
            case MUSTACHE:
                avatar.setMustache(item.getDrawableIndex());
                break;
            case HAIR_ACCESSORY:
                avatar.setHairAccessory(item.getDrawableIndex());
                break;
            default:
                break;
        }
    }


    public void sendResult(int REQUEST_CODE) {
        if (getTargetFragment() != null)
            getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_CODE, new Intent());
    }

}
