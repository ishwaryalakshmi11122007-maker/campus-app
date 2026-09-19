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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DepartmentEntity
import com.example.data.model.UserAccountEntity
import com.example.ui.theme.CampusAmber
import com.example.ui.theme.CampusNavyDark
import com.example.ui.theme.CampusNavyPrimary
import com.example.ui.theme.CampusTeal
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess

@Composable
fun AdminAccountsScreen(
    userAccounts: List<UserAccountEntity>,
    departments: List<DepartmentEntity>,
    currentSessionAccount: UserAccountEntity?,
    onCreateAccount: (username: String, passRaw: String, role: String, name: String, dept: String, desig: String, (Boolean, String) -> Unit) -> Unit,
    onToggleAccountStatus: (UserAccountEntity) -> Unit,
    onResetPassword: (accountId: String, newPassRaw: String, (Boolean, String) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf("ALL") } // ALL, ADMIN, DEAN, FACULTY, STUDENT
    var showCreateDialog by remember { mutableStateOf(false) }
    var resetPasswordAccount by remember { mutableStateOf<UserAccountEntity?>(null) }
    var statusConfirmAccount by remember { mutableStateOf<UserAccountEntity?>(null) }

    val filteredAccounts = userAccounts.filter { account ->
        val matchesSearch = account.fullName.contains(searchQuery, ignoreCase = true) ||
                account.username.contains(searchQuery, ignoreCase = true) ||
                account.departmentId.contains(searchQuery, ignoreCase = true)
        val matchesRole = selectedRoleFilter == "ALL" || account.role == selectedRoleFilter
        matchesSearch && matchesRole
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = CampusNavyPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("admin_create_account_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Account")
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
                            text = "User Accounts & Security",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = CampusNavyDark
                        )
                        Text(
                            text = "${userAccounts.count { it.status == "ACTIVE" }} Active · ${userAccounts.count { it.status == "DISABLED" }} Disabled",
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
                        Text("New Account", fontSize = 12.sp)
                    }
                }
            }

            // Search Bar & Role Chips
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by username, name, department...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = CampusNavyPrimary)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("ALL", "ADMIN", "DEAN", "FACULTY", "STUDENT").forEach { role ->
                        FilterChip(
                            selected = selectedRoleFilter == role,
                            onClick = { selectedRoleFilter = role },
                            label = { Text(role, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CampusNavyPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            if (filteredAccounts.isEmpty()) {
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
                                imageVector = Icons.Default.ManageAccounts,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No user accounts match criteria",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredAccounts) { account ->
                    AdminAccountCardItem(
                        account = account,
                        isCurrentSession = account.id == currentSessionAccount?.id,
                        onResetPassword = { resetPasswordAccount = account },
                        onToggleStatus = { statusConfirmAccount = account }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(56.dp))
            }
        }
    }

    // Create Account Dialog
    if (showCreateDialog) {
        CreateAccountAdminDialog(
            departments = departments,
            onDismiss = { showCreateDialog = false },
            onConfirm = { username, pass, role, name, dept, desig, callback ->
                onCreateAccount(username, pass, role, name, dept, desig) { success, msg ->
                    callback(success, msg)
                    if (success) showCreateDialog = false
                }
            }
        )
    }

    // Reset Password Dialog
    resetPasswordAccount?.let { account ->
        ResetPasswordAdminDialog(
            account = account,
            onDismiss = { resetPasswordAccount = null },
            onConfirm = { newPass, callback ->
                onResetPassword(account.id, newPass) { success, msg ->
                    callback(success, msg)
                    if (success) resetPasswordAccount = null
                }
            }
        )
    }

    // Status Confirmation Dialog
    statusConfirmAccount?.let { account ->
        val willDisable = account.status == "ACTIVE"
        AlertDialog(
            onDismissRequest = { statusConfirmAccount = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = if (willDisable) StatusDanger else StatusSuccess
                )
            },
            title = {
                Text(if (willDisable) "Disable Account Access?" else "Enable Account Access?")
            },
            text = {
                Text(
                    if (willDisable)
                        "Disabling '${account.username}' (${account.fullName}) will immediately block any sign in attempts. Historical records and submitted data remain completely safe."
                    else
                        "Enabling '${account.username}' will restore access to their role-assigned portal."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onToggleAccountStatus(account)
                        statusConfirmAccount = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (willDisable) StatusDanger else StatusSuccess
                    )
                ) {
                    Text(if (willDisable) "Disable" else "Enable")
                }
            },
            dismissButton = {
                TextButton(onClick = { statusConfirmAccount = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AdminAccountCardItem(
    account: UserAccountEntity,
    isCurrentSession: Boolean,
    onResetPassword: () -> Unit,
    onToggleStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isActive = account.status == "ACTIVE"
    val roleColor = when (account.role) {
        "ADMIN" -> CampusNavyPrimary
        "DEAN" -> Color(0xFF7C3AED)
        "FACULTY" -> CampusTeal
        "STUDENT" -> Color(0xFF0F766E)
        else -> Color.Gray
    }

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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isActive) roleColor else Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (account.role) {
                                "ADMIN" -> Icons.Default.Shield
                                "DEAN" -> Icons.Default.Security
                                "FACULTY" -> Icons.Default.Person
                                else -> Icons.Default.Person
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = account.fullName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isActive) CampusNavyDark else Color.Gray
                            )
                            if (isCurrentSession) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CampusTeal.copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("You", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CampusTeal)
                                }
                            }
                        }
                        Text(
                            text = "Username: ${account.username} · Dept: ${account.departmentId}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Role & Status Badges
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(roleColor.copy(alpha = 0.15f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = account.role,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = roleColor
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isActive) StatusSuccess.copy(alpha = 0.15f) else StatusDanger.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = account.status,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) StatusSuccess else StatusDanger,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Designation: ${account.designation}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                account.studentId?.let { id ->
                    Text(
                        text = "Linked Student: $id",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CampusTeal
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isCurrentSession) {
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
                        Text(if (isActive) "Disable" else "Enable", fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                }

                Button(
                    onClick = onResetPassword,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CampusNavyPrimary),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset Password", fontSize = 11.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountAdminDialog(
    departments: List<DepartmentEntity>,
    onDismiss: () -> Unit,
    onConfirm: (
        username: String,
        passRaw: String,
        role: String,
        name: String,
        dept: String,
        desig: String,
        (Boolean, String) -> Unit
    ) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("FACULTY") }
    var selectedDeptCode by remember { mutableStateOf(departments.firstOrNull()?.code ?: "ALL") }
    var deptDropdownExpanded by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = { Text("Create System User Account", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it.lowercase().trim() },
                    label = { Text("Username *") },
                    placeholder = { Text("e.g. jsmith.admin") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Initial Password *") },
                    placeholder = { Text("Minimum 6 characters") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name *") },
                    placeholder = { Text("e.g. John Smith") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = designation,
                    onValueChange = { designation = it },
                    label = { Text("Designation *") },
                    placeholder = { Text("e.g. System Administrator") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Role selector
                Text("Account Role *", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("ADMIN", "DEAN", "FACULTY", "STUDENT").forEach { r ->
                        FilterChip(
                            selected = selectedRole == r,
                            onClick = { selectedRole = r },
                            label = { Text(r, fontSize = 10.sp) }
                        )
                    }
                }

                // Department
                ExposedDropdownMenuBox(
                    expanded = deptDropdownExpanded,
                    onExpandedChange = { deptDropdownExpanded = !deptDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedDeptCode,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Department Scope") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = deptDropdownExpanded,
                        onDismissRequest = { deptDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("ALL (Campus Wide)") },
                            onClick = {
                                selectedDeptCode = "ALL"
                                deptDropdownExpanded = false
                            }
                        )
                        departments.forEach { dept ->
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

                errorMessage?.let {
                    Text(text = it, color = StatusDanger, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (username.isBlank() || password.isBlank() || fullName.isBlank() || designation.isBlank()) {
                        errorMessage = "All fields are required."
                        return@Button
                    }
                    if (password.length < 5) {
                        errorMessage = "Password must be at least 5 characters."
                        return@Button
                    }
                    isSubmitting = true
                    errorMessage = null
                    onConfirm(
                        username,
                        password,
                        selectedRole,
                        fullName,
                        selectedDeptCode,
                        designation
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
                Text(if (isSubmitting) "Creating..." else "Create Account")
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
fun ResetPasswordAdminDialog(
    account: UserAccountEntity,
    onDismiss: () -> Unit,
    onConfirm: (newPassRaw: String, (Boolean, String) -> Unit) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = { Text("Reset Password: ${account.username}", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Set a new password for ${account.fullName}. The password will be salted and hashed with SHA-256 before storing.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password *") },
                    placeholder = { Text("Enter new temporary password") },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
                    if (newPassword.isBlank() || newPassword.length < 5) {
                        errorMessage = "Password must be at least 5 characters."
                        return@Button
                    }
                    isSubmitting = true
                    errorMessage = null
                    onConfirm(newPassword) { success, msg ->
                        isSubmitting = false
                        if (!success) {
                            errorMessage = msg
                        }
                    }
                },
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = CampusNavyPrimary)
            ) {
                Text(if (isSubmitting) "Updating..." else "Reset Password")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSubmitting) {
                Text("Cancel")
            }
        }
    )
}
