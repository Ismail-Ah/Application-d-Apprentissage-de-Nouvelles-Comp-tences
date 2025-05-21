package com.example.courseapp

import android.app.Application
import com.example.courseapp.viewmodel.CourseViewModel
import com.google.firebase.FirebaseApp

class CourseApplication : Application() {
    lateinit var courseViewModel: CourseViewModel
        private set

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        courseViewModel = CourseViewModel(this)
    }
}
