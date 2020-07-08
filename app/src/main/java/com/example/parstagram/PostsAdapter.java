package com.example.parstagram;

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
import com.parse.ParseFile;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public interface OnClickListener{
        void onItemClickedPostDetails(int position);
        void onItemClickedUserDetails(String userId);
    }

    OnClickListener onClickListener;
    Context context;
    List<Post> posts;

public PostsAdapter(Context context, List<Post> posts,  OnClickListener onClickListener){
    this.context = context;
    this.posts = posts;
    this.onClickListener = onClickListener;
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

    class ViewHolder extends RecyclerView.ViewHolder{

    LinearLayout llPosts;
    TextView tvUsername;
    TextView tvDescription;
    TextView tvTimestamp;
    ImageView ivImage;
    ImageView ivProfilePic;
    RelativeLayout rlHeader;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);

            llPosts = itemView.findViewById(R.id.llPost);
            rlHeader = itemView.findViewById(R.id.rlHeader);

        }

        public void bind(final Post post) {

           llPosts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    onClickListener.onItemClickedPostDetails(getAdapterPosition());
                }
            });

            rlHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    onClickListener.onItemClickedUserDetails(post.getUser().getObjectId());
                }
            });

            tvUsername.setText(post.getUser().getUsername());
            tvDescription.setText(post.getDescription());
            tvTimestamp.setText(Post.getRelativeTimeAgo(post.getTimestamp().toString()));

            ParseFile image = post.getImage();
            if(image !=null){
                Glide.with(context).load(post.getImage().getUrl()).into(ivImage);
            }else{
                ivImage.setVisibility(View.GONE);
            }

            ParseFile profilePicture = post.getUser().getParseFile("profilePic");
            if(profilePicture != null){
                Glide.with(context).load(profilePicture.getUrl()).placeholder(R.drawable.profilepic).fitCenter().circleCrop().into(ivProfilePic);
            }else{
                Glide.with(context).load(R.drawable.profilepic).fitCenter().circleCrop().into(ivProfilePic);
            }
        }
    }

}
