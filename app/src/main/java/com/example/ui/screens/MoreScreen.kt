package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.StorageBadgeHeader
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.LifeSyncViewModel

@Composable
fun MoreScreen(
    viewModel: LifeSyncViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val meetings by viewModel.meetings.collectAsStateWithLifecycle()
    val trips by viewModel.trips.collectAsStateWithLifecycle()
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val goals by viewModel.goals.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("more_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "More Modules",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            StorageBadgeHeader()
            Spacer(modifier = Modifier.height(6.dp))
        }

        item {
            MoreMenuCard(
                title = "Work & Office",
                subtitle = "Meetings, agendas, client follow-ups & deliverables",
                badge = "${meetings.size}",
                icon = Icons.Default.BusinessCenter,
                iconTint = BlueAccent,
                onClick = { onNavigate(Screen.Work.route) },
                testTag = "more_item_work"
            )
        }

        item {
            MoreMenuCard(
                title = "Trips & Travel",
                subtitle = "Itineraries, hotel bookings, flight/train details & packing list",
                badge = "${trips.size}",
                icon = Icons.Default.Flight,
                iconTint = CyanAccent,
                onClick = { onNavigate(Screen.Travel.route) },
                testTag = "more_item_travel"
            )
        }

        item {
            MoreMenuCard(
                title = "Family & Friends",
                subtitle = "Birthdays, anniversaries, call reminders & gift ideas",
                badge = "${contacts.size}",
                icon = Icons.Default.Favorite,
                iconTint = PinkAccent,
                onClick = { onNavigate(Screen.Family.route) },
                testTag = "more_item_family"
            )
        }

        item {
            MoreMenuCard(
                title = "Notes & Daily Scratchpad",
                subtitle = "Categorized personal notes, pinned ideas & checklists",
                badge = "${notes.size}",
                icon = Icons.Default.Description,
                iconTint = PurplePrimary,
                onClick = { onNavigate(Screen.Notes.route) },
                testTag = "more_item_notes"
            )
        }

        item {
            MoreMenuCard(
                title = "Goals & Routines",
                subtitle = "Daily habits, streak tracking, milestones & routines",
                badge = "${goals.size}",
                icon = Icons.Default.Flag,
                iconTint = GreenAccent,
                onClick = { onNavigate(Screen.Goals.route) },
                testTag = "more_item_goals"
            )
        }

        item {
            MoreMenuCard(
                title = "Productivity Analytics",
                subtitle = "Weekly trends, completion rate & financial distribution",
                badge = "Charts",
                icon = Icons.Default.BarChart,
                iconTint = OrangeAccent,
                onClick = { onNavigate(Screen.Analytics.route) },
                testTag = "more_item_analytics"
            )
        }

        item {
            MoreMenuCard(
                title = "Settings & Local Storage",
                subtitle = "Profile, JSON manual backup & restore, theme & database",
                badge = "100% Local",
                icon = Icons.Default.Settings,
                iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = { onNavigate(Screen.Settings.route) },
                testTag = "more_item_settings"
            )
        }
    }
}

@Composable
fun MoreMenuCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
    testTag: String = "more_menu_card"
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
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
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconTint.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = iconTint.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = iconTint,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
