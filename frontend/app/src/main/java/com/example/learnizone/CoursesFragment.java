package com.example.learnizone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnizone.adapters.CategoryAdapter;
import com.example.learnizone.adapters.CourseCardAdapter;
import com.example.learnizone.models.Category;
import com.example.learnizone.models.Course;
import com.example.learnizone.CourseDetailActivity;  // ajuste le chemin si nécessaire


import java.util.ArrayList;
import java.util.List;

public class CoursesFragment extends Fragment {

    private RecyclerView categoriesRecyclerView;
    private RecyclerView coursesRecyclerView;
    private SearchView searchView;

    private CategoryAdapter categoryAdapter;
    private CourseCardAdapter courseAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courses, container, false);

        categoriesRecyclerView = view.findViewById(R.id.categories_recyclerview);
        coursesRecyclerView = view.findViewById(R.id.courses_recyclerview);
        searchView = view.findViewById(R.id.search_view);

        setupRecyclerViews();
        setupSearchView();
        loadData();

        return view;
    }

    private void setupRecyclerViews() {
        // Configuration du recyclerview des catégories
        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoriesRecyclerView.setLayoutManager(categoryLayoutManager);
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), category -> {
            // Filtrer les cours par catégorie
            filterCoursesByCategory(category);
        });
        categoriesRecyclerView.setAdapter(categoryAdapter);

        // Configuration du recyclerview des cours
        GridLayoutManager courseLayoutManager = new GridLayoutManager(getContext(), 2);
        coursesRecyclerView.setLayoutManager(courseLayoutManager);
        courseAdapter = new CourseCardAdapter(new ArrayList<>(), course -> {
            // Naviguer vers les détails du cours
            navigateToCourseDetails(course.getId());
        });
        coursesRecyclerView.setAdapter(courseAdapter);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterCoursesByQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    loadAllCourses();
                }
                return false;
            }
        });
    }

    private void loadData() {
        // Charger les catégories
        List<Category> categories = getCategoriesData();
        categoryAdapter.updateCategories(categories);

        // Charger tous les cours
        loadAllCourses();
    }

    private void loadAllCourses() {
        // Dans une vraie application, ces données viendraient d'une API
        List<Course> allCourses = getAllCoursesData();
        courseAdapter.updateCourses(allCourses);
    }

    private void filterCoursesByCategory(Category category) {
        // Dans une vraie application, cela filtrerait les cours depuis une source de données
        // Pour cette démo, nous filtrons la liste complète
        List<Course> allCourses = getAllCoursesData();
        List<Course> filteredCourses = new ArrayList<>();

        for (Course course : allCourses) {
            if (course.getCategory().equals(category.getName())) {
                filteredCourses.add(course);
            }
        }

        courseAdapter.updateCourses(filteredCourses);
    }

    private void filterCoursesByQuery(String query) {
        // Dans une vraie application, cela filtrerait les cours depuis une source de données
        // Pour cette démo, nous filtrons la liste complète
        List<Course> allCourses = getAllCoursesData();
        List<Course> filteredCourses = new ArrayList<>();

        for (Course course : allCourses) {
            if (course.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredCourses.add(course);
            }
        }

        courseAdapter.updateCourses(filteredCourses);
    }

    private void navigateToCourseDetails(String courseId) {
        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
        intent.putExtra(CourseDetailActivity.EXTRA_COURSE_ID, courseId);
        startActivity(intent);
    }

    // Méthodes pour obtenir des données d'exemple
    private List<Category> getCategoriesData() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("1", "Programmation", R.drawable.ic_programming));
        categories.add(new Category("2", "Design", R.drawable.ic_design));
        categories.add(new Category("3", "Marketing", R.drawable.ic_marketing));
        categories.add(new Category("4", "Business", R.drawable.ic_business));
        categories.add(new Category("5", "Photographie", R.drawable.ic_photography));
        categories.add(new Category("6", "Musique", R.drawable.ic_music));
        return categories;
    }

    private List<Course> getAllCoursesData() {
        List<Course> courses = new ArrayList<>();

        // Ces données seraient normalement chargées d'une base de données ou d'une API
        courses.add(new Course(
                "1",
                "Les bases de la programmation JavaScript",
                "Programmation",
                "https://images.unsplash.com/photo-1488590528505-98d2b5aba04b",
                "10h 30min",
                4.7,
                "Débutant"
        ));

        courses.add(new Course(
                "2",
                "Photographie pour débutants: Maîtriser les fondamentaux",
                "Photographie",
                "https://images.unsplash.com/photo-1500673922987-e212871fec22",
                "8h 15min",
                4.5,
                "Débutant"
        ));

        courses.add(new Course(
                "3",
                "Gestion du temps et productivité",
                "Business",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b",
                "6h 45min",
                4.8,
                "Intermédiaire"
        ));

        courses.add(new Course(
                "4",
                "Développement web avec React",
                "Programmation",
                "https://images.unsplash.com/photo-1461749280684-dccba630e2f6",
                "12h 20min",
                4.9,
                "Avancé"
        ));

        courses.add(new Course(
                "5",
                "Apprendre à apprendre: Techniques d'apprentissage efficaces",
                "Éducation",
                "https://images.unsplash.com/photo-1581091226825-a6a2a5aee158",
                "5h 10min",
                4.6,
                "Débutant"
        ));

        courses.add(new Course(
                "6",
                "Les fondements du design d'interface utilisateur",
                "Design",
                "https://images.unsplash.com/photo-1498050108023-c5249f4df085",
                "9h 45min",
                4.4,
                "Intermédiaire"
        ));

        return courses;
    }
}
