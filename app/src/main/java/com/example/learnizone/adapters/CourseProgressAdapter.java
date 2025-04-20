package com.example.learnizone.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnizone.R;
import com.example.learnizone.CourseProgress;

import java.util.List;

public class CourseProgressAdapter extends RecyclerView.Adapter<CourseProgressAdapter.ViewHolder> {

    public interface OnCourseClickListener {
        void onCourseClick(CourseProgress course);
    }

    private List<CourseProgress> courseList;
    private OnCourseClickListener listener;

    public CourseProgressAdapter(List<CourseProgress> courseList, OnCourseClickListener listener) {
        this.courseList = courseList;
        this.listener = listener;
    }

    public void updateCourses(List<CourseProgress> newCourses) {
        this.courseList = newCourses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_progress, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CourseProgress course = courseList.get(position);
        holder.title.setText(course.getTitle());
        holder.progressBar.setProgress(course.getProgressPercentage());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCourseClick(course);
        });
    }

    @Override
    public int getItemCount() {
        return courseList != null ? courseList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ProgressBar progressBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.course_title);
            progressBar = itemView.findViewById(R.id.course_progress_bar);
        }
    }
}
