package com.reading7;

import java.util.ArrayList;

public class Book {
    private int id;
    private String title;
    private String image_url;
    private ArrayList<BookGenre> genres;
    private String author;
    private String publisher;
    private int num_pages;
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

    public Book(int id, String title, String image, ArrayList<BookGenre> genres, String author, String publisher, int num_pages){
        this.id = id;
        this.title = title;
        this.image_url = image;
        this.genres = genres;
        this.author = author;
        this.publisher = publisher;
        this.num_pages = num_pages;
        //this.avg_rating = 0;
    }

    public int getId() {
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
