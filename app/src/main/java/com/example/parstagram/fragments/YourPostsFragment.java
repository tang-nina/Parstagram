package com.example.parstagram.fragments;

import android.util.Log;

import com.example.parstagram.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class YourPostsFragment extends PostsFragment {
    private static final String TAG = "ProfileFragment";
    @Override
    protected void queryPost(final boolean refresh){
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
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
                        swipeContainer.setRefreshing(false);
                    }else{
                        posts.addAll(objects);
                        adapter.notifyDataSetChanged();
                    }
                }

            }
        });
    }
}