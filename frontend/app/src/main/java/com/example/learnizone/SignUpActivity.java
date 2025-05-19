package com.example.learnizone;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learnizone.activities.TeacherDashboardActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private TextInputEditText nameInput, emailInput, passwordInput;
    private RadioGroup roleRadioGroup;
    private MaterialButton signUpButton;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        nameInput = findViewById(R.id.full_name_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        roleRadioGroup = findViewById(R.id.roleRadioGroup);
        signUpButton = findViewById(R.id.btn_continue);

        // Set up click listeners
        signUpButton.setOnClickListener(v -> signUp());
        findViewById(R.id.txt_login_s).setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void signUp() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String role = roleRadioGroup.getCheckedRadioButtonId() == R.id.studentRadio ? "etudiant" : "professeur";

        // Validate inputs
        if (TextUtils.isEmpty(name)) {
            nameInput.setError("Name is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            return;
        }
        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            return;
        }

        // Show loading state
        signUpButton.setEnabled(false);
        signUpButton.setText("Creating account...");

        // Create user with Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Create user document in Firestore
                        String userId = auth.getCurrentUser().getUid();
                        Map<String, Object> user = new HashMap<>();
                        user.put("name", name);
                        user.put("email", email);
                        user.put("role", role);

                        db.collection("users").document(userId)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    // Redirect based on role
                                    Intent intent;
                                    if (role.equals("etudiant")) {
                                        intent = new Intent(SignUpActivity.this, MainActivity2.class);
                                    } else {
                                        intent = new Intent(SignUpActivity.this, TeacherDashboardActivity.class);
                                    }
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(SignUpActivity.this, "Error saving user data: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                    resetSignUpButton();
                                });
                    } else {
                        Toast.makeText(SignUpActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        resetSignUpButton();
                    }
                });
    }

    private void resetSignUpButton() {
        signUpButton.setEnabled(true);
        signUpButton.setText("Sign Up");
    }
}