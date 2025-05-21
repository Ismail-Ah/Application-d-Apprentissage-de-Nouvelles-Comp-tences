package com.example.courseapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.courseapp.model.AuthState
import com.example.courseapp.model.Course
import com.example.courseapp.model.UserRole
import com.example.courseapp.navigation.Screen
import com.example.courseapp.ui.components.CourseCard
import com.example.courseapp.ui.components.NavigationDrawer
import com.example.courseapp.viewmodel.AuthViewModel
import com.example.courseapp.viewmodel.CourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCoursesScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    courseViewModel: CourseViewModel = viewModel()
) {
    var showDrawer by remember { mutableStateOf(false) }
    val authState by authViewModel.authState.collectAsState()
    val user = (authState as? AuthState.Authenticated)?.user
    val enrolledCourses by courseViewModel.enrolledCourses.collectAsState(emptyList())
    var coursesWithExtras by remember { mutableStateOf<List<CourseViewModel.CourseWithExtras>>(emptyList()) }

    // Load courses with extras
    LaunchedEffect(user?.id) {
        user?.id?.let { userId ->
            courseViewModel.getCoursesWithExtras(userId) { list ->
                coursesWithExtras = list
            }
        }
    }

    if (user != null) {
        NavigationDrawer(
            user = user,
            navController = navController,
            authViewModel = authViewModel,
            isDrawerOpen = showDrawer,
            onDrawerStateChange = { showDrawer = it }
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
                        },
                        colors = TopAppBarDefaults.largeTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface
                        )
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
                            selected = true,
                            onClick = { navController.navigate(Screen.MyCourses.route) },
                            icon = {
                                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "My courses")
                            },
                            label = { Text("My courses") }
                        )
                        if (user.role == UserRole.STUDENT) {
                            NavigationBarItem(
                                selected = false,
                                onClick = { navController.navigate(Screen.Progress.route) },
                                icon = {
                                    Icon(imageVector = Icons.Outlined.Send, contentDescription = "Progress")
                                },
                                label = { Text("Progress") }
                            )
                        }
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
                if (user.role == UserRole.STUDENT) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Enrolled Courses Section
                        item {
                            Text(
                                "Enrolled Courses",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.5).sp
                                ),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        if (enrolledCourses.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "No enrolled courses",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(enrolledCourses) { course ->
                                val courseWithExtras = coursesWithExtras.find { it.course.id == course.id }
                                CourseCard(
                                    course = course,
                                    progress = courseWithExtras?.progress ?: 0,
                                    instructorName = courseWithExtras?.instructorName ?: course.instructor,
                                    onClick = { navController.navigate(Screen.CourseDetail.createRoute(course.id)) },
                                    userRole = user.role
                                )
                            }
                        }

                        // Completed Courses Section
                        item {
                            Text(
                                "Completed Courses",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.5).sp
                                ),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        val completedCourses = coursesWithExtras.filter { it.progress >= 100 }
                        if (completedCourses.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "No completed courses",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(completedCourses) { courseWithExtras ->
                                CourseCard(
                                    course = courseWithExtras.course,
                                    progress = courseWithExtras.progress,
                                    instructorName = courseWithExtras.instructorName,
                                    onClick = { navController.navigate(Screen.CourseDetail.createRoute(courseWithExtras.course.id)) },
                                    userRole = user.role
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surface,
                                        MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )
                            )
                    ) {
                        AnimatedVisibility(
                            visible = enrolledCourses.isEmpty(),
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = null,
                                        modifier = Modifier.size(96.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        "No Enrolled Courses",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        "Enroll in courses to start learning",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = enrolledCourses.isNotEmpty(),
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                contentPadding = PaddingValues(vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(
                                    items = enrolledCourses,
                                    key = { it.id }
                                ) { course ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .animateContentSize(),
                                        shape = RoundedCornerShape(16.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                    ) {
                                        CourseCard(
                                            course = course,
                                            onClick = {
                                                navController.navigate(Screen.CourseDetail.createRoute(course.id))
                                            },
                                            userRole = user.role
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
} 
