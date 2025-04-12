package com.example.learnflow;



public class ModuleElement {
    public enum ElementType {
        TEXT,
        QUIZ,
        VIDEO
    }

    private String title;
    private ElementType type;

    public ModuleElement(String title, ElementType type) {
        this.title = title;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public ElementType getType() {
        return type;
    }
}