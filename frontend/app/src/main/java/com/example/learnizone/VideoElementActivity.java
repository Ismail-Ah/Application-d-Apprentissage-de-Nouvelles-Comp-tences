package com.example.learnizone;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class VideoElementActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element);

        TextView titleTextView = findViewById(R.id.element_title);
        String title = getIntent().getStringExtra("element_title");
        titleTextView.setText(title != null ? title : "Video Element");

        ImageButton backButton = findViewById(R.id.back_button);
        // Back button click listener
        backButton.setOnClickListener(v -> finish());
    }
}