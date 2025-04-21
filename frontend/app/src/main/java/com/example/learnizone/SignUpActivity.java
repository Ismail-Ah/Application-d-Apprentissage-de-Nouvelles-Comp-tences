package com.example.learnizone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private TextView txt_login;
    private Button btn_continue;
    private CheckBox termsCheckbox;

    private TextInputEditText fullNameInput, emailInput, passwordInput, confirmPasswordInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_sign_up);
        txt_login = findViewById(R.id.txt_login_s);
        btn_continue = findViewById(R.id.btn_continue);
        fullNameInput = findViewById(R.id.full_name_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        termsCheckbox = findViewById(R.id.terms_checkbox);
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputs()){
                    registerUser();
                }
            }
        });
    }

    public boolean validateInputs(){
        String fullName = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Reset errors
        ((TextInputLayout) findViewById(R.id.full_name_input_layout)).setError(null);
        ((TextInputLayout) findViewById(R.id.email_input_layout)).setError(null);
        ((TextInputLayout) findViewById(R.id.password_input_layout)).setError(null);
        ((TextInputLayout) findViewById(R.id.confirm_password_input_layout)).setError(null);

        // Validate inputs
        if (fullName.isEmpty()) {
            ((TextInputLayout) findViewById(R.id.full_name_input_layout)).setError("Full name is required");
            return false;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ((TextInputLayout) findViewById(R.id.email_input_layout)).setError("Valid email is required");
            return false;
        }
        if (password.length() < 6) {
            ((TextInputLayout) findViewById(R.id.password_input_layout)).setError("Password must be at least 6 characters");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            ((TextInputLayout) findViewById(R.id.confirm_password_input_layout)).setError("Passwords do not match");
            return false;
        }
        if (!termsCheckbox.isChecked()) {
            Toast.makeText(this, "Please agree to the Terms of Service", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void registerUser() {
        String fullName = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = auth.getCurrentUser().getUid();
                        Map<String, Object> user = new HashMap<>();
                        user.put("fullName", fullName);
                        user.put("email", email);
                        user.put("selectedDomains", Collections.emptyList());

                        db.collection("users").document(userId)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignUpActivity.this, SignUpActivity2.class);
                                    intent.putExtra("USER_ID", userId);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(SignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        ((TextInputLayout) findViewById(R.id.email_input_layout)).setError(task.getException().getMessage());
                    }
                });
    }
}