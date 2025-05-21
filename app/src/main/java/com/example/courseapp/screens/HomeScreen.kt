package com.example.courseapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = "Course App",
            style = MaterialTheme.typography.headlineLarge,
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Description
        Text(
            text = "Welcome to Course App! Your one-stop platform for learning and growing. Access courses, track your progress, and achieve your learning goals.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Sign In Button
        Button(
            onClick = onSignInClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(bottom = 16.dp)
        ) {
            Text("Sign In")
        }

        // Sign Up Button
        OutlinedButton(
            onClick = onSignUpClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Sign Up")
        }
    }
} 