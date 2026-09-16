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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.DepartmentEntity
import com.example.data.model.StudentEntity
import com.example.data.model.UserAccountEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CampusAmber
import com.example.ui.theme.CampusNavyPrimary
import com.example.ui.theme.CampusTeal
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AttendanceManagementScreen(
    departments: List<DepartmentEntity>,
    students: List<StudentEntity>,
    attendanceHistory: List<AttendanceRecordEntity>,
    onSaveAttendance: (deptId: String, subjectCode: String, subjectName: String, date: String, statusMap: Map<String, String>) -> Unit,
    currentUserAccount: UserAccountEntity? = null,
    modifier: Modifier = Modifier
) {
    val isFacultyDeptLocked = currentUserAccount?.role == "FACULTY" && currentUserAccount.departmentId != "ALL"
    val defaultDept = if (isFacultyDeptLocked) {
        currentUserAccount!!.departmentId
    } else {
        departments.firstOrNull()?.id ?: "CSE"
    }

    var selectedTab by remember { mutableStateOf(0) } // 0: Mark Attendance, 1: History Log
    var selectedDeptId by remember(currentUserAccount) { mutableStateOf(defaultDept) }
    var selectedSubjectCode by remember { mutableStateOf("CS501") }
    var selectedSubjectName by remember { mutableStateOf("Database Management Systems") }
    val todayDate = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
    var attendanceDate by remember { mutableStateOf(todayDate) }

    // Department students for attendance
    val deptStudents = students.filter { it.departmentId == selectedDeptId }

    // State map: studentId -> "PRESENT", "ABSENT", "LATE", "EXCUSED"
    val studentStatusMap = remember(selectedDeptId, deptStudents) {
        mutableStateMapOf<String, String>().apply {
            deptStudents.forEach { student ->
                put(student.id, "PRESENT")
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = CampusNavyPrimary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mark Attendance", fontWeight = FontWeight.SemiBold)
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Session Logs (${attendanceHistory.size})", fontWeight = FontWeight.SemiBold)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTab == 0) {
            // MARK ATTENDANCE VIEW
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Config card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Class & Lecture Selection",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Department picker chips
                            if (isFacultyDeptLocked) {
                                Surface(
                                    color = CampusNavyPrimary.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = CampusNavyPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Assigned Department: $selectedDeptId (Staff access scoped)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = CampusNavyPrimary
                                        )
                                    }
                                }
                            } else {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    departments.forEach { dept ->
                                        val isSelected = selectedDeptId == dept.id
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { selectedDeptId = dept.id },
                                            label = { Text(dept.id) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = CampusNavyPrimary,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Subject Picker quick chips
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                val sampleSubjects = when (selectedDeptId) {
                                    "CSE" -> listOf("CS501" to "DBMS", "CS502" to "OS", "CS504" to "Networks")
                                    "ECE" -> listOf("EC301" to "Signals", "EC302" to "Analog Circuits")
                                    "IT" -> listOf("IT401" to "Web Tech", "IT402" to "Cloud Arch")
                                    else -> listOf("ME301" to "Thermodynamics", "ME302" to "CAD/CAM")
                                }

                                sampleSubjects.forEach { (code, name) ->
                                    val isSelected = selectedSubjectCode == code
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedSubjectCode = code
                                            selectedSubjectName = name
                                        },
                                        label = { Text("$code ($name)") },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = CampusTeal,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = attendanceDate,
                                    onValueChange = { attendanceDate = it },
                                    label = { Text("Date (YYYY-MM-DD)") },
                                    singleLine = true,
                                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                    modifier = Modifier.weight(1f)
                                )

                                Button(
                                    onClick = {
                                        // Mark all present
                                        deptStudents.forEach { studentStatusMap[it.id] = "PRESENT" }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("All Present", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                // Summary Counter Bar
                item {
                    val presentCount = deptStudents.count { studentStatusMap[it.id] == "PRESENT" || studentStatusMap[it.id] == "EXCUSED" }
                    val absentCount = deptStudents.count { studentStatusMap[it.id] == "ABSENT" }
                    val lateCount = deptStudents.count { studentStatusMap[it.id] == "LATE" }
                    val rate = if (deptStudents.isNotEmpty()) {
                        Math.round((presentCount.toDouble() / deptStudents.size) * 1000.0) / 10.0
                    } else 0.0

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Enrolled", style = MaterialTheme.typography.labelSmall)
                                Text("${deptStudents.size}", fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Present", style = MaterialTheme.typography.labelSmall, color = StatusSuccess)
                                Text("$presentCount", fontWeight = FontWeight.Bold, color = StatusSuccess)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Absent", style = MaterialTheme.typography.labelSmall, color = StatusDanger)
                                Text("$absentCount", fontWeight = FontWeight.Bold, color = StatusDanger)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Late", style = MaterialTheme.typography.labelSmall, color = CampusAmber)
                                Text("$lateCount", fontWeight = FontWeight.Bold, color = CampusAmber)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Attendance %", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    "$rate%",
                                    fontWeight = FontWeight.Bold,
                                    color = if (rate >= 75.0) StatusSuccess else StatusDanger
                                )
                            }
                        }
                    }
                }

                // Student Attendance Roster
                items(deptStudents) { student ->
                    val currentStatus = studentStatusMap[student.id] ?: "PRESENT"

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("attendance_row_${student.rollNo}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(CampusNavyPrimary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = student.fullName.take(1),
                                        fontWeight = FontWeight.Bold,
                                        color = CampusNavyPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = student.fullName,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                    Text(
                                        text = "${student.rollNo} · Current: ${student.attendancePercentage}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (student.attendancePercentage < 75.0) StatusDanger else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Quick status selector buttons
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf(
                                    "PRESENT" to ("P" to StatusSuccess),
                                    "ABSENT" to ("A" to StatusDanger),
                                    "LATE" to ("L" to CampusAmber),
                                    "EXCUSED" to ("E" to Color(0xFF6366F1))
                                ).forEach { (statusKey, pair) ->
                                    val isSelected = currentStatus == statusKey
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) pair.second else MaterialTheme.colorScheme.surfaceVariant)
                                            .clickable { studentStatusMap[student.id] = statusKey },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = pair.first,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Save Attendance Button
                item {
                    Button(
                        onClick = {
                            onSaveAttendance(
                                selectedDeptId,
                                selectedSubjectCode,
                                selectedSubjectName,
                                attendanceDate,
                                studentStatusMap.toMap()
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_attendance_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CampusNavyPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save & Submit Attendance Record", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // ATTENDANCE LOG / HISTORY VIEW
            if (attendanceHistory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No attendance records logged yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(attendanceHistory) { record ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${record.subjectCode} - ${record.subjectName}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                    Text(
                                        text = "Student: ${record.studentId} · Dept: ${record.departmentId} · Date: ${record.date}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                StatusBadge(status = record.status)
                            }
                        }
                    }
                }
            }
        }
    }
}
