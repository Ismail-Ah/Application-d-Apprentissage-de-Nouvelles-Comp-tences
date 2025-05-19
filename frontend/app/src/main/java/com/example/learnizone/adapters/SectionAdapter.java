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

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionViewHolder> {
    private List<CourseSection> sections;
    private OnSectionClickListener listener;

    public interface OnSectionClickListener {
        void onSectionClick(CourseSection section);
    }

    public SectionAdapter(List<CourseSection> sections, OnSectionClickListener listener) {
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
        holder.bind(section);
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    public void updateSections(List<CourseSection> newSections) {
        this.sections = newSections;
        notifyDataSetChanged();
    }

    class SectionViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView descriptionTextView;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.sectionTitle);
            descriptionTextView = itemView.findViewById(R.id.sectionDescription);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onSectionClick(sections.get(position));
                }
            });
        }

        public void bind(CourseSection section) {
            titleTextView.setText(section.getTitle());
            descriptionTextView.setText(section.getDescription());
        }
    }
} 