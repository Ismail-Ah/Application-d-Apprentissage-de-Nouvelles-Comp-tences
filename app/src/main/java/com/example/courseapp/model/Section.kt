package com.example.courseapp.model
 
data class Section(
    val id: String,
    val title: String,
    val lessons: List<Lesson>
) 
