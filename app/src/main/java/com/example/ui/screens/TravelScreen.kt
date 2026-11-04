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
import com.example.data.database.TripEntity
import com.example.ui.components.LifeSyncTopBar
import com.example.ui.components.PrimaryGradientButton
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.LifeSyncViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TravelScreen(
    viewModel: LifeSyncViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val trips by viewModel.trips.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf("Upcoming") } // Upcoming, Past, Ideas
    var showAddTripDialog by remember { mutableStateOf(false) }
    var tripToEdit by remember { mutableStateOf<TripEntity?>(null) }
    var viewingPackingTrip by remember { mutableStateOf<TripEntity?>(null) }

    val tabs = listOf("Upcoming", "Past", "Ideas")

    val filteredTrips = remember(trips, selectedTab) {
        when (selectedTab) {
            "Upcoming" -> trips.filter { it.status != "COMPLETED" && it.status != "IDEA" }
            "Past" -> trips.filter { it.status == "COMPLETED" }
            "Ideas" -> trips.filter { it.status == "IDEA" }
            else -> trips
        }
    }

    Scaffold(
        topBar = {
            LifeSyncTopBar(
                title = "Trips & Travel",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("travel_screen")
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Tabs
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tabs.forEach { tab ->
                            val isSelected = selectedTab == tab
                            Button(
                                onClick = { selectedTab = tab },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) PurplePrimary else MaterialTheme.colorScheme.surface,
                                    contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = tab,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }

                if (filteredTrips.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No trips found in $selectedTab. Tap + Add Trip to plan a getaway!",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                } else {
                    items(filteredTrips) { trip ->
                        TripCard(
                            trip = trip,
                            onClick = {
                                tripToEdit = trip
                                showAddTripDialog = true
                            },
                            onOpenPacking = {
                                viewingPackingTrip = trip
                            },
                            onDelete = { viewModel.deleteTrip(trip) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                PrimaryGradientButton(
                    text = "+ Add Trip",
                    onClick = {
                        tripToEdit = null
                        showAddTripDialog = true
                    },
                    icon = Icons.Default.Add,
                    testTag = "add_trip_button"
                )
            }
        }
    }

    if (showAddTripDialog) {
        AddEditTripDialog(
            initialTrip = tripToEdit,
            onDismiss = {
                showAddTripDialog = false
                tripToEdit = null
            },
            onSave = { trip ->
                if (tripToEdit == null) {
                    viewModel.addTrip(trip)
                } else {
                    viewModel.updateTrip(trip)
                }
                showAddTripDialog = false
                tripToEdit = null
            }
        )
    }

    if (viewingPackingTrip != null) {
        PackingListDialog(
            trip = viewingPackingTrip!!,
            onDismiss = { viewingPackingTrip = null },
            onUpdateChecklist = { updatedList ->
                viewModel.updateTrip(viewingPackingTrip!!.copy(packingChecklist = updatedList))
                viewingPackingTrip = null
            }
        )
    }
}

@Composable
fun TripCard(
    trip: TripEntity,
    onClick: () -> Unit,
    onOpenPacking: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("trip_card_${trip.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Visual Banner Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(NavyCardElevated, PurplePrimary.copy(alpha = 0.35f))
                        )
                    )
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = trip.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "📍 ${trip.destination}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
                        )
                    }

                    StatusBadge(trip.status)
                }
            }

            // Body info
            Column(
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${trip.startDate} — ${trip.endDate}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    if (trip.budget > 0) {
                        Text(
                            text = "Budget: ${trip.currency}${String.format(Locale.getDefault(), "%,.0f", trip.budget)}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OrangeAccent
                            )
                        )
                    }
                }

                // Tags strip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TripTagPill("Travel", Icons.Default.DirectionsTransit)
                        TripTagPill("Stay", Icons.Default.Hotel)
                        Surface(
                            onClick = onOpenPacking,
                            shape = RoundedCornerShape(8.dp),
                            color = PurplePrimary.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Checklist,
                                    contentDescription = null,
                                    tint = PurplePrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Packing",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PurplePrimary
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete trip",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TripTagPill(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PackingListDialog(
    trip: TripEntity,
    onDismiss: () -> Unit,
    onUpdateChecklist: (String) -> Unit
) {
    var checklistText by remember { mutableStateOf(trip.packingChecklist) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "${trip.name} Packing Checklist",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Items (comma or newline separated):",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                OutlinedTextField(
                    value = checklistText,
                    onValueChange = { checklistText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    placeholder = { Text("e.g. Passport, Camera, Sunscreen, Hiking Shoes") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onUpdateChecklist(checklistText) },
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun AddEditTripDialog(
    initialTrip: TripEntity?,
    onDismiss: () -> Unit,
    onSave: (TripEntity) -> Unit
) {
    var name by remember { mutableStateOf(initialTrip?.name ?: "") }
    var destination by remember { mutableStateOf(initialTrip?.destination ?: "") }
    var startDate by remember {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        mutableStateOf(initialTrip?.startDate ?: sdf.format(Date()))
    }
    var endDate by remember {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 3)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        mutableStateOf(initialTrip?.endDate ?: sdf.format(cal.time))
    }
    var budgetStr by remember { mutableStateOf(initialTrip?.budget?.toString() ?: "") }
    var status by remember { mutableStateOf(initialTrip?.status ?: "PLANNING") }
    var hotel by remember { mutableStateOf(initialTrip?.hotelDetails ?: "") }
    var packing by remember { mutableStateOf(initialTrip?.packingChecklist ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialTrip == null) "Plan New Trip" else "Edit Trip",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Trip Name (e.g. Nepal Trip 🇳🇵)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = { Text("Destination") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("Start Date") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text("End Date") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = budgetStr,
                    onValueChange = { budgetStr = it },
                    label = { Text("Estimated Budget (₹)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hotel,
                    onValueChange = { hotel = it },
                    label = { Text("Hotel / Stay Details") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Status selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("PLANNING", "CONFIRMED", "BOOKED", "COMPLETED").forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = { Text(s, fontSize = 10.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val budget = budgetStr.toDoubleOrNull() ?: 0.0
                        onSave(
                            initialTrip?.copy(
                                name = name,
                                destination = destination,
                                startDate = startDate,
                                endDate = endDate,
                                budget = budget,
                                status = status,
                                hotelDetails = hotel,
                                packingChecklist = packing
                            ) ?: TripEntity(
                                name = name,
                                destination = destination,
                                startDate = startDate,
                                endDate = endDate,
                                budget = budget,
                                status = status,
                                hotelDetails = hotel,
                                packingChecklist = packing
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
