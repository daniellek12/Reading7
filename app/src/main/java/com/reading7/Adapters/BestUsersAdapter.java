package com.reading7.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reading7.BookFragment;
import com.reading7.MainActivity;
import com.reading7.Objects.Avatar;
import com.reading7.Objects.Book;
import com.reading7.Objects.Comment;
import com.reading7.Objects.User;
import com.reading7.R;
import com.reading7.ReviewCommentsFragment;
import com.reading7.Utils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.reading7.Utils.RelativeDateDisplay;

public class BestUsersAdapter extends RecyclerView.Adapter<BestUsersAdapter.ViewHolder> {

    Context mContext;
    Fragment mFragment;
    List<User> users;


    public BestUsersAdapter(Context context, Fragment fragment, List<User> users) {
        this.mContext = context;
        this.mFragment = fragment;
        this.users = users;
    }

    @NonNull
    @Override
    public BestUsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.best_user_item, parent, false);
        return new BestUsersAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int pos = position;

        holder.userName.setText(users.get(pos).getFull_name());
        holder.points.setText(users.get(pos).getPoints()+" נקודות");

        Avatar avatar = users.get(pos).getAvatar();
        avatar.loadIntoImage(mContext, holder.profileImage);

        Utils.OpenProfileOnClick profileListener = new Utils.OpenProfileOnClick(mContext, users.get(pos).getEmail());
        holder.profileImage.setOnClickListener(profileListener);
        holder.userName.setOnClickListener(profileListener);

        }




    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;
        TextView userName;
        TextView points;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //convertView = LayoutInflater.from(mContext).inflate(R.layout.search_item, parent, false);
            profileImage = itemView.findViewById(R.id.profileImage_best);
            userName = itemView.findViewById(R.id.userName_best);
            points = itemView.findViewById(R.id.points_best);
        }
    }

}

