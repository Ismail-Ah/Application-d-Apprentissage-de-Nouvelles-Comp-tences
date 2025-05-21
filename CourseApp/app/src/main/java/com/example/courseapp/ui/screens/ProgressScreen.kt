package com.example.courseapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.courseapp.navigation.Screen
import com.example.courseapp.viewmodel.CourseViewModel
import com.example.courseapp.viewmodel.CourseViewModel.CourseWithExtras

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    navController: NavController,
    courseViewModel: CourseViewModel
) {
    var showDrawer by remember { mutableStateOf(false) }
    val enrolledCourses by courseViewModel.enrolledCourses.collectAsState(emptyList())
    var coursesWithExtras by remember { mutableStateOf<List<CourseWithExtras>>(emptyList()) }

    // Load courses with extras
    LaunchedEffect(Unit) {
        courseViewModel.getCoursesWithExtras(courseViewModel.getCurrentUserId()) { list ->
            coursesWithExtras = list
        }
    }
    
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "My Progress",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { showDrawer = !showDrawer },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            imageVector = if (showDrawer) Icons.Default.Close else Icons.Default.Menu,
                            contentDescription = if (showDrawer) "Close" else "Menu",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
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
                    onClick = { navController.navigate(Screen.Progress.route) },
                    icon = {
                        Icon(imageVector = Icons.Outlined.Send, contentDescription = "Progress")
                    },
                    label = { Text("Progress") }
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
            // Overall Progress Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Overall Progress",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val overallProgress = if (coursesWithExtras.isNotEmpty()) {
                        coursesWithExtras.map { it.progress }.average() / 100.0
                    } else 0.0
                    LinearProgressIndicator(
                        progress = overallProgress.toFloat(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "${(overallProgress * 100).toInt()}% Complete",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Statistics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Courses Enrolled
                StatCard(
                    title = "Courses Enrolled",
                    value = enrolledCourses.size.toString(),
                    icon = Icons.Outlined.AccountBox,
                    modifier = Modifier.weight(1f)
                )
                // Courses Completed
                StatCard(
                    title = "Courses Completed",
                    value = coursesWithExtras.count { it.progress >= 100 }.toString(),
                    icon = Icons.Outlined.PlayArrow,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // In Progress Courses Section
            Text(
                "In Progress Courses",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val inProgressCourses = coursesWithExtras.filter { 
                it.progress < 100 && enrolledCourses.any { course -> course.id == it.course.id }
            }

            if (inProgressCourses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No courses in progress",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                inProgressCourses.forEach { courseWithExtras ->
                    CourseProgressCard(courseWithExtras)
                }
            }
        }
        BottomNavigationBarProgress(navController)
    }
}

@Composable
private fun CourseProgressCard(courseWithExtras: CourseWithExtras) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = courseWithExtras.course.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "By ${courseWithExtras.instructorName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = courseWithExtras.progress / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${courseWithExtras.progress}% Complete",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Icon(
                imageVector = icon,
                contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary
                )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BottomNavigationBarProgress(navController: NavController) {
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
                onClick = { navController.navigate(Screen.Progress.route) },
                icon = {
                    Icon(imageVector = Icons.Outlined.Send, contentDescription = "Progress")
                },
                label = { Text("Progress") }
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
} 
