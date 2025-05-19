package com.example.learnizone.models;

import java.util.List;
import java.util.Map;

public class CourseSection {
    private String id;
    private String title;
    private String description;
    private int order;
    private List<SubSection> subsections;

    public CourseSection() {}

    public CourseSection(String id, String title, String description, int order) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getOrder() {
        return order;
    }

    public List<SubSection> getSubsections() {
        return subsections;
    }

    public void setSubsections(List<SubSection> subsections) {
        this.subsections = subsections;
    }

    public static class SubSection {
        private String id;
        private String title;
        private String type; // "TEXT", "VIDEO", "IMAGE"
        private String content; // For text content
        private String mediaUrl; // For video/image URLs
        private int order;

        public SubSection() {}

        public SubSection(String id, String title, String type, String content, String mediaUrl, int order) {
            this.id = id;
            this.title = title;
            this.type = type;
            this.content = content;
            this.mediaUrl = mediaUrl;
            this.order = order;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getType() {
            return type;
        }

        public String getContent() {
            return content;
        }

        public String getMediaUrl() {
            return mediaUrl;
        }

        public int getOrder() {
            return order;
        }
    }
} 