package com.reading7;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.reading7.Objects.Book;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        loadFragment(new ExploreFragment());
    }

    public boolean loadFragment(Fragment fragment) {

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmant_container, fragment)
                    .addToBackStack(fragment.getClass().toString())
                    .commit();
            return true;
        }

        return false;
    }


    public boolean loadBookFragment(Fragment fragment, Book book){

        if(fragment != null && book != null){

            ((BookFragment)fragment).setBook(book);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragmant_container, fragment)
                    .addToBackStack(fragment.getClass().toString())
                    .commit();
            return true;
        }

        return false;
    }


    public boolean loadPublicProfileFragment(Fragment fragment, String user_email) {

        if (fragment != null && user_email != null) {
            ((PublicProfileFragment)fragment).setUser(user_email);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragmant_container, fragment)
                    .addToBackStack(fragment.getClass().toString())
                    .commit();
            return true;
        }

        return false;
    }


    public boolean loadAuthorFragment(Fragment fragment, String author_name){

        if(fragment != null && author_name != null){

            ((AuthorFragment)fragment).setAuthor(author_name);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragmant_container, fragment)
                    .addToBackStack(fragment.getClass().toString())
                    .commit();
            return true;
        }

        return false;
    }


    public boolean loadShelfFragment(Fragment fragment){

        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragmant_container, fragment)
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


    public void disableBottomNavigation(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.getMenu().findItem(R.id.navigation_home).setEnabled(false);
        bottomNavigationView.getMenu().findItem(R.id.navigation_explore).setEnabled(false);
        bottomNavigationView.getMenu().findItem(R.id.navigation_profile).setEnabled(false);
    }

    public void enableBottomNavigation(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.getMenu().findItem(R.id.navigation_home).setEnabled(true);
        bottomNavigationView.getMenu().findItem(R.id.navigation_explore).setEnabled(true);
        bottomNavigationView.getMenu().findItem(R.id.navigation_profile).setEnabled(true);
    }


}
