package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey val id: String, // e.g. "ACC-FAC-CSE", "ACC-STU-101", "ACC-ADMIN-01"
    val username: String, // Roll number (e.g. CS2024001), faculty handle (e.g. vance.cse), or "admin"
    val passwordHash: String, // SHA-256 password hash
    val plainPasswordHint: String = "", // Optional hint for demonstration/temporary record
    val role: String, // "ADMIN", "DEAN", "FACULTY", "STUDENT"
    val fullName: String,
    val departmentId: String, // "CSE", "ECE", "IT", "ME", or "ALL"
    val designation: String = "Staff Member",
    val studentId: String? = null, // Reference to StudentEntity.id if STUDENT
    val employeeId: String? = null, // Reference to FacultyMemberEntity.employeeId if FACULTY/DEAN/ADMIN
    val status: String = "ACTIVE", // "ACTIVE", "DISABLED", "INACTIVE"
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long? = null
)

@Entity(tableName = "faculty_members")
data class FacultyMemberEntity(
    @PrimaryKey val employeeId: String, // e.g. "EMP-CSE-001"
    val fullName: String,
    val email: String,
    val phone: String,
    val departmentId: String,
    val designation: String,
    val username: String,
    val status: String = "ACTIVE", // "ACTIVE", "INACTIVE"
    val joinedDate: String = "2024-01-15"
)

@Entity(tableName = "departments")
data class DepartmentEntity(
    @PrimaryKey val id: String, // e.g. "CSE", "ECE", "ME", "IT", "CE"
    val name: String,
    val code: String,
    val headOfDept: String,
    val totalStudents: Int = 0,
    val buildingRoom: String = "",
    val status: String = "ACTIVE" // "ACTIVE", "INACTIVE"
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actorUserId: String,
    val actorName: String,
    val actionType: String, // "CREATE", "UPDATE", "DEACTIVATE", "REACTIVATE", "LOGIN", "PASSWORD_RESET", "STATUS_CHANGE"
    val targetEntity: String, // "STUDENT", "FACULTY", "DEPARTMENT", "ACCOUNT", "MARKS", "ATTENDANCE"
    val targetId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val description: String
)

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val id: String, // e.g. "STU-101"
    val rollNo: String, // e.g. "CS2024001"
    val fullName: String,
    val email: String,
    val phone: String,
    val departmentId: String, // "CSE", "ECE", "IT", "ME"
    val semester: Int, // 1 to 8
    val section: String = "A",
    val cgpa: Double = 8.0,
    val attendancePercentage: Double = 85.0,
    val status: String = "ACTIVE", // ACTIVE, ON_LEAVE, AT_RISK
    val guardianContact: String = "",
    val avatarColorHex: String = "#1E3A8A",
    val encryptedNationalId: String = "", // AES-256 encrypted national identification
    val isEncrypted: Boolean = true
)

@Entity(tableName = "attendance_records")
data class AttendanceRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val departmentId: String,
    val subjectCode: String,
    val subjectName: String,
    val date: String, // "YYYY-MM-DD"
    val status: String, // "PRESENT", "ABSENT", "LATE", "EXCUSED"
    val periodNumber: Int = 1,
    val markedByFaculty: String = "Faculty"
)

@Entity(tableName = "academic_results")
data class AcademicResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val semester: Int,
    val subjectCode: String,
    val subjectName: String,
    val credits: Int,
    val internalMarks: Double, // out of 40 or 50
    val externalMarks: Double, // out of 60 or 50
    val totalMarks: Double, // out of 100
    val grade: String, // "A+", "A", "B+", "B", "C", "F"
    val gradePoints: Double, // 10.0, 9.0, 8.0, etc.
    val examSession: String // "Spring 2026", "Fall 2025"
)

@Entity(tableName = "student_documents")
data class StudentDocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val docType: String, // "10TH_MARKSHEET", "12TH_CERTIFICATE", "GOVT_ID", "TRANSFER_CERTIFICATE", "FEE_RECEIPT", "MEDICAL_CERTIFICATE", "BONAFIDE"
    val title: String,
    val documentNumber: String = "", // Plaintext or decrypted value
    val fileUriOrName: String = "",
    val fileSizeKb: Int = 0,
    val uploadDate: String = "",
    val status: String, // "VERIFIED", "PENDING", "MISSING", "REJECTED"
    val rejectionReason: String? = null,
    val verifiedBy: String? = null,
    val isEncrypted: Boolean = true,
    val encryptionAlgorithm: String = "AES-256-CBC",
    val encryptedDocNumber: String = "", // Ciphertext at rest
    val integrityHash: String = "" // SHA-256 integrity digest
)

@Entity(tableName = "assignments")
data class AssignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subjectCode: String,
    val subjectName: String,
    val departmentId: String,
    val semester: Int,
    val description: String,
    val dueDate: String,
    val totalMarks: Double = 100.0,
    val facultyName: String
)

@Entity(tableName = "assignment_submissions")
data class AssignmentSubmissionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val assignmentId: Long,
    val studentId: String,
    val studentName: String,
    val studentRollNo: String,
    val submissionDate: String,
    val contentText: String,
    val attachmentName: String = "",
    val status: String = "SUBMITTED", // "SUBMITTED", "GRADED", "LATE"
    val marksObtained: Double? = null,
    val facultyFeedback: String? = null,
    val gradedAt: String? = null
)

@Entity(tableName = "leave_requests")
data class LeaveRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val studentName: String,
    val studentRollNo: String,
    val departmentId: String,
    val startDate: String,
    val endDate: String,
    val leaveType: String, // "MEDICAL", "ACADEMIC", "PERSONAL", "EMERGENCY"
    val reason: String,
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val appliedDate: String,
    val facultyRemarks: String? = null,
    val reviewedAt: String? = null
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val category: String, // "EXAM", "DEADLINE", "ATTENDANCE", "DOCUMENT", "ANNOUNCEMENT"
    val departmentId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val actionTarget: String? = null
)
