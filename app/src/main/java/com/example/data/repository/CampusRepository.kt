package com.example.data.repository

import com.example.data.local.CampusDao
import com.example.data.local.PreloadData
import com.example.data.model.AcademicResultEntity
import com.example.data.model.AssignmentEntity
import com.example.data.model.AssignmentSubmissionEntity
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.AuditLogEntity
import com.example.data.model.DepartmentEntity
import com.example.data.model.FacultyMemberEntity
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

sealed class AuthResult {
    data class Success(val userAccount: UserAccountEntity) : AuthResult()
    data class AccountDeactivated(val message: String) : AuthResult()
    object InvalidCredentials : AuthResult()
}

class CampusRepository(private val dao: CampusDao) {

    val allDepartments: Flow<List<DepartmentEntity>> = dao.getAllDepartments()
    val activeDepartments: Flow<List<DepartmentEntity>> = dao.getActiveDepartments()
    val allStudents: Flow<List<StudentEntity>> = dao.getAllStudents()
    val activeStudents: Flow<List<StudentEntity>> = dao.getActiveStudents()
    val allFacultyMembers: Flow<List<FacultyMemberEntity>> = dao.getAllFacultyMembers()
    val activeFacultyMembers: Flow<List<FacultyMemberEntity>> = dao.getActiveFacultyMembers()
    val allAssignments: Flow<List<AssignmentEntity>> = dao.getAllAssignments()
    val allSubmissions: Flow<List<AssignmentSubmissionEntity>> = dao.getAllSubmissions()
    val allLeaveRequests: Flow<List<LeaveRequestEntity>> = dao.getAllLeaveRequests()
    val allNotifications: Flow<List<NotificationEntity>> = dao.getAllNotifications()
    val allResults: Flow<List<AcademicResultEntity>> = dao.getAllResults()
    val allAttendance: Flow<List<AttendanceRecordEntity>> = dao.getAllAttendance()
    val allDocuments: Flow<List<StudentDocumentEntity>> = dao.getAllDocuments()
    val allUserAccounts: Flow<List<UserAccountEntity>> = dao.getAllUserAccounts()
    val allAuditLogs: Flow<List<AuditLogEntity>> = dao.getAllAuditLogs()

    suspend fun ensureDataSeeded() {
        val existingDepts = dao.getAllDepartments().firstOrNull()
        if (existingDepts.isNullOrEmpty()) {
            dao.insertUserAccounts(PreloadData.userAccounts)
            dao.insertFacultyMembers(PreloadData.facultyMembers)
            dao.insertDepartments(PreloadData.departments)
            dao.insertStudents(PreloadData.students)
            dao.insertAttendanceList(PreloadData.attendanceRecords)
            dao.insertResults(PreloadData.academicResults)
            dao.insertDocuments(PreloadData.studentDocuments)
            dao.insertAssignments(PreloadData.assignments)
            dao.insertSubmissions(PreloadData.assignmentSubmissions)
            dao.insertLeaveRequests(PreloadData.leaveRequests)
            dao.insertNotifications(PreloadData.notifications)
            dao.insertAuditLogs(PreloadData.auditLogs)
        } else {
            // Ensure accounts and faculty are present even if DB had previous data
            val existingAccounts = dao.getAllUserAccounts().firstOrNull()
            if (existingAccounts.isNullOrEmpty()) {
                dao.insertUserAccounts(PreloadData.userAccounts)
            }
            val existingFaculty = dao.getAllFacultyMembers().firstOrNull()
            if (existingFaculty.isNullOrEmpty()) {
                dao.insertFacultyMembers(PreloadData.facultyMembers)
            }
            val existingLogs = dao.getAllAuditLogs().firstOrNull()
            if (existingLogs.isNullOrEmpty()) {
                dao.insertAuditLogs(PreloadData.auditLogs)
            }
        }
    }

    suspend fun authenticateUser(username: String, passwordRaw: String): AuthResult {
        val trimmed = username.trim()
        val user = dao.getUserByUsername(trimmed) ?: return AuthResult.InvalidCredentials
        val expectedHash = CampusCrypto.hashPassword(passwordRaw.trim())
        if (user.passwordHash != expectedHash) {
            return AuthResult.InvalidCredentials
        }
        if (user.status.equals("DISABLED", ignoreCase = true) || user.status.equals("INACTIVE", ignoreCase = true)) {
            return AuthResult.AccountDeactivated("This account has been deactivated or suspended by the Administrator.")
        }
        // Update last login
        dao.updateLastLogin(user.id, System.currentTimeMillis())
        // Log audit
        logAudit(
            actorUserId = user.id,
            actorName = user.fullName,
            actionType = "LOGIN",
            targetEntity = "ACCOUNT",
            targetId = user.id,
            description = "${user.fullName} (${user.role}) logged in successfully."
        )
        return AuthResult.Success(user)
    }

