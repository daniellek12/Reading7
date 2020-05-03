package com.reading7.Objects;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import com.reading7.R;
import com.reading7.Utils;

import java.io.Serializable;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.reading7.Utils.getColor;
import static com.reading7.Utils.getDrawable;

public class Avatar implements Serializable {

    private int backgroundColor;
    private int skinColor;
    private int eyeColor;
    private int shirtColor;
    private int hairColor;
    private int hairType;
    private int shirtType;
    private int glassesType;
    private int mustache;
    private int hairAccessory;
    private ArrayList<Item> unlockedItems = new ArrayList<Item>();     // Items bought in the gift shop

    public Avatar() {
    }

    public Avatar(int skinColor, int eyeColor, int shirtColor, int hairColor, int hairType) {

        this.skinColor = skinColor;
        this.eyeColor = eyeColor;
        this.shirtColor = shirtColor;
        this.hairColor = hairColor;
        this.hairType = hairType;
        this.unlockedItems = new ArrayList<Item>();
    }

    public Avatar(int backgroundColor, int skinColor, int eyeColor, int shirtColor, int hairColor,
                  int hairType, int shirtType, int glassesType, int mustache, int hairAccessory) {
        this.backgroundColor = backgroundColor;
        this.skinColor = skinColor;
        this.eyeColor = eyeColor;
        this.shirtColor = shirtColor;
        this.hairColor = hairColor;
        this.hairType = hairType;
        this.shirtType = shirtType;
        this.glassesType = glassesType;
        this.mustache = mustache;
        this.hairAccessory = hairAccessory;
        this.unlockedItems = new ArrayList<Item>();

    }

    public Avatar(int backgroundColor, int skinColor, int eyeColor, int shirtColor, int hairColor,
                  int hairType, int shirtType, int glassesType, int mustache, int hairAccessory,
                  ArrayList<Item> unlockedItems) {
        this.backgroundColor = backgroundColor;
        this.skinColor = skinColor;
        this.eyeColor = eyeColor;
        this.shirtColor = shirtColor;
        this.hairColor = hairColor;
        this.hairType = hairType;
        this.shirtType = shirtType;
        this.glassesType = glassesType;
        this.mustache = mustache;
        this.hairAccessory = hairAccessory;
        this.unlockedItems = unlockedItems;
    }

