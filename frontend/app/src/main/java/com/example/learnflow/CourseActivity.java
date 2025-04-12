package com.example.learnflow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CourseActivity extends AppCompatActivity {
    private static final String TAG = "CourseActivity";

    private ImageButton backButton;
    private ImageView courseLogo;
    private TextView courseTitle, continueLabel, continueItemTitle, progressText;
    private ProgressBar progressBar;
    private Button resumeButton;
    private LinearLayout tabHome, tabVideos, tabDates, tabChat;
    private RecyclerView moduleRecyclerView;
    private ModuleAdapter moduleAdapter;
    private List<Module> moduleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        // Initialize views
        backButton = findViewById(R.id.back_button);
        courseLogo = findViewById(R.id.course_logo);
        courseTitle = findViewById(R.id.course_title);
        continueLabel = findViewById(R.id.continue_label);
        continueItemTitle = findViewById(R.id.continue_item_title);
        progressBar = findViewById(R.id.progress_bar);
        progressText = findViewById(R.id.progress_text);
        resumeButton = findViewById(R.id.resume_button);
        tabHome = findViewById(R.id.tab_home);
        tabVideos = findViewById(R.id.tab_videos);
        tabDates = findViewById(R.id.tab_dates);
        tabChat = findViewById(R.id.tab_chat);
        moduleRecyclerView = findViewById(R.id.module_recycler_view);

        // Log to check if views are null
        Log.d(TAG, "tabHome is " + (tabHome == null ? "null" : "not null"));
        Log.d(TAG, "tabVideos is " + (tabVideos == null ? "null" : "not null"));
        Log.d(TAG, "tabDates is " + (tabDates == null ? "null" : "not null"));
        Log.d(TAG, "tabChat is " + (tabChat == null ? "null" : "not null"));

        // Get course data from intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("course_title");
        int progress = intent.getIntExtra("course_progress", 0);

        // Update UI with course data
        courseTitle.setText(title);
        progressBar.setProgress(progress);
        int totalAssignments = 8; // Example total
        int completedAssignments = (int) (progress / 12.5); // Example calculation: 100% = 8 assignments
        progressText.setText(completedAssignments + " of " + totalAssignments + " assignments complete");
        continueItemTitle.setText("Hands-On Lab: Writing Files with Open"); // Example, can be dynamic

        // Set up RecyclerView for modules
        // Set up RecyclerView for modules
        moduleList = new ArrayList<>();
        moduleList.add(new Module("About this course", false, Arrays.asList(
                new ModuleElement("Course Overview", ModuleElement.ElementType.TEXT),
                new ModuleElement("Syllabus", ModuleElement.ElementType.TEXT),
                new ModuleElement("Resources", ModuleElement.ElementType.TEXT)
        )));
        moduleList.add(new Module("Module 1 - Python Basics", true, Arrays.asList(
                new ModuleElement("Introduction to Python", ModuleElement.ElementType.VIDEO),
                new ModuleElement("Variables and Data Types", ModuleElement.ElementType.TEXT),
                new ModuleElement("Basic Operations", ModuleElement.ElementType.QUIZ)
        )));
        moduleList.add(new Module("Module 2 - Python Data Structures", false, Arrays.asList(
                new ModuleElement("Lists and Tuples", ModuleElement.ElementType.TEXT),
                new ModuleElement("Dictionaries", ModuleElement.ElementType.TEXT),
                new ModuleElement("Sets", ModuleElement.ElementType.QUIZ)
        )));
        moduleList.add(new Module("Module 3 - Python Programming Fundamentals", false, Arrays.asList(
                new ModuleElement("Functions", ModuleElement.ElementType.TEXT),
                new ModuleElement("Loops", ModuleElement.ElementType.VIDEO),
                new ModuleElement("Conditionals", ModuleElement.ElementType.QUIZ)
        )));
        moduleList.add(new Module("Module 4 - Working with Data in Python", false, Arrays.asList(
                new ModuleElement("File I/O", ModuleElement.ElementType.TEXT),
                new ModuleElement("Data Analysis with Pandas", ModuleElement.ElementType.VIDEO),
                new ModuleElement("Visualization with Matplotlib", ModuleElement.ElementType.QUIZ)
        )));

        moduleAdapter = new ModuleAdapter(this, moduleList);
        moduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        moduleRecyclerView.setAdapter(moduleAdapter);

        // Back button
        backButton.setOnClickListener(v -> finish());

        // Resume button
        /*resumeButton.setOnClickListener(v -> {
            Intent resumeIntent = new Intent(CourseActivity.this, QuizElementActivity.class);
            resumeIntent.putExtra("lesson_title", continueItemTitle.getText().toString());
            startActivity(resumeIntent);
        });*/

        // Tab bar click listeners with null checks
        if (tabHome != null) {
            tabHome.setOnClickListener(v -> highlightTab(tabHome));
        } else {
            Log.e(TAG, "tabHome is null, cannot set OnClickListener");
        }

        if (tabVideos != null) {
            tabVideos.setOnClickListener(v -> {
                highlightTab(tabVideos);
                Toast.makeText(this, "Videos tab clicked", Toast.LENGTH_SHORT).show();
            });
        } else {
            Log.e(TAG, "tabVideos is null, cannot set OnClickListener");
        }

        if (tabDates != null) {
            tabDates.setOnClickListener(v -> {
                highlightTab(tabDates);
                Toast.makeText(this, "Dates tab clicked", Toast.LENGTH_SHORT).show();
            });
        } else {
            Log.e(TAG, "tabDates is null, cannot set OnClickListener");
        }

        if (tabChat != null) {
            tabChat.setOnClickListener(v -> {
                highlightTab(tabChat);
                Toast.makeText(this, "Chat tab clicked", Toast.LENGTH_SHORT).show();
            });
        } else {
            Log.e(TAG, "tabChat is null, cannot set OnClickListener");
        }

        // Highlight the default tab (Home) if it's not null
        if (tabHome != null) {
            highlightTab(tabHome);
        } else {
            Log.e(TAG, "Cannot highlight tabHome because it is null");
        }
    }

    private void highlightTab(LinearLayout selectedTab) {
        // Reset all tabs
        if (tabHome != null) resetTab(tabHome);
        if (tabVideos != null) resetTab(tabVideos);
        if (tabDates != null) resetTab(tabDates);
        if (tabChat != null) resetTab(tabChat);

        // Highlight the selected tab
        selectedTab.setBackgroundResource(R.drawable.button_background_circular);
    }

    private void resetTab(LinearLayout tab) {
        tab.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }
}