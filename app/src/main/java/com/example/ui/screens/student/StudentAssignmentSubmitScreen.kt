package com.example.ui.screens.student

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Send
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
import com.example.data.model.StudentEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CampusAmber
import com.example.ui.theme.CampusNavyPrimary
import com.example.ui.theme.CampusTeal
import com.example.ui.theme.StatusSuccess

@Composable
fun StudentAssignmentSubmitScreen(
    student: StudentEntity?,
    assignments: List<AssignmentEntity>,
    mySubmissions: List<AssignmentSubmissionEntity>,
    onSubmitAssignment: (assignmentId: Long, content: String, attachmentName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var filterTab by remember { mutableStateOf("ALL") } // "ALL", "DUE", "SUBMITTED"
    var assignmentToSubmit by remember { mutableStateOf<AssignmentEntity?>(null) }

    if (student == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No student record selected.")
        }
        return
    }

    // Filter assignments for student's department
    val deptAssignments = assignments.filter { it.departmentId == student.departmentId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterTab == "ALL",
                onClick = { filterTab = "ALL" },
                label = { Text("All Assignments (${deptAssignments.size})") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CampusNavyPrimary,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = filterTab == "DUE",
                onClick = { filterTab = "DUE" },
                label = {
                    val dueCount = deptAssignments.count { assign -> mySubmissions.none { it.assignmentId == assign.id } }
                    Text("Pending Submission ($dueCount)")
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CampusAmber,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = filterTab == "SUBMITTED",
                onClick = { filterTab = "SUBMITTED" },
                label = { Text("Turned In (${mySubmissions.size})") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = StatusSuccess,
                    selectedLabelColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (deptAssignments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No assignments active for your department.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(deptAssignments) { assign ->
                    val mySub = mySubmissions.find { it.assignmentId == assign.id }
                    val isSubmitted = mySub != null

                    val matchesFilter = when (filterTab) {
                        "DUE" -> !isSubmitted
                        "SUBMITTED" -> isSubmitted
                        else -> true
                    }

                    if (matchesFilter) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("assignment_card_${assign.id}"),
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
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
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
                                                text = assign.title,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "${assign.subjectCode} - ${assign.subjectName} · Max: ${assign.totalMarks} pts",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    StatusBadge(status = mySub?.status ?: "PENDING")
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = assign.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = CampusAmber,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Deadline: ${assign.dueDate}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CampusAmber
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                if (mySub != null) {
                                    // Submitted details
                                    Surface(
                                        color = if (mySub.status == "GRADED") StatusSuccess.copy(alpha = 0.1f) else CampusNavyPrimary.copy(alpha = 0.08f),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "Turned In: ${mySub.submissionDate.take(10)}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                if (mySub.marksObtained != null) {
                                                    Text(
                                                        text = "Score: ${mySub.marksObtained} / ${assign.totalMarks}",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = StatusSuccess
                                                    )
                                                }
                                            }

                                            if (mySub.attachmentName.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(12.dp), tint = CampusTeal)
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Text(mySub.attachmentName, fontSize = 11.sp, color = CampusTeal)
                                                }
                                            }

                                            if (mySub.facultyFeedback != null) {
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = "Faculty Review: \"${mySub.facultyFeedback}\"",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = { assignmentToSubmit = assign },
                                        colors = ButtonDefaults.buttonColors(containerColor = CampusNavyPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Submit Solution", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Submission Dialog
    assignmentToSubmit?.let { assign ->
        var contentText by remember { mutableStateOf("") }
        var attachmentName by remember {
            mutableStateOf("${student.rollNo.lowercase()}_${assign.subjectCode.lowercase()}_assignment.zip")
        }

        AlertDialog(
            onDismissRequest = { assignmentToSubmit = null },
            title = {
                Text(
                    text = "Submit ${assign.title}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Subject: ${assign.subjectCode} - ${assign.subjectName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = contentText,
                        onValueChange = { contentText = it },
                        label = { Text("Write Submission Summary / Answer Text *") },
                        placeholder = { Text("e.g. Implemented B+ Tree algorithm with dynamic node splitting...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = attachmentName,
                        onValueChange = { attachmentName = it },
                        label = { Text("Attachment File Name") },
                        leadingIcon = { Icon(Icons.Default.AttachFile, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (contentText.isNotBlank()) {
                            onSubmitAssignment(assign.id, contentText, attachmentName)
                            assignmentToSubmit = null
                        }
                    },
                    enabled = contentText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = CampusNavyPrimary)
                ) {
                    Text("Turn In")
                }
            },
            dismissButton = {
                TextButton(onClick = { assignmentToSubmit = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