    public Avatar(Avatar avatar) {
        this.backgroundColor = avatar.getBackgroundColor();
        this.skinColor = avatar.getSkinColor();
        this.eyeColor = avatar.getEyeColor();
        this.shirtColor = avatar.getShirtColor();
        this.hairColor = avatar.getHairColor();
        this.hairType = avatar.getHairType();
        this.shirtType = avatar.getShirtType();
        this.glassesType = avatar.getGlassesType();
        this.mustache = avatar.getMustache();
        this.hairAccessory = avatar.getHairAccessory();
        this.unlockedItems = avatar.getUnlockedItems();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getSkinColor() {
        return skinColor;
    }

    public void setSkinColor(int skinColor) {
        this.skinColor = skinColor;
    }

    public int getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(int eyeColor) {
        this.eyeColor = eyeColor;
    }

    public int getShirtColor() {
        return shirtColor;
    }

    public void setShirtColor(int shirtColor) {
        this.shirtColor = shirtColor;
    }

    public int getHairColor() {
        return hairColor;
    }

    public void setHairColor(int hairColor) {
        this.hairColor = hairColor;
    }

    public int getHairType() {
        return hairType;
    }

    public void setHairType(int hairType) {
        this.hairType = hairType;
    }

    public int getShirtType() {
        return shirtType;
    }

    public void setShirtType(int shirtType) {
        this.shirtType = shirtType;
    }

    public int getGlassesType() {
        return glassesType;
    }

    public void setGlassesType(int glassesType) {
        this.glassesType = glassesType;
    }

    public int getMustache() {
        return mustache;
    }

    public void setMustache(int mustache) {
        this.mustache = mustache;
    }

    public int getHairAccessory() {
        return hairAccessory;
    }

    public void setHairAccessory(int hairAccessory) {
        this.hairAccessory = hairAccessory;
    }

    public ArrayList<Item> getUnlockedItems() {
        return unlockedItems;
    }

    public void setUnlockedItems(ArrayList<Item> unlockedItems) {
        this.unlockedItems = unlockedItems;
    }

    public void unlockItem(Item item) {
        if (!unlockedItems.contains(item))
            unlockedItems.add(item);
    }

    public void loadIntoImage(Context context, CircleImageView image) {

        LayerDrawable layer = (LayerDrawable) getDrawable(context, "avatar_layout");

        Drawable skinDrawable = getDrawable(context, "skin" + skinColor);
        layer.setDrawableByLayerId(R.id.skin, skinDrawable);

        Drawable eyesDrawable = getDrawable(context, "eyes");
        eyesDrawable.setTint(getColor(context, "eyes" + eyeColor));
        layer.setDrawableByLayerId(R.id.eyes, eyesDrawable);

        Drawable hairTypeDrawable = getDrawable(context, "hair" + hairType);
        if (hairAccessory >= 5 && hairAccessory <= 10) // hat
            hairTypeDrawable = getDrawable(context, "hat_hair" + hairType);
        layer.setDrawableByLayerId(R.id.hair, hairTypeDrawable);

        Drawable hairDrawable = layer.findDrawableByLayerId(R.id.hair);
        hairDrawable.setTint(getColor(context, "hair" + hairColor));
        layer.setDrawableByLayerId(R.id.hair, hairDrawable);

        LayerDrawable shirtLayer = (LayerDrawable) getDrawable(context, "shirt").getConstantState().newDrawable().mutate();

        if (shirtType != 0) {
            Drawable shirtTypeDrawable = getDrawable(context, "shirt" + shirtType);
            shirtLayer.setDrawableByLayerId(R.id.type, shirtTypeDrawable);
        } else
            shirtLayer.setDrawableByLayerId(R.id.type, getDrawable(context, "blank"));

        Drawable shirtDrawable = shirtLayer.findDrawableByLayerId(R.id.color);
        shirtDrawable.setTint(getColor(context, "shirt" + shirtColor));
        shirtLayer.setDrawableByLayerId(R.id.color, shirtDrawable);

        layer.setDrawableByLayerId(R.id.shirt, shirtLayer);

        if (glassesType != 0) {
            Drawable glassesDrawable = getDrawable(context, "glasses" + glassesType);
            layer.setDrawableByLayerId(R.id.glasses, glassesDrawable);
        } else
            layer.setDrawableByLayerId(R.id.glasses, getDrawable(context, "blank"));

        if (mustache != 0) {
            Drawable mustacheDrawable = getDrawable(context, "mustache" + mustache);
            mustacheDrawable.setTint(getColor(context, "hair" + hairColor));
            layer.setDrawableByLayerId(R.id.mustache, mustacheDrawable);
        } else
            layer.setDrawableByLayerId(R.id.mustache, getDrawable(context, "blank"));

        if (hairAccessory != 0) {
            Drawable hairAccessoryDrawable = getDrawable(context, "hair_accessory" + hairAccessory);
            layer.setDrawableByLayerId(R.id.hair_accessory, hairAccessoryDrawable);
        } else
            layer.setDrawableByLayerId(R.id.hair_accessory, getDrawable(context, "blank"));

        image.setImageDrawable(layer);
    }


    /**
     * ************************************** Class Item ********************************************
     */

    public enum ItemType {SKIN, EYES, HAIR, BACKGROUND, SHIRT, GLASSES, MUSTACHE, HAIR_ACCESSORY}

    public static class Item {

        private ItemType type;
        private int drawableIndex;  // e.g. drawableIndex=3 for shirt3
        private int colorIndex;     // If defining a color is needed

        public Item() {
        }

        public Item(ItemType type, int drawableIndex) {
            this.type = type;
            this.drawableIndex = drawableIndex;
            this.colorIndex = 0;
        }

        public Item(ItemType type, int drawableIndex, int colorIndex) {
            this.type = type;
            this.drawableIndex = drawableIndex;
            this.colorIndex = colorIndex;
        }

        public ItemType getType() {
            return type;
        }

        public void setType(ItemType type) {
            this.type = type;
        }

        public int getDrawableIndex() {
            return drawableIndex;
        }

        public void setDrawableIndex(int drawableIndex) {
            this.drawableIndex = drawableIndex;
        }

        public int getColorIndex() {
            return colorIndex;
        }

        public void setColorIndex(int colorIndex) {
            this.colorIndex = colorIndex;
        }

        public Drawable findDisplayDrawable(Context context) {

            Drawable drawable = null;
            switch (type) {
                case SKIN:
                    return Utils.getDrawable(context, "skin" + drawableIndex);
                case EYES:
                    drawable = getDrawable(context, "circle").getConstantState().newDrawable().mutate();
                    drawable.setTint(getColor(context, "eyes" + drawableIndex));
                    return drawable;
                case HAIR:
                    return getHairIconDrawable(context);
                case SHIRT:
                    return getShirtIconDrawable(context);
                case GLASSES:
                    return getGlassesIconDrawable(context);
                case MUSTACHE:
                    return getMustacheIconDrawable(context);
                case HAIR_ACCESSORY:
                    return getHairAccessoryIconDrawable(context);
                case BACKGROUND:
                    return Utils.getDrawable(context, "circle");
            }

            return drawable;
        }

        public Drawable findAvatarDrawable(Context context) {

            Drawable drawable = null;

            switch (type) {
                case SKIN:
                    return Utils.getDrawable(context, "skin" + drawableIndex);
                case EYES:
                    drawable = getDrawable(context, "eyes").getConstantState().newDrawable().mutate();
                    drawable.setTint(getColor(context, "eyes" + drawableIndex));
                    return drawable;
                case HAIR:
                    return getHairAvatarDrawable(context);
                case SHIRT:
                    return getShirtAvatarDrawable(context);
                case GLASSES:
                    return getGlassesAvatarDrawable(context);
                case MUSTACHE:
                    return getMustacheAvatarDrawable(context);
                case HAIR_ACCESSORY:
                    return getHairAccessoryAvatarDrawable(context);
                case BACKGROUND:
                    return Utils.getDrawable(context, "circle");
            }

            return drawable;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item item = (Item) o;
            return drawableIndex == item.drawableIndex &&
                    colorIndex == item.colorIndex &&
                    type == item.type;
        }


        private Drawable getShirtIconDrawable(Context context) {

            LayerDrawable layer = (LayerDrawable) getDrawable(context, "shirt_icon").getConstantState().newDrawable().mutate();

            Drawable shirtTypeDrawable = getDrawable(context, "blank");
            if (drawableIndex != 0)
                shirtTypeDrawable = getDrawable(context, "shirt" + drawableIndex + "_icon");
            layer.setDrawableByLayerId(R.id.type, shirtTypeDrawable);

            Drawable shirtDrawable = layer.findDrawableByLayerId(R.id.color);
            if (colorIndex != 0) shirtDrawable.setTint(getColor(context, "shirt" + colorIndex));
            else shirtDrawable.setTint(getColor(context, "transparent"));

            layer.setDrawableByLayerId(R.id.color, shirtDrawable);

            return layer;
        }

        private Drawable getShirtAvatarDrawable(Context context) {

            LayerDrawable layer = (LayerDrawable) getDrawable(context, "shirt").getConstantState().newDrawable().mutate();

            Drawable shirtTypeDrawable = getDrawable(context, "blank");
            if (drawableIndex != 0)
                shirtTypeDrawable = getDrawable(context, "shirt" + drawableIndex);
            layer.setDrawableByLayerId(R.id.type, shirtTypeDrawable);

            if (colorIndex != 0) {
                Drawable shirtDrawable = layer.findDrawableByLayerId(R.id.color);
                shirtDrawable.setTint(getColor(context, "shirt" + colorIndex));
                layer.setDrawableByLayerId(R.id.color, shirtDrawable);
            }

            return layer;
        }

        private Drawable getHairIconDrawable(Context context) {

            LayerDrawable layer = (LayerDrawable) getDrawable(context, "head_item_icon").getConstantState().newDrawable().mutate();

            String hairTypeString = "hair" + drawableIndex;
            Drawable hairDrawable = getDrawable(context, hairTypeString);

            String hairColorString = "hair" + colorIndex;
            hairDrawable.setTint(getColor(context, hairColorString));

            layer.setDrawableByLayerId(R.id.item, hairDrawable);

            return layer;
        }

        private Drawable getHairAvatarDrawable(Context context) {

            String hairTypeString = "hair" + drawableIndex;
            Drawable drawable = getDrawable(context, hairTypeString);

            String hairColorString = "hair" + colorIndex;
            drawable.setTint(getColor(context, hairColorString));

            return drawable;
        }

        private Drawable getMustacheIconDrawable(Context context) {

            if (drawableIndex == 0)
                return Utils.getDrawable(context, "blank_avatar");

            LayerDrawable layer = (LayerDrawable) getDrawable(context, "head_item_icon").getConstantState().newDrawable().mutate();

            String mustacheTypeString = "mustache" + drawableIndex;
            Drawable mustacheDrawable = getDrawable(context, mustacheTypeString);

            String mustacheColorString = "hair" + colorIndex; // Same color as the hair
            mustacheDrawable.setTint(getColor(context, mustacheColorString));

            layer.setDrawableByLayerId(R.id.item, mustacheDrawable);

            return layer;
        }

        private Drawable getMustacheAvatarDrawable(Context context) {

            if (drawableIndex == 0)
                return Utils.getDrawable(context, "blank");

            Drawable drawable = getDrawable(context, "mustache" + drawableIndex);

            String mustacheColorString = "hair" + colorIndex; // Same color as the hair
            drawable.setTint(getColor(context, mustacheColorString));

            return drawable;
        }

        private Drawable getGlassesIconDrawable(Context context) {

            if (drawableIndex == 0)
                return Utils.getDrawable(context, "blank_avatar");

            LayerDrawable layer = (LayerDrawable) getDrawable(context, "head_item_icon").getConstantState().newDrawable().mutate();
            Drawable glassesDrawable = getDrawable(context, "glasses" + drawableIndex);
            layer.setDrawableByLayerId(R.id.item, glassesDrawable);

            return layer;
        }

        private Drawable getGlassesAvatarDrawable(Context context) {

            if (drawableIndex == 0)
                return Utils.getDrawable(context, "blank");

            return getDrawable(context, "glasses" + drawableIndex);
        }

        private Drawable getHairAccessoryIconDrawable(Context context) {

            if (drawableIndex == 0)
                return Utils.getDrawable(context, "blank_avatar");

            LayerDrawable layer = (LayerDrawable) getDrawable(context, "head_item_icon").getConstantState().newDrawable().mutate();
            Drawable hairAccessoryDrawable = getDrawable(context, "hair_accessory" + drawableIndex);
            layer.setDrawableByLayerId(R.id.item, hairAccessoryDrawable);

            return layer;
        }

        private Drawable getHairAccessoryAvatarDrawable(Context context) {

            if (drawableIndex == 0)
                return Utils.getDrawable(context, "blank");

            return getDrawable(context, "hair_accessory" + drawableIndex);
        }

    }

}
