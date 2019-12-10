package com.reading7;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reading7.Adapters.FeedAdapter;

import java.sql.Timestamp;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {

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

        posts.add(new Post("1", PostType.Review, new Timestamp(System.currentTimeMillis()), "1", "1",
                "adva@gmail.com", 3, "content", null, "עודד פז",
                "ג׳ינג׳י"));

        posts.add(new Post("1", PostType.WishList, new Timestamp(System.currentTimeMillis()), "1", "הארי פוטר ואבן החכמים",
                "1", "1", "mail","נועה קירל"));

        posts.add(new Post("2", PostType.Review, new Timestamp(System.currentTimeMillis()), "2", "2",
                "mail2", 4, "content", null, "אליאנה תדהר",
                "אשמת הכוכבים"));

//        posts.add(new Post((float)4, 1, "״גינגי״", "טל מוסרי", "לפני 3 שעות"));
//        posts.add(new Post((float)3, 2, "״הארי פוטר ואבן החכמים״", "אליאנה תדהר", "לפני 12 שעות"));
//        posts.add(new Post((float)2.4, 3, "״אשמת הכוכבים״", "עודד מנשה", "לפני 13 שעות"));
//        posts.add(new Post((float)1.5, 4, "״משחקי הרעב״", "אילן רוזנפלד", "לפני יום"));
//        posts.add(new Post((float)4.5, 5, "״גינגי תעלומת הילד המכשף״", "רוני מנדלבאום", "לפני יומיים"));
//        posts.add(new Post((float)5, 6, "״הנסיך הקטן״", "ימית סול", "לפני 6 ימים"));

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


