package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
import com.example.data.repository.CampusRepository
import com.example.util.CampusCrypto
import com.example.util.NotificationHelper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class UserRole {
    FACULTY,
    STUDENT
}

enum class FacultyTab {
    OVERVIEW,
    STUDENTS,
    ATTENDANCE,
    MARKS,
    GRADING,
    LEAVE_REQUESTS,
    DOCUMENTS,
    ANALYTICS
}

enum class StudentTab {
    OVERVIEW,
    ATTENDANCE_MARKS,
    DOCUMENTS,
    ASSIGNMENTS,
    LEAVE,
    EXAMS_DEADLINES
}

data class DepartmentAnalytics(
    val departmentId: String,
    val departmentName: String,
    val studentCount: Int,
    val averageAttendance: Double,
    val averageCgpa: Double,
    val atRiskCount: Int,
    val passRatePercentage: Double
)

data class CollegeOverallAnalytics(
    val totalStudents: Int,
    val overallAttendance: Double,
    val overallAverageCgpa: Double,
    val totalAtRiskStudents: Int,
    val totalPendingGrading: Int,
    val totalPendingLeaves: Int,
    val totalMissingDocuments: Int,
    val departmentBreakdowns: List<DepartmentAnalytics>
)

class CampusViewModel(
    private val repository: CampusRepository
) : ViewModel() {

    private val _currentRole = MutableStateFlow(UserRole.FACULTY)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Authentication and RBAC (Role-Based Access Control)
    private val _currentUserAccount = MutableStateFlow<UserAccountEntity?>(null)
    val currentUserAccount: StateFlow<UserAccountEntity?> = _currentUserAccount.asStateFlow()

    val allUserAccounts: StateFlow<List<UserAccountEntity>> = repository.allUserAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _selectedStudentId = MutableStateFlow("STU-101")
    val selectedStudentId: StateFlow<String> = _selectedStudentId.asStateFlow()

    private val _selectedDepartmentFilter = MutableStateFlow("ALL")
    val selectedDepartmentFilter: StateFlow<String> = _selectedDepartmentFilter.asStateFlow()

    private val _facultyTab = MutableStateFlow(FacultyTab.OVERVIEW)
    val facultyTab: StateFlow<FacultyTab> = _facultyTab.asStateFlow()

    private val _studentTab = MutableStateFlow(StudentTab.OVERVIEW)
    val studentTab: StateFlow<StudentTab> = _studentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    val departments: StateFlow<List<DepartmentEntity>> = repository.allDepartments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val students: StateFlow<List<StudentEntity>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val assignments: StateFlow<List<AssignmentEntity>> = repository.allAssignments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val submissions: StateFlow<List<AssignmentSubmissionEntity>> = repository.allSubmissions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leaveRequests: StateFlow<List<LeaveRequestEntity>> = repository.allLeaveRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allResults: StateFlow<List<AcademicResultEntity>> = repository.allResults
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttendance: StateFlow<List<AttendanceRecordEntity>> = repository.allAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDocuments: StateFlow<List<StudentDocumentEntity>> = repository.allDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active student derived flows for Student Portal
    val activeStudent: StateFlow<StudentEntity?> = combine(students, _selectedStudentId) { stuList, id ->
        stuList.find { it.id == id } ?: stuList.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeStudentAttendance: StateFlow<List<AttendanceRecordEntity>> = combine(allAttendance, _selectedStudentId) { attList, id ->
        attList.filter { it.studentId == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeStudentResults: StateFlow<List<AcademicResultEntity>> = combine(allResults, _selectedStudentId) { resList, id ->
        resList.filter { it.studentId == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeStudentDocuments: StateFlow<List<StudentDocumentEntity>> = combine(allDocuments, _selectedStudentId) { docList, id ->
        docList.filter { it.studentId == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeStudentSubmissions: StateFlow<List<AssignmentSubmissionEntity>> = combine(submissions, _selectedStudentId) { subList, id ->
        subList.filter { it.studentId == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeStudentLeaveRequests: StateFlow<List<LeaveRequestEntity>> = combine(leaveRequests, _selectedStudentId) { leaveList, id ->
        leaveList.filter { it.studentId == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Scoped Authorization (Department-Level Isolation & Unique Student Access) ---

    // Department staff can ONLY see students belonging to their department.
    // Individual students can ONLY see their own record.
    // Dean can see all departments or filter by department.
    val authorizedStudents: StateFlow<List<StudentEntity>> = combine(
        students,
        _currentUserAccount,
        _selectedDepartmentFilter
    ) { stuList, account, filter ->
        if (account == null) emptyList()
        else if (account.role == "STUDENT") {
            stuList.filter { it.id == (account.studentId ?: "") }
        } else if (account.role == "DEAN" || account.departmentId == "ALL") {
            if (filter == "ALL") stuList else stuList.filter { it.departmentId.equals(filter, ignoreCase = true) }
        } else {
            // Strictly isolated to the staff's department
            stuList.filter { it.departmentId.equals(account.departmentId, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val authorizedAttendance: StateFlow<List<AttendanceRecordEntity>> = combine(
        allAttendance,
        _currentUserAccount
    ) { attList, account ->
        if (account == null) emptyList()
        else if (account.role == "STUDENT") {
            attList.filter { it.studentId == (account.studentId ?: "") }
        } else if (account.role == "DEAN" || account.departmentId == "ALL") {
            attList
        } else {
            attList.filter { it.departmentId.equals(account.departmentId, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val authorizedDocuments: StateFlow<List<StudentDocumentEntity>> = combine(
        allDocuments,
        students,
        _currentUserAccount
    ) { docList, stuList, account ->
        if (account == null) emptyList()
        else if (account.role == "STUDENT") {
            docList.filter { it.studentId == (account.studentId ?: "") }
        } else if (account.role == "DEAN" || account.departmentId == "ALL") {
            docList
        } else {
            val allowedStudentIds = stuList.filter { it.departmentId.equals(account.departmentId, ignoreCase = true) }.map { it.id }.toSet()
            docList.filter { it.studentId in allowedStudentIds }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val authorizedAssignments: StateFlow<List<AssignmentEntity>> = combine(
        assignments,
        students,
        _currentUserAccount
    ) { assignList, stuList, account ->
        if (account == null) emptyList()
        else if (account.role == "STUDENT") {
            val myStu = stuList.find { it.id == (account.studentId ?: "") }
            if (myStu != null) assignList.filter { it.departmentId.equals(myStu.departmentId, ignoreCase = true) } else assignList
        } else if (account.role == "DEAN" || account.departmentId == "ALL") {
            assignList
        } else {
            assignList.filter { it.departmentId.equals(account.departmentId, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val authorizedSubmissions: StateFlow<List<AssignmentSubmissionEntity>> = combine(
        submissions,
        assignments,
        _currentUserAccount
    ) { subList, assignList, account ->
        if (account == null) emptyList()
        else if (account.role == "STUDENT") {
            subList.filter { it.studentId == (account.studentId ?: "") }
        } else if (account.role == "DEAN" || account.departmentId == "ALL") {
            subList
        } else {
            val deptAssignIds = assignList.filter { it.departmentId.equals(account.departmentId, ignoreCase = true) }.map { it.id }.toSet()
            subList.filter { it.assignmentId in deptAssignIds }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val authorizedLeaveRequests: StateFlow<List<LeaveRequestEntity>> = combine(
        leaveRequests,
        _currentUserAccount
    ) { reqList, account ->
        if (account == null) emptyList()
        else if (account.role == "STUDENT") {
            reqList.filter { it.studentId == (account.studentId ?: "") }
        } else if (account.role == "DEAN" || account.departmentId == "ALL") {
            reqList
        } else {
            reqList.filter { it.departmentId.equals(account.departmentId, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Real-time Analytics Combined Flow
    val overallAnalytics: StateFlow<CollegeOverallAnalytics> = combine(
        combine(students, departments) { stu, dept -> stu to dept },
        combine(submissions, leaveRequests, allDocuments) { sub, leave, doc -> Triple(sub, leave, doc) }
    ) { (stuList, deptList), (subList, leaveList, docList) ->
        val totalStudents = stuList.size
        val overallAttendance = if (stuList.isNotEmpty()) {
            Math.round(stuList.map { it.attendancePercentage }.average() * 10.0) / 10.0
        } else 0.0

        val overallCgpa = if (stuList.isNotEmpty()) {
            Math.round(stuList.map { it.cgpa }.average() * 100.0) / 100.0
        } else 0.0

        val atRisk = stuList.count { it.attendancePercentage < 75.0 || it.cgpa < 5.5 }
        val pendingGrading = subList.count { it.status == "SUBMITTED" }
        val pendingLeaves = leaveList.count { it.status == "PENDING" }
        val missingDocs = docList.count { it.status == "MISSING" }

        val deptBreakdowns = deptList.map { dept ->
            val deptStudents = stuList.filter { it.departmentId == dept.id }
            val count = deptStudents.size
            val deptAvgAtt = if (deptStudents.isNotEmpty()) {
                Math.round(deptStudents.map { it.attendancePercentage }.average() * 10.0) / 10.0
            } else 0.0
            val deptAvgCgpa = if (deptStudents.isNotEmpty()) {
                Math.round(deptStudents.map { it.cgpa }.average() * 100.0) / 100.0
            } else 0.0
            val deptAtRisk = deptStudents.count { it.attendancePercentage < 75.0 || it.cgpa < 5.5 }
            val passRate = if (deptStudents.isNotEmpty()) {
                Math.round((deptStudents.count { it.cgpa >= 5.0 }.toDouble() / deptStudents.size) * 1000.0) / 10.0
            } else 100.0

            DepartmentAnalytics(
                departmentId = dept.id,
                departmentName = dept.name,
                studentCount = count,
                averageAttendance = deptAvgAtt,
                averageCgpa = deptAvgCgpa,
                atRiskCount = deptAtRisk,
                passRatePercentage = passRate
            )
        }

        CollegeOverallAnalytics(
            totalStudents = totalStudents,
            overallAttendance = overallAttendance,
            overallAverageCgpa = overallCgpa,
            totalAtRiskStudents = atRisk,
            totalPendingGrading = pendingGrading,
            totalPendingLeaves = pendingLeaves,
            totalMissingDocuments = missingDocs,
            departmentBreakdowns = deptBreakdowns
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        CollegeOverallAnalytics(0, 0.0, 0.0, 0, 0, 0, 0, emptyList())
    )

    init {
        viewModelScope.launch {
            repository.ensureDataSeeded()
        }
    }

    fun setRole(role: UserRole) {
        _currentRole.value = role
    }

    fun setUserRole(role: UserRole) = setRole(role)

    fun setSelectedStudentId(studentId: String) {
        _selectedStudentId.value = studentId
    }

    fun setActiveStudentId(studentId: String) = setSelectedStudentId(studentId)

    fun setDepartmentFilter(deptId: String) {
        _selectedDepartmentFilter.value = deptId
    }

    fun setSelectedDeptFilter(deptId: String) = setDepartmentFilter(deptId)

    fun setFacultyTab(tab: FacultyTab) {
        _facultyTab.value = tab
    }

    fun setStudentTab(tab: StudentTab) {
        _studentTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // --- Authentication & Session Actions ---

    fun login(username: String, passwordRaw: String, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _loginError.value = null
            val account = repository.authenticateUser(username, passwordRaw)
            if (account != null) {
                _currentUserAccount.value = account
                if (account.role == "STUDENT") {
                    _currentRole.value = UserRole.STUDENT
                    account.studentId?.let { id ->
                        _selectedStudentId.value = id
                    }
                } else {
                    _currentRole.value = UserRole.FACULTY
                    _selectedDepartmentFilter.value = account.departmentId
                }
                _toastMessage.emit("Authenticated: ${account.fullName} [${account.departmentId}]")
                onComplete(true)
            } else {
                _loginError.value = "Invalid credentials. Use preset demo accounts below."
                onComplete(false)
            }
        }
    }

    fun logout() {
        _currentUserAccount.value = null
        _loginError.value = null
        _selectedDepartmentFilter.value = "ALL"
    }

    // --- Actions ---

    fun addNewStudent(
        rollNo: String,
        fullName: String,
        email: String,
        phone: String,
        departmentId: String,
        semester: Int,
        section: String
    ) = addStudent(rollNo, fullName, email, phone, departmentId, semester, section)

    fun saveAttendanceSession(
        deptId: String,
        subjectCode: String,
        subjectName: String,
        date: String,
        statusMap: Map<String, String>
    ) = markAttendance(deptId, subjectCode, subjectName, date, statusMap)

    fun requestLeave(leaveType: String, startDate: String, endDate: String, reason: String) {
        val student = activeStudent.value ?: return
        submitLeaveRequest(
            studentId = student.id,
            studentName = student.fullName,
            studentRollNo = student.rollNo,
            departmentId = student.departmentId,
            startDate = startDate,
            endDate = endDate,
            leaveType = leaveType,
            reason = reason
        )
    }

    fun submitAssignment(assignmentId: Long, contentText: String, attachmentName: String) {
        val student = activeStudent.value ?: return
        submitAssignment(
            assignmentId = assignmentId,
            studentId = student.id,
            studentName = student.fullName,
            studentRollNo = student.rollNo,
            contentText = contentText,
            attachmentName = attachmentName
        )
    }

    fun addStudent(
        rollNo: String,
        fullName: String,
        email: String,
        phone: String,
        departmentId: String,
        semester: Int,
        section: String
    ) {
        viewModelScope.launch {
            val account = _currentUserAccount.value
            // Enforce department staff scope: staff can only add to their department
            val assignedDept = if (account != null && account.role == "FACULTY" && account.departmentId != "ALL") {
                account.departmentId
            } else {
                departmentId
            }

            val nationalIdPlain = "NAT-${(1000..9999).random()}-${(1000..9999).random()}"
            val encNationalId = CampusCrypto.encrypt(nationalIdPlain)
            val studentId = "STU-${(100..999).random()}"

            val newStudent = StudentEntity(
                id = studentId,
                rollNo = rollNo,
                fullName = fullName,
                email = email,
                phone = phone,
                departmentId = assignedDept,
                semester = semester,
                section = section,
                cgpa = 7.5,
                attendancePercentage = 100.0,
                status = "ACTIVE",
                avatarColorHex = listOf("#1E3A8A", "#0D9488", "#7C3AED", "#2563EB", "#059669").random(),
                encryptedNationalId = encNationalId,
                isEncrypted = true
            )
            repository.addStudent(newStudent)

            // Register matching student login credentials
            val newAcc = UserAccountEntity(
                id = "ACC-$studentId",
                username = rollNo,
                passwordHash = CampusCrypto.hashPassword(rollNo.lowercase() + "123"),
                plainPasswordHint = rollNo.lowercase() + "123",
                role = "STUDENT",
                fullName = fullName,
                departmentId = assignedDept,
                designation = "B.Tech $assignedDept - Sem $semester",
                studentId = studentId
            )
            repository.registerUserAccount(newAcc)

            _toastMessage.emit("Student $fullName registered for $assignedDept with login: $rollNo")
        }
    }

    fun markAttendance(
        deptId: String,
        subjectCode: String,
        subjectName: String,
        date: String,
        statusMap: Map<String, String>
    ) {
        viewModelScope.launch {
            val account = _currentUserAccount.value
            if (account != null && account.role == "FACULTY" && account.departmentId != "ALL" && !deptId.equals(account.departmentId, ignoreCase = true)) {
                _toastMessage.emit("Denied: You can only record attendance for your department (${account.departmentId})")
                return@launch
            }
            repository.markAttendance(
                studentIds = statusMap.keys.toList(),
                deptId = deptId,
                subjectCode = subjectCode,
                subjectName = subjectName,
                date = date,
                statusMap = statusMap
            )
            _toastMessage.emit("Attendance recorded for ${statusMap.size} students!")
        }
    }

    fun submitMissingDocument(
        studentId: String,
        docType: String,
        title: String,
        documentNumber: String,
        fileName: String,
        fileSizeKb: Int
    ) {
        viewModelScope.launch {
            repository.submitMissingDocument(
                studentId = studentId,
                docType = docType,
                title = title,
                documentNumber = documentNumber,
                fileName = fileName,
                fileSizeKb = fileSizeKb
            )
            _toastMessage.emit("Document '$title' submitted & encrypted with AES-256!")
        }
    }

    fun verifyDocument(docId: Long, isApproved: Boolean, reason: String?, verifierName: String? = null) {
        viewModelScope.launch {
            val verifier = verifierName ?: _currentUserAccount.value?.fullName ?: "Department Faculty"
            repository.verifyDocument(docId, isApproved, reason, verifier)
            _toastMessage.emit("Document ${if (isApproved) "VERIFIED" else "REJECTED"} by $verifier!")
        }
    }

    fun submitAssignment(
        assignmentId: Long,
        studentId: String,
        studentName: String,
        studentRollNo: String,
        contentText: String,
        attachmentName: String
    ) {
        viewModelScope.launch {
            repository.submitAssignment(
                assignmentId = assignmentId,
                studentId = studentId,
                studentName = studentName,
                studentRollNo = studentRollNo,
                contentText = contentText,
                attachmentName = attachmentName
            )
            _toastMessage.emit("Assignment submitted successfully!")
        }
    }

    fun gradeSubmission(submissionId: Long, marks: Double, feedback: String) {
        viewModelScope.launch {
            repository.gradeSubmission(submissionId, marks, feedback)
            _toastMessage.emit("Submission evaluated & grade posted!")
        }
    }

    fun submitLeaveRequest(
        studentId: String,
        studentName: String,
        studentRollNo: String,
        departmentId: String,
        startDate: String,
        endDate: String,
        leaveType: String,
        reason: String
    ) {
        viewModelScope.launch {
            repository.submitLeaveRequest(
                studentId = studentId,
                studentName = studentName,
                studentRollNo = studentRollNo,
                departmentId = departmentId,
                startDate = startDate,
                endDate = endDate,
                leaveType = leaveType,
                reason = reason
            )
            _toastMessage.emit("Leave permission request submitted!")
        }
    }

    fun reviewLeaveRequest(requestId: Long, isApproved: Boolean, remarks: String?) {
        viewModelScope.launch {
            repository.reviewLeaveRequest(requestId, isApproved, remarks)
            _toastMessage.emit("Leave request ${if (isApproved) "APPROVED" else "DECLINED"}!")
        }
    }

    fun addAcademicResult(
        studentId: String,
        semester: Int,
        subjectCode: String,
        subjectName: String,
        credits: Int,
        internalMarks: Double,
        externalMarks: Double,
        examSession: String
    ) {
        viewModelScope.launch {
            val total = internalMarks + externalMarks
            val (grade, points) = when {
                total >= 90 -> "A+" to 10.0
                total >= 80 -> "A" to 9.0
                total >= 70 -> "B+" to 8.0
                total >= 60 -> "B" to 7.0
                total >= 50 -> "C" to 6.0
                total >= 40 -> "P" to 5.0
                else -> "F" to 0.0
            }
            val result = AcademicResultEntity(
                studentId = studentId,
                semester = semester,
                subjectCode = subjectCode,
                subjectName = subjectName,
                credits = credits,
                internalMarks = internalMarks,
                externalMarks = externalMarks,
                totalMarks = total,
                grade = grade,
                gradePoints = points,
                examSession = examSession
            )
            repository.addAcademicResult(result)
            _toastMessage.emit("Marks for $subjectCode added and GPA updated!")
        }
    }

    fun sendPushNotification(
        context: Context,
        title: String,
        message: String,
        category: String,
        studentId: String? = null
    ) {
        viewModelScope.launch {
            val channelId = if (category == "EXAM") {
                NotificationHelper.CHANNEL_EXAMS
            } else {
                NotificationHelper.CHANNEL_DEADLINES
            }
            val notificationId = (System.currentTimeMillis() % 100000).toInt()
            val posted = NotificationHelper.showNotification(
                context = context,
                notificationId = notificationId,
                channelId = channelId,
                title = title,
                message = message
            )
            repository.addNotification(
                NotificationEntity(
                    title = title,
                    message = message,
                    category = category,
                    timestamp = System.currentTimeMillis()
                )
            )
            if (posted) {
                _toastMessage.emit("Push notification posted to system bar!")
            } else {
                _toastMessage.emit("Notification saved (system notifications permission required)")
            }
        }
    }

    fun markNotificationAsRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
            _toastMessage.emit("All notifications marked as read")
        }
    }
}

class CampusViewModelFactory(private val repository: CampusRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CampusViewModel::class.java)) {
            return CampusViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
