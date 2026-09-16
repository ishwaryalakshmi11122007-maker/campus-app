package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.CampusDatabase
import com.example.data.repository.CampusRepository
import com.example.ui.CampusApp
import com.example.ui.theme.CampusTheme
import com.example.ui.viewmodel.CampusViewModel
import com.example.ui.viewmodel.CampusViewModelFactory
import com.example.util.NotificationHelper

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create notification channels for exams, deadlines, grades
        NotificationHelper.initNotificationChannels(this)

        // Initialize Room Database & Repository
        val database = CampusDatabase.getDatabase(applicationContext)
        val repository = CampusRepository(database.campusDao())
        val viewModelFactory = CampusViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[CampusViewModel::class.java]

        setContent {
            CampusTheme {
                CampusApp(viewModel = viewModel)
            }
        }
    }
}
