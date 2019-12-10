package com.reading7;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String full_name; //user's name
    private String email; //user's email
    private String birth_date; //user's birth date

    private List<Integer> followers; //ids list of the followers
    private List<Integer> following; //ids list of the following

    public User(){
        this.full_name = "";
        this.email = "";
        this.birth_date = "";
        followers = new ArrayList<>();
        following = new ArrayList<>();
    }

    public User(String full_name, String email, String birth_date){
        this.full_name = full_name;
        this.email = email;
        this.birth_date = birth_date;
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

    public List<Integer> getfollowers() {
        return followers;
    }

    public List<Integer> getfollowing() {
        return following;
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

    public void setfollowers(List<Integer> followers) { this.followers = followers; }

    public void setfollowing(List<Integer> following) { this.following = following; }
}
