package com.example.learnizone;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.learnizone.adapters.LearningPointAdapter;
import com.example.learnizone.adapters.ModuleAdapter;
import com.example.learnizone.models.Course;
import com.example.learnizone.models.Module;

import java.util.ArrayList;
import java.util.List;

public class CourseDetailActivity extends AppCompatActivity {

    public static final String EXTRA_COURSE_ID = "com.example.learnizone.EXTRA_COURSE_ID";

    private ImageView courseImage;
    private TextView courseCategory;
    private TextView courseTitle;
    private TextView courseDifficulty;
    private TextView courseDuration;
    private TextView courseRating;
    private TextView courseDescription;
    private RecyclerView courseLearningPoints;
    private RecyclerView courseModules;
    private Button actionButton;

    private String courseId;
    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        courseId = getIntent().getStringExtra(EXTRA_COURSE_ID);
        if (courseId == null) {
            Toast.makeText(this, "Erreur : Cours introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        loadCourseData();
        setupRecyclerViews();
        setupActionButton();
    }

    private void initViews() {
        courseImage = findViewById(R.id.course_image);
        courseCategory = findViewById(R.id.course_category);
        courseTitle = findViewById(R.id.course_title);
        courseDifficulty = findViewById(R.id.course_difficulty);
        courseDuration = findViewById(R.id.course_duration);
        courseRating = findViewById(R.id.course_rating);
        courseDescription = findViewById(R.id.course_description);
        courseLearningPoints = findViewById(R.id.course_learning_points);
        courseModules = findViewById(R.id.course_modules);
        actionButton = findViewById(R.id.action_button);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }

    private void loadCourseData() {
        // Dans une vraie application, ces données viendraient d'une API
        course = DataProvider.getCourseById(courseId);

        if (course == null) {
            Toast.makeText(this, "Erreur : Cours introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        courseTitle.setText(course.getTitle());
        courseCategory.setText(course.getCategory());
        courseDifficulty.setText(course.getDifficulty());
        courseDuration.setText(course.getDuration());
        courseRating.setText(String.format("%.1f", course.getRating()));
        courseDescription.setText(course.getDescription());

        // Charger l'image avec Glide
        Glide.with(this)
                .load(course.getImageUrl())
                .centerCrop()
                .into(courseImage);
    }

    private void setupRecyclerViews() {
        // Configuration du recyclerview des points d'apprentissage
        LinearLayoutManager learningPointsLayoutManager = new LinearLayoutManager(this);
        courseLearningPoints.setLayoutManager(learningPointsLayoutManager);
        LearningPointAdapter learningPointAdapter = new LearningPointAdapter(getLearningPoints());
        courseLearningPoints.setAdapter(learningPointAdapter);

        // Configuration du recyclerview des modules
        LinearLayoutManager modulesLayoutManager = new LinearLayoutManager(this);
        courseModules.setLayoutManager(modulesLayoutManager);
        ModuleAdapter moduleAdapter = new ModuleAdapter(getModules());
        courseModules.setAdapter(moduleAdapter);
    }

    private void setupActionButton() {
        // Dans une vraie application, vérifier si l'utilisateur est déjà inscrit
        boolean isEnrolled = false; // Exemple, à remplacer par la logique réelle

        if (isEnrolled) {
            actionButton.setText(R.string.continue_course);
            actionButton.setOnClickListener(v -> {
                // Naviguer vers le contenu du cours
                Toast.makeText(this, "Continuer le cours", Toast.LENGTH_SHORT).show();
            });
        } else {
            actionButton.setText(R.string.enroll);
            actionButton.setOnClickListener(v -> {
                // Inscrire l'utilisateur au cours
                Toast.makeText(this, "Inscription au cours", Toast.LENGTH_SHORT).show();

                // Changer le bouton après l'inscription
                actionButton.setText(R.string.continue_course);
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Méthodes pour obtenir des données d'exemple
    private List<String> getLearningPoints() {
        List<String> learningPoints = new ArrayList<>();
        learningPoints.add("Comprendre les concepts fondamentaux de JavaScript");
        learningPoints.add("Manipuler le DOM avec JavaScript");
        learningPoints.add("Utiliser les fonctions et les objets en JavaScript");
        learningPoints.add("Gérer les événements dans une page web");
        learningPoints.add("Créer des applications interactives");
        return learningPoints;
    }

    private List<Module> getModules() {
        List<Module> modules = new ArrayList<>();
        modules.add(new Module("1", "Introduction à JavaScript", "2 leçons • 45 min"));
        modules.add(new Module("2", "Variables et types de données", "4 leçons • 1h 15min"));
        modules.add(new Module("3", "Structures de contrôle", "3 leçons • 1h"));
        modules.add(new Module("4", "Fonctions", "5 leçons • 1h 30min"));
        modules.add(new Module("5", "Objets et tableaux", "4 leçons • 1h 45min"));
        modules.add(new Module("6", "Le DOM", "3 leçons • 1h 15min"));
        modules.add(new Module("7", "Événements", "3 leçons • 1h"));
        modules.add(new Module("8", "Projet final", "2 leçons • 2h"));
        return modules;
    }
}
