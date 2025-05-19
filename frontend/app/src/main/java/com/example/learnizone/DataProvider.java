package com.example.learnizone;

import com.example.learnizone.models.Course;

import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe simule une source de données pour l'application.
 * Dans une application réelle, ces données viendraient d'une API ou d'une base de données locale.
 */
public class DataProvider {

    /**
     * Récupère un cours par son ID
     */
    public static Course getCourseById(String courseId) {
        List<Course> allCourses = getAllCourses();
        for (Course course : allCourses) {
            if (course.getId().equals(courseId)) {
                return course;
            }
        }
        return null;
    }

    /**
     * Récupère la liste de tous les cours
     */
    public static List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();



        return courses;
    }

    /**
     * Récupère la liste des cours en cours
     */
    public static List<Course> getCoursesInProgress() {
        List<Course> courses = new ArrayList<>();


        return courses;
    }

    /**
     * Récupère la liste des cours recommandés
     */
    public static List<Course> getRecommendedCourses() {
        List<Course> courses = new ArrayList<>();


        return courses;
    }

    /**
     * Récupère la liste des cours populaires
     */
    public static List<Course> getPopularCourses() {
        List<Course> courses = new ArrayList<>();



        return courses;
    }
}
