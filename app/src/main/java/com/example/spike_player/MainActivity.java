package com.example.spike_player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.spike_player.fragments.ExploreFragment;
import com.example.spike_player.fragments.LibraryFragment;
import com.example.spike_player.fragments.ProfileFragment;
import com.example.spike_player.fragments.TrendingFragment;
import com.example.spike_player.fragments.WatchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ImageView upload;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout fragment_container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        changeStatusBarColor();

        bottomNavigationView=findViewById(R.id.bottomnavigation);
        fragment_container=findViewById(R.id.fragment_container);




        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_watch:
                    selectedFragment = new WatchFragment();
                    break;
                case R.id.nav_trending:
                     selectedFragment = new TrendingFragment();
                    break;
                case R.id.nav_explore:
                     selectedFragment = new ExploreFragment();
                    break;
                case R.id.nav_library:
                     selectedFragment = new LibraryFragment();
                    break;
                case R.id.nav_profile:
                    selectedFragment = new ProfileFragment();
                    break;
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }

            return false;
        });

        // Load the initial fragment
        loadFragment(new WatchFragment());


    }
    private void changeStatusBarColor() {
        // Use the following code to change the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }
    }
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}