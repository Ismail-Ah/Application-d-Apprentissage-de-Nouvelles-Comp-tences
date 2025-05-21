package com.example.courseapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.courseapp.model.AuthState
import com.example.courseapp.model.UserRole
import com.example.courseapp.screens.HomeScreen
import com.example.courseapp.ui.screens.*
import com.example.courseapp.viewmodel.AuthViewModel
import com.example.courseapp.viewmodel.CourseViewModel
import com.example.courseapp.viewmodel.NoteViewModel
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import com.example.courseapp.utils.NetworkUtils
import com.example.courseapp.viewmodel.CreditCardViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    courseViewModel: CourseViewModel,
    creditCardViewModel: CreditCardViewModel,
    noteViewModel: NoteViewModel
) {
    val authState by authViewModel.authState.collectAsState()
    val user = (authState as? AuthState.Authenticated)?.user
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = when {
            !NetworkUtils.isNetworkAvailable(context) -> Screen.Downloads.route
            authState is AuthState.Authenticated -> Screen.Home.route
            else -> Screen.Welcome.route
        }
    ) {
        composable(Screen.Welcome.route) {
            HomeScreen(
                onSignInClick = { navController.navigate(Screen.Login.route) },
                onSignUpClick = { navController.navigate(Screen.SignUp.route) }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable(Screen.Home.route) {
            if (user != null) {
                if (user.role == UserRole.INSTRUCTOR) {
                    InstructorDashboardScreen(
                        navController = navController,
                        user = user,
                        courseViewModel = courseViewModel,
                        authViewModel = authViewModel
                    )
                } else {
                    HomeScreen(
                        navController = navController,
                        authViewModel = authViewModel,
                        courseViewModel = courseViewModel
                    )
                }
            }
        }
        composable(Screen.MyCourses.route) {
            MyCoursesScreen(
                navController = navController,
                authViewModel = authViewModel,
                courseViewModel = courseViewModel
            )
        }
        composable(Screen.Bookmarks.route) {
            BookmarksScreen(navController = navController)
        }
        composable(Screen.Assignments.route) {
            AssignmentsScreen(navController = navController)
        }
        composable(Screen.Quizzes.route) {
            QuizzesScreen(navController = navController)
        }
        composable(Screen.Schedule.route) {
            ScheduleScreen(navController = navController)
        }
        composable(Screen.Discussions.route) {
            DiscussionsScreen(navController = navController)
        }
        composable(Screen.Progress.route) {
            ProgressScreen(
                navController = navController,
                courseViewModel = courseViewModel
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                navController = navController,
                authViewModel = authViewModel,

            )
        }
        composable(
            route = Screen.CourseDetail.route,
            arguments = Screen.CourseDetail.arguments
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: return@composable
            CourseDetailScreen(
                navController = navController,
                authViewModel = authViewModel,
                courseViewModel = courseViewModel,
                courseId = courseId
            )
        }
        composable(
            route = Screen.Lesson.route,
            arguments = listOf(
                navArgument("courseId") { type = NavType.StringType },
                navArgument("sectionId") { type = NavType.StringType },
                navArgument("lessonIndex") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: return@composable
            val sectionId = backStackEntry.arguments?.getString("sectionId") ?: return@composable
            val lessonIndex = backStackEntry.arguments?.getInt("lessonIndex") ?: return@composable
            LessonScreen(
                navController = navController,
                courseId = courseId,
                sectionId = sectionId,
                lessonIndex = lessonIndex,
                courseViewModel = courseViewModel,
                authViewModel = authViewModel,
                noteViewModel = noteViewModel,
                modifier = Modifier
            )
        }
        composable(
            route = Screen.CourseEditor.route,
            arguments = Screen.CourseEditor.arguments
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: return@composable
            CourseEditorScreen(
                navController = navController,
                courseId = courseId,
                courseViewModel = courseViewModel
            )
        }
        composable(
            route = Screen.LessonDetails.route,
            arguments = Screen.LessonDetails.arguments
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: return@composable
            val sectionId = backStackEntry.arguments?.getString("sectionId") ?: return@composable
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable
            LessonDetailsScreen(
                navController = navController,
                courseId = courseId,
                sectionId = sectionId,
                lessonId = lessonId,
                courseViewModel = courseViewModel
            )
        }
        composable(
            route = Screen.QuizEditor.route,
            arguments = Screen.QuizEditor.arguments
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: return@composable
            val sectionId = backStackEntry.arguments?.getString("sectionId") ?: return@composable
            val quizId = backStackEntry.arguments?.getString("quizId") ?: return@composable
            QuizEditorScreen(
                navController = navController,
                courseId = courseId,
                sectionId = sectionId,
                quizId = quizId,
                courseViewModel = courseViewModel
            )
        }
        composable(
            route = Screen.QuizTaking.route,
            arguments = Screen.QuizTaking.arguments
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: return@composable
            val sectionId = backStackEntry.arguments?.getString("sectionId") ?: return@composable
            val quizId = backStackEntry.arguments?.getString("quizId") ?: return@composable
            QuizTakingScreen(
                navController = navController,
                courseId = courseId,
                sectionId = sectionId,
                quizId = quizId,
                courseViewModel = courseViewModel
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}
