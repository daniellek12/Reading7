package com.reading7;

public class Recommendation {
    private String user_id;
    private String book_id;
    private double similarity_rating;

    public Recommendation(String user_id, String book_id, double rating){
        this.user_id = user_id;
        this.book_id = book_id;
        this.similarity_rating = rating;
    }

    public String getBook_id() {
        return book_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public double getSimilarity_rating() {
        return similarity_rating;
    }

    //these shouldn't be used, idealy new recommendations will be written from server side

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setSimilarity_rating(double similarity_rating) {
        this.similarity_rating = similarity_rating;
    }
}
