package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.LifeSyncTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.LifeSyncViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationHistoryScreen(
    viewModel: LifeSyncViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val sdf = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }

    LaunchedEffect(Unit) {
        viewModel.markAllNotificationsRead()
    }

    Scaffold(
        topBar = {
            LifeSyncTopBar(
                title = "Alerts & Notifications",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("notification_history_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Alerts (${notifications.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    if (notifications.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearNotifications() }) {
                            Text("Clear All", color = RedAccent)
                        }
                    }
                }
            }

            if (notifications.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No notification history. When reminders fire, they will appear here.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(notifications) { notif ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(14.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(PurplePrimary.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = PurplePrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = notif.title,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = notif.message,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                                Text(
                                    text = sdf.format(Date(notif.timestamp)),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
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
