package com.reading7.Objects;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class UserTest {
    ArrayList<User> users = new ArrayList<>();

    private String full_name = "fn";
    private String email = "mail";
    private String birth_date = "bdate";
    int NTEST = 10; // modify as you like
    int sub_ntest = 12; // modify as you like

    private Boolean is_private = false;
    private Avatar avatar = new Avatar();
    private int points = 0;

    String sfollowers = "followers";
    String sfollowing = "following";
    String sfollow_requests = "follow_rqst";
    String slast_searches = "last_srchs";
    String sfavourite_books = "fvrt_books";
    String sfavourite_genres = "fvrt_genres";
    String sliked_reviews = "likes_rvws";

    @Before
    public void setUp() throws Exception {
        for (int i = 0; i < NTEST; i++) {
            ArrayList<String> cur_followers = new ArrayList<>();            // Emails list of the followers
            ArrayList<String> cur_following = new ArrayList<>();            // Emails list of the following
            ArrayList<String> cur_follow_requests = new ArrayList<>();      // Emails list of the followers requested to follow user
            ArrayList<String> cur_last_searches = new ArrayList<>();
            ArrayList<String> cur_favourite_books = new ArrayList<>();      // top books to view on profile
            ArrayList<String> cur_favourite_genres = new ArrayList<>();
            ArrayList<String> cur_liked_reviews = new ArrayList<>();

            for (int j = 0; j < sub_ntest; j++) {
                cur_followers.add(sfollowers + String.valueOf(i) + "-" + String.valueOf(j));
                cur_following.add(sfollowing + String.valueOf(i) + "-" + String.valueOf(j));
                cur_follow_requests.add(sfollow_requests + String.valueOf(i) + "-" + String.valueOf(j));
                cur_last_searches.add(slast_searches + String.valueOf(i) + "-" + String.valueOf(j));
                cur_favourite_books.add(sfavourite_books + String.valueOf(i) + "-" + String.valueOf(j));
                cur_favourite_genres.add(sfavourite_genres + String.valueOf(i) + "-" + String.valueOf(j));
                cur_liked_reviews.add(sliked_reviews + String.valueOf(i) + "-" + String.valueOf(j));
            }

            String scur = String.valueOf(i);
            User user = new User(full_name + scur, email + scur, birth_date + scur,
                    cur_followers, cur_following, cur_follow_requests, cur_last_searches, cur_favourite_books, cur_favourite_genres, cur_liked_reviews,
                    avatar, is_private, points);
            points += 1;
            is_private = !is_private;

            users.add(user);
        }
    }

    @Test
    public void getFollow_requests() {
        for (int i = 0; i < NTEST; i++) {
            ArrayList<String> cur_follow_requests = users.get(i).getFollow_requests();
            for (int j = 0; j < sub_ntest; j++) {
                assertEquals( sfollow_requests + String.valueOf(i) + "-" + String.valueOf(j), cur_follow_requests.get(j));
            }
        }
    }

    @Test
    public void setFollow_requests() {
        ArrayList<String> cur_followers = new ArrayList<>();            // Emails list of the followers
        ArrayList<String> cur_following = new ArrayList<>();            // Emails list of the following
        ArrayList<String> cur_follow_requests = new ArrayList<>();      // Emails list of the followers requested to follow user
        ArrayList<String> cur_last_searches = new ArrayList<>();
        ArrayList<String> cur_favourite_books = new ArrayList<>();      // top books to view on profile
        ArrayList<String> cur_favourite_genres = new ArrayList<>();
        ArrayList<String> cur_liked_reviews = new ArrayList<>();

        ArrayList<String> set_cur_follow_requests = new ArrayList<>();

        for (int j = 0; j < sub_ntest; j++) {
            set_cur_follow_requests.add(sfollow_requests + String.valueOf(j));
        }

        User dummy = new User(full_name, email, birth_date, cur_followers, cur_following,
                cur_follow_requests, cur_last_searches, cur_favourite_books, cur_favourite_genres, cur_liked_reviews,
                avatar, is_private, points);

        dummy.setFollow_requests(set_cur_follow_requests);

        ArrayList<String> from_dummy = dummy.getFollow_requests();

        for (int j = 0; j < sub_ntest; j++) {
            assertEquals(set_cur_follow_requests.get(j), from_dummy.get(j));
        }
    }

    @Test
    public void getFull_name() {
        for (int i = 0; i < NTEST; i++) {
            assertEquals(full_name + String.valueOf(i), users.get(i).getFull_name());
        }
    }

    @Test
    public void setFull_name() {
        ArrayList<String> cur_followers = new ArrayList<>();
        ArrayList<String> cur_following = new ArrayList<>();
        ArrayList<String> cur_follow_requests = new ArrayList<>();
        ArrayList<String> cur_last_searches = new ArrayList<>();
        ArrayList<String> cur_favourite_books = new ArrayList<>();
        ArrayList<String> cur_favourite_genres = new ArrayList<>();
        ArrayList<String> cur_liked_reviews = new ArrayList<>();

        User dummy = new User(full_name, email, birth_date, cur_followers, cur_following,
                cur_follow_requests, cur_last_searches, cur_favourite_books, cur_favourite_genres, cur_liked_reviews,
                avatar, is_private, points);

        dummy.setFull_name("TEST_USER");

        assertEquals("TEST_USER", dummy.getFull_name());
    }

    @Test
    public void getEmail() {
        for (int i = 0; i < NTEST; i++) {
            assertEquals(email + String.valueOf(i), users.get(i).getEmail());
        }
    }

    @Test
    public void setEmail() {
        ArrayList<String> cur_followers = new ArrayList<>();
        ArrayList<String> cur_following = new ArrayList<>();
        ArrayList<String> cur_follow_requests = new ArrayList<>();
        ArrayList<String> cur_last_searches = new ArrayList<>();
        ArrayList<String> cur_favourite_books = new ArrayList<>();
        ArrayList<String> cur_favourite_genres = new ArrayList<>();
        ArrayList<String> cur_liked_reviews = new ArrayList<>();

        User dummy = new User(full_name, email, birth_date, cur_followers, cur_following,
                cur_follow_requests, cur_last_searches, cur_favourite_books, cur_favourite_genres, cur_liked_reviews,
                avatar, is_private, points);

        dummy.setEmail("TEST_EMAIL");

        assertEquals("TEST_EMAIL", dummy.getEmail());
    }

    @Test
    public void getBirth_date() {
        for (int i = 0; i < NTEST; i++) {
            assertEquals(birth_date + String.valueOf(i), users.get(i).getBirth_date());
        }
    }

    @Test
    public void getIs_private() {
        for (int i = 0; i < NTEST; i++) {
            assertEquals(!(i % 2 == 0), users.get(i).getIs_private());
        }
    }

    @Test
    public void setIs_private() {
        ArrayList<String> cur_followers = new ArrayList<>();
        ArrayList<String> cur_following = new ArrayList<>();
        ArrayList<String> cur_follow_requests = new ArrayList<>();
        ArrayList<String> cur_last_searches = new ArrayList<>();
        ArrayList<String> cur_favourite_books = new ArrayList<>();
        ArrayList<String> cur_favourite_genres = new ArrayList<>();
        ArrayList<String> cur_liked_reviews = new ArrayList<>();

        User dummy = new User(full_name, email, birth_date, cur_followers, cur_following,
                cur_follow_requests, cur_last_searches, cur_favourite_books, cur_favourite_genres, cur_liked_reviews,
                avatar, is_private, points);

        dummy.setIs_private(true);
        assertEquals(true, dummy.getIs_private());

        dummy.setIs_private(false);
        assertEquals(false, dummy.getIs_private());
    }

    @Test
    public void setBirth_date() {
        ArrayList<String> cur_followers = new ArrayList<>();
        ArrayList<String> cur_following = new ArrayList<>();
        ArrayList<String> cur_follow_requests = new ArrayList<>();
        ArrayList<String> cur_last_searches = new ArrayList<>();
        ArrayList<String> cur_favourite_books = new ArrayList<>();
        ArrayList<String> cur_favourite_genres = new ArrayList<>();
        ArrayList<String> cur_liked_reviews = new ArrayList<>();

        User dummy = new User(full_name, email, birth_date, cur_followers, cur_following,
                cur_follow_requests, cur_last_searches, cur_favourite_books, cur_favourite_genres, cur_liked_reviews,
                avatar, is_private, points);

        dummy.setBirth_date("TEST_BDATE");
        assertEquals("TEST_BDATE", dummy.getBirth_date());
    }

    @Test
    public void getFollowers() {
        for (int i = 0; i < NTEST; i++) {
            ArrayList<String> cur_followers = users.get(i).getFollowers();
            for (int j = 0; j < sub_ntest; j++) {
                assertEquals(sfollowers + String.valueOf(i) + "-" + String.valueOf(j), cur_followers.get(j));
            }
        }
    }

    @Test
    public void setFollowers() {
        ArrayList<String> cur_followers = new ArrayList<>();            // Emails list of the followers
        ArrayList<String> cur_following = new ArrayList<>();            // Emails list of the following
        ArrayList<String> cur_follow_requests = new ArrayList<>();      // Emails list of the followers requested to follow user
        ArrayList<String> cur_last_searches = new ArrayList<>();
        ArrayList<String> cur_favourite_books = new ArrayList<>();      // top books to view on profile
        ArrayList<String> cur_favourite_genres = new ArrayList<>();
        ArrayList<String> cur_liked_reviews = new ArrayList<>();

        ArrayList<String> set_cur_followers = new ArrayList<>();

        for (int j = 0; j < sub_ntest; j++) {
            set_cur_followers.add(sfollowers + String.valueOf(j));
        }

        User dummy = new User(full_name, email, birth_date, cur_followers, cur_following,
                cur_follow_requests, cur_last_searches, cur_favourite_books, cur_favourite_genres, cur_liked_reviews,
                avatar, is_private, points);

        dummy.setFollowers(set_cur_followers);

        ArrayList<String> from_dummy = dummy.getFollowers();

        for (int j = 0; j < sub_ntest; j++) {
            assertEquals(set_cur_followers.get(j), from_dummy.get(j));
        }
    }

    @Test
    public void getFollowing() {
        for (int i = 0; i < NTEST; i++) {
            ArrayList<String> cur_following = users.get(i).getFollowing();
            for (int j = 0; j < sub_ntest; j++) {
                assertEquals(sfollowing + String.valueOf(i) + "-" + String.valueOf(j), cur_following.get(j));
            }
        }
    }

    @Test
    public void setFollowing() {
        ArrayList<String> cur_followers = new ArrayList<>();
        ArrayList<String> cur_following = new ArrayList<>();
        ArrayList<String> cur_follow_requests = new ArrayList<>();
        ArrayList<String> cur_last_searches = new ArrayList<>();
        ArrayList<String> cur_favourite_books = new ArrayList<>();
        ArrayList<String> cur_favourite_genres = new ArrayList<>();
        ArrayList<String> cur_liked_reviews = new ArrayList<>();

        ArrayList<String> set_cur_following = new ArrayList<>();

        for (int j = 0; j < sub_ntest; j++) {
            set_cur_following.add(sfollowing + String.valueOf(j));
        }

        User dummy = new User(full_name, email, birth_date, cur_followers, cur_following,
                cur_follow_requests, cur_last_searches, cur_favourite_books, cur_favourite_genres, cur_liked_reviews,
                avatar, is_private, points);

        dummy.setFollowing(set_cur_following);

        ArrayList<String> from_dummy = dummy.getFollowing();

        for (int j = 0; j < sub_ntest; j++) {
            assertEquals(set_cur_following.get(j), from_dummy.get(j));
        }
    }

    @Test
    public void getLast_searches() {
        for (int i = 0; i < NTEST; i++) {
            ArrayList<String> cur_last_srchs = users.get(i).getLast_searches();
            for (int j = 0; j < sub_ntest; j++) {
                assertEquals(slast_searches + String.valueOf(i) + "-" + String.valueOf(j), cur_last_srchs.get(j));
            }
        }
    }

    @Test
    public void setLast_searches() {
        ArrayList<String> cur_followers = new ArrayList<>();
        ArrayList<String> cur_following = new ArrayList<>();
        ArrayList<String> cur_follow_requests = new ArrayList<>();
        ArrayList<String> cur_last_searches = new ArrayList<>();
        ArrayList<String> cur_favourite_books = new ArrayList<>();
        ArrayList<String> cur_favourite_genres = new ArrayList<>();
        ArrayList<String> cur_liked_reviews = new ArrayList<>();

        ArrayList<String> set_cur_last_search = new ArrayList<>();

        for (int j = 0; j < sub_ntest; j++) {
            set_cur_last_search.add(slast_searches + String.valueOf(j));
        }

        User dummy = new User(full_name, email, birth_date, cur_followers, cur_following,
                cur_follow_requests, cur_last_searches, cur_favourite_books, cur_favourite_genres, cur_liked_reviews,
                avatar, is_private, points);

        dummy.setLast_searches(set_cur_last_search);

        ArrayList<String> from_dummy = dummy.getLast_searches();

        for (int j = 0; j < sub_ntest; j++) {
            assertEquals(set_cur_last_search.get(j), from_dummy.get(j));
        }
    }

    @Test
    public void getFavourite_books() {
        for (int i = 0; i < NTEST; i++) {
            ArrayList<String> cur_fvt_books = users.get(i).getFavourite_books();
            for (int j = 0; j < sub_ntest; j++) {
                assertEquals(sfavourite_books + String.valueOf(i) + "-" + String.valueOf(j), cur_fvt_books.get(j));
            }
        }
    }

    @Test
    public void setFavourite_books() {
        ArrayList<String> cur_followers = new ArrayList<>();
        ArrayList<String> cur_following = new ArrayList<>();
        ArrayList<String> cur_follow_requests = new ArrayList<>();
        ArrayList<String> cur_last_searches = new ArrayList<>();
        ArrayList<String> cur_favourite_books = new ArrayList<>();
        ArrayList<String> cur_favourite_genres = new ArrayList<>();
        ArrayList<String> cur_liked_reviews = new ArrayList<>();

        ArrayList<String> set_cur_fav_books = new ArrayList<>();

        for (int j = 0; j < sub_ntest; j++) {
            set_cur_fav_books.add(sfavourite_books + String.valueOf(j));
        }

        User dummy = new User(full_name, email, birth_date, cur_followers, cur_following,
                cur_follow_requests, cur_last_searches, cur_favourite_books, cur_favourite_genres, cur_liked_reviews,
                avatar, is_private, points);

        dummy.setFavourite_books(set_cur_fav_books);

        ArrayList<String> from_dummy = dummy.getFavourite_books();

        for (int j = 0; j < sub_ntest; j++) {
            assertEquals(set_cur_fav_books.get(j), from_dummy.get(j));
        }
    }

    @Test
    public void getFavourite_genres() {
        for (int i = 0; i < NTEST; i++) {
            ArrayList<String> cur_fvt_genres = users.get(i).getFavourite_genres();
            for (int j = 0; j < sub_ntest; j++) {
                assertEquals(sfavourite_genres + String.valueOf(i) + "-" + String.valueOf(j), cur_fvt_genres.get(j));
            }
        }
    }

    @Test
    public void setFavourite_genres() {
        ArrayList<String> cur_followers = new ArrayList<>();
        ArrayList<String> cur_following = new ArrayList<>();
        ArrayList<String> cur_follow_requests = new ArrayList<>();
        ArrayList<String> cur_last_searches = new ArrayList<>();
        ArrayList<String> cur_favourite_books = new ArrayList<>();
        ArrayList<String> cur_favourite_genres = new ArrayList<>();
        ArrayList<String> cur_liked_reviews = new ArrayList<>();

        ArrayList<String> set_cur_fav_genres = new ArrayList<>();

        for (int j = 0; j < sub_ntest; j++) {
            set_cur_fav_genres.add(sfavourite_books + String.valueOf(j));
        }

        User dummy = new User(full_name, email, birth_date, cur_followers, cur_following,
                cur_follow_requests, cur_last_searches, cur_favourite_books, cur_favourite_genres, cur_liked_reviews,
                avatar, is_private, points);

        dummy.setFavourite_genres(set_cur_fav_genres);

        ArrayList<String> from_dummy = dummy.getFavourite_genres();

        for (int j = 0; j < sub_ntest; j++) {
            assertEquals(set_cur_fav_genres.get(j), from_dummy.get(j));
        }
    }

    @Test
    public void getLiked_reviews() {
        for (int i = 0; i < NTEST; i++) {
            ArrayList<String> cur_liked_reviews = users.get(i).getLiked_reviews();
            for (int j = 0; j < sub_ntest; j++) {
                assertEquals(sliked_reviews + String.valueOf(i) + "-" + String.valueOf(j), cur_liked_reviews.get(j));
            }
        }
    }

    @Test
    public void setLiked_reviews() {
        ArrayList<String> cur_followers = new ArrayList<>();
        ArrayList<String> cur_following = new ArrayList<>();
        ArrayList<String> cur_follow_requests = new ArrayList<>();
        ArrayList<String> cur_last_searches = new ArrayList<>();
        ArrayList<String> cur_favourite_books = new ArrayList<>();
        ArrayList<String> cur_favourite_genres = new ArrayList<>();
        ArrayList<String> cur_liked_reviews = new ArrayList<>();

        ArrayList<String> set_cur_liked_reviews = new ArrayList<>();

        for (int j = 0; j < sub_ntest; j++) {
            set_cur_liked_reviews.add(sfavourite_books + String.valueOf(j));
        }

        User dummy = new User(full_name, email, birth_date, cur_followers, cur_following,
                cur_follow_requests, cur_last_searches, cur_favourite_books, cur_favourite_genres, cur_liked_reviews,
                avatar, is_private, points);

        dummy.setLiked_reviews(set_cur_liked_reviews);

        ArrayList<String> from_dummy = dummy.getLiked_reviews();

        for (int j = 0; j < sub_ntest; j++) {
            assertEquals(set_cur_liked_reviews.get(j), from_dummy.get(j));
        }
    }

    @Test
    public void remove_like() {
        ArrayList<String> cur_followers = new ArrayList<>();
        ArrayList<String> cur_following = new ArrayList<>();
        ArrayList<String> cur_follow_requests = new ArrayList<>();
        ArrayList<String> cur_last_searches = new ArrayList<>();
        ArrayList<String> cur_favourite_books = new ArrayList<>();
        ArrayList<String> cur_favourite_genres = new ArrayList<>();
        ArrayList<String> cur_liked_reviews = new ArrayList<>();

        for (int j = 0; j < sub_ntest; j++) {
            cur_liked_reviews.add(sliked_reviews + String.valueOf(j));
        }

        User dummy = new User(full_name, email, birth_date, cur_followers, cur_following,
                cur_follow_requests, cur_last_searches, cur_favourite_books, cur_favourite_genres, cur_liked_reviews,
                avatar, is_private, points);

        for (int j = 0; j < sub_ntest / 2; j++) {
            dummy.remove_like(sliked_reviews + String.valueOf(j));
        }

        ArrayList<String> from_dummy = dummy.getLiked_reviews();
        assertEquals(sub_ntest / 2, from_dummy.size());
    }

    @Test
    public void add_like() {
        ArrayList<String> cur_followers = new ArrayList<>();
        ArrayList<String> cur_following = new ArrayList<>();
        ArrayList<String> cur_follow_requests = new ArrayList<>();
        ArrayList<String> cur_last_searches = new ArrayList<>();
        ArrayList<String> cur_favourite_books = new ArrayList<>();
        ArrayList<String> cur_favourite_genres = new ArrayList<>();
        ArrayList<String> cur_liked_reviews = new ArrayList<>();

        for (int j = 0; j < sub_ntest; j++) {
            cur_liked_reviews.add(sliked_reviews + String.valueOf(j));
        }

        User dummy = new User(full_name, email, birth_date, cur_followers, cur_following,
                cur_follow_requests, cur_last_searches, cur_favourite_books, cur_favourite_genres, cur_liked_reviews,
                avatar, is_private, points);

        for (int j = sub_ntest; j < sub_ntest * 2; j++) {
            dummy.add_like(sliked_reviews + String.valueOf(j));
        }

        ArrayList<String> from_dummy = dummy.getLiked_reviews();
        assertEquals(sub_ntest * 2 , from_dummy.size());

        for (int j = 0; j < sub_ntest * 2; j++) {
            assertEquals(sliked_reviews + String.valueOf(j) , from_dummy.get(j));
        }
    }

    @Test
    public void getPoints() {
        int cur_points = 0;
        for (int i = 0; i < NTEST; i++) {
            assertEquals(cur_points ,users.get(i).getPoints());
            cur_points += 1;
        }
    }

    @Test
    public void setPoints() {
        ArrayList<String> cur_followers = new ArrayList<>();
        ArrayList<String> cur_following = new ArrayList<>();
        ArrayList<String> cur_follow_requests = new ArrayList<>();
        ArrayList<String> cur_last_searches = new ArrayList<>();
        ArrayList<String> cur_favourite_books = new ArrayList<>();
        ArrayList<String> cur_favourite_genres = new ArrayList<>();
        ArrayList<String> cur_liked_reviews = new ArrayList<>();

        User dummy = new User(full_name, email, birth_date, cur_followers, cur_following,
                cur_follow_requests, cur_last_searches, cur_favourite_books, cur_favourite_genres, cur_liked_reviews,
                avatar, is_private, points);

        dummy.setPoints(1000000);

        assertEquals(1000000, dummy.getPoints());
    }

    @Test
    public void addPoints() {
        ArrayList<String> cur_followers = new ArrayList<>();
        ArrayList<String> cur_following = new ArrayList<>();
        ArrayList<String> cur_follow_requests = new ArrayList<>();
        ArrayList<String> cur_last_searches = new ArrayList<>();
        ArrayList<String> cur_favourite_books = new ArrayList<>();
        ArrayList<String> cur_favourite_genres = new ArrayList<>();
        ArrayList<String> cur_liked_reviews = new ArrayList<>();

        User dummy = new User(full_name, email, birth_date, cur_followers, cur_following,
                cur_follow_requests, cur_last_searches, cur_favourite_books, cur_favourite_genres, cur_liked_reviews,
                avatar, is_private, points);

        dummy.setPoints(1000000);
        dummy.addPoints(2143567);
        assertEquals(1000000 + 2143567, dummy.getPoints());
    }

    @Test
    public void reducePoints() {
        ArrayList<String> cur_followers = new ArrayList<>();
        ArrayList<String> cur_following = new ArrayList<>();
        ArrayList<String> cur_follow_requests = new ArrayList<>();
        ArrayList<String> cur_last_searches = new ArrayList<>();
        ArrayList<String> cur_favourite_books = new ArrayList<>();
        ArrayList<String> cur_favourite_genres = new ArrayList<>();
        ArrayList<String> cur_liked_reviews = new ArrayList<>();

        User dummy = new User(full_name, email, birth_date, cur_followers, cur_following,
                cur_follow_requests, cur_last_searches, cur_favourite_books, cur_favourite_genres, cur_liked_reviews,
                avatar, is_private, points);

        dummy.setPoints(1000000);
        dummy.reducePoints(12345);

        assertEquals(1000000 - 12345, dummy.getPoints());
    }
}