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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.PaymentEntity
import com.example.ui.components.PrimaryGradientButton
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.LifeSyncViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PaymentsScreen(
    viewModel: LifeSyncViewModel,
    modifier: Modifier = Modifier
) {
    val allPayments by viewModel.payments.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf("Upcoming") } // Upcoming, Paid, All
    var showAddDialog by remember { mutableStateOf(false) }
    var paymentToEdit by remember { mutableStateOf<PaymentEntity?>(null) }

    val tabs = listOf("Upcoming", "Paid", "All")

    val filteredPayments = remember(allPayments, selectedTab) {
        when (selectedTab) {
            "Upcoming" -> allPayments.filter { it.status != "PAID" }
            "Paid" -> allPayments.filter { it.status == "PAID" }
            else -> allPayments
        }
    }

    val totalUpcoming = remember(allPayments) {
        allPayments.filter { it.status != "PAID" }.sumOf { it.amount }
    }
    val totalPaid = remember(allPayments) {
        allPayments.filter { it.status == "PAID" }.sumOf { it.amount }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("payments_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 110.dp)
        ) {
            // Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Payments",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Summary Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Total Due",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "₹${String.format(Locale.getDefault(), "%,.0f", totalUpcoming)}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OrangeAccent
                                    )
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Paid So Far",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "₹${String.format(Locale.getDefault(), "%,.0f", totalPaid)}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GreenAccent
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Filter Tabs (Upcoming, Paid, All)
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
            }

            // Payment Cards List
            if (filteredPayments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No payments in this tab.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            } else {
                items(filteredPayments) { payment ->
                    PaymentRowCard(
                        payment = payment,
                        onTogglePaid = { viewModel.togglePaymentPaid(payment) },
                        onClick = {
                            paymentToEdit = payment
                            showAddDialog = true
                        },
                        onDelete = { viewModel.deletePayment(payment) }
                    )
                }
            }
        }

        // Bottom Add Payment Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            PrimaryGradientButton(
                text = "+ Add Payment",
                onClick = {
                    paymentToEdit = null
                    showAddDialog = true
                },
                icon = Icons.Default.Add,
                testTag = "add_payment_button"
            )
        }
    }

    if (showAddDialog) {
        AddEditPaymentDialog(
            initialPayment = paymentToEdit,
            onDismiss = {
                showAddDialog = false
                paymentToEdit = null
            },
            onSave = { payment ->
                if (paymentToEdit == null) {
                    viewModel.addPayment(payment)
                } else {
                    viewModel.updatePayment(payment)
                }
                showAddDialog = false
                paymentToEdit = null
            }
        )
    }
}

@Composable
fun PaymentRowCard(
    payment: PaymentEntity,
    onTogglePaid: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val isPaid = payment.status == "PAID"
    val (icon, iconColor) = getPaymentIconAndColor(payment.category, payment.title)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable { onClick() }
            .testTag("payment_item_${payment.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconColor.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = payment.category,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = payment.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "${payment.currency}${String.format(Locale.getDefault(), "%,.0f", payment.amount)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = if (isPaid) "Paid on ${payment.paidDate ?: payment.dueDate}" else "Due ${payment.dueDate}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isPaid) GreenAccent else OrangeAccent,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(
                    onClick = onTogglePaid,
                    modifier = Modifier.testTag("toggle_paid_${payment.id}")
                ) {
                    Icon(
                        imageVector = if (isPaid) Icons.Default.CheckCircle else Icons.Default.NotificationsActive,
                        contentDescription = "Toggle paid",
                        tint = if (isPaid) GreenAccent else PurplePrimary
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete payment",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun getPaymentIconAndColor(category: String, title: String): Pair<ImageVector, Color> {
    val lower = "$category $title".lowercase(Locale.ROOT)
    return when {
        lower.contains("electricity") || lower.contains("power") -> Icons.Default.Bolt to OrangeAccent
        lower.contains("rent") || lower.contains("house") -> Icons.Default.Home to PurplePrimary
        lower.contains("credit") || lower.contains("card") -> Icons.Default.CreditCard to BlueAccent
        lower.contains("mobile") || lower.contains("recharge") || lower.contains("phone") -> Icons.Default.PhoneAndroid to GreenAccent
        lower.contains("bike") || lower.contains("car") || lower.contains("emi") -> Icons.Default.TwoWheeler to RedAccent
        lower.contains("internet") || lower.contains("wifi") -> Icons.Default.Wifi to CyanAccent
        lower.contains("water") -> Icons.Default.WaterDrop to BlueAccent
        else -> Icons.Default.AccountBalanceWallet to OrangeAccent
    }
}

@Composable
fun AddEditPaymentDialog(
    initialPayment: PaymentEntity?,
    onDismiss: () -> Unit,
    onSave: (PaymentEntity) -> Unit
) {
    var title by remember { mutableStateOf(initialPayment?.title ?: "") }
    var amountStr by remember { mutableStateOf(initialPayment?.amount?.toString() ?: "") }
    var category by remember { mutableStateOf(initialPayment?.category ?: "Electricity") }
    var dueDate by remember {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        mutableStateOf(initialPayment?.dueDate ?: sdf.format(Date()))
    }
    var notes by remember { mutableStateOf(initialPayment?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialPayment == null) "Add Payment" else "Edit Payment",
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
                    label = { Text("Payment Title (e.g. Rent, Electricity)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount (₹)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Category selector
                Text("Category", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Electricity", "Rent", "Credit Card", "Mobile Recharge", "EMI").forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 10.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amount > 0) {
                        onSave(
                            initialPayment?.copy(
                                title = title,
                                amount = amount,
                                category = category,
                                dueDate = dueDate,
                                notes = notes
                            ) ?: PaymentEntity(
                                title = title,
                                amount = amount,
                                category = category,
                                dueDate = dueDate,
                                notes = notes,
                                status = "UPCOMING"
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
