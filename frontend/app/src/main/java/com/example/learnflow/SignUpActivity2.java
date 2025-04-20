package com.example.learnflow;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignUpActivity2 extends AppCompatActivity implements DomainCategoryAdapter.OnDomainSelectionListener {
    RecyclerView domains_recycler_view;
    MaterialButton finishButton;
    List<Category> categories;
    List<Domain> selectedDomains = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        domains_recycler_view = findViewById(R.id.domains_recycler_view);
        finishButton = findViewById(R.id.finish_button);

        domains_recycler_view.setLayoutManager(new LinearLayoutManager(this));

        categories = new ArrayList<>();
        categories.add(new Category("Tech", Arrays.asList(
                new Domain("Web Development", R.drawable.web_development),
                new Domain("AI", R.drawable.ai),
                new Domain("Cybersecurity", R.drawable.cybersecurity)
        )));
        categories.add(new Category("Business", Arrays.asList(
                new Domain("Finance", R.drawable.finance),
                new Domain("Marketing", R.drawable.marketing)
        )));
        categories.add(new Category("Arts", Arrays.asList(
                new Domain("Graphic Design", R.drawable.graphic_design),
                new Domain("Music", R.drawable.music)
        )));

        domains_recycler_view.setAdapter(new DomainCategoryAdapter(categories, this));

        finishButton.setOnClickListener(v -> {
            if (selectedDomains.size() >= 3) {
                // Proceed with account creation
                Toast.makeText(this, "Account created with " + selectedDomains.size() + " domains selected", Toast.LENGTH_SHORT).show();
                // Add your account creation logic here
            } else {
                Toast.makeText(this, "Please select at least 3 domains", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDomainSelected(Domain domain, boolean isSelected) {
        if (isSelected) {
            selectedDomains.add(domain);
        } else {
            selectedDomains.remove(domain);
        }
        finishButton.setEnabled(selectedDomains.size() >= 3);
    }
}