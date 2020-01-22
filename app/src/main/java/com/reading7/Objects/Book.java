package com.reading7.Objects;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Book {
    private String id;
    private String title;
    private ArrayList<String> genres;
    private String author;
    private String publisher;
    private int num_pages;
    private String description;
    private float avg_rating;
    private float avg_age;
    private ArrayList<String> actual_genres;
    private int raters_count;

    public Book() {}

    public float getAvg_age() {
        return avg_age;
    }

    public void setAvg_age(float avg_age) {
        this.avg_age = avg_age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getNum_pages() {
        return num_pages;
    }

    public void setNum_pages(int num_pages) {
        this.num_pages = num_pages;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getAvg_rating() {
        return avg_rating;
    }

    public void setAvg_rating(float avg_rating) {
        this.avg_rating = avg_rating;
    }



    public Book(String id, String title, ArrayList<String> genres,ArrayList<String> actual_genres, String author, String publisher, int num_pages, String description, float avg_rating, float avg_age) {
        this.id = id;
        this.title = title;
        this.genres = genres;
        this.author = author;
        this.publisher = publisher;
        this.num_pages = num_pages;
        this.description = description;
        this.avg_rating = avg_rating;
        this.actual_genres=actual_genres;
        //this.num_readers = num_readers;
        this.avg_age = avg_age;
        this.raters_count = 0;
    }

    public int getRaters_count() {
        return raters_count;
    }

    public void setRaters_count(int raters_count) {
        this.raters_count = raters_count;
    }

    public ArrayList<String> getActual_genres() {
        return actual_genres;
    }

    public void setActual_genres(ArrayList<String> actual_genres) {
        this.actual_genres = actual_genres;
    }

    @NonNull
    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object object) {
        boolean same = false;

        if (object != null && object instanceof Book) {
            same = this.id.equals(((Book) object).id) && this.title.equals(((Book) object).title);
        }
        return same;
    }
}
