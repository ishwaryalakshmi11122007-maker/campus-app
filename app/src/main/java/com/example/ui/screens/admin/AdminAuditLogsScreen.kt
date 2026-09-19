package com.example.ui.screens.admin

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuditLogEntity
import com.example.ui.theme.CampusAmber
import com.example.ui.theme.CampusNavyDark
import com.example.ui.theme.CampusNavyPrimary
import com.example.ui.theme.CampusTeal
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminAuditLogsScreen(
    auditLogs: List<AuditLogEntity>,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTargetFilter by remember { mutableStateOf("ALL") } // ALL, DEPARTMENT, STUDENT, FACULTY, USER_ACCOUNT, AUTH

    val filteredLogs = auditLogs.filter { log ->
        val matchesSearch = log.description.contains(searchQuery, ignoreCase = true) ||
                log.actionType.contains(searchQuery, ignoreCase = true) ||
                log.actorName.contains(searchQuery, ignoreCase = true) ||
                log.targetId.contains(searchQuery, ignoreCase = true)
        val matchesTarget = when (selectedTargetFilter) {
            "DEPARTMENT" -> log.targetEntity == "DEPARTMENT"
            "STUDENT" -> log.targetEntity == "STUDENT"
            "FACULTY" -> log.targetEntity == "FACULTY"
            "USER_ACCOUNT" -> log.targetEntity == "USER_ACCOUNT" || log.targetEntity == "ACCOUNT"
            "AUTH" -> log.targetEntity == "AUTH"
            else -> true
        }
        matchesSearch && matchesTarget
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "System Audit Trail & Security Log",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = CampusNavyDark
                )
                Text(
                    text = "${filteredLogs.size} recorded administrative events",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Search & Target Filter
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search logs by details, actor, or ID...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = CampusNavyPrimary)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("ALL", "DEPARTMENT", "STUDENT", "FACULTY", "USER_ACCOUNT", "AUTH").forEach { filter ->
                    item {
                        FilterChip(
                            selected = selectedTargetFilter == filter,
                            onClick = { selectedTargetFilter = filter },
                            label = { Text(filter, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CampusNavyPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        if (filteredLogs.isEmpty()) {
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
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No audit log entries found",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredLogs) { log ->
                AuditLogCard(log = log)
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AuditLogCard(
    log: AuditLogEntity,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy · HH:mm:ss", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(log.timestamp))

    val (badgeBg, badgeText) = when {
        log.actionType.contains("CREATE") || log.actionType.contains("ENROLL") || log.actionType.contains("APPOINT") ->
            Color(0xFF10B981).copy(alpha = 0.15f) to Color(0xFF047857)
        log.actionType.contains("UPDATE") || log.actionType.contains("RESET") ->
            CampusNavyPrimary.copy(alpha = 0.12f) to CampusNavyPrimary
        log.actionType.contains("DEACTIVATE") || log.actionType.contains("DISABLE") ->
            StatusDanger.copy(alpha = 0.12f) to StatusDanger
        log.actionType.contains("REACTIVATE") || log.actionType.contains("ENABLE") ->
            StatusSuccess.copy(alpha = 0.15f) to StatusSuccess
        log.actionType.contains("AUTH") || log.actionType.contains("LOGIN") ->
            CampusTeal.copy(alpha = 0.15f) to CampusTeal
        else -> CampusAmber.copy(alpha = 0.15f) to CampusAmber
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = log.actionType,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = badgeText
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "[${log.targetEntity}]",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = formattedDate,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = log.description,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Actor: ${log.actorName} (${log.actorUserId})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CampusNavyPrimary
                )

                Text(
                    text = "Target ID: ${log.targetId}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
