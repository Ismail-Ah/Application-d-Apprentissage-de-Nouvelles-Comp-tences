package com.example.courseapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
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
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    courseViewModel: CourseViewModel = viewModel()
) {
    var showDrawer by remember { mutableStateOf(false) }
    val authState by authViewModel.authState.collectAsState()
    val user = (authState as? AuthState.Authenticated)?.user
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    
    val courses by courseViewModel.courses.collectAsState(emptyList())
    val enrolledCourses by courseViewModel.enrolledCourses.collectAsState(emptyList())
    
    // Get unique categories
    val categories = remember(courses) { courses.map { it.category }.filter { it.isNotBlank() }.distinct() }

    val filteredCourses = remember(searchQuery, selectedCategory, courses) {
        courses.filter { course ->
            (searchQuery.isBlank() || course.title.contains(searchQuery, ignoreCase = true) ||
                course.instructor.contains(searchQuery, ignoreCase = true) ||
                course.category.contains(searchQuery, ignoreCase = true)) &&
            (selectedCategory == null || course.category == selectedCategory)
        }
    }

    val userId = user?.id ?: ""
    var coursesWithExtras by remember { mutableStateOf<List<CourseViewModel.CourseWithExtras>>(emptyList()) }

    LaunchedEffect(userId, courses) {
        if (userId.isNotBlank()) {
            courseViewModel.getCoursesWithExtras(userId) { list ->
                coursesWithExtras = list
            }
        }
    }

    // Load enrolled courses when user is authenticated
    LaunchedEffect(user?.id) {
        user?.id?.let { userId ->
            courseViewModel.loadEnrolledCourses(userId)
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
                    TopAppBar(
                        title = { 
                            if (showSearchBar) {
                                // Search Bar
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    placeholder = { Text("Search courses...") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Search,
                                            contentDescription = "Search",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    )
                                )
                            } else {
                                Text(
                                    "eLearning",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
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
                        actions = {
                            IconButton(
                                onClick = { showSearchBar = !showSearchBar },
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (showSearchBar) Icons.Default.Close else Icons.Default.Search,
                                    contentDescription = if (showSearchBar) "Close Search" else "Search",
                                    tint = MaterialTheme.colorScheme.primary
                                )
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
                // Filter Bar
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = {
                                Text(
                                    text = "All",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.List,
                                    contentDescription = "All"
                                )
                            }
                        )
                    }
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = {
                                selectedCategory = if (selectedCategory == category) null else category
                            },
                            label = {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = category
                                )
                            }
                        )
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(bottom = 72.dp)
                    ) {
                        if (searchQuery.isNotBlank()) {
                            item {
                                Text(
                                    text = "Search Results",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.5).sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                            items(coursesWithExtras) { courseWithExtras ->
                                CourseCard(
                                    course = courseWithExtras.course,
                                    progress = courseWithExtras.progress,
                                    instructorName = courseWithExtras.instructorName,
                                    onClick = { navController.navigate(Screen.CourseDetail.createRoute(courseWithExtras.course.id)) },
                                    userRole = user?.role ?: UserRole.STUDENT
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        } else {
                            item {
                                Text(
                                    text = "Welcome, ${user.name}!",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.5).sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Continue your learning journey",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        letterSpacing = 0.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            // Featured Courses Section
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Featured Courses",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = (-0.5).sp
                                        )
                                    )
                                    TextButton(
                                        onClick = { /* TODO: Navigate to all courses */ }
                                    ) {
                                        Text(
                                            text = "See All",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Featured Courses List
                            items(coursesWithExtras.take(3)) { courseWithExtras ->
                                CourseCard(
                                    course = courseWithExtras.course,
                                    progress = courseWithExtras.progress,
                                    instructorName = courseWithExtras.instructorName,
                                    onClick = { navController.navigate(Screen.CourseDetail.createRoute(courseWithExtras.course.id)) },
                                    userRole = user?.role ?: UserRole.STUDENT
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Popular Categories Section
                            item {
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "Popular Categories",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.5).sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Categories Grid
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            CategoryCard(
                                                title = "Development",
                                                icon = Icons.Outlined.Info,
                                                onClick = { /* TODO: Navigate to category */ }
                                            )
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            CategoryCard(
                                                title = "Design",
                                                icon = Icons.Outlined.AddCircle,
                                                onClick = { /* TODO: Navigate to category */ }
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            CategoryCard(
                                                title = "Business",
                                                icon = Icons.Outlined.ThumbUp,
                                                onClick = { /* TODO: Navigate to category */ }
                                            )
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            CategoryCard(
                                                title = "Marketing",
                                                icon = Icons.Outlined.Check,
                                                onClick = { /* TODO: Navigate to category */ }
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            // Continue Learning Section
                            item {
                                Text(
                                    text = "Continue Learning",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.5).sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Continue Learning Courses
                            items(coursesWithExtras.drop(3)) { courseWithExtras ->
                                CourseCard(
                                    course = courseWithExtras.course,
                                    progress = courseWithExtras.progress,
                                    instructorName = courseWithExtras.instructorName,
                                    onClick = { navController.navigate(Screen.CourseDetail.createRoute(courseWithExtras.course.id)) },
                                    userRole = user?.role ?: UserRole.STUDENT
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                    
                    BottomNavigationBar(navController)
                }
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            NavigationBarItem(
                selected = true,
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

@Composable
private fun CategoryCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
} 
