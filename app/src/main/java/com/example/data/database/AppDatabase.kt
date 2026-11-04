package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TaskEntity::class,
        EventEntity::class,
        MeetingEntity::class,
        PaymentEntity::class,
        TripEntity::class,
        TripItineraryEntity::class,
        ContactEntity::class,
        NoteEntity::class,
        GoalEntity::class,
        ReminderEntity::class,
        NotificationHistoryEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun eventDao(): EventDao
    abstract fun meetingDao(): MeetingDao
    abstract fun paymentDao(): PaymentDao
    abstract fun tripDao(): TripDao
    abstract fun tripItineraryDao(): TripItineraryDao
    abstract fun contactDao(): ContactDao
    abstract fun noteDao(): NoteDao
    abstract fun goalDao(): GoalDao
    abstract fun reminderDao(): ReminderDao
    abstract fun notificationHistoryDao(): NotificationHistoryDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lifesync_local.db"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
