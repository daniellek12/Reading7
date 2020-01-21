package com.reading7.Objects;

import java.util.ArrayList;

public class Comment implements Comparable {

    // id of comment defined by (review_id, commenter_email)
    private String review_id;
    private String commenter_email;
    private String commenter_name;
    private ArrayList<Integer> commenter_avatar;
    private com.google.firebase.Timestamp comment_time;
    private String comment_content;
    private boolean is_notify; // check if needed?

    public Comment() {}

    public Comment(String review_id,
                   String commenter_email,
                   String comment_content,
                   com.google.firebase.Timestamp comment_time,
                   String commenter_name,
                   ArrayList<Integer> commenter_avatar,
                   boolean is_notify) {
        this.review_id = review_id;
        this.commenter_email = commenter_email;
        this.comment_content = comment_content;
        this.commenter_name = commenter_name;
        this.comment_time = comment_time;
        this.commenter_avatar = commenter_avatar;
        this.is_notify = is_notify;
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

    public String getCommenter_name() {
        return commenter_name;
    }

    public void setCommenter_name(String commenter_name) {
        this.commenter_name = commenter_name;
    }

    public ArrayList<Integer> getCommenter_avatar() {
        return commenter_avatar;
    }

    public void setCommenter_avatar(ArrayList<Integer> reviewer_avatar) {
        this.commenter_avatar = reviewer_avatar;
    }

    public boolean getIs_notify() {
        return is_notify;
    }

    public void setIs_notify(boolean is_notify) {
        this.is_notify = is_notify;
    }

    @Override
    public int compareTo(Object o) {
        return (comment_time.compareTo(((Comment)o).comment_time));
    }

}
