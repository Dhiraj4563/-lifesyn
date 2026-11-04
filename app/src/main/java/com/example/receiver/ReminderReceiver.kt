package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.database.AppDatabase
import com.example.data.database.NotificationHistoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "LifeSync Reminder"
        val message = intent.getStringExtra("message") ?: "You have an upcoming activity."
        val type = intent.getStringExtra("type") ?: "TASK"
        val id = intent.getStringExtra("id") ?: System.currentTimeMillis().toString()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "lifesync_reminders_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "LifeSync Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Important task, meeting, and payment alerts"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            id.hashCode(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(id.hashCode(), notification)

        // Record in in-app notification history
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                db.notificationHistoryDao().insertNotification(
                    NotificationHistoryEntity(
                        title = title,
                        message = message,
                        type = type,
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
