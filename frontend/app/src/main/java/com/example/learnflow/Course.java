package com.example.learnflow;


public class Course {
    private String title;
    private String category;
    private String duration;
    private float rating;
    private int progress;

    public Course(String title, String category, String duration, float rating, int progress) {
        this.title = title;
        this.category = category;
        this.duration = duration;
        this.rating = rating;
        this.progress = progress;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getDuration() {
        return duration;
    }

    public float getRating() {
        return rating;
    }

    public int getProgress() {
        return progress;
    }
}