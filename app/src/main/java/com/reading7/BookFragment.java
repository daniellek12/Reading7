package com.reading7;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.ReviewListAdapter;
import com.reading7.Dialogs.AddToShelfDialog;
import com.reading7.Dialogs.ChallengeUserDialog;
import com.reading7.Dialogs.DeleteBookDialog;
import com.reading7.Dialogs.InviteUserDialog;
import com.reading7.Dialogs.RankBookDialog;
import com.reading7.Objects.Book;
import com.reading7.Objects.Review;
import com.reading7.Objects.User;
import com.reading7.Objects.WishList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class BookFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Book mBook;

    private List<Review> lstReviews;
    private List<Review> lstAllReviews;
    private RecyclerView reviewsRV;

    private int countRaters;
    private RatingBar rankRatingBar;
    private TextView ratingNum;
    private float mAvgAge;

    private boolean isWishlist = false;
    private boolean isReviewed = false;
    private boolean isReviewedWithContent = false;

    public boolean admin_delete = false;
    public boolean edit_mode = false;

    public boolean isReviewedWithContent() {
        return isReviewedWithContent;
    }

    public void setReviewedWithContent(boolean reviewedWithContent) {
        isReviewedWithContent = reviewedWithContent;
    }


    private Review mReview;
    private int mRank;
    private String mReviewTitle;
    private String mReviewContent;

    public BookFragment(Book book) {
        this.mBook = book;
    }

    public Book getBook() {
        return mBook;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        countRaters = 0;
        mReview = null;
        return inflater.inflate(R.layout.book_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lstReviews = new ArrayList<>();
        lstAllReviews = new ArrayList<>();
        getBookInformation();
        initReviews();
        initOpenSummary();
        initBackButton();
        initScrollView();

        if (Utils.isAdmin){
            getActivity().findViewById(R.id.addToWishlist).setVisibility(View.GONE);
            getActivity().findViewById(R.id.button_read).setVisibility(View.INVISIBLE);
            getActivity().findViewById(R.id.button_already_read).setVisibility(View.GONE);
            getActivity().findViewById(R.id.top_buttons_layout).setVisibility(View.GONE);
            getActivity().findViewById(R.id.share_layout).setVisibility(View.INVISIBLE);
            getActivity().findViewById(R.id.button_delete_book).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.editButton).setVisibility(View.VISIBLE);
            initDeleteButton();
            initEditButton();
            initSaveButton();
        }
        else {
            initWishlistButton();
            initRankButton();
            initAlreadyReadButton();
            initShelfButton();
            initInviteButton();
            initChallengeButton();
        }

    }

    private void initEditButton() {
        getActivity().findViewById(R.id.editButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditMode(true);
                initEditFields();
            }
        });
    }

    public void setEditMode(boolean active) {
        if (active){
            edit_mode = true;
            getActivity().findViewById(R.id.editButton).setVisibility(View.GONE);
            getActivity().findViewById(R.id.saveButton).setVisibility(View.VISIBLE);
            //getActivity().findViewById(R.id.book_name).setVisibility(View.INVISIBLE);
            //getActivity().findViewById(R.id.book_name_edit).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.author).setVisibility(View.INVISIBLE);
            getActivity().findViewById(R.id.author_edit).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.publisher).setVisibility(View.INVISIBLE);
            getActivity().findViewById(R.id.publisher_edit).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.genres).setVisibility(View.INVISIBLE);
            getActivity().findViewById(R.id.genres_edit).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.bookRatingBar).setVisibility(View.INVISIBLE);
            getActivity().findViewById(R.id.ratingNum).setVisibility(View.INVISIBLE);
            getActivity().findViewById(R.id.summary_title).setVisibility(View.INVISIBLE);
            getActivity().findViewById(R.id.summary).setVisibility(View.INVISIBLE);
            getActivity().findViewById(R.id.summary_edit).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.numPages).setVisibility(View.GONE);
            getActivity().findViewById(R.id.numPages_edit).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.button_delete_book).setVisibility(View.GONE);
        }
        else {
            edit_mode = false;
            getActivity().findViewById(R.id.editButton).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.saveButton).setVisibility(View.GONE);
            //getActivity().findViewById(R.id.book_name).setVisibility(View.VISIBLE);
            //getActivity().findViewById(R.id.book_name_edit).setVisibility(View.GONE);
            getActivity().findViewById(R.id.author).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.author_edit).setVisibility(View.GONE);
            getActivity().findViewById(R.id.publisher).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.publisher_edit).setVisibility(View.GONE);
            getActivity().findViewById(R.id.genres).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.genres_edit).setVisibility(View.GONE);
            getActivity().findViewById(R.id.bookRatingBar).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.ratingNum).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.summary_title).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.summary).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.summary_edit).setVisibility(View.GONE);
            getActivity().findViewById(R.id.numPages).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.numPages_edit).setVisibility(View.GONE);
            getActivity().findViewById(R.id.button_delete_book).setVisibility(View.VISIBLE);
        }

    }

    private void initSaveButton() {
        getActivity().findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditMode(false);
                updateBook();
            }
        });
    }

    public void updateBook() {
//        EditText title_edit = getActivity().findViewById(R.id.book_name_edit);
//        mBook.setTitle(title_edit.getText().toString());

        EditText author_edit = getActivity().findViewById(R.id.author_edit);
        mBook.setAuthor(author_edit.getText().toString());

        EditText publisher_edit = getActivity().findViewById(R.id.publisher_edit);
        mBook.setPublisher(publisher_edit.getText().toString());

        EditText num_pages_edit = getActivity().findViewById(R.id.numPages_edit);
        mBook.setNum_pages(Integer.parseInt(num_pages_edit.getText().toString()));

        EditText summary_edit = getActivity().findViewById(R.id.summary_edit);
        mBook.setDescription(summary_edit.getText().toString());

        EditText genres_edit = getActivity().findViewById(R.id.genres_edit);
        String genres_new = genres_edit.getText().toString();
        String[] elements = genres_new.split("\\|");
        String[] trimmed = new String[elements.length];
        for (int i = 0; i < elements.length; i++)
            trimmed[i] = elements[i].trim();
        List<String> genres_list = Arrays.asList(trimmed);
        ArrayList<String> genres_arraylist = new ArrayList<String>(genres_list);
        mBook.setGenres(genres_arraylist);

        DocumentReference bookRef = FirebaseFirestore.getInstance().collection("Books").document(mBook.getId());
        bookRef.update("title", mBook.getTitle(), "author", mBook.getAuthor(),
                "publisher", mBook.getPublisher(), "num_pages", mBook.getNum_pages(), "description",
                mBook.getDescription(), "genres", genres_arraylist);

        getBookInformation();
    }

    private void initDeleteButton() {
        getActivity().findViewById(R.id.button_delete_book).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteBookDialog dialog = new DeleteBookDialog();

                Bundle args = new Bundle();
                args.putString("book_id", mBook.getId());
                args.putString("book_title", mBook.getTitle());
                args.putString("context", "book fragment");

                dialog.setArguments(args);
                dialog.setTargetFragment(BookFragment.this, 404);
                dialog.show(getActivity().getSupportFragmentManager(), "example dialog");
//                Utils.deleteBookFromDB(mBook.getId(), mBook.getTitle());
//                admin_delete = true;
//                getActivity().onBackPressed();
            }
        });
    }

    public void sendResult(int result_code) {
        Intent intent = new Intent();
        intent.putExtra("book_id", mBook.getId());

        if (getTargetFragment() != null)
            getTargetFragment().onActivityResult(getTargetRequestCode(), result_code, intent);
    }

    private void getBookInformation() {

        setBookGenres();

        rankRatingBar = getActivity().findViewById(R.id.bookRatingBar);
        rankRatingBar.setRating(mBook.getAvg_rating());

        ratingNum = getActivity().findViewById(R.id.ratingNum);
        ratingNum.setText(Float.toString(mBook.getAvg_rating()));

        mAvgAge = mBook.getAvg_age();

        TextView textViewAuthor = getActivity().findViewById(R.id.author);
        textViewAuthor.setText(mBook.getAuthor() + ", ");

        TextView textViewPublisher = getActivity().findViewById(R.id.publisher);
        textViewPublisher.setText(getResources().getString(R.string.publisher) + " " + mBook.getPublisher());

        TextView textViewTitle = getActivity().findViewById(R.id.book_name);
        textViewTitle.setText(mBook.getTitle());

        ImageView coverImage = getActivity().findViewById(R.id.bookCoverImage);
        Utils.showImage(mBook.getTitle(), coverImage, getActivity());

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

        if(Utils.isAdmin)
            initEditFields();
    }

    private void initEditFields() {
//        EditText book_name_edit = getActivity().findViewById(R.id.book_name_edit);
//        book_name_edit.setText(mBook.getTitle());

        EditText author_edit = getActivity().findViewById(R.id.author_edit);
        author_edit.setText(mBook.getAuthor());

        EditText publisher_edit = getActivity().findViewById(R.id.publisher_edit);
        publisher_edit.setText(mBook.getPublisher());

        TextView genres = getActivity().findViewById(R.id.genres);
        EditText genres_edit = getActivity().findViewById(R.id.genres_edit);
        genres_edit.setText(genres.getText());

        EditText summary_edit = getActivity().findViewById(R.id.summary_edit);
        summary_edit.setText(mBook.getDescription());

        EditText pages_edit = getActivity().findViewById(R.id.numPages_edit);
        pages_edit.setText(Integer.toString(mBook.getNum_pages()));
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


    private void initReviews() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        reviewsRV = getActivity().findViewById(R.id.reviews);
        reviewsRV.setLayoutManager(layoutManager);
        getBookReviews();
    }

    private void getBookReviews() {

        Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), false);
        countRaters = 0;
        lstReviews.clear();
        lstAllReviews.clear();

        final List<Review> newlist = new ArrayList<Review>();
        Query query = db.collection("Reviews").whereEqualTo("book_id", mBook.getId());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        countRaters++;
                        Review review = doc.toObject(Review.class);
                        lstAllReviews.add(review);
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

    private void findMyReview() {

        final List<Review> tempList = new ArrayList<Review>();
        tempList.addAll(lstAllReviews);
        for (Review review : tempList) {
            if (review.getReviewer_email().equals(mAuth.getCurrentUser().getEmail())) {
                isReviewed = true;
                mRank = review.getRank();
                mReviewTitle = review.getReview_title();
                mReviewContent = review.getReview_content();
                updateRankButton();

                if ((!review.getReview_title().isEmpty()) || (!review.getReview_content().isEmpty())){
                    lstReviews.remove(review);
                    lstReviews.add(0, review);
                    isReviewedWithContent = true;
                }
                break;
            }
        }

        TextView countRatersText = getActivity().findViewById(R.id.reviwersNum);
        if (countRaters == 1)
            countRatersText.setText(getResources().getString(R.string.one_reviewer));
        else
            countRatersText.setText(countRaters + " " + getResources().getString(R.string.reviewers));

        ReviewListAdapter adapter = new ReviewListAdapter(getActivity(), lstReviews, this);
        reviewsRV.setAdapter(adapter);
        Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), true);

    }


    public boolean isReviewed() {
        return isReviewed;
    }

    public void setReviewed(boolean reviewed) {
        this.isReviewed = reviewed;
    }


    private void initOpenSummary() {

        final TextView mSummary = getActivity().findViewById(R.id.summary);
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
        getActivity().findViewById(R.id.bookBackButton).setOnClickListener(new View.OnClickListener() {
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
        getActivity().findViewById(R.id.addToWishlist).setOnClickListener(new View.OnClickListener() {
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
                    WishList wlist = new WishList("", currentUser.getEmail(), mBook.getId(), mBook.getTitle(), mBook.getAuthor(), Timestamp.now());
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

        final Button wishListBtn = getActivity().findViewById(R.id.addToWishlist);
        Drawable heart = Utils.getDrawable(getContext(), "heart");

        if (isWishlist) {
            wishListBtn.setText(getString(R.string.remove_from_wishlist));
            wishListBtn.setTextColor(getResources().getColor(R.color.lightGrey));
            wishListBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
            heart.setColorFilter(getResources().getColor(R.color.lightGrey), PorterDuff.Mode.SRC_ATOP);
            wishListBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, heart, null);
        } else {
            wishListBtn.setText(getString(R.string.add_to_wishlist));
            wishListBtn.setTextColor(getResources().getColor(R.color.white));
            wishListBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            heart.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            wishListBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, heart, null);
        }

        ObjectAnimator.ofPropertyValuesHolder(wishListBtn,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 1.1f, 1),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 1.1f, 1))
                .setDuration(200)
                .start();
    }


    private void initRankButton() {

        updateRankButton();

        // Set the rank button functionality
        Button rankBtn = getActivity().findViewById(R.id.button_read);
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

        final Button rankBtn = getActivity().findViewById(R.id.button_read);

        if (Utils.isAdmin) {
            return;
        }

        if (isReviewed) {
            rankBtn.setVisibility(View.INVISIBLE);
            getActivity().findViewById(R.id.button_already_read).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.addToWishlist).setVisibility(View.GONE);
        } else {
            rankBtn.setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.button_already_read).setVisibility(View.GONE);
            getView().findViewById(R.id.addToWishlist).setVisibility(View.VISIBLE);
        }
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


    private String ageString(float avgAge) {
        int ageInt = (int) avgAge;
        String ageString = "" + ageInt + "-" + (ageInt + 1) + "";
        return ageString;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode) {

            case 202:
                float avg = data.getFloatExtra("avg", 0);
                mAvgAge = data.getFloatExtra("avgAge", 0);

                rankRatingBar.setRating(avg);
                ratingNum.setText(Float.toString(avg));
                lstReviews.clear();
                getBookReviews(); //overhead

                isReviewed = true;
                updateRankButton();
                break;

            case 303:
                String review_id = data.getStringExtra("review_id");
                ((ReviewListAdapter) reviewsRV.getAdapter()).notifyReviewCommentsChanged(review_id);
                break;

            case 404:
                int rank = data.getIntExtra("rank", 0);
                int reviewer_age = data.getIntExtra("reviewer_age", 0);
                updateAfterReviewDeleted(rank, reviewer_age);
                getBookReviews();
                //getActivity().onBackPressed();

        }
    }

    private void updateAfterReviewDeleted(int rank, int reviewer_age) {

        final float newAvg, newAvgAge;
        if (mBook.getRaters_count() == 1) {
            newAvg = 0;
            newAvgAge = 0;
        } else {
            newAvg = ((mBook.getAvg_rating() * mBook.getRaters_count()) - rank) / (mBook.getRaters_count() - 1);
            newAvgAge = ((mBook.getAvg_age() * mBook.getRaters_count()) - reviewer_age) / (mBook.getRaters_count() - 1);
        }
        final int countRaters = mBook.getRaters_count() - 1;
        db.collection("Books").document(mBook.getId()).update("avg_rating", newAvg, "avg_age", newAvgAge, "raters_count", countRaters);
        updateUIAfterDeleteReview(newAvg, newAvgAge, countRaters);
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


    private void initShelfButton() {
        Button shelfButton = getActivity().findViewById(R.id.addToShelf);
        shelfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddToShelfDialog dialog = new AddToShelfDialog();

                Bundle args = new Bundle();
                args.putString("book_title", mBook.getTitle());

                dialog.setArguments(args);
                dialog.setTargetFragment(BookFragment.this, 404);
                dialog.show(getActivity().getSupportFragmentManager(), "example dialog");
            }
        });
    }

    private void initInviteButton() {
        Button inviteButton = getActivity().findViewById(R.id.share_button);
        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InviteUserDialog dialog = new InviteUserDialog();

                Bundle args = new Bundle();
                args.putString("book_title", mBook.getTitle());

                dialog.setArguments(args);
                dialog.setTargetFragment(BookFragment.this, 505);
                dialog.show(getActivity().getSupportFragmentManager(), "example dialog");
            }
        });
    }

    private void initChallengeButton() {
        ImageButton challengeButton = getActivity().findViewById(R.id.challenge_button);
        challengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChallengeUserDialog dialog = new ChallengeUserDialog();

                Bundle args = new Bundle();
                args.putString("book_title", mBook.getTitle());

                dialog.setArguments(args);
                dialog.setTargetFragment(BookFragment.this, 606);
                dialog.show(getActivity().getSupportFragmentManager(), "example dialog");
            }
        });
    }

    public void updateUIAfterDeleteReview(float rank_avg, float age_avg, int count_raters) {

        rankRatingBar.setRating(rank_avg);
        ratingNum.setText(Float.toString(rank_avg));
        mAvgAge = age_avg;
        this.countRaters = count_raters;
        TextView countRatersText = getActivity().findViewById(R.id.reviwersNum);
        if (count_raters == 1)
            countRatersText.setText(getResources().getString(R.string.one_reviewer));
        else
            countRatersText.setText(count_raters + " " + getResources().getString(R.string.reviewers));

        updateRankButton();
    }


}
