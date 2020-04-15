package com.reading7.Objects;


import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class WishList implements Comparable {

    private String id;
    private Timestamp adding_time;
    private String book_id;
    private String book_title;
    private String book_author;
    private String user_email;
    private String user_name;
    private Avatar user_avatar;


    public WishList() {

    }

    public WishList(String id, String user_email, String user_name, String book_id, String book_name, String book_author, Timestamp adding_time, Avatar user_avatar) {
        this.id = id;
        this.adding_time = adding_time;
        this.book_id = book_id;
        this.book_title = book_name;
        this.book_author = book_author;
        this.user_email = user_email;
        this.user_name = user_name;
        this.user_avatar = user_avatar;
    }


    public String getBook_title() {
        return book_title;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }


    public String getBook_author() {
        return book_author;
    }

    public void setBook_author(String book_author) {
        this.book_author = book_author;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }


    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }


    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }


    public Timestamp getAdding_time() {
        return adding_time;
    }

    public void setAdding_time(Timestamp adding_time) {
        this.adding_time = adding_time;
    }


    public Avatar getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(Avatar user_avatar) {
        this.user_avatar = user_avatar;
    }


    @Override
    public int compareTo(Object o) {
        return (adding_time.compareTo(((WishList)o).adding_time));
    }

}