    suspend fun logAudit(
        actorUserId: String,
        actorName: String,
        actionType: String,
        targetEntity: String,
        targetId: String,
        description: String
    ) {
        dao.insertAuditLog(
            AuditLogEntity(
                actorUserId = actorUserId,
                actorName = actorName,
                actionType = actionType,
                targetEntity = targetEntity,
                targetId = targetId,
                timestamp = System.currentTimeMillis(),
                description = description
            )
        )
    }

    suspend fun getUserForStudent(studentId: String): UserAccountEntity? {
        return dao.getUserByStudentId(studentId)
    }

    suspend fun getUserAccountById(id: String): UserAccountEntity? {
        return dao.getUserAccountById(id)
    }

    // --- ADMINISTRATIVE OPERATIONS ---

    // 1. Departments
    suspend fun addDepartment(department: DepartmentEntity, actor: UserAccountEntity?): Result<Unit> {
        val existing = dao.getDepartmentByIdDirect(department.id)
        if (existing != null) {
            return Result.failure(Exception("Department with ID '${department.id}' already exists."))
        }
        val existingCode = dao.getDepartmentByCode(department.code)
        if (existingCode != null) {
            return Result.failure(Exception("Department code '${department.code}' is already assigned to '${existingCode.name}'."))
        }
        dao.insertDepartment(department)
        logAudit(
            actorUserId = actor?.id ?: "SYSTEM",
            actorName = actor?.fullName ?: "Administrator",
            actionType = "CREATE",
            targetEntity = "DEPARTMENT",
            targetId = department.id,
            description = "Created department '${department.name}' (${department.code}) in ${department.buildingRoom}."
        )
        return Result.success(Unit)
    }

    suspend fun updateDepartment(department: DepartmentEntity, actor: UserAccountEntity?): Result<Unit> {
        dao.updateDepartment(department)
        logAudit(
            actorUserId = actor?.id ?: "SYSTEM",
            actorName = actor?.fullName ?: "Administrator",
            actionType = "UPDATE",
            targetEntity = "DEPARTMENT",
            targetId = department.id,
            description = "Updated department details for '${department.name}' (${department.id})."
        )
        return Result.success(Unit)
    }

    suspend fun setDepartmentStatus(departmentId: String, newStatus: String, actor: UserAccountEntity?): Result<Unit> {
        dao.updateDepartmentStatus(departmentId, newStatus)
        logAudit(
            actorUserId = actor?.id ?: "SYSTEM",
            actorName = actor?.fullName ?: "Administrator",
            actionType = if (newStatus == "ACTIVE") "REACTIVATE" else "DEACTIVATE",
            targetEntity = "DEPARTMENT",
            targetId = departmentId,
            description = "Changed department '$departmentId' status to $newStatus."
        )
        return Result.success(Unit)
    }

    // 2. Students
    suspend fun addStudentWithAccount(
        student: StudentEntity,
        createAccount: Boolean,
        tempPasswordRaw: String?,
        actor: UserAccountEntity?
    ): Result<Unit> {
        val existingRoll = dao.getStudentByRollNo(student.rollNo)
        if (existingRoll != null) {
            return Result.failure(Exception("Student with Roll Number '${student.rollNo}' already exists."))
        }
        val existingEmail = dao.getStudentByEmail(student.email)
        if (existingEmail != null) {
            return Result.failure(Exception("Email address '${student.email}' is already registered."))
        }

        dao.insertStudent(student)
        // Add default documents checklist
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

        if (createAccount) {
            val pass = tempPasswordRaw?.trim()?.ifBlank { "welcome123" } ?: "welcome123"
            val account = UserAccountEntity(
                id = "ACC-STU-${student.id.replace("STU-", "")}",
                username = student.rollNo,
                passwordHash = CampusCrypto.hashPassword(pass),
                plainPasswordHint = pass,
                role = "STUDENT",
                fullName = student.fullName,
                departmentId = student.departmentId,
                designation = "B.Tech ${student.departmentId} - Sem ${student.semester}",
                studentId = student.id,
                status = "ACTIVE"
            )
            dao.insertUserAccount(account)
        }

        logAudit(
            actorUserId = actor?.id ?: "SYSTEM",
            actorName = actor?.fullName ?: "Administrator",
            actionType = "CREATE",
            targetEntity = "STUDENT",
            targetId = student.id,
            description = "Enrolled student '${student.fullName}' (Roll: ${student.rollNo}) in department ${student.departmentId}."
        )
        return Result.success(Unit)
    }

