package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DepartmentEntity
import com.example.data.model.StudentEntity
import com.example.data.model.UserAccountEntity
import com.example.ui.theme.CampusAmber
import com.example.ui.theme.CampusNavyPrimary
import com.example.ui.theme.CampusTeal
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusDangerBg
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningBg
import com.example.ui.viewmodel.UserRole
import com.example.util.CampusCrypto

@Composable
fun CampusTopAppBar(
    currentRole: UserRole,
    activeStudent: StudentEntity?,
    unreadNotificationCount: Int,
    currentUserAccount: UserAccountEntity? = null,
    onRoleClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onLogoutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CampusNavyPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "College Logo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "CampusDB",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CampusTeal.copy(alpha = 0.15f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "AES-256",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CampusTeal
                            )
                        }
                    }

                    val userSubtitle = if (currentUserAccount != null) {
                        when (currentUserAccount.role) {
                            "ADMIN" -> "${currentUserAccount.fullName} · System Administrator"
                            "DEAN" -> "${currentUserAccount.fullName} · Dean of Academic Affairs"
                            "FACULTY" -> "${currentUserAccount.fullName} · ${currentUserAccount.departmentId} Faculty"
                            "STUDENT" -> "${currentUserAccount.fullName} · Roll: ${currentUserAccount.username}"
                            else -> currentUserAccount.fullName
                        }
                    } else {
                        when (currentRole) {
                            UserRole.ADMIN -> "System Administrator"
                            UserRole.DEAN -> "Dean of Academic Affairs"
                            UserRole.FACULTY -> "Faculty Administration"
                            UserRole.STUDENT -> "Student: ${activeStudent?.fullName ?: "Portal"}"
                        }
                    }

                    Text(
                        text = userSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Institutional Authenticated Role Badge (Fixed according to authenticated credentials)
                val (roleColor, roleIcon, roleLabel) = when (currentRole) {
                    UserRole.ADMIN -> Triple(CampusNavyPrimary, Icons.Default.Shield, "Admin")
                    UserRole.DEAN -> Triple(Color(0xFF7C3AED), Icons.Default.School, "Dean")
                    UserRole.FACULTY -> Triple(CampusNavyPrimary, Icons.Default.SupervisorAccount, currentUserAccount?.departmentId ?: "Faculty")
                    UserRole.STUDENT -> Triple(CampusTeal, Icons.Default.Person, "Student")
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = roleColor.copy(alpha = 0.15f),
                    modifier = Modifier.testTag("role_badge_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = roleIcon,
                            contentDescription = "Role: $roleLabel",
                            tint = roleColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = roleLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = roleColor
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Notification Icon
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("notifications_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotificationCount > 0) {
                                Badge(
                                    containerColor = StatusDanger,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = "$unreadNotificationCount",
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Logout Icon Button
                IconButton(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("logout_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Sign Out",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status.uppercase()) {
        "PRESENT", "VERIFIED", "APPROVED", "GRADED", "ACTIVE" ->
            Triple(StatusSuccessBg, StatusSuccess, status)
        "ABSENT", "REJECTED", "AT_RISK" ->
            Triple(StatusDangerBg, StatusDanger, status.replace("_", " "))
        "PENDING", "SUBMITTED", "LATE" ->
            Triple(StatusWarningBg, StatusWarning, status)
        "MISSING" ->
            Triple(StatusDangerBg, StatusDanger, "MISSING")
        "EXCUSED" ->
            Triple(Color(0xFFE0E7FF), Color(0xFF4338CA), "EXCUSED")
        else ->
            Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, status)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AttendanceProgressBar(
    percentage: Double,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    val color = when {
        percentage >= 85.0 -> StatusSuccess
        percentage >= 75.0 -> CampusTeal
        percentage >= 65.0 -> CampusAmber
        else -> StatusDanger
    }

    Column(modifier = modifier) {
        if (showLabel) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Attendance Status",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (percentage < 75.0) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Low Attendance Warning",
                            tint = StatusDanger,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Below 75% Mandate",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusDanger
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = color
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        LinearProgressIndicator(
            progress = { (percentage / 100.0).toFloat().coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun DepartmentFilterRow(
    departments: List<DepartmentEntity>,
    selectedDeptId: String,
    onSelectDept: (String) -> Unit,
    currentUserAccount: UserAccountEntity? = null,
    modifier: Modifier = Modifier
) {
    val isLockedToDept = currentUserAccount != null && currentUserAccount.role == "FACULTY" && currentUserAccount.departmentId != "ALL"

    if (isLockedToDept) {
        // Staff can only view and change their own department
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CampusNavyPrimary.copy(alpha = 0.08f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = CampusNavyPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "DEPARTMENT SCOPE RESTRICTED: ${currentUserAccount?.departmentId}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CampusNavyPrimary
                )
                Text(
                    text = "Access strictly limited to ${currentUserAccount?.departmentId} department staff.",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyRow(
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedDeptId == "ALL",
                    onClick = { onSelectDept("ALL") },
                    label = { Text("All Depts") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CampusNavyPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }

            items(departments) { dept ->
                FilterChip(
                    selected = selectedDeptId == dept.id,
                    onClick = { onSelectDept(dept.id) },
                    label = { Text(dept.id) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CampusNavyPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun EncryptedFieldViewer(
    label: String,
    rawEncryptedValue: String,
    modifier: Modifier = Modifier
) {
    var isDecrypted by remember { mutableStateOf(false) }
    var showCryptoDialog by remember { mutableStateOf(false) }

    val plainText = remember(rawEncryptedValue) {
        CampusCrypto.decrypt(rawEncryptedValue)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = CampusTeal,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CampusNavyPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(CampusTeal.copy(alpha = 0.15f))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "AES-256-CBC",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = CampusTeal
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (isDecrypted) plainText else "•••• •••• ${plainText.takeLast(4).ifEmpty { "ENCR" }}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { isDecrypted = !isDecrypted },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (isDecrypted) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (isDecrypted) "Mask value" else "Decrypt value",
                    tint = CampusNavyPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
            IconButton(
                onClick = { showCryptoDialog = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Cryptographic integrity details",
                    tint = CampusTeal,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }

    if (showCryptoDialog) {
        AlertDialog(
            onDismissRequest = { showCryptoDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = CampusNavyPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cryptographic Security Info", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "This field is cryptographically protected at rest using hardware-grade symmetric encryption.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider()
                    Text("• Cipher Algorithm: AES/CBC/PKCS5Padding (256-bit)", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Text("• Key Strength: 256-bit institutional cipher key", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Text("• Integrity Digest: SHA-256 hash verified", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Text("• Raw Ciphertext:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = rawEncryptedValue.take(65) + if (rawEncryptedValue.length > 65) "..." else "",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(6.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showCryptoDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun RoleSwitcherDialog(
    currentRole: UserRole,
    students: List<StudentEntity>,
    selectedStudentId: String,
    currentUserAccount: UserAccountEntity? = null,
    onRoleSelected: (UserRole) -> Unit,
    onStudentSelected: (String) -> Unit,
    onLogout: () -> Unit = {},
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Account & View Settings",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (currentUserAccount != null) {
                    Surface(
                        color = CampusNavyPrimary.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = CampusNavyPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = currentUserAccount.fullName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                Text(
                                    text = "${currentUserAccount.role} · Dept: ${currentUserAccount.departmentId}",
                                    fontSize = 11.sp,
                                    color = CampusTeal,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            OutlinedButton(
                                onClick = {
                                    onDismiss()
                                    onLogout()
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Logout", fontSize = 11.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Text(
                    text = "Switch between Faculty Admin and individual Student Portal views.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Faculty Role Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            onRoleSelected(UserRole.FACULTY)
                            onDismiss()
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (currentRole == UserRole.FACULTY)
                            CampusNavyPrimary.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = if (currentRole == UserRole.FACULTY)
                        CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CampusNavyPrimary))
                    else null
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CampusNavyPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SupervisorAccount,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Faculty & Dean Dashboard",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Manage departments, mark attendance, grade submissions, review leaves",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (currentRole == UserRole.FACULTY) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Active",
                                tint = CampusNavyPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Select Student Profile:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                students.take(4).forEach { student ->
                    val isSelected = currentRole == UserRole.STUDENT && selectedStudentId == student.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                onStudentSelected(student.id)
                                onRoleSelected(UserRole.STUDENT)
                                onDismiss()
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                CampusTeal.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        border = if (isSelected)
                            CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CampusTeal))
                        else null
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(CampusTeal),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = student.fullName.take(1),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${student.fullName} (${student.rollNo})",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "${student.departmentId} · Sem ${student.semester} · CGPA: ${student.cgpa} · Att: ${student.attendancePercentage}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = CampusTeal
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
