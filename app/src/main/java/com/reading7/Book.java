package com.reading7;

import java.util.ArrayList;

public class Book {
    private String id;
    private String title;
    private String image_url;
    private ArrayList<BookGenre> genres;
    private String author;
    private String publisher;
    private int num_pages;
    private int release_year;
    private String summary;
    //private int avg_rating;
    //private ArrayList<Review> reviews;

    public enum BookGenre{
        ActionAndAdventure,
        Art,
        AlternateHistory,
        Autobiography,
        Anthology,
        Biography,
        ChickLit,
        BookReview,
        Childrens,
        Cookbook,
        Comic,
        Diary,
        Crime,
        Encyclopedia,
        Drama,
        Guide,
        Fairytale,
        Health,
        Fantasy,
        History,
        GraphicNovel,
        Journal,
        HistoricalFiction,
        Math,
        Horror,
        Memoir,
        Mystery,
        Prayer,
        ParanormalRomance,
        ReligionAndSpirituality,
        PictureBook,
        Textbook,
        Poetry,
        Review,
        PoliticalThriller,
        Science,
        Romance,
        SelfHelp,
        Satire,
        Travel,
        ScienceFiction,
        TrueCrime,
        ShortStory,
        Suspense,
        Thriller,
        YoungAdult
    }

    public Book(String id, String title, String image, ArrayList<BookGenre> genres, String author, String publisher, int num_pages,int release_year, String summary){
        this.id = id;
        this.title = title;
        this.image_url = image;
        this.genres = genres;
        this.author = author;
        this.publisher = publisher;
        this.num_pages = num_pages;
        //this.avg_rating = 0;
       this.release_year= release_year;
       this.summary=summary;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setGenres(ArrayList<BookGenre> genres) {
        this.genres = genres;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setNum_pages(int num_pages) {
        this.num_pages = num_pages;
    }

    public int getRelease_year() {
        return release_year;
    }

    public void setRelease_year(int release_year) {
        this.release_year = release_year;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getId() {
        return id;
    }

    public ArrayList<BookGenre> getGenres() {
        return genres;
    }

    /*public int getAvg_rating() {
        return avg_rating;
    }*/

    public int getNum_pages() {
        return num_pages;
    }

    public String getAuthor() {
        return author;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getTitle() {
        return title;
    }
}
