package com.reading7;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.ReviewListAdapter;
import com.reading7.Objects.Book;
import com.reading7.Objects.Review;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewsFragment extends Fragment {

    private final ArrayList<Review> contentReviews = new ArrayList<>();
    private final ArrayList<Review> noContentReviews = new ArrayList<>();
    private BookFragment bookFragment;


    public ReviewsFragment(BookFragment bookFragment) {
        this.bookFragment = bookFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.reviews_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ((TextView) view.findViewById(R.id.reviews_toolbar_title)).setText(getString(R.string.books_reviews, bookFragment.getBook().getTitle()));
        initReviews();
        initBackButton();
    }


    private void initBackButton() {
        getActivity().findViewById(R.id.reviewsBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void initReviews() {

        Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), false);

        final RecyclerView reviewsRV = getActivity().findViewById(R.id.reviewsRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        reviewsRV.setLayoutManager(layoutManager);

        contentReviews.clear();
        noContentReviews.clear();

        FirebaseFirestore.getInstance().collection("Reviews").whereEqualTo("book_id", bookFragment.getBook().getId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Review myReview = null;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Review review = document.toObject(Review.class);
                        if (review.getReviewer_email().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                            myReview = review;
                        else if ((review.getReview_title().equals("") && review.getReview_content().equals("")))
                            noContentReviews.add(review);
                        else
                            contentReviews.add(review);
                    }

                    Collections.sort(contentReviews, Collections.reverseOrder());
                    Collections.sort(noContentReviews, Collections.reverseOrder());

                    if (myReview != null)
                        contentReviews.add(0, myReview);
                    contentReviews.addAll(noContentReviews);

                    ReviewListAdapter adapter = new ReviewListAdapter(getActivity(), contentReviews, ReviewsFragment.this);
                    reviewsRV.setAdapter(adapter);
                    Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), true);
                }
            }
        });
    }

    public Book getBook() {
        return bookFragment.getBook();
    }

    public void updateReviewDeletion(int deletedRank, int deletedReviewerAge) {
        bookFragment.updateAfterReviewDeleted(deletedRank, deletedReviewerAge);
        bookFragment.getBookReviews();
    }

}
