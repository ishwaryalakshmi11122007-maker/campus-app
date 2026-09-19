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
import androidx.compose.material.icons.filled.SupervisorAccount
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
import com.example.data.model.FacultyMemberEntity
import com.example.data.model.UserAccountEntity
import com.example.ui.theme.CampusAmber
import com.example.ui.theme.CampusNavyDark
import com.example.ui.theme.CampusNavyPrimary
import com.example.ui.theme.CampusTeal
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess

@Composable
fun AdminFacultyScreen(
    facultyMembers: List<FacultyMemberEntity>,
    departments: List<DepartmentEntity>,
    userAccounts: List<UserAccountEntity>,
    onAppointFaculty: (
        employeeId: String,
        fullName: String,
        email: String,
        phone: String,
        departmentId: String,
        designation: String,
        username: String,
        createAccount: Boolean,
        tempPasswordRaw: String?,
        role: String,
        (Boolean, String) -> Unit
    ) -> Unit,
    onUpdateFaculty: (FacultyMemberEntity, (Boolean, String) -> Unit) -> Unit,
    onToggleStatus: (FacultyMemberEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedDept by remember { mutableStateOf("ALL") }
    var selectedStatus by remember { mutableStateOf("ALL") } // ALL, ACTIVE, INACTIVE
    var showAppointDialog by remember { mutableStateOf(false) }
    var editingFaculty by remember { mutableStateOf<FacultyMemberEntity?>(null) }
    var statusConfirmFaculty by remember { mutableStateOf<FacultyMemberEntity?>(null) }

    val activeDepartments = departments.filter { it.status == "ACTIVE" }

    val filteredFaculty = facultyMembers.filter { faculty ->
        val matchesSearch = faculty.fullName.contains(searchQuery, ignoreCase = true) ||
                faculty.employeeId.contains(searchQuery, ignoreCase = true) ||
                faculty.email.contains(searchQuery, ignoreCase = true) ||
                faculty.username.contains(searchQuery, ignoreCase = true)
        val matchesDept = selectedDept == "ALL" || faculty.departmentId == selectedDept
        val matchesStatus = when (selectedStatus) {
            "ACTIVE" -> faculty.status == "ACTIVE"
            "INACTIVE" -> faculty.status == "INACTIVE"
            else -> true
        }
        matchesSearch && matchesDept && matchesStatus
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAppointDialog = true },
                containerColor = CampusNavyPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("admin_appoint_faculty_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Appoint Faculty")
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
                            text = "Faculty & Staff Governance",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = CampusNavyDark
                        )
                        Text(
                            text = "${facultyMembers.count { it.status == "ACTIVE" }} Active · ${facultyMembers.count { it.status == "INACTIVE" }} Inactive",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { showAppointDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CampusNavyPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Appoint", fontSize = 12.sp)
                    }
                }
            }

            // Search Bar & Department Filter
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name, ID, or username...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = CampusNavyPrimary)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

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

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("ALL", "ACTIVE", "INACTIVE").forEach { status ->
                        FilterChip(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status },
                            label = { Text(status, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (status == "INACTIVE") StatusDanger else CampusNavyPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            if (filteredFaculty.isEmpty()) {
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
                                imageVector = Icons.Default.SupervisorAccount,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No faculty members match criteria",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredFaculty) { faculty ->
                    val linkedAccount = userAccounts.find { it.username == faculty.username }
                    AdminFacultyCardItem(
                        faculty = faculty,
                        linkedAccount = linkedAccount,
                        onEdit = { editingFaculty = faculty },
                        onToggleStatus = { statusConfirmFaculty = faculty }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(56.dp))
            }
        }
    }

    // Appoint Faculty Dialog
    if (showAppointDialog) {
        AppointFacultyAdminDialog(
            activeDepartments = activeDepartments,
            onDismiss = { showAppointDialog = false },
            onConfirm = { empId, name, email, phone, dept, desig, username, createAcc, pass, role, callback ->
                onAppointFaculty(empId, name, email, phone, dept, desig, username, createAcc, pass, role) { success, msg ->
                    callback(success, msg)
                    if (success) showAppointDialog = false
                }
            }
        )
    }

    // Edit Faculty Dialog
    editingFaculty?.let { faculty ->
        EditFacultyAdminDialog(
            faculty = faculty,
            departments = departments,
            onDismiss = { editingFaculty = null },
            onConfirm = { updated, callback ->
                onUpdateFaculty(updated) { success, msg ->
                    callback(success, msg)
                    if (success) editingFaculty = null
                }
            }
        )
    }

    // Status confirmation Dialog
    statusConfirmFaculty?.let { faculty ->
        val willDeactivate = faculty.status == "ACTIVE"
        AlertDialog(
            onDismissRequest = { statusConfirmFaculty = null },
            icon = {
                Icon(
                    imageVector = if (willDeactivate) Icons.Default.Warning else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (willDeactivate) StatusDanger else StatusSuccess
                )
            },
            title = {
                Text(if (willDeactivate) "Deactivate Faculty Member?" else "Reactivate Faculty Member?")
            },
            text = {
                Text(
                    if (willDeactivate)
                        "Deactivating '${faculty.fullName}' (${faculty.employeeId}) will disable their institutional login. All past grading, verification, and attendance sessions will remain completely intact."
                    else
                        "Reactivating '${faculty.fullName}' will restore active faculty privileges."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onToggleStatus(faculty)
                        statusConfirmFaculty = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (willDeactivate) StatusDanger else StatusSuccess
                    )
                ) {
                    Text(if (willDeactivate) "Deactivate" else "Activate")
                }
            },
            dismissButton = {
                TextButton(onClick = { statusConfirmFaculty = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AdminFacultyCardItem(
    faculty: FacultyMemberEntity,
    linkedAccount: UserAccountEntity?,
    onEdit: () -> Unit,
    onToggleStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isActive = faculty.status == "ACTIVE"

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
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
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isActive) CampusNavyPrimary else Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = faculty.fullName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = faculty.fullName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isActive) CampusNavyDark else Color.Gray
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${faculty.designation}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CampusNavyPrimary.copy(alpha = 0.1f))
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = faculty.departmentId,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CampusNavyPrimary
                                )
                            }
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isActive) StatusSuccess.copy(alpha = 0.15f) else StatusDanger.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = faculty.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) StatusSuccess else StatusDanger,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("ID: ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = faculty.employeeId, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("User: ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = faculty.username, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CampusTeal)
                }
                linkedAccount?.let { acc ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Role: ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = acc.role, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C3AED))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = faculty.email, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onToggleStatus,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isActive) StatusDanger else StatusSuccess
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
                    Text(if (isActive) "Deactivate" else "Activate", fontSize = 11.sp)
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
fun AppointFacultyAdminDialog(
    activeDepartments: List<DepartmentEntity>,
    onDismiss: () -> Unit,
    onConfirm: (
        empId: String,
        name: String,
        email: String,
        phone: String,
        dept: String,
        desig: String,
        username: String,
        createAcc: Boolean,
        tempPass: String?,
        role: String,
        (Boolean, String) -> Unit
    ) -> Unit
) {
    var empId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedDeptCode by remember { mutableStateOf(activeDepartments.firstOrNull()?.code ?: "") }
    var deptDropdownExpanded by remember { mutableStateOf(false) }
    var designation by remember { mutableStateOf("Assistant Professor") }
    var username by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("FACULTY") } // FACULTY or DEAN
    var createAccount by remember { mutableStateOf(true) }
    var tempPassword by remember { mutableStateOf("faculty123") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = { Text("Appoint Faculty / Academic Staff", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "Assign credentials and departmental affiliation.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    OutlinedTextField(
                        value = empId,
                        onValueChange = { empId = it.uppercase() },
                        label = { Text("Employee ID *") },
                        placeholder = { Text("e.g. FAC-CSE-009") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name *") },
                        placeholder = { Text("e.g. Dr. Eleanor Gray") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Institutional Email *") },
                        placeholder = { Text("e.g. eleanor.gray@college.edu") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        placeholder = { Text("e.g. +1 555-0211") },
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
                    OutlinedTextField(
                        value = designation,
                        onValueChange = { designation = it },
                        label = { Text("Designation *") },
                        placeholder = { Text("e.g. Associate Professor & HOD") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it.lowercase().filter { ch -> ch.isLetterOrDigit() || ch == '.' } },
                        label = { Text("Login Username *") },
                        placeholder = { Text("e.g. eleanor.cse") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Role selection (FACULTY or DEAN)
                item {
                    Text("Role Assignment:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = role == "FACULTY",
                            onClick = { role = "FACULTY" },
                            label = { Text("FACULTY (Dept Scoped)") }
                        )
                        FilterChip(
                            selected = role == "DEAN",
                            onClick = { role = "DEAN" },
                            label = { Text("DEAN (Campus Wide)") }
                        )
                    }
                }

                // Account checkbox
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
                        Text("Create Active Login Account", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                    if (empId.isBlank() || name.isBlank() || email.isBlank() || username.isBlank() || selectedDeptCode.isBlank()) {
                        errorMessage = "Employee ID, Name, Email, Dept, and Username are required."
                        return@Button
                    }
                    if (createAccount && tempPassword.isBlank()) {
                        errorMessage = "Temporary password cannot be empty."
                        return@Button
                    }
                    isSubmitting = true
                    errorMessage = null
                    onConfirm(
                        empId,
                        name,
                        email,
                        phone.ifBlank { "+1 555-0200" },
                        selectedDeptCode,
                        designation,
                        username,
                        createAccount,
                        if (createAccount) tempPassword else null,
                        role
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
                Text(if (isSubmitting) "Appointing..." else "Appoint Faculty")
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
fun EditFacultyAdminDialog(
    faculty: FacultyMemberEntity,
    departments: List<DepartmentEntity>,
    onDismiss: () -> Unit,
    onConfirm: (FacultyMemberEntity, (Boolean, String) -> Unit) -> Unit
) {
    var name by remember { mutableStateOf(faculty.fullName) }
    var email by remember { mutableStateOf(faculty.email) }
    var phone by remember { mutableStateOf(faculty.phone) }
    var designation by remember { mutableStateOf(faculty.designation) }
    var status by remember { mutableStateOf(faculty.status) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = { Text("Edit Faculty: ${faculty.employeeId}", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = designation,
                    onValueChange = { designation = it },
                    label = { Text("Designation") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Status:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    FilterChip(
                        selected = status == "ACTIVE",
                        onClick = { status = "ACTIVE" },
                        label = { Text("ACTIVE") }
                    )
                    FilterChip(
                        selected = status == "INACTIVE",
                        onClick = { status = "INACTIVE" },
                        label = { Text("INACTIVE") }
                    )
                }

                errorMessage?.let {
                    Text(text = it, color = StatusDanger, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || email.isBlank() || designation.isBlank()) {
                        errorMessage = "Name, email, and designation cannot be blank."
                        return@Button
                    }
                    isSubmitting = true
                    val updated = faculty.copy(
                        fullName = name.trim(),
                        email = email.trim(),
                        phone = phone.trim(),
                        designation = designation.trim(),
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
