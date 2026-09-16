package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserAccountEntity::class,
        DepartmentEntity::class,
        StudentEntity::class,
        AttendanceRecordEntity::class,
        AcademicResultEntity::class,
        StudentDocumentEntity::class,
        AssignmentEntity::class,
        AssignmentSubmissionEntity::class,
        LeaveRequestEntity::class,
        NotificationEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class CampusDatabase : RoomDatabase() {

    abstract fun campusDao(): CampusDao

    companion object {
        @Volatile
        private var INSTANCE: CampusDatabase? = null

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
                    .fallbackToDestructiveMigration()
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
            dao.insertDepartments(PreloadData.departments)
            dao.insertStudents(PreloadData.students)
            dao.insertAttendanceList(PreloadData.attendanceRecords)
            dao.insertResults(PreloadData.academicResults)
            dao.insertDocuments(PreloadData.studentDocuments)
            dao.insertAssignments(PreloadData.assignments)
            dao.insertSubmissions(PreloadData.assignmentSubmissions)
            dao.insertLeaveRequests(PreloadData.leaveRequests)
            dao.insertNotifications(PreloadData.notifications)
        }
    }
}
