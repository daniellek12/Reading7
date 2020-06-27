package com.reading7.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.reading7.MainActivity;
import com.reading7.Objects.Avatar;
import com.reading7.Objects.User;
import com.reading7.ProfileFragment;
import com.reading7.PublicProfileFragment;
import com.reading7.R;
import com.reading7.StatisticsDialog;
import com.reading7.Utils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UsersLeaderboardAdapter extends RecyclerView.Adapter<UsersLeaderboardAdapter.ViewHolder> {

    private Context mContext;
    private StatisticsDialog mDialog;

    private List<User> users;


    public UsersLeaderboardAdapter(Context context, StatisticsDialog dialog, List<User> users) {
        this.mContext = context;
        this.mDialog = dialog;
        this.users = users;
    }

    @NonNull
    @Override
    public UsersLeaderboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_user_item, parent, false);
        return new UsersLeaderboardAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = users.get(position);

        Avatar avatar = user.getAvatar();
        avatar.loadIntoImage(mContext, holder.profileImage);
        holder.userName.setText(user.getFull_name());

        holder.points.setText(String.valueOf(user.getPoints()));
        holder.place.setText(String.valueOf(position + 1));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                    ((MainActivity) mContext).loadFragment(new ProfileFragment());
                else
                    ((MainActivity) mContext).addFragment(new PublicProfileFragment(user.getEmail()));

                mDialog.dismiss();
            }
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView profileImage;
        private TextView userName;
        private TextView points;
        private TextView place;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage_best);
            userName = itemView.findViewById(R.id.userName_best);
            points = itemView.findViewById(R.id.points_best);
            place = itemView.findViewById(R.id.place);
        }
    }

}

