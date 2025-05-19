package com.example.learnizone.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnizone.R;
import com.example.learnizone.adapters.DomainAdapter;
import com.example.learnizone.models.Course;
import com.example.learnizone.models.Domain;
import com.example.learnizone.repositories.CourseRepository;
import com.example.learnizone.repositories.DomainRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateCourseActivity extends AppCompatActivity {
    private TextInputEditText titleInput;
    private TextInputEditText descriptionInput;
    private TextInputEditText imageUrlInput;
    private AutoCompleteTextView domainDropdown;
    private MaterialButton createButton;
    private CourseRepository courseRepository;
    private DomainRepository domainRepository;
    private List<String> domains;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private Uri selectedImageUri;
    private DomainAdapter domainAdapter;
    private RecyclerView domainsRecyclerView;

    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    // Assuming courseImagePreview is an ImageView
                    // courseImagePreview.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        // Initialize repositories
        courseRepository = new CourseRepository();
        domainRepository = new DomainRepository();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize views
        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        imageUrlInput = findViewById(R.id.imageUrlInput);
        domainDropdown = findViewById(R.id.domainDropdown);
        createButton = findViewById(R.id.createButton);
        domainsRecyclerView = findViewById(R.id.domainsRecyclerView);

        // Set up RecyclerView
        domainsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize domain adapter
        domainAdapter = new DomainAdapter(new ArrayList<>(), domain -> {
            // Handle domain selection
            domainDropdown.setText(domain.getName());
            // Hide the RecyclerView after selection
            domainsRecyclerView.setVisibility(View.GONE);
        });
        domainsRecyclerView.setAdapter(domainAdapter);

        // Set up domain dropdown click listener
        domainDropdown.setOnClickListener(v -> {
            // Show the RecyclerView when dropdown is clicked
            domainsRecyclerView.setVisibility(View.VISIBLE);
        });

        // Load domains
        loadDomains();

        // Set up click listeners
        createButton.setOnClickListener(v -> createCourse());
    }

    private void loadDomains() {
        domainRepository.getAllDomains(new DomainRepository.OnDomainsLoadedListener() {
            @Override
            public void onDomainsLoaded(List<Domain> domains) {
                if (domains.isEmpty()) {
                    // If no domains exist, add default domains
                    domainRepository.addDefaultDomains();
                    // Reload domains after adding defaults
                    loadDomains();
                } else {
                    domainAdapter.updateDomains(domains);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(CreateCourseActivity.this,
                        "Erreur lors du chargement des domaines: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createCourse() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String imageUrl = imageUrlInput.getText().toString().trim();
        String domain = domainDropdown.getText().toString().trim();

        // Validate inputs
        if (title.isEmpty()) {
            titleInput.setError("Title is required");
            return;
        }
        if (description.isEmpty()) {
            descriptionInput.setError("Description is required");
            return;
        }
        if (domain.isEmpty()) {
            domainDropdown.setError("Domain is required");
            return;
        }

        // Get current user ID
        String currentUserId = auth.getCurrentUser().getUid();
        if (currentUserId == null) {
            Toast.makeText(this, "You must be logged in to create a course", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create new course
        String courseId = UUID.randomUUID().toString();
        Course course = new Course(
                courseId,
                title,
                domain,
                imageUrl,
                "0", // duration
                0.0, // rating
                "Beginner", // difficulty
                description,
                new ArrayList<>(), // prerequisites
                new ArrayList<>(), // learning objectives
                new ArrayList<>(), // modules
                currentUserId // Set the current user as the instructor
        );

        // Save course to Firestore
        courseRepository.createCourse(course)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Course created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> 
                    Toast.makeText(this, "Error creating course: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show());
    }
} 