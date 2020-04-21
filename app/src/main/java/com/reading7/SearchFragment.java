package com.reading7;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.duolingo.open.rtlviewpager.RtlViewPager;
import com.google.android.material.tabs.TabLayout;
import com.reading7.Adapters.SearchBooksAdapter;
import com.reading7.Adapters.TabsPagerAdapter;
import com.reading7.Objects.Book;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment implements androidx.appcompat.widget.SearchView.OnQueryTextListener {

    androidx.appcompat.widget.SearchView searchView;
    RtlViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = getActivity().findViewById(R.id.viewPager);
        searchView = getActivity().findViewById(R.id.searchView);

        initTabsViewPager();
        initSearchView();
        initBackButton();
    }


    private void initSearchView() {

        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();

        //remove underline
        View v = getActivity().findViewById(androidx.appcompat.R.id.search_plate);
        v.setBackgroundColor(getActivity().getResources().getColor(R.color.transparent));

        //remove search icon
        ImageView searchViewIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        ((ViewGroup)searchViewIcon.getParent()).removeView(searchViewIcon);

        searchView.setOnQueryTextListener(this);
    }


    private void initBackButton() {

        ImageView backButton = getActivity().findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.closeKeyboard(getContext());
                getActivity().onBackPressed();
            }
        });
    }


    private void initTabsViewPager() {

        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());

//        tabsPagerAdapter.addFragment(new SearchBooksFragment(), "ספרים");//TODO remove
        ArrayList<Book> books = new ArrayList<Book>();
        tabsPagerAdapter.addFragment(new GenericSearchFragment<Book>(Book.class, new SearchBooksAdapter(getContext(), books), books), "ספרים"); //TODO implement
//        tabsPagerAdapter.addFragment(new SearchAuthorsFragment(), "סופרים");
        tabsPagerAdapter.addFragment(new SearchFriendsFragment(), "חברים");

        final ViewPager viewPager = getActivity().findViewById(R.id.viewPager);
        viewPager.setAdapter(tabsPagerAdapter);

        TabLayout tabs = getActivity().findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }


    public void initViewPagerOnPageChanged() {

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                String s = searchView.getQuery().toString();
                //FIXME commented code is correct for 3 tabs (Books, Authors and members search). current code works only for 2 tabs (Books and members)
//                switch (position){
//                    case 0:
//                        ((SearchBooksFragment)((TabsPagerAdapter)viewPager.getAdapter()).getItem(position)).onQueryTextChange(s);
//                        break;
//                    case 1:
//                        ((SearchAuthorsFragment)((TabsPagerAdapter)viewPager.getAdapter()).getItem(position)).onQueryTextChange(s);
//                        break;
//                    case 2:
//                        ((SearchFriendsFragment)((TabsPagerAdapter)viewPager.getAdapter()).getItem(position)).onQueryTextChange(s);
//                        break;
//                }
                switch (position){
                    case 0:
                        ((SearchBooksFragment)((TabsPagerAdapter)viewPager.getAdapter()).getItem(position)).onQueryTextChange(s);
                        break;
//                    case 1:
//                        ((SearchAuthorsFragment)((TabsPagerAdapter)viewPager.getAdapter()).getItem(position)).onQueryTextChange(s);
//                        break;
                    case 1:
                        ((SearchFriendsFragment)((TabsPagerAdapter)viewPager.getAdapter()).getItem(position)).onQueryTextChange(s);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public boolean onQueryTextSubmit(String s) {

        Fragment frag = ((TabsPagerAdapter)viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
        switch (viewPager.getCurrentItem()) {

            case 0:
                ((GenericSearchFragment)frag).onQueryTextChange(s);
                break;
            case 1:
                ((SearchAuthorsFragment)frag).onQueryTextChange(s);
                break;
            case 2:
                ((SearchFriendsFragment)frag).onQueryTextChange(s);
                break;
        }
        return true;

    }

    @Override
    public boolean onQueryTextChange(String s) {

        Fragment frag = ((TabsPagerAdapter)viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
        switch (viewPager.getCurrentItem()) {

            case 0:
                ((GenericSearchFragment)frag).onQueryTextChange(s);
                break;
                //FIXME currently only books and members allowed
//            case 1:
//                ((SearchAuthorsFragment)frag).onQueryTextChange(s);
//                break;
            case 1:
                ((SearchFriendsFragment)frag).onQueryTextChange(s);
                break;
        }
        return true;
    }

}
