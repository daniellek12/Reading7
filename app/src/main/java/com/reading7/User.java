package com.reading7;

public class User {

    private String full_name; //user's name
    private String email; //user's email
    private int age; //user's age
    private String school_name; //user's school name

    public User(){
        this.full_name = "";
        this.email = "";
        this.age = 0;
        this.school_name = "";
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
}
