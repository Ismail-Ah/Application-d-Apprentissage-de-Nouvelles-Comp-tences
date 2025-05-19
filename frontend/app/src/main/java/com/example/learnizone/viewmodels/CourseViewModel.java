package com.example.learnizone.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.learnizone.models.Course;
import com.example.learnizone.repositories.CourseRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseViewModel extends ViewModel {
    private final CourseRepository courseRepository;
    private final MutableLiveData<List<Course>> courses = new MutableLiveData<>();
    private final MutableLiveData<Course> selectedCourse = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Exception> error = new MutableLiveData<>();

    public CourseViewModel() {
        courseRepository = new CourseRepository();
    }

    public LiveData<List<Course>> getCourses() {
        return courses;
    }

    public LiveData<Course> getSelectedCourse() {
        return selectedCourse;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Exception> getError() {
        return error;
    }

    public void getAllCourses() {
        courseRepository.getAllCourses(new CourseRepository.OnCoursesLoadedListener() {
            @Override
            public void onCoursesLoaded(List<Course> courseList) {
                courses.postValue(courseList);
            }

            @Override
            public void onError(Exception e) {
                error.postValue(e);
            }
        });
    }

    public void getCoursesByTeacher(String teacherId) {
        courseRepository.getCoursesByTeacher(teacherId, new CourseRepository.OnCoursesLoadedListener() {
            @Override
            public void onCoursesLoaded(List<Course> courseList) {
                courses.postValue(courseList);
            }

            @Override
            public void onError(Exception e) {
                error.postValue(e);
            }
        });
    }

    public void loadCourseById(String courseId) {
        isLoading.setValue(true);
        courseRepository.getCourse(courseId)
                .addOnSuccessListener(documentSnapshot -> {
                    Course course = documentSnapshot.toObject(Course.class);
                    selectedCourse.setValue(course);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue(e);
                    isLoading.setValue(false);
                });
    }

    public void searchCourses(String searchTerm) {
        isLoading.setValue(true);
        courseRepository.searchCourses(searchTerm)
                .addOnSuccessListener(querySnapshot -> {
                    List<Course> courseList = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Course course = document.toObject(Course.class);
                        if (course != null) {
                            courseList.add(course);
                        }
                    }
                    courses.setValue(courseList);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue(e);
                    isLoading.setValue(false);
                });
    }

    public void updateCourseProgress(String courseId, int progress) {
        courseRepository.updateCourseProgress(courseId, progress)
                .addOnFailureListener(e -> error.setValue(e));
    }

    public void addUserNote(String courseId, String noteText) {
        Map<String, Object> note = new HashMap<>();
        note.put("text", noteText);
        note.put("timestamp", System.currentTimeMillis());

        courseRepository.addUserNote(courseId, note)
                .addOnFailureListener(e -> error.setValue(e));
    }

    public void updateDownloadStatus(String courseId, boolean isDownloaded) {
        courseRepository.updateDownloadStatus(courseId, isDownloaded)
                .addOnFailureListener(e -> error.setValue(e));
    }

    public void loadCoursesByCategory(String category) {
        isLoading.setValue(true);
        courseRepository.getCoursesByCategory(category)
                .addOnSuccessListener(querySnapshot -> {
                    List<Course> courseList = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Course course = document.toObject(Course.class);
                        if (course != null) {
                            courseList.add(course);
                        }
                    }
                    courses.setValue(courseList);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue(e);
                    isLoading.setValue(false);
                });
    }

    public void loadTopRatedCourses(int limit) {
        isLoading.setValue(true);
        courseRepository.getTopRatedCourses(limit)
                .addOnSuccessListener(querySnapshot -> {
                    List<Course> courseList = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Course course = document.toObject(Course.class);
                        if (course != null) {
                            courseList.add(course);
                        }
                    }
                    courses.setValue(courseList);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue(e);
                    isLoading.setValue(false);
                });
    }
} 