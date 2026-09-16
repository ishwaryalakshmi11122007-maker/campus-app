package com.example.data.local

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

object PreloadData {

    val userAccounts = listOf(
        // Department Faculty / Staff Accounts (Restricted to their respective department)
        UserAccountEntity(
            id = "ACC-FAC-CSE",
            username = "vance.cse",
            passwordHash = CampusCrypto.hashPassword("cse123"),
            plainPasswordHint = "cse123",
            role = "FACULTY",
            fullName = "Dr. Robert Vance, Ph.D.",
            departmentId = "CSE",
            designation = "HOD & Professor (CSE)"
        ),
        UserAccountEntity(
            id = "ACC-FAC-ECE",
            username = "rostova.ece",
            passwordHash = CampusCrypto.hashPassword("ece123"),
            plainPasswordHint = "ece123",
            role = "FACULTY",
            fullName = "Dr. Elena Rostova, Ph.D.",
            departmentId = "ECE",
            designation = "HOD & Professor (ECE)"
        ),
        UserAccountEntity(
            id = "ACC-FAC-IT",
            username = "turing.it",
            passwordHash = CampusCrypto.hashPassword("it123"),
            plainPasswordHint = "it123",
            role = "FACULTY",
            fullName = "Prof. Alan Turing Jr.",
            departmentId = "IT",
            designation = "Associate Professor (IT)"
        ),
        UserAccountEntity(
            id = "ACC-FAC-ME",
            username = "sterling.me",
            passwordHash = CampusCrypto.hashPassword("me123"),
            plainPasswordHint = "me123",
            role = "FACULTY",
            fullName = "Dr. Marcus Sterling, M.Tech",
            departmentId = "ME",
            designation = "Associate Professor (ME)"
        ),
        UserAccountEntity(
            id = "ACC-DEAN",
            username = "dean.xavier",
            passwordHash = CampusCrypto.hashPassword("admin123"),
            plainPasswordHint = "admin123",
            role = "DEAN",
            fullName = "Prof. Charles Xavier",
            departmentId = "ALL",
            designation = "Academic Dean / Director"
        ),

        // Individual Student Accounts (Unique access to their own data only)
        UserAccountEntity(
            id = "ACC-STU-101",
            username = "CS2024001",
            passwordHash = CampusCrypto.hashPassword("alex123"),
            plainPasswordHint = "alex123",
            role = "STUDENT",
            fullName = "Alex Mercer",
            departmentId = "CSE",
            designation = "B.Tech CSE - 5th Sem",
            studentId = "STU-101"
        ),
        UserAccountEntity(
            id = "ACC-STU-102",
            username = "CS2024002",
            passwordHash = CampusCrypto.hashPassword("sophia123"),
            plainPasswordHint = "sophia123",
            role = "STUDENT",
            fullName = "Sophia Chen",
            departmentId = "CSE",
            designation = "B.Tech CSE - 5th Sem",
            studentId = "STU-102"
        ),
        UserAccountEntity(
            id = "ACC-STU-103",
            username = "CS2024003",
            passwordHash = CampusCrypto.hashPassword("jordan123"),
            plainPasswordHint = "jordan123",
            role = "STUDENT",
            fullName = "Jordan Reed",
            departmentId = "CSE",
            designation = "B.Tech CSE - 5th Sem",
            studentId = "STU-103"
        ),
        UserAccountEntity(
            id = "ACC-STU-104",
            username = "EC2024011",
            passwordHash = CampusCrypto.hashPassword("ananya123"),
            plainPasswordHint = "ananya123",
            role = "STUDENT",
            fullName = "Ananya Sharma",
            departmentId = "ECE",
            designation = "B.Tech ECE - 3rd Sem",
            studentId = "STU-104"
        ),
        UserAccountEntity(
            id = "ACC-STU-105",
            username = "EC2024012",
            passwordHash = CampusCrypto.hashPassword("devon123"),
            plainPasswordHint = "devon123",
            role = "STUDENT",
            fullName = "Devon Miller",
            departmentId = "ECE",
            designation = "B.Tech ECE - 3rd Sem",
            studentId = "STU-105"
        ),
        UserAccountEntity(
            id = "ACC-STU-106",
            username = "IT2024021",
            passwordHash = CampusCrypto.hashPassword("kavya123"),
            plainPasswordHint = "kavya123",
            role = "STUDENT",
            fullName = "Kavya Patel",
            departmentId = "IT",
            designation = "B.Tech IT - 3rd Sem",
            studentId = "STU-106"
        ),
        UserAccountEntity(
            id = "ACC-STU-108",
            username = "ME2024032",
            passwordHash = CampusCrypto.hashPassword("zoe123"),
            plainPasswordHint = "zoe123",
            role = "STUDENT",
            fullName = "Zoe Washington",
            departmentId = "ME",
            designation = "B.Tech ME - 5th Sem",
            studentId = "STU-108"
        )
    )

