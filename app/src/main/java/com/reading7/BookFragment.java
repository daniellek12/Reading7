package com.reading7;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reading7.Adapters.FeedAdapter;
import com.reading7.Adapters.ReviewListAdapter;

import java.util.ArrayList;


public class BookFragment extends Fragment {

    // Simplified class, for UI testing only
    public class Review{
        public String userName;
        public Float rating;
        public String postTime;
        public String content;

        public Review(String userName, Float rating, String postTime, String content){
            this.userName = userName;
            this.rating = rating;
            this.postTime = postTime;
            this.content = content;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.book_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initReviews();
    }

    private ArrayList<BookFragment.Review> getReviews() {
        ArrayList<BookFragment.Review> reviews = new ArrayList<>();

        reviews.add(new BookFragment.Review("רותם סלע", (float)5, "11.06.19", "אחד הטובים שקראתי! מומלץ בטירוף!!"));
        reviews.add(new BookFragment.Review("משה פרץ", (float)3.5, "11.12.18", "סביר פלוס עם סוף מפתיע"));
        reviews.add(new BookFragment.Review("רוני דלומי", (float)1, "11.06.18", "וואלה לא משהו, הייתי רוצה שהסופר יפרט יותר בחלק מהקטעים..."));
        reviews.add(new BookFragment.Review("עומר אדם", (float)4.5, "11.12.17", "אחד הטובים שקראתי! מומלץ בטירוף!!"));

        return reviews;
    }

    private void initReviews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        RecyclerView postsRV = getActivity().findViewById(R.id.reviews);
        postsRV.setLayoutManager(layoutManager);
        ReviewListAdapter adapter = new ReviewListAdapter(getActivity(), getReviews());
        postsRV.setAdapter(adapter);

    }
}
