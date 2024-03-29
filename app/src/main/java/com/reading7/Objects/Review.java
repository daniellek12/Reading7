package com.reading7.Objects;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;

public class Review implements Comparable {

    private String review_id;           // random value
    private String book_id;             // key for book
    private String book_title;
    private String book_author;
    private String reviewer_email;      // key for user
    private int rank;                   // 1-5(number of stars)
    private String review_title;
    private String review_content;      // NEED to decide whether we want to limit the length
    private Timestamp review_time;      // time the review was published // note - constructor gets miliseconds(System.currentTimeMillis())
    private int likes_count;
    private int reviewer_age;           // age and not birth year needs to be static
    private ArrayList<Comment> comments;

    // instead array of comments, we will save collection of comments with the review_id

    // some fields seem to be pointless because we can bring the information from
    // the user db with query on the reviewer_email, but the query will take time and we are
    // going to load this info on the reviewer a lot of times, so the query will be called
    // too much times, from our experience, the best solution is to save the fields for Request too.
    // This fields will be all the data on the user we want to present


    public Review() {
    }

    public Review(String review_id,
                  String book_id,
                  String reviewer_email,
                  int reviewer_age,
                  int rank,
                  String review_title,
                  String review_content,
                  Timestamp review_time,
                  String book_title,
                  String book_author) {
        this.review_id = review_id;
        this.book_id = book_id;
        this.book_title = book_title;
        this.book_author = book_author;
        this.reviewer_email = reviewer_email;
        this.rank = rank;
        this.review_title = review_title;
        this.review_content = review_content;
        this.review_time = review_time;
        this.likes_count = 0;
        this.reviewer_age = reviewer_age;
        this.comments = new ArrayList<Comment>();
    }


    public String getBook_title() {
        return book_title;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
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


    public String getReview_title() {
        return review_title;
    }

    public void setReview_title(String review_title) {
        this.review_title = review_title;
    }


    public int getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    public void addLike() {this.likes_count ++;}

    public void reduceLike() {this.likes_count --;}


    public int getReviewer_age() {
        return reviewer_age;
    }

    public void setReviewer_age(int reviewer_age) {
        this.reviewer_age = reviewer_age;
    }


    public String getBook_author() {
        return book_author;
    }

    public void setBook_author(String book_author) {
        this.book_author = book_author;
    }



    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void removeComment(Comment comment) {
        if (comments.contains(comment))
            comments.remove(comment);
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public ArrayList<Comment> getComments() {
        return this.comments;
    }


    @Override
    public int compareTo(Object o) {
        return (review_time.compareTo(((Review) o).review_time));
    }

}
