package com.example.parstagram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.R;
import com.example.parstagram.models.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public interface OnClickListener {
        void onItemClickedPostDetails(int position);

        void onItemClickedUserDetails(String userId);
    }

    OnClickListener onClickListener;
    Context context;
    List<Post> posts;
    ArrayList<String> likes = new ArrayList<String>();

    public PostsAdapter(Context context, List<Post> posts, OnClickListener onClickListener) {
        this.context = context;
        this.posts = posts;
        this.onClickListener = onClickListener;

        ArrayList temp = (ArrayList) ParseUser.getCurrentUser().get("likedPosts");
        if (temp != null) {
            likes.addAll(temp);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ParseUser user = ParseUser.getCurrentUser();

        LinearLayout llPosts;
        TextView tvUsername;
        TextView tvDescription;
        TextView tvTimestamp;
        ImageView ivImage;
        ImageView ivProfilePic;
        RelativeLayout rlHeader;
        TextView tvLikes;
        ImageView ivLike;
        ImageView ivComment;
        ImageView ivDirect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivComment = itemView.findViewById(R.id.ivComment);
            ivDirect = itemView.findViewById(R.id.ivDirect);

            ivDirect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

            llPosts = itemView.findViewById(R.id.llPost);
            rlHeader = itemView.findViewById(R.id.rlHeader);
        }

        private void likeAPost(Post post){
            Glide.with(context).load(R.drawable.ufi_heart_active).into(ivLike);
            ivLike.setColorFilter(context.getResources().getColor(R.color.red));

            formatLikesText(post);
            ivLike.setTag("liked");
        }

        private void unlikeAPost(Post post){
            Glide.with(context).load(R.drawable.ufi_heart).into(ivLike);
            ivLike.setColorFilter(context.getResources().getColor(R.color.black));

            formatLikesText(post);
            ivLike.setTag("unliked");
        }

        private void formatLikesText(Post post){
            String likes = post.formatLikes();
            if (likes == null) {
                tvLikes.setVisibility(View.GONE);
            } else {
                tvLikes.setVisibility(View.VISIBLE);
                tvLikes.setText(post.formatLikes());
            }
        }

        public void bind(final Post post) {

            if (likes.contains(post.getId())) {
                likeAPost(post);
            } else {
                unlikeAPost(post);
            }

            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ivLike.getTag().equals("unliked")) {
                        post.addLike();
                        likes.add(post.getId());
                        user.put("likedPosts", likes);
                        user.saveInBackground();
                        likeAPost(post);
                    } else if (ivLike.getTag().equals("liked")) {
                        post.subtractLike();
                        likes.remove(post.getId());
                        user.put("likedPosts", likes);
                        user.saveInBackground();
                        unlikeAPost(post);
                    }
                }
            });


            llPosts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onItemClickedPostDetails(getAdapterPosition());
                }
            });

            rlHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onItemClickedUserDetails(post.getUser().getObjectId());
                }
            });

            tvUsername.setText(post.getUser().getUsername());
            tvDescription.setText(post.getDescription());
            tvTimestamp.setText(Post.getRelativeTimeAgo(post.getTimestamp().toString()));
            formatLikesText(post);

            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(post.getImage().getUrl()).into(ivImage);
            } else {
                ivImage.setVisibility(View.GONE);
            }

            ParseFile profilePicture = post.getUser().getParseFile("profilePic");
            if (profilePicture != null) {
                Glide.with(context).load(profilePicture.getUrl())
                        .placeholder(R.drawable.profilepic).fitCenter().circleCrop().into(ivProfilePic);
            } else {
                Glide.with(context).load(R.drawable.profilepic).fitCenter().circleCrop().into(ivProfilePic);
            }
        }
    }

}
