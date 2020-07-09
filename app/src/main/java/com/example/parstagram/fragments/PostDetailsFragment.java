package com.example.parstagram.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.parstagram.Post;
import com.example.parstagram.R;
import com.parse.ParseFile;

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

    private PostsFragment.OnItemSelectedListener listener;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private Post curPost;

    public PostDetailsFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PostDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        tvCaption= view.findViewById(R.id.tvCaption);
        tvUsername = view.findViewById(R.id.tvUsername);
        ivPost = view.findViewById(R.id.ivPost);
        tvTimestamp = view.findViewById(R.id.tvTimestamp);
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        rlPoster = view.findViewById(R.id.rlPoster);

        tvCaption.setText(curPost.getDescription());
        tvUsername.setText(curPost.getUser().getUsername());
        tvTimestamp.setText(Post.getRelativeTimeAgo(curPost.getTimestamp().toString()));

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