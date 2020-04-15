package com.reading7.Objects;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import com.reading7.R;

import java.io.Serializable;

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
    private int glassesColor;
    private int glassesType;
    private int mustache;
    private int hairAccessory;

    public Avatar(){}

    public Avatar(int skinColor, int eyeColor, int shirtColor, int hairColor, int hairType) {

        this.skinColor = skinColor;
        this.eyeColor = eyeColor;
        this.shirtColor = shirtColor;
        this.hairColor = hairColor;
        this.hairType = hairType;
    }

    public Avatar(int backgroundColor, int skinColor, int eyeColor, int shirtColor, int hairColor, int hairType, int glassesColor, int glassesType, int mustache, int hairAccessory) {
        this.backgroundColor = backgroundColor;
        this.skinColor = skinColor;
        this.eyeColor = eyeColor;
        this.shirtColor = shirtColor;
        this.hairColor = hairColor;
        this.hairType = hairType;
        this.glassesColor = glassesColor;
        this.glassesType = glassesType;
        this.mustache = mustache;
        this.hairAccessory = hairAccessory;
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

    public int getGlassesColor() {
        return glassesColor;
    }

    public void setGlassesColor(int glassesColor) {
        this.glassesColor = glassesColor;
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

    public void loadIntoImage(Context context, CircleImageView image) {

        LayerDrawable layer = (LayerDrawable) getDrawable(context, "avatar_layout");

        String skinString = "skin" + skinColor;
        Drawable skinDrawable = getDrawable(context, skinString);
        layer.setDrawableByLayerId(R.id.skin, skinDrawable);

        String eyesString = "eyes" + eyeColor;
        Drawable eyesDrawable = layer.findDrawableByLayerId(R.id.eyes);
        eyesDrawable.setTint(getColor(context, eyesString));
        layer.setDrawableByLayerId(R.id.eyes, eyesDrawable);

        String hairTypeString = "hair" + hairType;
        Drawable hairTypeDrawable = getDrawable(context, hairTypeString);
        layer.setDrawableByLayerId(R.id.hair, hairTypeDrawable);

        String hairColorString = "hair" + hairColor;
        Drawable hairDrawable = layer.findDrawableByLayerId(R.id.hair);
        hairDrawable.setTint(getColor(context, hairColorString));
        layer.setDrawableByLayerId(R.id.hair, hairDrawable);

        String shirtString = "shirt" + shirtColor;
        Drawable shirtDrawable = layer.findDrawableByLayerId(R.id.shirt);
        shirtDrawable.setTint(getColor(context, shirtString));
        layer.setDrawableByLayerId(R.id.shirt, shirtDrawable);

        image.setImageDrawable(layer);
    }


}
