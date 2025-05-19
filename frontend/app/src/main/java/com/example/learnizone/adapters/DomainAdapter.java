package com.example.learnizone.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnizone.R;
import com.example.learnizone.models.Domain;

import java.util.List;

public class DomainAdapter extends RecyclerView.Adapter<DomainAdapter.DomainViewHolder> {
    private List<Domain> domains;
    private OnDomainClickListener listener;

    public interface OnDomainClickListener {
        void onDomainClick(Domain domain);
    }

    public DomainAdapter(List<Domain> domains, OnDomainClickListener listener) {
        this.domains = domains;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DomainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_domain, parent, false);
        return new DomainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DomainViewHolder holder, int position) {
        Domain domain = domains.get(position);
        holder.bind(domain);
    }

    @Override
    public int getItemCount() {
        return domains.size();
    }

    public void updateDomains(List<Domain> newDomains) {
        this.domains = newDomains;
        notifyDataSetChanged();
    }

    class DomainViewHolder extends RecyclerView.ViewHolder {
        private final TextView domainName;
        private final TextView domainDescription;

        DomainViewHolder(@NonNull View itemView) {
            super(itemView);
            domainName = itemView.findViewById(R.id.domainName);
            domainDescription = itemView.findViewById(R.id.domainDescription);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDomainClick(domains.get(position));
                }
            });
        }

        void bind(Domain domain) {
            domainName.setText(domain.getName());
            domainDescription.setText(domain.getDescription());
        }
    }
} 