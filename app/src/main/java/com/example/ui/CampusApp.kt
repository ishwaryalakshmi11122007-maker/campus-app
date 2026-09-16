package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.components.CampusTopAppBar
import com.example.ui.components.NotificationCenterDialog
import com.example.ui.components.RoleSwitcherDialog
import com.example.ui.screens.auth.CampusLoginScreen
import com.example.ui.screens.faculty.AnalyticsDashboardScreen
import com.example.ui.screens.faculty.AttendanceManagementScreen
import com.example.ui.screens.faculty.FacultyDashboardScreen
import com.example.ui.screens.faculty.FacultyDocVerificationScreen
import com.example.ui.screens.faculty.FacultyGradingScreen
import com.example.ui.screens.faculty.FacultyLeaveReviewScreen
import com.example.ui.screens.faculty.MarksManagementScreen
import com.example.ui.screens.faculty.StudentsDirectoryScreen
import com.example.ui.screens.student.StudentAssignmentSubmitScreen
import com.example.ui.screens.student.StudentDocSubmissionScreen
import com.example.ui.screens.student.StudentHomeScreen
import com.example.ui.screens.student.StudentLeaveRequestScreen
import com.example.ui.screens.student.StudentRecordsScreen
import com.example.ui.theme.CampusNavyPrimary
import com.example.ui.theme.CampusTeal
import com.example.ui.viewmodel.CampusViewModel
import com.example.ui.viewmodel.FacultyTab
import com.example.ui.viewmodel.StudentTab
import com.example.ui.viewmodel.UserRole
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun CampusApp(
    viewModel: CampusViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Authentication session state
    val currentUserAccount by viewModel.currentUserAccount.collectAsState()
    val allUserAccounts by viewModel.allUserAccounts.collectAsState()
    val loginError by viewModel.loginError.collectAsState()

    // Request notification permission for Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Collect Toast/Snackbar Messages
    LaunchedEffect(viewModel) {
        viewModel.toastMessage.collectLatest { msg ->
            scope.launch {
                snackbarHostState.showSnackbar(msg)
            }
        }
    }

    // If user is not authenticated, display the Secure Login Screen
    if (currentUserAccount == null) {
        CampusLoginScreen(
            userAccounts = allUserAccounts,
            loginError = loginError,
            onLogin = { username, passwordRaw ->
                viewModel.login(username, passwordRaw)
            }
        )
        return
    }

    // State Collection
    val currentRole by viewModel.currentRole.collectAsState()
    val facultyTab by viewModel.facultyTab.collectAsState()
    val studentTab by viewModel.studentTab.collectAsState()
    val activeStudent by viewModel.activeStudent.collectAsState()
    val activeStudentId by viewModel.selectedStudentId.collectAsState()

    val departments by viewModel.departments.collectAsState()
    val students by viewModel.students.collectAsState()
    val attendanceHistory by viewModel.allAttendance.collectAsState()
    val results by viewModel.allResults.collectAsState()
    val documents by viewModel.allDocuments.collectAsState()
    val assignments by viewModel.assignments.collectAsState()
    val submissions by viewModel.submissions.collectAsState()
    val leaveRequests by viewModel.leaveRequests.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val analytics by viewModel.overallAnalytics.collectAsState()

    // Department-scoped / student-isolated flows
    val authorizedStudents by viewModel.authorizedStudents.collectAsState()
    val authorizedAttendance by viewModel.authorizedAttendance.collectAsState()
    val authorizedDocuments by viewModel.authorizedDocuments.collectAsState()
    val authorizedAssignments by viewModel.authorizedAssignments.collectAsState()
    val authorizedSubmissions by viewModel.authorizedSubmissions.collectAsState()
    val authorizedLeaves by viewModel.authorizedLeaveRequests.collectAsState()

    val activeStudentAttendance by viewModel.activeStudentAttendance.collectAsState()
    val activeStudentResults by viewModel.activeStudentResults.collectAsState()
    val activeStudentDocuments by viewModel.activeStudentDocuments.collectAsState()
    val activeStudentSubmissions by viewModel.activeStudentSubmissions.collectAsState()
    val activeStudentLeaves by viewModel.activeStudentLeaveRequests.collectAsState()

    val selectedDeptFilter by viewModel.selectedDepartmentFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Dialog States
    var showRoleSwitcherDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }
    val unreadNotificationCount = notifications.count { !it.isRead }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                CampusTopAppBar(
                    currentRole = currentRole,
                    activeStudent = activeStudent,
                    unreadNotificationCount = unreadNotificationCount,
                    currentUserAccount = currentUserAccount,
                    onRoleClick = { showRoleSwitcherDialog = true },
                    onNotificationClick = { showNotificationDialog = true },
                    onLogoutClick = { viewModel.logout() }
                )

                // Scrollable sub-header for Faculty view so all 8 departments & modules are 1-tap accessible
                if (currentRole == UserRole.FACULTY) {
                    ScrollableTabRow(
                        selectedTabIndex = facultyTab.ordinal,
                        edgePadding = 12.dp,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = CampusNavyPrimary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FacultyTab.values().forEach { tab ->
                            val label = when (tab) {
                                FacultyTab.OVERVIEW -> "Overview"
                                FacultyTab.STUDENTS -> "Students (${authorizedStudents.size})"
                                FacultyTab.ATTENDANCE -> "Attendance"
                                FacultyTab.MARKS -> "Marks & SGPA"
                                FacultyTab.GRADING -> "Grading (${authorizedSubmissions.count { it.status == "SUBMITTED" }})"
                                FacultyTab.LEAVE_REQUESTS -> "Leaves (${authorizedLeaves.count { it.status == "PENDING" }})"
                                FacultyTab.DOCUMENTS -> "Docs (${authorizedDocuments.count { it.status == "PENDING" }})"
                                FacultyTab.ANALYTICS -> "Analytics"
                            }
                            Tab(
                                selected = facultyTab == tab,
                                onClick = { viewModel.setFacultyTab(tab) },
                                text = {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = if (facultyTab == tab) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                if (currentRole == UserRole.FACULTY) {
                    // Faculty Navigation Items
                    NavigationBarItem(
                        selected = facultyTab == FacultyTab.OVERVIEW,
                        onClick = { viewModel.setFacultyTab(FacultyTab.OVERVIEW) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Overview") },
                        label = { Text("Overview", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CampusNavyPrimary,
                            indicatorColor = CampusNavyPrimary.copy(alpha = 0.15f)
                        )
                    )
                    NavigationBarItem(
                        selected = facultyTab == FacultyTab.ATTENDANCE,
                        onClick = { viewModel.setFacultyTab(FacultyTab.ATTENDANCE) },
                        icon = { Icon(Icons.Default.FactCheck, contentDescription = "Attendance") },
                        label = { Text("Attendance", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CampusNavyPrimary,
                            indicatorColor = CampusNavyPrimary.copy(alpha = 0.15f)
                        )
                    )
                    NavigationBarItem(
                        selected = facultyTab == FacultyTab.STUDENTS,
                        onClick = { viewModel.setFacultyTab(FacultyTab.STUDENTS) },
                        icon = { Icon(Icons.Default.Group, contentDescription = "Students") },
                        label = { Text("Students", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CampusNavyPrimary,
                            indicatorColor = CampusNavyPrimary.copy(alpha = 0.15f)
                        )
                    )
                    NavigationBarItem(
                        selected = facultyTab == FacultyTab.GRADING,
                        onClick = { viewModel.setFacultyTab(FacultyTab.GRADING) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (analytics.totalPendingGrading > 0) {
                                        Badge { Text("${analytics.totalPendingGrading}") }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.RateReview, contentDescription = "Grading")
                            }
                        },
                        label = { Text("Grading", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CampusNavyPrimary,
                            indicatorColor = CampusNavyPrimary.copy(alpha = 0.15f)
                        )
                    )
                    NavigationBarItem(
                        selected = facultyTab == FacultyTab.ANALYTICS,
                        onClick = { viewModel.setFacultyTab(FacultyTab.ANALYTICS) },
                        icon = { Icon(Icons.Default.Insights, contentDescription = "Analytics") },
                        label = { Text("Analytics", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CampusNavyPrimary,
                            indicatorColor = CampusNavyPrimary.copy(alpha = 0.15f)
                        )
                    )
                } else {
                    // Student Portal Navigation Items
                    NavigationBarItem(
                        selected = studentTab == StudentTab.OVERVIEW,
                        onClick = { viewModel.setStudentTab(StudentTab.OVERVIEW) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Portal", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CampusTeal,
                            indicatorColor = CampusTeal.copy(alpha = 0.15f)
                        )
                    )
                    NavigationBarItem(
                        selected = studentTab == StudentTab.ATTENDANCE_MARKS,
                        onClick = { viewModel.setStudentTab(StudentTab.ATTENDANCE_MARKS) },
                        icon = { Icon(Icons.Default.Assessment, contentDescription = "Records") },
                        label = { Text("Records", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CampusTeal,
                            indicatorColor = CampusTeal.copy(alpha = 0.15f)
                        )
                    )
                    NavigationBarItem(
                        selected = studentTab == StudentTab.DOCUMENTS,
                        onClick = { viewModel.setStudentTab(StudentTab.DOCUMENTS) },
                        icon = {
                            val missingCount = activeStudentDocuments.count { doc -> doc.status == "MISSING" }
                            BadgedBox(
                                badge = {
                                    if (missingCount > 0) {
                                        Badge { Text("$missingCount") }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.UploadFile, contentDescription = "Documents")
                            }
                        },
                        label = { Text("Documents", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CampusTeal,
                            indicatorColor = CampusTeal.copy(alpha = 0.15f)
                        )
                    )
                    NavigationBarItem(
                        selected = studentTab == StudentTab.ASSIGNMENTS,
                        onClick = { viewModel.setStudentTab(StudentTab.ASSIGNMENTS) },
                        icon = { Icon(Icons.Default.Assignment, contentDescription = "Assignments") },
                        label = { Text("Assignments", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CampusTeal,
                            indicatorColor = CampusTeal.copy(alpha = 0.15f)
                        )
                    )
                    NavigationBarItem(
                        selected = studentTab == StudentTab.LEAVE,
                        onClick = { viewModel.setStudentTab(StudentTab.LEAVE) },
                        icon = { Icon(Icons.Default.EventNote, contentDescription = "Leaves") },
                        label = { Text("Leaves", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CampusTeal,
                            indicatorColor = CampusTeal.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Crossfade(targetState = currentRole to (if (currentRole == UserRole.FACULTY) facultyTab.name else studentTab.name), label = "screen_fade") { (role, _) ->
                if (role == UserRole.FACULTY) {
                    when (facultyTab) {
                        FacultyTab.OVERVIEW -> {
                            FacultyDashboardScreen(
                                analytics = analytics,
                                departments = departments,
                                students = authorizedStudents,
                                onNavigateTab = { viewModel.setFacultyTab(it) }
                            )
                        }
                        FacultyTab.STUDENTS -> {
                            StudentsDirectoryScreen(
                                students = authorizedStudents,
                                departments = departments,
                                allResults = results,
                                allAttendance = authorizedAttendance,
                                allDocuments = authorizedDocuments,
                                selectedDeptFilter = selectedDeptFilter,
                                searchQuery = searchQuery,
                                onSelectDept = { viewModel.setSelectedDeptFilter(it) },
                                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                onAddStudent = { roll, name, email, phone, dept, sem, sec ->
                                    viewModel.addNewStudent(roll, name, email, phone, dept, sem, sec)
                                },
                                currentUserAccount = currentUserAccount
                            )
                        }
                        FacultyTab.ATTENDANCE -> {
                            AttendanceManagementScreen(
                                departments = departments,
                                students = authorizedStudents,
                                attendanceHistory = authorizedAttendance,
                                onSaveAttendance = { deptId, code, name, date, statusMap ->
                                    viewModel.saveAttendanceSession(deptId, code, name, date, statusMap)
                                },
                                currentUserAccount = currentUserAccount
                            )
                        }
                        FacultyTab.MARKS -> {
                            MarksManagementScreen(
                                students = authorizedStudents,
                                departments = departments,
                                results = results,
                                onAddResult = { studentId, sem, code, name, cred, internal, ext, session ->
                                    viewModel.addAcademicResult(studentId, sem, code, name, cred, internal, ext, session)
                                },
                                currentUserAccount = currentUserAccount
                            )
                        }
                        FacultyTab.GRADING -> {
                            FacultyGradingScreen(
                                assignments = authorizedAssignments,
                                submissions = authorizedSubmissions,
                                onGradeSubmission = { subId, marks, feedback ->
                                    viewModel.gradeSubmission(subId, marks, feedback)
                                }
                            )
                        }
                        FacultyTab.LEAVE_REQUESTS -> {
                            FacultyLeaveReviewScreen(
                                leaveRequests = authorizedLeaves,
                                onReviewLeave = { reqId, isApproved, remarks ->
                                    viewModel.reviewLeaveRequest(reqId, isApproved, remarks)
                                }
                            )
                        }
                        FacultyTab.DOCUMENTS -> {
                            FacultyDocVerificationScreen(
                                documents = authorizedDocuments,
                                students = authorizedStudents,
                                onVerifyDocument = { docId, isApproved, reason, verifier ->
                                    viewModel.verifyDocument(docId, isApproved, reason, verifier)
                                }
                            )
                        }
                        FacultyTab.ANALYTICS -> {
                            AnalyticsDashboardScreen(
                                analytics = analytics,
                                students = authorizedStudents,
                                onSendStudentAlert = { student ->
                                    viewModel.sendPushNotification(
                                        context = context,
                                        title = "Academic Advisory Alert",
                                        message = "Dear ${student.fullName}, attendance is ${student.attendancePercentage}% and CGPA is ${student.cgpa}. Please meet your academic dean.",
                                        category = "EXAM",
                                        studentId = student.id
                                    )
                                }
                            )
                        }
                    }
                } else {
                    // STUDENT PORTAL
                    when (studentTab) {
                        StudentTab.OVERVIEW -> {
                            StudentHomeScreen(
                                student = activeStudent,
                                results = activeStudentResults,
                                documents = activeStudentDocuments,
                                assignments = assignments,
                                notifications = notifications,
                                onNavigateTab = { viewModel.setStudentTab(it) }
                            )
                        }
                        StudentTab.ATTENDANCE_MARKS -> {
                            StudentRecordsScreen(
                                student = activeStudent,
                                results = activeStudentResults,
                                attendance = activeStudentAttendance
                            )
                        }
                        StudentTab.DOCUMENTS -> {
                            StudentDocSubmissionScreen(
                                student = activeStudent,
                                documents = activeStudentDocuments,
                                onSubmitDocument = { studentId, docType, title, docNumber, fileName, fileSize ->
                                    viewModel.submitMissingDocument(studentId, docType, title, docNumber, fileName, fileSize)
                                }
                            )
                        }
                        StudentTab.ASSIGNMENTS -> {
                            StudentAssignmentSubmitScreen(
                                student = activeStudent,
                                assignments = assignments,
                                mySubmissions = activeStudentSubmissions,
                                onSubmitAssignment = { assignId, content, attachName ->
                                    viewModel.submitAssignment(assignId, content, attachName)
                                }
                            )
                        }
                        StudentTab.LEAVE -> {
                            StudentLeaveRequestScreen(
                                student = activeStudent,
                                leaveRequests = activeStudentLeaves,
                                onRequestLeave = { type, start, end, reason ->
                                    viewModel.requestLeave(type, start, end, reason)
                                }
                            )
                        }
                        StudentTab.EXAMS_DEADLINES -> {
                            StudentHomeScreen(
                                student = activeStudent,
                                results = activeStudentResults,
                                documents = activeStudentDocuments,
                                assignments = assignments,
                                notifications = notifications,
                                onNavigateTab = { viewModel.setStudentTab(it) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Role Switcher Dialog
    if (showRoleSwitcherDialog) {
        RoleSwitcherDialog(
            currentRole = currentRole,
            students = authorizedStudents,
            selectedStudentId = activeStudentId,
            currentUserAccount = currentUserAccount,
            onRoleSelected = { viewModel.setUserRole(it) },
            onStudentSelected = { viewModel.setActiveStudentId(it) },
            onLogout = { viewModel.logout() },
            onDismiss = { showRoleSwitcherDialog = false }
        )
    }

    // Notifications Dialog
    if (showNotificationDialog) {
        NotificationCenterDialog(
            notifications = notifications,
            onDismiss = { showNotificationDialog = false },
            onMarkAllRead = { viewModel.markAllNotificationsAsRead() },
            onNotificationClick = { viewModel.markNotificationAsRead(it) },
            onTriggerExamNotification = {
                viewModel.sendPushNotification(
                    context = context,
                    title = "Midterm Examination Schedule Released",
                    message = "CSE/IT Midterm Examinations commence on Oct 01, 2026. Review room allocations.",
                    category = "EXAM",
                    studentId = activeStudentId
                )
            },
            onTriggerDeadlineNotification = {
                viewModel.sendPushNotification(
                    context = context,
                    title = "Assignment Deadline Alert",
                    message = "CS501 Database Indexing Lab Assignment is due in 48 hours. Submit via student portal.",
                    category = "DEADLINE",
                    studentId = activeStudentId
                )
            }
        )
    }
}
