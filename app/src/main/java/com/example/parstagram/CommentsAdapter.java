package com.example.parstagram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    Context context;
    List<Comment> comments;

    public CommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivProfilePic;
        TextView tvComment;
        TextView tvUsername;
        TextView tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
        }

        public void bind(final Comment comment) {
            ParseUser user = comment.getUser();

            tvUsername.setText(user.getUsername());
            tvComment.setText(comment.getComment());
            tvTime.setText(Comment.getRelativeTimeAgo(comment.getTimestamp().toString()));
            ParseFile profilePicture = user.getParseFile("profilePic");
            if(profilePicture != null){
                Glide.with(context).load(profilePicture.getUrl()).placeholder(R.drawable.profilepic).fitCenter().circleCrop().into(ivProfilePic);
            }else{
                Glide.with(context).load(R.drawable.profilepic).fitCenter().circleCrop().into(ivProfilePic);
            }
        }

    }

}
