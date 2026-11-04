package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.EventEntity
import com.example.data.database.PaymentEntity
import com.example.data.database.TaskEntity
import com.example.ui.components.*
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.LifeSyncViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: LifeSyncViewModel,
    onNavigate: (String) -> Unit,
    onOpenQuickAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle()
    val meetings by viewModel.meetings.collectAsStateWithLifecycle()
    val payments by viewModel.payments.collectAsStateWithLifecycle()
    val trips by viewModel.trips.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    val sdfDisplay = remember { SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()) }
    val sdfDateOnly = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val currentDateStr = remember { sdfDateOnly.format(Date()) }
    val currentDateDisplay = remember { sdfDisplay.format(Date()) }

    val unreadNotifications = remember(notifications) { notifications.count { !it.isRead } }

    val workTasksCount = remember(tasks) { tasks.count { it.category.equals("work", ignoreCase = true) && it.status != "COMPLETED" } }
    val personalTasksCount = remember(tasks) { tasks.count { !it.category.equals("work", ignoreCase = true) && it.status != "COMPLETED" } }
    val meetingsCount = remember(meetings, events) { meetings.size + events.count { it.isWork } }
    val upcomingTripsCount = remember(trips) { trips.count { it.status != "COMPLETED" } }
    val eventsCount = remember(events) { events.size }
    val paymentsDueAmount = remember(payments) {
        payments.filter { it.status != "PAID" }.sumOf { it.amount }
    }

    // Schedule items for today (Events + Meetings + Tasks with dueTime)
    val todaySchedule = remember(events, meetings, tasks, payments, currentDateStr) {
        val list = mutableListOf<ScheduleItem>()
        events.filter { it.date == currentDateStr }.forEach {
            list.add(
                ScheduleItem(
                    id = it.id,
                    time = it.startTime,
                    title = it.title,
                    category = it.category,
                    icon = if (it.isWork) Icons.Default.Work else Icons.Default.Event,
                    color = Color(android.graphics.Color.parseColor(it.colorHex.ifEmpty { "#8B5CF6" })),
                    type = "EVENT"
                )
            )
        }
        meetings.filter { it.date == currentDateStr }.forEach {
            list.add(
                ScheduleItem(
                    id = it.id,
                    time = it.startTime,
                    title = it.title,
                    category = "Work",
                    icon = Icons.Default.Videocam,
                    color = BlueAccent,
                    type = "MEETING"
                )
            )
        }
        payments.filter { it.dueDate == currentDateStr && it.status != "PAID" }.forEach {
            list.add(
                ScheduleItem(
                    id = it.id,
                    time = "17:30",
                    title = "Pay ${it.title}",
                    category = "Payment",
                    icon = Icons.Default.AccountBalanceWallet,
                    color = OrangeAccent,
                    type = "PAYMENT"
                )
            )
        }
        list.sortBy { it.time }
        list
    }

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Good Morning ☀️"
            in 12..16 -> "Good Afternoon 🌤️"
            in 17..21 -> "Good Evening 👋"
            else -> "Good Night 🌙"
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(PurplePrimary.copy(alpha = 0.2f))
                                .border(1.5.dp, PurplePrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = PurplePrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Column {
                            Text(
                                text = greeting,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                            Text(
                                text = "${userProfile?.name ?: "Dhiraj"} • $currentDateDisplay",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { onNavigate(Screen.Search.route) },
                            modifier = Modifier.testTag("home_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        IconButton(
                            onClick = { onNavigate(Screen.Notifications.route) },
                            modifier = Modifier.testTag("home_notification_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (unreadNotifications > 0) {
                                        Badge(containerColor = RedAccent) {
                                            Text("$unreadNotifications")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Storage Banner
                StorageBadgeHeader()

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Navigation Horizontal Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickModulePill("Work", Icons.Default.BusinessCenter, BlueAccent) { onNavigate(Screen.Work.route) }
                    QuickModulePill("Tasks", Icons.Default.CheckCircle, GreenAccent) { onNavigate(Screen.Tasks.route) }
                    QuickModulePill("Schedule", Icons.Default.CalendarMonth, PurplePrimary) { onNavigate(Screen.Calendar.route) }
                    QuickModulePill("Payments", Icons.Default.AccountBalanceWallet, OrangeAccent) { onNavigate(Screen.Payments.route) }
                    QuickModulePill("Trips", Icons.Default.Flight, CyanAccent) { onNavigate(Screen.Travel.route) }
                    QuickModulePill("Family", Icons.Default.Favorite, PinkAccent) { onNavigate(Screen.Family.route) }
                    QuickModulePill("Progress", Icons.Default.TrendingUp, GreenAccent) { onNavigate(Screen.Analytics.route) }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 6 Grid Stats Cards
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            count = "$workTasksCount",
                            label = "Work Tasks",
                            icon = Icons.Default.BusinessCenter,
                            iconTint = BlueAccent,
                            onClick = { onNavigate(Screen.Tasks.route) },
                            modifier = Modifier.weight(1f),
                            testTag = "stat_work_tasks"
                        )
                        StatCard(
                            count = "$meetingsCount",
                            label = "Meetings",
                            icon = Icons.Default.Videocam,
                            iconTint = PurplePrimary,
                            onClick = { onNavigate(Screen.Work.route) },
                            modifier = Modifier.weight(1f),
                            testTag = "stat_meetings"
                        )
                        StatCard(
                            count = "$personalTasksCount",
                            label = "Personal",
                            icon = Icons.Default.Person,
                            iconTint = GreenAccent,
                            onClick = { onNavigate(Screen.Tasks.route) },
                            modifier = Modifier.weight(1f),
                            testTag = "stat_personal"
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            count = "₹${String.format(Locale.getDefault(), "%,.0f", paymentsDueAmount)}",
                            label = "Payments Due",
                            icon = Icons.Default.AccountBalanceWallet,
                            iconTint = OrangeAccent,
                            onClick = { onNavigate(Screen.Payments.route) },
                            modifier = Modifier.weight(1f),
                            testTag = "stat_payments_due"
                        )
                        StatCard(
                            count = "$upcomingTripsCount",
                            label = "Trips",
                            icon = Icons.Default.Flight,
                            iconTint = CyanAccent,
                            onClick = { onNavigate(Screen.Travel.route) },
                            modifier = Modifier.weight(1f),
                            testTag = "stat_trips"
                        )
                        StatCard(
                            count = "$eventsCount",
                            label = "Events",
                            icon = Icons.Default.Event,
                            iconTint = PinkAccent,
                            onClick = { onNavigate(Screen.Calendar.route) },
                            modifier = Modifier.weight(1f),
                            testTag = "stat_events"
                        )
                    }
                }
            }
        }

        // Today's Schedule Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                SectionHeader(
                    title = "Today's Schedule",
                    actionText = "View All →",
                    onActionClick = { onNavigate(Screen.Calendar.route) }
                )

                if (todaySchedule.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EventAvailable,
                                contentDescription = null,
                                tint = GreenAccent
                            )
                            Text(
                                text = "No scheduled events for today. Tap (+) to add one!",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        todaySchedule.forEach { item ->
                            ScheduleRow(item = item, onClick = { onNavigate(Screen.Calendar.route) })
                        }
                    }
                }
            }
        }

        // Important / Starred Tasks Section
        item {
            val starredTasks = tasks.filter { it.isStarred && it.status != "COMPLETED" }
            if (starredTasks.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    SectionHeader(
                        title = "Important Tasks",
                        actionText = "All Tasks →",
                        onActionClick = { onNavigate(Screen.Tasks.route) }
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        starredTasks.take(3).forEach { task ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigate(Screen.Tasks.route) },
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
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        IconButton(
                                            onClick = { viewModel.toggleTaskComplete(task) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.RadioButtonUnchecked,
                                                contentDescription = "Check task",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = task.title,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                ),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${task.category} • Due: ${task.dueDate}",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                        }
                                    }
                                    PriorityBadge(task.priority)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Upcoming Payments Quick Alert
        item {
            val duePayments = payments.filter { it.status != "PAID" }.take(2)
            if (duePayments.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    SectionHeader(
                        title = "Upcoming Payments",
                        actionText = "Manage →",
                        onActionClick = { onNavigate(Screen.Payments.route) }
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        duePayments.forEach { payment ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigate(Screen.Payments.route) },
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
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(OrangeAccent.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AccountBalanceWallet,
                                                contentDescription = null,
                                                tint = OrangeAccent,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = payment.title,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                            Text(
                                                text = "Due: ${payment.dueDate}",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${payment.currency}${String.format(Locale.getDefault(), "%,.0f", payment.amount)}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = OrangeAccent
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class ScheduleItem(
    val id: String,
    val time: String,
    val title: String,
    val category: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val type: String
)

@Composable
fun ScheduleRow(
    item: ScheduleItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
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
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.category,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.NotificationsNone,
                contentDescription = "Reminder set",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun QuickModulePill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}
