package com.example.courseapp

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.courseapp.navigation.NavGraph
import com.example.courseapp.ui.theme.ELearningTheme
import com.example.courseapp.viewmodel.AuthViewModel
import com.example.courseapp.viewmodel.CourseViewModel
import com.example.courseapp.viewmodel.CreditCardViewModel
import java.util.concurrent.TimeUnit
import com.example.courseapp.viewmodel.NoteViewModel

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private val handler = Handler(Looper.getMainLooper())
    private val engagementCheckInterval = TimeUnit.MINUTES.toMillis(1) // Check every 1 minute

    private lateinit var authViewModel: AuthViewModel
    private lateinit var courseViewModel: CourseViewModel
    private lateinit var creditCardViewModel: CreditCardViewModel
    private lateinit var noteViewModel: NoteViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "Notification permission granted")
        } else {
            Log.d(TAG, "Notification permission denied")
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d(TAG, "Notification permission already granted")
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Show explanation to the user
                    Log.d(TAG, "Should show notification permission rationale")
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private val engagementCheckRunnable = object : Runnable {
        override fun run() {
            try {
                // Get the CourseViewModel instance
                val app = application as? CourseApplication
                if (app != null) {
                    app.courseViewModel.checkUserEngagement()
                } else {
                    Log.e(TAG, "Application is not an instance of CourseApplication")
                }
                
                // Schedule the next check
                handler.postDelayed(this, engagementCheckInterval)
            } catch (e: Exception) {
                Log.e(TAG, "Error checking user engagement", e)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize ViewModels
        authViewModel = AuthViewModel()
        courseViewModel = (application as CourseApplication).courseViewModel
        creditCardViewModel = CreditCardViewModel()
        noteViewModel = NoteViewModel()
        
        // Request notification permission
        checkNotificationPermission()
        
        // Start periodic engagement checks
        handler.post(engagementCheckRunnable)
        
        setContent {
            ELearningTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val context = LocalContext.current

                    NavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        courseViewModel = courseViewModel,
                        creditCardViewModel = creditCardViewModel,
                        noteViewModel = noteViewModel
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove callbacks to prevent memory leaks
        handler.removeCallbacks(engagementCheckRunnable)
    }
}
