package com.reading7.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.reading7.Objects.Avatar;
import com.reading7.R;
import com.reading7.Utils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditAvatarAdapter extends RecyclerView.Adapter<EditAvatarAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Avatar.Item> items;
    private Avatar avatar;
    private CircleImageView image;


    public EditAvatarAdapter(Context mContext, ArrayList<Avatar.Item> items, Avatar avatar, CircleImageView image) {
        this.mContext = mContext;
        this.items = items;
        this.avatar = avatar;
        this.image = image;
    }

    @NonNull
    @Override
    public EditAvatarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.edit_avatar_item, viewGroup, false);
        return new EditAvatarAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditAvatarAdapter.ViewHolder holder, final int position) {

        final Avatar.Item item = items.get(position);

        holder.image.setImageDrawable(item.findDisplayDrawable(mContext));
        if (isCurrent(item))
            holder.image.setBackground(Utils.getDrawable(mContext, "edittext_border_background_light"));
        else
            holder.image.setBackground(null);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAvatar(item);
                avatar.loadIntoImage(mContext, image);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
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

    private boolean isCurrent(Avatar.Item item) {

        switch (item.getType()) {
            case SKIN:
                return avatar.getSkinColor() == item.getDrawableIndex();
            case EYES:
                return avatar.getEyeColor() == item.getDrawableIndex();
            case HAIR:
                return avatar.getHairType() == item.getDrawableIndex();
            case MUSTACHE:
                return avatar.getMustache() == item.getDrawableIndex();
            case SHIRT:
                if (item.getDrawableIndex() < 7 && item.getDrawableIndex() > 0)
                    return avatar.getShirtType() == item.getDrawableIndex();
                return (avatar.getShirtType() == item.getDrawableIndex() &&
                        avatar.getShirtColor() == item.getColorIndex());
            case GLASSES:
                return avatar.getGlassesType() == item.getDrawableIndex();
            case HAIR_ACCESSORY:
                return avatar.getHairAccessory() == item.getDrawableIndex();
        }

        return false;
    }

}
