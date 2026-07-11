package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Debt
import com.example.ui.theme.*
import com.example.ui.util.Formatters
import com.example.viewmodel.FinanceViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val debtsState by viewModel.debtStats.collectAsState()
    val activeDebts = debtsState.activeDebts

    var selectedSection by remember { mutableStateOf("LENT") } // LENT (To Collect) or BORROWED (To Repay)
    var showAddDialog by remember { mutableStateOf(false) }
    var debtToResolve by remember { mutableStateOf<Debt?>(null) }

    // Filter active debts by type
    val filteredDebts = remember(activeDebts, selectedSection) {
        activeDebts.filter { it.type == selectedSection }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = CardPurple,
                contentColor = BrandOnPrimaryContainer,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 80.dp) // Offset above bottom nav
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Loan Record")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Section Headers: To Collect (Lent) vs To Repay (Borrowed)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selectedSection == "LENT") CardPurple else Color.Transparent)
                        .clickable { selectedSection = "LENT" },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "To Collect (Lent)",
                            color = if (selectedSection == "LENT") BrandOnPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = Formatters.formatCurrency(debtsState.totalLent),
                            color = if (selectedSection == "LENT") BrandOnPrimaryContainer.copy(alpha = 0.8f) else ColorIncome,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selectedSection == "BORROWED") CardPurple else Color.Transparent)
                        .clickable { selectedSection = "BORROWED" },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "To Repay (Borrowed)",
                            color = if (selectedSection == "BORROWED") BrandOnPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = Formatters.formatCurrency(debtsState.totalBorrowed),
                            color = if (selectedSection == "BORROWED") BrandOnPrimaryContainer.copy(alpha = 0.8f) else ColorExpense,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Loans list
            if (filteredDebts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = if (selectedSection == "LENT") Icons.Default.Handshake else Icons.Default.Payments,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (selectedSection == "LENT") "No outstanding money to collect" else "No outstanding debts to repay",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedSection == "LENT") {
                                "Record money you lent to people so you can remind them and track repayment status."
                            } else {
                                "Record loans you borrowed from people so you keep track of what you owe."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp, top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredDebts, key = { it.id }) { debt ->
                        DebtCardItem(
                            debt = debt,
                            onResolveClick = { debtToResolve = debt },
                            onDeleteClick = { viewModel.deleteDebt(debt) }
                        )
                    }
                }
            }
        }
    }

    // Add Debt / Loan Dialog Modal
    if (showAddDialog) {
        AddDebtDialog(
            defaultType = selectedSection,
            onDismiss = { showAddDialog = false },
            onSave = { personName, amount, type, description, dueDate ->
                viewModel.addDebt(personName, amount, type, description, dueDate)
                showAddDialog = false
            }
        )
    }

    // Resolve Confirmation Dialog
    debtToResolve?.let { debt ->
        ResolveDebtConfirmationDialog(
            debt = debt,
            onDismiss = { debtToResolve = null },
            onConfirm = { autoLogTransaction ->
                viewModel.resolveDebt(debt, autoLogTransaction)
                debtToResolve = null
            }
        )
    }
}

