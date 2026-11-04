package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.TaskEntity
import com.example.ui.components.PriorityBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.LifeSyncViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TasksScreen(
    viewModel: LifeSyncViewModel,
    modifier: Modifier = Modifier
) {
    val allTasks by viewModel.tasks.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TaskEntity?>(null) }
    var showCompletedSection by remember { mutableStateOf(true) }

    val categories = listOf("All", "Work", "Personal", "Family", "Finance", "Travel", "Shopping")

    val filteredTasks = remember(allTasks, selectedCategory, searchQuery) {
        allTasks.filter { task ->
            val matchCategory = selectedCategory == "All" || task.category.equals(selectedCategory, ignoreCase = true)
            val matchSearch = searchQuery.isBlank() || task.title.contains(searchQuery, ignoreCase = true) || task.description.contains(searchQuery, ignoreCase = true)
            matchCategory && matchSearch
        }
    }

    val pendingTasks = remember(filteredTasks) { filteredTasks.filter { it.status != "COMPLETED" } }
    val completedTasks = remember(filteredTasks) { filteredTasks.filter { it.status == "COMPLETED" } }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("tasks_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Screen Header & Search
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
                        Text(
                            text = "Tasks",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilledTonalIconButton(
                                onClick = {
                                    taskToEdit = null
                                    showAddDialog = true
                                },
                                modifier = Modifier.testTag("tasks_add_quick_icon")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Task",
                                    tint = PurplePrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search Input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tasks_search_input"),
                        placeholder = { Text("Search tasks...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Filter Pills
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = selectedCategory == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat) },
                                shape = RoundedCornerShape(20.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PurplePrimary,
                                    selectedLabelColor = Color.White,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    labelColor = MaterialTheme.colorScheme.onSurface
                                ),
                                border = if (isSelected) null else FilterChipDefaults.filterChipBorder(enabled = true, selected = false),
                                modifier = Modifier.testTag("filter_chip_$cat")
                            )
                        }
                    }
                }
            }

            // Pending Tasks List
            if (pendingTasks.isEmpty()) {
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
                                imageVector = Icons.Default.TaskAlt,
                                contentDescription = null,
                                tint = GreenAccent,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "All tasks completed! Great job.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            } else {
                items(pendingTasks) { task ->
                    TaskCard(
                        task = task,
                        onToggleComplete = { viewModel.toggleTaskComplete(task) },
                        onToggleStarred = { viewModel.toggleTaskStarred(task) },
                        onClick = {
                            taskToEdit = task
                            showAddDialog = true
                        },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }

            // Completed Tasks Collapsible Section
            if (completedTasks.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCompletedSection = !showCompletedSection },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Completed (${completedTasks.size})",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Icon(
                                imageVector = if (showCompletedSection) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Toggle completed",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (showCompletedSection) {
                    items(completedTasks) { task ->
                        TaskCard(
                            task = task,
                            onToggleComplete = { viewModel.toggleTaskComplete(task) },
                            onToggleStarred = { viewModel.toggleTaskStarred(task) },
                            onClick = {
                                taskToEdit = task
                                showAddDialog = true
                            },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                }
            }
        }

        // Floating Action Button for Quick Add Task
        FloatingActionButton(
            onClick = {
                taskToEdit = null
                showAddDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("tasks_fab_add"),
            containerColor = PurplePrimary,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Task",
                modifier = Modifier.size(28.dp)
            )
        }
    }

    if (showAddDialog) {
        AddEditTaskDialog(
            initialTask = taskToEdit,
            onDismiss = {
                showAddDialog = false
                taskToEdit = null
            },
            onSave = { task ->
                if (taskToEdit == null) {
                    viewModel.addTask(task)
                } else {
                    viewModel.updateTask(task)
                }
                showAddDialog = false
                taskToEdit = null
            }
        )
    }
}

@Composable
fun TaskCard(
    task: TaskEntity,
    onToggleComplete: () -> Unit,
    onToggleStarred: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val isCompleted = task.status == "COMPLETED"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable { onClick() }
            .testTag("task_item_${task.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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
                // Checkbox
                IconButton(
                    onClick = onToggleComplete,
                    modifier = Modifier
                        .size(26.dp)
                        .testTag("task_check_${task.id}")
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            tint = GreenAccent,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Mark Complete",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                            textDecoration = if (isCompleted) TextDecoration.LineThrough else null
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${task.category} • ${if (isCompleted) "Completed" else "Due ${task.dueDate}"}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        if (task.dueTime != null) {
                            Text(
                                text = "• ${task.dueTime}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PriorityBadge(task.priority)

                IconButton(
                    onClick = onToggleStarred,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (task.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Star",
                        tint = if (task.isStarred) OrangeAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddEditTaskDialog(
    initialTask: TaskEntity?,
    onDismiss: () -> Unit,
    onSave: (TaskEntity) -> Unit
) {
    var title by remember { mutableStateOf(initialTask?.title ?: "") }
    var description by remember { mutableStateOf(initialTask?.description ?: "") }
    var category by remember { mutableStateOf(initialTask?.category ?: "Work") }
    var dueDate by remember {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        mutableStateOf(initialTask?.dueDate ?: sdf.format(Date()))
    }
    var dueTime by remember { mutableStateOf(initialTask?.dueTime ?: "17:00") }
    var priority by remember { mutableStateOf(initialTask?.priority ?: "Medium") }
    var isStarred by remember { mutableStateOf(initialTask?.isStarred ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialTask == null) "New Task" else "Edit Task",
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
                    label = { Text("Task Title *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text("Due Date") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = dueTime,
                        onValueChange = { dueTime = it },
                        label = { Text("Due Time") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Category selection
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Work", "Personal", "Family", "Finance", "Travel", "Shopping").forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                // Priority selection
                Text(
                    text = "Priority",
                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Urgent", "High", "Medium", "Low").forEach { p ->
                        FilterChip(
                            selected = priority.equals(p, ignoreCase = true),
                            onClick = { priority = p },
                            label = { Text(p, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            initialTask?.copy(
                                title = title,
                                description = description,
                                category = category,
                                dueDate = dueDate,
                                dueTime = dueTime.takeIf { it.isNotBlank() },
                                priority = priority,
                                isStarred = isStarred,
                                updatedAt = System.currentTimeMillis()
                            ) ?: TaskEntity(
                                title = title,
                                description = description,
                                category = category,
                                dueDate = dueDate,
                                dueTime = dueTime.takeIf { it.isNotBlank() },
                                priority = priority,
                                isStarred = isStarred
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                modifier = Modifier.testTag("save_task_button")
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
