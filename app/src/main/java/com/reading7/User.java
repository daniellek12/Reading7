package com.reading7;

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
    private Queue<String> last_searches;
    private Book[] favourite_books; // top books to view on profile
    private ArrayList<String> favourite_genres;
    private ArrayList<String> liked_reviews;

    public User() {
        this.full_name = "";
        this.email = "";
        this.birth_date = "";
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.last_searches = new LinkedList<>();
        this.favourite_books = new Book[3];
        this.favourite_genres = new ArrayList<>();

    }

    public User(String full_name, String email, String birth_date) {
        this.full_name = full_name;
        this.email = email;
        this.birth_date = birth_date;
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.last_searches = new LinkedList<>();
        this.favourite_books = new Book[3];
        this.favourite_genres = new ArrayList<>();
    }

    public User(String full_name, String email, String birth_date, ArrayList<String> followers, ArrayList<String> following, Queue<String> last_searches) {
        this.full_name = full_name;
        this.email = email;
        this.birth_date = birth_date;
        this.followers = followers;
        this.following = following;
        this.last_searches = last_searches;
//        this.favourite_books = new Book[3];
//        this.favourite_genres = new ArrayList<>();
        //TODO add this fields ^^
    }

    public String getFull_name() {
        return full_name;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<String> getfollowers() {
        return followers;
    }

    public ArrayList<String> getfollowing() {
        return following;
    }

    public Queue<String> getLast_searches() {
        return last_searches;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setfollowers(ArrayList<String> followers) {
        this.followers = followers;
    }

    public void setfollowing(ArrayList<String> following) {
        this.following = following;
    }

    public void setLast_searches(Queue<String> last_searches) {
        this.last_searches = last_searches;
    }
}
