package com.example.learnizone;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        setupBottomNavigation();

        // Par défaut, on affiche le fragment d'accueil
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LearnFragment())
                    .commit();
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.navigation_learn) {
                fragment = new LearnFragment();
            } else if (itemId == R.id.navigation_courses) {
                fragment = new CoursesFragment();
            } else if (itemId == R.id.navigation_profile) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                return true;
            }

            return false;
        });
    }

    public void navigateToCourses() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new CoursesFragment())
                .addToBackStack(null)
                .commit();
    }
}
