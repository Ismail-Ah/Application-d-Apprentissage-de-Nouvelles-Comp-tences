package com.example.courseapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.courseapp.model.User
import com.example.courseapp.navigation.Screen
import com.example.courseapp.viewmodel.CourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCourseScreen(
    navController: NavController,
    courseViewModel: CourseViewModel,
    user: User
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var category by remember { mutableStateOf("") }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "Add New Course",
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
                    selected = false,
                    onClick = { navController.navigate(Screen.InstructorDashboard.route) },
                    icon = {
                        Icon(imageVector = Icons.Default.Home, contentDescription = "My Courses")
                    },
                    label = { Text("My Courses") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Already on this screen */ },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Course Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Course Title") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF167464),
                    focusedLabelColor = Color(0xFF167464),
                    cursorColor = Color(0xFF167464)
                ),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Title",
                        tint = Color(0xFF167464)
                    )
                }
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF167464),
                    focusedLabelColor = Color(0xFF167464),
                    cursorColor = Color(0xFF167464)
                ),
                shape = RoundedCornerShape(12.dp),
                minLines = 3,
                maxLines = 5,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Description",
                        tint = Color(0xFF167464)
                    )
                }
            )

            // Image Selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { imagePicker.launch("image/*") },
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF167464).copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (imageUri != null) Icons.Default.CheckCircle else Icons.Default.Add,
                        contentDescription = "Select Image",
                        tint = Color(0xFF167464)
                    )
                    Text(
                        if (imageUri != null) "Image Selected" else "Select Course Image",
                        color = Color(0xFF167464)
                    )
                }
            }

            // Category
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF167464),
                    focusedLabelColor = Color(0xFF167464),
                    cursorColor = Color(0xFF167464)
                ),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Category",
                        tint = Color(0xFF167464)
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    courseViewModel.createCourse(
                        title = title,
                        description = description,
                        instructor = user.name,
                        imageUrl = imageUri?.toString() ?: "",
                        sections = emptyList(),
                        instructorId = user.id,
                        category = category
                    )
                    navController.navigate(Screen.InstructorDashboard.route) {
                        popUpTo(Screen.InstructorDashboard.route) { inclusive = true }
                    }
                },
                enabled = title.isNotBlank() && description.isNotBlank() && imageUri != null && category.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6D00),
                    disabledContainerColor = Color(0xFFFF6D00).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    "Create Course",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
} 