    val departments = listOf(
        DepartmentEntity(
            id = "CSE",
            name = "Computer Science & Engineering",
            code = "CS",
            headOfDept = "Dr. Robert Vance, Ph.D.",
            totalStudents = 180,
            buildingRoom = "Tech Block A - 302"
        ),
        DepartmentEntity(
            id = "ECE",
            name = "Electronics & Communication Engg",
            code = "EC",
            headOfDept = "Dr. Elena Rostova, Ph.D.",
            totalStudents = 140,
            buildingRoom = "Signals Hall B - 105"
        ),
        DepartmentEntity(
            id = "IT",
            name = "Information Technology",
            code = "IT",
            headOfDept = "Prof. Alan Turing Jr.",
            totalStudents = 120,
            buildingRoom = "Cyber Core C - 204"
        ),
        DepartmentEntity(
            id = "ME",
            name = "Mechanical Engineering",
            code = "ME",
            headOfDept = "Dr. Marcus Sterling, M.Tech",
            totalStudents = 95,
            buildingRoom = "Foundry Complex D - 110"
        )
    )

    val students = listOf(
        StudentEntity(
            id = "STU-101",
            rollNo = "CS2024001",
            fullName = "Alex Mercer",
            email = "alex.mercer@campus.edu",
            phone = "+1 (555) 234-5678",
            departmentId = "CSE",
            semester = 5,
            section = "A",
            cgpa = 8.92,
            attendancePercentage = 92.5,
            status = "ACTIVE",
            guardianContact = "+1 (555) 901-2345",
            avatarColorHex = "#1D4ED8"
        ),
        StudentEntity(
            id = "STU-102",
            rollNo = "CS2024002",
            fullName = "Sophia Chen",
            email = "sophia.chen@campus.edu",
            phone = "+1 (555) 345-6789",
            departmentId = "CSE",
            semester = 5,
            section = "A",
            cgpa = 9.45,
            attendancePercentage = 96.0,
            status = "ACTIVE",
            guardianContact = "+1 (555) 812-3456",
            avatarColorHex = "#0D9488"
        ),
        StudentEntity(
            id = "STU-103",
            rollNo = "CS2024003",
            fullName = "Jordan Reed",
            email = "jordan.reed@campus.edu",
            phone = "+1 (555) 456-7890",
            departmentId = "CSE",
            semester = 5,
            section = "B",
            cgpa = 6.42,
            attendancePercentage = 68.5, // AT RISK (<75%)
            status = "AT_RISK",
            guardianContact = "+1 (555) 723-4567",
            avatarColorHex = "#EA580C"
        ),
        StudentEntity(
            id = "STU-104",
            rollNo = "EC2024011",
            fullName = "Ananya Sharma",
            email = "ananya.sharma@campus.edu",
            phone = "+1 (555) 567-8901",
            departmentId = "ECE",
            semester = 3,
            section = "A",
            cgpa = 8.78,
            attendancePercentage = 88.0,
            status = "ACTIVE",
            guardianContact = "+1 (555) 634-5678",
            avatarColorHex = "#7C3AED"
        ),
        StudentEntity(
            id = "STU-105",
            rollNo = "EC2024012",
            fullName = "Devon Miller",
            email = "devon.miller@campus.edu",
            phone = "+1 (555) 678-9012",
            departmentId = "ECE",
            semester = 3,
            section = "B",
            cgpa = 5.20, // AT RISK GPA (<5.5)
            attendancePercentage = 71.0, // AT RISK Attendance (<75%)
            status = "AT_RISK",
            guardianContact = "+1 (555) 545-6789",
            avatarColorHex = "#DC2626"
        ),
        StudentEntity(
            id = "STU-106",
            rollNo = "IT2024021",
            fullName = "Kavya Patel",
            email = "kavya.patel@campus.edu",
            phone = "+1 (555) 789-0123",
            departmentId = "IT",
            semester = 7,
            section = "A",
            cgpa = 9.15,
            attendancePercentage = 94.0,
            status = "ACTIVE",
            guardianContact = "+1 (555) 456-7891",
            avatarColorHex = "#059669"
        ),
        StudentEntity(
            id = "STU-107",
            rollNo = "ME2024031",
            fullName = "Liam O'Connor",
            email = "liam.oconnor@campus.edu",
            phone = "+1 (555) 890-1234",
            departmentId = "ME",
            semester = 5,
            section = "A",
            cgpa = 7.85,
            attendancePercentage = 81.5,
            status = "ACTIVE",
            guardianContact = "+1 (555) 367-8902",
            avatarColorHex = "#2563EB"
        ),
        StudentEntity(
            id = "STU-108",
            rollNo = "ME2024032",
            fullName = "Zoe Washington",
            email = "zoe.w@campus.edu",
            phone = "+1 (555) 901-2345",
            departmentId = "ME",
            semester = 5,
            section = "A",
            cgpa = 8.10,
            attendancePercentage = 84.0,
            status = "ACTIVE",
            guardianContact = "+1 (555) 278-9013",
            avatarColorHex = "#4F46E5"
        )
    )

