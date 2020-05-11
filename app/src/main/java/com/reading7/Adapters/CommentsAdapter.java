package com.reading7.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reading7.MainActivity;
import com.reading7.Objects.Avatar;
import com.reading7.Objects.Comment;
import com.reading7.Objects.User;
import com.reading7.R;
import com.reading7.ReviewCommentsFragment;
import com.reading7.Utils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.reading7.Utils.RelativeDateDisplay;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    Context mContext;
    Fragment mFragment;
    List<Comment> comments;


    public CommentsAdapter(Context context, Fragment fragment, List<Comment> comments) {
        this.mContext = context;
        this.mFragment = fragment;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Comment comment = comments.get(position);

        bindCommentUser(holder, comment.getCommenter_email());
        holder.comment.setText(comment.getComment_content());

        String time = RelativeDateDisplay(Timestamp.now().toDate().getTime() -
                comment.getComment_time().toDate().getTime());
        holder.postTime.setText(time);

        setupDeleteComment(holder, position);
    }


    private void bindCommentUser(final ViewHolder holder, final String email) {

        DocumentReference userReference = FirebaseFirestore.getInstance().collection("Users").document(email);
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User reviewer = task.getResult().toObject(User.class);

                holder.userName.setText(reviewer.getFull_name());

                Avatar avatar = reviewer.getAvatar();
                avatar.loadIntoImage(mContext, holder.profileImage);

                Utils.OpenProfileOnClick profileListener = new Utils.OpenProfileOnClick(mContext, email);
                holder.profileImage.setOnClickListener(profileListener);
                holder.userName.setOnClickListener(profileListener);
            }
        });
    }


    private void setupDeleteComment(final ViewHolder holder, final int position) {

        final Comment comment = comments.get(position);
        User user = ((MainActivity) mContext).getCurrentUser();

        if (Utils.isAdmin || comment.getCommenter_email().equals(user.getEmail())) {
            holder.more.setVisibility(View.VISIBLE);
            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.deleteComment.getVisibility() == View.VISIBLE)
                        holder.deleteComment.setVisibility(View.GONE);
                    else holder.deleteComment.setVisibility(View.VISIBLE);
                }
            });

            holder.deleteComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    comments.remove(position);
                    notifyItemRemoved(position);
                    deleteComment(comment);
                }
            });
        }

    }

    private void deleteComment(Comment comment) {
        FirebaseFirestore.getInstance()
                .collection("Reviews")
                .document(comment.getReview_id())
                .update("comments", FieldValue.arrayRemove(comment));

        ((ReviewCommentsFragment) mFragment).updateCommentsNum();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;
        TextView userName;
        TextView postTime;
        TextView comment;
        ImageButton more;
        LinearLayout deleteComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            userName = itemView.findViewById(R.id.userName);
            postTime = itemView.findViewById(R.id.postTime);
            comment = itemView.findViewById(R.id.comment);
            more = itemView.findViewById(R.id.more);
            deleteComment = itemView.findViewById(R.id.deleteComment);
        }
    }


    public void notifyAddComment() {
        notifyItemInserted(comments.size() - 1);
    }

}