    suspend fun updateStudent(student: StudentEntity, actor: UserAccountEntity?): Result<Unit> {
        dao.updateStudent(student)
        // Update user account name if linked
        dao.getUserByStudentId(student.id)?.let { acct ->
            dao.updateUserAccount(
                acct.copy(
                    fullName = student.fullName,
                    departmentId = student.departmentId
                )
            )
        }
        logAudit(
            actorUserId = actor?.id ?: "SYSTEM",
            actorName = actor?.fullName ?: "Administrator",
            actionType = "UPDATE",
            targetEntity = "STUDENT",
            targetId = student.id,
            description = "Updated profile details for student '${student.fullName}' (${student.rollNo})."
        )
        return Result.success(Unit)
    }

    suspend fun setStudentStatus(studentId: String, newStatus: String, actor: UserAccountEntity?): Result<Unit> {
        dao.updateStudentStatus(studentId, newStatus)
        // If deactivated, disable linked account
        if (newStatus == "INACTIVE") {
            dao.getUserByStudentId(studentId)?.let { acct ->
                dao.updateAccountStatus(acct.id, "DISABLED")
            }
        } else if (newStatus == "ACTIVE") {
            dao.getUserByStudentId(studentId)?.let { acct ->
                dao.updateAccountStatus(acct.id, "ACTIVE")
            }
        }
        logAudit(
            actorUserId = actor?.id ?: "SYSTEM",
            actorName = actor?.fullName ?: "Administrator",
            actionType = if (newStatus == "ACTIVE") "REACTIVATE" else "DEACTIVATE",
            targetEntity = "STUDENT",
            targetId = studentId,
            description = "Changed student status for '$studentId' to $newStatus."
        )
        return Result.success(Unit)
    }

    // 3. Faculty & Staff
    suspend fun addFacultyMember(
        faculty: FacultyMemberEntity,
        createAccount: Boolean,
        tempPasswordRaw: String?,
        role: String,
        actor: UserAccountEntity?
    ): Result<Unit> {
        val existingEmp = dao.getFacultyMemberByIdDirect(faculty.employeeId)
        if (existingEmp != null) {
            return Result.failure(Exception("Employee ID '${faculty.employeeId}' already exists."))
        }
        val existingUser = dao.getUserByUsername(faculty.username)
        if (existingUser != null) {
            return Result.failure(Exception("Username '${faculty.username}' is already taken."))
        }

        dao.insertFacultyMember(faculty)

        if (createAccount) {
            val pass = tempPasswordRaw?.trim()?.ifBlank { "faculty123" } ?: "faculty123"
            val account = UserAccountEntity(
                id = "ACC-${faculty.employeeId}",
                username = faculty.username,
                passwordHash = CampusCrypto.hashPassword(pass),
                plainPasswordHint = pass,
                role = role, // "FACULTY", "DEAN", "ADMIN"
                fullName = faculty.fullName,
                departmentId = faculty.departmentId,
                designation = faculty.designation,
                employeeId = faculty.employeeId,
                status = "ACTIVE"
            )
            dao.insertUserAccount(account)
        }

        logAudit(
            actorUserId = actor?.id ?: "SYSTEM",
            actorName = actor?.fullName ?: "Administrator",
            actionType = "CREATE",
            targetEntity = "FACULTY",
            targetId = faculty.employeeId,
            description = "Appointed faculty '${faculty.fullName}' (${faculty.designation}) in department ${faculty.departmentId}."
        )
        return Result.success(Unit)
    }

    suspend fun updateFacultyMember(faculty: FacultyMemberEntity, actor: UserAccountEntity?): Result<Unit> {
        dao.updateFacultyMember(faculty)
        dao.getUserByEmployeeId(faculty.employeeId)?.let { acct ->
            dao.updateUserAccount(
                acct.copy(
                    fullName = faculty.fullName,
                    departmentId = faculty.departmentId,
                    designation = faculty.designation
                )
            )
        }
        logAudit(
            actorUserId = actor?.id ?: "SYSTEM",
            actorName = actor?.fullName ?: "Administrator",
            actionType = "UPDATE",
            targetEntity = "FACULTY",
            targetId = faculty.employeeId,
            description = "Updated faculty profile for '${faculty.fullName}' (${faculty.employeeId})."
        )
        return Result.success(Unit)
    }

