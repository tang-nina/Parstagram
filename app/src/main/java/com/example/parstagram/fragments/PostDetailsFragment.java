package com.example.parstagram.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.Comment;
import com.example.parstagram.CommentsAdapter;
import com.example.parstagram.Post;
import com.example.parstagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostDetailsFragment extends Fragment {
    TextView tvCaption;
    TextView tvUsername;
    ImageView ivPost;
    TextView tvTimestamp;
    ImageView ivProfilePic;
    RelativeLayout rlPoster;
    ImageView ivLike;
    TextView tvLikes;
    TextView tvViewComments;
    RecyclerView rvComments;
    Button btnSubmit;
    EditText etComment;
    CommentsAdapter adapter;
    LinearLayoutManager llm;

    ArrayList<String> likes = new ArrayList<>();
    List<Comment> comments;

    ParseUser user = ParseUser.getCurrentUser();

    private PostsFragment.OnItemSelectedListener listener;

    private static final String ARG_PARAM1 = "param1";

    private Post curPost;

    public PostDetailsFragment() {
        // Required empty public constructor
    }

    public static PostDetailsFragment newInstance(Post post) {
        PostDetailsFragment fragment = new PostDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, post);
        fragment.setArguments(args);
        return fragment;
    }

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PostsFragment.OnItemSelectedListener) {
            listener = (PostsFragment.OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            curPost = (Post) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        ArrayList temp = (ArrayList) user.get("likedPosts");
        if(temp != null){
            likes.addAll(temp);
        }

        comments = new ArrayList<>();

        tvCaption= view.findViewById(R.id.tvCaption);
        tvUsername = view.findViewById(R.id.tvUsername);
        ivPost = view.findViewById(R.id.ivPost);
        tvTimestamp = view.findViewById(R.id.tvTimestamp);
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        rlPoster = view.findViewById(R.id.rlPoster);
        tvLikes = view.findViewById(R.id.tvLikes);
        tvViewComments = view.findViewById(R.id.tvViewComments);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        etComment = view.findViewById(R.id.etComment);

        rvComments = view.findViewById(R.id.rvComments);

        adapter = new CommentsAdapter(getContext(), comments);
        llm = new LinearLayoutManager(getContext());
        rvComments.setLayoutManager(llm);
        rvComments.setAdapter(adapter);


        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_COMMENTER);
        query.include(Comment.KEY_COMMENT);
        query.include(Comment.KEY_POST);
        // query.setLimit(10); check this
        query.whereEqualTo("post", curPost);
        query.addDescendingOrder(Comment.KEY_CREATED);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                if( e != null){
                    Log.e("hi", "something went wrong", e);
                    return;
                }else{
                    comments.clear();
                    comments.addAll(objects);
                    adapter.notifyDataSetChanged();
                    tvViewComments.setText("Comments (" + comments.size()+")");
                }

            }
        });

        tvCaption.setText(curPost.getDescription());
        tvUsername.setText(curPost.getUser().getUsername());
        tvTimestamp.setText(Post.getRelativeTimeAgo(curPost.getTimestamp().toString()));

        ivLike = view.findViewById(R.id.ivLike);

        if(likes.contains(curPost.getId())){
            System.out.println(curPost.getId());
            Glide.with(getContext()).load(R.drawable.ufi_heart_active).into(ivLike);
            ivLike.setColorFilter(getContext().getResources().getColor(R.color.red));

            String likes = curPost.formatLikes();
            tvLikes.setVisibility(View.VISIBLE);
            tvLikes.setText(curPost.formatLikes());

            ivLike.setTag("liked");
        }else{
            Glide.with(getContext()).load(R.drawable.ufi_heart).into(ivLike);
            ivLike.setColorFilter(getContext().getResources().getColor(R.color.black));

            String likes = curPost.formatLikes();
            if(likes == null){
                tvLikes.setVisibility(View.GONE);
            }else{
                tvLikes.setVisibility(View.VISIBLE);
                tvLikes.setText(curPost.formatLikes());
            }

            ivLike.setTag("unliked");
        }

        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ivLike.getTag().equals("unliked")){
                    curPost.addLike();
                    likes.add(curPost.getId());
                    user.put("likedPosts", likes);
                    user.saveInBackground();

                    Glide.with(getContext()).load(R.drawable.ufi_heart_active).into(ivLike);
                    ivLike.setColorFilter(getContext().getResources().getColor(R.color.red));

                    String likes = curPost.formatLikes();
                    tvLikes.setVisibility(View.VISIBLE);
                    tvLikes.setText(curPost.formatLikes());

                    ivLike.setTag("liked");
                }else if(ivLike.getTag().equals("liked")){
                    curPost.subtractLike();
                    likes.remove(curPost.getId());
                    user.put("likedPosts", likes);
                    user.saveInBackground();

                    Glide.with(getContext()).load(R.drawable.ufi_heart).into(ivLike);
                    ivLike.setColorFilter(getContext().getResources().getColor(R.color.black));

                    String likes = curPost.formatLikes();
                    if(likes == null){
                        tvLikes.setVisibility(View.GONE);
                    }else{
                        tvLikes.setVisibility(View.VISIBLE);
                        tvLikes.setText(curPost.formatLikes());
                    }

                    ivLike.setTag("unliked");

                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentText = etComment.getText().toString();
                final Comment comment = new Comment();
                comment.setComment(commentText);
                comment.setPost(curPost);
                comment.setUser(user);
               comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            Toast.makeText(getContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                        }
                        etComment.setText("");
                        comments.add(0, comment);
                        adapter.notifyDataSetChanged();
                        tvViewComments.setText("Comments (" + adapter.getItemCount()+")");
                    }
                });
                //send to recycler view
            }
        });

        ParseFile image = curPost.getImage();
        if(image != null){
            Glide.with(view.getContext()).load(curPost.getImage().getUrl()).into(ivPost);
        }else{
            ivPost.setVisibility(View.GONE);
        }

        ParseFile profilePicture = curPost.getUser().getParseFile("profilePic");
        if(profilePicture != null){
            Glide.with(view.getContext()).load(profilePicture.getUrl()).placeholder(R.drawable.profilepic).fitCenter().circleCrop().into(ivProfilePic);
        }else{
            Glide.with(view.getContext()).load(R.drawable.profilepic).fitCenter().circleCrop().into(ivProfilePic);
        }


        rlPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                listener.onUserDetailItemSelected(curPost.getUser().getObjectId());
            }
        });


    }

}