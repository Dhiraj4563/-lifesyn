package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.LifeSyncViewModel

data class SearchResultItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val icon: ImageVector,
    val iconTint: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: LifeSyncViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchCategory by viewModel.searchCategory.collectAsStateWithLifecycle()

    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle()
    val meetings by viewModel.meetings.collectAsStateWithLifecycle()
    val payments by viewModel.payments.collectAsStateWithLifecycle()
    val trips by viewModel.trips.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()

    val categories = listOf("ALL", "TASKS", "EVENTS", "PAYMENTS", "TRIPS", "NOTES", "CONTACTS")

    val searchResults = remember(query, searchCategory, tasks, events, meetings, payments, trips, notes, contacts) {
        val q = query.trim()
        val list = mutableListOf<SearchResultItem>()

        if (q.isBlank()) return@remember list

        // Tasks
        if (searchCategory == "ALL" || searchCategory == "TASKS") {
            tasks.filter { it.title.contains(q, true) || it.description.contains(q, true) }.forEach {
                list.add(
                    SearchResultItem(
                        id = it.id,
                        title = it.title,
                        subtitle = "Task • Due: ${it.dueDate} • ${it.status}",
                        category = "Tasks",
                        icon = Icons.Default.CheckCircle,
                        iconTint = GreenAccent
                    )
                )
            }
        }

        // Events & Meetings
        if (searchCategory == "ALL" || searchCategory == "EVENTS") {
            events.filter { it.title.contains(q, true) || it.location.contains(q, true) }.forEach {
                list.add(
                    SearchResultItem(
                        id = it.id,
                        title = it.title,
                        subtitle = "Event • ${it.date} ${it.startTime} • ${it.location}",
                        category = "Events",
                        icon = Icons.Default.Event,
                        iconTint = PurplePrimary
                    )
                )
            }
            meetings.filter { it.title.contains(q, true) || it.agenda.contains(q, true) }.forEach {
                list.add(
                    SearchResultItem(
                        id = it.id,
                        title = it.title,
                        subtitle = "Meeting • ${it.date} ${it.startTime} • ${it.company}",
                        category = "Meetings",
                        icon = Icons.Default.Videocam,
                        iconTint = BlueAccent
                    )
                )
            }
        }

        // Payments
        if (searchCategory == "ALL" || searchCategory == "PAYMENTS") {
            payments.filter { it.title.contains(q, true) || it.category.contains(q, true) }.forEach {
                list.add(
                    SearchResultItem(
                        id = it.id,
                        title = it.title,
                        subtitle = "Payment • ${it.currency}${it.amount} • Due ${it.dueDate}",
                        category = "Payments",
                        icon = Icons.Default.AccountBalanceWallet,
                        iconTint = OrangeAccent
                    )
                )
            }
        }

        // Trips
        if (searchCategory == "ALL" || searchCategory == "TRIPS") {
            trips.filter { it.name.contains(q, true) || it.destination.contains(q, true) }.forEach {
                list.add(
                    SearchResultItem(
                        id = it.id,
                        title = it.name,
                        subtitle = "Trip • ${it.destination} • ${it.startDate}",
                        category = "Trips",
                        icon = Icons.Default.Flight,
                        iconTint = CyanAccent
                    )
                )
            }
        }

        // Notes
        if (searchCategory == "ALL" || searchCategory == "NOTES") {
            notes.filter { it.title.contains(q, true) || it.content.contains(q, true) }.forEach {
                list.add(
                    SearchResultItem(
                        id = it.id,
                        title = it.title,
                        subtitle = "Note • ${it.category}",
                        category = "Notes",
                        icon = Icons.Default.Description,
                        iconTint = PurplePrimary
                    )
                )
            }
        }

        // Contacts
        if (searchCategory == "ALL" || searchCategory == "CONTACTS") {
            contacts.filter { it.name.contains(q, true) || it.relationship.contains(q, true) }.forEach {
                list.add(
                    SearchResultItem(
                        id = it.id,
                        title = it.name,
                        subtitle = "Contact • ${it.relationship} • ${it.phone}",
                        category = "Contacts",
                        icon = Icons.Default.Person,
                        iconTint = PinkAccent
                    )
                )
            }
        }

        list
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search your entire life...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp)
                            .testTag("global_search_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("search_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Category filter chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = searchCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setSearchCategory(cat) },
                            label = { Text(cat, fontSize = 11.sp) },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PurplePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            if (query.isBlank()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Type keywords like 'Rent', 'Meeting', 'Trip', or 'Report' to search offline.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            } else if (searchResults.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No results found for '$query'",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(searchResults) { item ->
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
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(item.iconTint.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = item.iconTint,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = item.subtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
