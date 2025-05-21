package com.example.courseapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    size: Int = 120
) {
    val primaryColor = Color(0xFFFF6D00) // Orange
    val secondaryColor = Color.White // Blanc
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Name
        Text(
            text = "Course App",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            ),
            color = primaryColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Tagline
        Text(
            text = "Learn. Grow. Succeed.",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.sp
            ),
            color = secondaryColor.copy(alpha = 0.9f)
        )
    }
} 