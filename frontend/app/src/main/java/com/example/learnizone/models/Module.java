package com.example.learnizone.models;

public class Module {
    private String id;
    private String title;
    private String duration;
    private boolean isCompleted;
    private boolean isLocked;

    public Module(String id, String title, String duration) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.isCompleted = false;
        this.isLocked = false;
    }

    public Module(String id, String title, String duration, boolean isCompleted, boolean isLocked) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.isCompleted = isCompleted;
        this.isLocked = isLocked;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDuration() {
        return duration;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public boolean isLocked() {
        return isLocked;
    }
}
