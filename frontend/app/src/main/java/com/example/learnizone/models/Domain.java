package com.example.learnizone.models;

import com.google.firebase.firestore.DocumentId;

public class Domain {
    @DocumentId
    private String id;
    private String name;
    private String description;
    private String iconUrl;

    // Default constructor for Firestore
    public Domain() {}

    public Domain(String name, String description, String iconUrl) {
        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
} 