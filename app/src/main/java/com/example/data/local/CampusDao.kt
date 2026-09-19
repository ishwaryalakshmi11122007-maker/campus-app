package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface CampusDao {

    // --- DEPARTMENTS ---
    @Query("SELECT * FROM departments ORDER BY name ASC")
    fun getAllDepartments(): Flow<List<DepartmentEntity>>

    @Query("SELECT * FROM departments WHERE status = 'ACTIVE' ORDER BY name ASC")
    fun getActiveDepartments(): Flow<List<DepartmentEntity>>

    @Query("SELECT * FROM departments WHERE id = :id LIMIT 1")
    fun getDepartmentById(id: String): Flow<DepartmentEntity?>

    @Query("SELECT * FROM departments WHERE id = :id LIMIT 1")
    suspend fun getDepartmentByIdDirect(id: String): DepartmentEntity?

    @Query("SELECT * FROM departments WHERE LOWER(code) = LOWER(:code) LIMIT 1")
    suspend fun getDepartmentByCode(code: String): DepartmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepartments(departments: List<DepartmentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepartment(department: DepartmentEntity)

    @Update
    suspend fun updateDepartment(department: DepartmentEntity)

    @Query("UPDATE departments SET status = :status WHERE id = :id")
    suspend fun updateDepartmentStatus(id: String, status: String)

    // --- STUDENTS ---
    @Query("SELECT * FROM students ORDER BY rollNo ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE status = 'ACTIVE' ORDER BY rollNo ASC")
    fun getActiveStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE departmentId = :deptId ORDER BY rollNo ASC")
    fun getStudentsByDepartment(deptId: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    fun getStudentById(id: String): Flow<StudentEntity?>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getStudentByIdDirect(id: String): StudentEntity?

    @Query("SELECT * FROM students WHERE LOWER(rollNo) = LOWER(:rollNo) LIMIT 1")
    suspend fun getStudentByRollNo(rollNo: String): StudentEntity?

    @Query("SELECT * FROM students WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getStudentByEmail(email: String): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Query("UPDATE students SET status = :status WHERE id = :id")
    suspend fun updateStudentStatus(id: String, status: String)

    // --- FACULTY MEMBERS ---
    @Query("SELECT * FROM faculty_members ORDER BY fullName ASC")
    fun getAllFacultyMembers(): Flow<List<FacultyMemberEntity>>

    @Query("SELECT * FROM faculty_members WHERE status = 'ACTIVE' ORDER BY fullName ASC")
    fun getActiveFacultyMembers(): Flow<List<FacultyMemberEntity>>

    @Query("SELECT * FROM faculty_members WHERE departmentId = :deptId ORDER BY fullName ASC")
    fun getFacultyByDepartment(deptId: String): Flow<List<FacultyMemberEntity>>

    @Query("SELECT * FROM faculty_members WHERE employeeId = :employeeId LIMIT 1")
    fun getFacultyMemberById(employeeId: String): Flow<FacultyMemberEntity?>

    @Query("SELECT * FROM faculty_members WHERE employeeId = :employeeId LIMIT 1")
    suspend fun getFacultyMemberByIdDirect(employeeId: String): FacultyMemberEntity?

    @Query("SELECT * FROM faculty_members WHERE LOWER(username) = LOWER(:username) LIMIT 1")
    suspend fun getFacultyByUsername(username: String): FacultyMemberEntity?

    @Query("SELECT * FROM faculty_members WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getFacultyByEmail(email: String): FacultyMemberEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFacultyMember(faculty: FacultyMemberEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFacultyMembers(facultyList: List<FacultyMemberEntity>)

    @Update
    suspend fun updateFacultyMember(faculty: FacultyMemberEntity)

    @Query("UPDATE faculty_members SET status = :status WHERE employeeId = :employeeId")
    suspend fun updateFacultyMemberStatus(employeeId: String, status: String)

    // --- ATTENDANCE ---
    @Query("SELECT * FROM attendance_records WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceForStudent(studentId: String): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance_records WHERE departmentId = :deptId AND date = :date")
    fun getAttendanceByDateAndDept(deptId: String, date: String): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance_records")
    fun getAllAttendance(): Flow<List<AttendanceRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(record: AttendanceRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceList(records: List<AttendanceRecordEntity>)

    // --- RESULTS & MARKS ---
    @Query("SELECT * FROM academic_results WHERE studentId = :studentId ORDER BY semester DESC, subjectCode ASC")
    fun getResultsForStudent(studentId: String): Flow<List<AcademicResultEntity>>

    @Query("SELECT * FROM academic_results ORDER BY id DESC")
    fun getAllResults(): Flow<List<AcademicResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: AcademicResultEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResults(results: List<AcademicResultEntity>)

    @Update
    suspend fun updateResult(result: AcademicResultEntity)

    // --- DOCUMENTS ---
    @Query("SELECT * FROM student_documents WHERE studentId = :studentId ORDER BY id ASC")
    fun getDocumentsForStudent(studentId: String): Flow<List<StudentDocumentEntity>>

    @Query("SELECT * FROM student_documents WHERE status IN ('PENDING', 'MISSING') ORDER BY id DESC")
    fun getActionableDocuments(): Flow<List<StudentDocumentEntity>>

    @Query("SELECT * FROM student_documents ORDER BY id DESC")
    fun getAllDocuments(): Flow<List<StudentDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: StudentDocumentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(docs: List<StudentDocumentEntity>)

    @Update
    suspend fun updateDocument(doc: StudentDocumentEntity)

    // --- ASSIGNMENTS ---
    @Query("SELECT * FROM assignments ORDER BY dueDate ASC")
    fun getAllAssignments(): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE departmentId = :deptId ORDER BY dueDate ASC")
    fun getAssignmentsByDepartment(deptId: String): Flow<List<AssignmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignments(assignments: List<AssignmentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: AssignmentEntity)

    // --- SUBMISSIONS ---
    @Query("SELECT * FROM assignment_submissions ORDER BY id DESC")
    fun getAllSubmissions(): Flow<List<AssignmentSubmissionEntity>>

    @Query("SELECT * FROM assignment_submissions WHERE assignmentId = :assignmentId ORDER BY submissionDate DESC")
    fun getSubmissionsForAssignment(assignmentId: Long): Flow<List<AssignmentSubmissionEntity>>

    @Query("SELECT * FROM assignment_submissions WHERE studentId = :studentId ORDER BY id DESC")
    fun getSubmissionsForStudent(studentId: String): Flow<List<AssignmentSubmissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: AssignmentSubmissionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmissions(submissions: List<AssignmentSubmissionEntity>)

    @Update
    suspend fun updateSubmission(submission: AssignmentSubmissionEntity)

    // --- LEAVE REQUESTS ---
    @Query("SELECT * FROM leave_requests ORDER BY id DESC")
    fun getAllLeaveRequests(): Flow<List<LeaveRequestEntity>>

    @Query("SELECT * FROM leave_requests WHERE studentId = :studentId ORDER BY id DESC")
    fun getLeaveRequestsForStudent(studentId: String): Flow<List<LeaveRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaveRequest(request: LeaveRequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaveRequests(requests: List<LeaveRequestEntity>)

    @Update
    suspend fun updateLeaveRequest(request: LeaveRequestEntity)

    // --- NOTIFICATIONS ---
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsRead()

    // --- USER ACCOUNTS & AUTH ---
    @Query("SELECT * FROM user_accounts ORDER BY role ASC, fullName ASC")
    fun getAllUserAccounts(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM user_accounts WHERE LOWER(username) = LOWER(:username) LIMIT 1")
    suspend fun getUserByUsername(username: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE studentId = :studentId LIMIT 1")
    suspend fun getUserByStudentId(studentId: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE employeeId = :employeeId LIMIT 1")
    suspend fun getUserByEmployeeId(employeeId: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE id = :id LIMIT 1")
    suspend fun getUserAccountById(id: String): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccounts(accounts: List<UserAccountEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccount(account: UserAccountEntity)

    @Update
    suspend fun updateUserAccount(account: UserAccountEntity)

    @Query("UPDATE user_accounts SET status = :status WHERE id = :id")
    suspend fun updateAccountStatus(id: String, status: String)

    @Query("UPDATE user_accounts SET status = :status WHERE LOWER(username) = LOWER(:username)")
    suspend fun updateAccountStatusByUsername(username: String, status: String)

    @Query("UPDATE user_accounts SET passwordHash = :newPasswordHash, plainPasswordHint = :plainHint WHERE id = :id")
    suspend fun updateAccountPassword(id: String, newPasswordHash: String, plainHint: String = "")

    @Query("UPDATE user_accounts SET lastLogin = :timestamp WHERE id = :id")
    suspend fun updateLastLogin(id: String, timestamp: Long)

    // --- AUDIT LOGS ---
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLogs(logs: List<AuditLogEntity>)
}
