package com.reading7.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.BookFragment;
import com.reading7.MainActivity;
import com.reading7.Objects.Book;
import com.reading7.Objects.WishList;
import com.reading7.R;
import com.reading7.ShelfFragment;
import com.reading7.Utils;

import java.util.ArrayList;

public class CustomShelvesAdapter extends RecyclerView.Adapter<CustomShelvesAdapter.ViewHolder> {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ArrayList<String> shelfNames;
    private Context mContext;
    String user_email;
    private Activity mActivity;
    private ViewGroup viewGroup;

    public CustomShelvesAdapter(ArrayList<String> shelfNames, Context context, String user_email,ViewGroup viewGroup,Activity activity){
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.shelfNames = shelfNames;
        this.mContext = context;
        this.user_email = user_email;
        this.viewGroup = viewGroup;
        this.mActivity=activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView shelf_title;
        RelativeLayout shelf_title_layout;
        RecyclerView shelfBooksRV;
        ArrayList<String> shelfBookNames = new ArrayList<String>();
        RelativeLayout empty_shelf_layout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            shelf_title = itemView.findViewById(R.id.shelf_title);
            shelf_title_layout = itemView.findViewById(R.id.shelfTitle_layout);
            empty_shelf_layout = itemView.findViewById(R.id.emptyList);

            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            shelfBooksRV = itemView.findViewById(R.id.customListRV);
            shelfBooksRV.setLayoutManager(layoutManager);
        }
    }

    @NonNull
    @Override
    public CustomShelvesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_shelf_item, viewGroup, false);
        return new CustomShelvesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomShelvesAdapter.ViewHolder viewHolder, final int i) {

        final String shelfName = shelfNames.get(i);
        viewHolder.shelf_title.setText(shelfName);

        final ShelfFragment customShelf = new ShelfFragment(viewHolder.shelfBookNames, shelfName,
                user_email, ShelfFragment.ShelfType.CUSTOM);
        final ProfileShelfAdapter shelfAdapter = new ProfileShelfAdapter(mContext, viewHolder.shelfBookNames, customShelf,viewGroup,mActivity);
        viewHolder.shelfBooksRV.setAdapter(shelfAdapter);

        CollectionReference collection = db.collection("Users")
                .document(user_email).collection("Shelves");
        Query query = collection.whereEqualTo("shelf_name", shelfName);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    viewHolder.shelfBookNames.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ArrayList<String> shelf_books = (ArrayList<String>)(doc.getData().get("book_names"));
                        viewHolder.shelfBookNames.addAll(shelf_books);
                    }
                    shelfAdapter.notifyDataSetChanged();

                    if (viewHolder.shelfBookNames.isEmpty()) {
                        viewHolder.shelfBooksRV.setVisibility(View.INVISIBLE);
                        viewHolder.empty_shelf_layout.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.shelfBooksRV.setVisibility(View.VISIBLE);
                        viewHolder.empty_shelf_layout.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        viewHolder.shelf_title_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) mContext).addFragment(new ShelfFragment(viewHolder.shelfBookNames, shelfName, user_email, ShelfFragment.ShelfType.WISHLIST));
            }
        });
    }

    @Override
    public int getItemCount() {
        return shelfNames.size();
    }
}
