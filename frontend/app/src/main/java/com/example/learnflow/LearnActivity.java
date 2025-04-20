package com.example.learnflow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class LearnActivity extends AppCompatActivity {
    private RecyclerView coursesRecyclerView;
    private CourseAdapter courseAdapter;
    private List<Course> courseList;
    private EditText searchBar;
    private Button filterButton, sortButton;
    private FloatingActionButton addCourseFab;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        // Initialize views
        coursesRecyclerView = findViewById(R.id.courses_recycler_view);
        searchBar = findViewById(R.id.search_bar);
        filterButton = findViewById(R.id.filter_button);
        sortButton = findViewById(R.id.sort_button);
        addCourseFab = findViewById(R.id.add_course_fab);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up RecyclerView
        courseList = new ArrayList<>();
        // Sample data with new fields
        courseList.add(new Course("Python Basics", "Programmation", "10h 30min", 4.8f, 75));
        courseList.add(new Course("Java for Beginners", "Programmation", "15h 45min", 4.5f, 50));
        courseList.add(new Course("Web Development", "Développement Web", "12h 20min", 4.9f, 30));
        courseList.add(new Course("Data Science", "Science des Données", "20h 10min", 4.7f, 90));

        courseAdapter = new CourseAdapter(this, courseList);
        coursesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        coursesRecyclerView.setAdapter(courseAdapter);

        // Search functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                courseAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Filter button
        filterButton.setOnClickListener(v -> {
            // Show a dialog to filter by progress
            String[] options = {"All", "Progress > 50%", "Progress > 75%"};
            new AlertDialog.Builder(LearnActivity.this)
                    .setTitle("Filter by Progress")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            courseAdapter.filter(""); // Reset filter
                        } else if (which == 1) {
                            courseAdapter.filterByProgress(50);
                        } else if (which == 2) {
                            courseAdapter.filterByProgress(75);
                        }
                    })
                    .show();
        });

        // Sort button
        sortButton.setOnClickListener(v -> {
            // Sort by progress (descending)
            courseAdapter.sortByProgress();
            Toast.makeText(LearnActivity.this, "Sorted by progress", Toast.LENGTH_SHORT).show();
        });

        // Floating Action Button
        addCourseFab.setOnClickListener(v -> {
            // Show a dialog to add a new course
            AlertDialog.Builder builder = new AlertDialog.Builder(LearnActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_course, null);
            EditText editCourseTitle = dialogView.findViewById(R.id.edit_course_title);
            EditText editCourseCategory = dialogView.findViewById(R.id.edit_course_category);
            EditText editCourseDuration = dialogView.findViewById(R.id.edit_course_duration);
            EditText editCourseRating = dialogView.findViewById(R.id.edit_course_rating);

            builder.setTitle("Add New Course")
                    .setView(dialogView)
                    .setPositiveButton("Add", (dialog, which) -> {
                        String title = editCourseTitle.getText().toString().trim();
                        String category = editCourseCategory.getText().toString().trim();
                        String duration = editCourseDuration.getText().toString().trim();
                        String ratingStr = editCourseRating.getText().toString().trim();
                        if (!title.isEmpty() && !category.isEmpty() && !duration.isEmpty() && !ratingStr.isEmpty()) {
                            try {
                                float rating = Float.parseFloat(ratingStr);
                                courseList.add(new Course(title, category, duration, rating, 0));
                                courseAdapter.notifyDataSetChanged();
                                Toast.makeText(LearnActivity.this, "Course added", Toast.LENGTH_SHORT).show();
                            } catch (NumberFormatException e) {
                                Toast.makeText(LearnActivity.this, "Invalid rating format", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LearnActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                //Abdelouahed
                return true;
            } else if (itemId == R.id.nav_courses) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                //Ismail
                return true;
            }
            return false;
        });
    }
}
