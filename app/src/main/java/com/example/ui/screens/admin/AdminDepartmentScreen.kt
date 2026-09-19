package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.StudentEntity
import com.example.ui.theme.CampusAmber
import com.example.ui.theme.CampusNavyDark
import com.example.ui.theme.CampusNavyPrimary
import com.example.ui.theme.CampusTeal
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess

@Composable
fun AdminDepartmentScreen(
    departments: List<DepartmentEntity>,
    students: List<StudentEntity>,
    onCreateDepartment: (name: String, code: String, headOfDept: String, buildingRoom: String, (Boolean, String) -> Unit) -> Unit,
    onUpdateDepartment: (DepartmentEntity, (Boolean, String) -> Unit) -> Unit,
    onToggleStatus: (DepartmentEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, ACTIVE, INACTIVE
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingDepartment by remember { mutableStateOf<DepartmentEntity?>(null) }
    var statusConfirmDepartment by remember { mutableStateOf<DepartmentEntity?>(null) }

    val filteredDepartments = departments.filter { dept ->
        val matchesSearch = dept.name.contains(searchQuery, ignoreCase = true) ||
                dept.code.contains(searchQuery, ignoreCase = true) ||
                dept.headOfDept.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "ACTIVE" -> dept.status == "ACTIVE"
            "INACTIVE" -> dept.status == "INACTIVE"
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = CampusNavyPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("admin_add_department_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Department")
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
                            text = "Department Management",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = CampusNavyDark
                        )
                        Text(
                            text = "${departments.count { it.status == "ACTIVE" }} Active · ${departments.count { it.status == "INACTIVE" }} Inactive",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { showCreateDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CampusNavyPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Dept", fontSize = 12.sp)
                    }
                }
            }

            // Search Bar & Filter Chips
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by code, name, or head...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = CampusNavyPrimary)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("ALL", "ACTIVE", "INACTIVE").forEach { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CampusNavyPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            if (filteredDepartments.isEmpty()) {
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
                                imageVector = Icons.Default.Business,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No departments found",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Tap 'Add Dept' to create a new department.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredDepartments) { dept ->
                    val studentCount = students.count { it.departmentId == dept.code }
                    DepartmentCardItem(
                        department = dept,
                        studentCount = studentCount,
                        onEdit = { editingDepartment = dept },
                        onToggleStatus = { statusConfirmDepartment = dept }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(56.dp))
            }
        }
    }

    // Create Department Dialog
    if (showCreateDialog) {
        CreateDepartmentDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, code, head, room, callback ->
                onCreateDepartment(name, code, head, room) { success, msg ->
                    callback(success, msg)
                    if (success) showCreateDialog = false
                }
            }
        )
    }

    // Edit Department Dialog
    editingDepartment?.let { dept ->
        EditDepartmentDialog(
            department = dept,
            onDismiss = { editingDepartment = null },
            onConfirm = { updated, callback ->
                onUpdateDepartment(updated) { success, msg ->
                    callback(success, msg)
                    if (success) editingDepartment = null
                }
            }
        )
    }

    // Status Toggle Confirmation Dialog
    statusConfirmDepartment?.let { dept ->
        val willDeactivate = dept.status == "ACTIVE"
        AlertDialog(
            onDismissRequest = { statusConfirmDepartment = null },
            icon = {
                Icon(
                    imageVector = if (willDeactivate) Icons.Default.Warning else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (willDeactivate) StatusDanger else StatusSuccess
                )
            },
            title = {
                Text(if (willDeactivate) "Deactivate Department?" else "Reactivate Department?")
            },
            text = {
                Text(
                    if (willDeactivate)
                        "Deactivating '${dept.name}' (${dept.code}) will prevent new student enrollments and new faculty assignments. All historical student, attendance, and academic records will remain completely intact."
                    else
                        "Reactivating '${dept.name}' will allow new student enrollments and faculty appointments."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onToggleStatus(dept)
                        statusConfirmDepartment = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (willDeactivate) StatusDanger else StatusSuccess
                    )
                ) {
                    Text(if (willDeactivate) "Deactivate" else "Activate")
                }
            },
            dismissButton = {
                TextButton(onClick = { statusConfirmDepartment = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DepartmentCardItem(
    department: DepartmentEntity,
    studentCount: Int,
    onEdit: () -> Unit,
    onToggleStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isActive = department.status == "ACTIVE"
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) 1.5.dp else 0.5.dp)
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
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isActive) CampusNavyPrimary else Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = department.code.take(4),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = department.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isActive) CampusNavyDark else Color.Gray
                        )
                        Text(
                            text = "Code: ${department.code}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isActive) StatusSuccess.copy(alpha = 0.15f) else StatusDanger.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = department.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) StatusSuccess else StatusDanger,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Details info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "HOD: ${department.headOfDept}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MeetingRoom,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = department.buildingRoom,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = CampusTeal
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$studentCount enrolled students",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CampusTeal
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card Action Buttons
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
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
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
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun CreateDepartmentDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, code: String, head: String, room: String, (Boolean, String) -> Unit) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var head by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = { Text("Add New Department", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Configure department details. Inactive or duplicate codes are prevented.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it.uppercase().filter { ch -> ch.isLetterOrDigit() } },
                    label = { Text("Department Code *") },
                    placeholder = { Text("e.g. AI, CIVIL, BIO") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Department Name *") },
                    placeholder = { Text("e.g. Artificial Intelligence & Data Science") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = head,
                    onValueChange = { head = it },
                    label = { Text("Head of Department (HOD) *") },
                    placeholder = { Text("e.g. Dr. Arthur Vance") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Building & Room *") },
                    placeholder = { Text("e.g. Tech Block 4, Rm 302") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                errorMessage?.let {
                    Text(text = it, color = StatusDanger, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || code.isBlank() || head.isBlank() || room.isBlank()) {
                        errorMessage = "All fields are required."
                        return@Button
                    }
                    isSubmitting = true
                    errorMessage = null
                    onConfirm(name, code, head, room) { success, msg ->
                        isSubmitting = false
                        if (!success) {
                            errorMessage = msg
                        }
                    }
                },
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = CampusNavyPrimary)
            ) {
                Text(if (isSubmitting) "Creating..." else "Create Department")
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
fun EditDepartmentDialog(
    department: DepartmentEntity,
    onDismiss: () -> Unit,
    onConfirm: (DepartmentEntity, (Boolean, String) -> Unit) -> Unit
) {
    var name by remember { mutableStateOf(department.name) }
    var head by remember { mutableStateOf(department.headOfDept) }
    var room by remember { mutableStateOf(department.buildingRoom) }
    var status by remember { mutableStateOf(department.status) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = { Text("Edit Department: ${department.code}", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Department Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = head,
                    onValueChange = { head = it },
                    label = { Text("Head of Department (HOD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Building & Room") },
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
                    if (name.isBlank() || head.isBlank() || room.isBlank()) {
                        errorMessage = "Name, Head, and Room cannot be blank."
                        return@Button
                    }
                    isSubmitting = true
                    val updated = department.copy(
                        name = name.trim(),
                        headOfDept = head.trim(),
                        buildingRoom = room.trim(),
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
