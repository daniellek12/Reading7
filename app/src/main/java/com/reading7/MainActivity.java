package com.reading7;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Objects.Book;
import com.reading7.Objects.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        initCurrentUser();


        Intent intent = getIntent();

        if(intent.getStringExtra("type") != null) {

            String type = intent.getStringExtra("type");

            if(type.equals( getResources().getString(R.string.follow_notificiation_private))||
                    type.equals( getResources().getString(R.string.follow_notificiation_public))||
                    type.equals( getResources().getString(R.string.request_approved_notificiation))) {
                loadFragment(new PublicProfileFragment(intent.getStringExtra("from_email")));
                return;
            }

            if(type.equals(getResources().getString(R.string.like_notificiation))){

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String book_title = intent.getStringExtra("book_title");
                CollectionReference requestCollectionRef = db.collection("Books");
                Query requestQuery = requestCollectionRef.whereEqualTo("title",book_title);
                requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            Book b = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                b = document.toObject(Book.class);
                               loadFragment(new BookFragment(b));
                                return;
                            }

                        }
                    }
                });
            }
        }

        else loadFragment(new ExploreFragment());
    }

    public User getUser(){
        return mUser;
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


    private void initCurrentUser(){

        CollectionReference requestCollectionRef = FirebaseFirestore.getInstance().collection("Users");
        Query requestQuery = requestCollectionRef.whereEqualTo("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
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
    public void setCurrentUser( User user) {
        this.mUser = user;
    }
    @Override
    public void onBackPressed()
    {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 1)
            finish();
        else
         super.onBackPressed();
    }

}
