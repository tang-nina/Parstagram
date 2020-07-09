package com.example.parstagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.parstagram.EndlessRecyclerViewScrollListener;
import com.example.parstagram.LoginActivity;
import com.example.parstagram.Post;
import com.example.parstagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 50;

    public String photoFileName = "photo.jpg";
    private File photoFile;

    SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;
    Date lastPostTime;

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
        btnLogout = view.findViewById(R.id.btnLogout);
        ivCamera = view.findViewById(R.id.ivCamera);

        if(!(id.equals(ParseUser.getCurrentUser().getObjectId()))){
            btnLogout.setVisibility(View.GONE);
            ivCamera.setVisibility(View.GONE);
        }else{
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
                    launchCamera();
                }
            });
        }

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", id);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                user = objects.get(0);
                posts = new ArrayList<Post>();

                tvUsername = view.findViewById(R.id.tvUsername);
                ivProfilePic = view.findViewById(R.id.ivProfilePic);

                rvYourPosts = view.findViewById(R.id.rvYourPosts);
                adapter = new ProfileAdapter(view.getContext(), posts);
                glm = new GridLayoutManager(view.getContext(), 3);
                rvYourPosts.setAdapter(adapter);
                rvYourPosts.setLayoutManager(glm);

                scrollListener = new EndlessRecyclerViewScrollListener(glm) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        // Triggered only when new data needs to be appended to the list
                        // Add whatever code is needed to append new items to the bottom of the list
                        loadNextDataFromApi(page);
                    }
                };
                // Adds the scroll listener to RecyclerView
                rvYourPosts.addOnScrollListener(scrollListener);

                tvUsername.setText(user.getUsername());

                ParseFile profilePicture = user.getParseFile("profilePic");
                if(profilePicture != null){
                    Glide.with(view.getContext()).load(profilePicture.getUrl()).placeholder(R.drawable.profilepic).fitCenter().circleCrop().into(ivProfilePic);
                }else{
                    Glide.with(view.getContext()).load(R.drawable.profilepic).fitCenter().circleCrop().into(ivProfilePic);
                }

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
        });
    }

    public void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider.PARSTAGRAM", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                //ivProfilePic.setImageBitmap(takenImage);
                Glide.with(getContext()).load(photoFile).placeholder(R.drawable.profilepic).fitCenter().circleCrop().into(ivProfilePic);
                sendPost(user, photoFile);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendPost(ParseUser user, File photo){
        user.put("profilePic", new ParseFile(photo));
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "done: error while saving", e);
                    Toast.makeText(getContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loadNextDataFromApi(int offset) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.whereEqualTo(Post.KEY_USER, user);
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