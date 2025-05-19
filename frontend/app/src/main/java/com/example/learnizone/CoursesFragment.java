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

        return courses;
    }
}
