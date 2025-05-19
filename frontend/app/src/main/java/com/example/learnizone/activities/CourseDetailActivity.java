package com.example.learnizone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnizone.R;
import com.example.learnizone.adapters.CourseSectionAdapter;
import com.example.learnizone.models.Course;
import com.example.learnizone.models.CourseSection;
import com.example.learnizone.repositories.CourseRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CourseDetailActivity extends AppCompatActivity {
    private String courseId;
    private Course course;
    private CourseRepository courseRepository;
    private RecyclerView sectionsRecyclerView;
    private CourseSectionAdapter sectionAdapter;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Get course ID from intent
        courseId = getIntent().getStringExtra("courseId");
        if (courseId == null) {
            Toast.makeText(this, "Error: Course ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Course Details");

        // Initialize views
        TextView titleTextView = findViewById(R.id.courseTitle);
        TextView descriptionTextView = findViewById(R.id.courseDescription);
        sectionsRecyclerView = findViewById(R.id.course_modules);

        // Initialize RecyclerView
        sectionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sectionAdapter = new CourseSectionAdapter(new ArrayList<>(), section -> {
            // Handle section click - navigate to section details
            Intent intent = new Intent(this, CourseSectionsActivity.class);
            intent.putExtra("courseId", courseId);
            intent.putExtra("sectionId", section.getId());
            startActivity(intent);
        });
        sectionsRecyclerView.setAdapter(sectionAdapter);

        // Initialize CourseRepository
        courseRepository = new CourseRepository();

        // Set up FAB for adding new section
        FloatingActionButton fab = findViewById(R.id.fab_add_section);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddSubsectionActivity.class);
            intent.putExtra("courseId", courseId);
            startActivity(intent);
        });

        // Load course details
        loadCourseDetails();
    }

    private void loadCourseDetails() {
        courseRepository.getCourseById(courseId)
                .addOnSuccessListener(course -> {
                    this.course = course;
                    updateUI();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading course: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUI() {
        if (course != null) {
            // Update title and description
            TextView titleTextView = findViewById(R.id.courseTitle);
            TextView descriptionTextView = findViewById(R.id.courseDescription);
            
            titleTextView.setText(course.getTitle());
            descriptionTextView.setText(course.getDescription());

            // Update sections
            List<CourseSection> sections = new ArrayList<>();
            if (course.getModules() != null) {
                for (Map<String, Object> module : course.getModules()) {
                    CourseSection section = new CourseSection(
                        (String) module.get("id"),
                        (String) module.get("title"),
                        (String) module.get("description"),
                        (Integer) module.get("order")
                    );
                    sections.add(section);
                }
            }
            sectionAdapter.updateSections(sections);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_edit_course) {
            // Handle edit course
            Intent intent = new Intent(this, CreateCourseActivity.class);
            intent.putExtra("courseId", courseId);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload course details when returning to this activity
        loadCourseDetails();
    }
} 