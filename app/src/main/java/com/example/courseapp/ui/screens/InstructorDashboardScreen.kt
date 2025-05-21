package com.example.courseapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.courseapp.model.Course
import com.example.courseapp.model.User
import com.example.courseapp.navigation.Screen
import com.example.courseapp.ui.components.CourseCard
import com.example.courseapp.ui.components.NavigationDrawer
import com.example.courseapp.viewmodel.CourseViewModel
import com.example.courseapp.viewmodel.AuthViewModel
import com.example.courseapp.model.CourseSection
import com.example.courseapp.model.Lesson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructorDashboardScreen(
    navController: NavController,
    user: User,
    courseViewModel: CourseViewModel,
    authViewModel: AuthViewModel
) {
    var showDrawer by remember { mutableStateOf(false) }
    var showCreateCourseDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }

    // Load instructor courses
    LaunchedEffect(user.id) {
        courseViewModel.loadInstructorCourses(user.id)
    }

    val instructorCourses by courseViewModel.instructorCourses.collectAsState(emptyList())

    NavigationDrawer(
        user = user,
        navController = navController,
        authViewModel = authViewModel,
        isDrawerOpen = showDrawer,
        onDrawerStateChange = { showDrawer = it }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        if (showSearchBar) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Search your courses...") },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        } else {
                            Text(
                                "My Courses",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { showDrawer = !showDrawer },
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(
                                imageVector = if (showDrawer) Icons.Default.Close else Icons.Default.Menu,
                                contentDescription = if (showDrawer) "Close" else "Menu",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showSearchBar = !showSearchBar },
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(
                                imageVector = if (showSearchBar) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = if (showSearchBar) "Close Search" else "Search",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { showCreateCourseDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Create Course")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.primary,
                        actionIconContentColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.shadow(4.dp)
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(instructorCourses.filter {
                    it.title.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
                }) { course ->
                    CourseCard(
                        course = course,
                        onClick = {
                            navController.navigate("course_editor/${course.id}")
                        }
                    )
                }
            }
        }
    }

    if (showCreateCourseDialog) {
        CreateCourseDialog(
            onDismiss = { showCreateCourseDialog = false },
            onCreateCourse = { title, description, imageUri, category ->
                courseViewModel.createCourse(
                    title = title,
                    description = description,
                    instructor = user.name,
                    imageUrl = imageUri?.toString() ?: "",
                    sections = emptyList(),
                    instructorId = user.id,
                    category = category
                )
                showCreateCourseDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateCourseDialog(
    onDismiss: () -> Unit,
    onCreateCourse: (String, String, Uri?, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var category by remember { mutableStateOf("") }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Course") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Course Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = { imagePicker.launch("image/*") }) {
                    Text(if (imageUri != null) "Image Selected" else "Select Image")
                }
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onCreateCourse(title, description, imageUri, category)
                },
                enabled = title.isNotBlank() && description.isNotBlank() && imageUri != null && category.isNotBlank()
            ) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnrolledStudentsDialog(
    course: Course,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enrolled Students") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Total enrolled: ${course.enrolledStudents}")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
} 
