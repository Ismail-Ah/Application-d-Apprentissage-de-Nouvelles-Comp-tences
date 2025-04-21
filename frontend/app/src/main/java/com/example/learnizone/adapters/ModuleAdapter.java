package com.example.learnizone.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnizone.models.Module;
import com.example.learnizone.R;

import java.util.List;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder> {

    private List<Module> modules;
    private OnModuleClickListener listener;

    public interface OnModuleClickListener {
        void onModuleClick(Module module);
    }

    public ModuleAdapter(List<Module> modules) {
        this.modules = modules;
    }

    public ModuleAdapter(List<Module> modules, OnModuleClickListener listener) {
        this.modules = modules;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_module2, parent, false);
        return new ModuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        Module module = modules.get(position);
        holder.bind(module);
    }

    @Override
    public int getItemCount() {
        return modules.size();
    }

    class ModuleViewHolder extends RecyclerView.ViewHolder {
        private ImageView moduleStatus;
        private TextView moduleTitle;
        private TextView moduleDuration;

        public ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            moduleStatus = itemView.findViewById(R.id.module_status);
            moduleTitle = itemView.findViewById(R.id.module_title);
            moduleDuration = itemView.findViewById(R.id.module_duration);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    Module module = modules.get(position);
                    if (!module.isLocked()) {
                        listener.onModuleClick(module);
                    }
                }
            });
        }

        public void bind(Module module) {
            moduleTitle.setText(module.getTitle());
            moduleDuration.setText(module.getDuration());

            if (module.isCompleted()) {
                moduleStatus.setImageResource(R.drawable.ic_check_circle);
                moduleStatus.setColorFilter(itemView.getContext().getResources().getColor(R.color.primary));
            } else if (module.isLocked()) {
                moduleStatus.setImageResource(R.drawable.ic_lock);
                moduleStatus.setColorFilter(itemView.getContext().getResources().getColor(R.color.gray_400));
                itemView.setAlpha(0.5f);
            } else {
                moduleStatus.setImageResource(R.drawable.ic_circle);
                moduleStatus.setColorFilter(itemView.getContext().getResources().getColor(R.color.gray_300));
            }
        }
    }
}
