package com.reading7;

import java.util.ArrayList;

public class Book {
    private String id;
    private String title;
    private ArrayList<String> genres;
    private String author;
    private String publisher;
    private int num_pages;
    private String description;
    private double avg_rating;
    private int num_readers;


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

    public double getAvg_rating() {
        return avg_rating;
    }

    public void setAvg_rating(double avg_rating) {
        this.avg_rating = avg_rating;
    }

    public int getNum_readers() {
        return num_readers;
    }

    public void setNum_readers(int num_readers) {
        this.num_readers = num_readers;
    }

    public Book(String id, String title, ArrayList<String> genres, String author, String publisher, int num_pages, String description, int avg_rating, int num_readers) {
        this.id = id;
        this.title = title;
        this.genres = genres;
        this.author = author;
        this.publisher = publisher;
        this.num_pages = num_pages;
        this.description = description;
        this.avg_rating = avg_rating;
        this.num_readers = num_readers;
    }
}
