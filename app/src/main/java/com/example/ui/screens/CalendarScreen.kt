package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.EventEntity
import com.example.data.model.CalendarViewMode
import com.example.ui.components.PrimaryGradientButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.LifeSyncViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(
    viewModel: LifeSyncViewModel,
    modifier: Modifier = Modifier
) {
    val events by viewModel.events.collectAsStateWithLifecycle()
    val meetings by viewModel.meetings.collectAsStateWithLifecycle()
    val payments by viewModel.payments.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val calendarMode by viewModel.calendarMode.collectAsStateWithLifecycle()

    var showAddEventDialog by remember { mutableStateOf(false) }
    var eventToEdit by remember { mutableStateOf<EventEntity?>(null) }

    val calendar = remember { Calendar.getInstance() }
    var currentYearMonth by remember {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        mutableStateOf(sdf.format(calendar.time))
    }

    // Days strip calculation around the current month
    val daysInMonth = remember(currentYearMonth) {
        val list = mutableListOf<CalendarDay>()
        val cal = Calendar.getInstance()
        val sdfDayName = SimpleDateFormat("EEE", Locale.getDefault())
        val sdfFull = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Set to start of current month view
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 1..maxDays) {
            cal.set(Calendar.DAY_OF_MONTH, i)
            val fullDate = sdfFull.format(cal.time)
            val dayName = sdfDayName.format(cal.time).substring(0, 3)
            list.add(CalendarDay(dayNumber = i, dayOfWeek = dayName, fullDate = fullDate))
        }
        list
    }

    val sdfDisplayDate = remember { SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()) }
    val displaySelectedDate = remember(selectedDate) {
        try {
            val d = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate)
            if (d != null) sdfDisplayDate.format(d) else selectedDate
        } catch (e: Exception) {
            selectedDate
        }
    }

    // Day items
    val dayItems = remember(events, meetings, payments, selectedDate) {
        val list = mutableListOf<CalendarTimelineItem>()
        events.filter { it.date == selectedDate }.forEach {
            list.add(
                CalendarTimelineItem(
                    id = it.id,
                    time = it.startTime,
                    title = it.title,
                    subtitle = "${it.category} • ${it.location.ifEmpty { "Event" }}",
                    color = Color(android.graphics.Color.parseColor(it.colorHex.ifEmpty { "#8B5CF6" })),
                    icon = Icons.Default.Event,
                    rawEvent = it
                )
            )
        }
        meetings.filter { it.date == selectedDate }.forEach {
            list.add(
                CalendarTimelineItem(
                    id = it.id,
                    time = it.startTime,
                    title = it.title,
                    subtitle = "Work • ${it.location}",
                    color = BlueAccent,
                    icon = Icons.Default.Videocam,
                    rawEvent = null
                )
            )
        }
        payments.filter { it.dueDate == selectedDate }.forEach {
            list.add(
                CalendarTimelineItem(
                    id = it.id,
                    time = "17:30",
                    title = "Pay ${it.title}",
                    subtitle = "Payment • ${it.status.replace("_", " ")}",
                    color = OrangeAccent,
                    icon = Icons.Default.AccountBalanceWallet,
                    rawEvent = null
                )
            )
        }
        list.sortBy { it.time }
        list
    }

    // Check for conflicts
    val hasConflict = remember(dayItems) {
        val times = dayItems.map { it.time }
        times.size > times.distinct().size
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("calendar_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Month Header with Prev/Next
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            calendar.add(Calendar.MONTH, -1)
                            currentYearMonth = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)
                        },
                        modifier = Modifier.testTag("calendar_prev_month")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Month",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Text(
                        text = currentYearMonth,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )

                    IconButton(
                        onClick = {
                            calendar.add(Calendar.MONTH, 1)
                            currentYearMonth = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)
                        },
                        modifier = Modifier.testTag("calendar_next_month")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Month",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            // Days horizontal strip
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(daysInMonth) { day ->
                        val isSelected = day.fullDate == selectedDate
                        CalendarDayPill(
                            day = day,
                            isSelected = isSelected,
                            onClick = { viewModel.selectDate(day.fullDate) }
                        )
                    }
                }
            }

            // View Mode Selector (Day, Week, Month, Agenda)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CalendarViewMode.entries.forEach { mode ->
                        val isSelected = calendarMode == mode
                        Button(
                            onClick = { viewModel.setCalendarMode(mode) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) PurplePrimary else MaterialTheme.colorScheme.surface,
                                contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = mode.displayName,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }

            // Selected Date & Conflict Alert
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = displaySelectedDate,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )

                    if (hasConflict) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(OrangeAccent.copy(alpha = 0.15f))
                                .border(1.dp, OrangeAccent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Conflict warning",
                                tint = OrangeAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Notice: You have overlapping scheduled items at the same time.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = OrangeAccent,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }

            // Timeline Items
            if (dayItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EventNote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "No events scheduled for this day",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            } else {
                items(dayItems) { item ->
                    TimelineCard(
                        item = item,
                        onEdit = {
                            if (item.rawEvent != null) {
                                eventToEdit = item.rawEvent
                                showAddEventDialog = true
                            }
                        },
                        onDelete = {
                            if (item.rawEvent != null) {
                                viewModel.deleteEvent(item.rawEvent)
                            }
                        }
                    )
                }
            }
        }

        // Add Event Button (Fixed at Bottom)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            PrimaryGradientButton(
                text = "+ Add Event",
                onClick = {
                    eventToEdit = null
                    showAddEventDialog = true
                },
                icon = Icons.Default.Add,
                testTag = "add_event_button"
            )
        }
    }

    if (showAddEventDialog) {
        AddEditEventDialog(
            initialEvent = eventToEdit,
            initialDate = selectedDate,
            onDismiss = {
                showAddEventDialog = false
                eventToEdit = null
            },
            onSave = { event ->
                if (eventToEdit == null) {
                    viewModel.addEvent(event)
                } else {
                    viewModel.updateEvent(event)
                }
                showAddEventDialog = false
                eventToEdit = null
            }
        )
    }
}

