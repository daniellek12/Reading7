package com.reading7;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;


public class Post implements Comparable {

    private String post_id;
    private PostType type;
    private Timestamp post_time;


    //instead of using Polymorphism and dynamic cast,
    // i decided to save a lot more fields, because im afraid that dynamic cast will cause overhead
    // i prefer to save a lot of data instead of getting slower performence.

    //****Review****
    private String review_id;
    private String book_id;
    private String reviewer_email;
    private int rank;
    private String review_content;
    //private Timestamp review_time;
    private int[] emojis_count;
    //private String reviewer_name;
    private String book_title;
    private String image_url;


    //****WishList****
    private String wishList_Id;
    private String user_email;
    private String user_name;
    //private String book_id;
    //private String book_title;
    //private String image_url;
    //private Timestamp adding_time;

    //****New Book****
    private ArrayList<Book.BookGenre> new_book_genres;
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



/*--------------------------Constructors--------------------*/
public Post(String post_id, PostType type, Timestamp post_time, String review_id, String book_id, String reviewer_email, int rank, String review_content, int[] emojis_count, String reviewer_name, String book_title, String image_url) {
    this.post_id = post_id;
    this.type = type;
    this.post_time = post_time;
    this.review_id = review_id;
    this.book_id = book_id;
    this.reviewer_email = reviewer_email;
    this.rank = rank;
    this.review_content = review_content;
    this.emojis_count = emojis_count;
    this.user_name = reviewer_name;
    this.book_title = book_title;
    this.image_url = image_url;
}


    //WishList
    public Post(String post_id, PostType type, Timestamp post_time, String book_id, String book_title, String image_url, String wishList_Id, String user_email, String user_name) {
        this.post_id = post_id;
        this.type = type;
        this.post_time = post_time;
        this.book_id = book_id;
        this.book_title = book_title;
        this.image_url = image_url;
        this.wishList_Id = wishList_Id;
        this.user_email = user_email;
        this.user_name = user_name;
    }


    //Recommendation + New Book Post
    public Post(String post_id, PostType type, Timestamp post_time, String book_id, String book_title, String image_url, ArrayList<Book.BookGenre> new_book_genres) {
        this.post_id = post_id;
        this.type = type;
        this.post_time = post_time;
        this.book_id = book_id;
        this.book_title = book_title;
        this.image_url = image_url;
        this.new_book_genres = new_book_genres;
    }
 /*--------------------------End Constructors--------------------*/

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

    public int[] getEmojis_count() {
        return emojis_count;
    }

    public void setEmojis_count(int[] emojis_count) {
        this.emojis_count = emojis_count;
    }

    public String getBook_title() {
        return book_title;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
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

    public ArrayList<Book.BookGenre> getNew_book_genres() {
        return new_book_genres;
    }

    public void setNew_book_genres(ArrayList<Book.BookGenre> new_book_genres) {
        this.new_book_genres = new_book_genres;
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
}
