package com.example.learnizone;

public class CourseProgress {
    private String id;
    private String title;
    private String category;
    private String imageUrl;
    private String duration;
    private double rating;
    private String difficulty;
    private int progressPercentage;

    public CourseProgress(String id, String title, String category, String imageUrl, String duration, double rating, String difficulty, int progressPercentage) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.imageUrl = imageUrl;
        this.duration = duration;
        this.rating = rating;
        this.difficulty = difficulty;
        this.progressPercentage = progressPercentage;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
    public String getDuration() { return duration; }
    public double getRating() { return rating; }
    public String getDifficulty() { return difficulty; }
    public int getProgressPercentage() { return progressPercentage; }
}
