package com.example.learnizone.models;

public class Course {
    private String id;
    private String title;
    private String category;
    private String imageUrl;
    private String duration;
    private double rating;
    private String difficulty;
    private String description;
    private int progress;

    public Course(String id, String title, String category, String imageUrl, String duration, double rating, String difficulty) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.imageUrl = imageUrl;
        this.duration = duration;
        this.rating = rating;
        this.difficulty = difficulty;
        this.progress = 0;
    }

    public Course(String id, String title, String category, String imageUrl, String duration, double rating, String difficulty, int progress) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.imageUrl = imageUrl;
        this.duration = duration;
        this.rating = rating;
        this.difficulty = difficulty;
        this.progress = progress;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDuration() {
        return duration;
    }

    public double getRating() {
        return rating;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getProgress() {
        return progress;
    }

    public String getDescription() {
        // Dans une véritable application, cette description serait stockée avec l'objet
        return "Apprenez les fondamentaux de JavaScript, le langage de programmation le plus populaire pour le développement web. Ce cours vous donnera une base solide pour créer des applications web interactives.";
    }
}
