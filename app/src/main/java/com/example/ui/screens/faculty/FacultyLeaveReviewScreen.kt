package com.example.ui.screens.faculty

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.AlertDialog
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
import com.example.data.model.LeaveRequestEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CampusAmber
import com.example.ui.theme.CampusNavyPrimary
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess

@Composable
fun FacultyLeaveReviewScreen(
    leaveRequests: List<LeaveRequestEntity>,
    onReviewLeave: (requestId: Long, isApproved: Boolean, remarks: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var statusFilter by remember { mutableStateOf("PENDING") } // "PENDING", "APPROVED", "REJECTED", "ALL"
    var requestToReview by remember { mutableStateOf<LeaveRequestEntity?>(null) }
    var reviewApprovalChoice by remember { mutableStateOf(true) }

    val filtered = leaveRequests.filter { req ->
        when (statusFilter) {
            "PENDING" -> req.status == "PENDING"
            "APPROVED" -> req.status == "APPROVED"
            "REJECTED" -> req.status == "REJECTED"
            else -> true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val pendingCount = leaveRequests.count { it.status == "PENDING" }
            FilterChip(
                selected = statusFilter == "PENDING",
                onClick = { statusFilter = "PENDING" },
                label = { Text("Pending ($pendingCount)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CampusAmber,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = statusFilter == "APPROVED",
                onClick = { statusFilter = "APPROVED" },
                label = { Text("Approved") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = StatusSuccess,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = statusFilter == "REJECTED",
                onClick = { statusFilter = "REJECTED" },
                label = { Text("Declined") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = StatusDanger,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = statusFilter == "ALL",
                onClick = { statusFilter = "ALL" },
                label = { Text("All (${leaveRequests.size})") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CampusNavyPrimary,
                    selectedLabelColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No leave requests in this view.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered) { req ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("leave_card_${req.id}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(1.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(CampusNavyPrimary.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = null,
                                            tint = CampusNavyPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "${req.studentName} (${req.studentRollNo})",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "Dept: ${req.departmentId} · Category: ${req.leaveType}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                StatusBadge(status = req.status)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Duration: ",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "${req.startDate} to ${req.endDate}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Reason: ${req.reason}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            if (req.status == "PENDING") {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            requestToReview = req
                                            reviewApprovalChoice = true
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Approve")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            requestToReview = req
                                            reviewApprovalChoice = false
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusDanger)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Decline")
                                    }
                                }
                            } else if (req.facultyRemarks != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Faculty Remarks: \"${req.facultyRemarks}\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Review Dialog
    requestToReview?.let { req ->
        var remarksText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { requestToReview = null },
            title = {
                Text(
                    text = if (reviewApprovalChoice) "Approve Leave Request" else "Decline Leave Request",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Student: ${req.studentName} (${req.startDate} to ${req.endDate})",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    OutlinedTextField(
                        value = remarksText,
                        onValueChange = { remarksText = it },
                        label = { Text("Faculty Decision Remarks (Optional)") },
                        placeholder = { Text(if (reviewApprovalChoice) "e.g. Approved. Attend makeup tests." else "e.g. Insufficient attendance credits.") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReviewLeave(req.id, reviewApprovalChoice, remarksText.ifBlank { null })
                        requestToReview = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (reviewApprovalChoice) StatusSuccess else StatusDanger
                    )
                ) {
                    Text(if (reviewApprovalChoice) "Confirm Approval" else "Confirm Decline")
                }
            },
            dismissButton = {
                TextButton(onClick = { requestToReview = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
