package com.example.learnizone;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnizone.Course;
import com.example.learnizone.R;

import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private List<Course> courseList;
    private List<Course> courseListFull; // For filtering
    private Context context;

    public CourseAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
        this.courseListFull = new ArrayList<>(courseList); // Copy for filtering
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_card, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.categoryLabel.setText(course.getCategory());
        holder.courseTitle.setText(course.getTitle());
        holder.durationText.setText(course.getDuration());
        holder.ratingText.setText(String.valueOf(course.getRating()));
        holder.progressBar.setProgress(course.getProgress());

        // Handle "Continue" button click
        holder.continueButton.setOnClickListener(v -> {
            // Start a new activity to view course details
            Intent intent = new Intent(context, CourseActivity.class);
            intent.putExtra("course_title", course.getTitle());
            intent.putExtra("course_category", course.getCategory());
            intent.putExtra("course_duration", course.getDuration());
            intent.putExtra("course_rating", course.getRating());
            intent.putExtra("course_progress", course.getProgress());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public void filter(String text) {
        courseList.clear();
        if (text.isEmpty()) {
            courseList.addAll(courseListFull);
        } else {
            text = text.toLowerCase();
            for (Course course : courseListFull) {
                if (course.getTitle().toLowerCase().contains(text) || course.getCategory().toLowerCase().contains(text)) {
                    courseList.add(course);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void sortByProgress() {
        courseList.sort((c1, c2) -> Integer.compare(c2.getProgress(), c1.getProgress())); // Descending order
        notifyDataSetChanged();
    }

    public void filterByProgress(int minProgress) {
        courseList.clear();
        for (Course course : courseListFull) {
            if (course.getProgress() >= minProgress) {
                courseList.add(course);
            }
        }
        notifyDataSetChanged();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView categoryLabel, courseTitle, durationText, ratingText;
        ProgressBar progressBar;
        Button continueButton;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryLabel = itemView.findViewById(R.id.category_label);
            courseTitle = itemView.findViewById(R.id.course_title);
            durationText = itemView.findViewById(R.id.duration_text);
            ratingText = itemView.findViewById(R.id.rating_text);
            progressBar = itemView.findViewById(R.id.progress_bar);
            continueButton = itemView.findViewById(R.id.continue_button);
        }
    }
}