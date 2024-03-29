package com.reading7.Objects;

import java.util.ArrayList;

public class User {

    private String full_name;                       // user's name
    private String email;                           // user's email
    private String birth_date;                      // user's birth date
    private ArrayList<String> followers;            // Emails list of the followers
    private ArrayList<String> following;            // Emails list of the following
    private ArrayList<String> follow_requests;      // Emails list of the followers requested to follow user
    private ArrayList<String> last_searches;
    private ArrayList<String> favourite_books;      // top books to view on profile
    private ArrayList<String> favourite_genres;
    private ArrayList<String> liked_reviews;
    private String token_id;
    private Boolean is_private;                     //true if profile listed as private
    private Boolean is_notify;
    private Avatar avatar;
    private int points;

    public User() {
        this.full_name = "";
        this.email = "";
        this.birth_date = "";
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.follow_requests = new ArrayList<>();
        this.last_searches = new ArrayList<>();
        this.favourite_books = new ArrayList<>();
        this.favourite_genres = new ArrayList<>();
        this.liked_reviews = new ArrayList<>();
        this.avatar = new Avatar(1, 1, 1, 1, 1);
        this.is_private = false; // default is public (as in Instagram)
        this.token_id = "";
        this.is_notify = true;
        this.points = 0;
    }

    //Don't use this constructor, should only be used in SignUpStep1
    public User(String full_name, String email, String birth_date, Avatar avatar) {
        this.full_name = full_name;
        this.email = email;
        this.birth_date = birth_date;
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.follow_requests = new ArrayList<>();
        this.last_searches = new ArrayList<>();
        this.favourite_books = new ArrayList<>();
        this.favourite_genres = new ArrayList<>();
        this.liked_reviews = new ArrayList<>();
        this.avatar = avatar;
        this.is_private = false;
        this.token_id = "";
        this.is_notify = true;
        this.points = 0;

    }


    public User(String full_name, String email, String birth_date, ArrayList<String> followers, ArrayList<String> following, ArrayList<String> follow_requests, ArrayList<String> last_searches, ArrayList<String> favourite_books, ArrayList<String> favourite_genres, ArrayList<String> liked_reviews, Avatar avatar, Boolean is_private, int points) {
        this.full_name = full_name;
        this.email = email;
        this.birth_date = birth_date;
        this.followers = followers;
        this.following = following;
        this.follow_requests = follow_requests;
        this.last_searches = last_searches;
        this.favourite_books = favourite_books;
        this.favourite_genres = favourite_genres;
        this.liked_reviews = liked_reviews;
        this.avatar = avatar;
        this.is_private = is_private;
        this.points = points;
        this.token_id = "";
        this.is_notify = true;
    }

    public ArrayList<String> getFollow_requests() {
        return follow_requests;
    }

    public void setFollow_requests(ArrayList<String> follow_requests) {
        this.follow_requests = follow_requests;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public Boolean getIs_private() {
        return is_private;
    }

    public void setIs_private(Boolean is_private) {
        this.is_private = is_private;
    }

    public ArrayList<String> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<String> followers) {
        this.followers = followers;
    }

    public ArrayList<String> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<String> following) {
        this.following = following;
    }

    public ArrayList<String> getLast_searches() {
        return last_searches;
    }

    public void setLast_searches(ArrayList<String> last_searches) {
        this.last_searches = last_searches;
    }

    public ArrayList<String> getFavourite_books() {
        return favourite_books;
    }

    public void setFavourite_books(ArrayList<String> favourite_books) {
        this.favourite_books = favourite_books;
    }

    public ArrayList<String> getFavourite_genres() {
        return favourite_genres;
    }

    public void setFavourite_genres(ArrayList<String> favourite_genres) {
        this.favourite_genres = favourite_genres;
    }

    public ArrayList<String> getLiked_reviews() {
        return liked_reviews;
    }

    public void setLiked_reviews(ArrayList<String> liked_reviews) {
        this.liked_reviews = liked_reviews;
    }

    public void remove_like(String id) {
        this.liked_reviews.remove(id);
    }

    public void add_like(String id) {
        this.liked_reviews.add(id);
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public Boolean getIs_notify() {
        return is_notify;
    }

    public void setIs_notify(Boolean is_notify) {
        this.is_notify = is_notify;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void reducePoints(int points) {
        this.points = Math.max(this.points - points, 0);
    }

}