    val attendanceRecords = listOf(
        // Records for Alex Mercer (STU-101)
        AttendanceRecordEntity(
            studentId = "STU-101",
            departmentId = "CSE",
            subjectCode = "CS501",
            subjectName = "Database Management Systems",
            date = "2026-09-15",
            status = "PRESENT",
            periodNumber = 1,
            markedByFaculty = "Dr. R. Vance"
        ),
        AttendanceRecordEntity(
            studentId = "STU-101",
            departmentId = "CSE",
            subjectCode = "CS502",
            subjectName = "Operating Systems",
            date = "2026-09-15",
            status = "PRESENT",
            periodNumber = 2,
            markedByFaculty = "Prof. K. Adams"
        ),
        AttendanceRecordEntity(
            studentId = "STU-101",
            departmentId = "CSE",
            subjectCode = "CS503",
            subjectName = "Design & Analysis of Algorithms",
            date = "2026-09-14",
            status = "PRESENT",
            periodNumber = 1,
            markedByFaculty = "Dr. M. Wright"
        ),
        AttendanceRecordEntity(
            studentId = "STU-101",
            departmentId = "CSE",
            subjectCode = "CS504",
            subjectName = "Computer Networks",
            date = "2026-09-14",
            status = "LATE",
            periodNumber = 3,
            markedByFaculty = "Prof. S. Lee"
        ),
        AttendanceRecordEntity(
            studentId = "STU-101",
            departmentId = "CSE",
            subjectCode = "CS501",
            subjectName = "Database Management Systems",
            date = "2026-09-12",
            status = "PRESENT",
            periodNumber = 1,
            markedByFaculty = "Dr. R. Vance"
        ),
        AttendanceRecordEntity(
            studentId = "STU-101",
            departmentId = "CSE",
            subjectCode = "CS505",
            subjectName = "Software Engineering",
            date = "2026-09-11",
            status = "ABSENT",
            periodNumber = 2,
            markedByFaculty = "Prof. E. Clark"
        ),

        // Records for Jordan Reed (STU-103) - Low attendance student
        AttendanceRecordEntity(
            studentId = "STU-103",
            departmentId = "CSE",
            subjectCode = "CS501",
            subjectName = "Database Management Systems",
            date = "2026-09-15",
            status = "ABSENT",
            periodNumber = 1,
            markedByFaculty = "Dr. R. Vance"
        ),
        AttendanceRecordEntity(
            studentId = "STU-103",
            departmentId = "CSE",
            subjectCode = "CS502",
            subjectName = "Operating Systems",
            date = "2026-09-15",
            status = "ABSENT",
            periodNumber = 2,
            markedByFaculty = "Prof. K. Adams"
        ),
        AttendanceRecordEntity(
            studentId = "STU-103",
            departmentId = "CSE",
            subjectCode = "CS503",
            subjectName = "Design & Analysis of Algorithms",
            date = "2026-09-14",
            status = "PRESENT",
            periodNumber = 1,
            markedByFaculty = "Dr. M. Wright"
        ),

        // Records for Sophia Chen (STU-102)
        AttendanceRecordEntity(
            studentId = "STU-102",
            departmentId = "CSE",
            subjectCode = "CS501",
            subjectName = "Database Management Systems",
            date = "2026-09-15",
            status = "PRESENT",
            periodNumber = 1,
            markedByFaculty = "Dr. R. Vance"
        ),
        AttendanceRecordEntity(
            studentId = "STU-102",
            departmentId = "CSE",
            subjectCode = "CS502",
            subjectName = "Operating Systems",
            date = "2026-09-15",
            status = "PRESENT",
            periodNumber = 2,
            markedByFaculty = "Prof. K. Adams"
        ),

        // Records for Ananya Sharma (STU-104)
        AttendanceRecordEntity(
            studentId = "STU-104",
            departmentId = "ECE",
            subjectCode = "EC301",
            subjectName = "Signals & Systems",
            date = "2026-09-15",
            status = "PRESENT",
            periodNumber = 1,
            markedByFaculty = "Dr. E. Rostova"
        ),
        AttendanceRecordEntity(
            studentId = "STU-104",
            departmentId = "ECE",
            subjectCode = "EC302",
            subjectName = "Analog Circuits",
            date = "2026-09-15",
            status = "PRESENT",
            periodNumber = 2,
            markedByFaculty = "Prof. N. Patel"
        )
    )

