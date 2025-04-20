package com.example.learnizone.models;

public class Category {
    private String id;
    private String name;
    private int iconResId;

    public Category(String id, String name, int iconResId) {
        this.id = id;
        this.name = name;
        this.iconResId = iconResId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }
}
