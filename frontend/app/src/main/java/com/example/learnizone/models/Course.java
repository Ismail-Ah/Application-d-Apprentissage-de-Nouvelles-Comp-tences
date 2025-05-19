package com.example.learnizone.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Course {
    @DocumentId
    private String id;
    private String title;
    private String category;
    private String imageUrl;
    private String duration;
    private double rating;
    private String difficulty;
    private String description;
    private int progress;
    private List<String> prerequisites;
    private List<String> learningObjectives;
    private List<Map<String, Object>> modules;
    private boolean isDownloaded;
    private @ServerTimestamp Date lastAccessed;
    private List<String> tags;
    private String instructor;
    private int totalStudents;
    private List<String> supportedFormats; // ["VIDEO", "AUDIO", "TEXT", "QUIZ"]
    private Map<String, Object> quizData;
    private List<String> completedSections;
    private List<Map<String, Object>> userNotes;

    // Default constructor for Firestore
    public Course() {}

    public Course(String id, String title, String category, String imageUrl, String duration, 
                 double rating, String difficulty, String description, List<String> prerequisites,
                 List<String> learningObjectives, List<Map<String, Object>> modules,
                 String instructor) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.imageUrl = imageUrl;
        this.duration = duration;
        this.rating = rating;
        this.difficulty = difficulty;
        this.description = description;
        this.prerequisites = prerequisites;
        this.learningObjectives = learningObjectives;
        this.modules = modules;
        this.instructor = instructor;
        this.progress = 0;
        this.isDownloaded = false;
        this.totalStudents = 0;
        this.supportedFormats = List.of("VIDEO", "AUDIO", "TEXT", "QUIZ");
        this.completedSections = List.of();
        this.userNotes = List.of();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id=id;
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

    public List<String> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<String> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public List<String> getLearningObjectives() {
        return learningObjectives;
    }

    public void setLearningObjectives(List<String> learningObjectives) {
        this.learningObjectives = learningObjectives;
    }

    public List<Map<String, Object>> getModules() {
        return modules;
    }

    public void setModules(List<Map<String, Object>> modules) {
        this.modules = modules;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public Date getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(Date lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public List<String> getSupportedFormats() {
        return supportedFormats;
    }

    public void setSupportedFormats(List<String> supportedFormats) {
        this.supportedFormats = supportedFormats;
    }

    public Map<String, Object> getQuizData() {
        return quizData;
    }

    public void setQuizData(Map<String, Object> quizData) {
        this.quizData = quizData;
    }

    public List<String> getCompletedSections() {
        return completedSections;
    }

    public void setCompletedSections(List<String> completedSections) {
        this.completedSections = completedSections;
    }

    public List<Map<String, Object>> getUserNotes() {
        return userNotes;
    }

    public void setUserNotes(List<Map<String, Object>> userNotes) {
        this.userNotes = userNotes;
    }
}
