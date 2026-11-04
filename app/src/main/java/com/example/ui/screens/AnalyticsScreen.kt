package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.components.LifeSyncTopBar
import com.example.ui.components.StorageBadgeHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.LifeSyncViewModel
import java.util.Locale

@Composable
fun AnalyticsScreen(
    viewModel: LifeSyncViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val payments by viewModel.payments.collectAsStateWithLifecycle()
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    val meetings by viewModel.meetings.collectAsStateWithLifecycle()

    val completedTasks = remember(tasks) { tasks.count { it.status == "COMPLETED" } }
    val pendingTasks = remember(tasks) { tasks.count { it.status != "COMPLETED" } }
    val totalTasks = remember(tasks) { tasks.size }
    val taskCompletionRate = remember(totalTasks, completedTasks) {
        if (totalTasks > 0) (completedTasks.toFloat() / totalTasks.toFloat()) * 100f else 0f
    }

    val workTasks = remember(tasks) { tasks.count { it.category.equals("work", true) } }
    val personalTasks = remember(tasks) { tasks.count { !it.category.equals("work", true) } }

    val totalPaymentsDue = remember(payments) { payments.filter { it.status != "PAID" }.sumOf { it.amount } }
    val totalPaymentsPaid = remember(payments) { payments.filter { it.status == "PAID" }.sumOf { it.amount } }

    val avgGoalProgress = remember(goals) {
        if (goals.isNotEmpty()) {
            goals.map {
                if (it.targetDaysOrCount > 0) (it.currentProgress.toFloat() / it.targetDaysOrCount.toFloat()) * 100f
                else 0f
            }.average().toInt()
        } else 0
    }

    Scaffold(
        topBar = {
            LifeSyncTopBar(
                title = "Productivity Analytics",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("analytics_screen")
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

            // Task Completion Overview Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(18.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Task Completion Rate",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${taskCompletionRate.toInt()}% Completed",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GreenAccent
                                )
                            )
                            Text(
                                text = "$completedTasks done / $pendingTasks pending",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        LinearProgressIndicator(
                            progress = { (taskCompletionRate / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = GreenAccent,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }

            // Work vs Personal Balance
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(18.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Work vs. Personal Split",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        val totalSplit = (workTasks + personalTasks).coerceAtLeast(1)
                        val workRatio = workTasks.toFloat() / totalSplit.toFloat()
                        val personalRatio = personalTasks.toFloat() / totalSplit.toFloat()

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(workRatio.coerceAtLeast(0.05f))
                                    .fillMaxHeight()
                                    .background(BlueAccent)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(personalRatio.coerceAtLeast(0.05f))
                                    .fillMaxHeight()
                                    .background(PinkAccent)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(BlueAccent))
                                Text(
                                    text = "Work: $workTasks tasks",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(PinkAccent))
                                Text(
                                    text = "Personal: $personalTasks tasks",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                                )
                            }
                        }
                    }
                }
            }

            // Financial Commitments Summary
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(18.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Financial Overview",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Upcoming Due", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                Text(
                                    "₹${String.format(Locale.getDefault(), "%,.0f", totalPaymentsDue)}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OrangeAccent
                                    )
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Cleared This Cycle", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                Text(
                                    "₹${String.format(Locale.getDefault(), "%,.0f", totalPaymentsPaid)}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GreenAccent
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Habits & Goals Average Progress
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(18.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Average Goal & Habit Consistency",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Text(
                            text = "$avgGoalProgress% Consistent",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PurplePrimary
                            )
                        )

                        LinearProgressIndicator(
                            progress = { (avgGoalProgress / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = PurplePrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }
    }
}
