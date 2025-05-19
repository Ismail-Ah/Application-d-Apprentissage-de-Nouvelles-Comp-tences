package com.example.learnizone.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnizone.R;
import com.example.learnizone.models.CourseSection;

import java.util.List;

public class CourseSectionAdapter extends RecyclerView.Adapter<CourseSectionAdapter.SectionViewHolder> {
    private List<CourseSection> sections;
    private final OnSectionClickListener listener;

    public interface OnSectionClickListener {
        void onSectionClick(CourseSection section);
    }

    public CourseSectionAdapter(List<CourseSection> sections, OnSectionClickListener listener) {
        this.sections = sections;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_section, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        CourseSection section = sections.get(position);
        holder.bind(section, listener);
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    public void updateSections(List<CourseSection> newSections) {
        this.sections = newSections;
        notifyDataSetChanged();
    }

    static class SectionViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView descriptionTextView;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.sectionTitle);
            descriptionTextView = itemView.findViewById(R.id.sectionDescription);
        }

        public void bind(CourseSection section, OnSectionClickListener listener) {
            titleTextView.setText(section.getTitle());
            descriptionTextView.setText(section.getDescription());
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSectionClick(section);
                }
            });
        }
    }
} 