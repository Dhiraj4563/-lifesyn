package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.data.parser.ParsedQuickAddResult
import com.example.ui.components.PrimaryGradientButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.LifeSyncViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddBottomSheet(
    viewModel: LifeSyncViewModel,
    onDismiss: () -> Unit,
    onSelectModuleForm: (String) -> Unit // "TASK", "EVENT", "MEETING", "PAYMENT", "TRIP", "NOTE", "GOAL", "CONTACT"
) {
    var fastEntryText by remember { mutableStateOf("") }
    var parsedResult by remember { mutableStateOf<ParsedQuickAddResult?>(null) }
    var saveSuccessMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState())
                .testTag("quick_add_bottom_sheet"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Add Anything",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            // Smart NLP Fast Entry Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AutoFixHigh, contentDescription = null, tint = PurplePrimary)
                        Text(
                            text = "Smart Local Fast-Entry",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = PurplePrimary
                            )
                        )
                    }

                    OutlinedTextField(
                        value = fastEntryText,
                        onValueChange = {
                            fastEntryText = it
                            if (it.length > 3) {
                                parsedResult = viewModel.parseQuickAddInput(it)
                            } else {
                                parsedResult = null
                            }
                        },
                        placeholder = { Text("e.g. 'Pay rent 15000 on November 5' or 'Meeting tomorrow 10am'") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quick_add_smart_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Extracted Preview Card
                    if (parsedResult != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(PurplePrimary)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Detected: ${parsedResult!!.entityType}",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = PurplePrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = GreenAccent.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "Category: ${parsedResult!!.category}",
                                            fontSize = 11.sp,
                                            color = GreenAccent,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "Title: ${parsedResult!!.title}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )

                                if (parsedResult!!.amount != null) {
                                    Text(
                                        text = "Amount: ₹${String.format("%,.0f", parsedResult!!.amount)}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = OrangeAccent,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                Text(
                                    text = "Date: ${parsedResult!!.date} ${parsedResult!!.time?.let { "• Time: $it" } ?: ""}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )

                                Button(
                                    onClick = {
                                        viewModel.saveQuickAddResult(parsedResult!!)
                                        fastEntryText = ""
                                        parsedResult = null
                                        onDismiss()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("confirm_quick_add_save"),
                                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                                ) {
                                    Text("Confirm & Save Extracted Item")
                                }
                            }
                        }
                    }
                }
            }

            Text(
                text = "Or Choose Category to Add",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            // Grid of Manual Quick Add Buttons
            val modules = listOf(
                Triple("Add Task", Icons.Default.CheckCircle, GreenAccent) to "TASK",
                Triple("Add Event", Icons.Default.Event, PurplePrimary) to "EVENT",
                Triple("Add Meeting", Icons.Default.Videocam, BlueAccent) to "MEETING",
                Triple("Add Payment", Icons.Default.AccountBalanceWallet, OrangeAccent) to "PAYMENT",
                Triple("Add Trip", Icons.Default.Flight, CyanAccent) to "TRIP",
                Triple("Add Note", Icons.Default.Description, PurplePrimary) to "NOTE",
                Triple("Add Goal", Icons.Default.Flag, GreenAccent) to "GOAL",
                Triple("Add Birthday", Icons.Default.Cake, PinkAccent) to "CONTACT"
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                modules.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { (item, moduleKey) ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        onDismiss()
                                        onSelectModuleForm(moduleKey)
                                    }
                                    .testTag("quick_add_btn_$moduleKey"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(item.third.copy(alpha = 0.16f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = item.second,
                                            contentDescription = item.first,
                                            tint = item.third,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Text(
                                        text = item.first,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
