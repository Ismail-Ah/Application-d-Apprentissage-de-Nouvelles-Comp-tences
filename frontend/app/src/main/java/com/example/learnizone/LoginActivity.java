package com.example.learnizone;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learnizone.activities.TeacherDashboardActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);

        // Set up click listeners
        loginButton.setOnClickListener(v -> login());
        findViewById(R.id.txt_register_l).setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        });
    }

    private void login() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            return;
        }

        // Show loading state
        loginButton.setEnabled(false);
        loginButton.setText("Connexion en cours...");

        // Authenticate user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Get user role from Firestore
                        String userId = auth.getCurrentUser().getUid();
                        db.collection("users").document(userId)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    String role = documentSnapshot.getString("role");
                                    if (role != null) {
                                        // Redirect based on role
                                        Intent intent;
                                        if (role.equals("etudiant")) {
                                            intent = new Intent(LoginActivity.this, MainActivity2.class);
                                        } else {
                                            intent = new Intent(LoginActivity.this, TeacherDashboardActivity.class);
                                        }
                                        startActivity(intent);
                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Error: User role not found",
                                                Toast.LENGTH_SHORT).show();
                                        resetLoginButton();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                    resetLoginButton();
                                });
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        resetLoginButton();
                    }
                });
    }

    private void resetLoginButton() {
        loginButton.setEnabled(true);
        loginButton.setText("Se connecter");
    }
}