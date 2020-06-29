package com.reading7.Objects;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ShelfTest {
    private ArrayList<Shelf> shelves = new ArrayList<>();
    int NTEST = 10; // modify as you like
    String id = "id";
    String name = "name";
    private int sub_NTEST = 100;

    @Before
    public void setUp() {
        String scur;
        for (int i = 0; i < NTEST; i++) {
            //ArrayList<Book> cur_books = new ArrayList<>();
            scur = String.valueOf(i);
            String bcur = scur + "-";
            ArrayList<String> cbooks = new ArrayList<>();
            for(int j = 0; j < sub_NTEST; j++) {
                /*
                String bid = "", title = "", author = "", publisher = "", description = "";
                int avg_rating = 3, avg_age = 10 ,num_pages = 0;
                ArrayList<String> genres = new ArrayList<>(), actual_genres = new ArrayList<>();
                cur = new Book(bid + bcur, title + bcur, genres, actual_genres,
                        author +bcur , publisher + bcur, num_pages,
                        description + bcur, avg_rating, avg_age);

                cur_books.add(cur);
                 */
                cbooks.add(bcur + String.valueOf(j));
            }
            shelves.add(new Shelf(id + scur, name + scur, cbooks));
        }
    }

    @Test
    public void getId() {
        for(int i = 0; i < NTEST ; i++) {
            assertEquals(id + String.valueOf(i), shelves.get(i).getId());
        }
    }

    @Test
    public void getShelf_name() {
        for(int i = 0; i < NTEST ; i++) {
            assertEquals(name + String.valueOf(i), shelves.get(i).getShelf_name());
        }
    }

    @Test
    public void setId() {
        Shelf dummy = new Shelf(id, name);
        dummy.setId("TEST_ID");
        assertEquals("TEST_ID", dummy.getId());
    }

    @Test
    public void setShelf_name() {
        Shelf dummy = new Shelf(id, name);
        dummy.setShelf_name("TEST_NAME");
        assertEquals("TEST_NAME", dummy.getShelf_name());
    }

    @Test
    public void getBook_names() {
        for(int i = 0; i < NTEST ; i++) {
            ArrayList<String> cur_shelf_books = shelves.get(i).getBook_names();
            for(int j = 0; j < sub_NTEST ; j++ ) {
                assertEquals(String.valueOf(i) + "-" + String.valueOf(j), cur_shelf_books.get(j));
            }
        }
    }

    @Test
    public void setBook_names() {
        Shelf dummy = new Shelf(id, name);
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < sub_NTEST; i++) {
            names.add(String.valueOf(i));
        }

        dummy.setBook_names(names);
        ArrayList<String> names_from_dummy = dummy.getBook_names();
        for (int i = 0; i < sub_NTEST; i++) {
            assertEquals(String.valueOf(i), names_from_dummy.get(i));
        }

    }
}