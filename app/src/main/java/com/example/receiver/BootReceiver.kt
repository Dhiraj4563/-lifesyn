package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.database.AppDatabase
import com.example.notification.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getInstance(context)
                    val now = System.currentTimeMillis()
                    val dueReminders = db.reminderDao().getDueReminders(now + 30L * 24 * 3600 * 1000)
                    for (reminder in dueReminders) {
                        if (reminder.triggerTimestamp > now) {
                            ReminderScheduler.scheduleReminder(
                                context = context,
                                id = reminder.id,
                                title = reminder.title,
                                message = reminder.message,
                                type = reminder.referenceType,
                                triggerTimestamp = reminder.triggerTimestamp
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