    val academicResults = listOf(
        // Alex Mercer (STU-101) - Sem 4 & 5
        AcademicResultEntity(
            studentId = "STU-101",
            semester = 4,
            subjectCode = "CS401",
            subjectName = "Data Structures & Algorithms",
            credits = 4,
            internalMarks = 38.0,
            externalMarks = 56.0,
            totalMarks = 94.0,
            grade = "A+",
            gradePoints = 10.0,
            examSession = "Spring 2026"
        ),
        AcademicResultEntity(
            studentId = "STU-101",
            semester = 4,
            subjectCode = "CS402",
            subjectName = "Computer Organization",
            credits = 3,
            internalMarks = 35.0,
            externalMarks = 52.0,
            totalMarks = 87.0,
            grade = "A",
            gradePoints = 9.0,
            examSession = "Spring 2026"
        ),
        AcademicResultEntity(
            studentId = "STU-101",
            semester = 4,
            subjectCode = "CS403",
            subjectName = "Discrete Mathematics",
            credits = 4,
            internalMarks = 34.0,
            externalMarks = 50.0,
            totalMarks = 84.0,
            grade = "A",
            gradePoints = 9.0,
            examSession = "Spring 2026"
        ),
        AcademicResultEntity(
            studentId = "STU-101",
            semester = 4,
            subjectCode = "CS404",
            subjectName = "Object Oriented Programming Java",
            credits = 4,
            internalMarks = 39.0,
            externalMarks = 55.0,
            totalMarks = 94.0,
            grade = "A+",
            gradePoints = 10.0,
            examSession = "Spring 2026"
        ),
        AcademicResultEntity(
            studentId = "STU-101",
            semester = 4,
            subjectCode = "CS405",
            subjectName = "DSA Practical Laboratory",
            credits = 2,
            internalMarks = 48.0,
            externalMarks = 48.0,
            totalMarks = 96.0,
            grade = "A+",
            gradePoints = 10.0,
            examSession = "Spring 2026"
        ),

        // Sophia Chen (STU-102)
        AcademicResultEntity(
            studentId = "STU-102",
            semester = 4,
            subjectCode = "CS401",
            subjectName = "Data Structures & Algorithms",
            credits = 4,
            internalMarks = 40.0,
            externalMarks = 58.0,
            totalMarks = 98.0,
            grade = "A+",
            gradePoints = 10.0,
            examSession = "Spring 2026"
        ),
        AcademicResultEntity(
            studentId = "STU-102",
            semester = 4,
            subjectCode = "CS402",
            subjectName = "Computer Organization",
            credits = 3,
            internalMarks = 38.0,
            externalMarks = 56.0,
            totalMarks = 94.0,
            grade = "A+",
            gradePoints = 10.0,
            examSession = "Spring 2026"
        ),

        // Jordan Reed (STU-103)
        AcademicResultEntity(
            studentId = "STU-103",
            semester = 4,
            subjectCode = "CS401",
            subjectName = "Data Structures & Algorithms",
            credits = 4,
            internalMarks = 22.0,
            externalMarks = 35.0,
            totalMarks = 57.0,
            grade = "C",
            gradePoints = 6.0,
            examSession = "Spring 2026"
        ),
        AcademicResultEntity(
            studentId = "STU-103",
            semester = 4,
            subjectCode = "CS402",
            subjectName = "Computer Organization",
            credits = 3,
            internalMarks = 20.0,
            externalMarks = 31.0,
            totalMarks = 51.0,
            grade = "C",
            gradePoints = 5.0,
            examSession = "Spring 2026"
        )
    )

