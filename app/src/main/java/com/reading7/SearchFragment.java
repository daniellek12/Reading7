package com.reading7;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.reading7.Adapters.TabsPagerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


public class SearchFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initSearchView();
        initBackButton();
        initTabsViewPager();

    }

    private void initSearchView(){

        androidx.appcompat.widget.SearchView searchView = getActivity().findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();

        //open keyboard
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        //remove underline
        View v = getActivity().findViewById(androidx.appcompat.R.id.search_plate);
        v.setBackgroundColor(getActivity().getResources().getColor(R.color.transparent));

        //remove close button
        ImageView closeBtn = (ImageView)searchView.findViewById(R.id.search_close_btn);
        closeBtn.setEnabled(false);
        closeBtn.setImageDrawable(null);

        //remove search icon
        ImageView searchViewIcon = (ImageView)searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        ((ViewGroup)searchViewIcon.getParent()).removeView(searchViewIcon);

    }

    private void initBackButton() {

        ImageButton backButton = getActivity().findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //close keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                getActivity().onBackPressed();
            }
        });

    }

    private void initTabsViewPager() {

        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());

        //TODO: change this to real fragments
        tabsPagerAdapter.addFragment(new SearchBooksFragment(), "ספרים");
        tabsPagerAdapter.addFragment(new SearchBooksFragment(), "סופרים");
        tabsPagerAdapter.addFragment(new SearchBooksFragment(), "חברים");

        ViewPager viewPager = getActivity().findViewById(R.id.viewPager);
        viewPager.setAdapter(tabsPagerAdapter);

        TabLayout tabs = getActivity().findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(2);
    }

}
