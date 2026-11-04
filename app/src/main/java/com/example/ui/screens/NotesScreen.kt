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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.NoteEntity
import com.example.ui.components.LifeSyncTopBar
import com.example.ui.components.PrimaryGradientButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.LifeSyncViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotesScreen(
    viewModel: LifeSyncViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var noteToEdit by remember { mutableStateOf<NoteEntity?>(null) }

    val filteredNotes = remember(notes, searchQuery) {
        if (searchQuery.isBlank()) notes
        else notes.filter { it.title.contains(searchQuery, true) || it.content.contains(searchQuery, true) }
    }

    Scaffold(
        topBar = {
            LifeSyncTopBar(
                title = "Notes & Scratchpad",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("notes_screen")
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search your notes...") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                        },
                        shape = RoundedCornerShape(14.dp)
                    )
                }

                if (filteredNotes.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No notes yet. Tap + Add Note to jot down quick thoughts or checklists.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(filteredNotes) { note ->
                        NoteCard(
                            note = note,
                            onClick = {
                                noteToEdit = note
                                showAddDialog = true
                            },
                            onDelete = { viewModel.deleteNote(note) }
                        )
                    }
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
                    text = "+ Add Note",
                    onClick = {
                        noteToEdit = null
                        showAddDialog = true
                    },
                    icon = Icons.Default.Add
                )
            }
        }
    }

    if (showAddDialog) {
        AddEditNoteDialog(
            initialNote = noteToEdit,
            onDismiss = {
                showAddDialog = false
                noteToEdit = null
            },
            onSave = { note ->
                if (noteToEdit == null) {
                    viewModel.addNote(note)
                } else {
                    viewModel.updateNote(note)
                }
                showAddDialog = false
                noteToEdit = null
            }
        )
    }
}

@Composable
fun NoteCard(
    note: NoteEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val sdf = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    val dateStr = remember(note.updatedAt) { sdf.format(Date(note.updatedAt)) }

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
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (note.isPinned) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = OrangeAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete note",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = PurplePrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = note.category,
                        style = MaterialTheme.typography.labelSmall.copy(color = PurplePrimary),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
fun AddEditNoteDialog(
    initialNote: NoteEntity?,
    onDismiss: () -> Unit,
    onSave: (NoteEntity) -> Unit
) {
    var title by remember { mutableStateOf(initialNote?.title ?: "") }
    var content by remember { mutableStateOf(initialNote?.content ?: "") }
    var category by remember { mutableStateOf(initialNote?.category ?: "Personal") }
    var isPinned by remember { mutableStateOf(initialNote?.isPinned ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialNote == null) "New Note" else "Edit Note",
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
                    label = { Text("Note Title *") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Note Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Work", "Personal", "Travel", "Finance", "Ideas").forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 10.sp) }
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = isPinned,
                        onCheckedChange = { isPinned = it }
                    )
                    Text("Pin this note to the top")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            initialNote?.copy(
                                title = title,
                                content = content,
                                category = category,
                                isPinned = isPinned,
                                updatedAt = System.currentTimeMillis()
                            ) ?: NoteEntity(
                                title = title,
                                content = content,
                                category = category,
                                isPinned = isPinned
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
