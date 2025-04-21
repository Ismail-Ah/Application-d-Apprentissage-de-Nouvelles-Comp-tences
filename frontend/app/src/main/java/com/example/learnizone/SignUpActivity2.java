package com.example.learnizone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SignUpActivity2 extends AppCompatActivity implements DomainCategoryAdapter.OnDomainSelectionListener {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private RecyclerView domainsRecyclerView;
    private MaterialButton finishButton;
    private List<Category> categories;
    private List<Domain> selectedDomains;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        domainsRecyclerView = findViewById(R.id.domains_recycler_view);
        finishButton = findViewById(R.id.finish_button);
        selectedDomains = new ArrayList<>();

        // Set up RecyclerView
        domainsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize categories
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

        domainsRecyclerView.setAdapter(new DomainCategoryAdapter(categories, this));

        // Finish button click listener
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDomains.size() >= 3) {
                    saveSelectedDomains();
                } else {
                    Toast.makeText(SignUpActivity2.this, "Please select at least 3 domains", Toast.LENGTH_SHORT).show();
                }
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

    private void saveSelectedDomains() {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : getIntent().getStringExtra("USER_ID");
        List<String> selectedDomainNames = new ArrayList<>();
        for (Domain domain : selectedDomains) {
            selectedDomainNames.add(domain.getName());
        }

        if (userId != null) {
            db.collection("users").document(userId)
                    .update("selectedDomains", selectedDomainNames)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(SignUpActivity2.this, "Account created with " + selectedDomains.size() + " domains", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity2.this, LoginActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SignUpActivity2.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
        }
    }
}