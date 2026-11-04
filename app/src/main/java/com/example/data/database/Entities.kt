package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val category: String = "WORK",
    val dueDate: String, // YYYY-MM-DD
    val dueTime: String? = null, // HH:mm
    val priority: String = "MEDIUM",
    val status: String = "PENDING",
    val reminderMinutesBefore: Int? = 30,
    val recurrence: String = "NONE",
    val tags: String = "",
    val checklistJson: String = "",
    val isStarred: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val category: String = "PERSONAL",
    val date: String, // YYYY-MM-DD
    val startTime: String, // HH:mm
    val endTime: String? = null, // HH:mm
    val location: String = "",
    val reminderMinutesBefore: Int? = 15,
    val recurrence: String = "NONE",
    val colorHex: String = "#8B5CF6",
    val participants: String = "",
    val isWork: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "meetings")
data class MeetingEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val company: String = "",
    val project: String = "",
    val client: String = "",
    val agenda: String = "",
    val notes: String = "",
    val date: String, // YYYY-MM-DD
    val startTime: String, // HH:mm
    val endTime: String? = null,
    val location: String = "Office / Online",
    val participants: String = "",
    val outcomes: String = "",
    val followUpTasks: String = "",
    val reminderMinutesBefore: Int? = 10,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val currency: String = "₹",
    val category: String = "OTHER",
    val dueDate: String, // YYYY-MM-DD
    val reminderDaysBefore: Int = 1,
    val recipient: String = "",
    val paymentMethod: String = "UPI / Card",
    val notes: String = "",
    val recurrence: String = "MONTHLY",
    val status: String = "UPCOMING",
    val paidDate: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val destination: String,
    val startDate: String, // YYYY-MM-DD
    val endDate: String, // YYYY-MM-DD
    val travelers: String = "",
    val budget: Double = 0.0,
    val currency: String = "₹",
    val status: String = "PLANNING",
    val notes: String = "",
    val transportMode: String = "Flight / Train",
    val hotelDetails: String = "",
    val packingChecklist: String = "", // JSON list of items
    val placesToVisit: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "trip_itineraries")
data class TripItineraryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val tripId: String,
    val dayNumber: Int,
    val date: String,
    val title: String,
    val time: String? = null,
    val location: String = "",
    val notes: String = "",
    val isCompleted: Boolean = false
)

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val relationship: String = "",
    val category: String = "FAMILY",
    val phone: String = "",
    val email: String = "",
    val birthday: String? = null, // MM-DD or YYYY-MM-DD
    val anniversary: String? = null,
    val notes: String = "",
    val reminderToCallDays: Int? = null,
    val lastContactedDate: String? = null,
    val giftIdeas: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val category: String = "Personal",
    val isPinned: Boolean = false,
    val tags: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val category: String = "Personal",
    val type: String = "DAILY",
    val targetDaysOrCount: Int = 30,
    val currentProgress: Int = 0,
    val streakCount: Int = 0,
    val dueDate: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val referenceId: String = "",
    val referenceType: String = "TASK", // TASK, EVENT, PAYMENT, MEETING
    val title: String,
    val message: String = "",
    val triggerTimestamp: Long,
    val isTriggered: Boolean = false,
    val recurrence: String = "NONE",
    val channelId: String = "lifesync_reminders"
)

@Entity(tableName = "notification_history")
data class NotificationHistoryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val type: String = "REMINDER",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "current_user",
    val name: String = "Dhiraj",
    val greeting: String = "Let's organize your day.",
    val profileImageUri: String? = null,
    val themeMode: String = "SYSTEM", // DARK, LIGHT, SYSTEM
    val notificationsEnabled: Boolean = true,
    val defaultReminderMinutes: Int = 15,
    val firstDayOfWeek: Int = 1, // 1 for Sunday, 2 for Monday
    val backupReminderEnabled: Boolean = true,
    val lastBackupTimestamp: Long = 0L
)
