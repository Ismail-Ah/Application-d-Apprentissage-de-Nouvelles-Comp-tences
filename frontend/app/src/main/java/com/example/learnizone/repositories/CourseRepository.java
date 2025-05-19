package com.example.learnizone.repositories;

import com.example.learnizone.models.Course;
import com.example.learnizone.models.CourseSection;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseRepository {
    private final FirebaseFirestore db;
    private final CollectionReference coursesCollection;
    private static final String COURSES_COLLECTION = "courses";

    public CourseRepository() {
        db = FirebaseFirestore.getInstance();
        coursesCollection = db.collection("courses");
    }

    public interface OnCoursesLoadedListener {
        void onCoursesLoaded(List<Course> courses);
        void onError(Exception e);
    }

    public void getCoursesByTeacher(String teacherId, OnCoursesLoadedListener listener) {
        db.collection(COURSES_COLLECTION)
                .whereEqualTo("instructorId", teacherId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Course> courses = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Course course = document.toObject(Course.class);
                        course.setId(document.getId());
                        courses.add(course);
                    }
                    listener.onCoursesLoaded(courses);
                })
                .addOnFailureListener(listener::onError);
    }

    public void addCourse(Course course, OnCourseAddedListener listener) {
        db.collection(COURSES_COLLECTION)
                .add(course)
                .addOnSuccessListener(documentReference -> {
                    course.setId(documentReference.getId());
                    listener.onCourseAdded(course);
                })
                .addOnFailureListener(listener::onError);
    }

    public interface OnCourseAddedListener {
        void onCourseAdded(Course course);
        void onError(Exception e);
    }

    // Create a new course
    public Task<Void> createCourse(Course course) {
        return db.collection(COURSES_COLLECTION)
                .document(course.getId())
                .set(course);
    }

    // Get a course by ID
    public Task<DocumentSnapshot> getCourse(String courseId) {
        return coursesCollection.document(courseId).get();
    }

    // Get all courses
    public void getAllCourses(OnCoursesLoadedListener listener) {
        db.collection(COURSES_COLLECTION)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Course> courses = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Course course = document.toObject(Course.class);
                        course.setId(document.getId());
                        courses.add(course);
                    }
                    listener.onCoursesLoaded(courses);
                })
                .addOnFailureListener(listener::onError);
    }

    // Get courses by category
    public Task<QuerySnapshot> getCoursesByCategory(String category) {
        return coursesCollection.whereEqualTo("category", category).get();
    }

    // Get courses by difficulty
    public Task<QuerySnapshot> getCoursesByDifficulty(String difficulty) {
        return coursesCollection.whereEqualTo("difficulty", difficulty).get();
    }

    // Search courses by title
    public Task<QuerySnapshot> searchCourses(String searchTerm) {
        return coursesCollection.whereGreaterThanOrEqualTo("title", searchTerm)
                .whereLessThanOrEqualTo("title", searchTerm + '\uf8ff')
                .get();
    }

    // Update course progress
    public Task<Void> updateCourseProgress(String courseId, int progress) {
        return coursesCollection.document(courseId)
                .update("progress", progress);
    }

    // Update completed sections
    public Task<Void> updateCompletedSections(String courseId, List<String> completedSections) {
        return coursesCollection.document(courseId)
                .update("completedSections", completedSections);
    }

    // Add user note to course
    public Task<Void> addUserNote(String courseId, Map<String, Object> note) {
        return coursesCollection.document(courseId)
                .update("userNotes", com.google.firebase.firestore.FieldValue.arrayUnion(note));
    }

    // Update course download status
    public Task<Void> updateDownloadStatus(String courseId, boolean isDownloaded) {
        return coursesCollection.document(courseId)
                .update("isDownloaded", isDownloaded);
    }

    // Get courses by tags
    public Task<QuerySnapshot> getCoursesByTags(List<String> tags) {
        return coursesCollection.whereArrayContainsAny("tags", tags).get();
    }

    // Get top rated courses
    public Task<QuerySnapshot> getTopRatedCourses(int limit) {
        return coursesCollection.orderBy("rating", Query.Direction.DESCENDING)
                .limit(limit)
                .get();
    }

    // Get most popular courses
    public Task<QuerySnapshot> getMostPopularCourses(int limit) {
        return coursesCollection.orderBy("totalStudents", Query.Direction.DESCENDING)
                .limit(limit)
                .get();
    }

    // Update quiz data
    public Task<Void> updateQuizData(String courseId, Map<String, Object> quizData) {
        return coursesCollection.document(courseId)
                .update("quizData", quizData);
    }

    public Task<Void> addSectionToCourse(String courseId, CourseSection section) {
        DocumentReference courseRef = db.collection("courses").document(courseId);
        
        Map<String, Object> sectionData = new HashMap<>();
        sectionData.put("id", section.getId());
        sectionData.put("title", section.getTitle());
        sectionData.put("description", section.getDescription());
        sectionData.put("order", section.getOrder());
        sectionData.put("subsections", new ArrayList<>());

        return courseRef.update("modules", sectionData);
    }

    public Task<Void> addSubsectionToSection(String courseId, String sectionId, Map<String, Object> subsection) {
        DocumentReference courseRef = db.collection("courses").document(courseId);
        
        // Get the current course
        return courseRef.get().continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }

            DocumentSnapshot document = task.getResult();
            Course course = document.toObject(Course.class);
            if (course == null) {
                throw new Exception("Course not found");
            }

            // Find the section and add the subsection
            List<Map<String, Object>> modules = course.getModules();
            for (Map<String, Object> module : modules) {
                if (sectionId.equals(module.get("id"))) {
                    List<Map<String, Object>> subsections = (List<Map<String, Object>>) module.get("subsections");
                    if (subsections == null) {
                        subsections = new ArrayList<>();
                    }
                    subsections.add(subsection);
                    module.put("subsections", subsections);
                    break;
                }
            }

            // Update the course
            return courseRef.update("modules", modules);
        });
    }

    public Task<Void> updateCourse(Course course) {
        // Check if the current user is the instructor
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (!course.isInstructor(currentUserId)) {
            throw new SecurityException("Only the course instructor can update the course");
        }

        return db.collection(COURSES_COLLECTION)
                .document(course.getId())
                .set(course);
    }

    public Task<Void> deleteCourse(String courseId) {
        // First get the course to check ownership
        return db.collection(COURSES_COLLECTION)
                .document(courseId)
                .get()
                .continueWithTask(task -> {
                    Course course = task.getResult().toObject(Course.class);
                    if (course == null) {
                        throw new Exception("Course not found");
                    }

                    // Check if the current user is the instructor
                    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if (!course.isInstructor(currentUserId)) {
                        throw new SecurityException("Only the course instructor can delete the course");
                    }

                    // If ownership is verified, delete the course
                    return db.collection(COURSES_COLLECTION)
                            .document(courseId)
                            .delete();
                });
    }

    public Task<Course> getCourseById(String courseId) {
        return db.collection(COURSES_COLLECTION)
                .document(courseId)
                .get()
                .continueWith(task -> {
                    Course course = task.getResult().toObject(Course.class);
                    if (course == null) {
                        throw new Exception("Course not found");
                    }
                    course.setId(task.getResult().getId());
                    return course;
                });
    }
} 