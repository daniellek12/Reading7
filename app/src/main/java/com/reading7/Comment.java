package com.reading7;

import java.sql.Timestamp;

public class Comment implements Comparable {

    private String comment_id; // random value
    private String review_id;//the id of the relevant review
    private String commenter_email;//key of user
    private String comment_content;
    private Timestamp comment_time; // time the comment published
    //private int likes_count; // NEED consult with team because im afraid it will load a lot of time each time user likes something

    //the following fields seems to be pointless because we can bring the information from
    //the user db with query on the commenter_email, but the query will take time and we are
    //going to load this info on the commenter a lot of times, so the query will be called
    //too much times, from our experience, the best solution is to save the fields for Comment too.
    //This fields will be all the data on the user we want to present
    private String commenter_name;
    private String commenter_age; // age or grade, depands on the User fields
    private String commenter_school_name;

    public Comment(String comment_id, String review_id, String commenter_email, String comment_content, Timestamp comment_time, String commenter_name, String commenter_age, String commenter_school_name) {
        this.comment_id = comment_id;
        this.review_id = review_id;
        this.commenter_email = commenter_email;
        this.comment_content = comment_content;
        this.comment_time = comment_time;
        //this.likes_count = likes_count;
        this.commenter_name = commenter_name;
        this.commenter_age = commenter_age;
        this.commenter_school_name = commenter_school_name;
    }

    public String getCommenter_school_name() {
        return commenter_school_name;
    }

    public void setCommenter_school_name(String commenter_school_name) {
        this.commenter_school_name = commenter_school_name;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getReview_id() {
        return review_id;
    }

    public void setReview_id(String review_id) {
        this.review_id = review_id;
    }

    public String getCommenter_email() {
        return commenter_email;
    }

    public void setCommenter_email(String commenter_email) {
        this.commenter_email = commenter_email;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public Timestamp getComment_time() {
        return comment_time;
    }

    public void setComment_time(Timestamp comment_time) {
        this.comment_time = comment_time;
    }


    public String getCommenter_name() {
        return commenter_name;
    }

    public void setCommenter_name(String commenter_name) {
        this.commenter_name = commenter_name;
    }

    public String getCommenter_age() {
        return commenter_age;
    }

    public void setCommenter_age(String commenter_age) {
        this.commenter_age = commenter_age;
    }


    @Override
    public int compareTo(Object o) {
        return (comment_time.compareTo(((Comment)o).comment_time));
    }
}
