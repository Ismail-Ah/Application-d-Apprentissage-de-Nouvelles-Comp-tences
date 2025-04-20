package com.example.learnflow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DomainCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_CATEGORY = 0;
    private static final int TYPE_DOMAIN = 1;

    private List<Object> itemList = new ArrayList<>();
    private List<Category> originalCategories;
    private OnDomainSelectionListener selectionListener;

    public DomainCategoryAdapter(List<Category> categories, OnDomainSelectionListener listener) {
        this.originalCategories = categories;
        this.selectionListener = listener;
        rebuildItemList();
    }

    private void rebuildItemList() {
        itemList.clear();
        for (Category category : originalCategories) {
            itemList.add(category);
            if (category.isExpanded()) {
                itemList.addAll(category.getDomains());
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return (itemList.get(position) instanceof Category) ? TYPE_CATEGORY : TYPE_DOMAIN;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CATEGORY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new CategoryViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_domain, parent, false);
            return new DomainViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_CATEGORY) {
            Category category = (Category) itemList.get(position);
            ((CategoryViewHolder) holder).bind(category);
        } else {
            Domain domain = (Domain) itemList.get(position);
            ((DomainViewHolder) holder).bind(domain);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView expandIcon;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.category_title);
            expandIcon = itemView.findViewById(R.id.expand_icon);
        }

        public void bind(Category category) {
            title.setText(category.getTitle());
            expandIcon.setRotation(category.isExpanded() ? 180f : 0f);  // Rotate icon based on expansion
            itemView.setOnClickListener(v -> {
                category.setExpanded(!category.isExpanded());
                rebuildItemList();
            });
        }
    }

    class DomainViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image;
        ImageView selectionIndicator;

        public DomainViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.domain_name);
            image = itemView.findViewById(R.id.image_domain);
            selectionIndicator = itemView.findViewById(R.id.selection_indicator);
        }

        public void bind(Domain domain) {
            name.setText(domain.getName());
            image.setImageResource(domain.getImage());
            selectionIndicator.setVisibility(domain.isSelected() ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(v -> {
                domain.setSelected(!domain.isSelected());
                selectionIndicator.setVisibility(domain.isSelected() ? View.VISIBLE : View.GONE);
                if (selectionListener != null) {
                    selectionListener.onDomainSelected(domain, domain.isSelected());
                }
            });
        }
    }

    public interface OnDomainSelectionListener {
        void onDomainSelected(Domain domain, boolean isSelected);
    }
}