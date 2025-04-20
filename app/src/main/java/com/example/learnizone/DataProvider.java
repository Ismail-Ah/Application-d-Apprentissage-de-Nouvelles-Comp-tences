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

        courses.add(new Course(
                "1",
                "Les bases de la programmation JavaScript",
                "Programmation",
                "https://images.unsplash.com/photo-1488590528505-98d2b5aba04b",
                "10h 30min",
                4.7,
                "Débutant",
                60
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
                "Intermédiaire",
                25
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

    /**
     * Récupère la liste des cours en cours
     */
    public static List<Course> getCoursesInProgress() {
        List<Course> courses = new ArrayList<>();

        courses.add(new Course(
                "1",
                "Les bases de la programmation JavaScript",
                "Programmation",
                "https://images.unsplash.com/photo-1488590528505-98d2b5aba04b",
                "10h 30min",
                4.7,
                "Débutant",
                60
        ));

        courses.add(new Course(
                "3",
                "Gestion du temps et productivité",
                "Business",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b",
                "6h 45min",
                4.8,
                "Intermédiaire",
                25
        ));

        return courses;
    }

    /**
     * Récupère la liste des cours recommandés
     */
    public static List<Course> getRecommendedCourses() {
        List<Course> courses = new ArrayList<>();

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
                "4",
                "Développement web avec React",
                "Programmation",
                "https://images.unsplash.com/photo-1461749280684-dccba630e2f6",
                "12h 20min",
                4.9,
                "Avancé"
        ));

        return courses;
    }

    /**
     * Récupère la liste des cours populaires
     */
    public static List<Course> getPopularCourses() {
        List<Course> courses = new ArrayList<>();

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
