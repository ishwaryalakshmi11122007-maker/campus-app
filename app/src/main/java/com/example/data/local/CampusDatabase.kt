package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserAccountEntity::class,
        FacultyMemberEntity::class,
        DepartmentEntity::class,
        StudentEntity::class,
        AttendanceRecordEntity::class,
        AcademicResultEntity::class,
        StudentDocumentEntity::class,
        AssignmentEntity::class,
        AssignmentSubmissionEntity::class,
        LeaveRequestEntity::class,
        NotificationEntity::class,
        AuditLogEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class CampusDatabase : RoomDatabase() {

    abstract fun campusDao(): CampusDao

    companion object {
        @Volatile
        private var INSTANCE: CampusDatabase? = null

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add employeeId, status, createdAt, lastLogin to user_accounts
                db.execSQL("ALTER TABLE user_accounts ADD COLUMN employeeId TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE user_accounts ADD COLUMN status TEXT NOT NULL DEFAULT 'ACTIVE'")
                db.execSQL("ALTER TABLE user_accounts ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE user_accounts ADD COLUMN lastLogin INTEGER DEFAULT NULL")

                // Add status to departments
                db.execSQL("ALTER TABLE departments ADD COLUMN status TEXT NOT NULL DEFAULT 'ACTIVE'")

                // Create faculty_members table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS faculty_members (
                        employeeId TEXT PRIMARY KEY NOT NULL,
                        fullName TEXT NOT NULL,
                        email TEXT NOT NULL,
                        phone TEXT NOT NULL,
                        departmentId TEXT NOT NULL,
                        designation TEXT NOT NULL,
                        username TEXT NOT NULL,
                        status TEXT NOT NULL DEFAULT 'ACTIVE',
                        joinedDate TEXT NOT NULL DEFAULT '2024-01-15'
                    )
                    """.trimIndent()
                )

                // Create audit_logs table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS audit_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        actorUserId TEXT NOT NULL,
                        actorName TEXT NOT NULL,
                        actionType TEXT NOT NULL,
                        targetEntity TEXT NOT NULL,
                        targetId TEXT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        description TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(
            context: Context,
            scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
        ): CampusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CampusDatabase::class.java,
                    "campus_database"
                )
                    .addMigrations(MIGRATION_2_3)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate database in the background on initial creation
                            INSTANCE?.let { database ->
                                scope.launch(Dispatchers.IO) {
                                    populateInitialData(database.campusDao())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialData(dao: CampusDao) {
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
        }
    }
}
