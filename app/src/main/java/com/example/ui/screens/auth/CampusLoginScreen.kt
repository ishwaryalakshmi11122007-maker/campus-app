package com.example.ui.screens.auth

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserAccountEntity
import com.example.ui.theme.CampusAmber
import com.example.ui.theme.CampusNavyDark
import com.example.ui.theme.CampusNavyPrimary
import com.example.ui.theme.CampusTeal
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess

@Composable
fun CampusLoginScreen(
    userAccounts: List<UserAccountEntity>,
    loginError: String?,
    onLogin: (username: String, passwordRaw: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("admin123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedPresetRoleTab by remember { mutableIntStateOf(0) } // 0 = Admin, 1 = Dean, 2 = Faculty, 3 = Students

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(36.dp))

                // Institutional Security Shield Header
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(CampusNavyPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Security Shield",
                        tint = Color.White,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "COLLEGE SECURE PORTAL",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    ),
                    color = CampusNavyPrimary
                )

                Text(
                    text = "Department Staff & Unique Student Access",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Encryption active badge
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(CampusTeal.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = CampusTeal,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AES-256 Document & Details Encryption Active",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CampusTeal
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
            }

            // Login Input Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Sign In",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = CampusNavyDark
                        )
                        Text(
                            text = "Enter your individual staff username or student roll number",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Username field
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username or Roll No") },
                            placeholder = { Text("e.g. vance.cse or CS2024001") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = CampusNavyPrimary)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("username_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Password field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            placeholder = { Text("Enter account password") },
                            leadingIcon = {
                                Icon(Icons.Default.Key, contentDescription = null, tint = CampusNavyPrimary)
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (username.isNotBlank() && password.isNotBlank()) {
                                        onLogin(username, password)
                                    }
                                }
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("password_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Error message
                        if (loginError != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(StatusDanger.copy(alpha = 0.1f))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = loginError,
                                    color = StatusDanger,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                if (username.isNotBlank() && password.isNotBlank()) {
                                    onLogin(username, password)
                                }
                            },
                            enabled = username.isNotBlank() && password.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = CampusNavyPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_submit_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockOpen,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Access Secure Portal",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Quick Demo Credential Picker (to test department staff and student isolation easily)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.VpnKey,
                                    contentDescription = null,
                                    tint = CampusAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Quick Demo Accounts",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = CampusNavyDark
                                )
                            }
                            Text(
                                text = "Tap to autofill",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Tab Switcher across 4 Roles: Admin, Dean, Faculty, Students
                        TabRow(
                            selectedTabIndex = selectedPresetRoleTab,
                            containerColor = Color.Transparent,
                            contentColor = CampusNavyPrimary
                        ) {
                            Tab(
                                selected = selectedPresetRoleTab == 0,
                                onClick = { selectedPresetRoleTab = 0 },
                                text = { Text("Admin", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                            )
                            Tab(
                                selected = selectedPresetRoleTab == 1,
                                onClick = { selectedPresetRoleTab = 1 },
                                text = { Text("Dean", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                            )
                            Tab(
                                selected = selectedPresetRoleTab == 2,
                                onClick = { selectedPresetRoleTab = 2 },
                                text = { Text("Faculty", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                            )
                            Tab(
                                selected = selectedPresetRoleTab == 3,
                                onClick = { selectedPresetRoleTab = 3 },
                                text = { Text("Students", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val filteredAccounts = when (selectedPresetRoleTab) {
                            0 -> userAccounts.filter { it.role == "ADMIN" }
                            1 -> userAccounts.filter { it.role == "DEAN" }
                            2 -> userAccounts.filter { it.role == "FACULTY" }
                            else -> userAccounts.filter { it.role == "STUDENT" }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            filteredAccounts.forEach { account ->
                                val isSelected = username.equals(account.username, ignoreCase = true)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) CampusTeal else MaterialTheme.colorScheme.outlineVariant,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .background(if (isSelected) CampusTeal.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface)
                                        .clickable {
                                            username = account.username
                                            password = account.plainPasswordHint
                                            // Automatically trigger login for swift testing
                                            onLogin(account.username, account.plainPasswordHint)
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = account.fullName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = CampusNavyDark
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            // Department pill
                                            val deptColor = when (account.departmentId) {
                                                "CSE" -> CampusNavyPrimary
                                                "ECE" -> CampusTeal
                                                "IT" -> Color(0xFF7C3AED)
                                                "ME" -> Color(0xFFEA580C)
                                                else -> Color(0xFF4B5563)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(deptColor.copy(alpha = 0.15f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = account.departmentId,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = deptColor
                                                )
                                            }
                                        }
                                        Text(
                                            text = "${account.designation} · ID: ${account.username}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            username = account.username
                                            password = account.plainPasswordHint
                                            onLogin(account.username, account.plainPasswordHint)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (account.role == "STUDENT") CampusTeal else CampusNavyPrimary
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                            horizontal = 10.dp,
                                            vertical = 4.dp
                                        ),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Log In", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Security Policy & Encryption Notice
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CampusNavyPrimary.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = CampusNavyPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Institutional Privacy Architecture",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = CampusNavyPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• Unique Student Access: Students can view and submit only their own personal records, documents, grades, and leaves.\n" +
                                    "• Department Staff Scope: Faculty members only have authorization to view, record attendance, and evaluate students belonging to their own department.\n" +
                                    "• AES-256 Encryption: Documents, serial numbers, and sensitive identity details are encrypted at rest with SHA-256 cryptographic verification.",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
