package com.example.courseapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.courseapp.model.AuthState
import com.example.courseapp.navigation.Screen
import com.example.courseapp.viewmodel.AuthViewModel
import coil.compose.AsyncImage
import com.example.courseapp.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.authState.collectAsState()
    val user = (authState as? AuthState.Authenticated)?.user

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 72.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (user != null) {
                    // Profile Header
                    AsyncImage(
                        model = user.profileImage,
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    // Settings Options
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column {
                            if (user.role == UserRole.INSTRUCTOR) {
                                // My Courses Option for Instructors
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            text = "My Courses",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    },
                                    leadingContent = {
                                        Icon(
                                            Icons.Default.Home,
                                            contentDescription = "My Courses",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    trailingContent = {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "Navigate",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        navController.navigate(Screen.InstructorDashboard.route)
                                    }
                                )
                                HorizontalDivider()

                                // Add Course Option for Instructors
                                ListItem(
                                    headlineContent = { Text("Add Course") },
                                    leadingContent = {
                                        Icon(
                                            Icons.Outlined.AddCircle,
                                            contentDescription = "Add Course",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    trailingContent = {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "Navigate",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        navController.navigate(Screen.AddCourse.route)
                                    }
                                )
                                HorizontalDivider()
                            } else {
                                // Student Options
                                ListItem(
                                    headlineContent = { Text("Discover") },
                                    leadingContent = {
                                        Icon(
                                            Icons.Default.Home,
                                            contentDescription = "Discover",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    trailingContent = {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "Navigate",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        navController.navigate(Screen.Home.route)
                                    }
                                )
                                HorizontalDivider()

                                ListItem(
                                    headlineContent = { Text("My Courses") },
                                    leadingContent = {
                                        Icon(
                                            Icons.Default.ShoppingCart,
                                            contentDescription = "My Courses",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    trailingContent = {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "Navigate",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        navController.navigate(Screen.MyCourses.route)
                                    }
                                )
                                HorizontalDivider()

                                ListItem(
                                    headlineContent = { Text("Progress") },
                                    leadingContent = {
                                        Icon(
                                            Icons.Outlined.Send,
                                            contentDescription = "Progress",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    trailingContent = {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "Navigate",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        navController.navigate(Screen.Progress.route)
                                    }
                                )
                                HorizontalDivider()
                            }

                            // Settings Option (for both roles)
                            ListItem(
                                headlineContent = { Text("Settings") },
                                leadingContent = {
                                    Icon(
                                        Icons.Default.Settings,
                                        contentDescription = "Settings",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                trailingContent = {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Navigate",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                modifier = Modifier.clickable {
                                    navController.navigate(Screen.Settings.route)
                                }
                            )
                        }
                    }
                } else {
                    Text("No user information available.")
                }
            }
            BottomNavigationBarProfile(navController, user?.role)
        }
    }
}

@Composable
private fun BottomNavigationBarProfile(navController: NavController, userRole: UserRole?) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            if (userRole == UserRole.INSTRUCTOR) {
                // Instructor Navigation Items
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.InstructorDashboard.route) },
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
                    selected = true,
                    onClick = { /* Already on this screen */ },
                    icon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Profile")
                    },
                    label = { Text("Profile") }
                )
            } else {
                // Student Navigation Items
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Home.route) },
                    icon = {
                        Icon(imageVector = Icons.Default.Home, contentDescription = "Discover")
                    },
                    label = { Text("Discover") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.MyCourses.route) },
                    icon = {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "My courses")
                    },
                    label = { Text("My courses") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Progress.route) },
                    icon = {
                        Icon(imageVector = Icons.Outlined.Send, contentDescription = "Progress")
                    },
                    label = { Text("Progress") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Already on this screen */ },
                    icon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Profile")
                    },
                    label = { Text("Profile") }
                )
            }
        }
    }
} 
