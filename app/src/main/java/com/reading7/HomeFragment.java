package com.reading7;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reading7.Adapters.FeedAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {

    //Fake class, just for testing
    public class Post {

        public Float rating;
        public Integer cover;
        public String bookName;
        public String userName;
        public String postTime;

        public Post(Float rating, Integer cover, String bookName, String userName, String postTime){

            this.bookName = bookName;
            this.userName = userName;
            this.cover = cover;
            this.postTime = postTime;
            this.rating = rating;
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initPosts();
    }


    private ArrayList<Post> getPosts() {

        ArrayList<Post> posts =new ArrayList<Post>();

        posts.add(new Post((float)4, 1, "״גינגי״", "טל מוסרי", "11.09.19"));
        posts.add(new Post((float)3, 2, "״הארי פוטר ואבן החכמים״", "אליאנה תדהר", "13.07.19"));
        posts.add(new Post((float)2.4, 3, "״אשמת הכוכבים״", "עודד מנשה", "11.07.19"));
        posts.add(new Post((float)1.5, 4, "״משחקי הרעב״", "אילן רוזנפלד", "04.06.19"));
        posts.add(new Post((float)4.5, 5, "״גינגי תעלומת הילד המכשף״", "רוני מנדלבאום", "24.01.19"));
        posts.add(new Post((float)5, 6, "״הנסיך הקטן״", "ימית סול", "03.12.18"));

        return posts;
    }

    private void initPosts() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        RecyclerView postsRV = getActivity().findViewById(R.id.posts);
        postsRV.setLayoutManager(layoutManager);
        FeedAdapter adapter = new FeedAdapter(getActivity(),getPosts());
        postsRV.setAdapter(adapter);

    }
}


