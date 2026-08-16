package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.MonthlyBreakdownCard
import com.example.ui.components.MonthlyBreakdownSheet
import com.example.ui.components.PermissionBanner
import com.example.ui.components.SimulateSmsDialog
import com.example.ui.components.SummaryCards
import com.example.ui.components.TransactionDetailSheet
import com.example.ui.components.TransactionItemCard
import com.example.ui.components.WeeklyComparisonChart
import com.example.ui.theme.SleekBlueContainer
import com.example.ui.theme.SleekNavyDark
import com.example.ui.theme.SleekNavyPrimary
import com.example.ui.theme.SleekOnBlueContainer
import com.example.ui.viewmodel.FinanceViewModel
import com.example.ui.viewmodel.TransactionFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val monthlySheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.notificationMessage) {
        uiState.notificationMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearNotificationMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SleekBlueContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "M",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = SleekOnBlueContainer
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "MAQAL",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.5).sp,
                                    color = SleekNavyDark
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SleekBlueContainer.copy(alpha = 0.5f)
                                ) {
                                    Text(
                                        text = "898 SAHAL",
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = SleekNavyPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                },
                actions = {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 1.dp,
                        modifier = Modifier.size(38.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.setSimulateDialogOpen(true) },
                            modifier = Modifier.testTag("simulate_sms_icon_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sms,
                                contentDescription = "Simulate SMS",
                                tint = SleekNavyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Box {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 1.dp,
                            modifier = Modifier.size(38.dp)
                        ) {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Menu",
                                    tint = SleekNavyDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Monthly Trends") },
                                onClick = {
                                    showMenu = false
                                    viewModel.setMonthlyBreakdownOpen(true)
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Simulate 898 SMS") },
                                onClick = {
                                    showMenu = false
                                    viewModel.setSimulateDialogOpen(true)
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Sms, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Reset Demo Records") },
                                onClick = {
                                    showMenu = false
                                    viewModel.resetDemoData()
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.setSimulateDialogOpen(true) },
                icon = { Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(18.dp)) },
                text = { Text("Test SMS Parser", fontWeight = FontWeight.SemiBold) },
                containerColor = SleekNavyPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.testTag("simulate_sms_fab")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .testTag("dashboard_lazy_column"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Permission Banner (if not granted)
            item {
                PermissionBanner(
                    onPermissionChanged = { granted ->
                        viewModel.setPermissionGranted(granted)
                    }
                )
            }

            // Sleek Summary Cards (Balance Hero + Money In / Out)
            item {
                SummaryCards(
                    currentBalance = uiState.currentBalance,
                    totalMoneyIn = uiState.totalMoneyIn,
                    totalMoneyOut = uiState.totalMoneyOut,
                    selectedFilter = uiState.selectedFilter,
                    onSelectFilter = { filter -> viewModel.setFilter(filter) }
                )
            }

            // Monthly Spending Trends Breakdown Card (This Month vs Previous Month)
            item {
                MonthlyBreakdownCard(
                    breakdownData = uiState.monthlyBreakdown,
                    onClickViewMore = { viewModel.setMonthlyBreakdownOpen(true) }
                )
            }

            // Weekly Velocity Chart
            item {
                WeeklyComparisonChart(
                    weeklyData = uiState.weeklyChartData
                )
            }

            // Search Bar & Filter Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Search Bar
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { 
                            Text(
                                text = "Search by recipient name or service provider...",
                                fontSize = 13.5.sp,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            ) 
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search transactions",
                                tint = if (uiState.searchQuery.isNotEmpty()) SleekNavyPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.setSearchQuery("") },
                                    modifier = Modifier.testTag("clear_search_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear search",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_text_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = SleekNavyPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    // Active Search Feedback / Result Count Badge
                    if (uiState.searchQuery.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${uiState.filteredTransactions.size} matching result${if (uiState.filteredTransactions.size == 1) "" else "s"} for \"${uiState.searchQuery}\"",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = SleekNavyPrimary,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "Reset",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { viewModel.setSearchQuery("") }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Filter Row with Counts
                    val allCount = uiState.transactions.size
                    val inCount = uiState.transactions.count { it.type == com.example.data.model.TransactionType.IN.code }
                    val outCount = uiState.transactions.count { it.type == com.example.data.model.TransactionType.OUT.code }
                    val servicesCount = uiState.transactions.count {
                        it.category.contains("Service", ignoreCase = true) ||
                                it.recipientOrSource.contains("adeeg", ignoreCase = true) ||
                                it.recipientOrSource.contains("card", ignoreCase = true) ||
                                (it.rawSms?.contains("adeeg", ignoreCase = true) == true)
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(TransactionFilter.entries) { filter ->
                            val isSelected = uiState.selectedFilter == filter
                            val count = when (filter) {
                                TransactionFilter.ALL -> allCount
                                TransactionFilter.MONEY_IN -> inCount
                                TransactionFilter.MONEY_OUT -> outCount
                                TransactionFilter.SERVICES -> servicesCount
                            }

                            FilterChip(
                                selected = isSelected,
                                onClick = { 
                                    if (isSelected && filter != TransactionFilter.ALL) {
                                        viewModel.setFilter(TransactionFilter.ALL)
                                    } else {
                                        viewModel.setFilter(filter)
                                    }
                                },
                                label = { 
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = filter.label, 
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                        Surface(
                                            shape = CircleShape,
                                            color = if (isSelected) SleekNavyPrimary else MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = "$count",
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SleekBlueContainer,
                                    selectedLabelColor = SleekNavyDark,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) SleekNavyPrimary else MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier.testTag("filter_chip_${filter.name.lowercase()}")
                            )
                        }
                    }
                }
            }

            // Recent Activity Section Header (Dynamic title matching active filter)
            item {
                val headerTitle = when (uiState.selectedFilter) {
                    TransactionFilter.ALL -> "Recent Activity"
                    TransactionFilter.MONEY_IN -> "Money In (${uiState.filteredTransactions.size})"
                    TransactionFilter.MONEY_OUT -> "Money Out (${uiState.filteredTransactions.size})"
                    TransactionFilter.SERVICES -> "Services & Cards (${uiState.filteredTransactions.size})"
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = headerTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (uiState.selectedFilter != TransactionFilter.ALL) {
                            Text(
                                text = "Show all",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = SleekNavyPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { viewModel.setFilter(TransactionFilter.ALL) }
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SleekBlueContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "Golis 898",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = SleekNavyPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Transaction Items List
            if (uiState.filteredTransactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (uiState.searchQuery.isNotEmpty()) Icons.Default.Search else Icons.Default.Receipt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (uiState.searchQuery.isNotEmpty()) "No results for \"${uiState.searchQuery}\"" else "No transactions found",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (uiState.searchQuery.isNotEmpty()) 
                                    "No recipient, service provider, or transaction matches this search." 
                                else 
                                    "Incoming Sahal (898) SMS messages will appear here automatically, or tap 'Test SMS Parser' to simulate one.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            if (uiState.searchQuery.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                androidx.compose.material3.TextButton(
                                    onClick = { viewModel.setSearchQuery("") },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Clear search", color = SleekNavyPrimary, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            } else {
                items(
                    items = uiState.filteredTransactions,
                    key = { it.id }
                ) { transaction ->
                    TransactionItemCard(
                        transaction = transaction,
                        onClick = { viewModel.selectTransaction(transaction) }
                    )
                }
            }
        }
    }

    // Simulate SMS Modal Dialog
    if (uiState.isSimulateDialogOpen) {
        SimulateSmsDialog(
            onDismiss = { viewModel.setSimulateDialogOpen(false) },
            onSimulate = { body, sender ->
                viewModel.simulateSms(body, sender)
            }
        )
    }

    // Transaction Detail Sheet
    uiState.selectedTransaction?.let { selected ->
        TransactionDetailSheet(
            transaction = selected,
            onDismiss = { viewModel.selectTransaction(null) },
            onDelete = { id -> viewModel.deleteTransaction(id) }
        )
    }

    // Monthly Spending Trends Breakdown Sheet
    if (uiState.isMonthlyBreakdownOpen) {
        MonthlyBreakdownSheet(
            breakdownData = uiState.monthlyBreakdown,
            sheetState = monthlySheetState,
            onDismiss = { viewModel.setMonthlyBreakdownOpen(false) }
        )
    }
}
