package com.reading7;

import java.sql.Timestamp;

public class WishList implements Comparable {

    private String id;
    private String user_email;
    private String user_name;
    private String user_city;
    private int user_age;
    private String book_id;
    private String book_name;
    //another fields of book;
    private Timestamp adding_time;

    public WishList(String id, String user_email, String user_name, String user_city, int user_age, String book_id, String book_name, Timestamp adding_time) {
        this.id = id;
        this.user_email = user_email;
        this.user_name = user_name;
        this.user_city = user_city;
        this.user_age = user_age;
        this.book_id = book_id;
        this.book_name = book_name;
        this.adding_time = adding_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUser_city() {
        return user_city;
    }

    public void setUser_city(String user_city) {
        this.user_city = user_city;
    }

    public int getUser_age() {
        return user_age;
    }

    public void setUser_age(int user_age) {
        this.user_age = user_age;
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public Timestamp getAdding_time() {
        return adding_time;
    }

    public void setAdding_time(Timestamp adding_time) {
        this.adding_time = adding_time;
    }


    @Override
    public int compareTo(Object o) {
        return (adding_time.compareTo(((WishList)o).adding_time));
    }

    public static class User {

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
}
