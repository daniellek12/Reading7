package com.reading7.Objects;

import com.google.firebase.Timestamp;

import java.util.Comparator;

public class Notification {

    private String type;
    private String from;
    private String book_title;
    private Timestamp time;

    public Notification() {

    }

    public Notification(String type, String from, String book_title, Timestamp time) {
        this.type = type;
        this.from = from;
        this.book_title = book_title;
        this.time = time;
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
                same = (this.book_title.equals(((Notification) object).book_title)) && (this.from.equals(((Notification) object).from));
            if (type.equals("הזמין אותך לקרוא ספר- לחץ כאן כדי לגלות איזה") && (((Notification) object).getType()).equals("הזמין אותך לקרוא ספר- לחץ כאן כדי לגלות איזה"))
                same = (this.book_title.equals(((Notification) object).book_title)) && (this.from.equals(((Notification) object).from));
            if (type.equals("אהב את הביקורת שלך") && (((Notification) object).getType()).equals("אהב את הביקורת שלך"))
                same = (this.book_title.equals(((Notification) object).book_title)) && (this.from.equals(((Notification) object).from));
            else {
                if ((type.equals("הסכים שתעקוב אחריו") && (((Notification) object).getType()).equals("הסכים שתעקוב אחריו")))
                    same = (this.from.equals(((Notification) object).from));
                else if ((!type.equals("הסכים שתעקוב אחריו") && !(((Notification) object).getType()).equals("הסכים שתעקוב אחריו"))
                        && (!type.equals("אהב את הביקורת שלך") && !(((Notification) object).getType()).equals("אהב את הביקורת שלך")) &&
                        (!type.equals("הגיב על הביקורת שלך") && !(((Notification) object).getType()).equals("הגיב על הביקורת שלך"))) {
                    same = (this.from.equals(((Notification) object).from));
                }
            }
        }
        return same;
    }
}