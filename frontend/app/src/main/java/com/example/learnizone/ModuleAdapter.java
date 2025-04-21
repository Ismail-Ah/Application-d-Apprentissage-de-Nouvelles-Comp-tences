package com.example.learnizone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder> {
    private List<Module> moduleList;
    private Context context;

    public ModuleAdapter(Context context, List<Module> moduleList) {
        this.context = context;
        this.moduleList = moduleList;
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_module, parent, false);
        return new ModuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        Module module = moduleList.get(position);
        holder.moduleTitle.setText(module.getTitle());
        holder.moduleStatusIcon.setImageResource(module.isCompleted() ? R.drawable.ic_checkmark : R.drawable.ic_cloud_download);

        // Set up the nested RecyclerView for elements
        ModuleElementAdapter elementAdapter = new ModuleElementAdapter(context, module.getElements());
        holder.elementsRecyclerView.setAdapter(elementAdapter);

        // Expand/Collapse logic
        holder.moduleHeader.setOnClickListener(v -> {
            boolean isExpanded = holder.elementsRecyclerView.getVisibility() == View.VISIBLE;
            holder.moduleChevron.setImageResource(isExpanded ? R.drawable.ic_chevron_down : R.drawable.ic_chevron_up);
            holder.elementsRecyclerView.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    public int getItemCount() {
        return moduleList.size();
    }

    static class ModuleViewHolder extends RecyclerView.ViewHolder {
        TextView moduleTitle;
        ImageView moduleChevron, moduleStatusIcon;
        LinearLayout moduleHeader;
        RecyclerView elementsRecyclerView;

        public ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            moduleTitle = itemView.findViewById(R.id.module_title);
            moduleChevron = itemView.findViewById(R.id.module_chevron);
            moduleStatusIcon = itemView.findViewById(R.id.module_status_icon);
            moduleHeader = itemView.findViewById(R.id.module_header);
            elementsRecyclerView = itemView.findViewById(R.id.elements_recycler_view);
        }
    }
}