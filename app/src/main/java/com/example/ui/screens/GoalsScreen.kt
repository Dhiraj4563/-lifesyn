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
import com.example.data.database.GoalEntity
import com.example.ui.components.LifeSyncTopBar
import com.example.ui.components.PrimaryGradientButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.LifeSyncViewModel

@Composable
fun GoalsScreen(
    viewModel: LifeSyncViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var goalToEdit by remember { mutableStateOf<GoalEntity?>(null) }

    Scaffold(
        topBar = {
            LifeSyncTopBar(
                title = "Goals & Routines",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("goals_screen")
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
                        text = "Build Consistency & Never Give Up",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                if (goals.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Text(
                                text = "No goals added yet. Start tracking your daily habits and personal targets!",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(goals) { goal ->
                        GoalCard(
                            goal = goal,
                            onIncrement = { viewModel.incrementGoal(goal) },
                            onClick = {
                                goalToEdit = goal
                                showAddDialog = true
                            },
                            onDelete = { viewModel.deleteGoal(goal) }
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
                    text = "+ Add Goal or Habit",
                    onClick = {
                        goalToEdit = null
                        showAddDialog = true
                    },
                    icon = Icons.Default.Add
                )
            }
        }
    }

    if (showAddDialog) {
        AddEditGoalDialog(
            initialGoal = goalToEdit,
            onDismiss = {
                showAddDialog = false
                goalToEdit = null
            },
            onSave = { goal ->
                if (goalToEdit == null) {
                    viewModel.addGoal(goal)
                } else {
                    viewModel.updateGoal(goal)
                }
                showAddDialog = false
                goalToEdit = null
            }
        )
    }
}

@Composable
fun GoalCard(
    goal: GoalEntity,
    onIncrement: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val progressRatio = remember(goal.currentProgress, goal.targetDaysOrCount) {
        if (goal.targetDaysOrCount > 0) {
            (goal.currentProgress.toFloat() / goal.targetDaysOrCount.toFloat()).coerceIn(0f, 1f)
        } else 0f
    }
    val percentage = (progressRatio * 100).toInt()

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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goal.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "${goal.category} • ${goal.type}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (goal.streakCount > 0) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = OrangeAccent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "🔥 ${goal.streakCount} streak",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = OrangeAccent,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            if (goal.description.isNotBlank()) {
                Text(
                    text = goal.description,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            // Progress bar & Counter
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${goal.currentProgress} / ${goal.targetDaysOrCount} completed ($percentage%)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = GreenAccent
                        )
                    )

                    Button(
                        onClick = onIncrement,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenAccent),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("+1 Progress", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                LinearProgressIndicator(
                    progress = { progressRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = GreenAccent,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

@Composable
fun AddEditGoalDialog(
    initialGoal: GoalEntity?,
    onDismiss: () -> Unit,
    onSave: (GoalEntity) -> Unit
) {
    var title by remember { mutableStateOf(initialGoal?.title ?: "") }
    var description by remember { mutableStateOf(initialGoal?.description ?: "") }
    var category by remember { mutableStateOf(initialGoal?.category ?: "Fitness") }
    var type by remember { mutableStateOf(initialGoal?.type ?: "DAILY") }
    var targetStr by remember { mutableStateOf(initialGoal?.targetDaysOrCount?.toString() ?: "30") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialGoal == null) "New Goal / Habit" else "Edit Goal",
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
                    label = { Text("Goal Title (e.g. 30 Min Exercise)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Motivation") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = targetStr,
                    onValueChange = { targetStr = it },
                    label = { Text("Target Count / Days") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("DAILY", "WEEKLY", "MONTHLY", "HABIT").forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(t, fontSize = 10.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetStr.toIntOrNull() ?: 30
                    if (title.isNotBlank()) {
                        onSave(
                            initialGoal?.copy(
                                title = title,
                                description = description,
                                category = category,
                                type = type,
                                targetDaysOrCount = target
                            ) ?: GoalEntity(
                                title = title,
                                description = description,
                                category = category,
                                type = type,
                                targetDaysOrCount = target
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
