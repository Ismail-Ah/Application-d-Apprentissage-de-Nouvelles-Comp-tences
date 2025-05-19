package com.example.learnizone.repositories;

import com.example.learnizone.models.Domain;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DomainRepository {
    private final FirebaseFirestore db;
    private static final String COLLECTION_DOMAINS = "domains";

    public interface OnDomainsLoadedListener {
        void onDomainsLoaded(List<Domain> domains);
        void onError(Exception e);
    }

    public interface OnDomainAddedListener {
        void onDomainAdded(Domain domain);
        void onError(Exception e);
    }

    public DomainRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void getAllDomains(OnDomainsLoadedListener listener) {
        db.collection(COLLECTION_DOMAINS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Domain> domains = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Domain domain = document.toObject(Domain.class);
                        domain.setId(document.getId());
                        domains.add(domain);
                    }
                    listener.onDomainsLoaded(domains);
                })
                .addOnFailureListener(listener::onError);
    }

    public void addDomain(Domain domain, OnDomainAddedListener listener) {
        db.collection(COLLECTION_DOMAINS)
                .add(domain)
                .addOnSuccessListener(documentReference -> {
                    domain.setId(documentReference.getId());
                    listener.onDomainAdded(domain);
                })
                .addOnFailureListener(listener::onError);
    }

    public void addDefaultDomains() {
        List<Domain> defaultDomains = new ArrayList<>();
        defaultDomains.add(new Domain("Informatique", "Domaines liés à l'informatique et aux technologies", ""));
        defaultDomains.add(new Domain("Mathématiques", "Domaines liés aux mathématiques et aux sciences", ""));
        defaultDomains.add(new Domain("Langues", "Domaines liés aux langues et à la communication", ""));
        defaultDomains.add(new Domain("Sciences", "Domaines liés aux sciences naturelles", ""));
        defaultDomains.add(new Domain("Arts", "Domaines liés aux arts et à la créativité", ""));

        for (Domain domain : defaultDomains) {
            addDomain(domain, new OnDomainAddedListener() {
                @Override
                public void onDomainAdded(Domain domain) {
                    // Domain added successfully
                }

                @Override
                public void onError(Exception e) {
                    // Handle error
                }
            });
        }
    }
} 