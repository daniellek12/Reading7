package com.reading7.Objects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class User {

    private String full_name; //user's name
    private String email; //user's email
    private String birth_date; //user's birth date

    private ArrayList<String> followers; //Emails list of the followers
    private ArrayList<String> following; //Emails list of the following
    private ArrayList<String> last_searches;
    private ArrayList<String> favourite_books; // top books to view on profile
    private ArrayList<String> favourite_genres;
    private ArrayList<String> liked_reviews;

    private ArrayList<Integer> avatar_details; //hair color, hair type, eyes color, glasses, skin color, shirt color

    public User() {
        this.full_name = "";
        this.email = "";
        this.birth_date = "";
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.last_searches = new ArrayList<>();
        this.favourite_books = new ArrayList<>();
        this.favourite_genres = new ArrayList<>();
        this.liked_reviews = new ArrayList<>();
        this.avatar_details = new ArrayList<>();
    }

    public User(String full_name, String email, String birth_date) {
        this.full_name = full_name;
        this.email = email;
        this.birth_date = birth_date;
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.last_searches = new ArrayList<>();
        this.favourite_books = new ArrayList<>();
        this.favourite_genres = new ArrayList<>();
        this.liked_reviews = new ArrayList<>();
        this.avatar_details = new ArrayList<>();
    }

    //TODO: REMOVE WHEN THE AVATAR WILL WORK

    public User(String full_name, String email, String birth_date, ArrayList<String> followers, ArrayList<String> following, ArrayList<String> last_searches, ArrayList<String> favourite_books, ArrayList<String> favourite_genres, ArrayList<String> liked_reviews) {
        this.full_name = full_name;
        this.email = email;
        this.birth_date = birth_date;
        this.followers = followers;
        this.following = following;
        this.last_searches = last_searches;
        this.favourite_books = favourite_books;
        this.favourite_genres = favourite_genres;
        this.liked_reviews = liked_reviews;;
    }


    public User(String full_name, String email, String birth_date, ArrayList<String> followers, ArrayList<String> following, ArrayList<String> last_searches, ArrayList<String> favourite_books, ArrayList<String> favourite_genres, ArrayList<String> liked_reviews, ArrayList<Integer> avatar_details) {
        this.full_name = full_name;
        this.email = email;
        this.birth_date = birth_date;
        this.followers = followers;
        this.following = following;
        this.last_searches = last_searches;
        this.favourite_books = favourite_books;
        this.favourite_genres = favourite_genres;
        this.liked_reviews = liked_reviews;
        this.avatar_details = avatar_details;
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

    public ArrayList<Integer> getAvatar_details() {
        return avatar_details;
    }

    public void setAvatar_details(ArrayList<Integer> avatar_details) {
        this.avatar_details = avatar_details;
    }
}