    val studentDocuments = listOf(
        // Alex Mercer (STU-101) - some verified, 1 missing!
        StudentDocumentEntity(
            studentId = "STU-101",
            docType = "10TH_MARKSHEET",
            title = "Secondary School Certificate (Class X)",
            documentNumber = "SSC-2021-98712",
            fileUriOrName = "alex_10th_marksheet.pdf",
            fileSizeKb = 840,
            uploadDate = "2024-08-10",
            status = "VERIFIED",
            verifiedBy = "Registrar Academic"
        ),
        StudentDocumentEntity(
            studentId = "STU-101",
            docType = "12TH_CERTIFICATE",
            title = "Higher Secondary Examination (Class XII)",
            documentNumber = "HSC-2023-44120",
            fileUriOrName = "alex_12th_certificate.pdf",
            fileSizeKb = 1120,
            uploadDate = "2024-08-10",
            status = "VERIFIED",
            verifiedBy = "Registrar Academic"
        ),
        StudentDocumentEntity(
            studentId = "STU-101",
            docType = "GOVT_ID",
            title = "National Identity Card / Passport",
            documentNumber = "ID-9081-3321",
            fileUriOrName = "alex_national_id.pdf",
            fileSizeKb = 450,
            uploadDate = "2024-08-11",
            status = "VERIFIED",
            verifiedBy = "Dean Student Affairs"
        ),
        StudentDocumentEntity(
            studentId = "STU-101",
            docType = "TRANSFER_CERTIFICATE",
            title = "College Transfer Certificate (TC)",
            documentNumber = "",
            fileUriOrName = "",
            fileSizeKb = 0,
            uploadDate = "",
            status = "MISSING", // MISSING! Shows prominently in Student Portal
            rejectionReason = "Not yet submitted by student during admission"
        ),
        StudentDocumentEntity(
            studentId = "STU-101",
            docType = "MEDICAL_CERTIFICATE",
            title = "Campus Health & Immunization Record",
            documentNumber = "MED-2024-088",
            fileUriOrName = "alex_immunization_form.pdf",
            fileSizeKb = 610,
            uploadDate = "2026-09-10",
            status = "PENDING", // PENDING verification by faculty
            verifiedBy = null
        ),

        // Sophia Chen (STU-102) - All verified
        StudentDocumentEntity(
            studentId = "STU-102",
            docType = "10TH_MARKSHEET",
            title = "Secondary School Certificate (Class X)",
            documentNumber = "SSC-2021-99831",
            fileUriOrName = "sophia_10th.pdf",
            fileSizeKb = 910,
            uploadDate = "2024-08-09",
            status = "VERIFIED",
            verifiedBy = "Registrar Academic"
        ),
        StudentDocumentEntity(
            studentId = "STU-102",
            docType = "FEE_RECEIPT",
            title = "Semester V Tuition Fee Receipt",
            documentNumber = "REC-2026-5541",
            fileUriOrName = "sophia_sem5_fee.pdf",
            fileSizeKb = 320,
            uploadDate = "2026-08-01",
            status = "VERIFIED",
            verifiedBy = "Finance Bureau"
        ),

        // Jordan Reed (STU-103) - Missing multiple docs
        StudentDocumentEntity(
            studentId = "STU-103",
            docType = "FEE_RECEIPT",
            title = "Semester V Tuition Fee Receipt",
            documentNumber = "",
            fileUriOrName = "",
            fileSizeKb = 0,
            uploadDate = "",
            status = "MISSING",
            rejectionReason = "Pending payment verification"
        ),
        StudentDocumentEntity(
            studentId = "STU-103",
            docType = "MEDICAL_CERTIFICATE",
            title = "Campus Health & Immunization Record",
            documentNumber = "",
            fileUriOrName = "",
            fileSizeKb = 0,
            uploadDate = "",
            status = "MISSING",
            rejectionReason = "Mandatory for campus residency"
        )
    )

