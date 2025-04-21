package com.example.learnizone;

public class Domain {
    String name;
    int image;
    boolean isSelected;  // Add this field

    public Domain(String name, int image) {
        this.name = name;
        this.image = image;
        this.isSelected = false;  // Default to unselected
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}