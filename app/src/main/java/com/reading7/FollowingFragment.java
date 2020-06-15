package com.reading7;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.reading7.Adapters.FollowingAdapter;
import com.reading7.Adapters.TabsPagerAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class FollowingFragment extends Fragment {

    private FollowingFragmentType currentTabType;


    FollowingFragment(FollowingFragmentType type) {
        this.currentTabType = type;
    }

    public FollowingFragmentType getCurrentTabType() {
        return currentTabType;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.following_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBackButton();
        initTitle();
        initTabsViewPager();
    }


    private void initBackButton() {
        getActivity().findViewById(R.id.followingBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void initTitle() {
        TextView title = getView().findViewById(R.id.my_title);
        String name = ((MainActivity) getActivity()).getCurrentUser().getFull_name();
        title.setText(name);
    }

    public void initTabsViewPager() {

        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());

        ArrayList<String> followersEmails = ((MainActivity) getActivity()).getCurrentUser().getFollowers();
        tabsPagerAdapter.addFragment(new FollowingTabFragment(followersEmails, this), followersEmails.size() + " " + "עוקבים");

        ArrayList<String> followingEmails = ((MainActivity) getActivity()).getCurrentUser().getFollowing();
        tabsPagerAdapter.addFragment(new FollowingTabFragment(followingEmails, this), followingEmails.size() + " " + "נעקבים");

        final ViewPager viewPager = getActivity().findViewById(R.id.viewPager);
        viewPager.setAdapter(tabsPagerAdapter);

        TabLayout tabs = getActivity().findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.selectTab(tabs.getTabAt(currentTabType.ordinal()));
    }


    /**
     * Updates the Followers tab title.
     */
    public void updateFollowersNum() {
        TabLayout tabs = getActivity().findViewById(R.id.tabs);
        ArrayList<String> followersEmails = ((MainActivity) getActivity()).getCurrentUser().getFollowers();
        tabs.getTabAt(0).setText(followersEmails.size() + " " + "עוקבים");
    }

    /**
     * Increases the number represented in Following tab title.
     */
    public void increaseFollowingNum() {
        TabLayout tabs = getActivity().findViewById(R.id.tabs);
        ArrayList<String> followingEmails = ((MainActivity) getActivity()).getCurrentUser().getFollowing();
        tabs.getTabAt(1).setText(followingEmails.size() + " " + "נעקבים");

        final ViewPager viewPager = getActivity().findViewById(R.id.viewPager);
        FollowingTabFragment fragment = (FollowingTabFragment) ((TabsPagerAdapter) viewPager.getAdapter()).getItem(1);
        fragment.notifyItemInserted();
    }

    /**
     * Decreases the number represented in Following tab title.
     */
    public void decreaseFollowingNum() {
        TabLayout tabs = getActivity().findViewById(R.id.tabs);
        ArrayList<String> followingEmails = ((MainActivity) getActivity()).getCurrentUser().getFollowing();
        tabs.getTabAt(1).setText(followingEmails.size() + " " + "נעקבים");
    }


    public enum FollowingFragmentType {
        FOLLOWERS,
        FOLLOWING
    }


    public static class FollowingTabFragment extends Fragment {

        private ArrayList<String> emails;
        private FollowingFragment fragment;
        private FollowingAdapter adapter;

        public FollowingTabFragment(ArrayList<String> emails, FollowingFragment fragment) {
            this.emails = emails;
            this.fragment = fragment;
        }


        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.following_tab_fragment, null);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

            RecyclerView usersRV = getView().findViewById(R.id.usersRV);
            usersRV.setHasFixedSize(true);
            usersRV.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new FollowingAdapter(getContext(), fragment, emails);
            usersRV.setAdapter(adapter);

            super.onViewCreated(view, savedInstanceState);
        }

        public void notifyItemInserted() {
            adapter.notifyItemInserted(emails.size() - 1);
        }
    }
}