    val assignments = listOf(
        AssignmentEntity(
            id = 1,
            title = "B+ Tree Indexing & Query Optimizer Lab",
            subjectCode = "CS501",
            subjectName = "Database Management Systems",
            departmentId = "CSE",
            semester = 5,
            description = "Implement a functional disk-backed B+ tree index structure in Kotlin/Java. Include range search tests and node splitting benchmarks.",
            dueDate = "2026-09-25",
            totalMarks = 25.0,
            facultyName = "Dr. Robert Vance"
        ),
        AssignmentEntity(
            id = 2,
            title = "Deadlock Detection Algorithm Simulation",
            subjectCode = "CS502",
            subjectName = "Operating Systems",
            departmentId = "CSE",
            semester = 5,
            description = "Simulate Banker's Resource-Allocation Graph algorithm with multiple resource instances. Provide execution trace screenshots and code documentation.",
            dueDate = "2026-09-30",
            totalMarks = 30.0,
            facultyName = "Prof. K. Adams"
        ),
        AssignmentEntity(
            id = 3,
            title = "TCP Congestion Control Comparative Analysis",
            subjectCode = "CS504",
            subjectName = "Computer Networks",
            departmentId = "CSE",
            semester = 5,
            description = "Analyze throughput differences between TCP Tahoe, Reno, and BBR under varying simulated packet loss rates (0.5% to 5%).",
            dueDate = "2026-10-05",
            totalMarks = 20.0,
            facultyName = "Prof. S. Lee"
        ),
        AssignmentEntity(
            id = 4,
            title = "Fourier Transform Filtering Circuit Report",
            subjectCode = "EC301",
            subjectName = "Signals & Systems",
            departmentId = "ECE",
            semester = 3,
            description = "Design an active Butterworth low-pass filter and plot its frequency response curve using MATLAB/SPICE.",
            dueDate = "2026-09-28",
            totalMarks = 25.0,
            facultyName = "Dr. Elena Rostova"
        )
    )

    val assignmentSubmissions = listOf(
        // Submission 1 - Graded
        AssignmentSubmissionEntity(
            id = 1,
            assignmentId = 1,
            studentId = "STU-101",
            studentName = "Alex Mercer",
            studentRollNo = "CS2024001",
            submissionDate = "2026-09-14 18:30",
            contentText = "Implemented 3-level B+ Tree with concurrent read locks and split propagation. All test suites passing with 100,000 inserted keys in 340ms.",
            attachmentName = "alex_mercer_bplus_tree_impl.zip",
            status = "GRADED",
            marksObtained = 24.5,
            facultyFeedback = "Exceptional clean implementation. Good handling of edge cases during node rebalancing.",
            gradedAt = "2026-09-15 11:00"
        ),
        // Submission 2 - Pending Grading by Faculty
        AssignmentSubmissionEntity(
            id = 2,
            assignmentId = 1,
            studentId = "STU-102",
            studentName = "Sophia Chen",
            studentRollNo = "CS2024002",
            submissionDate = "2026-09-15 14:15",
            contentText = "Complete B+ tree implementation using memory-mapped buffer pools. Included comprehensive benchmark charts against standard B-tree.",
            attachmentName = "sophia_chen_bptree_project.tar.gz",
            status = "SUBMITTED",
            marksObtained = null,
            facultyFeedback = null,
            gradedAt = null
        ),
        // Submission 3 - Pending Grading by Faculty
        AssignmentSubmissionEntity(
            id = 3,
            assignmentId = 2,
            studentId = "STU-101",
            studentName = "Alex Mercer",
            studentRollNo = "CS2024001",
            submissionDate = "2026-09-15 20:00",
            contentText = "Implemented Banker's Algorithm with interactive matrix inputs and visual safety sequence generator in Kotlin console.",
            attachmentName = "alex_mercer_bankers_algo.kt",
            status = "SUBMITTED",
            marksObtained = null,
            facultyFeedback = null,
            gradedAt = null
        )
    )

