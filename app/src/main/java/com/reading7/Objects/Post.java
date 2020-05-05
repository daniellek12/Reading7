package com.reading7.Objects;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;


public class Post implements Comparable {

    private String post_id;
    private PostType type;
    private Review mReview;         // If type is Review
    private WishList mWishlist;     // If type is Wishlist


/**
     ***************************************** Constructors *************************************
     */

    public Post(Review review) {
        this.type = PostType.Review;
        this.mReview = review;
    }

    public Post(WishList wishList) {
        this.type = PostType.WishList;
        this.mWishlist = wishList;
    }


/**
     *********************************** Getters and Setters ************************************
     */

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


    public Timestamp getPost_time() {
        if (type == PostType.Review)
            return mReview.getReview_time();

        if (type == PostType.WishList)
            return mWishlist.getAdding_time();

        return null;
    }

    public void setPost_time(Timestamp post_time) {

        if (type == PostType.Review)
            mReview.setReview_time(post_time);

        if (type == PostType.WishList)
            mWishlist.setAdding_time(post_time);
    }


    public String getReview_id() {
        if (type == PostType.Review)
            return mReview.getReview_id();

        return null;
    }

    public void setReview_id(String review_id) {
        if (type == PostType.Review)
            mReview.setReview_id(review_id);
    }


    public String getBook_id() {
        if (type == PostType.Review)
            return mReview.getBook_id();

        if (type == PostType.WishList)
            return mWishlist.getBook_id();

        return null;
    }

    public void setBook_id(String book_id) {
        if (type == PostType.Review)
            mReview.setBook_id(book_id);

        if (type == PostType.WishList)
            mWishlist.setBook_id(book_id);
    }


    public String getReview_title() {
        if (type == PostType.Review)
            return mReview.getReview_title();

        return null;
    }

    public void setReview_title(String review_title) {
        if (type == PostType.Review)
            mReview.setReview_title(review_title);
    }


    public String getReviewer_email() {
        if (type == PostType.Review)
            return mReview.getReviewer_email();

        return null;
    }

    public void setReviewer_email(String reviewer_email) {
        if (type == PostType.Review)
            mReview.setReviewer_email(reviewer_email);
    }


    public int getRank() {
        if (type == PostType.Review)
            return mReview.getRank();

        return -1;
    }

    public void setRank(int rank) {
        if (type == PostType.Review)
            mReview.setRank(rank);
    }


    public String getReview_content() {
        if (type == PostType.Review)
            return mReview.getReview_content();

        return null;
    }

    public void setReview_content(String review_content) {
        if (type == PostType.Review)
            mReview.setReview_content(review_content);
    }


    public int getLikes_count() {
        if (type == PostType.Review)
            return mReview.getLikes_count();

        return -1;
    }

    public void setLikes_count(int likes_count) {
        if (type == PostType.Review)
            mReview.setLikes_count(likes_count);
    }

    public void addLike() {
        if (type == PostType.Review)
            mReview.addLike();
    }

    public void reduceLike() {
        if (type == PostType.Review)
            mReview.reduceLike();
    }


    public String getBook_title() {
        if (type == PostType.Review)
            return mReview.getBook_title();

        if (type == PostType.WishList)
            return mWishlist.getBook_title();

        return null;
    }

    public void setBook_title(String book_title) {
        if (type == PostType.Review)
            mReview.setBook_title(book_title);

        if (type == PostType.WishList)
            mWishlist.setBook_title(book_title);
    }


    public String getWishList_Id() {
        if (type == PostType.WishList)
            return mWishlist.getId();

        return null;
    }

    public void setWishList_Id(String wishList_Id) {
        if (type == PostType.WishList)
            mWishlist.setId(wishList_Id);
    }


    public String getUser_email() {
        if (type == PostType.WishList)
            return mWishlist.getUser_email();

        return null;
    }

    public void setUser_email(String user_email) {
        if (type == PostType.WishList)
            mWishlist.setUser_email(user_email);
    }


    public String getBook_author() {
        if (type == PostType.Review)
            return mReview.getBook_author();

        if (type == PostType.WishList)
            return mWishlist.getBook_author();

        return null;
    }

    public void setBook_author(String book_author) {
        if (type == PostType.Review)
            mReview.setBook_author(book_author);

        if (type == PostType.WishList)
            mWishlist.setBook_author(book_author);
    }




    public HashMap<String, Comment> getComments() {
        if (type == PostType.Review)
            return mReview.getComments();

        return null;
    }

    public Review toReview() {
        if (this.type == PostType.Review)
            return mReview;

        return null;
    }


    @Override
    public int compareTo(Object o) {
        if (type == PostType.Recommendation) {
            //there is no time for recommendation and we want to put them in random order in fid so we'll return random value[-1,0,1]
            Random rand = new Random();
            int n = rand.nextInt(3); //[0-2]
            return n - 1;// [-1,0,1]
        }

        return (getPost_time().compareTo(((Post) o).getPost_time()));
    }

    public static class SortByDate implements Comparator<Post> {
        public int compare(Post a, Post b) {
            return -a.getPost_time().compareTo(b.getPost_time()); // reverse, because newer posts are more relevant
        }
    }

}
