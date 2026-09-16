package com.example.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.StudentDocumentEntity
import com.example.data.model.StudentEntity
import com.example.ui.components.EncryptedFieldViewer
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CampusAmber
import com.example.ui.theme.CampusNavyPrimary
import com.example.ui.theme.CampusTeal
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess

@Composable
fun StudentDocSubmissionScreen(
    student: StudentEntity?,
    documents: List<StudentDocumentEntity>,
    onSubmitDocument: (studentId: String, docType: String, title: String, docNumber: String, fileName: String, fileSizeKb: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSubmitDialog by remember { mutableStateOf(false) }
    var presetDocTitle by remember { mutableStateOf("") }
    var presetDocType by remember { mutableStateOf("") }

    val missingDocs = documents.filter { it.status == "MISSING" }
    val verifiedDocs = documents.filter { it.status == "VERIFIED" }
    val pendingDocs = documents.filter { it.status == "PENDING" }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    presetDocTitle = ""
                    presetDocType = "ACADEMIC"
                    showSubmitDialog = true
                },
                containerColor = CampusNavyPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("submit_document_fab")
            ) {
                Icon(Icons.Default.UploadFile, contentDescription = "Submit Document")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Security Guarantee Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CampusTeal.copy(alpha = 0.1f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CampusTeal.copy(alpha = 0.4f)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CampusTeal),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Secure Document Vault",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = CampusTeal
                            )
                            Text(
                                text = "All certificates are encrypted and directly routed to the college registrar for verification.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Compliance Status Bar
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total Required", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${documents.size}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Verified", fontSize = 11.sp, color = StatusSuccess)
                            Text("${verifiedDocs.size}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = StatusSuccess)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Pending Review", fontSize = 11.sp, color = CampusAmber)
                            Text("${pendingDocs.size}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CampusAmber)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Missing", fontSize = 11.sp, color = StatusDanger)
                            Text("${missingDocs.size}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = StatusDanger)
                        }
                    }
                }
            }

            // Missing Documents Priority Action Group
            if (missingDocs.isNotEmpty()) {
                item {
                    Text(
                        text = "Action Required: Missing Documents",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = StatusDanger
                    )
                }

                items(missingDocs) { doc ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(StatusDanger.copy(alpha = 0.5f)))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
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
                                        .background(StatusDanger.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = StatusDanger,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = doc.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Category: ${doc.docType} · Not on file",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = StatusDanger
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    presetDocTitle = doc.title
                                    presetDocType = doc.docType
                                    showSubmitDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = StatusDanger),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Upload Now", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // All Documents List
            item {
                Text(
                    text = "My Document Records",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(documents) { doc ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
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
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = CampusNavyPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = doc.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Type: ${doc.docType}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            StatusBadge(status = doc.status)
                        }

                        if (doc.status != "MISSING") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    if (doc.isEncrypted && doc.encryptedDocNumber.isNotBlank()) {
                                        EncryptedFieldViewer(
                                            label = "Encrypted Registration / Doc Number",
                                            rawEncryptedValue = doc.encryptedDocNumber
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                    } else if (doc.documentNumber.isNotBlank()) {
                                        Text(
                                            text = "Registration / Doc #: ${doc.documentNumber}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    if (doc.fileUriOrName.isNotBlank()) {
                                        Text(
                                            text = "Attached: ${doc.fileUriOrName} (${doc.fileSizeKb} KB)",
                                            fontSize = 11.sp,
                                            color = CampusNavyPrimary
                                        )
                                    }
                                    if (doc.verifiedBy != null) {
                                        Text(
                                            text = "Verified by: ${doc.verifiedBy} on ${doc.uploadDate}",
                                            fontSize = 10.sp,
                                            color = StatusSuccess
                                        )
                                    }
                                    if (doc.rejectionReason != null) {
                                        Text(
                                            text = "Correction required: ${doc.rejectionReason}",
                                            fontSize = 11.sp,
                                            color = StatusDanger
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Submit Document Dialog
    if (showSubmitDialog && student != null) {
        SubmitDocumentDialog(
            initialTitle = presetDocTitle,
            initialType = presetDocType,
            student = student,
            onDismiss = { showSubmitDialog = false },
            onConfirm = { docType, title, docNumber, fileName, fileSize ->
                onSubmitDocument(student.id, docType, title, docNumber, fileName, fileSize)
                showSubmitDialog = false
            }
        )
    }
}

@Composable
fun SubmitDocumentDialog(
    initialTitle: String,
    initialType: String,
    student: StudentEntity,
    onDismiss: () -> Unit,
    onConfirm: (docType: String, title: String, docNumber: String, fileName: String, fileSize: Int) -> Unit
) {
    var title by remember { mutableStateOf(if (initialTitle.isNotBlank()) initialTitle else "12th Board Certificate") }
    var docType by remember { mutableStateOf(if (initialType.isNotBlank()) initialType else "ACADEMIC") }
    var docNumber by remember { mutableStateOf("") }
    var fileName by remember {
        mutableStateOf("${student.rollNo.lowercase()}_${title.lowercase().replace(" ", "_")}.pdf")
    }
    var isAgreed by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, contentDescription = null, tint = CampusNavyPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Secure Document Upload", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Submitting for student: ${student.fullName} (${student.rollNo})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        fileName = "${student.rollNo.lowercase()}_${it.lowercase().replace(" ", "_")}.pdf"
                    },
                    label = { Text("Document Name *") },
                    placeholder = { Text("e.g. 12th Board Certificate") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = docNumber,
                    onValueChange = { docNumber = it },
                    label = { Text("Document / Certificate Serial # *") },
                    placeholder = { Text("e.g. CBSE-2024-884920") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("Document Attachment File") },
                    leadingIcon = { Icon(Icons.Default.AttachFile, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { isAgreed = !isAgreed }
                ) {
                    Checkbox(
                        checked = isAgreed,
                        onCheckedChange = { isAgreed = it }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "I declare that this document is genuine and unaltered.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && docNumber.isNotBlank() && isAgreed) {
                        onConfirm(docType, title, docNumber, fileName, 780)
                    }
                },
                enabled = title.isNotBlank() && docNumber.isNotBlank() && isAgreed,
                colors = ButtonDefaults.buttonColors(containerColor = CampusNavyPrimary)
            ) {
                Text("Submit Securely")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
