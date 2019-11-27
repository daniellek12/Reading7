package com.reading7;

import java.sql.Timestamp;

public class Review implements Comparable {

    private String review_id; // random value
    private String book_id; //key for book
    private String reviewer_email; //key for user
    private int rank; //1-5(number of stars)
    private String review_content; // NEED to decide whether we want to limit the length
    private Timestamp review_time;//time the review was published // note - constructor gets miliseconds(System.currentTimeMillis())
    private int likes_count;
    // instead array of comments, we will save collection of comments with the review_id

    //the following fields seems to be pointless because we can bring the information from
    //the user db with query on the reviewer_email, but the query will take time and we are
    //going to load this info on the reviewer a lot of times, so the query will be called
    //too much times, from our experience, the best solution is to save the fields for Request too.
    //This fields will be all the data on the user we want to present
    private String reviewer_name;
    private String reviewer_age; // age or grade, depands on the User fields
    private String reviewer_city;

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    //fields of book for Post class
    private String book_name; // for PostClass


    public Review(String review_id, String book_id, String reviewer_email, int rank, String review_content, Timestamp review_time, int likes_count, String reviewer_name, String reviewer_age, String reviewer_city, String book_name) {
        this.review_id = review_id;
        this.book_id = book_id;
        this.reviewer_email = reviewer_email;
        this.rank = rank;
        this.review_content = review_content;
        this.review_time = review_time;
        this.likes_count = likes_count;
        this.reviewer_name = reviewer_name;
        this.reviewer_age = reviewer_age;
        this.reviewer_city = reviewer_city;
        this.book_name = book_name;

    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }
    public String getReview_id() {
        return review_id;
    }

    public void setReview_id(String review_id) {
        this.review_id = review_id;
    }

    public String getReviewer_email() {
        return reviewer_email;
    }

    public void setReviewer_email(String reviewer_email) {
        this.reviewer_email = reviewer_email;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getReview_content() {
        return review_content;
    }

    public void setReview_content(String review_content) {
        this.review_content = review_content;
    }

    public Timestamp getReview_time() {
        return review_time;
    }

    public void setReview_time(Timestamp review_time) {
        this.review_time = review_time;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    public String getReviewer_name() {
        return reviewer_name;
    }

    public void setReviewer_name(String reviewer_name) {
        this.reviewer_name = reviewer_name;
    }

    public String getReviewer_age() {
        return reviewer_age;
    }

    public void setReviewer_age(String reviewer_age) {
        this.reviewer_age = reviewer_age;
    }

    public String getReviewer_city() {
        return reviewer_city;
    }

    public void setReviewer_city(String reviewer_city) {
        this.reviewer_city = reviewer_city;
    }

    @Override
    public int compareTo(Object o) {
        return (review_time.compareTo(((Review)o).review_time));
    }

}
