package com.reading7.Objects;

import java.util.ArrayList;

public class Comment implements Comparable {

    // id of comment defined by (review_id, commenter_email)
    private String review_id;
    private String commenter_email;
    private com.google.firebase.Timestamp comment_time;
    private String comment_content;

    public Comment() {}

    public Comment(String review_id,
                   String commenter_email,
                   String comment_content,
                   com.google.firebase.Timestamp comment_time) {
        this.review_id = review_id;
        this.commenter_email = commenter_email;
        this.comment_content = comment_content;
        this.comment_time = comment_time;
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
        this.commenter_email=  commenter_email;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public com.google.firebase.Timestamp getComment_time() {
        return comment_time;
    }

    public void setComment_time(com.google.firebase.Timestamp  comment_time) {
        this.comment_time = comment_time;
    }





    @Override
    public int compareTo(Object o) {
        return (comment_time.compareTo(((Comment)o).comment_time));
    }

}
