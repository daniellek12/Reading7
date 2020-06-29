package com.reading7.Objects;

import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static com.google.firebase.Timestamp.now;
import static org.junit.Assert.assertEquals;

public class WishListTest {

    ArrayList<WishList> wishlists = new ArrayList<>();
    ArrayList<Timestamp> times = new ArrayList<>();
    int NTEST = 10; // modify as you like

    private String id = "id";
    private Timestamp adding_time;
    private String book_id = "b_id";
    private String book_title = "b_title";
    private String book_author = "b_authour";
    private String user_email = "b_email";
    @Before
    public void setUp() throws Exception {
        for (int i = 0; i < NTEST; i++) {
            String scur = String.valueOf(i);
            adding_time = now();
            times.add(adding_time);
            wishlists.add(new WishList(id + scur, user_email + scur,
                    book_id + scur, book_title + scur,
                    book_author + scur, adding_time));
        }
    }

    @Test
    public void getBook_title() {
        for(int i = 0; i < NTEST; i++) {
            assertEquals(book_title + String.valueOf(i), wishlists.get(i).getBook_title());
        }
    }

    @Test
    public void setBook_title() {
        Timestamp cur_time = now();
        WishList dummy = new WishList(id, user_email, book_id, book_title, book_author, cur_time);

        dummy.setBook_title("TEST_BTITLE");
        assertEquals("TEST_BTITLE", dummy.getBook_title());
    }

    @Test
    public void getBook_author() {
        for(int i = 0; i < NTEST; i++) {
            assertEquals(book_author + String.valueOf(i), wishlists.get(i).getBook_author());
        }
    }

    @Test
    public void setBook_author() {
        Timestamp cur_time = now();
        WishList dummy = new WishList(id, user_email, book_id, book_title, book_author, cur_time);

        dummy.setBook_author("TEST_BAUTHOR");
        assertEquals("TEST_BAUTHOR", dummy.getBook_author());

    }

    @Test
    public void getId() {
        for(int i = 0; i < NTEST; i++) {
            assertEquals(id + String.valueOf(i), wishlists.get(i).getId());
        }
    }

    @Test
    public void setId() {
        Timestamp cur_time = now();
        WishList dummy = new WishList(id, user_email, book_id, book_title, book_author, cur_time);

        dummy.setId("TEST_ID");
        assertEquals("TEST_ID", dummy.getId());
    }

    @Test
    public void getUser_email() {
        for(int i = 0; i < NTEST; i++) {
            assertEquals(user_email + String.valueOf(i), wishlists.get(i).getUser_email());
        }
    }

    @Test
    public void setUser_email() {
        Timestamp cur_time = now();
        WishList dummy = new WishList(id, user_email, book_id, book_title, book_author, cur_time);

        dummy.setUser_email("TEST_UMAIL");
        assertEquals("TEST_UMAIL", dummy.getUser_email());
    }

    @Test
    public void getBook_id() {
        for(int i = 0; i < NTEST; i++) {
            assertEquals(book_id + String.valueOf(i), wishlists.get(i).getBook_id());
        }
    }

    @Test
    public void setBook_id() {
        Timestamp cur_time = now();
        WishList dummy = new WishList(id, user_email, book_id, book_title, book_author, cur_time);

        dummy.setBook_id("TEST_BID");
        assertEquals("TEST_BID", dummy.getBook_id());
    }

    @Test
    public void getAdding_time() {
        for(int i = 0; i < NTEST; i++) {
            assertEquals(times.get(i), wishlists.get(i).getAdding_time());
        }
    }

    @Test
    public void setAdding_time() {
        Timestamp cur_time = now();
        WishList dummy = new WishList(id, user_email, book_id, book_title, book_author, cur_time);

        Timestamp new_time = now();
        dummy.setAdding_time(new_time);
        assertEquals(new_time, dummy.getAdding_time());
    }
}