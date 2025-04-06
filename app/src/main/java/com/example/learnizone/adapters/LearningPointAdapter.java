package com.example.learnizone.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnizone.R;

import java.util.List;

public class LearningPointAdapter extends RecyclerView.Adapter<LearningPointAdapter.LearningPointViewHolder> {

    private List<String> learningPoints;

    public LearningPointAdapter(List<String> learningPoints) {
        this.learningPoints = learningPoints;
    }

    @NonNull
    @Override
    public LearningPointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_learning_point, parent, false);
        return new LearningPointViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LearningPointViewHolder holder, int position) {
        String learningPoint = learningPoints.get(position);
        holder.bind(learningPoint);
    }

    @Override
    public int getItemCount() {
        return learningPoints.size();
    }

    static class LearningPointViewHolder extends RecyclerView.ViewHolder {
        private ImageView bulletPoint;
        private TextView learningPointText;

        public LearningPointViewHolder(@NonNull View itemView) {
            super(itemView);
            bulletPoint = itemView.findViewById(R.id.bullet_point);
            learningPointText = itemView.findViewById(R.id.learning_point_text);
        }

        public void bind(String learningPoint) {
            learningPointText.setText(learningPoint);
        }
    }
}
