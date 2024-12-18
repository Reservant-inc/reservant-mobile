import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import reservant_mobile.data.models.dtos.MoneyDTO
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.ButtonComponent
import reservant_mobile.ui.components.FormInput
import reservant_mobile.ui.components.IconWithHeader
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun WalletActivity(
    onReturnClick: () -> Unit = {}
) {
    val viewModel: WalletViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WalletViewModel() as T
            }
        }
    )

    val balance by viewModel.balance
    val errorMessage by viewModel.errorMessage
    val walletHistoryFlow = viewModel.walletHistoryFlow.value
    val walletHistory = walletHistoryFlow?.collectAsLazyPagingItems()

    var showAddDialog by remember { mutableStateOf(false) }
    var amountText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            IconWithHeader(
                icon = Icons.Rounded.Wallet,
                text = "Wallet",
                showBackButton = true,
                onReturnClick = onReturnClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (errorMessage != null) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            }

            Text(
                text = "Balance: ${balance ?: "Loading..."} zł",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            ButtonComponent(
                onClick = { showAddDialog = true },
                label = "Add Money",
                icon = Icons.Default.Add
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Transaction History",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (walletHistory != null) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(walletHistory.itemCount) { index ->
                        val transaction = walletHistory[index]
                        transaction?.let {
                            TransactionCard(transaction = it)
                        }
                    }

                    walletHistory.apply {
                        when {
                            loadState.refresh is androidx.paging.LoadState.Loading -> {
                                item { Text("Loading...") }
                            }
                            loadState.append is androidx.paging.LoadState.Loading -> {
                                item { Text("Loading more...") }
                            }
                            loadState.refresh is androidx.paging.LoadState.Error -> {
                                item { Text("Error loading data") }
                            }
                            loadState.append is androidx.paging.LoadState.Error -> {
                                item { Text("Error loading more data") }
                            }
                        }
                    }
                }
            } else {
                Text("No history found or still loading...", modifier = Modifier.padding(16.dp))
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Money") },
            text = {
                Column {
                    FormInput(
                        inputText = amountText,
                        onValueChange = { amountText = it },
                        label = "Amount"
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount != null && amount > 0) {
                        viewModel.addMoneyToWallet(amount) {
                            showAddDialog = false
                            amountText = ""
                        }
                    } else {
                    }
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    amountText = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TransactionCard(transaction: MoneyDTO) {
    val formattedTime = transaction.time?.let {
        formatToDateTime(it, "dd.MM.yyyy HH:mm")
    } ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = transaction.title, style = MaterialTheme.typography.titleMedium)
            Text(text = "${transaction.amount} zł", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 4.dp))
            Text(text = "Time: $formattedTime", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
