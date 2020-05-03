package com.reading7.Objects;

public class Product {

    private int price;
    private Avatar.Item item;

    public Product(int price, Avatar.Item item) {
        this.price = price;
        this.item = item;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Avatar.Item getItem() {
        return item;
    }

    public void setItem(Avatar.Item item) {
        this.item = item;
    }

}
