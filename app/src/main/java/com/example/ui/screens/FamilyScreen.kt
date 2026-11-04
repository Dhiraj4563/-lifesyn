package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.ContactEntity
import com.example.ui.components.LifeSyncTopBar
import com.example.ui.components.PrimaryGradientButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.LifeSyncViewModel

@Composable
fun FamilyScreen(
    viewModel: LifeSyncViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var contactToEdit by remember { mutableStateOf<ContactEntity?>(null) }

    Scaffold(
        topBar = {
            LifeSyncTopBar(
                title = "Family & Friends",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("family_screen")
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
                    Text(
                        text = "Important People & Dates",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                if (contacts.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Text(
                                text = "No contacts saved yet. Add family members or friends to track birthdays and anniversaries.",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(contacts) { contact ->
                        ContactCard(
                            contact = contact,
                            onClick = {
                                contactToEdit = contact
                                showAddDialog = true
                            },
                            onDelete = { viewModel.deleteContact(contact) }
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
                    text = "+ Add Contact / Important Date",
                    onClick = {
                        contactToEdit = null
                        showAddDialog = true
                    },
                    icon = Icons.Default.Add
                )
            }
        }
    }

    if (showAddDialog) {
        AddEditContactDialog(
            initialContact = contactToEdit,
            onDismiss = {
                showAddDialog = false
                contactToEdit = null
            },
            onSave = { contact ->
                if (contactToEdit == null) {
                    viewModel.addContact(contact)
                } else {
                    viewModel.updateContact(contact)
                }
                showAddDialog = false
                contactToEdit = null
            }
        )
    }
}

@Composable
fun ContactCard(
    contact: ContactEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(PinkAccent.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (contact.category.equals("family", true)) Icons.Default.Favorite else Icons.Default.Person,
                        contentDescription = null,
                        tint = PinkAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = contact.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "${contact.relationship.ifEmpty { contact.category }} • ${contact.phone.ifEmpty { "No phone" }}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    if (contact.birthday != null) {
                        Text(
                            text = "🎂 Birthday: ${contact.birthday}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = PinkAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                    if (contact.anniversary != null) {
                        Text(
                            text = "💍 Anniversary: ${contact.anniversary}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OrangeAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete contact",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun AddEditContactDialog(
    initialContact: ContactEntity?,
    onDismiss: () -> Unit,
    onSave: (ContactEntity) -> Unit
) {
    var name by remember { mutableStateOf(initialContact?.name ?: "") }
    var relationship by remember { mutableStateOf(initialContact?.relationship ?: "") }
    var category by remember { mutableStateOf(initialContact?.category ?: "FAMILY") }
    var phone by remember { mutableStateOf(initialContact?.phone ?: "") }
    var birthday by remember { mutableStateOf(initialContact?.birthday ?: "") }
    var anniversary by remember { mutableStateOf(initialContact?.anniversary ?: "") }
    var giftIdeas by remember { mutableStateOf(initialContact?.giftIdeas ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialContact == null) "Add Contact" else "Edit Contact",
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
                    label = { Text("Name *") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = relationship,
                    onValueChange = { relationship = it },
                    label = { Text("Relationship (e.g. Mother, Friend)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = birthday,
                        onValueChange = { birthday = it },
                        label = { Text("Birthday (MM-DD)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = anniversary,
                        onValueChange = { anniversary = it },
                        label = { Text("Anniversary") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = giftIdeas,
                    onValueChange = { giftIdeas = it },
                    label = { Text("Gift Ideas / Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            initialContact?.copy(
                                name = name,
                                relationship = relationship,
                                category = category,
                                phone = phone,
                                birthday = birthday.takeIf { it.isNotBlank() },
                                anniversary = anniversary.takeIf { it.isNotBlank() },
                                giftIdeas = giftIdeas
                            ) ?: ContactEntity(
                                name = name,
                                relationship = relationship,
                                category = category,
                                phone = phone,
                                birthday = birthday.takeIf { it.isNotBlank() },
                                anniversary = anniversary.takeIf { it.isNotBlank() },
                                giftIdeas = giftIdeas
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
