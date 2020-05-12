package com.reading7;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Objects.Book;
import com.reading7.Objects.Review;
import com.reading7.Objects.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        if (Utils.isAdmin)
            navigation.setVisibility(View.GONE);
        else
            navigation.setOnNavigationItemSelectedListener(this);

        initCurrentUser();

        final Intent intent = getIntent();
        if (intent.getStringExtra("type") != null)
            loadFragmentFromIntent(intent);

        else loadFragment(new ExploreFragment());

    }


    private void initCurrentUser() {

        CollectionReference requestCollectionRef = FirebaseFirestore.getInstance().collection("Users");
        Query requestQuery = requestCollectionRef.whereEqualTo("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mUser = document.toObject(User.class);
                    }
                }
            }
        });
    }

    public User getCurrentUser() {
        return mUser;
    }

    public void setCurrentUser(User user) {
        this.mUser = user;
    }


    /**
     * Replaces the current main fragment.
     */
    public boolean loadFragment(Fragment fragment) {

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmant_container, fragment, fragment.toString())
                    .addToBackStack(fragment.getClass().toString())
                    .commit();
            return true;
        }

        return false;
    }

    /**
     * Adds a new fragment on top of the current main fragment.
     * The current fragment will remain at it's current state.
     */
    public boolean addFragment(Fragment fragment) {

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragmant_container, fragment, fragment.toString())
                    .addToBackStack(fragment.getClass().toString())
                    .commit();
            return true;
        }

        return false;
    }

    private void loadFragmentFromIntent(Intent intent) {

        String type = intent.getStringExtra("type");

        if(type == null) {
            loadFragment(new ExploreFragment());
            return;
        }

        if (type.equals(getResources().getString(R.string.follow_notificiation_private))
                || type.equals(getResources().getString(R.string.follow_notificiation_public))
                || type.equals(getResources().getString(R.string.request_approved_notificiation))) {
            loadFragment(new PublicProfileFragment(intent.getStringExtra("from_email")));
            return;
        }

        if (type.equals(getResources().getString(R.string.invite_notificiation))) {
            Utils.enableDisableClicks(this, (ViewGroup) findViewById(android.R.id.content).getRootView(), false);
            Query bookRef = FirebaseFirestore.getInstance().collection("Books").whereEqualTo("title", intent.getStringExtra("book_title"));
            bookRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (final DocumentSnapshot doc : task.getResult()) {
                            Book b = doc.toObject(Book.class);
                            loadFragment(new BookFragment(b));
                            Utils.enableDisableClicks(MainActivity.this, (ViewGroup) findViewById(android.R.id.content).getRootView(), true);
                        }
                    }
                }
            });

        }
        if(type.equals(getResources().getString(R.string.challenge_notificiation))){
//            this.addFragment(new ChallengeFragment(intent.getStringExtra("book_title"), intent.getStringExtra("question_content"), intent.getStringExtra("possible_answers"), intent.getStringExtra("right_answer")));
//           this.addFragment(new ChallengeFragment(book_title,question_content,possible_answers,right_answer));
            loadFragment(new NotificationsFragment());
            return;
        }

        if ((type.equals(getResources().getString(R.string.like_notificiation)))
                || (type.equals(getResources().getString(R.string.comment_notificiation)))) {
            final Intent intent1 = intent;
            CollectionReference requestCollectionRef = FirebaseFirestore.getInstance().collection("Users");
            Query requestQuery = requestCollectionRef.whereEqualTo("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
            requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            mUser = document.toObject(User.class);
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            String book_title = intent1.getStringExtra("book_title");
                            CollectionReference requestCollectionRef = db.collection("Reviews");
                            Query requestQuery = requestCollectionRef.whereEqualTo("book_title", book_title).whereEqualTo("reviewer_email", mUser.getEmail());
                            requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {

                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            final Review review = document.toObject(Review.class);

                                            CollectionReference requestCollectionRef = FirebaseFirestore.getInstance().collection("Users");
                                            Query requestQuery = requestCollectionRef.whereEqualTo("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                            requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            mUser = document.toObject(User.class);
                                                            loadFragment(new ReviewCommentsFragment(review));
                                                            return;
                                                        }
                                                    }
                                                }
                                            });

                                        }

                                    }
                                }
                            });
                        }
                    }
                }
            });


        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Fragment fragment = null;

        int index = getSupportFragmentManager().getBackStackEntryCount() - 1;
        FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(index);

        switch (menuItem.getItemId()) {

            case R.id.navigation_home:
                if (backEntry.getName().equals("class com.reading7.HomeFragment"))
                    return true;
                fragment = new HomeFragment();
                break;

            case R.id.navigation_explore:
                if (backEntry.getName().equals("class com.reading7.ExploreFragment"))
                    return true;
                fragment = new ExploreFragment();
                break;

            case R.id.navigation_profile:
                if (backEntry.getName().equals("class com.reading7.ProfileFragment"))
                    return true;
                fragment = new ProfileFragment();
                break;
        }

        return loadFragment(fragment);
    }


    public void setBottomNavigationEnabled(boolean enabled) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.getMenu().findItem(R.id.navigation_home).setEnabled(enabled);
        bottomNavigationView.getMenu().findItem(R.id.navigation_explore).setEnabled(enabled);
        bottomNavigationView.getMenu().findItem(R.id.navigation_profile).setEnabled(enabled);

        if (enabled)
            bottomNavigationView.setAlpha((float) 1);
        else
            bottomNavigationView.setAlpha((float) 0.5);
    }


    @Override
    public void onBackPressed() {
        if (!Utils.clicksEnabled) return;

        int count = getSupportFragmentManager().getBackStackEntryCount();

        // Customize onBackPressed for specific fragments //
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmant_container);
        if (fragment instanceof ReviewCommentsFragment)
            if (((ReviewCommentsFragment) fragment).admin_delete)
                ((ReviewCommentsFragment) fragment).sendResultDeleted(404);
            else
                ((ReviewCommentsFragment) fragment).sendResult(303, ((ReviewCommentsFragment) fragment).getReviewId());
        else if (fragment instanceof BookFragment && ((BookFragment)fragment).admin_delete)
            ((BookFragment) fragment).sendResult(101);

        if (count == 1)
            finish();
        else if (getFragmentManager().getBackStackEntryCount() > 1)
            getFragmentManager().popBackStack();
        else
            super.onBackPressed();

    }

}
