package com.example.learnflow;

import java.util.List;

public class Module {
    private String title;
    private boolean isCompleted;
    private List<ModuleElement> elements;

    public Module(String title, boolean isCompleted, List<ModuleElement> elements) {
        this.title = title;
        this.isCompleted = isCompleted;
        this.elements = elements;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public List<ModuleElement> getElements() {
        return elements;
    }
}