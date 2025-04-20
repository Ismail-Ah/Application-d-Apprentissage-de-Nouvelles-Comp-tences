package com.example.learnizone.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.learnizone.models.Course;
import com.example.learnizone.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class CourseCardAdapter extends RecyclerView.Adapter<CourseCardAdapter.CourseViewHolder> {

    private List<Course> courses;
    private OnCourseClickListener listener;

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    public CourseCardAdapter(List<Course> courses, OnCourseClickListener listener) {
        this.courses = courses;
        this.listener = listener;
    }

    public void updateCourses(List<Course> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_card, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    class CourseViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardView;
        private ImageView courseImage;
        private TextView courseTitle;
        private TextView courseCategory;
        private TextView courseDuration;
        private TextView courseRating;
        private TextView courseDifficulty;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            courseImage = itemView.findViewById(R.id.course_image);
            courseTitle = itemView.findViewById(R.id.course_title);
            courseCategory = itemView.findViewById(R.id.course_category);
            courseDuration = itemView.findViewById(R.id.course_duration);
            courseRating = itemView.findViewById(R.id.course_rating);
            courseDifficulty = itemView.findViewById(R.id.course_difficulty);

            cardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onCourseClick(courses.get(position));
                }
            });
        }

        public void bind(Course course) {
            courseTitle.setText(course.getTitle());
            courseCategory.setText(course.getCategory());
            courseDuration.setText(course.getDuration());
            courseRating.setText(String.format("%.1f", course.getRating()));
            courseDifficulty.setText(course.getDifficulty());

            // Charger l'image avec Glide
            Glide.with(itemView.getContext())
                    .load(course.getImageUrl())
                    .centerCrop()
                    .into(courseImage);
        }
    }
}
