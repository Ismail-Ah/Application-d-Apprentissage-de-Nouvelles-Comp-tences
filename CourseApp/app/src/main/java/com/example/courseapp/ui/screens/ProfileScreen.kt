package com.example.courseapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
                            // Home Option
                            ListItem(
                                headlineContent = { Text("Home") },
                                leadingContent = {
                                    Icon(
                                        Icons.Default.Home,
                                        contentDescription = "Home",
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
                            Divider()

                            // My Courses Option
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
                            Divider()

                            // Settings Option
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
            BottomNavigationBarProfile(navController)
        }
    }
}

@Composable
private fun BottomNavigationBarProfile(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
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
                selected = true,
                onClick = { navController.navigate(Screen.Profile.route) },
                icon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Profile")
                },
                label = { Text("Profile") }
            )
        }
    }
} 
