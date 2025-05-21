package com.example.courseapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.courseapp.model.Course

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseCard(
    course: Course,
    progress: Int = 0,
    instructorName: String = "",
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null
) {
    // Pick a color based on course category or fallback
    val bgColor = when (course.category.lowercase()) {
        "development" -> Color(0xFFE3F2FD) // Light Blue
        "design" -> Color(0xFFF3E5F5) // Light Purple
        "business" -> Color(0xFFE8F5E9) // Light Green
        "marketing" -> Color(0xFFFFEBEE) // Light Red
        else -> when (course.title.hashCode() % 4) {
            0 -> Color(0xFFE3F2FD) // Light Blue
            1 -> Color(0xFFF3E5F5) // Light Purple
            2 -> Color(0xFFE8F5E9) // Light Green
            else -> Color(0xFFFFEBEE) // Light Red
        }
    }
    
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Progress pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.9f))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Learning Progress",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color.Black
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "$progress%",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.Black
                        )
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                .clickable { onClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Go",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = Color.Black
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "By $instructorName",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(12.dp))
                    Row {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.White.copy(alpha = 0.9f),
                            shadowElevation = 2.dp
                        ) {
                            Text(
                                text = course.duration,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.White.copy(alpha = 0.9f),
                            shadowElevation = 2.dp
                        ) {
                            Text(
                                text = "${course.sections.sumOf { it.lessons.size }} lessons",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                    if (trailingContent != null) {
                        Spacer(Modifier.width(8.dp))
                        trailingContent()
                    }
                }
                Spacer(Modifier.width(16.dp))
                // Right: Image
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = Color.Black.copy(alpha = 0.1f)
                        )
                ) {
                    AsyncImage(
                        model = course.imageUrl,
                        contentDescription = course.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                }
            }
        }
    }
}
