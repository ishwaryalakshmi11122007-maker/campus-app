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
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
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
import com.example.data.model.StudentDocumentEntity
import com.example.data.model.StudentEntity
import com.example.ui.components.EncryptedFieldViewer
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CampusAmber
import com.example.ui.theme.CampusNavyPrimary
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess

@Composable
fun FacultyDocVerificationScreen(
    documents: List<StudentDocumentEntity>,
    students: List<StudentEntity>,
    onVerifyDocument: (docId: Long, isApproved: Boolean, reason: String?, verifier: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var statusFilter by remember { mutableStateOf("PENDING") } // "PENDING", "MISSING", "VERIFIED", "ALL"
    var docToVerify by remember { mutableStateOf<StudentDocumentEntity?>(null) }
    var isApproving by remember { mutableStateOf(true) }

    val filtered = documents.filter { doc ->
        when (statusFilter) {
            "PENDING" -> doc.status == "PENDING"
            "MISSING" -> doc.status == "MISSING"
            "VERIFIED" -> doc.status == "VERIFIED"
            else -> true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val pendingCount = documents.count { it.status == "PENDING" }
            FilterChip(
                selected = statusFilter == "PENDING",
                onClick = { statusFilter = "PENDING" },
                label = { Text("Under Review ($pendingCount)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CampusAmber,
                    selectedLabelColor = Color.White
                )
            )
            val missingCount = documents.count { it.status == "MISSING" }
            FilterChip(
                selected = statusFilter == "MISSING",
                onClick = { statusFilter = "MISSING" },
                label = { Text("Missing ($missingCount)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = StatusDanger,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = statusFilter == "VERIFIED",
                onClick = { statusFilter = "VERIFIED" },
                label = { Text("Verified") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = StatusSuccess,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = statusFilter == "ALL",
                onClick = { statusFilter = "ALL" },
                label = { Text("All (${documents.size})") },
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
                Text("No documents in this view.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered) { doc ->
                    val student = students.find { it.id == doc.studentId }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("doc_card_${doc.id}"),
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(CampusNavyPrimary.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Description,
                                            contentDescription = null,
                                            tint = CampusNavyPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = doc.title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "Student: ${student?.fullName ?: doc.studentId} (${student?.rollNo ?: ""}) · Dept: ${student?.departmentId ?: ""}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                StatusBadge(status = doc.status)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    if (doc.isEncrypted && doc.encryptedDocNumber.isNotBlank()) {
                                        EncryptedFieldViewer(
                                            label = "Encrypted Document / Reg Number",
                                            rawEncryptedValue = doc.encryptedDocNumber
                                        )
                                    } else if (doc.documentNumber.isNotBlank()) {
                                        Text(
                                            text = "Document / Certificate Number: ${doc.documentNumber}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                                        )
                                    }
                                    if (doc.fileUriOrName.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.AttachFile,
                                                contentDescription = null,
                                                tint = CampusNavyPrimary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${doc.fileUriOrName} (${doc.fileSizeKb} KB)",
                                                fontSize = 11.sp,
                                                color = CampusNavyPrimary
                                            )
                                        }
                                    }
                                    if (doc.uploadDate.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Uploaded on: ${doc.uploadDate}",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (doc.rejectionReason != null) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Note: ${doc.rejectionReason}",
                                            fontSize = 11.sp,
                                            color = StatusDanger
                                        )
                                    }
                                }
                            }

                            if (doc.status == "PENDING") {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            docToVerify = doc
                                            isApproving = true
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Verify & Approve")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            docToVerify = doc
                                            isApproving = false
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusDanger)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Reject")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Verification Dialog
    docToVerify?.let { doc ->
        var rejectionReason by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { docToVerify = null },
            title = {
                Text(
                    text = if (isApproving) "Verify Document" else "Reject Document",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Document: ${doc.title} (${doc.documentNumber})",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (!isApproving) {
                        OutlinedTextField(
                            value = rejectionReason,
                            onValueChange = { rejectionReason = it },
                            label = { Text("Reason for Rejection *") },
                            placeholder = { Text("e.g. Scanned copy blurred / signature missing") },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = "This document will be marked as officially verified and archived in the college database.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onVerifyDocument(
                            doc.id,
                            isApproving,
                            if (isApproving) null else rejectionReason.ifBlank { "Rejected by reviewer" },
                            "Faculty Registrar"
                        )
                        docToVerify = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isApproving) StatusSuccess else StatusDanger
                    )
                ) {
                    Text(if (isApproving) "Confirm Verification" else "Confirm Rejection")
                }
            },
            dismissButton = {
                TextButton(onClick = { docToVerify = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
