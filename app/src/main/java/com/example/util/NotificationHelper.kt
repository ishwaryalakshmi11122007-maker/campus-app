package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {

    const val CHANNEL_EXAMS = "campus_exams_channel"
    const val CHANNEL_DEADLINES = "campus_deadlines_channel"

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val examChannel = NotificationChannel(
                CHANNEL_EXAMS,
                "Exams & Academic Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for upcoming semester exams, timetables, and hall assignments"
                enableVibration(true)
            }

            val deadlineChannel = NotificationChannel(
                CHANNEL_DEADLINES,
                "Departmental Deadlines",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Deadlines for assignment submissions, fee clearance, and document uploads"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(examChannel)
            notificationManager.createNotificationChannel(deadlineChannel)
        }
    }

    fun showNotification(
        context: Context,
        notificationId: Int,
        channelId: String,
        title: String,
        message: String
    ): Boolean {
        return try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(context)
            if (notificationManager.areNotificationsEnabled()) {
                notificationManager.notify(notificationId, builder.build())
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
