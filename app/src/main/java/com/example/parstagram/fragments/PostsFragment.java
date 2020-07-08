package com.example.parstagram.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.parstagram.EndlessRecyclerViewScrollListener;
import com.example.parstagram.Post;
import com.example.parstagram.PostsAdapter;
import com.example.parstagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;


public class PostsFragment extends Fragment {
    private static final String TAG = "PostsFragment";
    SwipeRefreshLayout swipeContainer;

    Date lastPostTime;

    RecyclerView rvPosts;
    PostsAdapter adapter;
    List<Post> posts;
    LinearLayoutManager llm;

    private EndlessRecyclerViewScrollListener scrollListener;
    private PostsFragment.OnItemSelectedListener listener;

    // Define the events that the fragment will use to communicate
    public interface OnItemSelectedListener {
        // This can be any number of events to be sent to the activity
        public void onPostDetailsItemSelected(Post post);
        public void onUserDetailItemSelected(String userId);
    }

    public PostsFragment() {
        // Required empty public constructor
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        rvPosts = view.findViewById(R.id.rvPosts);
        posts = new ArrayList<Post>();

        llm = new LinearLayoutManager(getContext());
        //click listener for each item in the to do list
        //will bring up a screen where user can edit the item
        PostsAdapter.OnClickListener onClickListener = new PostsAdapter.OnClickListener(){
            @Override
            public void onItemClickedPostDetails(int position) {
                listener.onPostDetailsItemSelected(posts.get(position));
            }

            @Override
            public void onItemClickedUserDetails(String objectId) {
                listener.onUserDetailItemSelected(objectId);
            }
        };

        adapter = new PostsAdapter(getContext(), posts, onClickListener);
        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(llm);

        scrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvPosts.addOnScrollListener(scrollListener);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                queryPost(true);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        queryPost(false);
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED);
        query.whereLessThan(Post.KEY_CREATED, lastPostTime);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if( e != null){
                    Log.e(TAG, "something went wrong", e);
                    return;
                }else{
                    int size = objects.size();
                    if(size != 0){
                        lastPostTime = objects.get(size-1).getTimestamp();
                        adapter.addAll(objects);
                    }
                }

            }
        });


    }

    protected void queryPost(final boolean refresh){
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if( e != null){
                    Log.e(TAG, "something went wrong", e);
                    return;
                }else{
                    lastPostTime = objects.get(objects.size()-1).getTimestamp();
                    if(refresh){
                        adapter.clear();
                        adapter.addAll(objects);
                        swipeContainer.setRefreshing(false);
                        scrollListener.resetState();
                    }else{
                        posts.addAll(objects);
                        adapter.notifyDataSetChanged();
                    }
                }

            }
        });
    }
}