data class CalendarDay(
    val dayNumber: Int,
    val dayOfWeek: String,
    val fullDate: String
)

data class CalendarTimelineItem(
    val id: String,
    val time: String,
    val title: String,
    val subtitle: String,
    val color: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val rawEvent: EventEntity?
)

@Composable
fun CalendarDayPill(
    day: CalendarDay,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(54.dp)
            .clickable { onClick() }
            .testTag("calendar_day_${day.dayNumber}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PurplePrimary else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) null else CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = day.dayOfWeek,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = "${day.dayNumber}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@Composable
fun TimelineCard(
    item: CalendarTimelineItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable { onEdit() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Colored dot / line
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(item.color)
                )

                Text(
                    text = item.time,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.width(68.dp)
                )

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(item.color.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.color,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            if (item.rawEvent != null) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete event",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddEditEventDialog(
    initialEvent: EventEntity?,
    initialDate: String,
    onDismiss: () -> Unit,
    onSave: (EventEntity) -> Unit
) {
    var title by remember { mutableStateOf(initialEvent?.title ?: "") }
    var category by remember { mutableStateOf(initialEvent?.category ?: "Work") }
    var date by remember { mutableStateOf(initialEvent?.date ?: initialDate) }
    var startTime by remember { mutableStateOf(initialEvent?.startTime ?: "10:00") }
    var endTime by remember { mutableStateOf(initialEvent?.endTime ?: "11:00") }
    var location by remember { mutableStateOf(initialEvent?.location ?: "") }
    var isWork by remember { mutableStateOf(initialEvent?.isWork ?: (category == "Work")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialEvent == null) "Add Event" else "Edit Event",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Event Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("event_title_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location / Link") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Category selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Work", "Personal", "Family", "Travel").forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = {
                                category = cat
                                isWork = cat == "Work"
                            },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val color = when (category) {
                            "Work" -> "#3B82F6"
                            "Personal" -> "#10B981"
                            "Family" -> "#EC4899"
                            "Travel" -> "#8B5CF6"
                            else -> "#F59E0B"
                        }
                        onSave(
                            initialEvent?.copy(
                                title = title,
                                category = category,
                                date = date,
                                startTime = startTime,
                                endTime = endTime,
                                location = location,
                                isWork = isWork,
                                colorHex = color
                            ) ?: EventEntity(
                                title = title,
                                category = category,
                                date = date,
                                startTime = startTime,
                                endTime = endTime,
                                location = location,
                                isWork = isWork,
                                colorHex = color
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                modifier = Modifier.testTag("dialog_save_event_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
