package com.reading7.Adapters;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Objects.Avatar;
import com.reading7.Objects.Notification;
import com.reading7.Objects.User;
import com.reading7.R;
import com.reading7.SimilarUserFragment;
import com.reading7.Utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SimilarUsersAdapter extends RecyclerView.Adapter<SimilarUsersAdapter.MyViewHolder> {

    private Context mContext ;
    private List<String> mData ;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Fragment mFragment;


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView user_name;
        TextView user_age;
        CircleImageView profile_image;
        CardView card;
        ImageButton decline;


        public MyViewHolder(View itemView) {
            super(itemView);

            user_name = itemView.findViewById(R.id.userName);
            user_age = itemView.findViewById(R.id.ageUser);
            profile_image = itemView.findViewById(R.id.profileImage);
            card = itemView.findViewById(R.id.cardview_id);
            decline = itemView.findViewById(R.id.decline);

        }
    }


    public SimilarUsersAdapter(Context mContext, List<String> mData, FirebaseAuth mAuth,Fragment fragment) {
        setHasStableIds(true);
        this.mContext = mContext;
        this.mData = mData;
        this.mAuth = mAuth;
        this.mFragment = fragment;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.similar_user_item,parent,false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        setUpCardSize(holder, position);
       db = FirebaseFirestore.getInstance();

        CollectionReference requestCollectionRef = db.collection("Users");
        Query requestQuery = requestCollectionRef.whereEqualTo("email",mData.get(position));
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                User user = new User();
                for (DocumentSnapshot document : task.getResult()) {
                    user = document.toObject(User.class);

                }

                final String name = user.getFull_name();
                holder.user_name.setText(name);

                String age = "" + Utils.calculateAge(user.getBirth_date());
                holder.user_age.setText("גיל: "+age);
                Avatar avatar = user.getAvatar();
                avatar.loadIntoImage(mContext, holder.profile_image);
                    Utils.OpenProfileOnClick profileListener = new Utils.OpenProfileOnClick(mContext, user.getEmail());
                    holder.profile_image.setOnClickListener(profileListener);
                    holder.user_name.setOnClickListener(profileListener);

                    holder.decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CollectionReference requestsRef = db.collection("SimilarUsers");
                        Query requestQuery = requestsRef.whereEqualTo("similar_user", mData.get(position)).whereEqualTo("user_id",mAuth.getCurrentUser().getEmail());
                        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        document.getReference().delete();

                                        mData.remove(holder.getAdapterPosition());
                                        notifyItemRemoved(holder.getAdapterPosition());
                                        if (mData.isEmpty()) {
                                            ((SimilarUserFragment) mFragment).reload();

                                        }

                                    }
                                }
                            }


                        });

                    }
                });
            }}});
        }




    @Override
    public int getItemCount() {
        return mData.size();
    }


    @Override
    public long getItemId(int position) {
        return mData.get(position).hashCode();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


    private void setUpCardSize(final MyViewHolder holder, int position){

        ViewGroup.LayoutParams params = holder.card.getLayoutParams();

        Display display = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        params.width = (int)(0.8 * size.x);

        int margin = (int)(0.1 * size.x);
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) holder.card.getLayoutParams();

        if(position == 0)
            marginParams.setMargins(margin, marginParams.topMargin, marginParams.rightMargin, marginParams.bottomMargin);

        if(position == mData.size()-1)
            marginParams.setMargins(marginParams.leftMargin, marginParams.topMargin, margin, marginParams.bottomMargin);

    }





}

