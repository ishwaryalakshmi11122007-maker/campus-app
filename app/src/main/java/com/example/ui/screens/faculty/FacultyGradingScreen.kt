package com.example.ui.screens.faculty

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Grading
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssignmentEntity
import com.example.data.model.AssignmentSubmissionEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CampusAmber
import com.example.ui.theme.CampusNavyPrimary
import com.example.ui.theme.CampusTeal
import com.example.ui.theme.StatusSuccess

@Composable
fun FacultyGradingScreen(
    assignments: List<AssignmentEntity>,
    submissions: List<AssignmentSubmissionEntity>,
    onGradeSubmission: (submissionId: Long, marks: Double, feedback: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var statusFilter by remember { mutableStateOf("ALL") } // "ALL", "SUBMITTED", "GRADED"
    var submissionToGrade by remember { mutableStateOf<AssignmentSubmissionEntity?>(null) }

    val filteredSubmissions = submissions.filter { sub ->
        when (statusFilter) {
            "SUBMITTED" -> sub.status == "SUBMITTED"
            "GRADED" -> sub.status == "GRADED"
            else -> true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Status Filter row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = statusFilter == "ALL",
                onClick = { statusFilter = "ALL" },
                label = { Text("All (${submissions.size})") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CampusNavyPrimary,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = statusFilter == "SUBMITTED",
                onClick = { statusFilter = "SUBMITTED" },
                label = {
                    val count = submissions.count { it.status == "SUBMITTED" }
                    Text("Needs Grading ($count)")
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CampusAmber,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = statusFilter == "GRADED",
                onClick = { statusFilter = "GRADED" },
                label = {
                    val count = submissions.count { it.status == "GRADED" }
                    Text("Evaluated ($count)")
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = StatusSuccess,
                    selectedLabelColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filteredSubmissions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No assignment submissions in this category.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredSubmissions) { sub ->
                    val assignment = assignments.find { it.id == sub.assignmentId }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submission_card_${sub.id}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(1.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(CampusNavyPrimary.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Assignment,
                                            contentDescription = null,
                                            tint = CampusNavyPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = assignment?.title ?: "Assignment #${sub.assignmentId}",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "By ${sub.studentName} (${sub.studentRollNo})",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                StatusBadge(status = sub.status)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Student Answer & Attachment
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = sub.contentText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    if (sub.attachmentName.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.AttachFile,
                                                contentDescription = "Attachment",
                                                tint = CampusTeal,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = sub.attachmentName,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = CampusTeal
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // If graded, show marks and feedback
                            if (sub.status == "GRADED" && sub.marksObtained != null) {
                                Surface(
                                    color = StatusSuccess.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Faculty Feedback:",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = StatusSuccess
                                            )
                                            Text(
                                                text = sub.facultyFeedback ?: "Graded satisfactorily.",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }

                                        Text(
                                            text = "${sub.marksObtained} / ${assignment?.totalMarks ?: 100.0}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = StatusSuccess
                                        )
                                    }
                                }
                            } else {
                                // Needs grading button
                                Button(
                                    onClick = { submissionToGrade = sub },
                                    colors = ButtonDefaults.buttonColors(containerColor = CampusNavyPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RateReview,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Evaluate & Assign Grade", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Grade Evaluation Dialog
    submissionToGrade?.let { sub ->
        val assignment = assignments.find { it.id == sub.assignmentId }
        var marksText by remember { mutableStateOf("") }
        var feedbackText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { submissionToGrade = null },
            title = {
                Text(
                    text = "Evaluate Submission",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Student: ${sub.studentName} (${sub.studentRollNo})",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "Assignment: ${assignment?.title ?: ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = marksText,
                        onValueChange = { marksText = it },
                        label = { Text("Marks Obtained (Max: ${assignment?.totalMarks ?: 100.0})") },
                        placeholder = { Text("e.g. 23.5") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = feedbackText,
                        onValueChange = { feedbackText = it },
                        label = { Text("Faculty Qualitative Feedback") },
                        placeholder = { Text("e.g. Excellent logic, well structured code.") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val marks = marksText.toDoubleOrNull() ?: 0.0
                        onGradeSubmission(sub.id, marks, feedbackText)
                        submissionToGrade = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CampusNavyPrimary)
                ) {
                    Text("Post Grade & Feedback")
                }
            },
            dismissButton = {
                TextButton(onClick = { submissionToGrade = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
