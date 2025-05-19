package com.example.learnizone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnizone.R;
import com.example.learnizone.adapters.SectionAdapter;
import com.example.learnizone.models.Course;
import com.example.learnizone.models.CourseSection;
import com.example.learnizone.repositories.CourseRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CourseSectionsActivity extends AppCompatActivity {
    private RecyclerView sectionsRecyclerView;
    private SectionAdapter sectionAdapter;
    private FloatingActionButton addSectionFab;
    private CourseRepository courseRepository;
    private String courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_sections);

        // Get course ID from intent
        courseId = getIntent().getStringExtra("courseId");
        if (courseId == null) {
            Toast.makeText(this, "Error: Course ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase
        courseRepository = new CourseRepository();

        // Initialize views
        sectionsRecyclerView = findViewById(R.id.sectionsRecyclerView);
        addSectionFab = findViewById(R.id.addSectionFab);

        // Setup RecyclerView
        sectionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sectionAdapter = new SectionAdapter(new ArrayList<>(), section -> {
            // Navigate to add subsection activity
            Intent intent = new Intent(CourseSectionsActivity.this, AddSubsectionActivity.class);
            intent.putExtra("courseId", courseId);
            intent.putExtra("sectionId", section.getId());
            startActivity(intent);
        });
        sectionsRecyclerView.setAdapter(sectionAdapter);

        // Set up click listeners
        addSectionFab.setOnClickListener(v -> showAddSectionDialog());

        // Load sections
        loadSections();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSections();
    }

    private void loadSections() {
        courseRepository.getCourse(courseId)
                .addOnSuccessListener(documentSnapshot -> {
                    Course course = documentSnapshot.toObject(Course.class);
                    if (course != null && course.getModules() != null) {
                        List<CourseSection> sections = new ArrayList<>();
                        for (Map<String, Object> module : course.getModules()) {
                            CourseSection section = new CourseSection(
                                    (String) module.get("id"),
                                    (String) module.get("title"),
                                    (String) module.get("description"),
                                    ((Long) module.get("order")).intValue()
                            );
                            sections.add(section);
                        }
                        sectionAdapter.updateSections(sections);
                    }
                })
                .addOnFailureListener(e -> 
                    Toast.makeText(this, "Error loading sections: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show());
    }

    private void showAddSectionDialog() {
        // Create a dialog to add a new section
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Add New Section");

        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_section, null);
        TextInputEditText titleInput = dialogView.findViewById(R.id.sectionTitleInput);
        TextInputEditText descriptionInput = dialogView.findViewById(R.id.sectionDescriptionInput);

        builder.setView(dialogView);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create new section
            String sectionId = UUID.randomUUID().toString();
            CourseSection newSection = new CourseSection(
                    sectionId,
                    title,
                    description,
                    sectionAdapter.getItemCount()
            );

            // Add section to course
            courseRepository.addSectionToCourse(courseId, newSection)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Section added successfully", Toast.LENGTH_SHORT).show();
                        loadSections();
                    })
                    .addOnFailureListener(e -> 
                        Toast.makeText(this, "Error adding section: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
} 