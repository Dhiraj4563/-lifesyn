package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.MeetingEntity
import com.example.ui.components.LifeSyncTopBar
import com.example.ui.components.PrimaryGradientButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.LifeSyncViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WorkScreen(
    viewModel: LifeSyncViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val meetings by viewModel.meetings.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val workTasks = remember(tasks) { tasks.filter { it.category.equals("work", ignoreCase = true) } }

    var showAddMeetingDialog by remember { mutableStateOf(false) }
    var meetingToEdit by remember { mutableStateOf<MeetingEntity?>(null) }

    Scaffold(
        topBar = {
            LifeSyncTopBar(
                title = "Work & Office",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("work_screen")
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Meetings & Agendas",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                if (meetings.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Text(
                                text = "No work meetings scheduled. Tap Add Meeting to create one.",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(meetings) { meeting ->
                        MeetingCard(
                            meeting = meeting,
                            onClick = {
                                meetingToEdit = meeting
                                showAddMeetingDialog = true
                            },
                            onDelete = { viewModel.deleteMeeting(meeting) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Office Deliverables (${workTasks.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                items(workTasks) { task ->
                    TaskCard(
                        task = task,
                        onToggleComplete = { viewModel.toggleTaskComplete(task) },
                        onToggleStarred = { viewModel.toggleTaskStarred(task) },
                        onClick = { viewModel.toggleTaskComplete(task) },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                PrimaryGradientButton(
                    text = "+ Add Meeting",
                    onClick = {
                        meetingToEdit = null
                        showAddMeetingDialog = true
                    },
                    icon = Icons.Default.Add
                )
            }
        }
    }

    if (showAddMeetingDialog) {
        AddEditMeetingDialog(
            initialMeeting = meetingToEdit,
            onDismiss = {
                showAddMeetingDialog = false
                meetingToEdit = null
            },
            onSave = { meeting ->
                if (meetingToEdit == null) {
                    viewModel.addMeeting(meeting)
                } else {
                    viewModel.updateMeeting(meeting)
                }
                showAddMeetingDialog = false
                meetingToEdit = null
            }
        )
    }
}

@Composable
fun MeetingCard(
    meeting: MeetingEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BlueAccent.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = BlueAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = meeting.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "${meeting.date} • ${meeting.startTime} - ${meeting.endTime ?: "Done"}",
                            style = MaterialTheme.typography.bodySmall.copy(color = BlueAccent)
                        )
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete meeting",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (meeting.agenda.isNotBlank()) {
                Text(
                    text = "Agenda: ${meeting.agenda}",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "📍 ${meeting.location}",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                if (meeting.participants.isNotBlank()) {
                    Text(
                        text = "👥 ${meeting.participants}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        }
    }
}

@Composable
fun AddEditMeetingDialog(
    initialMeeting: MeetingEntity?,
    onDismiss: () -> Unit,
    onSave: (MeetingEntity) -> Unit
) {
    var title by remember { mutableStateOf(initialMeeting?.title ?: "") }
    var company by remember { mutableStateOf(initialMeeting?.company ?: "") }
    var agenda by remember { mutableStateOf(initialMeeting?.agenda ?: "") }
    var date by remember {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        mutableStateOf(initialMeeting?.date ?: sdf.format(Date()))
    }
    var startTime by remember { mutableStateOf(initialMeeting?.startTime ?: "10:00") }
    var location by remember { mutableStateOf(initialMeeting?.location ?: "Conference Room B") }
    var participants by remember { mutableStateOf(initialMeeting?.participants ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialMeeting == null) "Schedule Meeting" else "Edit Meeting",
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
                    label = { Text("Meeting Title *") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = agenda,
                    onValueChange = { agenda = it },
                    label = { Text("Agenda / Topic") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Time") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location / Video Link") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = participants,
                    onValueChange = { participants = it },
                    label = { Text("Participants") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            initialMeeting?.copy(
                                title = title,
                                company = company,
                                agenda = agenda,
                                date = date,
                                startTime = startTime,
                                location = location,
                                participants = participants
                            ) ?: MeetingEntity(
                                title = title,
                                company = company,
                                agenda = agenda,
                                date = date,
                                startTime = startTime,
                                location = location,
                                participants = participants
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
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
