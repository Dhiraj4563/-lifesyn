package com.example.data.backup

import android.content.Context
import com.example.data.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupStats(
    val exportDate: String,
    val totalTasks: Int,
    val totalEvents: Int,
    val totalMeetings: Int,
    val totalPayments: Int,
    val totalTrips: Int,
    val totalContacts: Int,
    val totalNotes: Int,
    val totalGoals: Int,
    val fileSizeKb: Long
)

class BackupManager(private val db: AppDatabase, private val context: Context) {

    suspend fun createBackupJson(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("app", "LifeSync")
        root.put("version", 1)
        root.put("timestamp", System.currentTimeMillis())
        root.put("exportDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))

        // Tasks
        val tasks = db.taskDao().getAllTasks().first()
        val tasksArray = JSONArray()
        for (t in tasks) {
            val obj = JSONObject()
            obj.put("id", t.id)
            obj.put("title", t.title)
            obj.put("description", t.description)
            obj.put("category", t.category)
            obj.put("dueDate", t.dueDate)
            obj.put("dueTime", t.dueTime ?: "")
            obj.put("priority", t.priority)
            obj.put("status", t.status)
            obj.put("isStarred", t.isStarred)
            obj.put("createdAt", t.createdAt)
            tasksArray.put(obj)
        }
        root.put("tasks", tasksArray)

        // Events
        val events = db.eventDao().getAllEvents().first()
        val eventsArray = JSONArray()
        for (e in events) {
            val obj = JSONObject()
            obj.put("id", e.id)
            obj.put("title", e.title)
            obj.put("description", e.description)
            obj.put("category", e.category)
            obj.put("date", e.date)
            obj.put("startTime", e.startTime)
            obj.put("endTime", e.endTime ?: "")
            obj.put("location", e.location)
            obj.put("colorHex", e.colorHex)
            obj.put("isWork", e.isWork)
            eventsArray.put(obj)
        }
        root.put("events", eventsArray)

        // Meetings
        val meetings = db.meetingDao().getAllMeetings().first()
        val meetingsArray = JSONArray()
        for (m in meetings) {
            val obj = JSONObject()
            obj.put("id", m.id)
            obj.put("title", m.title)
            obj.put("company", m.company)
            obj.put("project", m.project)
            obj.put("client", m.client)
            obj.put("agenda", m.agenda)
            obj.put("notes", m.notes)
            obj.put("date", m.date)
            obj.put("startTime", m.startTime)
            obj.put("location", m.location)
            meetingsArray.put(obj)
        }
        root.put("meetings", meetingsArray)

        // Payments
        val payments = db.paymentDao().getAllPayments().first()
        val paymentsArray = JSONArray()
        for (p in payments) {
            val obj = JSONObject()
            obj.put("id", p.id)
            obj.put("title", p.title)
            obj.put("amount", p.amount)
            obj.put("currency", p.currency)
            obj.put("category", p.category)
            obj.put("dueDate", p.dueDate)
            obj.put("status", p.status)
            obj.put("paidDate", p.paidDate ?: "")
            paymentsArray.put(obj)
        }
        root.put("payments", paymentsArray)

        // Trips
        val trips = db.tripDao().getAllTrips().first()
        val tripsArray = JSONArray()
        for (tr in trips) {
            val obj = JSONObject()
            obj.put("id", tr.id)
            obj.put("name", tr.name)
            obj.put("destination", tr.destination)
            obj.put("startDate", tr.startDate)
            obj.put("endDate", tr.endDate)
            obj.put("status", tr.status)
            obj.put("budget", tr.budget)
            obj.put("transportMode", tr.transportMode)
            obj.put("hotelDetails", tr.hotelDetails)
            tripsArray.put(obj)
        }
        root.put("trips", tripsArray)

        // Contacts
        val contacts = db.contactDao().getAllContacts().first()
        val contactsArray = JSONArray()
        for (c in contacts) {
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("name", c.name)
            obj.put("relationship", c.relationship)
            obj.put("category", c.category)
            obj.put("phone", c.phone)
            obj.put("birthday", c.birthday ?: "")
            obj.put("anniversary", c.anniversary ?: "")
            contactsArray.put(obj)
        }
        root.put("contacts", contactsArray)

        // Notes
        val notes = db.noteDao().getAllNotes().first()
        val notesArray = JSONArray()
        for (n in notes) {
            val obj = JSONObject()
            obj.put("id", n.id)
            obj.put("title", n.title)
            obj.put("content", n.content)
            obj.put("category", n.category)
            obj.put("isPinned", n.isPinned)
            notesArray.put(obj)
        }
        root.put("notes", notesArray)

        // Goals
        val goals = db.goalDao().getAllGoals().first()
        val goalsArray = JSONArray()
        for (g in goals) {
            val obj = JSONObject()
            obj.put("id", g.id)
            obj.put("title", g.title)
            obj.put("description", g.description)
            obj.put("type", g.type)
            obj.put("targetDaysOrCount", g.targetDaysOrCount)
            obj.put("currentProgress", g.currentProgress)
            obj.put("streakCount", g.streakCount)
            obj.put("isCompleted", g.isCompleted)
            goalsArray.put(obj)
        }
        root.put("goals", goalsArray)

        root.toString(2)
    }

    suspend fun saveBackupToLocalFile(): File = withContext(Dispatchers.IO) {
        val json = createBackupJson()
        val backupDir = File(context.filesDir, "backups").apply { if (!exists()) mkdirs() }
        val fileName = "LifeSync_Backup_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.json"
        val file = File(backupDir, fileName)
        file.writeText(json)
        file
    }

    suspend fun inspectBackup(jsonString: String): BackupStats? = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            if (!root.has("app") || root.getString("app") != "LifeSync") return@withContext null

            val date = root.optString("exportDate", "Unknown Date")
            val tasksCount = root.optJSONArray("tasks")?.length() ?: 0
            val eventsCount = root.optJSONArray("events")?.length() ?: 0
            val meetingsCount = root.optJSONArray("meetings")?.length() ?: 0
            val paymentsCount = root.optJSONArray("payments")?.length() ?: 0
            val tripsCount = root.optJSONArray("trips")?.length() ?: 0
            val contactsCount = root.optJSONArray("contacts")?.length() ?: 0
            val notesCount = root.optJSONArray("notes")?.length() ?: 0
            val goalsCount = root.optJSONArray("goals")?.length() ?: 0

            BackupStats(
                exportDate = date,
                totalTasks = tasksCount,
                totalEvents = eventsCount,
                totalMeetings = meetingsCount,
                totalPayments = paymentsCount,
                totalTrips = tripsCount,
                totalContacts = contactsCount,
                totalNotes = notesCount,
                totalGoals = goalsCount,
                fileSizeKb = (jsonString.toByteArray().size / 1024L).coerceAtLeast(1)
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun restoreFromJson(jsonString: String, replaceAll: Boolean): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            if (!root.has("app") || root.getString("app") != "LifeSync") return@withContext false

            if (replaceAll) {
                db.taskDao().clearAll()
                db.eventDao().clearAll()
                db.meetingDao().clearAll()
                db.paymentDao().clearAll()
                db.tripDao().clearAll()
                db.contactDao().clearAll()
                db.noteDao().clearAll()
                db.goalDao().clearAll()
            }

            // Tasks
            val tasksArr = root.optJSONArray("tasks")
            if (tasksArr != null) {
                val list = mutableListOf<TaskEntity>()
                for (i in 0 until tasksArr.length()) {
                    val o = tasksArr.getJSONObject(i)
                    list.add(
                        TaskEntity(
                            id = o.optString("id"),
                            title = o.getString("title"),
                            description = o.optString("description", ""),
                            category = o.optString("category", "WORK"),
                            dueDate = o.optString("dueDate", ""),
                            dueTime = o.optString("dueTime").takeIf { it.isNotEmpty() },
                            priority = o.optString("priority", "MEDIUM"),
                            status = o.optString("status", "PENDING"),
                            isStarred = o.optBoolean("isStarred", false)
                        )
                    )
                }
                db.taskDao().insertTasks(list)
            }

            // Events
            val eventsArr = root.optJSONArray("events")
            if (eventsArr != null) {
                val list = mutableListOf<EventEntity>()
                for (i in 0 until eventsArr.length()) {
                    val o = eventsArr.getJSONObject(i)
                    list.add(
                        EventEntity(
                            id = o.optString("id"),
                            title = o.getString("title"),
                            description = o.optString("description", ""),
                            category = o.optString("category", "PERSONAL"),
                            date = o.optString("date", ""),
                            startTime = o.optString("startTime", "09:00"),
                            endTime = o.optString("endTime").takeIf { it.isNotEmpty() },
                            location = o.optString("location", ""),
                            colorHex = o.optString("colorHex", "#8B5CF6"),
                            isWork = o.optBoolean("isWork", false)
                        )
                    )
                }
                db.eventDao().insertEvents(list)
            }

            // Meetings
            val meetingsArr = root.optJSONArray("meetings")
            if (meetingsArr != null) {
                val list = mutableListOf<MeetingEntity>()
                for (i in 0 until meetingsArr.length()) {
                    val o = meetingsArr.getJSONObject(i)
                    list.add(
                        MeetingEntity(
                            id = o.optString("id"),
                            title = o.getString("title"),
                            company = o.optString("company", ""),
                            project = o.optString("project", ""),
                            client = o.optString("client", ""),
                            agenda = o.optString("agenda", ""),
                            notes = o.optString("notes", ""),
                            date = o.optString("date", ""),
                            startTime = o.optString("startTime", "10:00"),
                            location = o.optString("location", "")
                        )
                    )
                }
                db.meetingDao().insertMeetings(list)
            }

            // Payments
            val paymentsArr = root.optJSONArray("payments")
            if (paymentsArr != null) {
                val list = mutableListOf<PaymentEntity>()
                for (i in 0 until paymentsArr.length()) {
                    val o = paymentsArr.getJSONObject(i)
                    list.add(
                        PaymentEntity(
                            id = o.optString("id"),
                            title = o.getString("title"),
                            amount = o.optDouble("amount", 0.0),
                            currency = o.optString("currency", "₹"),
                            category = o.optString("category", "OTHER"),
                            dueDate = o.optString("dueDate", ""),
                            status = o.optString("status", "UPCOMING"),
                            paidDate = o.optString("paidDate").takeIf { it.isNotEmpty() }
                        )
                    )
                }
                db.paymentDao().insertPayments(list)
            }

            // Trips
            val tripsArr = root.optJSONArray("trips")
            if (tripsArr != null) {
                val list = mutableListOf<TripEntity>()
                for (i in 0 until tripsArr.length()) {
                    val o = tripsArr.getJSONObject(i)
                    list.add(
                        TripEntity(
                            id = o.optString("id"),
                            name = o.getString("name"),
                            destination = o.optString("destination", ""),
                            startDate = o.optString("startDate", ""),
                            endDate = o.optString("endDate", ""),
                            status = o.optString("status", "PLANNING"),
                            budget = o.optDouble("budget", 0.0),
                            transportMode = o.optString("transportMode", ""),
                            hotelDetails = o.optString("hotelDetails", "")
                        )
                    )
                }
                db.tripDao().insertTrips(list)
            }

            // Contacts
            val contactsArr = root.optJSONArray("contacts")
            if (contactsArr != null) {
                val list = mutableListOf<ContactEntity>()
                for (i in 0 until contactsArr.length()) {
                    val o = contactsArr.getJSONObject(i)
                    list.add(
                        ContactEntity(
                            id = o.optString("id"),
                            name = o.getString("name"),
                            relationship = o.optString("relationship", ""),
                            category = o.optString("category", "FAMILY"),
                            phone = o.optString("phone", ""),
                            birthday = o.optString("birthday").takeIf { it.isNotEmpty() },
                            anniversary = o.optString("anniversary").takeIf { it.isNotEmpty() }
                        )
                    )
                }
                db.contactDao().insertContacts(list)
            }

            // Notes
            val notesArr = root.optJSONArray("notes")
            if (notesArr != null) {
                val list = mutableListOf<NoteEntity>()
                for (i in 0 until notesArr.length()) {
                    val o = notesArr.getJSONObject(i)
                    list.add(
                        NoteEntity(
                            id = o.optString("id"),
                            title = o.getString("title"),
                            content = o.optString("content", ""),
                            category = o.optString("category", "Personal"),
                            isPinned = o.optBoolean("isPinned", false)
                        )
                    )
                }
                db.noteDao().insertNotes(list)
            }

            // Goals
            val goalsArr = root.optJSONArray("goals")
            if (goalsArr != null) {
                val list = mutableListOf<GoalEntity>()
                for (i in 0 until goalsArr.length()) {
                    val o = goalsArr.getJSONObject(i)
                    list.add(
                        GoalEntity(
                            id = o.optString("id"),
                            title = o.getString("title"),
                            description = o.optString("description", ""),
                            type = o.optString("type", "DAILY"),
                            targetDaysOrCount = o.optInt("targetDaysOrCount", 30),
                            currentProgress = o.optInt("currentProgress", 0),
                            streakCount = o.optInt("streakCount", 0),
                            isCompleted = o.optBoolean("isCompleted", false)
                        )
                    )
                }
                db.goalDao().insertGoals(list)
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
