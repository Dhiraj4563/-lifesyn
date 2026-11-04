package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@Composable
fun LifeSyncBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bottom_nav_bar"),
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple(Screen.Home, Icons.Default.Home, "home_tab"),
            Triple(Screen.Calendar, Icons.Default.CalendarMonth, "calendar_tab"),
            Triple(Screen.Tasks, Icons.Default.CheckCircle, "tasks_tab"),
            Triple(Screen.Payments, Icons.Default.AccountBalanceWallet, "payments_tab"),
            Triple(Screen.More, Icons.Default.MoreHoriz, "more_tab")
        )

        items.forEach { (screen, icon, testTag) ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                modifier = Modifier.testTag(testTag),
                selected = isSelected,
                onClick = { onNavigate(screen.route) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = screen.title,
                        tint = if (isSelected) PurplePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PurplePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = PurplePrimary.copy(alpha = 0.15f)
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeSyncTopBar(
    title: String,
    canNavigateBack: Boolean = false,
    onNavigateBack: () -> Unit = {},
    unreadNotificationCount: Int = 0,
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("top_bar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag("top_bar_search_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier.testTag("top_bar_notification_button")
            ) {
                BadgedBox(
                    badge = {
                        if (unreadNotificationCount > 0) {
                            Badge(
                                containerColor = RedAccent,
                                contentColor = Color.White
                            ) {
                                Text("$unreadNotificationCount")
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
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun StorageBadgeHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GreenAccent.copy(alpha = 0.12f))
            .border(1.dp, GreenAccent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(GreenAccent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = "100% Local Storage",
                    tint = GreenAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = "100% Local Storage",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = GreenAccent
                    )
                )
                Text(
                    text = "All data is saved on your device only. No cloud.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = "Private & Secure",
            tint = GreenAccent,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun StatCard(
    count: String,
    label: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "stat_card"
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconTint.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        if (actionText != null) {
            TextButton(
                onClick = onActionClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = PurplePrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
fun PriorityBadge(priority: String) {
    val (color, text) = when (priority.uppercase()) {
        "URGENT" -> RedAccent to "Urgent"
        "HIGH" -> OrangeAccent to "High"
        "MEDIUM" -> BlueAccent to "Medium"
        else -> GreenAccent to "Low"
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
fun StatusBadge(status: String) {
    val (color, text) = when (status.uppercase()) {
        "COMPLETED", "PAID", "CONFIRMED" -> GreenAccent to status.replace("_", " ").lowercase().replaceFirstChar { it.titlecase() }
        "IN_PROGRESS", "ONGOING", "PLANNING" -> BlueAccent to status.replace("_", " ").lowercase().replaceFirstChar { it.titlecase() }
        "DUE_TODAY", "OVERDUE" -> RedAccent to status.replace("_", " ").lowercase().replaceFirstChar { it.titlecase() }
        else -> OrangeAccent to status.replace("_", " ").lowercase().replaceFirstChar { it.titlecase() }
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
fun PrimaryGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    testTag: String = "primary_button"
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PurpleButtonGradient),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}
