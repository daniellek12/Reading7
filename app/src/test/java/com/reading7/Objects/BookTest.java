package com.reading7.Objects;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class BookTest {
    private ArrayList<Book> books = new ArrayList<>();
    int NTEST = 10; // modify as you like
    String id = "id";
    String title = "title";
    ArrayList<String> genres = new ArrayList<>();
    ArrayList<String> actual_genres = new ArrayList<>();
    String author = "author";
    String publisher = "publisher";
    int num_pages = 100;
    String description = "description";
    float avg_rating = (float) 0.0;
    float avg_age = (float) 0.0;

    //StringBuilder str = new StringBuilder();

    @Before
    public void setUp() throws Exception {
        for(int i = 0; i < NTEST; i++) {
            String scur = String.valueOf(i);
            genres.add(scur);
            if (i%2 == 0) actual_genres.add(scur);
            books.add(new Book(id + scur, title + scur, genres, actual_genres,
                    author + scur, publisher + scur, num_pages,
                    description + scur, avg_rating, avg_age));
            num_pages += 1;
            avg_rating += (float) 0.01;
            avg_age += (float) 0.01;
        }
    }

    @Test
    public void getAvg_age() {
        float init_avg_age = (float) 0.0;
        for(int i = 0; i < NTEST; i++) {
            assertEquals(init_avg_age, books.get(i).getAvg_age(), 0.001);
            init_avg_age += (float) 0.01;
        }
    }

    @Test
    public void setAvg_age() {
        ArrayList<String> cur_genres = new ArrayList<>();
        ArrayList<String> cur_actual_genres = new ArrayList<>();
        Book dummy = new Book(id, title, cur_genres, cur_actual_genres,
                author, publisher, num_pages,
                description, avg_rating, avg_age);

        dummy.setAvg_age((float) 12.03);

        assertEquals((float) 12.03, dummy.getAvg_age(), 0.001);
    }

    @Test
    public void getId() {
        for(int i = 0; i < NTEST; i++) {
            assertEquals(id + String.valueOf(i), books.get(i).getId());
        }
    }

    @Test
    public void setId() {
        ArrayList<String> cur_genres = new ArrayList<>();
        ArrayList<String> cur_actual_genres = new ArrayList<>();
        Book dummy = new Book(id, title, cur_genres, cur_actual_genres,
                author, publisher, num_pages,
                description, avg_rating, avg_age);

        dummy.setId("TEST_ID");

        assertEquals("TEST_ID", dummy.getId());
    }

    @Test
    public void getTitle() {
        for(int i = 0; i < NTEST; i++) {
            assertEquals(title + String.valueOf(i), books.get(i).getTitle());
        }
    }

    @Test
    public void setTitle() {
        ArrayList<String> cur_genres = new ArrayList<>();
        ArrayList<String> cur_actual_genres = new ArrayList<>();
        Book dummy = new Book(id, title, cur_genres, cur_actual_genres,
                author, publisher, num_pages,
                description, avg_rating, avg_age);

        dummy.setTitle("TEST_TITLE");

        assertEquals("TEST_TITLE", dummy.getTitle());
    }

    @Test
    public void getGenres() {
        for(int i = 0; i < NTEST; i++) {
            ArrayList<String> cur_genres = books.get(i).getGenres();
            for(int j = 0; j < i; j ++) {
                assertEquals(String.valueOf(j),genres.get(j));
            }
            assertEquals(NTEST, genres.size());
        }
    }

    @Test
    public void setGenres() {
        ArrayList<String> cur_genres = new ArrayList<>();
        ArrayList<String> cur_actual_genres = new ArrayList<>();

        Book dummy = new Book(id, title, cur_genres, cur_actual_genres,
                author, publisher, num_pages,
                description, avg_rating, avg_age);

        for(int i = 0; i < NTEST; i++) {
            cur_actual_genres.add("TEST_GENRE" + String.valueOf(i));
        }

        dummy.setGenres(cur_actual_genres);

        ArrayList<String> from_dummy = dummy.getGenres();

        for(int i = 0; i < NTEST; i++) {
            assertEquals("TEST_GENRE" + String.valueOf(i), from_dummy.get(i));
        }
    }

    @Test
    public void getAuthor() {
        for(int i = 0; i < NTEST; i++) {
            assertEquals(author + String.valueOf(i), books.get(i).getAuthor());
        }
    }

    @Test
    public void setAuthor() {
        ArrayList<String> cur_genres = new ArrayList<>();
        ArrayList<String> cur_actual_genres = new ArrayList<>();
        Book dummy = new Book(id, title, cur_genres, cur_actual_genres,
                author, publisher, num_pages,
                description, avg_rating, avg_age);

        dummy.setAuthor("TEST_AUTHOR");

        assertEquals("TEST_AUTHOR", dummy.getAuthor());
    }

    @Test
    public void getPublisher() {
        for(int i = 1; i < NTEST; i++) {
            assertEquals(publisher + String.valueOf(i), books.get(i).getPublisher());
        }
    }

    @Test
    public void setPublisher() {
        ArrayList<String> cur_genres = new ArrayList<>();
        ArrayList<String> cur_actual_genres = new ArrayList<>();
        Book dummy = new Book(id, title, cur_genres, cur_actual_genres,
                author, publisher, num_pages,
                description, avg_rating, avg_age);

        dummy.setPublisher("TEST_PUBLISHER");

        assertEquals("TEST_PUBLISHER", dummy.getPublisher());
    }

    @Test
    public void getNum_pages() {
        int init_num_pages = 100;
        for(int i = 0; i < NTEST; i++) {
            assertEquals(init_num_pages + i, books.get(i).getNum_pages());
        }
    }

    @Test
    public void setNum_pages() {
        ArrayList<String> cur_genres = new ArrayList<>();
        ArrayList<String> cur_actual_genres = new ArrayList<>();
        Book dummy = new Book(id, title, cur_genres, cur_actual_genres,
                author, publisher, num_pages,
                description, avg_rating, avg_age);

        dummy.setNum_pages(1234);

        assertEquals(1234, dummy.getNum_pages());
    }

    @Test
    public void getDescription() {
        for(int i = 0; i < NTEST; i++) {
            assertEquals(description + String.valueOf(i), books.get(i).getDescription());
        }
    }

    @Test
    public void setDescription() {
        ArrayList<String> cur_genres = new ArrayList<>();
        ArrayList<String> cur_actual_genres = new ArrayList<>();
        Book dummy = new Book(id, title, cur_genres, cur_actual_genres,
                author, publisher, num_pages,
                description, avg_rating, avg_age);

        dummy.setDescription("TEST_DESC");

        assertEquals("TEST_DESC", dummy.getDescription());
    }

    @Test
    public void getAvg_rating() {
        float init_avg_rating = (float) 0.0;
        for(int i = 0; i < NTEST; i++) {
            assertEquals(init_avg_rating, books.get(i).getAvg_rating(), 0.001);
            init_avg_rating += (float) 0.01;
        }
    }

    @Test
    public void setAvg_rating() {
        ArrayList<String> cur_genres = new ArrayList<>();
        ArrayList<String> cur_actual_genres = new ArrayList<>();
        Book dummy = new Book(id, title, cur_genres, cur_actual_genres,
                author, publisher, num_pages,
                description, avg_rating, avg_age);

        dummy.setAvg_rating((float) 2.1);

        assertEquals((float) 2.1, dummy.getAvg_rating(), 0.01);
    }

    @Test
    public void setRaters_count() {
        ArrayList<String> cur_genres = new ArrayList<>();
        ArrayList<String> cur_actual_genres = new ArrayList<>();
        Book dummy = new Book(id, title, cur_genres, cur_actual_genres,
                author, publisher, num_pages,
                description, avg_rating, avg_age);

        dummy.setRaters_count(23);

        assertEquals(23, dummy.getRaters_count());
    }

    @Test
    public void getActual_genres() {
        for(int i = 0; i < NTEST; i++) {
            ArrayList<String> cur_actual_genres = books.get(i).getActual_genres();
            int k = 0;
            for(int j = 0; j < i; j ++) {
                if (j % 2 == 0) assertEquals(String.valueOf(j),genres.get(k));
                k++;
            }
        }
    }

}