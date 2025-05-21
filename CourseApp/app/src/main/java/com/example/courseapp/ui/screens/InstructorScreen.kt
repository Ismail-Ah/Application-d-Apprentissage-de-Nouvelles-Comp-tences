package com.example.courseapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.courseapp.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructorScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "My Courses",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Already on this screen */ },
                    icon = {
                        Icon(imageVector = Icons.Default.Home, contentDescription = "My Courses")
                    },
                    label = { Text("My Courses") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.AddCourse.route) },
                    icon = {
                        Icon(imageVector = Icons.Outlined.AddCircle, contentDescription = "Add Course")
                    },
                    label = { Text("Add") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Profile.route) },
                    icon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Profile")
                    },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Course list content will go here
        }
    }
}
 