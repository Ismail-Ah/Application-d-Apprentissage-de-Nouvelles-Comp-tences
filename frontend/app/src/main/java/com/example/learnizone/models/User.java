package com.example.learnizone.models;

import com.google.firebase.firestore.DocumentId;

public class User {
    @DocumentId
    private String id;
    private String email;
    private String name;
    private String role; // "STUDENT" or "TEACHER"
    private String profileImageUrl;

    public User() {}

    public User(String id, String email, String name, String role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
} 