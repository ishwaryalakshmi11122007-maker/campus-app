package com.example.data.repository

import com.example.data.local.CampusDao
import com.example.data.local.PreloadData
import com.example.data.model.AcademicResultEntity
import com.example.data.model.AssignmentEntity
import com.example.data.model.AssignmentSubmissionEntity
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.DepartmentEntity
import com.example.data.model.LeaveRequestEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.StudentDocumentEntity
import com.example.data.model.StudentEntity
import com.example.data.model.UserAccountEntity
import com.example.util.CampusCrypto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CampusRepository(private val dao: CampusDao) {

    val allDepartments: Flow<List<DepartmentEntity>> = dao.getAllDepartments()
    val allStudents: Flow<List<StudentEntity>> = dao.getAllStudents()
    val allAssignments: Flow<List<AssignmentEntity>> = dao.getAllAssignments()
    val allSubmissions: Flow<List<AssignmentSubmissionEntity>> = dao.getAllSubmissions()
    val allLeaveRequests: Flow<List<LeaveRequestEntity>> = dao.getAllLeaveRequests()
    val allNotifications: Flow<List<NotificationEntity>> = dao.getAllNotifications()
    val allResults: Flow<List<AcademicResultEntity>> = dao.getAllResults()
    val allAttendance: Flow<List<AttendanceRecordEntity>> = dao.getAllAttendance()
    val allDocuments: Flow<List<StudentDocumentEntity>> = dao.getAllDocuments()
    val allUserAccounts: Flow<List<UserAccountEntity>> = dao.getAllUserAccounts()

    suspend fun ensureDataSeeded() {
        val existingDepts = dao.getAllDepartments().firstOrNull()
        if (existingDepts.isNullOrEmpty()) {
            dao.insertUserAccounts(PreloadData.userAccounts)
            dao.insertDepartments(PreloadData.departments)
            dao.insertStudents(PreloadData.students)
            dao.insertAttendanceList(PreloadData.attendanceRecords)
            dao.insertResults(PreloadData.academicResults)
            dao.insertDocuments(PreloadData.studentDocuments)
            dao.insertAssignments(PreloadData.assignments)
            dao.insertSubmissions(PreloadData.assignmentSubmissions)
            dao.insertLeaveRequests(PreloadData.leaveRequests)
            dao.insertNotifications(PreloadData.notifications)
        } else {
            // Ensure accounts are present even if DB had previous data
            val existingAccounts = dao.getAllUserAccounts().firstOrNull()
            if (existingAccounts.isNullOrEmpty()) {
                dao.insertUserAccounts(PreloadData.userAccounts)
            }
        }
    }

    suspend fun authenticateUser(username: String, passwordRaw: String): UserAccountEntity? {
        val trimmed = username.trim()
        val user = dao.getUserByUsername(trimmed) ?: return null
        val expectedHash = CampusCrypto.hashPassword(passwordRaw.trim())
        return if (user.passwordHash == expectedHash || user.plainPasswordHint == passwordRaw.trim()) {
            user
        } else {
            null
        }
    }

    suspend fun getUserForStudent(studentId: String): UserAccountEntity? {
        return dao.getUserByStudentId(studentId)
    }

    fun getStudentsByDepartment(deptId: String): Flow<List<StudentEntity>> =
        dao.getStudentsByDepartment(deptId)

    fun getStudentById(id: String): Flow<StudentEntity?> =
        dao.getStudentById(id)

    fun getAttendanceForStudent(studentId: String): Flow<List<AttendanceRecordEntity>> =
        dao.getAttendanceForStudent(studentId)

    fun getResultsForStudent(studentId: String): Flow<List<AcademicResultEntity>> =
        dao.getResultsForStudent(studentId)

    fun getDocumentsForStudent(studentId: String): Flow<List<StudentDocumentEntity>> =
        dao.getDocumentsForStudent(studentId)

    fun getSubmissionsForStudent(studentId: String): Flow<List<AssignmentSubmissionEntity>> =
        dao.getSubmissionsForStudent(studentId)

    fun getLeaveRequestsForStudent(studentId: String): Flow<List<LeaveRequestEntity>> =
        dao.getLeaveRequestsForStudent(studentId)

    // --- Actions ---

    suspend fun addStudent(student: StudentEntity) {
        dao.insertStudent(student)
        // Add default checklist documents for new student
        val defaultDocs = listOf(
            StudentDocumentEntity(
                studentId = student.id,
                docType = "10TH_MARKSHEET",
                title = "Secondary School Certificate (Class X)",
                status = "MISSING"
            ),
            StudentDocumentEntity(
                studentId = student.id,
                docType = "12TH_CERTIFICATE",
                title = "Higher Secondary Examination (Class XII)",
                status = "MISSING"
            ),
            StudentDocumentEntity(
                studentId = student.id,
                docType = "GOVT_ID",
                title = "National Identity Card / Passport",
                status = "MISSING"
            ),
            StudentDocumentEntity(
                studentId = student.id,
                docType = "FEE_RECEIPT",
                title = "Semester Admission & Fee Receipt",
                status = "MISSING"
            )
        )
        dao.insertDocuments(defaultDocs)
    }

    suspend fun markAttendance(
        studentIds: List<String>,
        deptId: String,
        subjectCode: String,
        subjectName: String,
        date: String,
        statusMap: Map<String, String> // studentId to PRESENT/ABSENT/LATE/EXCUSED
    ) {
        val records = studentIds.map { id ->
            AttendanceRecordEntity(
                studentId = id,
                departmentId = deptId,
                subjectCode = subjectCode,
                subjectName = subjectName,
                date = date,
                status = statusMap[id] ?: "PRESENT"
            )
        }
        dao.insertAttendanceList(records)

        // Recalculate attendance % for affected students
        for (studentId in studentIds) {
            val history = dao.getAttendanceForStudent(studentId).firstOrNull() ?: emptyList()
            if (history.isNotEmpty()) {
                val presentCount = history.count { it.status == "PRESENT" || it.status == "EXCUSED" }
                val lateCount = history.count { it.status == "LATE" }
                val weightedPresent = presentCount + (lateCount * 0.5)
                val newPercentage = (weightedPresent / history.size) * 100.0
                val rounded = Math.round(newPercentage * 10.0) / 10.0

                dao.getStudentByIdDirect(studentId)?.let { currentStudent ->
                    val updatedStatus = if (rounded < 75.0) "AT_RISK" else "ACTIVE"
                    dao.updateStudent(
                        currentStudent.copy(
                            attendancePercentage = rounded,
                            status = updatedStatus
                        )
                    )
                }
            }
        }
    }

    suspend fun submitMissingDocument(
        studentId: String,
        docType: String,
        title: String,
        documentNumber: String,
        fileName: String,
        fileSizeKb: Int
    ) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val encryptedDocNum = CampusCrypto.encrypt(documentNumber)
        val hash = CampusCrypto.sha256Fingerprint("$documentNumber|$fileName|$currentDate")

        // Check if a document record already exists for this type
        val existingDocs = dao.getDocumentsForStudent(studentId).firstOrNull() ?: emptyList()
        val existing = existingDocs.find { it.docType == docType }

        if (existing != null) {
            dao.updateDocument(
                existing.copy(
                    documentNumber = documentNumber,
                    fileUriOrName = fileName,
                    fileSizeKb = fileSizeKb,
                    uploadDate = currentDate,
                    status = "PENDING",
                    rejectionReason = null,
                    isEncrypted = true,
                    encryptionAlgorithm = "AES-256-CBC",
                    encryptedDocNumber = encryptedDocNum,
                    integrityHash = hash
                )
            )
        } else {
            dao.insertDocument(
                StudentDocumentEntity(
                    studentId = studentId,
                    docType = docType,
                    title = title,
                    documentNumber = documentNumber,
                    fileUriOrName = fileName,
                    fileSizeKb = fileSizeKb,
                    uploadDate = currentDate,
                    status = "PENDING",
                    isEncrypted = true,
                    encryptionAlgorithm = "AES-256-CBC",
                    encryptedDocNumber = encryptedDocNum,
                    integrityHash = hash
                )
            )
        }

        // Add a notification for submission
        dao.insertNotification(
            NotificationEntity(
                title = "Document Submitted for Review",
                message = "$title submitted by student $studentId. Awaiting faculty/admin verification.",
                category = "DOCUMENT",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun verifyDocument(docId: Long, isApproved: Boolean, reason: String?, verifierName: String) {
        val allDocs = dao.getAllDocuments().firstOrNull() ?: emptyList()
        val doc = allDocs.find { it.id == docId } ?: return
        dao.updateDocument(
            doc.copy(
                status = if (isApproved) "VERIFIED" else "REJECTED",
                rejectionReason = if (isApproved) null else reason,
                verifiedBy = verifierName
            )
        )
    }

    suspend fun submitAssignment(
        assignmentId: Long,
        studentId: String,
        studentName: String,
        studentRollNo: String,
        contentText: String,
        attachmentName: String
    ) {
        val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        dao.insertSubmission(
            AssignmentSubmissionEntity(
                assignmentId = assignmentId,
                studentId = studentId,
                studentName = studentName,
                studentRollNo = studentRollNo,
                submissionDate = now,
                contentText = contentText,
                attachmentName = attachmentName,
                status = "SUBMITTED"
            )
        )
    }

    suspend fun gradeSubmission(
        submissionId: Long,
        marksObtained: Double,
        feedback: String
    ) {
        val allSubs = dao.getAllSubmissions().firstOrNull() ?: emptyList()
        val sub = allSubs.find { it.id == submissionId } ?: return
        val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        dao.updateSubmission(
            sub.copy(
                marksObtained = marksObtained,
                facultyFeedback = feedback,
                status = "GRADED",
                gradedAt = now
            )
        )

        // Notify student of grading
        dao.insertNotification(
            NotificationEntity(
                title = "Assignment Graded",
                message = "Your submission for assignment #${sub.assignmentId} was evaluated: $marksObtained marks. Feedback: \"$feedback\"",
                category = "DEADLINE",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun submitLeaveRequest(
        studentId: String,
        studentName: String,
        studentRollNo: String,
        departmentId: String,
        startDate: String,
        endDate: String,
        leaveType: String,
        reason: String
    ) {
        val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        dao.insertLeaveRequest(
            LeaveRequestEntity(
                studentId = studentId,
                studentName = studentName,
                studentRollNo = studentRollNo,
                departmentId = departmentId,
                startDate = startDate,
                endDate = endDate,
                leaveType = leaveType,
                reason = reason,
                status = "PENDING",
                appliedDate = now
            )
        )
    }

    suspend fun reviewLeaveRequest(
        requestId: Long,
        isApproved: Boolean,
        remarks: String?
    ) {
        val allRequests = dao.getAllLeaveRequests().firstOrNull() ?: emptyList()
        val request = allRequests.find { it.id == requestId } ?: return
        val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        dao.updateLeaveRequest(
            request.copy(
                status = if (isApproved) "APPROVED" else "REJECTED",
                facultyRemarks = remarks,
                reviewedAt = now
            )
        )

        dao.insertNotification(
            NotificationEntity(
                title = "Leave Request ${if (isApproved) "Approved" else "Declined"}",
                message = "Leave request for ${request.studentRollNo} (${request.startDate} to ${request.endDate}) has been ${if (isApproved) "approved" else "declined"}.",
                category = "ANNOUNCEMENT",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun addAcademicResult(result: AcademicResultEntity) {
        dao.insertResult(result)
        // Recalculate CGPA for the student
        val results = dao.getResultsForStudent(result.studentId).firstOrNull() ?: emptyList()
        if (results.isNotEmpty()) {
            val totalCredits = results.sumOf { it.credits }
            val totalPoints = results.sumOf { it.gradePoints * it.credits }
            if (totalCredits > 0) {
                val newCgpa = Math.round((totalPoints / totalCredits) * 100.0) / 100.0
                dao.getStudentByIdDirect(result.studentId)?.let { currentStudent ->
                    val status = if (currentStudent.attendancePercentage < 75.0 || newCgpa < 5.5) "AT_RISK" else "ACTIVE"
                    dao.updateStudent(
                        currentStudent.copy(
                            cgpa = newCgpa,
                            status = status
                        )
                    )
                }
            }
        }
    }

    suspend fun markNotificationAsRead(id: Long) {
        dao.markNotificationRead(id)
    }

    suspend fun markAllNotificationsAsRead() {
        dao.markAllNotificationsRead()
    }

    suspend fun addNotification(notification: NotificationEntity) {
        dao.insertNotification(notification)
    }

    suspend fun registerUserAccount(account: UserAccountEntity) {
        dao.insertUserAccounts(listOf(account))
    }
}
