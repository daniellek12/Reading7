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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reading7.Adapters.FeedAdapter;
import com.reading7.Adapters.ReviewListAdapter;

import java.util.ArrayList;

import static com.reading7.Utils.calculateAge;


public class BookFragment extends Fragment {

    Activity mActivity;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Book mBook;


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
        Button rankBtn = mActivity.findViewById(R.id.button_read);
        rankBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SignUpActivity.this.onBackPressed();
                RankBookDialog dialog = new RankBookDialog();
                Bundle args = new Bundle();
                args.putString("book_id",mBook.getId() );
                args.putString("book_title",mBook.getTitle());

                dialog.setArguments(args);
                dialog.show(getActivity().getSupportFragmentManager(), "example dialog");

            }
        });
        initReviews();
        getBookInformation();
    }

    public Book getBook() {
        return mBook;
    }

    public void setBook(Book Book) {
        this.mBook = Book;
    }
    private ArrayList<String> getReviews() {
        ArrayList<String> reviews = new ArrayList<>();

        //reviews.add(new Review("רותם סלע", (float)5, "11.06.19", "אחד הטובים שקראתי! מומלץ בטירוף!!"));
        //reviews.add(new Review("משה פרץ", (float)3.5, "11.12.18", "סביר פלוס עם סוף מפתיע"));
        //reviews.add(new Review("רוני דלומי", (float)1, "11.06.18", "וואלה לא משהו, הייתי רוצה שהסופר יפרט יותר בחלק מהקטעים..."));
        //reviews.add(new Review("עומר אדם", (float)4.5, "11.12.17", "אחד הטובים שקראתי! מומלץ בטירוף!!"));

        return reviews;
    }

    private void initReviews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        RecyclerView postsRV = getActivity().findViewById(R.id.reviews);
        postsRV.setLayoutManager(layoutManager);
        //ReviewListAdapter adapter = new ReviewListAdapter(getActivity(), getReviews());
        //postsRV.setAdapter(adapter);

    }

    private void getBookInformation() {

        TextView textViewgeners = (TextView) getActivity().findViewById(R.id.genres);
        String geners = "";
        int first=0;
        for(String s:mBook.getGenres()) {
            if (first == 1)
                geners += ", " + s;
            else
            {
                first = 1;
                geners += s;

            }
        }
        textViewgeners.setText(geners);

       RatingBar rankRatingBar = (RatingBar) getActivity().findViewById(R.id.ratingBar);
        rankRatingBar.setRating(mBook.getAvg_rating());

        TextView textViewAuthor = (TextView) getActivity().findViewById(R.id.author_field);
        textViewAuthor.setText(mBook.getAuthor());

        TextView textViewPublisher = (TextView)getActivity().findViewById(R.id.publisher_field);
        textViewPublisher.setText(mBook.getPublisher());

        TextView textViewPages = (TextView)getActivity().findViewById(R.id.pages_field);
        textViewPages.setText(Integer.toString(mBook.getNum_pages()));

        TextView textViewTitle = (TextView)getActivity().findViewById(R.id.book_name);
        textViewTitle.setText(mBook.getTitle());

        ImageView coverImage = (ImageView) getActivity().findViewById(R.id.coverImage);
        Utils.showImage(mBook.getTitle(),coverImage,getActivity());
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity)context;
    }

}
