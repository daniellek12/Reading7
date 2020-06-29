package com.reading7.Objects;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class RecommendationTest {
    ArrayList<Recommendation> recommendations = new ArrayList<>();
    int NTEST = 10; // modify as you like
    private String user_id = "u_id";
    private String book_id = "b_id";
    private double similarity_rating = 0.00;

    @Before
    public void setUp() {
        for(int i = 0; i < NTEST; i++) {
            String scur = String.valueOf(i);
            recommendations.add(new Recommendation(user_id + scur, book_id + scur, similarity_rating));
            similarity_rating += 0.01;
        }
    }

    @Test
    public void getBook_id() {
        for(int i = 0; i < NTEST; i++) {
            assertEquals(book_id + String.valueOf(i), recommendations.get(i).getBook_id());
        }
    }

    @Test
    public void getUser_id() {
        for(int i = 0; i < NTEST; i++) {
            assertEquals(user_id + String.valueOf(i), recommendations.get(i).getUser_id());
        }
    }

    @Test
    public void getSimilarity_rating() {
        double cur_srating = 0.00;
        for(int i = 0; i < NTEST; i++) {
            assertEquals(cur_srating, recommendations.get(i).getSimilarity_rating(),0.001);
            cur_srating += 0.01;
        }
    }

    @Test
    public void setBook_id() {
        Recommendation dummy = new Recommendation(user_id, book_id, 0.02);

        dummy.setBook_id("TEST_BID");
        assertEquals("TEST_BID", dummy.getBook_id());
    }

    @Test
    public void setUser_id() {
        Recommendation dummy = new Recommendation(user_id, book_id, 0.02);

        dummy.setUser_id("TEST_UID");
        assertEquals("TEST_UID", dummy.getUser_id());
    }

    @Test
    public void setSimilarity_rating() {
        Recommendation dummy = new Recommendation(user_id, book_id, 0.02);

        dummy.setSimilarity_rating(0.04);
        assertEquals(0.04, dummy.getSimilarity_rating(), 0.001);
    }
}