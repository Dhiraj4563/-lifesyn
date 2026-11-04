package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.data.database.AppDatabase
import com.example.data.repository.LifeSyncRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LifeSyncApplication : Application() {

    lateinit var repository: LifeSyncRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getInstance(this)
        repository = LifeSyncRepository(database, this)

        createNotificationChannels()

        // Seed initial data if database is fresh
        CoroutineScope(Dispatchers.IO).launch {
            repository.seedDemoDataIfEmpty()
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "lifesync_reminders_channel",
                "LifeSync Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for tasks, meetings, payments, and events"
                enableVibration(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
