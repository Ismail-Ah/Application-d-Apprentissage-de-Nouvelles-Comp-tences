package com.example.learnizone;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Show logged-in user info
        showUserInfo();

        // Set up bottom navigation
        setupBottomNavigation();

        // Default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LearnFragment())
                    .commit();
        }
    }

    private void showUserInfo() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // User is logged in, fetch Firestore data
            String userId = currentUser.getUid();
            db.collection("users").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String fullName = document.getString("fullName");
                                String email = document.getString("email");
                                List<String> selectedDomains = (List<String>) document.get("selectedDomains");

                                // Build user info message
                                StringBuilder userInfo = new StringBuilder();
                                userInfo.append("Welcome, ").append(fullName).append("!\n");
                                userInfo.append("Email: ").append(email).append("\n");
                                userInfo.append("Domains: ");
                                if (selectedDomains != null && !selectedDomains.isEmpty()) {
                                    userInfo.append(String.join(", ", selectedDomains));
                                } else {
                                    userInfo.append("None");
                                }

                                // Show Toast
                                Toast.makeText(MainActivity2.this, userInfo.toString(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(MainActivity2.this, "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity2.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // No user logged in, redirect to LoginActivity
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
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
            } else if (itemId == R.id.nav_courses) {
                fragment = new LearnActivity(); // Assuming LearnActivity should be a fragment
            } else if (itemId == R.id.navigation_profile) {
                fragment = new ProfileActivity(); // Assuming ProfileActivity should be a fragment
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