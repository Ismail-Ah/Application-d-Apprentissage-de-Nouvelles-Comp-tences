package com.example.learnizone;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private CircleImageView profileImage;
    private TextView profileName;
    private TextView profileEmail;
    private TextView coursesCount;
    private TextView hoursCount;
    private TextView streakCount;
    private ConstraintLayout settingsAccount;
    private ConstraintLayout settingsNotifications;
    private ConstraintLayout settingsDarkMode;
    private ConstraintLayout settingsLogout;
    private SwitchMaterial darkModeSwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        loadUserData();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        profileImage = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);
        coursesCount = view.findViewById(R.id.courses_count);
        hoursCount = view.findViewById(R.id.hours_count);
        streakCount = view.findViewById(R.id.streak_count);
        settingsAccount = view.findViewById(R.id.settings_account);
        settingsNotifications = view.findViewById(R.id.settings_notifications);
        settingsDarkMode = view.findViewById(R.id.settings_dark_mode);
        settingsLogout = view.findViewById(R.id.settings_logout);
        darkModeSwitch = view.findViewById(R.id.settings_dark_mode_switch);
    }

    private void loadUserData() {
        // Dans une vraie application, ces données viendraient d'une source de données (préférences, base de données, API)
        profileName.setText("Alex Johnson");
        profileEmail.setText("alex.johnson@example.com");
        coursesCount.setText("12");
        hoursCount.setText("45");
        streakCount.setText("7");

        // Charger l'image de profil avec Glide
        // Glide.with(this)
        //     .load("https://images.unsplash.com/photo-1581091226825-a6a2a5aee158")
        //     .placeholder(R.drawable.placeholder_profile)
        //     .circleCrop()
        //     .into(profileImage);
    }

    private void setupClickListeners() {
        settingsAccount.setOnClickListener(v -> {
            // Dans une vraie application, naviguer vers les paramètres du compte
            Toast.makeText(getContext(), "Paramètres du compte", Toast.LENGTH_SHORT).show();
        });

        settingsNotifications.setOnClickListener(v -> {
            // Dans une vraie application, naviguer vers les paramètres de notifications
            Toast.makeText(getContext(), "Paramètres de notifications", Toast.LENGTH_SHORT).show();
        });

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Dans une vraie application, appliquer le mode sombre
            String message = isChecked ? "Mode sombre activé" : "Mode sombre désactivé";
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            // Appliquer le thème ici
        });

        settingsLogout.setOnClickListener(v -> {
            // Dans une vraie application, déconnexion et retour à l'écran de connexion
            Toast.makeText(getContext(), "Déconnexion...", Toast.LENGTH_SHORT).show();
            navigateToLogin();
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
