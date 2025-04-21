package com.example.learnizone;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnizone.adapters.CourseCardAdapter;
import com.example.learnizone.adapters.CourseProgressAdapter;

import com.example.learnizone.models.Course;


import java.util.ArrayList;
import java.util.List;

public class LearnFragment extends Fragment {

    private RecyclerView coursesInProgressRecyclerView;
    private RecyclerView recommendedCoursesRecyclerView;
    private RecyclerView popularCoursesRecyclerView;

    private CourseProgressAdapter progressAdapter;
    private CourseCardAdapter recommendedAdapter;
    private CourseCardAdapter popularAdapter;

    private TextView recommendedSeeAll;
    private TextView popularSeeAll;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learn, container, false);

        coursesInProgressRecyclerView = view.findViewById(R.id.courses_in_progress_recyclerview);
        recommendedCoursesRecyclerView = view.findViewById(R.id.recommended_courses_recyclerview);
        popularCoursesRecyclerView = view.findViewById(R.id.popular_courses_recyclerview);

        recommendedSeeAll = view.findViewById(R.id.recommended_see_all);
        popularSeeAll = view.findViewById(R.id.popular_see_all);

        setupRecyclerViews();
        loadCourses();
        setupClickListeners();

        return view;
    }

    private void setupRecyclerViews() {
        // Cours en progrès
        coursesInProgressRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressAdapter = new CourseProgressAdapter(new ArrayList<>(), course -> {
            navigateToCourseDetails(course.getId());
        });
        coursesInProgressRecyclerView.setAdapter(progressAdapter);

        // Cours recommandés
        LinearLayoutManager recommendedLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recommendedCoursesRecyclerView.setLayoutManager(recommendedLayoutManager);
        recommendedAdapter = new CourseCardAdapter(new ArrayList<>(), course -> {
            navigateToCourseDetails(course.getId());
        });
        recommendedCoursesRecyclerView.setAdapter(recommendedAdapter);

        // Cours populaires
        LinearLayoutManager popularLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        popularCoursesRecyclerView.setLayoutManager(popularLayoutManager);
        popularAdapter = new CourseCardAdapter(new ArrayList<>(), course -> {
            navigateToCourseDetails(course.getId());
        });
        popularCoursesRecyclerView.setAdapter(popularAdapter);
    }

    private void loadCourses() {
        // Dans une application réelle, ces données viendraient d'une API
        List<Course> rawCourses = DataProvider.getCoursesInProgress();
        List<CourseProgress> coursesInProgress = new ArrayList<>();

        for (Course course : rawCourses) {
            // Tu choisis ici comment déterminer la progression
            int progress = 50; // par exemple, 50% en dur pour tester
            coursesInProgress.add(new CourseProgress(
                    course.getId(),
                    course.getTitle(),
                    course.getCategory(),
                    course.getImageUrl(),
                    course.getDuration(),
                    course.getRating(),
                    course.getDifficulty(),
                    progress
            ));
        }

        List<Course> recommendedCourses = DataProvider.getRecommendedCourses();
        List<Course> popularCourses = DataProvider.getPopularCourses();

        progressAdapter.updateCourses(coursesInProgress);
        recommendedAdapter.updateCourses(recommendedCourses);
        popularAdapter.updateCourses(popularCourses);
    }

    private void setupClickListeners() {
        recommendedSeeAll.setOnClickListener(v -> {
            navigateToCoursesScreen();
        });

        popularSeeAll.setOnClickListener(v -> {
            navigateToCoursesScreen();
        });
    }

    private void navigateToCourseDetails(String courseId) {
        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
        intent.putExtra(CourseDetailActivity.EXTRA_COURSE_ID, courseId);
        startActivity(intent);
    }

    private void navigateToCoursesScreen() {
        if (getActivity() != null) {
            // Naviguer vers l'onglet des cours
            ((MainActivity2) getActivity()).navigateToCourses();
        }
    }
}
