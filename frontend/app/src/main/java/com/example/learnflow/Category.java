package com.example.learnflow;

import java.util.List;
public class Category {
    String title;
    List<Domain> domains;
    boolean isExpanded;

    public Category(String title, List<Domain> domains) {
        this.title = title;
        this.domains = domains;
        this.isExpanded = false;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getTitle() {
        return title;
    }

    public List<Domain> getDomains() {
        return domains;
    }
}
