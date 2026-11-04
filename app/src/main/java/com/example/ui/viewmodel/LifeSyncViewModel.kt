package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.LifeSyncApplication
import com.example.data.backup.BackupStats
import com.example.data.database.*
import com.example.data.model.CalendarViewMode
import com.example.data.parser.ParsedQuickAddResult
import com.example.data.parser.QuickAddNlpParser
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class LifeSyncViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as LifeSyncApplication).repository

    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val events: StateFlow<List<EventEntity>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val meetings: StateFlow<List<MeetingEntity>> = repository.allMeetings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payments: StateFlow<List<PaymentEntity>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trips: StateFlow<List<TripEntity>> = repository.allTrips
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val contacts: StateFlow<List<ContactEntity>> = repository.allContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<NoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<GoalEntity>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationHistoryEntity>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val _selectedDate = MutableStateFlow(sdfDate.format(Date()))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _calendarMode = MutableStateFlow(CalendarViewMode.DAY)
    val calendarMode: StateFlow<CalendarViewMode> = _calendarMode.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchCategory = MutableStateFlow("ALL")
    val searchCategory: StateFlow<String> = _searchCategory.asStateFlow()

    private val _backupStatusMessage = MutableStateFlow<String?>(null)
    val backupStatusMessage: StateFlow<String?> = _backupStatusMessage.asStateFlow()

    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    fun setCalendarMode(mode: CalendarViewMode) {
        _calendarMode.value = mode
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSearchCategory(category: String) {
        _searchCategory.value = category
    }

    fun clearBackupStatus() {
        _backupStatusMessage.value = null
    }

    // Task actions
    fun addTask(task: TaskEntity) = viewModelScope.launch { repository.insertTask(task) }
    fun updateTask(task: TaskEntity) = viewModelScope.launch { repository.updateTask(task) }
    fun deleteTask(task: TaskEntity) = viewModelScope.launch { repository.deleteTask(task) }
    fun toggleTaskComplete(task: TaskEntity) = viewModelScope.launch { repository.toggleTaskComplete(task) }
    fun toggleTaskStarred(task: TaskEntity) = viewModelScope.launch { repository.toggleTaskStarred(task) }

    // Event actions
    fun addEvent(event: EventEntity) = viewModelScope.launch { repository.insertEvent(event) }
    fun updateEvent(event: EventEntity) = viewModelScope.launch { repository.updateEvent(event) }
    fun deleteEvent(event: EventEntity) = viewModelScope.launch { repository.deleteEvent(event) }

    // Meeting actions
    fun addMeeting(meeting: MeetingEntity) = viewModelScope.launch { repository.insertMeeting(meeting) }
    fun updateMeeting(meeting: MeetingEntity) = viewModelScope.launch { repository.updateMeeting(meeting) }
    fun deleteMeeting(meeting: MeetingEntity) = viewModelScope.launch { repository.deleteMeeting(meeting) }

    // Payment actions
    fun addPayment(payment: PaymentEntity) = viewModelScope.launch { repository.insertPayment(payment) }
    fun updatePayment(payment: PaymentEntity) = viewModelScope.launch { repository.updatePayment(payment) }
    fun deletePayment(payment: PaymentEntity) = viewModelScope.launch { repository.deletePayment(payment) }
    fun togglePaymentPaid(payment: PaymentEntity) = viewModelScope.launch { repository.togglePaymentPaid(payment) }

    // Trip actions
    fun addTrip(trip: TripEntity) = viewModelScope.launch { repository.insertTrip(trip) }
    fun updateTrip(trip: TripEntity) = viewModelScope.launch { repository.updateTrip(trip) }
    fun deleteTrip(trip: TripEntity) = viewModelScope.launch { repository.deleteTrip(trip) }

    // Contact actions
    fun addContact(contact: ContactEntity) = viewModelScope.launch { repository.insertContact(contact) }
    fun updateContact(contact: ContactEntity) = viewModelScope.launch { repository.updateContact(contact) }
    fun deleteContact(contact: ContactEntity) = viewModelScope.launch { repository.deleteContact(contact) }

    // Note actions
    fun addNote(note: NoteEntity) = viewModelScope.launch { repository.insertNote(note) }
    fun updateNote(note: NoteEntity) = viewModelScope.launch { repository.updateNote(note) }
    fun deleteNote(note: NoteEntity) = viewModelScope.launch { repository.deleteNote(note) }

    // Goal actions
    fun addGoal(goal: GoalEntity) = viewModelScope.launch { repository.insertGoal(goal) }
    fun updateGoal(goal: GoalEntity) = viewModelScope.launch { repository.updateGoal(goal) }
    fun deleteGoal(goal: GoalEntity) = viewModelScope.launch { repository.deleteGoal(goal) }
    fun incrementGoal(goal: GoalEntity) = viewModelScope.launch { repository.incrementGoal(goal) }

    // Notification actions
    fun markAllNotificationsRead() = viewModelScope.launch { repository.markAllNotificationsAsRead() }
    fun clearNotifications() = viewModelScope.launch { repository.clearNotifications() }

    // User profile actions
    fun updateUserProfile(profile: UserProfileEntity) = viewModelScope.launch { repository.updateUserProfile(profile) }

    // Quick Add NLP Parser
    fun parseQuickAddInput(input: String): ParsedQuickAddResult {
        return QuickAddNlpParser.parse(input)
    }

    fun saveQuickAddResult(result: ParsedQuickAddResult) = viewModelScope.launch {
        when (result.entityType) {
            "PAYMENT" -> {
                repository.insertPayment(
                    PaymentEntity(
                        title = result.title,
                        amount = result.amount ?: 0.0,
                        category = result.category,
                        dueDate = result.date,
                        status = "UPCOMING"
                    )
                )
            }
            "MEETING" -> {
                repository.insertMeeting(
                    MeetingEntity(
                        title = result.title,
                        date = result.date,
                        startTime = result.time ?: "10:00"
                    )
                )
            }
            "EVENT" -> {
                repository.insertEvent(
                    EventEntity(
                        title = result.title,
                        category = result.category,
                        date = result.date,
                        startTime = result.time ?: "12:00"
                    )
                )
            }
            "TRIP" -> {
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, 3)
                repository.insertTrip(
                    TripEntity(
                        name = result.title,
                        destination = result.title,
                        startDate = result.date,
                        endDate = sdfDate.format(cal.time),
                        status = "PLANNING"
                    )
                )
            }
            "NOTE" -> {
                repository.insertNote(
                    NoteEntity(
                        title = result.title,
                        content = result.rawText,
                        category = result.category
                    )
                )
            }
            else -> {
                repository.insertTask(
                    TaskEntity(
                        title = result.title,
                        category = result.category,
                        dueDate = result.date,
                        dueTime = result.time,
                        priority = "MEDIUM"
                    )
                )
            }
        }
    }

    // Backup & Restore
    fun exportBackup(onComplete: (String) -> Unit) = viewModelScope.launch {
        try {
            val file = repository.backupManager.saveBackupToLocalFile()
            _backupStatusMessage.value = "Backup saved to: ${file.name}"
            onComplete(file.absolutePath)
        } catch (e: Exception) {
            _backupStatusMessage.value = "Export failed: ${e.message}"
        }
    }

    fun inspectBackup(json: String, onResult: (BackupStats?) -> Unit) = viewModelScope.launch {
        val stats = repository.backupManager.inspectBackup(json)
        onResult(stats)
    }

    fun restoreBackup(json: String, replaceAll: Boolean, onDone: (Boolean) -> Unit) = viewModelScope.launch {
        val success = repository.backupManager.restoreFromJson(json, replaceAll)
        _backupStatusMessage.value = if (success) "Backup restored successfully!" else "Failed to restore backup"
        onDone(success)
    }

    fun resetDatabase() = viewModelScope.launch {
        repository.clearAllData()
        _backupStatusMessage.value = "All local data cleared"
    }

    fun loadDemoData() = viewModelScope.launch {
        repository.loadDemoData()
        _backupStatusMessage.value = "Demo data loaded successfully"
    }
}
