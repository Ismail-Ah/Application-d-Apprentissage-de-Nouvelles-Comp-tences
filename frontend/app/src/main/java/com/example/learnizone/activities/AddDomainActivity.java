package com.example.learnizone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnizone.R;
import com.example.learnizone.adapters.DomainAdapter;
import com.example.learnizone.models.Domain;
import com.example.learnizone.repositories.DomainRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AddDomainActivity extends AppCompatActivity {
    private TextInputEditText domainNameInput;
    private TextInputEditText domainDescriptionInput;
    private RecyclerView domainsRecyclerView;
    private DomainAdapter domainAdapter;
    private DomainRepository domainRepository;
    private Domain selectedDomain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_domain);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        domainNameInput = findViewById(R.id.domainNameInput);
        domainDescriptionInput = findViewById(R.id.domainDescriptionInput);
        domainsRecyclerView = findViewById(R.id.domainsRecyclerView);
        FloatingActionButton saveDomainFab = findViewById(R.id.saveDomainFab);

        // Set up RecyclerView
        domainsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        domainAdapter = new DomainAdapter(new ArrayList<>(), domain -> {
            selectedDomain = domain;
            // Return the selected domain
            Intent resultIntent = new Intent();
            resultIntent.putExtra("domainId", domain.getId());
            resultIntent.putExtra("domainName", domain.getName());
            setResult(RESULT_OK, resultIntent);
            finish();
        });
        domainsRecyclerView.setAdapter(domainAdapter);

        // Initialize repository
        domainRepository = new DomainRepository();

        // Load existing domains
        loadDomains();

        // Set up FAB click listener
        saveDomainFab.setOnClickListener(v -> saveNewDomain());
    }

    private void loadDomains() {
        domainRepository.getAllDomains(new DomainRepository.OnDomainsLoadedListener() {
            @Override
            public void onDomainsLoaded(List<Domain> domains) {
                domainAdapter.updateDomains(domains);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AddDomainActivity.this,
                        "Erreur lors du chargement des domaines: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveNewDomain() {
        String name = domainNameInput.getText().toString().trim();
        String description = domainDescriptionInput.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            domainNameInput.setError("Le nom du domaine est requis");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            domainDescriptionInput.setError("La description est requise");
            return;
        }

        Domain newDomain = new Domain(name, description, "");
        domainRepository.addDomain(newDomain, new DomainRepository.OnDomainAddedListener() {
            @Override
            public void onDomainAdded(Domain domain) {
                // Return the new domain
                Intent resultIntent = new Intent();
                resultIntent.putExtra("domainId", domain.getId());
                resultIntent.putExtra("domainName", domain.getName());
                setResult(RESULT_OK, resultIntent);
                finish();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AddDomainActivity.this,
                        "Erreur lors de l'ajout du domaine: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 