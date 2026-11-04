package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.backup.BackupStats
import com.example.data.database.UserProfileEntity
import com.example.ui.components.LifeSyncTopBar
import com.example.ui.components.StorageBadgeHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.LifeSyncViewModel
import java.io.File

@Composable
fun SettingsScreen(
    viewModel: LifeSyncViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle()
    val meetings by viewModel.meetings.collectAsStateWithLifecycle()
    val payments by viewModel.payments.collectAsStateWithLifecycle()
    val trips by viewModel.trips.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    val backupMessage by viewModel.backupStatusMessage.collectAsStateWithLifecycle()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showClearDataConfirmDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var backupJsonInput by remember { mutableStateOf("") }
    var inspectStats by remember { mutableStateOf<BackupStats?>(null) }
    var showStatsPreview by remember { mutableStateOf(false) }

    LaunchedEffect(backupMessage) {
        if (backupMessage != null) {
            Toast.makeText(context, backupMessage, Toast.LENGTH_LONG).show()
            viewModel.clearBackupStatus()
        }
    }

    val totalRecords = tasks.size + events.size + meetings.size + payments.size + trips.size + notes.size + contacts.size + goals.size

    Scaffold(
        topBar = {
            LifeSyncTopBar(
                title = "Settings & Storage",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("settings_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                StorageBadgeHeader()
            }

            // Profile Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showEditProfileDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(PurplePrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = PurplePrimary,
                                    modifier = Modifier.size(30.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = userProfile?.name ?: "Dhiraj",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = userProfile?.greeting ?: "Let's organize your day.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }

                        IconButton(onClick = { showEditProfileDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = PurplePrimary
                            )
                        }
                    }
                }
            }

            // Storage Disclosure Box
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCardElevated)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = GreenAccent)
                            Text(
                                text = "Strict Local Storage Policy",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GreenAccent
                                )
                            )
                        }
                        Text(
                            text = "• All personal tasks, events, finances, and trips are saved only on your local Android device SQLite/Room database.\n" +
                                    "• Android clears internal storage when uninstalling the app. Use the Export JSON Backup feature below to keep offline backups safe on your storage or files.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Database Records: $totalRecords",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "DB: Room SQLite",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }
            }

            // Backup & Restore Actions
            item {
                Text(
                    text = "Backup & Data Management",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        // Export to JSON
                        ListItem(
                            headlineContent = { Text("Export Backup (JSON)", fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text("Save complete database to internal/files backup") },
                            leadingContent = {
                                Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, tint = PurplePrimary)
                            },
                            trailingContent = {
                                Button(
                                    onClick = {
                                        viewModel.exportBackup { path ->
                                            Toast.makeText(context, "Saved to $path", Toast.LENGTH_LONG).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                                ) {
                                    Text("Export", fontSize = 12.sp)
                                }
                            }
                        )

                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Restore from JSON
                        ListItem(
                            headlineContent = { Text("Restore from Backup", fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text("Import verified JSON backup file or text") },
                            leadingContent = {
                                Icon(imageVector = Icons.Default.FileUpload, contentDescription = null, tint = BlueAccent)
                            },
                            trailingContent = {
                                OutlinedButton(onClick = { showRestoreDialog = true }) {
                                    Text("Restore", fontSize = 12.sp)
                                }
                            }
                        )

                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Load Demo Data
                        ListItem(
                            headlineContent = { Text("Load Demo Data", fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text("Re-seed realistic sample tasks, trips & finances") },
                            leadingContent = {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = GreenAccent)
                            },
                            trailingContent = {
                                TextButton(onClick = { viewModel.loadDemoData() }) {
                                    Text("Load", fontSize = 12.sp, color = GreenAccent)
                                }
                            }
                        )

                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Reset Database
                        ListItem(
                            headlineContent = { Text("Clear All Data", fontWeight = FontWeight.SemiBold, color = RedAccent) },
                            supportingContent = { Text("Permanently erase all local database records") },
                            leadingContent = {
                                Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, tint = RedAccent)
                            },
                            trailingContent = {
                                TextButton(onClick = { showClearDataConfirmDialog = true }) {
                                    Text("Clear", fontSize = 12.sp, color = RedAccent)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Profile Dialog
    if (showEditProfileDialog) {
        var nameInput by remember { mutableStateOf(userProfile?.name ?: "Dhiraj") }
        var greetingInput by remember { mutableStateOf(userProfile?.greeting ?: "Let's organize your day.") }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Your Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = greetingInput,
                        onValueChange = { greetingInput = it },
                        label = { Text("Personal Motto / Greeting") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val current = userProfile ?: UserProfileEntity()
                        viewModel.updateUserProfile(current.copy(name = nameInput, greeting = greetingInput))
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Restore Dialog
    if (showRestoreDialog) {
        var replaceAll by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = {
                showRestoreDialog = false
                inspectStats = null
                backupJsonInput = ""
            },
            title = { Text("Restore Local Backup", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Paste your exported LifeSync JSON backup text below:",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    OutlinedTextField(
                        value = backupJsonInput,
                        onValueChange = {
                            backupJsonInput = it
                            if (it.length > 50) {
                                viewModel.inspectBackup(it) { stats ->
                                    inspectStats = stats
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        placeholder = { Text("{\n  \"app\": \"LifeSync\", ...\n}") }
                    )

                    if (inspectStats != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GreenAccent.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Valid LifeSync Backup Found ✅", color = GreenAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Export Date: ${inspectStats!!.exportDate}", fontSize = 11.sp)
                                Text("Tasks: ${inspectStats!!.totalTasks} • Events: ${inspectStats!!.totalEvents} • Payments: ${inspectStats!!.totalPayments}", fontSize = 11.sp)
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(checked = replaceAll, onCheckedChange = { replaceAll = it })
                        Text("Replace all current records (Otherwise merges)", fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (backupJsonInput.isNotBlank()) {
                            viewModel.restoreBackup(backupJsonInput, replaceAll) { success ->
                                showRestoreDialog = false
                                inspectStats = null
                                backupJsonInput = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) {
                    Text("Confirm Restore")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRestoreDialog = false
                    inspectStats = null
                    backupJsonInput = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Clear Data Confirmation Dialog
    if (showClearDataConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataConfirmDialog = false },
            title = { Text("Clear All Local Data?", fontWeight = FontWeight.Bold, color = RedAccent) },
            text = {
                Text("This will permanently delete all your tasks, events, meetings, payments, trips, and notes from this device. Are you sure you want to proceed?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetDatabase()
                        showClearDataConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedAccent)
                ) {
                    Text("Yes, Delete All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
