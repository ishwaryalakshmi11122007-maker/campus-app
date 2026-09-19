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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuditLogEntity
import com.example.data.model.DepartmentEntity
import com.example.data.model.FacultyMemberEntity
import com.example.data.model.StudentEntity
import com.example.data.model.UserAccountEntity
import com.example.ui.theme.CampusAmber
import com.example.ui.theme.CampusNavyDark
import com.example.ui.theme.CampusNavyPrimary
import com.example.ui.theme.CampusTeal
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.viewmodel.AdminTab
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    departments: List<DepartmentEntity>,
    students: List<StudentEntity>,
    faculty: List<FacultyMemberEntity>,
    userAccounts: List<UserAccountEntity>,
    auditLogs: List<AuditLogEntity>,
    onNavigateTab: (AdminTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeDepts = departments.count { it.status == "ACTIVE" }
    val activeStudents = students.count { it.status == "ACTIVE" }
    val atRiskStudents = students.count { it.status == "AT_RISK" }
    val inactiveStudents = students.count { it.status == "INACTIVE" }
    val activeFaculty = faculty.count { it.status == "ACTIVE" }
    val activeAccounts = userAccounts.count { it.status == "ACTIVE" }
    val disabledAccounts = userAccounts.count { it.status == "DISABLED" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Admin Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CampusNavyPrimary)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin Shield",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "System Administration",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Role-based Governance & Institutional Management",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = CampusTeal.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = "ADMIN",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminStatMetric(
                            title = "Depts",
                            count = "${departments.size}",
                            subtitle = "$activeDepts active",
                            modifier = Modifier.weight(1f)
                        )
                        AdminStatMetric(
                            title = "Students",
                            count = "${students.size}",
                            subtitle = "$activeStudents active",
                            modifier = Modifier.weight(1f)
                        )
                        AdminStatMetric(
                            title = "Faculty",
                            count = "${faculty.size}",
                            subtitle = "$activeFaculty active",
                            modifier = Modifier.weight(1f)
                        )
                        AdminStatMetric(
                            title = "Accounts",
                            count = "${userAccounts.size}",
                            subtitle = "$activeAccounts enabled",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Quick Admin Actions
        item {
            Text(
                text = "Administrative Modules",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = CampusNavyDark
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminQuickActionRow(
                    title = "Department Management",
                    description = "Create, configure, and manage institutional departments & heads",
                    icon = Icons.Default.Business,
                    countText = "${departments.size} departments",
                    iconColor = CampusNavyPrimary,
                    onClick = { onNavigateTab(AdminTab.DEPARTMENTS) }
                )
                AdminQuickActionRow(
                    title = "Student Directory & Enrollment",
                    description = "Enroll students, manage semester status, and deactivate accounts",
                    icon = Icons.Default.School,
                    countText = "${students.size} enrolled",
                    iconColor = CampusTeal,
                    onClick = { onNavigateTab(AdminTab.STUDENTS) }
                )
                AdminQuickActionRow(
                    title = "Faculty & Staff Governance",
                    description = "Appoint faculty members, assign designations, and configure roles",
                    icon = Icons.Default.SupervisorAccount,
                    countText = "${faculty.size} members",
                    iconColor = Color(0xFF7C3AED),
                    onClick = { onNavigateTab(AdminTab.FACULTY) }
                )
                AdminQuickActionRow(
                    title = "User Accounts & Security",
                    description = "Manage credentials, reset passwords, enable/disable access",
                    icon = Icons.Default.ManageAccounts,
                    countText = "${userAccounts.size} accounts",
                    iconColor = CampusAmber,
                    onClick = { onNavigateTab(AdminTab.ACCOUNTS) }
                )
                AdminQuickActionRow(
                    title = "Audit Logs & Security Trail",
                    description = "Comprehensive immutable audit log of administrative mutations",
                    icon = Icons.Default.History,
                    countText = "${auditLogs.size} events",
                    iconColor = Color(0xFF0F766E),
                    onClick = { onNavigateTab(AdminTab.AUDIT_LOGS) }
                )
            }
        }

        // Student Enrollment Health
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Student Academic Health Overview",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = CampusNavyDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Active Students", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "$activeStudents", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = StatusSuccess)
                        }
                        Column {
                            Text(text = "At Risk (<75% att / low CGPA)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "$atRiskStudents", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CampusAmber)
                        }
                        Column {
                            Text(text = "Inactive", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "$inactiveStudents", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = StatusDanger)
                        }
                    }

                    if (students.isNotEmpty()) {
                        val activeFraction = activeStudents.toFloat() / students.size
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { activeFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = StatusSuccess,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }

        // Recent Audit Activity Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Recent System Audit Activity",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = CampusNavyDark
                )
                Text(
                    text = "View All (${auditLogs.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CampusNavyPrimary,
                    modifier = Modifier.clickable { onNavigateTab(AdminTab.AUDIT_LOGS) }
                )
            }
        }

        // Recent 5 Audit Logs
        val recentLogs = auditLogs.take(5)
        if (recentLogs.isEmpty()) {
            item {
                Text(
                    text = "No administrative events logged yet.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            items(recentLogs) { log ->
                AuditLogRowItem(log = log)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AdminStatMetric(
    title: String,
    count: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White.copy(alpha = 0.12f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color.White
            )
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
fun AdminQuickActionRow(
    title: String,
    description: String,
    icon: ImageVector,
    countText: String,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = CampusNavyDark
                    )
                    Text(
                        text = countText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = iconColor
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun AuditLogRowItem(
    log: AuditLogEntity,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(log.timestamp))

    val (badgeBg, badgeText) = when {
        log.actionType.contains("CREATE") || log.actionType.contains("ENROLL") -> Color(0xFF10B981).copy(alpha = 0.15f) to Color(0xFF047857)
        log.actionType.contains("UPDATE") || log.actionType.contains("RESET") -> CampusNavyPrimary.copy(alpha = 0.12f) to CampusNavyPrimary
        log.actionType.contains("DEACTIVATE") || log.actionType.contains("DISABLE") -> StatusDanger.copy(alpha = 0.12f) to StatusDanger
        log.actionType.contains("AUTH") || log.actionType.contains("LOGIN") -> CampusTeal.copy(alpha = 0.15f) to CampusTeal
        else -> CampusAmber.copy(alpha = 0.15f) to CampusAmber
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(badgeBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = log.actionType,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeText
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${log.targetEntity}: ${log.targetId}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = formattedDate,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = log.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "By: ${log.actorName} (${log.actorUserId})",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
