package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC, dueTime ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE dueDate = :date ORDER BY dueTime ASC")
    fun getTasksByDate(date: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status = 'PENDING' OR status = 'IN_PROGRESS' ORDER BY dueDate ASC")
    fun getPendingTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: String): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: String)

    @Query("DELETE FROM tasks")
    suspend fun clearAll()
}

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY date ASC, startTime ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE date = :date ORDER BY startTime ASC")
    fun getEventsByDate(date: String): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC, startTime ASC")
    fun getEventsInRange(startDate: String, endDate: String): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: String): EventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteEventById(id: String)

    @Query("DELETE FROM events")
    suspend fun clearAll()
}

@Dao
interface MeetingDao {
    @Query("SELECT * FROM meetings ORDER BY date ASC, startTime ASC")
    fun getAllMeetings(): Flow<List<MeetingEntity>>

    @Query("SELECT * FROM meetings WHERE date = :date ORDER BY startTime ASC")
    fun getMeetingsByDate(date: String): Flow<List<MeetingEntity>>

    @Query("SELECT * FROM meetings WHERE id = :id")
    suspend fun getMeetingById(id: String): MeetingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeeting(meeting: MeetingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeetings(meetings: List<MeetingEntity>)

    @Update
    suspend fun updateMeeting(meeting: MeetingEntity)

    @Delete
    suspend fun deleteMeeting(meeting: MeetingEntity)

    @Query("DELETE FROM meetings WHERE id = :id")
    suspend fun deleteMeetingById(id: String)

    @Query("DELETE FROM meetings")
    suspend fun clearAll()
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY dueDate ASC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE status != 'PAID' ORDER BY dueDate ASC")
    fun getPendingPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE id = :id")
    suspend fun getPaymentById(id: String): PaymentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<PaymentEntity>)

    @Update
    suspend fun updatePayment(payment: PaymentEntity)

    @Delete
    suspend fun deletePayment(payment: PaymentEntity)

    @Query("DELETE FROM payments WHERE id = :id")
    suspend fun deletePaymentById(id: String)

    @Query("DELETE FROM payments")
    suspend fun clearAll()
}

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY startDate ASC")
    fun getAllTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :id")
    suspend fun getTripById(id: String): TripEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrips(trips: List<TripEntity>)

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    @Query("DELETE FROM trips WHERE id = :id")
    suspend fun deleteTripById(id: String)

    @Query("DELETE FROM trips")
    suspend fun clearAll()
}

@Dao
interface TripItineraryDao {
    @Query("SELECT * FROM trip_itineraries WHERE tripId = :tripId ORDER BY dayNumber ASC, time ASC")
    fun getItinerariesForTrip(tripId: String): Flow<List<TripItineraryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItinerary(itinerary: TripItineraryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItineraries(itineraries: List<TripItineraryEntity>)

    @Update
    suspend fun updateItinerary(itinerary: TripItineraryEntity)

    @Delete
    suspend fun deleteItinerary(itinerary: TripItineraryEntity)

    @Query("DELETE FROM trip_itineraries WHERE tripId = :tripId")
    suspend fun deleteByTripId(tripId: String)

    @Query("DELETE FROM trip_itineraries")
    suspend fun clearAll()
}

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContacts(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: String): ContactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<ContactEntity>)

    @Update
    suspend fun updateContact(contact: ContactEntity)

    @Delete
    suspend fun deleteContact(contact: ContactEntity)

    @Query("DELETE FROM contacts WHERE id = :id")
    suspend fun deleteContactById(id: String)

    @Query("DELETE FROM contacts")
    suspend fun clearAll()
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: String): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: String)

    @Query("DELETE FROM notes")
    suspend fun clearAll()
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE id = :id")
    suspend fun getGoalById(id: String): GoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<GoalEntity>)

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteGoalById(id: String)

    @Query("DELETE FROM goals")
    suspend fun clearAll()
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE isTriggered = 0 ORDER BY triggerTimestamp ASC")
    fun getActiveReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE isTriggered = 0 AND triggerTimestamp <= :currentTimestamp")
    suspend fun getDueReminders(currentTimestamp: Long): List<ReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<ReminderEntity>)

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE referenceId = :referenceId")
    suspend fun deleteByReferenceId(referenceId: String)

    @Query("DELETE FROM reminders")
    suspend fun clearAll()
}

@Dao
interface NotificationHistoryDao {
    @Query("SELECT * FROM notification_history ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationHistoryEntity)

    @Query("UPDATE notification_history SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notification_history WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM notification_history")
    suspend fun clearAll()
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 'current_user'")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 'current_user'")
    suspend fun getUserProfileSync(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)
}
