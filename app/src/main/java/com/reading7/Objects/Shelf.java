package com.reading7.Objects;

import java.util.ArrayList;

public class Shelf {
    private String id;
    private String shelf_name;
    private ArrayList<String> book_names = new ArrayList<>();

    public Shelf(){
        this.id = "";
        this.shelf_name = "";
    }

    public Shelf(String id, String shelf_name){
        this.id = id;
        this.shelf_name = shelf_name;
    }

    public Shelf(String id, String shelf_name, ArrayList<String> book_names){
        this.id = id;
        this.shelf_name = shelf_name;
        this.book_names.addAll(book_names);
    }

    public String getId() {
        return id;
    }

    public String getShelf_name() {
        return shelf_name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setShelf_name(String shelf_name) {
        this.shelf_name = shelf_name;
    }

    public ArrayList<String> getBook_names() {
        return book_names;
    }

    public void setBook_names(ArrayList<String> book_names) {
        this.book_names = book_names;
    }
}
