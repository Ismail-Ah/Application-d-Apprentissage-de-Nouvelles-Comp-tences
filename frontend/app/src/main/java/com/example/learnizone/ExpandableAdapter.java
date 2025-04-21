package com.example.learnizone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ExpandableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_CATEGORY = 0;
    private static final int VIEW_TYPE_DOMAIN = 1;

    private final Context context;
    private final List<Object> items;

    public ExpandableAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.items = generateItemList(categories);
    }

    private List<Object> generateItemList(List<Category> categories) {
        List<Object> itemList = new ArrayList<>();
        for (Category category : categories) {
            itemList.add(category);
            if (category.isExpanded()) {
                itemList.addAll(category.getDomains());
            }
        }
        return itemList;
    }

    @Override
    public int getItemViewType(int position) {
        return (items.get(position) instanceof Category) ? VIEW_TYPE_CATEGORY : VIEW_TYPE_DOMAIN;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CATEGORY) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
            return new CategoryViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_domain, parent, false);
            return new DomainViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CategoryViewHolder) {
            Category category = (Category) items.get(position);
            ((CategoryViewHolder) holder).bind(category);
            holder.itemView.setOnClickListener(v -> {
                category.setExpanded(!category.isExpanded());
                notifyDataSetChanged(); // rebuild list
            });
        } else {
            Domain domain = (Domain) items.get(position);
            ((DomainViewHolder) holder).bind(domain);
        }
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.category_title);
        }

        public void bind(Category category) {
            categoryTitle.setText(category.getTitle());
        }
    }

    class DomainViewHolder extends RecyclerView.ViewHolder {
        TextView domainName;
        ImageView domainImage;

        public DomainViewHolder(@NonNull View itemView) {
            super(itemView);
            domainName = itemView.findViewById(R.id.domain_name);
            domainImage = itemView.findViewById(R.id.image_domain);
        }

        public void bind(Domain domain) {
            domainName.setText(domain.getName());
            domainImage.setImageResource(domain.getImage());
        }
    }
}
