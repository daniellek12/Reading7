package com.reading7;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.admin_activity);
        BottomNavigationView navigation = ((MainActivity)this).findViewById(R.id.navigation);
        navigation.setVisibility(View.GONE);
        //loadFragment(new AdminFragment());
    }

//    public void loadFragment(Fragment fragment) {
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragmant_container, fragment, fragment.toString())
//                .addToBackStack(fragment.getClass().toString())
//                .commit();
//    }

//    public boolean addFragment(Fragment fragment) {
//
//        if (fragment != null) {
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .add(R.id.fragmant_container, fragment, fragment.toString())
//                    .addToBackStack(fragment.getClass().toString())
//                    .commit();
//            return true;
//        }
//
//        return false;
//    }

    @Override
    public void onBackPressed() {
        if (!Utils.clicksEnabled) return;

        int count = getSupportFragmentManager().getBackStackEntryCount();

        // Customize onBackPressed for specific fragments //
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmant_container);
        if (fragment instanceof ReviewCommentsFragment)
            ((ReviewCommentsFragment) fragment).sendResult(303, ((ReviewCommentsFragment) fragment).getReviewId());

        if (count == 1) {
            FirebaseAuth.getInstance().signOut();
            finish();
        }
        else if (getFragmentManager().getBackStackEntryCount() > 1)
            getFragmentManager().popBackStack();
        else
            super.onBackPressed();

    }
}
