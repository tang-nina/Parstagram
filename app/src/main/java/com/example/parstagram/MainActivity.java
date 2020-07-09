package com.example.parstagram;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.parstagram.fragments.ComposeFragment;
import com.example.parstagram.fragments.PostDetailsFragment;
import com.example.parstagram.fragments.PostsFragment;
import com.example.parstagram.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity implements PostsFragment.OnItemSelectedListener {
    private static final String TAG = "MainActivity";
    BottomNavigationView bottomNavigationView;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) { //menuItem is one of the items we put on the bar
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        fragment = new PostsFragment();
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                        break;
                    case R.id.action_compose:
                        fragment = new ComposeFragment();
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
                        break;
                    case R.id.action_profile:
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ProfileFragment fragmentDemo = ProfileFragment.newInstance(ParseUser.getCurrentUser().getObjectId());
                        ft.replace(R.id.flContainer, fragmentDemo);
                        ft.addToBackStack(null);
                        ft.commit();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + menuItem.getItemId());
                }
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.action_home); //default tab open
    }

    @Override
    public void onPostDetailsItemSelected(Post post) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        PostDetailsFragment fragment = PostDetailsFragment.newInstance(post);
        ft.replace(R.id.flContainer, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onUserDetailItemSelected(String objectId) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ProfileFragment fragmentDemo = ProfileFragment.newInstance(objectId);
        ft.replace(R.id.flContainer, fragmentDemo);
        ft.addToBackStack(null);
        ft.commit();
    }
}