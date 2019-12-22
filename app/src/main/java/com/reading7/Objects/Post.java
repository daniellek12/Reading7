package com.reading7.Objects;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;


public class Post implements Comparable {

    private String post_id;
    private PostType type;
    private Timestamp post_time;
    private String book_id;
    private String book_title;
    private String book_author;


    /* Review */
    private String review_id;
    private String reviewer_email;
    private int rank;
    private ArrayList<Integer> user_avatar;

    private String review_title;
    private String review_content;
    //private Timestamp review_time;
    private int likes_count;
    //private String reviewer_name;


    /* Wishlist */
    private String wishList_Id;
    private String user_email;
    private String user_name;
    //private String book_id;
    //private String book_title;
    //private String image_url;
    //private Timestamp adding_time;

    /* Newbook */
    private ArrayList<String> new_book_genres;
    //private String book_id;
    //private String book_title;
    //private String image_url;
    //private Timestamp adding_time;

    //****Recomandation****
    //private ArrayList<Book.BookGenre> new_book_genres;
    //private String book_id;
    //private String book_title;
    //private String image_url;
    //private Timestamp adding_time;



/*-----------------------------------------Constructors-------------------------------------------*/

    public Post(Review review) {

        this.type = PostType.Review;
        this.post_time = review.getReview_time();
        this.review_id = review.getReview_id();

        this.book_id = review.getBook_id();
        this.book_title = review.getBook_title();
        this.book_author = review.getBook_author();

        this.reviewer_email = review.getReviewer_email();
        this.user_name = review.getReviewer_name();
        this.user_avatar = review.getReviewer_avatar();

        this.rank = review.getRank();
        this.review_content = review.getReview_content();
        this.review_title = review.getReview_title();

        this.likes_count = review.getLikes_count();
    }

    public Post(WishList wishList) {

        this.type = PostType.WishList;
        this.post_time = wishList.getAdding_time();
        this.wishList_Id = wishList.getId();

        this.book_id = wishList.getBook_id();
        this.book_title = wishList.getBook_title();

        this.user_email = wishList.getUser_email();
        this.user_name = wishList.getUser_name();
        this.user_avatar = wishList.getUser_avatar();
    }

    //Recommendation + New Book Post
    public Post(String post_id, PostType type, Timestamp post_time, String book_id, String book_title, String image_url, ArrayList<String> new_book_genres) {
        this.type = type;
        this.post_time = post_time;
        this.book_id = book_id;
        this.book_title = book_title;
        //this.new_book_genres = new_book_genres;
    }



/*---------------------------------------Getters/Setters------------------------------------------*/

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
        return post_time;
    }

    public void setPost_time(Timestamp post_time) {
        this.post_time = post_time;
    }


    public String getReview_id() {
        return review_id;
    }

    public void setReview_id(String review_id) {
        this.review_id = review_id;
    }


    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }


    public String getReview_title() {
        return review_title;
    }

    public void setReview_title(String review_title) {
        this.review_title = review_title;
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


    public int getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }


    public String getBook_title() {
        return book_title;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }


    public String getWishList_Id() {
        return wishList_Id;
    }

    public void setWishList_Id(String wishList_Id) {
        this.wishList_Id = wishList_Id;
    }


    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }


    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }


    public ArrayList<String> getNew_book_genres() {
        return new_book_genres;
    }

    public void setNew_book_genres(ArrayList<String> new_book_genres) {
        this.new_book_genres = new_book_genres;
    }


    public ArrayList<Integer> getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(ArrayList<Integer> user_avatar) {
        this.user_avatar = user_avatar;
    }

    public String getBook_author() {
        return book_author;
    }

    public void setBook_author(String book_author) {
        this.book_author = book_author;
    }

    @Override
    public int compareTo(Object o) {
        if(type == PostType.Recommendation){
            //there is no time for recommendation and we want to put them in random order in fid so we'll return random value[-1,0,1]
            Random rand = new Random();
            int n = rand.nextInt(3); //[0-2]
            return n-1;// [-1,0,1]
        }
        return (post_time.compareTo(((Post)o).post_time));
    }

    public static class SortByDate implements Comparator<Post>
    {
        public int compare(Post a, Post b)
        {
            return -a.post_time.compareTo(b.post_time); // reverse, because newer posts are more relevant
        }
    }

}
