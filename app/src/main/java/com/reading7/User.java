package com.reading7;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String full_name; //user's name
    private String email; //user's email
    private int age; //user's age
    private String school_name; //user's school name

    private List<Integer> followers; //ids list of the followers
    private List<Integer> following; //ids list of the following

    public User(){
        this.full_name = "";
        this.email = "";
        this.age = 0;
        this.school_name = "";
        followers = new ArrayList<>();
        following = new ArrayList<>();
    }

    public User(String full_name, String email, int age, String school_name){
        this.full_name = full_name;
        this.email = email;
        this.age = age;
        this.school_name = school_name;
    }

    public String getFull_name() {
        return full_name;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public String getSchool_name() {
        return school_name;
    }

    public List<Integer> getfollowers() {
        return followers;
    }

    public List<Integer> getfollowing() {
        return following;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public void setfollowers(List<Integer> followers) { this.followers = followers; }

    public void setfollowing(List<Integer> following) { this.following = following; }
}
