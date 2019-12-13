package com.reading7;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.FeedAdapter;
import com.reading7.Adapters.ReviewListAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.reading7.Utils.calculateAge;


public class BookFragment extends Fragment {

    Activity mActivity;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Book mBook;
    private List<Review> lstReviews;
    private ReviewListAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.book_fragment, container, false);
    }

    @Override
    public void
    onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton rankBtn = mActivity.findViewById(R.id.button_read);
        rankBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SignUpActivity.this.onBackPressed();
                RankBookDialog dialog = new RankBookDialog();
                Bundle args = new Bundle();
                args.putString("book_id", mBook.getId());
                args.putString("book_title", mBook.getTitle());
                args.putFloat("avg", mBook.getAvg_rating());
                args.putInt("countRaters", lstReviews.size());

                dialog.setArguments(args);
                dialog.show(getActivity().getSupportFragmentManager(), "example dialog");

            }
        });

        ImageButton wishListBtn = mActivity.findViewById(R.id.button_wishlist);
        wishListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CollectionReference requestCollectionRef = db.collection("Users");
                Query requestQuery = requestCollectionRef.whereEqualTo("email", mAuth.getCurrentUser().getEmail());
                requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = new User();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                user = document.toObject(User.class);
                            }


                            WishList wlist = new WishList("", user.getEmail(), user.getFull_name(), mBook.getId(), mBook.getTitle(), Timestamp.now());
                            DocumentReference newWish = db.collection("Wishlist").document();
                            wlist.setId(newWish.getId());
                            newWish.set(wlist).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                         @Override
                                                                         public void onComplete(@NonNull Task<Void> task) {
                                                                             if (task.isSuccessful()) {
                                                                                 //getBookReviews();
                                                                             }

                                                                         }
                                                                     }
                            );

                        }
                    }
                });
                Toast.makeText(getActivity(), "הספר נוסף לרשימת המשאלות שלי", Toast.LENGTH_SHORT).show();
            }
        });

        lstReviews = new ArrayList<>();
        initOpenSummary();
        initReviews();
        getBookInformation();
        getBookReviews();
    }

    public Book getBook() {
        return mBook;
    }

    public void setBook(Book Book) {
        this.mBook = Book;
    }

    private void getBookReviews() {
        final List<Review> newlist = new ArrayList<Review>();
        CollectionReference collection = db.collection("Reviews");
        Query query = collection.whereEqualTo("book_id", mBook.getId());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        newlist.add(doc.toObject(Review.class));
                    }

                    lstReviews.addAll(newlist);
                    adapter.notifyDataSetChanged();


                }
            }
        });
    }

    private void initOpenSummary() {

        final ImageButton openSummaryBtn = mActivity.findViewById(R.id.summary_button);
        openSummaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView mSummary = mActivity.findViewById(R.id.summary);
                if (mSummary.getEllipsize() == TextUtils.TruncateAt.END) {
                    mSummary.setMaxLines(Integer.MAX_VALUE);
                    mSummary.setEllipsize(null);
                    openSummaryBtn.setImageResource(R.drawable.arrow_drop_up);
                } else if (mSummary.getEllipsize() == null) {
                    mSummary.setMaxLines(2);
                    mSummary.setEllipsize(TextUtils.TruncateAt.END);
                    openSummaryBtn.setImageResource(R.drawable.arrow_drop_down);
                }
            }
        });

        final TextView mSummary = mActivity.findViewById(R.id.summary);
        mSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView mSummary = mActivity.findViewById(R.id.summary);
                if (mSummary.getEllipsize() == TextUtils.TruncateAt.END) {
                    mSummary.setMaxLines(Integer.MAX_VALUE);
                    mSummary.setEllipsize(null);
                    openSummaryBtn.setImageResource(R.drawable.arrow_drop_up);
                } else if (mSummary.getEllipsize() == null) {
                    mSummary.setMaxLines(2);
                    mSummary.setEllipsize(TextUtils.TruncateAt.END);
                    openSummaryBtn.setImageResource(R.drawable.arrow_drop_down);
                }
            }
        });
    }

    private void initReviews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        RecyclerView postsRV = getActivity().findViewById(R.id.reviews);
        postsRV.setLayoutManager(layoutManager);
        adapter = new ReviewListAdapter(getActivity(), lstReviews);
        postsRV.setAdapter(adapter);

    }

    private void getBookInformation() {

        TextView textViewgeners = (TextView) getActivity().findViewById(R.id.genres);
        String geners = "";
        int first = 0;
        for (String s : mBook.getGenres()) {
            if (first == 1)
                geners += ", " + s;
            else {
                first = 1;
                geners += s;

            }
        }
        textViewgeners.setText(geners);

        RatingBar rankRatingBar = (RatingBar) getActivity().findViewById(R.id.ratingBar);
        rankRatingBar.setRating(mBook.getAvg_rating());

        TextView ratingNum = (TextView) getActivity().findViewById(R.id.ratingNum);
        ratingNum.setText(Float.toString(mBook.getAvg_rating()));

        TextView textViewAuthor = (TextView) getActivity().findViewById(R.id.author_field);
        textViewAuthor.setText(mBook.getAuthor());

        TextView textViewPublisher = (TextView) getActivity().findViewById(R.id.publisher_field);
        textViewPublisher.setText(mBook.getPublisher());

        TextView textViewPages = (TextView) getActivity().findViewById(R.id.pages_field);
        textViewPages.setText(Integer.toString(mBook.getNum_pages()));

        TextView textViewTitle = (TextView) getActivity().findViewById(R.id.book_name);
        textViewTitle.setText(mBook.getTitle());

        ImageView coverImage = (ImageView) getActivity().findViewById(R.id.coverImage);
        Utils.showImage(mBook.getTitle(), coverImage, getActivity());

        TextView textViewSummary = (TextView) getActivity().findViewById(R.id.summary);
        textViewSummary.setText(mBook.getDescription());
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

}
