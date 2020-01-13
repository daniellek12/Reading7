package com.reading7;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.ReviewListAdapter;
import com.reading7.Objects.Book;
import com.reading7.Objects.Review;
import com.reading7.Objects.User;
import com.reading7.Objects.WishList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class BookFragment extends Fragment {

    Activity mActivity;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Book mBook;
    private List<Review> lstReviews;
    private ReviewListAdapter adapter;
    private Review mReview;

    private int countRaters;
    private RatingBar rankRatingBar;
    private TextView ratingNum;
    private TextView avgAgeText;
    private float mAvgAge;

    private boolean isWishlist = false;
    private boolean isRanked = false;
    private int mRank;
    private String mReviewTitle;
    private String mReviewContent;
     boolean flag_reviewed;

    public BookFragment(Book book) {
        this.mBook = book;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        flag_reviewed =false;
        mAuth = FirebaseAuth.getInstance();
        countRaters = 0;
        db = FirebaseFirestore.getInstance();
        mReview=null;
        return inflater.inflate(R.layout.book_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lstReviews = new ArrayList<>();
        getBookInformation();
        initReviews();
        initOpenSummary();
        initWishlistButton();
        initRankButton();
        initAlreadyReadButton();
        initBackButton();
        initScrollView();
    }

    public Book getBook() {
        return mBook;
    }

    private void getBookInformation() {

        setBookGenres();

        rankRatingBar = getActivity().findViewById(R.id.bookRatingBar);
        rankRatingBar.setRating(mBook.getAvg_rating());

        ratingNum = getActivity().findViewById(R.id.ratingNum);
        ratingNum.setText(Float.toString(mBook.getAvg_rating()));

       avgAgeText = (TextView) getActivity().findViewById(R.id.ageAvg);
        avgAgeText.setText(AgeString(mBook.getAvg_age()));
        mAvgAge=mBook.getAvg_age();

        TextView textViewAuthor = getActivity().findViewById(R.id.author);
        textViewAuthor.setText(mBook.getAuthor() + ", ");

        TextView textViewPublisher = getActivity().findViewById(R.id.publisher);
        textViewPublisher.setText(getResources().getString(R.string.publisher) + " " + mBook.getPublisher());

        TextView textViewTitle = getActivity().findViewById(R.id.book_name);
        textViewTitle.setText(mBook.getTitle());

        ImageView coverImage = getActivity().findViewById(R.id.bookCoverImage);
        Utils.showImage(mBook.getTitle(), coverImage, mActivity);

        TextView textViewSummary = getActivity().findViewById(R.id.summary);
        textViewSummary.setText(mBook.getDescription());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textViewSummary.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }

        String pages = Integer.toString(mBook.getNum_pages());
        if (pages.equals("-1")) {
            getActivity().findViewById(R.id.numPagesLayout).setVisibility(View.GONE);
        } else {
            TextView textViewPages = getActivity().findViewById(R.id.numPages);
            textViewPages.setText(pages);
        }

        initScrollView();
    }

    private void getBookReviews() {
        countRaters = 0;
        lstReviews.clear();
        final List<Review> newlist = new ArrayList<Review>();
        CollectionReference collection = db.collection("Reviews");
        ((MainActivity) getActivity()).setBottomNavigationEnabled(false);
        Query query = collection.whereEqualTo("book_id", mBook.getId());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        countRaters++;
                        Review review = doc.toObject(Review.class);
                        if (!(review.getReview_title().equals("") && review.getReview_content().equals("")))
                            newlist.add(review);
                    }

                    lstReviews.addAll(newlist);
                    Collections.sort(lstReviews, Collections.reverseOrder());
                    findMyReview();

                }
            }
        });
    }

    private void initReviews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        RecyclerView postsRV = getActivity().findViewById(R.id.reviews);
        postsRV.setLayoutManager(layoutManager);
        adapter = new ReviewListAdapter(getActivity(), lstReviews,this);
        postsRV.setAdapter(adapter);

        getBookReviews();
    }

    public boolean isFlag_reviewed() {
        return flag_reviewed;
    }

    private void initOpenSummary() {

//        final ImageButton openSummaryBtn = mActivity.findViewById(R.id.summary_button);
//        openSummaryBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                TextView mSummary = mActivity.findViewById(R.id.summary);
//                if (mSummary.getEllipsize() == TextUtils.TruncateAt.END) {
//                    mSummary.setMaxLines(Integer.MAX_VALUE);
//                    mSummary.setEllipsize(null);
//                    openSummaryBtn.setImageResource(R.drawable.arrow_drop_up);
//                } else if (mSummary.getEllipsize() == null) {
//                    mSummary.setMaxLines(2);
//                    mSummary.setEllipsize(TextUtils.TruncateAt.END);
//                    openSummaryBtn.setImageResource(R.drawable.arrow_drop_down);
//                }
//            }
//        });

        final TextView mSummary = mActivity.findViewById(R.id.summary);
        mSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSummary.getEllipsize() == TextUtils.TruncateAt.END) {
                    mSummary.setMaxLines(Integer.MAX_VALUE);
                    mSummary.setEllipsize(null);
                } else if (mSummary.getEllipsize() == null) {
                    mSummary.setMaxLines(3);
                    mSummary.setEllipsize(TextUtils.TruncateAt.END);
                }
            }
        });
    }

    private void initBackButton() {
        ImageButton backButton = getActivity().findViewById(R.id.bookBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }


    private void initWishlistButton() {

        // Is the book already in wishlist?
        CollectionReference requestCollectionRef = db.collection("Wishlist");
        Query requestQuery = requestCollectionRef.whereEqualTo("user_email", mAuth.getCurrentUser().getEmail()).whereEqualTo("book_id", mBook.getId());
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !(task.getResult().isEmpty())) {
                    isWishlist = true;
                    updateWishlistButton();
                }
            }
        });

        // Set the wishlist button functionality
        mActivity.findViewById(R.id.button_wishlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User currentUser = ((MainActivity) getActivity()).getCurrentUser();
                if (isWishlist) {
                    CollectionReference requestCollectionRef = db.collection("Wishlist");
                    Query requestQuery = requestCollectionRef.whereEqualTo("user_email", currentUser.getEmail()).whereEqualTo("book_id", mBook.getId());
                    requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    document.getReference().delete();
                                }
                            }
                        }
                    });

                    isWishlist = false;
                    updateWishlistButton();

                } else {
                    WishList wlist = new WishList("", currentUser.getEmail(), currentUser.getFull_name(), mBook.getId(), mBook.getTitle(), Timestamp.now(), currentUser.getAvatar_details());
                    DocumentReference newWish = db.collection("Wishlist").document();
                    wlist.setId(newWish.getId());
                    newWish.set(wlist);

                    isWishlist = true;
                    updateWishlistButton();
                }
            }
        });
    }

    private void updateWishlistButton() {

        final Button wishListBtn = mActivity.findViewById(R.id.button_wishlist);
        Drawable heart = (wishListBtn.getCompoundDrawables())[2];

        if (isWishlist) {
            wishListBtn.setText(getString(R.string.remove_from_wishlist));
            wishListBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
            wishListBtn.setTextColor(getResources().getColor(R.color.darkGrey));
            heart.setColorFilter(getResources().getColor(R.color.darkGrey), PorterDuff.Mode.SRC_ATOP);
        } else {
            wishListBtn.setText(getString(R.string.add_to_wishlist));
            wishListBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            wishListBtn.setTextColor(getResources().getColor(R.color.white));
            heart.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        }

        wishListBtn.setCompoundDrawables(null, null, heart, null);
    }


    private void initRankButton() {

        // Is the book already ranked?
        CollectionReference requestCollectionRef = db.collection("Reviews");
        Query requestQuery = requestCollectionRef.whereEqualTo("reviewer_email", mAuth.getCurrentUser().getEmail()).whereEqualTo("book_id", mBook.getId());
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !(task.getResult().isEmpty())) {
                    isRanked = true;
                    for (DocumentSnapshot doc : task.getResult()) {
                        Review review = doc.toObject(Review.class);
                        mRank = review.getRank();
                        mReviewTitle = review.getReview_title();
                        mReviewContent = review.getReview_content();
                    }
                    updateRankButton();
                }
            }
        });

        // Set the rank button functionality
        Button rankBtn = mActivity.findViewById(R.id.button_read);
        rankBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RankBookDialog dialog = new RankBookDialog();

                Bundle args = new Bundle();
                args.putString("book_id", mBook.getId());
                args.putString("book_title", mBook.getTitle());
                args.putString("book_author", mBook.getAuthor());
                args.putFloat("avg", Float.parseFloat(ratingNum.getText().toString()));
                args.putFloat("avgAge", mAvgAge);
                args.putInt("countRaters", countRaters);

                dialog.setArguments(args);
                dialog.setTargetFragment(BookFragment.this, 202);
                dialog.show(getActivity().getSupportFragmentManager(), "example dialog");
            }
        });
    }

    private void updateRankButton() {

        final Button rankBtn = mActivity.findViewById(R.id.button_read);
        Drawable bubble = (rankBtn.getCompoundDrawables())[2];

        if (isRanked) {
            rankBtn.setVisibility(View.INVISIBLE);
            getActivity().findViewById(R.id.button_wishlist).setVisibility(View.GONE);
            getActivity().findViewById(R.id.button_already_read).setVisibility(View.VISIBLE);
        } else {
//            rankBtn.setText(getString(R.string.add_review));
//            rankBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
//            rankBtn.setTextColor(getResources().getColor(R.color.white));
//            bubble.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        }

        rankBtn.setCompoundDrawables(null, null, bubble, null);
    }


    private void initAlreadyReadButton() {

        Button alreadyReadButton = getActivity().findViewById(R.id.button_already_read);
        alreadyReadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RankBookDialog dialog = new RankBookDialog();

                Bundle args = new Bundle();
                args.putString("book_id", mBook.getId());
                args.putString("book_title", mBook.getTitle());
                args.putString("book_author", mBook.getAuthor());
                args.putFloat("avg", Float.parseFloat(ratingNum.getText().toString()));
                args.putFloat("avgAge", mAvgAge);
                args.putInt("countRaters", countRaters);

                dialog.setArguments(args);
                dialog.setTargetFragment(BookFragment.this, 202);
                dialog.show(getActivity().getSupportFragmentManager(), "example dialog");
            }
        });

    }



    private String AgeString(float avgAge) {
        int ageInt = (int) avgAge;
        String ageString = "" + ageInt + "-" + (ageInt + 1) + "";
        return ageString;
    }

    private void setBookGenres() {

        String geners = "";
        boolean first = true;
        for (String genre : mBook.getGenres()) {
            if (!first)
                geners += " | " + genre;
            else {
                geners += genre;
                first = false;
            }
        }

        TextView genres = getActivity().findViewById(R.id.genres);
        genres.setText(geners);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            genres.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure fragment codes match up
        if (requestCode == 202) {
            float avg = data.getFloatExtra(
                    "avg", 0);
            mAvgAge = data.getFloatExtra(
                    "avgAge", 0);


            rankRatingBar.setRating(avg);
            ratingNum.setText(Float.toString(avg));
            avgAgeText.setText(AgeString(mAvgAge));
            lstReviews.clear();
            getBookReviews();//overhead

            isRanked = true;
            updateRankButton();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }


    /**
     * Scrolls the nestedScrollView to the wanted initial position
     */
    private void initScrollView() {

        final View view = getActivity().findViewById(R.id.summary);
        final NestedScrollView scrollView = getActivity().findViewById(R.id.nestedScrollView);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ScrollPositionObserver());

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_UP);
            }
        });

    }

    private class ScrollPositionObserver implements ViewTreeObserver.OnScrollChangedListener {

        private int coverHeight;
        private ImageView cover;
        private int toolbarHeight;
        private androidx.appcompat.widget.Toolbar toolbar;
        private NestedScrollView scrollView;

        public ScrollPositionObserver() {
            scrollView = getActivity().findViewById(R.id.nestedScrollView);
            cover = getActivity().findViewById(R.id.bookCoverImage);
            coverHeight = getResources().getDimensionPixelSize(R.dimen.book_fragment_cover_height);
            toolbar = getActivity().findViewById(R.id.toolBar);
            toolbarHeight = getResources().getDimensionPixelSize(R.dimen.toolbar_height);
        }

        @Override
        public void onScrollChanged() {
            int scrollY = Math.min(Math.max(scrollView.getScrollY(), 0), coverHeight);

            // changing position of ImageView
            cover.setTranslationY(1 - scrollY / 2);

            // changing alpha of toolbar
            float alpha = 1 - (scrollY / (float) toolbarHeight);
            toolbar.setAlpha(alpha);
        }
    }

    private void findMyReview() {

        //should i for the array to find my review or should i do firebase query
/*
        mReview = null;
        CollectionReference requestCollectionRef = db.collection("Reviews");
        Query requestQuery = requestCollectionRef.whereEqualTo("reviewer_email", mAuth.getCurrentUser().getEmail()).whereEqualTo("book_title", mBook.getTitle());
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mReview = document.toObject(Review.class);
                    }
                    if (mReview != null) {
                        flag_reviewed=true;
                        lstReviews.remove(mReview);
                        lstReviews.add(0,mReview);

                    }*/
                    final List<Review> newlist = new ArrayList<Review>();
                    newlist.addAll(lstReviews);
                    for(Review r:newlist){
                        if(r.getReviewer_email().equals(mAuth.getCurrentUser().getEmail())) {
                            flag_reviewed=true;
                            lstReviews.remove(r);
                            lstReviews.add(0,r);
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();

                    TextView countRatersText = getActivity().findViewById(R.id.reviwersNum);
                    if (countRaters == 1)
                        countRatersText.setText(getResources().getString(R.string.one_reviewer));
                    else
                        countRatersText.setText(countRaters + " " + getResources().getString(R.string.reviewers));

                    ((MainActivity) getActivity()).setBottomNavigationEnabled(true);

    }

}
