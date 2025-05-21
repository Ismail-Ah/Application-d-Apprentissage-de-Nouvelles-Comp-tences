package com.example.courseapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courseapp.model.Course
import com.example.courseapp.model.CourseEnrollment
import com.example.courseapp.model.CourseSection
import com.example.courseapp.model.User
import com.example.courseapp.model.Lesson
import com.example.courseapp.model.Quiz
import com.example.courseapp.model.Question
import com.example.courseapp.model.QuestionType
import com.example.courseapp.model.QuizAttempt
import com.example.courseapp.model.Certificate
import com.example.courseapp.repository.CourseRepository
import com.example.courseapp.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.flow
import com.example.courseapp.service.GeminiService
import android.content.Context
import com.example.courseapp.R
import com.google.firebase.firestore.ListenerRegistration
import com.example.courseapp.local.AppDatabase
import com.example.courseapp.local.CourseEntity
import com.example.courseapp.local.LessonEntity
import com.example.courseapp.local.SectionEntity
import com.google.firebase.storage.FirebaseStorage
import com.example.courseapp.service.NotificationService
import com.example.courseapp.service.UserActivityTracker

import java.io.File

class CourseViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "CourseViewModel"
    private val repository = CourseRepository()
    private val userRepository = UserRepository()
    private val db = FirebaseFirestore.getInstance()
    private val coursesCollection = db.collection("courses")
    private val enrollmentsCollection = db.collection("enrollments")
    private val certificatesCollection = db.collection("certificates")
    private val context = application.applicationContext
    private val geminiService = GeminiService(context.getString(R.string.gemini_api_key)).apply {
        checkModelAvailability()
    }
    
    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses
    
    private val _enrolledCourses = MutableStateFlow<List<Course>>(emptyList())
    val enrolledCourses: StateFlow<List<Course>> = _enrolledCourses.asStateFlow()
    
    private val _instructorCourses = MutableStateFlow<List<Course>>(emptyList())
    val instructorCourses: StateFlow<List<Course>> = _instructorCourses
    
    private val _selectedCourse = MutableStateFlow<Course?>(null)
    val selectedCourse: StateFlow<Course?> = _selectedCourse.asStateFlow()
    
    private val _courseProgress = MutableStateFlow<Int>(0)
    val courseProgress: StateFlow<Int> = _courseProgress.asStateFlow()
    
    private val _enrolledStudents = MutableStateFlow<List<User>>(emptyList())
    val enrolledStudents: StateFlow<List<User>> = _enrolledStudents.asStateFlow()
    
    private val _externalResources = MutableStateFlow<List<String>>(emptyList())
    val externalResources: StateFlow<List<String>> = _externalResources.asStateFlow()
    
    private val _certificate = MutableStateFlow<Certificate?>(null)
    val certificate: StateFlow<Certificate?> = _certificate.asStateFlow()
    
    private val local_db = AppDatabase.getDatabase(application)

    private val storage = FirebaseStorage.getInstance()

    private val _downloadedCourses = MutableStateFlow<List<Course>>(emptyList())
    val downloadedCourses: StateFlow<List<Course>> = _downloadedCourses

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading: StateFlow<Boolean> = _isDownloading

    private val _downloadProgress = MutableStateFlow(0f)
    val downloadProgress: StateFlow<Float> = _downloadProgress
    
    private val notificationService = NotificationService(application)
    private val userActivityTracker = UserActivityTracker(application, notificationService)
    
    init {
        viewModelScope.launch {
            repository.getAllCourses().collectLatest { courses ->
                Log.d(TAG, "Loaded ${courses.size} courses")
                courses.forEach { course ->
                    Log.d(TAG, "Course: ${course.title}")
                }
                _courses.value = courses
            }
        }
        // Load downloaded courses on init
        loadDownloadedCourses()
    }
    
    fun loadEnrolledCourses(userId: String) {
        viewModelScope.launch {
            Log.d(TAG, "Loading enrolled courses for user: $userId")
            repository.getEnrolledCourses(userId).collectLatest { courses ->
                Log.d(TAG, "Loaded ${courses.size} enrolled courses for user $userId")
                courses.forEach { course ->
                    Log.d(TAG, "Enrolled course: ${course.title} (ID: ${course.id})")
                }
                _enrolledCourses.value = courses
            }
        }
    }
    
    fun loadInstructorCourses(instructorId: String) {
        viewModelScope.launch {
            try {
                val snapshot = coursesCollection.whereEqualTo("instructor", instructorId).get().await()
                _instructorCourses.value = snapshot.toObjects(Course::class.java)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun loadCourseById(courseId: String) {
        viewModelScope.launch {
            repository.getCourseById(courseId).collectLatest { course ->
                Log.d(TAG, "Loaded course: ${course?.title}")
                _selectedCourse.value = course
            }
        }
    }
    
    fun enrollInCourse(studentId: String, courseId: String) {
        viewModelScope.launch {
            try {
                repository.enrollInCourse(studentId, courseId)
                // Immediately reload enrolled courses for the student so the UI updates
                loadEnrolledCourses(studentId)
            } catch (e: Exception) {
                Log.e(TAG, "Error enrolling in course", e)
            }
        }
    }
    
    fun updateCourseProgress(userId: String, courseId: String, lessonId: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting progress update for user $userId in course $courseId for lesson $lessonId")
                
                // Get the enrollment document
                val enrollmentQuery = enrollmentsCollection
                    .whereEqualTo("studentId", userId)
                    .whereEqualTo("courseId", courseId)
                    .get()
                    .await()
                
                if (enrollmentQuery.isEmpty) {
                    Log.e(TAG, "No enrollment found for user $userId in course $courseId")
                    return@launch
                }
                
                val enrollmentDoc = enrollmentQuery.documents[0]
                val enrollment = enrollmentDoc.toObject(CourseEnrollment::class.java)
                
                if (enrollment == null) {
                    Log.e(TAG, "Failed to parse enrollment document")
                    return@launch
                }
                
                // Get the course to calculate total lessons and check section completion
                val courseRef = coursesCollection.document(courseId)
                val course = courseRef.get().await().toObject(Course::class.java)
                
                if (course != null) {
                    // Find which section this lesson belongs to
                    val section = course.sections.find { section ->
                        section.lessons.any { it.id == lessonId }
                    }
                    
                    if (section != null) {
                        // Add the new lesson to completed lessons if not already completed
                        val updatedCompletedLessons = if (!enrollment.completedLessons.contains(lessonId)) {
                            enrollment.completedLessons + lessonId
                        } else {
                            enrollment.completedLessons
                        }
                        
                        // Check if all lessons in this section are now completed
                        val sectionLessons = section.lessons.map { it.id }
                        val isSectionCompleted = sectionLessons.all { it in updatedCompletedLessons }
                        
                        // Calculate progress based on completed lessons vs total lessons
                        val totalLessons = course.sections.sumOf { it.lessons.size }
                        val progress = (updatedCompletedLessons.size * 100) / totalLessons
                        
                        val updatedEnrollment = enrollment.copy(
                            completedLessons = updatedCompletedLessons,
                            progress = progress
                        )
                        
                        // Update the enrollment document
                        enrollmentDoc.reference.set(updatedEnrollment).await()
                        
                        // If section is completed, add XP
                        if (isSectionCompleted) {
                            updateUserXP(userId, 2) // Add 2 XP for completing a section
                        }
                        
                        // Update the local state
                        _selectedCourse.value = course
                        
                        // Generate certificate if progress reaches 100%
                        if (progress == 100) {
                            generateCertificate(courseId, userId)
                        }
                        
                        Log.d(TAG, "Progress updated successfully: ${updatedCompletedLessons.size}/$totalLessons lessons completed (${progress}%)")
                    }
                } else {
                    Log.e(TAG, "Invalid course")
                }

                // Track lesson completion for notifications
                userActivityTracker.trackLessonCompletion(userId, courseId, lessonId)
                
                // Check for incomplete quizzes
                userActivityTracker.checkIncompleteQuizzes(userId)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating course progress", e)
            }
        }
    }
    
    fun loadCourseProgress(userId: String, courseId: String) {
        viewModelScope.launch {
            repository.getCourseProgress(userId, courseId).collectLatest { progress ->
                Log.d(TAG, "Loaded progress for user $userId in course $courseId: $progress%")
                _courseProgress.value = progress
            }
        }
    }
    
    fun createCourse(
        title: String,
        description: String,
        instructor: String,
        imageUrl: String,
        sections: List<CourseSection>,
        instructorId: String,
        category: String
    ) {
        viewModelScope.launch {
            try {
                // Always generate a unique course ID!
                val courseId = java.util.UUID.randomUUID().toString()
                val course = Course(
                    id = courseId,
                    title = title,
                    description = description,
                    instructor = instructorId,
                    imageUrl = imageUrl,
                    sections = sections,
                    category = category
                )
                repository.createCourse(course)
            } catch (e: Exception) {
                Log.e(TAG, "Error creating course", e)
            }
        }
    }
    
    fun loadEnrolledStudents(courseId: String) {
        viewModelScope.launch {
            repository.getEnrolledStudents(courseId).collectLatest { students ->
                _enrolledStudents.value = students
            }
        }
    }
    
    fun addSectionToCourse(courseId: String, sectionTitle: String) {
        viewModelScope.launch {
            try {
                val course = _selectedCourse.value ?: return@launch
                val newSection = CourseSection(
                    id = UUID.randomUUID().toString(),
                    title = sectionTitle,
                    lessons = emptyList()
                )
                val updatedSections = course.sections + newSection
                val updatedCourse = course.copy(sections = updatedSections)
                
                // Update the course in Firestore
                coursesCollection.document(courseId).set(updatedCourse).await()
                _selectedCourse.value = updatedCourse
                
                Log.d(TAG, "Section added successfully: $sectionTitle")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding section", e)
            }
        }
    }
    
    fun addLessonToSection(
        courseId: String,
        sectionId: String,
        lessonTitle: String,
        lessonDescription: String,
        lessonVideoUrl: String,
        lessonDuration: String,
        lessonPdfUrl: String,
        lessonImageUrl: String
    ) {
        viewModelScope.launch {
            try {
                val course = _selectedCourse.value ?: return@launch
                val updatedSections = course.sections.map { section ->
                    if (section.id == sectionId) {
                        val newLesson = Lesson(
                            id = UUID.randomUUID().toString(),
                            title = lessonTitle,
                            description = lessonDescription,
                            videoUrl = lessonVideoUrl,
                            duration = lessonDuration,
                            pdfUrl = lessonPdfUrl,
                            imageUrl = lessonImageUrl
                        )
                        section.copy(lessons = section.lessons + newLesson)
                    } else section
                }
                val updatedCourse = course.copy(sections = updatedSections)
                
                // Update the course in Firestore
                coursesCollection.document(courseId).set(updatedCourse).await()
                _selectedCourse.value = updatedCourse
                
                Log.d(TAG, "Lesson added successfully: $lessonTitle")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding lesson", e)
            }
        }
    }
    
    fun loadCourses() {
        viewModelScope.launch {
            try {
                val snapshot = coursesCollection.get().await()
                _courses.value = snapshot.toObjects(Course::class.java)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun selectCourse(courseId: String) {
        viewModelScope.launch {
            try {
                val document = coursesCollection.document(courseId).get().await()
                val course = document.toObject(Course::class.java)
                _selectedCourse.value = course
                Log.d(TAG, "Course loaded: ${course?.title}")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading course", e)
            }
        }
    }
    
    fun updateCourseRating(courseId: String, rating: Float) {
        viewModelScope.launch {
            try {
                repository.updateCourseRating(courseId, rating)
                // Reload the course to update the UI
                loadCourseById(courseId)
                Log.d(TAG, "Course rating updated to $rating")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating course rating", e)
            }
        }
    }
    
    fun addLessonWithFiles(
        courseId: String,
        sectionId: String,
        lessonTitle: String,
        lessonDescription: String,
        lessonDuration: String,
        videoUri: Uri?,
        pdfUri: Uri?,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            try {
                // Check if user is authenticated
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser == null) {
                    Log.e(TAG, "User must be authenticated to upload files")
                    return@launch
                }

                // Generate unique file names with extensions
                val videoPath = videoUri?.let { 
                    val extension = getFileExtension(it)
                    "lessons/videos/${UUID.randomUUID()}$extension"
                }
                val pdfPath = pdfUri?.let { 
                    val extension = getFileExtension(it)
                    "lessons/pdfs/${UUID.randomUUID()}$extension"
                }
                val imagePath = imageUri?.let { 
                    val extension = getFileExtension(it)
                    "lessons/images/${UUID.randomUUID()}$extension"
                }

                // Upload files with proper error handling
                val videoUrl = videoUri?.let { 
                    try {
                        repository.uploadFileToStorage(it, videoPath!!)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error uploading video: ${e.message}", e)
                        ""
                    }
                } ?: ""

                val pdfUrl = pdfUri?.let { 
                    try {
                        repository.uploadFileToStorage(it, pdfPath!!)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error uploading PDF: ${e.message}", e)
                        ""
                    }
                } ?: ""

                val imageUrl = imageUri?.let { 
                    try {
                        repository.uploadFileToStorage(it, imagePath!!)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error uploading image: ${e.message}", e)
                        ""
                    }
                } ?: ""

                // Add the lesson with the uploaded file URLs
                addLessonToSection(
                    courseId = courseId,
                    sectionId = sectionId,
                    lessonTitle = lessonTitle,
                    lessonDescription = lessonDescription,
                    lessonVideoUrl = videoUrl,
                    lessonDuration = lessonDuration,
                    lessonPdfUrl = pdfUrl,
                    lessonImageUrl = imageUrl
                )

                Log.d(TAG, "Lesson added successfully with files")
            } catch (e: Exception) {
                Log.e(TAG, "Error uploading files", e)
            }
        }
    }

    private fun getFileExtension(uri: Uri): String {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri)
        return when (mimeType) {
            "image/jpeg" -> ".jpg"
            "image/png" -> ".png"
            "application/pdf" -> ".pdf"
            "video/mp4" -> ".mp4"
            else -> ""
        }
    }

    fun getLessonCompletionStatus(userId: String, courseId: String, lessonId: String): Flow<Boolean> = callbackFlow {
        try {
            val enrollmentQuery = enrollmentsCollection
                .whereEqualTo("studentId", userId)
                .whereEqualTo("courseId", courseId)
                .get()
                .await()

            if (enrollmentQuery.isEmpty) {
                trySend(false)
                return@callbackFlow
            }

            val enrollment = enrollmentQuery.documents[0].toObject(CourseEnrollment::class.java)
            trySend(enrollment?.completedLessons?.contains(lessonId) ?: false)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting lesson completion status", e)
            trySend(false)
        }
        
        // Add awaitClose to properly clean up the flow
        awaitClose()
    }

    fun updateLesson(
        courseId: String,
        sectionId: String,
        lessonId: String,
        title: String,
        description: String,
        duration: String
    ) {
        viewModelScope.launch {
            try {
                val course = _selectedCourse.value ?: return@launch
                val updatedSections = course.sections.map { section ->
                    if (section.id == sectionId) {
                        val updatedLessons = section.lessons.map { lesson ->
                            if (lesson.id == lessonId) {
                                lesson.copy(
                                    title = title,
                                    description = description,
                                    duration = duration
                                )
                            } else lesson
                        }
                        section.copy(lessons = updatedLessons)
                    } else section
                }
                val updatedCourse = course.copy(sections = updatedSections)
                
                // Update the course in Firestore
                coursesCollection.document(courseId).set(updatedCourse).await()
                _selectedCourse.value = updatedCourse
                
                Log.d(TAG, "Lesson updated successfully: $title")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating lesson", e)
            }
        }
    }

    fun deleteLesson(courseId: String, sectionId: String, lessonId: String) {
        viewModelScope.launch {
            try {
                val course = _selectedCourse.value ?: return@launch
                val updatedSections = course.sections.map { section ->
                    if (section.id == sectionId) {
                        section.copy(lessons = section.lessons.filter { it.id != lessonId })
                    } else section
                }
                val updatedCourse = course.copy(sections = updatedSections)
                
                // Update the course in Firestore
                coursesCollection.document(courseId).set(updatedCourse).await()
                _selectedCourse.value = updatedCourse
                
                Log.d(TAG, "Lesson deleted successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting lesson", e)
            }
        }
    }

    fun updateLessonMedia(
        courseId: String,
        sectionId: String,
        lessonId: String,
        mediaType: String, // "image", "video", "pdf"
        uri: Uri
    ) {
        viewModelScope.launch {
            // 1. Upload file to Firebase Storage, get downloadUrl
            val downloadUrl = uploadFileToFirebase(uri, mediaType) // You must implement this!
            // 2. Update lesson's media URL in Firestore
            val course = _selectedCourse.value ?: return@launch
            val updatedSections = course.sections.map { section ->
                if (section.id == sectionId) {
                    val updatedLessons = section.lessons.map { lesson ->
                        if (lesson.id == lessonId) {
                            when (mediaType) {
                                "image" -> lesson.copy(imageUrl = downloadUrl)
                                "video" -> lesson.copy(videoUrl = downloadUrl)
                                "pdf" -> lesson.copy(pdfUrl = downloadUrl)
                                else -> lesson
                            }
                        } else lesson
                    }
                    section.copy(lessons = updatedLessons)
                } else section
            }
            val updatedCourse = course.copy(sections = updatedSections)
            coursesCollection.document(courseId).set(updatedCourse).await()
            _selectedCourse.value = updatedCourse
        }
    }

    fun deleteLessonMedia(
        courseId: String,
        sectionId: String,
        lessonId: String,
        mediaType: String // "image", "video", "pdf"
    ) {
        viewModelScope.launch {
            val course = _selectedCourse.value ?: return@launch
            val updatedSections = course.sections.map { section ->
                if (section.id == sectionId) {
                    val updatedLessons = section.lessons.map { lesson ->
                        if (lesson.id == lessonId) {
                            when (mediaType) {
                                "image" -> lesson.copy(imageUrl = "")
                                "video" -> lesson.copy(videoUrl = "")
                                "pdf" -> lesson.copy(pdfUrl = "")
                                else -> lesson
                            }
                        } else lesson
                    }
                    section.copy(lessons = updatedLessons)
                } else section
            }
            val updatedCourse = course.copy(sections = updatedSections)
            coursesCollection.document(courseId).set(updatedCourse).await()
            _selectedCourse.value = updatedCourse
        }
    }

    // You must implement this function to upload to Firebase Storage and return the download URL
    suspend fun uploadFileToFirebase(uri: Uri, mediaType: String): String {
        val extension = when (mediaType) {
            "image" -> ".jpg"
            "video" -> ".mp4"
            "pdf" -> ".pdf"
            else -> ""
        }
        val path = "lessons/${mediaType}s/${UUID.randomUUID()}$extension"
        return repository.uploadFileToStorage(uri, path)
    }

    fun addQuizToSection(
        courseId: String,
        sectionId: String,
        quizTitle: String,
        quizDescription: String,
        passingScore: Int,
        timeLimit: Int,
        attemptsAllowed: Int
    ) {
        viewModelScope.launch {
            try {
                val course = _selectedCourse.value ?: return@launch
                val newQuiz = Quiz(
                    id = UUID.randomUUID().toString(),
                    title = quizTitle,
                    description = quizDescription,
                    passingScore = passingScore,
                    timeLimit = timeLimit,
                    attemptsAllowed = attemptsAllowed
                )
                
                val updatedSections = course.sections.map { section ->
                    if (section.id == sectionId) {
                        section.copy(quizzes = section.quizzes + newQuiz)
                    } else section
                }
                val updatedCourse = course.copy(sections = updatedSections)
                
                // Update the course in Firestore
                coursesCollection.document(courseId).set(updatedCourse).await()
                _selectedCourse.value = updatedCourse
                
                Log.d(TAG, "Quiz added successfully: $quizTitle")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding quiz", e)
            }
        }
    }

    fun addQuestionToQuiz(
        courseId: String,
        sectionId: String,
        quizId: String,
        questionText: String,
        questionType: QuestionType,
        options: List<String>,
        correctAnswer: String,
        points: Int
    ) {
        viewModelScope.launch {
            try {
                val course = _selectedCourse.value ?: return@launch
                val newQuestion = Question(
                    id = UUID.randomUUID().toString(),
                    text = questionText,
                    type = questionType,
                    options = options,
                    correctAnswer = correctAnswer,
                    points = points
                )
                
                val updatedSections = course.sections.map { section ->
                    if (section.id == sectionId) {
                        val updatedQuizzes = section.quizzes.map { quiz ->
                            if (quiz.id == quizId) {
                                quiz.copy(questions = quiz.questions + newQuestion)
                            } else quiz
                        }
                        section.copy(quizzes = updatedQuizzes)
                    } else section
                }
                val updatedCourse = course.copy(sections = updatedSections)
                
                // Update the course in Firestore
                coursesCollection.document(courseId).set(updatedCourse).await()
                _selectedCourse.value = updatedCourse
                
                Log.d(TAG, "Question added successfully to quiz")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding question to quiz", e)
            }
        }
    }

    fun deleteQuiz(courseId: String, sectionId: String, quizId: String) {
        viewModelScope.launch {
            try {
                val course = _selectedCourse.value ?: return@launch
                val updatedSections = course.sections.map { section ->
                    if (section.id == sectionId) {
                        section.copy(quizzes = section.quizzes.filter { it.id != quizId })
                    } else section
                }
                val updatedCourse = course.copy(sections = updatedSections)
                
                // Update the course in Firestore
                coursesCollection.document(courseId).set(updatedCourse).await()
                _selectedCourse.value = updatedCourse
                
                Log.d(TAG, "Quiz deleted successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting quiz", e)
            }
        }
    }

    fun saveQuizAttempt(
        courseId: String,
        sectionId: String,
        quizId: String,
        score: Int,
        hasPassed: Boolean
    ) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "saveQuizAttempt called for user: ${FirebaseAuth.getInstance().currentUser?.uid}")
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser == null) {
                    Log.e(TAG, "User must be authenticated to save quiz attempt")
                    return@launch
                }
                Log.d(TAG, "User authenticated: ${currentUser.uid}")

                val attempt = QuizAttempt(
                    id = UUID.randomUUID().toString(),
                    studentId = currentUser.uid,
                    courseId = courseId,
                    sectionId = sectionId,
                    quizId = quizId,
                    score = score,
                    hasPassed = hasPassed,
                    timestamp = System.currentTimeMillis()
                )

                // Save the attempt
                db.collection("quiz_attempts").document(attempt.id).set(attempt).await()
                Log.d(TAG, "Quiz attempt written to Firestore: ${attempt.id}")

                // Count failed attempts for this quiz
                val failedAttemptsQuery = db.collection("quiz_attempts")
                    .whereEqualTo("studentId", currentUser.uid)
                    .whereEqualTo("courseId", courseId)
                    .whereEqualTo("sectionId", sectionId)
                    .whereEqualTo("quizId", quizId)
                    .whereEqualTo("hasPassed", false)
                    .get()
                    .await()

                val failedAttempts = failedAttemptsQuery.size()

                // Get the quiz to check attemptsAllowed
                val course = coursesCollection.document(courseId).get().await().toObject(Course::class.java)
                val quiz = course?.sections?.find { it.id == sectionId }?.quizzes?.find { it.id == quizId }
                val attemptsAllowed = quiz?.attemptsAllowed ?: 3

                if (!hasPassed && failedAttempts >= attemptsAllowed) {
                    // Reset section progress
                    val enrollmentQuery = enrollmentsCollection
                        .whereEqualTo("studentId", currentUser.uid)
                        .whereEqualTo("courseId", courseId)
                        .get()
                        .await()

                    if (!enrollmentQuery.isEmpty) {
                        val enrollmentDoc = enrollmentQuery.documents[0]
                        val enrollment = enrollmentDoc.toObject(CourseEnrollment::class.java)
                        if (enrollment != null && course != null) {
                            // Remove all completed lessons from this section
                            val sectionLessonIds = course.sections.find { it.id == sectionId }?.lessons?.map { it.id } ?: emptyList()
                            val updatedCompletedLessons = enrollment.completedLessons.filterNot { sectionLessonIds.contains(it) }
                            val updatedEnrollment = enrollment.copy(
                                completedLessons = updatedCompletedLessons,
                                progress = calculateProgress(courseId, updatedCompletedLessons)
                            )
                            enrollmentDoc.reference.set(updatedEnrollment).await()
                            Log.d(TAG, "Section progress reset due to exceeding allowed quiz attempts.")
                        }
                    }
                } else if (hasPassed) {
                    // If passed, just update the quiz attempt without affecting progress
                    val enrollmentQuery = enrollmentsCollection
                        .whereEqualTo("studentId", currentUser.uid)
                        .whereEqualTo("courseId", courseId)
                        .get()
                        .await()

                    if (!enrollmentQuery.isEmpty) {
                        val enrollmentDoc = enrollmentQuery.documents[0]
                        val enrollment = enrollmentDoc.toObject(CourseEnrollment::class.java)
                        if (enrollment != null) {
                            // Don't add section ID to completed lessons, just update the enrollment
                            val updatedEnrollment = enrollment.copy(
                                completedLessons = enrollment.completedLessons,
                                progress = calculateProgress(courseId, enrollment.completedLessons)
                            )
                            enrollmentDoc.reference.set(updatedEnrollment).await()
                        }
                    }
                }

                // Track quiz completion
                userActivityTracker.trackQuizCompletion(getCurrentUserId(), courseId, quizId)

                Log.d(TAG, "Quiz attempt saved successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving quiz attempt", e)
            }
        }
    }

    fun canTakeQuiz(courseId: String, sectionId: String): Flow<Boolean> = flow {
        var canTake = false
        try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                // First check if user is enrolled
                val enrollmentQuery = enrollmentsCollection
                    .whereEqualTo("studentId", currentUser.uid)
                    .whereEqualTo("courseId", courseId)
                    .get()
                    .await()

                if (!enrollmentQuery.isEmpty) {
                    // Get the enrollment
                    val enrollment = enrollmentQuery.documents[0].toObject(CourseEnrollment::class.java)
                    if (enrollment != null) {
                        // Get the course and section
                        val course = coursesCollection.document(courseId).get().await().toObject(Course::class.java)
                        val section = course?.sections?.find { it.id == sectionId }
                        
                        if (section != null) {
                            // Check if all lessons in the section are completed
                            val completedLessons = enrollment.completedLessons
                            canTake = section.lessons.all { lesson ->
                                completedLessons.contains(lesson.id)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking quiz eligibility", e)
        }
        emit(canTake)
    }

    private suspend fun calculateProgress(courseId: String, completedLessons: List<String>): Int {
        val course = coursesCollection.document(courseId).get().await().toObject(Course::class.java)
        val totalLessons = course?.sections?.sumOf { it.lessons.size } ?: 0
        return if (totalLessons > 0) {
            (completedLessons.size * 100) / totalLessons
        } else 0
    }

    fun getExternalResources(quizTitle: String, quizDescription: String, failedQuestions: List<Question>) {
        viewModelScope.launch {
            try {
                val questionTexts = failedQuestions.map { it.text }
                val resources = geminiService.generateLearningResources(
                    quizTitle = quizTitle,
                    quizDescription = quizDescription,
                    failedQuestions = questionTexts
                )
                _externalResources.value = resources
            } catch (e: Exception) {
                Log.e(TAG, "Error getting external resources", e)
                _externalResources.value = emptyList()
            }
        }
    }

    private fun generateCertificateNumber(): String {
        val year = java.time.LocalDate.now().year
        val random = (10000..99999).random()
        return "CERT-$year-$random"
    }
    
    private suspend fun generateCertificate(
        courseId: String,
        studentId: String
    ) {
        try {
            // Get course details
            val course = coursesCollection.document(courseId).get().await().toObject(Course::class.java)
            if (course == null) {
                Log.e(TAG, "Course not found for certificate generation")
                return
            }
            
            // Get student details
            val student = userRepository.getUserById(studentId)
            if (student == null) {
                Log.e(TAG, "Student not found for certificate generation")
                return
            }
            
            // Get instructor details
            val instructor = userRepository.getUserById(course.instructor)
            if (instructor == null) {
                Log.e(TAG, "Instructor not found for certificate generation")
                return
            }
            
            // Create certificate
            val certificate = Certificate(
                id = UUID.randomUUID().toString(),
                courseId = courseId,
                courseName = course.title,
                studentId = studentId,
                studentName = student.name,
                instructorId = course.instructor,
                instructorName = instructor.name,
                issueDate = System.currentTimeMillis(),
                certificateNumber = generateCertificateNumber()
            )
            
            // Save certificate to Firestore
            certificatesCollection.document(certificate.id).set(certificate).await()
            _certificate.value = certificate
            
            Log.d(TAG, "Certificate generated successfully: ${certificate.certificateNumber}")
        } catch (e: Exception) {
            Log.e(TAG, "Error generating certificate", e)
        }
    }
    
    fun getCertificate(certificateId: String) {
        viewModelScope.launch {
            try {
                val certificate = certificatesCollection.document(certificateId).get().await()
                    .toObject(Certificate::class.java)
                _certificate.value = certificate
            } catch (e: Exception) {
                Log.e(TAG, "Error getting certificate", e)
            }
        }
    }
    
    fun getUserCertificates(userId: String): Flow<List<Certificate>> = callbackFlow {
        if (userId.isEmpty()) {
            trySend(emptyList())
            return@callbackFlow
        }

        var listener: ListenerRegistration? = null
        try {
            listener = certificatesCollection
                .whereEqualTo("studentId", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val certificates = snapshot?.toObjects(Certificate::class.java) ?: emptyList()
                    trySend(certificates)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user certificates", e)
            trySend(emptyList())
        }

        awaitClose {
            listener?.remove()
        }
    }

    fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }

    fun ensureCertificatesForCompletedCourses() {
        val userId = getCurrentUserId()
        if (userId.isEmpty()) return

        viewModelScope.launch {
            // Get all enrollments for the user
            val enrollments = enrollmentsCollection.whereEqualTo("studentId", userId).get().await()
            for (enrollmentDoc in enrollments.documents) {
                val enrollment = enrollmentDoc.toObject(CourseEnrollment::class.java) ?: continue
                if (enrollment.progress == 100) {
                    // Check if certificate already exists
                    val certQuery = certificatesCollection
                        .whereEqualTo("studentId", userId)
                        .whereEqualTo("courseId", enrollment.courseId)
                        .get().await()
                    if (certQuery.isEmpty) {
                        // Generate certificate if missing
                        generateCertificate(enrollment.courseId, userId)
                    }
                }
            }
        }
    }

    data class CourseWithExtras(
        val course: Course,
        val progress: Int,
        val instructorName: String
    )

    fun getCoursesWithExtras(userId: String, onResult: (List<CourseWithExtras>) -> Unit) {
        viewModelScope.launch {
            val courses = _courses.value
            val result = mutableListOf<CourseWithExtras>()
            for (course in courses) {
                // Get progress
                val enrollmentSnap = enrollmentsCollection
                    .whereEqualTo("studentId", userId)
                    .whereEqualTo("courseId", course.id)
                    .get().await()
                val progress = enrollmentSnap.documents.firstOrNull()
                    ?.toObject(CourseEnrollment::class.java)?.progress ?: 0

                // Get instructor name
                val instructorSnap = db.collection("users").document(course.instructor).get().await()
                val instructorName = instructorSnap.getString("name") ?: course.instructor

                result.add(CourseWithExtras(course, progress, instructorName))
            }
            onResult(result)
        }
    }

    fun updateCourseInfo(courseId: String, description: String, category: String) {
        viewModelScope.launch {
            try {
                val course = _selectedCourse.value ?: return@launch
                val updatedCourse = course.copy(description = description, category = category)
                coursesCollection.document(courseId).set(updatedCourse).await()
                _selectedCourse.value = updatedCourse
            } catch (e: Exception) {
                Log.e(TAG, "Error updating course info", e)
            }
        }
    }

    fun deleteCourse(courseId: String) {
        viewModelScope.launch {
            try {
                coursesCollection.document(courseId).delete().await()
                // Remove from local state
                _selectedCourse.value = null
                _instructorCourses.value = _instructorCourses.value.filter { it.id != courseId }
                _courses.value = _courses.value.filter { it.id != courseId }
                Log.d(TAG, "Course deleted successfully: $courseId")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting course", e)
            }
        }
    }

    fun updateQuizDescription(courseId: String, sectionId: String, quizId: String, description: String) {
        viewModelScope.launch {
            try {
                val course = _selectedCourse.value ?: return@launch
                val updatedSections = course.sections.map { section ->
                    if (section.id == sectionId) {
                        val updatedQuizzes = section.quizzes.map { quiz ->
                            if (quiz.id == quizId) quiz.copy(description = description) else quiz
                        }
                        section.copy(quizzes = updatedQuizzes)
                    } else section
                }
                val updatedCourse = course.copy(sections = updatedSections)
                coursesCollection.document(courseId).set(updatedCourse).await()
                _selectedCourse.value = updatedCourse
            } catch (e: Exception) {
                Log.e(TAG, "Error updating quiz description", e)
            }
        }
    }

    fun deleteQuestionFromQuiz(courseId: String, sectionId: String, quizId: String, questionId: String) {
        viewModelScope.launch {
            try {
                val course = _selectedCourse.value ?: return@launch
                val updatedSections = course.sections.map { section ->
                    if (section.id == sectionId) {
                        val updatedQuizzes = section.quizzes.map { quiz ->
                            if (quiz.id == quizId) quiz.copy(questions = quiz.questions.filter { it.id != questionId }) else quiz
                        }
                        section.copy(quizzes = updatedQuizzes)
                    } else section
                }
                val updatedCourse = course.copy(sections = updatedSections)
                coursesCollection.document(courseId).set(updatedCourse).await()
                _selectedCourse.value = updatedCourse
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting question from quiz", e)
            }
        }
    }

    private suspend fun updateUserXP(userId: String, xpToAdd: Int) {
        try {
            val userRef = db.collection("users").document(userId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val user = snapshot.toObject(User::class.java)
                if (user != null) {
                    val updatedUser = user.copy(xp = user.xp + xpToAdd)
                    transaction.set(userRef, updatedUser)
                }
            }.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user XP", e)
        }
    }

    fun completeSection(courseId: String, sectionId: String) {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId()
                if (userId.isEmpty()) return@launch

                // Update enrollment progress
                val enrollmentRef = enrollmentsCollection.document("${userId}_${courseId}")
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(enrollmentRef)
                    val enrollment = snapshot.toObject(CourseEnrollment::class.java)
                    if (enrollment != null) {
                        val updatedEnrollment = enrollment.copy(
                            progress = enrollment.progress + 1
                        )
                        transaction.set(enrollmentRef, updatedEnrollment)
                    }
                }.await()

                // Add XP for completing the section
                updateUserXP(userId, 2) // Add 2 XP for completing a section

            } catch (e: Exception) {
                Log.e(TAG, "Error completing section", e)
            }
        }
    }

    fun loadDownloadedCourses() {
        viewModelScope.launch {
            try {
                val courses = local_db.courseDao().getAllDownloadedCourses()
                val downloadedCourses = courses.map { courseEntity ->
                    val sections = local_db.courseDao().getSectionsForCourse(courseEntity.id)
                    val courseSections = sections.map { sectionEntity ->
                        val lessons = local_db.courseDao().getLessonsForSection(sectionEntity.id)
                        CourseSection(
                            id = sectionEntity.id,
                            title = sectionEntity.title,
                            lessons = lessons.map { lessonEntity ->
                                Lesson(
                                    id = lessonEntity.id,
                                    title = lessonEntity.title,
                                    description = lessonEntity.description,
                                    duration = lessonEntity.duration,
                                    videoUrl = lessonEntity.localVideoPath ?: "",
                                    pdfUrl = lessonEntity.localPdfPath ?: "",
                                    imageUrl = lessonEntity.localImagePath ?: ""
                                )
                            }
                        )
                    }
                    Course(
                        id = courseEntity.id,
                        title = courseEntity.title,
                        description = courseEntity.description,
                        instructor = courseEntity.instructor,
                        category = courseEntity.category,
                        sections = courseSections
                    )
                }
                _downloadedCourses.value = downloadedCourses
            } catch (e: Exception) {
                Log.e(TAG, "Error loading downloaded courses", e)
            }
        }
    }

    fun downloadCourse(course: Course) {
        viewModelScope.launch {
            try {
                _isDownloading.value = true
                _downloadProgress.value = 0f

                // Save course
                val courseEntity = CourseEntity(
                    id = course.id,
                    title = course.title,
                    description = course.description,
                    instructor = course.instructor,
                    category = course.category
                )
                local_db.courseDao().insertCourse(courseEntity)

                // Save sections
                val sectionEntities = course.sections.mapIndexed { index, section ->
                    SectionEntity(
                        id = section.id,
                        courseId = course.id,
                        title = section.title,
                        order = index
                    )
                }
                local_db.courseDao().insertSections(sectionEntities)

                // Calculate total items to download
                var totalItems = 0
                course.sections.forEach { section ->
                    section.lessons.forEach { lesson ->
                        if (lesson.videoUrl.isNotEmpty()) totalItems++
                        if (lesson.pdfUrl.isNotEmpty()) totalItems++
                        if (lesson.imageUrl.isNotEmpty()) totalItems++
                    }
                }
                var downloadedItems = 0

                // Download lessons and their content
                course.sections.forEach { section ->
                    section.lessons.forEachIndexed { index, lesson ->
                        val lessonEntity = LessonEntity(
                            id = lesson.id,
                            sectionId = section.id,
                            courseId = course.id,
                            title = lesson.title,
                            description = lesson.description,
                            duration = lesson.duration,
                            order = index
                        )

                        // Download video if exists
                        if (lesson.videoUrl.isNotEmpty()) {
                            val videoPath = downloadFile(lesson.videoUrl, "${course.id}_${lesson.id}_video.mp4")
                            lessonEntity.localVideoPath = videoPath
                            downloadedItems++
                            _downloadProgress.value = downloadedItems.toFloat() / totalItems
                        }

                        // Download PDF if exists
                        if (lesson.pdfUrl.isNotEmpty()) {
                            val pdfPath = downloadFile(lesson.pdfUrl, "${course.id}_${lesson.id}_pdf.pdf")
                            lessonEntity.localPdfPath = pdfPath
                            downloadedItems++
                            _downloadProgress.value = downloadedItems.toFloat() / totalItems
                        }

                        // Download image if exists
                        if (lesson.imageUrl.isNotEmpty()) {
                            val imagePath = downloadFile(lesson.imageUrl, "${course.id}_${lesson.id}_image.jpg")
                            lessonEntity.localImagePath = imagePath
                            downloadedItems++
                            _downloadProgress.value = downloadedItems.toFloat() / totalItems
                        }

                        local_db.courseDao().insertLesson(lessonEntity)
                    }
                }

                // Update downloaded courses list
                loadDownloadedCourses()
            } catch (e: Exception) {
                Log.e(TAG, "Error downloading course", e)
                throw e
            } finally {
                _isDownloading.value = false
                _downloadProgress.value = 0f
            }
        }
    }

    private suspend fun downloadFile(url: String, fileName: String): String {
        val storageRef = storage.getReferenceFromUrl(url)
        val localFile = File(getApplication<Application>().getExternalFilesDir(null), fileName)
        
        // Create parent directories if they don't exist
        localFile.parentFile?.mkdirs()
        
        // Download file
        storageRef.getFile(localFile).await()
        
        // Verify file exists and has content
        if (!localFile.exists() || localFile.length() == 0L) {
            throw Exception("Failed to download file: $fileName")
        }
        
        return localFile.absolutePath
    }

    fun removeDownloadedCourse(courseId: String) {
        viewModelScope.launch {
            try {
                // Get all lessons to delete their files
                val lessons = local_db.courseDao().getLessonsForCourse(courseId)
                
                // Delete all downloaded files
                lessons.forEach { lesson ->
                    lesson.localVideoPath?.let { File(it).delete() }
                    lesson.localPdfPath?.let { File(it).delete() }
                    lesson.localImagePath?.let { File(it).delete() }
                }

                // Delete from database
                local_db.courseDao().deleteLessonsForCourse(courseId)
                local_db.courseDao().deleteSectionsForCourse(courseId)
                local_db.courseDao().deleteCourseById(courseId)
                
                loadDownloadedCourses()
            } catch (e: Exception) {
                Log.e(TAG, "Error removing downloaded course", e)
            }
        }
    }

    suspend fun getLocalVideoPath(courseId: String, lessonId: String): String? {
        return try {
            val lesson = local_db.courseDao().getLessonsForCourse(courseId)
                .find { it.id == lessonId }
            lesson?.localVideoPath?.let { path ->
                if (File(path).exists()) path else null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting local video path", e)
            null
        }
    }

    suspend fun getLocalPdfPath(courseId: String, lessonId: String): String? {
        return try {
            val lesson = local_db.courseDao().getLessonsForCourse(courseId)
                .find { it.id == lessonId }
            lesson?.localPdfPath?.let { path ->
                if (File(path).exists()) path else null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting local PDF path", e)
            null
        }
    }

    suspend fun getLocalImagePath(courseId: String, lessonId: String): String? {
        return try {
            val lesson = local_db.courseDao().getLessonsForCourse(courseId)
                .find { it.id == lessonId }
            lesson?.localImagePath?.let { path ->
                if (File(path).exists()) path else null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting local image path", e)
            null
        }
    }

    fun checkUserEngagement() {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId()
                if (userId.isNotEmpty()) {
                    // Check for inactive users
                    userActivityTracker.checkInactiveUsers()
                    
                    // Check for incomplete quizzes
                    userActivityTracker.checkIncompleteQuizzes(userId)
                    
                    // Track course progress for resume notifications
                    userActivityTracker.trackCourseProgress(userId, _selectedCourse.value?.id ?: "")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking user engagement", e)
            }
        }
    }
} 