@Composable
fun DebtCardItem(
    debt: Debt,
    onResolveClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isLent = debt.type == "LENT"
    val colorAccent = if (isLent) ColorIncome else ColorExpense
    val isOverdue = Formatters.isOverdue(debt.dueDate)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Name & Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isLent) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = debt.personName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = Formatters.formatCurrency(debt.amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = colorAccent
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description / Note
            if (debt.description.isNotEmpty()) {
                Text(
                    text = debt.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Timestamps and Due Dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lent: ${Formatters.formatDate(debt.timestamp)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )

                // Due Date Pill
                if (debt.dueDate > 0L) {
                    val relativeDue = Formatters.formatRelativeDueDate(debt.dueDate)
                    val pillColor = if (isOverdue) {
                        ColorExpense
                    } else if (relativeDue == "Due Today" || relativeDue == "Due Tomorrow") {
                        Color(0xFFE67E22)
                    } else {
                        ColorIncome
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(pillColor.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = relativeDue,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = pillColor
                        )
                    }
                } else {
                    Text(
                        text = "No due date",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Debt",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Button(
                    onClick = onResolveClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorAccent
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Resolve",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AddDebtDialog(
    defaultType: String,
    onDismiss: () -> Unit,
    onSave: (String, Double, String, String, Long) -> Unit
) {
    var personName by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isLent by remember { mutableStateOf(defaultType == "LENT") }

    // Preset Options for Due Dates
    var selectedDueOption by remember { mutableStateOf("none") } // none, 1week, 2weeks, 1month

    var nameError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "New Loan / Debt",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Lent vs Borrowed segment
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isLent) Color(0xFF2ECC71) else Color.Transparent)
                            .clickable { isLent = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Lent (Money Out)",
                            color = if (isLent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!isLent) Color(0xFFE74C3C) else Color.Transparent)
                            .clickable { isLent = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Borrowed (Money In)",
                            color = if (!isLent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Person Name Field
                OutlinedTextField(
                    value = personName,
                    onValueChange = {
                        personName = it
                        nameError = false
                    },
                    label = { Text("Person Name") },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError,
                    supportingText = if (nameError) {
                        { Text("Please enter a name") }
                    } else null
                )

                // Amount Field
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = {
                        if (it.isEmpty() || it.toDoubleOrNull() != null || it.endsWith(".")) {
                            amountStr = it
                            amountError = false
                        }
                    },
                    label = { Text("Amount") },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = amountError,
                    supportingText = if (amountError) {
                        { Text("Please enter a valid amount") }
                    } else null
                )

                // Description Field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Reason") },
                    leadingIcon = { Icon(Icons.Default.Edit, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Due Date presets header
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Repayment Reminder Due:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val itemsList = listOf("none" to "No due", "1week" to "1 Wk", "2weeks" to "2 Wk", "1month" to "1 Mo")
                        itemsList.forEach { (option, labelText) ->
                            val isSelected = selectedDueOption == option
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                    .border(
                                        width = if (isSelected) 1.5.dp else 0.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedDueOption = option }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = labelText,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val amount = amountStr.toDoubleOrNull()
                            if (personName.trim().isEmpty()) {
                                nameError = true
                            } else if (amount == null || amount <= 0) {
                                amountError = true
                            } else {
                                val cal = Calendar.getInstance()
                                val dueDateMillis = when (selectedDueOption) {
                                    "1week" -> cal.apply { add(Calendar.WEEK_OF_YEAR, 1) }.timeInMillis
                                    "2weeks" -> cal.apply { add(Calendar.WEEK_OF_YEAR, 2) }.timeInMillis
                                    "1month" -> cal.apply { add(Calendar.MONTH, 1) }.timeInMillis
                                    else -> 0L
                                }
                                onSave(
                                    personName.trim(),
                                    amount,
                                    if (isLent) "LENT" else "BORROWED",
                                    description.trim(),
                                    dueDateMillis
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLent) Color(0xFF2ECC71) else Color(0xFFE74C3C)
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun ResolveDebtConfirmationDialog(
    debt: Debt,
    onDismiss: () -> Unit,
    onConfirm: (Boolean) -> Unit
) {
    val isLent = debt.type == "LENT"
    val accentColor = if (isLent) Color(0xFF2ECC71) else Color(0xFFE74C3C)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Resolve Loan?",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Are you sure you want to resolve this loan with ${debt.personName} for ${Formatters.formatCurrency(debt.amount)}?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = if (isLent) {
                        "Resolving this and logging a payment will automatically record this as INCOME of ${Formatters.formatCurrency(debt.amount)} in your balance tracker."
                    } else {
                        "Resolving this and logging a payment will automatically record this as EXPENSE of ${Formatters.formatCurrency(debt.amount)} in your balance tracker."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        confirmButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                TextButton(
                    onClick = { onConfirm(false) },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Resolve Only")
                }
                Button(
                    onClick = { onConfirm(true) },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Icon(Icons.Default.Receipt, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Resolve & Auto-Log Payment")
                }
            }
        }
    )
}
