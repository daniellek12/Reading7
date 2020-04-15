package com.reading7.Objects;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Comparator;

public class Notification {

    //private String id;
    private String type;
    private String from;
    private String user_name;
    private String book_title;
    private Timestamp time;
    private Avatar user_avatar;

    public Notification() {

    }

    public Notification(String type, String from, String user_name, String book_title, Timestamp time, Avatar user_avatar) {
        this.type = type;
        this.from = from;
        this.user_name = user_name;
        this.book_title = book_title;
        this.time = time;
        this.user_avatar = user_avatar;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getBook_title() {
        return book_title;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public Avatar getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(Avatar user_avatar) {
        this.user_avatar = user_avatar;
    }

    public static class SortByDate implements Comparator<Notification> {
        public int compare(Notification a, Notification b) {
            return -a.time.compareTo(b.time);
        }
    }

    @Override
    public boolean equals(Object object) {
        boolean same = false;

        if (object != null && object instanceof Notification) {

            if (type.equals("הגיב על הביקורת שלך") && (((Notification) object).getType()).equals("הגיב על הביקורת שלך"))
                same = (this.book_title.equals(((Notification) object).book_title)) && (this.user_name.equals(((Notification) object).user_name));
            if (type.equals("הזמין אותך לקרוא ספר- לחץ כאן כדי לגלות איזה") && (((Notification) object).getType()).equals("הזמין אותך לקרוא ספר- לחץ כאן כדי לגלות איזה"))
                same = (this.book_title.equals(((Notification) object).book_title)) && (this.user_name.equals(((Notification) object).user_name));
            if (type.equals("אהב את הביקורת שלך") && (((Notification) object).getType()).equals("אהב את הביקורת שלך"))
                same = (this.book_title.equals(((Notification) object).book_title)) && (this.user_name.equals(((Notification) object).user_name));
            else {
                if ((type.equals("הסכים שתעקוב אחריו") && (((Notification) object).getType()).equals("הסכים שתעקוב אחריו")))
                    same = (this.user_name.equals(((Notification) object).user_name));
                else if ((!type.equals("הסכים שתעקוב אחריו") && !(((Notification) object).getType()).equals("הסכים שתעקוב אחריו"))
                        && (!type.equals("אהב את הביקורת שלך") && !(((Notification) object).getType()).equals("אהב את הביקורת שלך")) &&
                        (!type.equals("הגיב על הביקורת שלך") && !(((Notification) object).getType()).equals("הגיב על הביקורת שלך"))) {
                    same = (this.user_name.equals(((Notification) object).user_name));
                }

            }


        }

        return same;
    }
}