    val leaveRequests = listOf(
        LeaveRequestEntity(
            id = 1,
            studentId = "STU-101",
            studentName = "Alex Mercer",
            studentRollNo = "CS2024001",
            departmentId = "CSE",
            startDate = "2026-09-22",
            endDate = "2026-09-24",
            leaveType = "ACADEMIC",
            reason = "Representing university at the National Inter-College Hackathon Finals in Seattle.",
            status = "PENDING",
            appliedDate = "2026-09-15 09:30",
            facultyRemarks = null,
            reviewedAt = null
        ),
        LeaveRequestEntity(
            id = 2,
            studentId = "STU-104",
            studentName = "Ananya Sharma",
            studentRollNo = "EC2024011",
            departmentId = "ECE",
            startDate = "2026-09-10",
            endDate = "2026-09-12",
            leaveType = "MEDICAL",
            reason = "Severe viral fever and physician recommended bed rest.",
            status = "APPROVED",
            appliedDate = "2026-09-09 14:00",
            facultyRemarks = "Approved. Ensure lab backlogs are completed during makeup slots.",
            reviewedAt = "2026-09-09 16:30"
        ),
        LeaveRequestEntity(
            id = 3,
            studentId = "STU-103",
            studentName = "Jordan Reed",
            studentRollNo = "CS2024003",
            departmentId = "CSE",
            startDate = "2026-09-18",
            endDate = "2026-09-20",
            leaveType = "PERSONAL",
            reason = "Family wedding out of state.",
            status = "PENDING",
            appliedDate = "2026-09-14 11:20",
            facultyRemarks = null,
            reviewedAt = null
        )
    )

    val notifications = listOf(
        NotificationEntity(
            id = 1,
            title = "Upcoming Mid-Semester Examination",
            message = "Mid-Term exams for CS501 Database Management Systems will commence on Oct 08, 2026 at 10:00 AM (Hall A-301).",
            category = "EXAM",
            departmentId = "CSE",
            timestamp = System.currentTimeMillis() - 3600000 * 4,
            isRead = false,
            actionTarget = "EXAM_SCHEDULE"
        ),
        NotificationEntity(
            id = 2,
            title = "Departmental Deadline: Assignment 1 Due",
            message = "Assignment 1 (B+ Tree Indexing) submission closes on Sept 25, 2026, 11:59 PM. Late penalties apply after grace period.",
            category = "DEADLINE",
            departmentId = "CSE",
            timestamp = System.currentTimeMillis() - 3600000 * 12,
            isRead = false,
            actionTarget = "ASSIGNMENTS"
        ),
        NotificationEntity(
            id = 3,
            title = "Urgent: Missing Document Required",
            message = "Your College Transfer Certificate (TC) is pending verification. Please submit scanned document via the Secure Student Portal.",
            category = "DOCUMENT",
            departmentId = null,
            timestamp = System.currentTimeMillis() - 3600000 * 24,
            isRead = false,
            actionTarget = "DOCUMENTS"
        ),
        NotificationEntity(
            id = 4,
            title = "Annual Research Symposium Registration",
            message = "All final and pre-final year students can submit abstracts for IEEE Student Technical Colloquium before Oct 15, 2026.",
            category = "ANNOUNCEMENT",
            departmentId = null,
            timestamp = System.currentTimeMillis() - 3600000 * 48,
            isRead = true,
            actionTarget = "ANNOUNCEMENTS"
        )
    )
}