    suspend fun setFacultyStatus(employeeId: String, newStatus: String, actor: UserAccountEntity?): Result<Unit> {
        dao.updateFacultyMemberStatus(employeeId, newStatus)
        if (newStatus == "INACTIVE") {
            dao.getUserByEmployeeId(employeeId)?.let { acct ->
                dao.updateAccountStatus(acct.id, "DISABLED")
            }
        } else if (newStatus == "ACTIVE") {
            dao.getUserByEmployeeId(employeeId)?.let { acct ->
                dao.updateAccountStatus(acct.id, "ACTIVE")
            }
        }
        logAudit(
            actorUserId = actor?.id ?: "SYSTEM",
            actorName = actor?.fullName ?: "Administrator",
            actionType = if (newStatus == "ACTIVE") "REACTIVATE" else "DEACTIVATE",
            targetEntity = "FACULTY",
            targetId = employeeId,
            description = "Updated faculty member '$employeeId' status to $newStatus."
        )
        return Result.success(Unit)
    }

    // 4. User Accounts Management
    suspend fun createUserAccount(
        username: String,
        passwordRaw: String,
        role: String,
        fullName: String,
        departmentId: String,
        designation: String,
        studentId: String? = null,
        employeeId: String? = null,
        actor: UserAccountEntity?
    ): Result<Unit> {
        val existing = dao.getUserByUsername(username.trim())
        if (existing != null) {
            return Result.failure(Exception("Username '$username' already exists in the system."))
        }
        val id = "ACC-${System.currentTimeMillis().toString().takeLast(6)}"
        val account = UserAccountEntity(
            id = id,
            username = username.trim(),
            passwordHash = CampusCrypto.hashPassword(passwordRaw.trim()),
            plainPasswordHint = passwordRaw.trim(),
            role = role,
            fullName = fullName.trim(),
            departmentId = departmentId,
            designation = designation.trim(),
            studentId = studentId,
            employeeId = employeeId,
            status = "ACTIVE"
        )
        dao.insertUserAccount(account)
        logAudit(
            actorUserId = actor?.id ?: "SYSTEM",
            actorName = actor?.fullName ?: "Administrator",
            actionType = "CREATE",
            targetEntity = "ACCOUNT",
            targetId = account.id,
            description = "Created user account for '${account.fullName}' (Username: ${account.username}, Role: ${account.role})."
        )
        return Result.success(Unit)
    }

    suspend fun setAccountStatus(accountId: String, newStatus: String, actor: UserAccountEntity?): Result<Unit> {
        dao.updateAccountStatus(accountId, newStatus)
        logAudit(
            actorUserId = actor?.id ?: "SYSTEM",
            actorName = actor?.fullName ?: "Administrator",
            actionType = if (newStatus == "ACTIVE") "REACTIVATE" else "DEACTIVATE",
            targetEntity = "ACCOUNT",
            targetId = accountId,
            description = "Changed account status for '$accountId' to $newStatus."
        )
        return Result.success(Unit)
    }

    suspend fun resetAccountPassword(accountId: String, newPasswordRaw: String, actor: UserAccountEntity?): Result<Unit> {
        val newHash = CampusCrypto.hashPassword(newPasswordRaw.trim())
        dao.updateAccountPassword(accountId, newHash, newPasswordRaw.trim())
        logAudit(
            actorUserId = actor?.id ?: "SYSTEM",
            actorName = actor?.fullName ?: "Administrator",
            actionType = "PASSWORD_RESET",
            targetEntity = "ACCOUNT",
            targetId = accountId,
            description = "Reset temporary password for account ID: $accountId."
        )
        return Result.success(Unit)
    }

    suspend fun changePassword(
        accountId: String,
        currentPasswordRaw: String,
        newPasswordRaw: String
    ): Result<Unit> {
        val account = dao.getUserAccountById(accountId)
            ?: return Result.failure(Exception("Account not found."))
        val currentHash = CampusCrypto.hashPassword(currentPasswordRaw.trim())
        if (account.passwordHash != currentHash) {
            return Result.failure(Exception("Current password is incorrect."))
        }
        val newHash = CampusCrypto.hashPassword(newPasswordRaw.trim())
        dao.updateAccountPassword(accountId, newHash, newPasswordRaw.trim())
        logAudit(
            actorUserId = account.id,
            actorName = account.fullName,
            actionType = "PASSWORD_RESET",
            targetEntity = "ACCOUNT",
            targetId = account.id,
            description = "User ${account.fullName} changed their account password."
        )
        return Result.success(Unit)
    }

    fun getStudentsByDepartment(deptId: String): Flow<List<StudentEntity>> =
        dao.getStudentsByDepartment(deptId)

    fun getFacultyByDepartment(deptId: String): Flow<List<FacultyMemberEntity>> =
        dao.getFacultyByDepartment(deptId)

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

    // --- Legacy / Faculty Actions ---

    suspend fun addStudent(student: StudentEntity) {
        addStudentWithAccount(student, createAccount = true, tempPasswordRaw = "welcome123", actor = null)
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
