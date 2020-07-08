package com.example.parstagram.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.LoginActivity;
import com.example.parstagram.Post;
import com.example.parstagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    String id;
    ParseUser user;
    List<Post> posts;

    Button btnLogout;
    ImageView ivProfilePic;
    TextView tvUsername;
    ImageView ivCamera;

    RecyclerView rvYourPosts;
    ProfileAdapter adapter;
    GridLayoutManager glm;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String userId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull final View view, @NonNull Bundle savedInstanceState){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", id);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                user = objects.get(0);
                posts = new ArrayList<Post>();

                btnLogout = view.findViewById(R.id.btnLogout);
                ivCamera = view.findViewById(R.id.ivCamera);

                tvUsername = view.findViewById(R.id.tvUsername);
                ivProfilePic = view.findViewById(R.id.ivProfilePic);

                rvYourPosts = view.findViewById(R.id.rvYourPosts);
                adapter = new ProfileAdapter(view.getContext(), posts);
                glm = new GridLayoutManager(view.getContext(), 3);
                rvYourPosts.setAdapter(adapter);
                rvYourPosts.setLayoutManager(glm);

                tvUsername.setText(user.getUsername());

                ParseFile profilePicture = user.getParseFile("profilePic");
                if(profilePicture != null){
                    Glide.with(view.getContext()).load(profilePicture.getUrl()).placeholder(R.drawable.profilepic).fitCenter().circleCrop().into(ivProfilePic);
                }else{
                    Glide.with(view.getContext()).load(R.drawable.profilepic).fitCenter().circleCrop().into(ivProfilePic);
                }

                btnLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ParseUser.logOut();
                        //ParseUser currentUser = ParseUser.getCurrentUser();
                        Intent intent = new Intent(view.getContext(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });

                ivCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //camera stuff here
                    }
                });

                queryPost(true);
            }
        });
    }

    protected void queryPost(final boolean refresh){
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, user);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if( e != null){
                    Log.e(TAG, "something went wrong", e);
                    return;
                }else{
                    if(refresh){
                        adapter.clear();
                        adapter.addAll(objects);
                    }else{
                        posts.addAll(objects);
                        adapter.notifyDataSetChanged();
                    }
                }

            }
        });
    }
}