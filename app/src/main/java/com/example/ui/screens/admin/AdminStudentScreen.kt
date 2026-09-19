package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.DepartmentEntity
import com.example.data.model.StudentEntity
import com.example.ui.theme.CampusAmber
import com.example.ui.theme.CampusNavyDark
import com.example.ui.theme.CampusNavyPrimary
import com.example.ui.theme.CampusTeal
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess

@Composable
fun AdminStudentScreen(
    students: List<StudentEntity>,
    departments: List<DepartmentEntity>,
    onEnrollStudent: (
        rollNo: String,
        fullName: String,
        email: String,
        phone: String,
        departmentId: String,
        semester: Int,
        section: String,
        cgpa: Double,
        attendancePercentage: Double,
        guardianContact: String,
        createAccount: Boolean,
        tempPasswordRaw: String?,
        (Boolean, String) -> Unit
    ) -> Unit,
    onUpdateStudent: (StudentEntity, (Boolean, String) -> Unit) -> Unit,
    onToggleStatus: (StudentEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedDept by remember { mutableStateOf("ALL") }
    var selectedStatus by remember { mutableStateOf("ALL") } // ALL, ACTIVE, AT_RISK, INACTIVE
    var showEnrollDialog by remember { mutableStateOf(false) }
    var editingStudent by remember { mutableStateOf<StudentEntity?>(null) }
    var statusConfirmStudent by remember { mutableStateOf<StudentEntity?>(null) }

    val activeDepartments = departments.filter { it.status == "ACTIVE" }

    val filteredStudents = students.filter { student ->
        val matchesSearch = student.fullName.contains(searchQuery, ignoreCase = true) ||
                student.rollNo.contains(searchQuery, ignoreCase = true) ||
                student.email.contains(searchQuery, ignoreCase = true)
        val matchesDept = selectedDept == "ALL" || student.departmentId == selectedDept
        val matchesStatus = when (selectedStatus) {
            "ACTIVE" -> student.status == "ACTIVE"
            "AT_RISK" -> student.status == "AT_RISK"
            "INACTIVE" -> student.status == "INACTIVE"
            else -> true
        }
        matchesSearch && matchesDept && matchesStatus
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showEnrollDialog = true },
                containerColor = CampusNavyPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("admin_enroll_student_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Enroll Student")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Student Directory & Enrollment",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = CampusNavyDark
                        )
                        Text(
                            text = "${filteredStudents.size} of ${students.size} students shown",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { showEnrollDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CampusNavyPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Enroll", fontSize = 12.sp)
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name, roll no, or email...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = CampusNavyPrimary)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Department Filter Row
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        FilterChip(
                            selected = selectedDept == "ALL",
                            onClick = { selectedDept = "ALL" },
                            label = { Text("All Depts", fontSize = 11.sp) }
                        )
                    }
                    items(departments) { dept ->
                        FilterChip(
                            selected = selectedDept == dept.code,
                            onClick = { selectedDept = dept.code },
                            label = { Text(dept.code, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Status Filter Row
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("ALL", "ACTIVE", "AT_RISK", "INACTIVE").forEach { status ->
                        FilterChip(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status },
                            label = { Text(status, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (status) {
                                    "ACTIVE" -> StatusSuccess
                                    "AT_RISK" -> CampusAmber
                                    "INACTIVE" -> StatusDanger
                                    else -> CampusNavyPrimary
                                },
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            if (filteredStudents.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No students match current filters",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredStudents) { student ->
                    AdminStudentCardItem(
                        student = student,
                        onEdit = { editingStudent = student },
                        onToggleStatus = { statusConfirmStudent = student }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(56.dp))
            }
        }
    }

    // Enroll Student Dialog
    if (showEnrollDialog) {
        EnrollStudentAdminDialog(
            activeDepartments = activeDepartments,
            onDismiss = { showEnrollDialog = false },
            onConfirm = { roll, name, email, phone, dept, sem, sec, cgpa, att, guardian, createAcc, pass, callback ->
                onEnrollStudent(roll, name, email, phone, dept, sem, sec, cgpa, att, guardian, createAcc, pass) { success, msg ->
                    callback(success, msg)
                    if (success) showEnrollDialog = false
                }
            }
        )
    }

    // Edit Student Dialog
    editingStudent?.let { student ->
        EditStudentAdminDialog(
            student = student,
            departments = departments,
            onDismiss = { editingStudent = null },
            onConfirm = { updated, callback ->
                onUpdateStudent(updated) { success, msg ->
                    callback(success, msg)
                    if (success) editingStudent = null
                }
            }
        )
    }

    // Toggle Status Confirmation Dialog
    statusConfirmStudent?.let { student ->
        val willDeactivate = student.status != "INACTIVE"
        AlertDialog(
            onDismissRequest = { statusConfirmStudent = null },
            icon = {
                Icon(
                    imageVector = if (willDeactivate) Icons.Default.Warning else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (willDeactivate) StatusDanger else StatusSuccess
                )
            },
            title = {
                Text(if (willDeactivate) "Deactivate Student?" else "Reactivate Student?")
            },
            text = {
                Text(
                    if (willDeactivate)
                        "Deactivating '${student.fullName}' (${student.rollNo}) will disable portal login access. All grades, attendance history, and submissions will remain completely preserved in the database."
                    else
                        "Reactivating '${student.fullName}' will restore active student standing."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onToggleStatus(student)
                        statusConfirmStudent = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (willDeactivate) StatusDanger else StatusSuccess
                    )
                ) {
                    Text(if (willDeactivate) "Deactivate" else "Activate")
                }
            },
            dismissButton = {
                TextButton(onClick = { statusConfirmStudent = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AdminStudentCardItem(
    student: StudentEntity,
    onEdit: () -> Unit,
    onToggleStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isInactive = student.status == "INACTIVE"
    val isAtRisk = student.status == "AT_RISK"

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!isInactive) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isInactive -> Color.Gray
                                    isAtRisk -> CampusAmber
                                    else -> CampusTeal
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = student.fullName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = student.fullName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (!isInactive) CampusNavyDark else Color.Gray
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Roll: ${student.rollNo}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CampusNavyPrimary.copy(alpha = 0.1f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "${student.departmentId} · Sem ${student.semester}-${student.section}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CampusNavyPrimary
                                )
                            }
                        }
                    }
                }

                // Status Chip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = when {
                        isInactive -> StatusDanger.copy(alpha = 0.15f)
                        isAtRisk -> CampusAmber.copy(alpha = 0.15f)
                        else -> StatusSuccess.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = student.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isInactive -> StatusDanger
                            isAtRisk -> CampusAmber
                            else -> StatusSuccess
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metrics row: CGPA & Attendance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("CGPA: ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = String.format("%.2f", student.cgpa),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (student.cgpa < 6.0) CampusAmber else StatusSuccess
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Attendance: ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${student.attendancePercentage.toInt()}%",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (student.attendancePercentage < 75.0) StatusDanger else StatusSuccess
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = student.phone, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Actions row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onToggleStatus,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (!isInactive) StatusDanger else StatusSuccess
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (!isInactive) "Deactivate" else "Activate", fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onEdit,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CampusNavyPrimary),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontSize = 11.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrollStudentAdminDialog(
    activeDepartments: List<DepartmentEntity>,
    onDismiss: () -> Unit,
    onConfirm: (
        rollNo: String,
        fullName: String,
        email: String,
        phone: String,
        departmentId: String,
        semester: Int,
        section: String,
        cgpa: Double,
        attendancePercentage: Double,
        guardianContact: String,
        createAccount: Boolean,
        tempPasswordRaw: String?,
        (Boolean, String) -> Unit
    ) -> Unit
) {
    var rollNo by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var guardianContact by remember { mutableStateOf("") }
    var selectedDeptCode by remember { mutableStateOf(activeDepartments.firstOrNull()?.code ?: "") }
    var deptDropdownExpanded by remember { mutableStateOf(false) }
    var semester by remember { mutableIntStateOf(1) }
    var section by remember { mutableStateOf("A") }
    var cgpaInput by remember { mutableStateOf("8.00") }
    var attendanceInput by remember { mutableStateOf("90.0") }
    var createAccount by remember { mutableStateOf(true) }
    var tempPassword by remember { mutableStateOf("welcome123") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = { Text("Enroll New Student", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "Fill in student information. Only active departments can receive enrollments.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    OutlinedTextField(
                        value = rollNo,
                        onValueChange = { rollNo = it.uppercase() },
                        label = { Text("Roll Number *") },
                        placeholder = { Text("e.g. CS2026099") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name *") },
                        placeholder = { Text("e.g. Maya Lin") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Institutional Email *") },
                        placeholder = { Text("e.g. maya.lin@college.edu") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        placeholder = { Text("e.g. +1 555-0199") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = guardianContact,
                        onValueChange = { guardianContact = it },
                        label = { Text("Guardian Contact") },
                        placeholder = { Text("e.g. +1 555-0100") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Department Dropdown
                item {
                    ExposedDropdownMenuBox(
                        expanded = deptDropdownExpanded,
                        onExpandedChange = { deptDropdownExpanded = !deptDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedDeptCode,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Department (Active only) *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = deptDropdownExpanded,
                            onDismissRequest = { deptDropdownExpanded = false }
                        ) {
                            activeDepartments.forEach { dept ->
                                DropdownMenuItem(
                                    text = { Text("${dept.code} - ${dept.name}") },
                                    onClick = {
                                        selectedDeptCode = dept.code
                                        deptDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = semester.toString(),
                            onValueChange = { sem -> semester = sem.toIntOrNull()?.coerceIn(1, 8) ?: 1 },
                            label = { Text("Semester (1-8)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = section,
                            onValueChange = { sec -> section = sec.uppercase().take(1) },
                            label = { Text("Section (A/B)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = cgpaInput,
                            onValueChange = { cgpaInput = it },
                            label = { Text("Initial CGPA") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = attendanceInput,
                            onValueChange = { attendanceInput = it },
                            label = { Text("Attendance %") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Account creation checkbox
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { createAccount = !createAccount }
                    ) {
                        Checkbox(
                            checked = createAccount,
                            onCheckedChange = { createAccount = it }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text("Create Student Portal Account", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Allows student to login using their Roll No", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                if (createAccount) {
                    item {
                        OutlinedTextField(
                            value = tempPassword,
                            onValueChange = { tempPassword = it },
                            label = { Text("Initial Temporary Password *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                errorMessage?.let {
                    item {
                        Text(text = it, color = StatusDanger, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (rollNo.isBlank() || fullName.isBlank() || email.isBlank() || selectedDeptCode.isBlank()) {
                        errorMessage = "Roll number, name, email, and department are required."
                        return@Button
                    }
                    if (createAccount && tempPassword.isBlank()) {
                        errorMessage = "Temporary password cannot be empty when creating an account."
                        return@Button
                    }
                    val cgpa = cgpaInput.toDoubleOrNull() ?: 8.0
                    val att = attendanceInput.toDoubleOrNull() ?: 90.0

                    isSubmitting = true
                    errorMessage = null
                    onConfirm(
                        rollNo,
                        fullName,
                        email,
                        phone.ifBlank { "+1 555-0100" },
                        selectedDeptCode,
                        semester,
                        section.ifBlank { "A" },
                        cgpa,
                        att,
                        guardianContact.ifBlank { "+1 555-0101" },
                        createAccount,
                        if (createAccount) tempPassword else null
                    ) { success, msg ->
                        isSubmitting = false
                        if (!success) {
                            errorMessage = msg
                        }
                    }
                },
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = CampusNavyPrimary)
            ) {
                Text(if (isSubmitting) "Enrolling..." else "Enroll Student")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSubmitting) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditStudentAdminDialog(
    student: StudentEntity,
    departments: List<DepartmentEntity>,
    onDismiss: () -> Unit,
    onConfirm: (StudentEntity, (Boolean, String) -> Unit) -> Unit
) {
    var fullName by remember { mutableStateOf(student.fullName) }
    var email by remember { mutableStateOf(student.email) }
    var phone by remember { mutableStateOf(student.phone) }
    var guardianContact by remember { mutableStateOf(student.guardianContact) }
    var semester by remember { mutableIntStateOf(student.semester) }
    var section by remember { mutableStateOf(student.section) }
    var cgpaInput by remember { mutableStateOf(student.cgpa.toString()) }
    var attendanceInput by remember { mutableStateOf(student.attendancePercentage.toString()) }
    var status by remember { mutableStateOf(student.status) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = { Text("Edit Student: ${student.rollNo}", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = guardianContact,
                        onValueChange = { guardianContact = it },
                        label = { Text("Guardian Contact") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = semester.toString(),
                            onValueChange = { sem -> semester = sem.toIntOrNull()?.coerceIn(1, 8) ?: student.semester },
                            label = { Text("Semester") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = section,
                            onValueChange = { sec -> section = sec.uppercase().take(1) },
                            label = { Text("Section") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = cgpaInput,
                            onValueChange = { cgpaInput = it },
                            label = { Text("CGPA") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = attendanceInput,
                            onValueChange = { attendanceInput = it },
                            label = { Text("Attendance %") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text("Standing Status:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("ACTIVE", "AT_RISK", "INACTIVE").forEach { s ->
                            FilterChip(
                                selected = status == s,
                                onClick = { status = s },
                                label = { Text(s, fontSize = 10.sp) }
                            )
                        }
                    }
                }

                errorMessage?.let {
                    item {
                        Text(text = it, color = StatusDanger, fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isBlank() || email.isBlank()) {
                        errorMessage = "Name and email cannot be blank."
                        return@Button
                    }
                    val cgpa = cgpaInput.toDoubleOrNull() ?: student.cgpa
                    val att = attendanceInput.toDoubleOrNull() ?: student.attendancePercentage

                    isSubmitting = true
                    val updated = student.copy(
                        fullName = fullName.trim(),
                        email = email.trim(),
                        phone = phone.trim(),
                        guardianContact = guardianContact.trim(),
                        semester = semester,
                        section = section.ifBlank { "A" },
                        cgpa = cgpa,
                        attendancePercentage = att,
                        status = status
                    )
                    onConfirm(updated) { success, msg ->
                        isSubmitting = false
                        if (!success) {
                            errorMessage = msg
                        }
                    }
                },
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = CampusNavyPrimary)
            ) {
                Text(if (isSubmitting) "Saving..." else "Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSubmitting) {
                Text("Cancel")
            }
        }
    )
}
