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
import com.example.data.model.DepartmentEntity
import com.example.data.model.LeaveRequestEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.StudentDocumentEntity
import com.example.data.model.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CampusDao {

    // --- DEPARTMENTS ---
    @Query("SELECT * FROM departments ORDER BY name ASC")
    fun getAllDepartments(): Flow<List<DepartmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepartments(departments: List<DepartmentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepartment(department: DepartmentEntity)

    // --- STUDENTS ---
    @Query("SELECT * FROM students ORDER BY rollNo ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE departmentId = :deptId ORDER BY rollNo ASC")
    fun getStudentsByDepartment(deptId: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    fun getStudentById(id: String): Flow<StudentEntity?>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getStudentByIdDirect(id: String): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Update
    suspend fun updateStudent(student: StudentEntity)

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
    fun getAllUserAccounts(): Flow<List<com.example.data.model.UserAccountEntity>>

    @Query("SELECT * FROM user_accounts WHERE LOWER(username) = LOWER(:username) LIMIT 1")
    suspend fun getUserByUsername(username: String): com.example.data.model.UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE studentId = :studentId LIMIT 1")
    suspend fun getUserByStudentId(studentId: String): com.example.data.model.UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccounts(accounts: List<com.example.data.model.UserAccountEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccount(account: com.example.data.model.UserAccountEntity)
}
