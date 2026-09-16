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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.example.data.model.AcademicResultEntity
import com.example.data.model.DepartmentEntity
import com.example.data.model.StudentEntity
import com.example.data.model.UserAccountEntity
import com.example.ui.components.DepartmentFilterRow
import com.example.ui.theme.CampusAmber
import com.example.ui.theme.CampusNavyPrimary
import com.example.ui.theme.CampusTeal
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess

@Composable
fun MarksManagementScreen(
    students: List<StudentEntity>,
    departments: List<DepartmentEntity>,
    results: List<AcademicResultEntity>,
    onAddResult: (studentId: String, semester: Int, code: String, name: String, credits: Int, internal: Double, external: Double, session: String) -> Unit,
    currentUserAccount: UserAccountEntity? = null,
    modifier: Modifier = Modifier
) {
    var showAddMarkDialog by remember { mutableStateOf(false) }
    val initialDept = if (currentUserAccount != null && currentUserAccount.role == "FACULTY" && currentUserAccount.departmentId != "ALL") {
        currentUserAccount.departmentId
    } else {
        "ALL"
    }
    var selectedDeptId by remember { mutableStateOf(initialDept) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredResults = results.filter { res ->
        val student = students.find { it.id == res.studentId }
        val matchesDept = selectedDeptId == "ALL" || student?.departmentId == selectedDeptId
        val matchesSearch = searchQuery.isBlank() ||
                res.subjectCode.contains(searchQuery, ignoreCase = true) ||
                res.subjectName.contains(searchQuery, ignoreCase = true) ||
                (student?.fullName?.contains(searchQuery, ignoreCase = true) == true) ||
                (student?.rollNo?.contains(searchQuery, ignoreCase = true) == true)
        matchesDept && matchesSearch
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddMarkDialog = true },
                containerColor = CampusNavyPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("enter_marks_fab")
            ) {
                Icon(Icons.Default.PostAdd, contentDescription = "Enter Marks")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search & Filters
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by student, roll no, or subject...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )

            DepartmentFilterRow(
                departments = departments,
                selectedDeptId = selectedDeptId,
                onSelectDept = { selectedDeptId = it },
                currentUserAccount = currentUserAccount,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Marks & Exam Results Records (${filteredResults.size})",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            if (filteredResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No exam results found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredResults) { res ->
                        val student = students.find { it.id == res.studentId }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(1.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${res.subjectCode} - ${res.subjectName}",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "${student?.fullName ?: res.studentId} (${student?.rollNo ?: ""}) · Sem ${res.semester} · ${res.credits} Credits",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when (res.grade) {
                                                    "A+", "A" -> StatusSuccess
                                                    "B+", "B" -> CampusTeal
                                                    "C", "P" -> CampusAmber
                                                    else -> StatusDanger
                                                }
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = res.grade,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Internal", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("${res.internalMarks}/40", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("External", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("${res.externalMarks}/60", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Total", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("${res.totalMarks}/100", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Grade Points", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("${res.gradePoints}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CampusAmber)
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

    if (showAddMarkDialog) {
        val eligibleStudents = if (currentUserAccount != null && currentUserAccount.role == "FACULTY" && currentUserAccount.departmentId != "ALL") {
            students.filter { it.departmentId == currentUserAccount.departmentId }
        } else {
            students
        }
        AddMarkDialog(
            students = eligibleStudents,
            onDismiss = { showAddMarkDialog = false },
            onConfirm = { studentId, sem, code, name, credits, internal, external, session ->
                onAddResult(studentId, sem, code, name, credits, internal, external, session)
                showAddMarkDialog = false
            }
        )
    }
}

@Composable
fun AddMarkDialog(
    students: List<StudentEntity>,
    onDismiss: () -> Unit,
    onConfirm: (studentId: String, semester: Int, code: String, name: String, credits: Int, internal: Double, external: Double, session: String) -> Unit
) {
    var selectedStudentId by remember { mutableStateOf(students.firstOrNull()?.id ?: "") }
    var semesterText by remember { mutableStateOf("5") }
    var subjectCode by remember { mutableStateOf("CS501") }
    var subjectName by remember { mutableStateOf("Database Management Systems") }
    var creditsText by remember { mutableStateOf("4") }
    var internalMarksText by remember { mutableStateOf("35.0") }
    var externalMarksText by remember { mutableStateOf("52.0") }
    var examSession by remember { mutableStateOf("Spring 2026") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Enter Subject Marks & Result",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Student selector
                Text(
                    text = "Select Student:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LazyColumn(modifier = Modifier.height(110.dp)) {
                    items(students) { student ->
                        val isSelected = selectedStudentId == student.id
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) CampusNavyPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedStudentId = student.id }
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "${student.fullName} (${student.rollNo}) - ${student.departmentId}",
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = subjectCode,
                        onValueChange = { subjectCode = it },
                        label = { Text("Code") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = semesterText,
                        onValueChange = { semesterText = it },
                        label = { Text("Sem") },
                        modifier = Modifier.weight(0.6f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = creditsText,
                        onValueChange = { creditsText = it },
                        label = { Text("Credits") },
                        modifier = Modifier.weight(0.6f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = subjectName,
                    onValueChange = { subjectName = it },
                    label = { Text("Subject Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = internalMarksText,
                        onValueChange = { internalMarksText = it },
                        label = { Text("Internal (/40)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = externalMarksText,
                        onValueChange = { externalMarksText = it },
                        label = { Text("External (/60)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = examSession,
                    onValueChange = { examSession = it },
                    label = { Text("Exam Session") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sem = semesterText.toIntOrNull() ?: 1
                    val cred = creditsText.toIntOrNull() ?: 3
                    val intMarks = internalMarksText.toDoubleOrNull() ?: 0.0
                    val extMarks = externalMarksText.toDoubleOrNull() ?: 0.0
                    onConfirm(selectedStudentId, sem, subjectCode, subjectName, cred, intMarks, extMarks, examSession)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CampusNavyPrimary)
            ) {
                Text("Post Marks")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
