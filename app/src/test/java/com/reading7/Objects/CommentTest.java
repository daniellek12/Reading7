package com.reading7.Objects;

import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static com.google.firebase.Timestamp.now;
import static org.junit.Assert.*;

public class CommentTest {
    ArrayList<Comment> comments = new ArrayList<>();
    int NTEST = 10; // modify as you like
    private String review_id = "r_id";
    private String commenter_email = "c_mail";
    private com.google.firebase.Timestamp comment_time;
    private String comment_content = "c_cntnt";

    ArrayList<com.google.firebase.Timestamp> times = new ArrayList<>();


    @Before
    public void setUp() throws Exception {
        for (int i = 0; i < NTEST; i++) {
            String scur = String.valueOf(i);
            comment_time = now();
            times.add(comment_time);
            comments.add(new Comment(review_id + scur, commenter_email +
                    scur,comment_content + scur, comment_time));
        }
    }

    @Test
    public void getReview_id() {
        for (int i = 0; i < NTEST; i++) {
            String scur = String.valueOf(i);
            assertEquals(review_id + String.valueOf(i), comments.get(i).getReview_id());
        }
    }

    @Test
    public void setReview_id() {
        Timestamp time = now();
        Comment dummy = new Comment(review_id, commenter_email, comment_content, time);
        dummy.setReview_id("TEST_RID");
        assertEquals("TEST_RID", dummy.getReview_id());
    }

    @Test
    public void getCommenter_email() {
        for (int i = 0; i < NTEST; i++) {
            String scur = String.valueOf(i);
            assertEquals(commenter_email + String.valueOf(i), comments.get(i).getCommenter_email());
        }
    }

    @Test
    public void setCommenter_email() {
        Timestamp time = now();
        Comment dummy = new Comment(review_id, commenter_email, comment_content, time);
        dummy.setCommenter_email("TEST_MAIL");
        assertEquals("TEST_MAIL", dummy.getCommenter_email());
    }

    @Test
    public void getComment_content() {
        for (int i = 0; i < NTEST; i++) {
            String scur = String.valueOf(i);
            assertEquals(comment_content + String.valueOf(i), comments.get(i).getComment_content());
        }
    }

    @Test
    public void setComment_content() {
        Timestamp time = now();
        Comment dummy = new Comment(review_id, commenter_email, comment_content, time);
        dummy.setComment_content("TEST_CONTENT");
        assertEquals("TEST_CONTENT", dummy.getComment_content());
    }

    @Test
    public void getComment_time() {
        for (int i = 0; i < NTEST; i++) {
            assertEquals(times.get(i), comments.get(i).getComment_time());
        }
    }

    @Test
    public void setComment_time() {
        Timestamp time = now();
        Comment dummy = new Comment(review_id, commenter_email, comment_content, time);
        Timestamp new_time = now();
        dummy.setComment_time(new_time);
        assertEquals(new_time, dummy.getComment_time());
    }
}