package com.reading7;

import java.sql.Timestamp;
import java.util.Random;

enum PostType { // next to each type are the relevant fields from Post Class
    Review, //user_email, book_name,book_id,rank ,review_content ,post_time ,likes_count ,reviewer_name, reviewer_age ,reviewer_city
    WishList, //user_email,user name,user age,user city,book_id,post_time,book_name
    NewBook, // book_id,book_name, post_time, NEED to add another fields of book
    Recommendation, //book_id,book_name,NEED to add another fields of book
}

public class Post implements Comparable {

    private String post_id;
    private PostType type;

    //according to the type we know which values are relevant
    private String user_email; // email of the person who did this activity
    private int rank;
    private String book_id;
    private String book_name;
    private String review_content;
    private Timestamp post_time;
    private int likes_count;
    private String reviewer_name;
    private int reviewer_age;
    private String reviewer_city;
    // NEED another fields of book such as genre.image...

    public Post(String post_id, PostType type, String user_email, int rank, String book_id, String book_name, String review_content, Timestamp post_time, int likes_count, String reviewer_name, int reviewer_age, String reviewer_city) {
        this.post_id = post_id;
        this.type = type;
        this.user_email = user_email;
        this.rank = rank;
        this.book_id = book_id;
        this.book_name = book_name;
        this.review_content = review_content;
        this.post_time = post_time;
        this.likes_count = likes_count;
        this.reviewer_name = reviewer_name;
        this.reviewer_age = reviewer_age;
        this.reviewer_city = reviewer_city;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public PostType getType() {
        return type;
    }

    public void setType(PostType type) {
        this.type = type;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getReview_content() {
        return review_content;
    }

    public void setReview_content(String review_content) {
        this.review_content = review_content;
    }

    public Timestamp getPost_time() {
        return post_time;
    }

    public void setPost_time(Timestamp post_time) {
        this.post_time = post_time;
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

    public int getReviewer_age() {
        return reviewer_age;
    }

    public void setReviewer_age(int reviewer_age) {
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
        if(type == PostType.Recommendation){
            //there is no time for recommandation and we want to put them in random order in fid so we'll return random value[-1,0,1]
            Random rand = new Random();
            int n = rand.nextInt(3); //[0-2]
            return n-1;// [-1,0,1]
        }
        return (post_time.compareTo(((Post)o).post_time));
    }
}
