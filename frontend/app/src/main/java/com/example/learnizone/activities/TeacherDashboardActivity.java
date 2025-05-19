package com.example.learnizone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnizone.CourseDetailActivity;
import com.example.learnizone.LoginActivity;
import com.example.learnizone.R;
import com.example.learnizone.adapters.CourseAdapter;
import com.example.learnizone.models.Course;
import com.example.learnizone.repositories.CourseRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class TeacherDashboardActivity extends AppCompatActivity {
    private RecyclerView coursesRecyclerView;
    private CourseAdapter courseAdapter;
    private CourseRepository courseRepository;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Teacher Dashboard");

        // Initialize RecyclerView
        coursesRecyclerView = findViewById(R.id.coursesRecyclerView);
        coursesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseAdapter = new CourseAdapter(new ArrayList<>(), course -> {
            // Handle course click
            Intent intent = new Intent(this, CourseDetailActivity.class);
            intent.putExtra("courseId", course.getId());
            startActivity(intent);
        });
        coursesRecyclerView.setAdapter(courseAdapter);

        // Initialize CourseRepository
        courseRepository = new CourseRepository();

        // Set up FAB for creating new course
        FloatingActionButton fab = findViewById(R.id.fab_add_course);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, CreateCourseActivity.class);
            startActivity(intent);
        });

        // Load teacher's courses
        loadTeacherCourses();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload courses when returning to this activity
        loadTeacherCourses();
    }

    private void loadTeacherCourses() {
        String currentUserId = auth.getCurrentUser().getUid();
        if (currentUserId != null) {
            courseRepository.getCoursesByTeacher(currentUserId, new CourseRepository.OnCoursesLoadedListener() {
                @Override
                public void onCoursesLoaded(List<Course> courses) {
                    if (courses != null && !courses.isEmpty()) {
                        courseAdapter.updateCourses(courses);
                    } else {
                        Toast.makeText(TeacherDashboardActivity.this, 
                            "No courses found. Create your first course!", 
                            Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(TeacherDashboardActivity.this,
                        "Error loading courses: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            // Handle profile click
            return true;
        } else if (id == R.id.action_logout) {
            // Handle logout
            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
} 