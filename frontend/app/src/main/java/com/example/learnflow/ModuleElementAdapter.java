package com.example.learnflow;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ModuleElementAdapter extends RecyclerView.Adapter<ModuleElementAdapter.ElementViewHolder> {
    private Context context;
    private List<ModuleElement> elementList;

    public ModuleElementAdapter(Context context, List<ModuleElement> elementList) {
        this.context = context;
        this.elementList = elementList;
    }

    @NonNull
    @Override
    public ElementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_module_element, parent, false);
        return new ElementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ElementViewHolder holder, int position) {
        ModuleElement element = elementList.get(position);
        holder.elementTitle.setText(element.getTitle());

        // Set the icon based on the element type
        switch (element.getType()) {
            case TEXT:
                holder.elementIcon.setImageResource(R.drawable.ic_text);
                break;
            case QUIZ:
                holder.elementIcon.setImageResource(R.drawable.ic_quiz);
                break;
            case VIDEO:
                holder.elementIcon.setImageResource(R.drawable.ic_video);
                break;
        }

        // Handle click to navigate to the appropriate activity
        holder.itemView.setOnClickListener(v -> {
            Intent intent;
            switch (element.getType()) {
                case TEXT:
                    intent = new Intent(context, TextElementActivity.class);
                    break;
                case QUIZ:
                    intent = new Intent(context, QuizElementActivity.class);
                    break;
                case VIDEO:
                    intent = new Intent(context, VideoElementActivity.class);
                    break;
                default:
                    return; // Shouldn't happen
            }
            intent.putExtra("element_title", element.getTitle());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return elementList.size();
    }

    static class ElementViewHolder extends RecyclerView.ViewHolder {
        ImageView elementIcon;
        TextView elementTitle;

        public ElementViewHolder(@NonNull View itemView) {
            super(itemView);
            elementIcon = itemView.findViewById(R.id.element_icon);
            elementTitle = itemView.findViewById(R.id.element_title);
        }
    }
}