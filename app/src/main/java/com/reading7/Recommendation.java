package com.reading7;

public class Recommendation {
    private String user_id;
    private String book_id;

    public Recommendation(String user_id, String book_id){
        this.user_id = user_id;
        this.book_id = book_id;
    }

    public String getBook_id() {
        return book_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
