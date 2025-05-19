package com.example.learnizone.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learnizone.R;
import com.example.learnizone.models.CourseSection;
import com.example.learnizone.repositories.CourseRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddSubsectionActivity extends AppCompatActivity {
    private TextInputEditText titleInput;
    private TextInputEditText contentInput;
    private CourseRepository courseRepository;
    private String courseId;
    private String sectionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subsection);

        // Get course and section IDs from intent
        courseId = getIntent().getStringExtra("courseId");
        sectionId = getIntent().getStringExtra("sectionId");

        if (courseId == null || sectionId == null) {
            Toast.makeText(this, "Error: Course or section ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase
        courseRepository = new CourseRepository();

        // Initialize views
        titleInput = findViewById(R.id.subsectionTitleInput);
        contentInput = findViewById(R.id.subsectionContentInput);

        // Set up click listeners
        findViewById(R.id.saveButton).setOnClickListener(v -> saveSubsection());
    }

    private void saveSubsection() {
        String title = titleInput.getText().toString().trim();
        String content = contentInput.getText().toString().trim();

        if (title.isEmpty()) {
            titleInput.setError("Title is required");
            return;
        }

        // Create new subsection
        String subsectionId = UUID.randomUUID().toString();
        Map<String, Object> subsection = new HashMap<>();
        subsection.put("id", subsectionId);
        subsection.put("title", title);
        subsection.put("content", content);
        subsection.put("type", "text"); // Default type is text

        // Add subsection to section
        courseRepository.addSubsectionToSection(courseId, sectionId, subsection)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Subsection added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> 
                    Toast.makeText(this, "Error adding subsection: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show());
    }
} 