package com.example.data.repository

import android.content.Context
import com.example.data.backup.BackupManager
import com.example.data.database.*
import com.example.notification.ReminderScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class LifeSyncRepository(
    private val db: AppDatabase,
    private val context: Context
) {
    val backupManager = BackupManager(db, context)

    // Tasks
    val allTasks: Flow<List<TaskEntity>> = db.taskDao().getAllTasks()
    val pendingTasks: Flow<List<TaskEntity>> = db.taskDao().getPendingTasks()

    suspend fun insertTask(task: TaskEntity) {
        db.taskDao().insertTask(task)
        // If has due time and reminder, schedule alert
        task.dueTime?.let { time ->
            val timestamp = computeTimestamp(task.dueDate, time, task.reminderMinutesBefore ?: 15)
            if (timestamp > System.currentTimeMillis()) {
                ReminderScheduler.scheduleReminder(
                    context,
                    task.id,
                    "Task Due: ${task.title}",
                    "Category: ${task.category} • Priority: ${task.priority}",
                    "TASK",
                    timestamp
                )
            }
        }
    }

    suspend fun updateTask(task: TaskEntity) {
        db.taskDao().updateTask(task)
        if (task.status == "COMPLETED") {
            ReminderScheduler.cancelReminder(context, task.id)
        }
    }

    suspend fun deleteTask(task: TaskEntity) {
        db.taskDao().deleteTask(task)
        ReminderScheduler.cancelReminder(context, task.id)
    }

    suspend fun toggleTaskComplete(task: TaskEntity) {
        val newStatus = if (task.status == "COMPLETED") "PENDING" else "COMPLETED"
        updateTask(task.copy(status = newStatus, updatedAt = System.currentTimeMillis()))
    }

    suspend fun toggleTaskStarred(task: TaskEntity) {
        updateTask(task.copy(isStarred = !task.isStarred, updatedAt = System.currentTimeMillis()))
    }

    // Events
    val allEvents: Flow<List<EventEntity>> = db.eventDao().getAllEvents()
    fun getEventsByDate(date: String): Flow<List<EventEntity>> = db.eventDao().getEventsByDate(date)

    suspend fun insertEvent(event: EventEntity) {
        db.eventDao().insertEvent(event)
        val timestamp = computeTimestamp(event.date, event.startTime, event.reminderMinutesBefore ?: 15)
        if (timestamp > System.currentTimeMillis()) {
            ReminderScheduler.scheduleReminder(
                context,
                event.id,
                "Upcoming Event: ${event.title}",
                "${event.startTime} • ${event.location.ifEmpty { event.category }}",
                "EVENT",
                timestamp
            )
        }
    }

    suspend fun updateEvent(event: EventEntity) = db.eventDao().updateEvent(event)
    suspend fun deleteEvent(event: EventEntity) {
        db.eventDao().deleteEvent(event)
        ReminderScheduler.cancelReminder(context, event.id)
    }

    // Meetings
    val allMeetings: Flow<List<MeetingEntity>> = db.meetingDao().getAllMeetings()

    suspend fun insertMeeting(meeting: MeetingEntity) {
        db.meetingDao().insertMeeting(meeting)
        val timestamp = computeTimestamp(meeting.date, meeting.startTime, meeting.reminderMinutesBefore ?: 10)
        if (timestamp > System.currentTimeMillis()) {
            ReminderScheduler.scheduleReminder(
                context,
                meeting.id,
                "Meeting Alert: ${meeting.title}",
                "Time: ${meeting.startTime} • ${meeting.location}",
                "MEETING",
                timestamp
            )
        }
    }

    suspend fun updateMeeting(meeting: MeetingEntity) = db.meetingDao().updateMeeting(meeting)
    suspend fun deleteMeeting(meeting: MeetingEntity) {
        db.meetingDao().deleteMeeting(meeting)
        ReminderScheduler.cancelReminder(context, meeting.id)
    }

    // Payments
    val allPayments: Flow<List<PaymentEntity>> = db.paymentDao().getAllPayments()
    val pendingPayments: Flow<List<PaymentEntity>> = db.paymentDao().getPendingPayments()

    suspend fun insertPayment(payment: PaymentEntity) {
        db.paymentDao().insertPayment(payment)
        val timestamp = computeTimestamp(payment.dueDate, "09:00", payment.reminderDaysBefore * 24 * 60)
        if (timestamp > System.currentTimeMillis()) {
            ReminderScheduler.scheduleReminder(
                context,
                payment.id,
                "Payment Due: ${payment.title}",
                "Amount: ${payment.currency}${String.format(Locale.getDefault(), "%,.0f", payment.amount)} due on ${payment.dueDate}",
                "PAYMENT",
                timestamp
            )
        }
    }

    suspend fun updatePayment(payment: PaymentEntity) = db.paymentDao().updatePayment(payment)
    suspend fun deletePayment(payment: PaymentEntity) {
        db.paymentDao().deletePayment(payment)
        ReminderScheduler.cancelReminder(context, payment.id)
    }

    suspend fun togglePaymentPaid(payment: PaymentEntity) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val newStatus = if (payment.status == "PAID") "UPCOMING" else "PAID"
        val paidDate = if (newStatus == "PAID") sdf.format(Date()) else null
        updatePayment(payment.copy(status = newStatus, paidDate = paidDate))
    }

    // Trips
    val allTrips: Flow<List<TripEntity>> = db.tripDao().getAllTrips()
    suspend fun insertTrip(trip: TripEntity) = db.tripDao().insertTrip(trip)
    suspend fun updateTrip(trip: TripEntity) = db.tripDao().updateTrip(trip)
    suspend fun deleteTrip(trip: TripEntity) = db.tripDao().deleteTrip(trip)

    fun getTripItineraries(tripId: String): Flow<List<TripItineraryEntity>> =
        db.tripItineraryDao().getItinerariesForTrip(tripId)
    suspend fun insertItinerary(itinerary: TripItineraryEntity) = db.tripItineraryDao().insertItinerary(itinerary)
    suspend fun updateItinerary(itinerary: TripItineraryEntity) = db.tripItineraryDao().updateItinerary(itinerary)
    suspend fun deleteItinerary(itinerary: TripItineraryEntity) = db.tripItineraryDao().deleteItinerary(itinerary)

    // Contacts
    val allContacts: Flow<List<ContactEntity>> = db.contactDao().getAllContacts()
    suspend fun insertContact(contact: ContactEntity) = db.contactDao().insertContact(contact)
    suspend fun updateContact(contact: ContactEntity) = db.contactDao().updateContact(contact)
    suspend fun deleteContact(contact: ContactEntity) = db.contactDao().deleteContact(contact)

    // Notes
    val allNotes: Flow<List<NoteEntity>> = db.noteDao().getAllNotes()
    suspend fun insertNote(note: NoteEntity) = db.noteDao().insertNote(note)
    suspend fun updateNote(note: NoteEntity) = db.noteDao().updateNote(note)
    suspend fun deleteNote(note: NoteEntity) = db.noteDao().deleteNote(note)

    // Goals
    val allGoals: Flow<List<GoalEntity>> = db.goalDao().getAllGoals()
    suspend fun insertGoal(goal: GoalEntity) = db.goalDao().insertGoal(goal)
    suspend fun updateGoal(goal: GoalEntity) = db.goalDao().updateGoal(goal)
    suspend fun deleteGoal(goal: GoalEntity) = db.goalDao().deleteGoal(goal)
    suspend fun incrementGoal(goal: GoalEntity) {
        val next = (goal.currentProgress + 1).coerceAtMost(goal.targetDaysOrCount)
        val isCompleted = next >= goal.targetDaysOrCount
        val streak = if (next > goal.currentProgress) goal.streakCount + 1 else goal.streakCount
        updateGoal(goal.copy(currentProgress = next, streakCount = streak, isCompleted = isCompleted))
    }

    // Notification History
    val allNotifications: Flow<List<NotificationHistoryEntity>> = db.notificationHistoryDao().getAllNotifications()
    suspend fun markAllNotificationsAsRead() = db.notificationHistoryDao().markAllAsRead()
    suspend fun clearNotifications() = db.notificationHistoryDao().clearAll()

    // User Profile
    val userProfile: Flow<UserProfileEntity?> = db.userProfileDao().getUserProfile()
    suspend fun updateUserProfile(profile: UserProfileEntity) = db.userProfileDao().insertUserProfile(profile)

    // Reset Database
    suspend fun clearAllData() {
        db.taskDao().clearAll()
        db.eventDao().clearAll()
        db.meetingDao().clearAll()
        db.paymentDao().clearAll()
        db.tripDao().clearAll()
        db.tripItineraryDao().clearAll()
        db.contactDao().clearAll()
        db.noteDao().clearAll()
        db.goalDao().clearAll()
        db.reminderDao().clearAll()
        db.notificationHistoryDao().clearAll()
    }

    // Seeding Demo Data
    suspend fun seedDemoDataIfEmpty() {
        val existingTasks = db.taskDao().getAllTasks().first()
        if (existingTasks.isEmpty()) {
            loadDemoData()
        }
    }

    suspend fun loadDemoData() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        val today = sdf.format(cal.time)

        cal.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = sdf.format(cal.time)

        cal.add(Calendar.DAY_OF_YEAR, 3)
        val nextWeek = sdf.format(cal.time)

        // Seed Profile
        db.userProfileDao().insertUserProfile(
            UserProfileEntity(
                name = "Dhiraj",
                greeting = "Let's organize your day."
            )
        )

        // Seed Tasks
        val tasks = listOf(
            TaskEntity(
                title = "Prepare sales report",
                description = "Compile regional sales data and presentation slides for executive review",
                category = "Work",
                dueDate = today,
                dueTime = "16:00",
                priority = "Urgent",
                status = "PENDING",
                isStarred = true
            ),
            TaskEntity(
                title = "Follow up with customer",
                description = "Call Apex Corp regarding contract renewal terms",
                category = "Work",
                dueDate = today,
                dueTime = "14:30",
                priority = "High",
                status = "PENDING",
                isStarred = false
            ),
            TaskEntity(
                title = "Book train tickets",
                description = "Confirm seat reservation for Bangalore return",
                category = "Travel",
                dueDate = today,
                dueTime = "11:00",
                priority = "Medium",
                status = "COMPLETED",
                isStarred = false
            ),
            TaskEntity(
                title = "Buy groceries",
                description = "Vegetables, olive oil, oat milk, whole wheat flour",
                category = "Personal",
                dueDate = tomorrow,
                dueTime = "18:00",
                priority = "Medium",
                status = "PENDING",
                isStarred = false
            ),
            TaskEntity(
                title = "Call home",
                description = "Weekly catch-up with parents",
                category = "Family",
                dueDate = tomorrow,
                dueTime = "20:00",
                priority = "High",
                status = "PENDING",
                isStarred = true
            ),
            TaskEntity(
                title = "Plan weekend trip",
                description = "Review resort bookings and packing checklist",
                category = "Travel",
                dueDate = nextWeek,
                dueTime = "19:00",
                priority = "Medium",
                status = "PENDING",
                isStarred = false
            )
        )
        db.taskDao().insertTasks(tasks)

        // Seed Events
        val events = listOf(
            EventEntity(
                title = "Office Team Meeting",
                description = "Sprint planning and milestone check-in",
                category = "Work",
                date = today,
                startTime = "10:00",
                endTime = "10:30",
                location = "Conference Room B",
                colorHex = "#3B82F6",
                isWork = true
            ),
            EventEntity(
                title = "Lunch with Friends",
                description = "Catch-up at Downtown Grill",
                category = "Personal",
                date = today,
                startTime = "13:30",
                endTime = "14:30",
                location = "Downtown Grill",
                colorHex = "#EC4899",
                isWork = false
            ),
            EventEntity(
                title = "Personal Appointment",
                description = "Routine dental checkup",
                category = "Personal",
                date = today,
                startTime = "15:00",
                endTime = "15:45",
                location = "HealthCare Center",
                colorHex = "#10B981",
                isWork = false
            ),
            EventEntity(
                title = "Plan Nepal Trip",
                description = "Discuss trekking routes with travel group",
                category = "Travel",
                date = today,
                startTime = "19:00",
                endTime = "19:30",
                location = "Home",
                colorHex = "#8B5CF6",
                isWork = false
            )
        )
        db.eventDao().insertEvents(events)

        // Seed Meetings
        val meetings = listOf(
            MeetingEntity(
                title = "Office Team Meeting",
                company = "LifeSync Technologies",
                project = "Mobile 2.0",
                agenda = "Review weekly deliverables and unblock team members",
                date = today,
                startTime = "10:00",
                endTime = "10:30",
                location = "Room 302 / Google Meet",
                participants = "Design Team, Engineering Leads"
            ),
            MeetingEntity(
                title = "Customer Follow-up",
                company = "Apex Corp",
                client = "Rajesh Verma",
                agenda = "Finalize software license agreement",
                date = today,
                startTime = "11:30",
                endTime = "12:15",
                location = "Zoom",
                participants = "Dhiraj, Rajesh"
            )
        )
        db.meetingDao().insertMeetings(meetings)

        // Seed Payments
        val payments = listOf(
            PaymentEntity(
                title = "Electricity Bill",
                amount = 2500.0,
                currency = "₹",
                category = "Electricity",
                dueDate = today,
                status = "DUE_TODAY",
                recipient = "State Electricity Board",
                paymentMethod = "UPI"
            ),
            PaymentEntity(
                title = "Rent",
                amount = 15000.0,
                currency = "₹",
                category = "Rent",
                dueDate = tomorrow,
                status = "UPCOMING",
                recipient = "Landlord - Rajesh",
                paymentMethod = "Bank Transfer"
            ),
            PaymentEntity(
                title = "Credit Card",
                amount = 8000.0,
                currency = "₹",
                category = "Credit Card",
                dueDate = nextWeek,
                status = "UPCOMING",
                recipient = "HDFC Bank",
                paymentMethod = "Auto-debit"
            ),
            PaymentEntity(
                title = "Mobile Recharge",
                amount = 499.0,
                currency = "₹",
                category = "Mobile Recharge",
                dueDate = nextWeek,
                status = "UPCOMING",
                recipient = "Jio / Airtel",
                paymentMethod = "UPI"
            ),
            PaymentEntity(
                title = "EMI - Bike",
                amount = 4200.0,
                currency = "₹",
                category = "EMI",
                dueDate = nextWeek,
                status = "UPCOMING",
                recipient = "Bajaj Finance",
                paymentMethod = "Auto-debit"
            )
        )
        db.paymentDao().insertPayments(payments)

        // Seed Trips
        val trip1 = TripEntity(
            name = "Nepal Trip 🇳🇵",
            destination = "Kathmandu & Pokhara",
            startDate = "2026-12-20",
            endDate = "2026-12-25",
            travelers = "Dhiraj, Amit, Rahul",
            budget = 45000.0,
            status = "CONFIRMED",
            transportMode = "Flight / Train",
            hotelDetails = "Himalayan View Resort, Pokhara",
            packingChecklist = "Passports, Warm jacket, Trekking shoes, Power bank, First-aid kit"
        )
        val trip2 = TripEntity(
            name = "Udupi Weekend",
            destination = "Malpe Beach & St. Mary's Island",
            startDate = "2026-11-14",
            endDate = "2026-11-15",
            travelers = "Family (4 people)",
            budget = 12000.0,
            status = "PLANNING",
            transportMode = "Car Drive",
            hotelDetails = "Paradise Isle Beach Resort",
            packingChecklist = "Sunscreen, Beachwear, Sunglasses, Camera"
        )
        db.tripDao().insertTrips(listOf(trip1, trip2))

        // Seed Contacts
        val contacts = listOf(
            ContactEntity(
                name = "Mom",
                relationship = "Mother",
                category = "FAMILY",
                phone = "+91 98765 43210",
                birthday = "12-12",
                notes = "Favorite flowers: Jasmine"
            ),
            ContactEntity(
                name = "Priya Patel",
                relationship = "Best Friend",
                category = "FRIENDS",
                phone = "+91 98222 11000",
                anniversary = "11-20",
                giftIdeas = "Handmade artisan candle set"
            ),
            ContactEntity(
                name = "Rahul Sharma",
                relationship = "Engineering Partner",
                category = "COLLEAGUES",
                phone = "+91 98111 22334",
                notes = "Working on architecture migration"
            )
        )
        db.contactDao().insertContacts(contacts)

        // Seed Notes
        val notes = listOf(
            NoteEntity(
                title = "Nepal Trip Packing List",
                content = "1. Passports & travel permits\n2. Thermal wear & windproof jacket\n3. High-capacity power bank\n4. Trekking poles & sturdy boots\n5. Universal power adapters\n6. Electrolytes & emergency medicines",
                category = "Travel",
                isPinned = true
            ),
            NoteEntity(
                title = "Ideas for Q1 Project Roadmap",
                content = "- Local-first architecture design\n- Offline caching strategies\n- User feedback on dashboard widgets\n- Faster search indexing",
                category = "Work",
                isPinned = false
            ),
            NoteEntity(
                title = "Home Maintenance Contacts",
                content = "Electrician: Ramesh (+91 98450 12345)\nPlumber: Suresh (+91 98450 67890)\nAC Servicing: CoolAir Care",
                category = "Personal",
                isPinned = false
            )
        )
        db.noteDao().insertNotes(notes)

        // Seed Goals
        val goals = listOf(
            GoalEntity(
                title = "Drink 2.5L Water Daily",
                description = "Stay energized and hydrated throughout the day",
                category = "Health",
                type = "DAILY",
                targetDaysOrCount = 30,
                currentProgress = 24,
                streakCount = 7
            ),
            GoalEntity(
                title = "30 Min Morning Workout",
                description = "Mix of cardio, strength training, and yoga stretches",
                category = "Fitness",
                type = "HABIT",
                targetDaysOrCount = 20,
                currentProgress = 15,
                streakCount = 5
            ),
            GoalEntity(
                title = "Read 1 Non-Fiction Book",
                description = "Atomic Habits by James Clear",
                category = "Personal Growth",
                type = "MONTHLY",
                targetDaysOrCount = 10,
                currentProgress = 7,
                streakCount = 3
            )
        )
        db.goalDao().insertGoals(goals)

        // Seed In-app Notification
        db.notificationHistoryDao().insertNotification(
            NotificationHistoryEntity(
                title = "Welcome to LifeSync!",
                message = "Your personal life organizer is ready. 100% of your data stays private on this device.",
                type = "SYSTEM"
            )
        )
    }

    private fun computeTimestamp(dateStr: String, timeStr: String, minutesBefore: Int): Long {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = sdf.parse("$dateStr $timeStr")
            (date?.time ?: System.currentTimeMillis()) - (minutesBefore * 60 * 1000L)
        } catch (e: Exception) {
            System.currentTimeMillis() + 3600000L
        }
    }
}
