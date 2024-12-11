package reservant_mobile.ui.activities

import WalletViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import reservant_mobile.data.models.dtos.MoneyDTO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletActivity(
) {
    val viewModel: WalletViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WalletViewModel() as T
            }
        }
    )
    val balance by viewModel.balance
    val errorMessage by viewModel.errorMessage

    val walletHistory = viewModel.walletHistoryFlow?.collectAsLazyPagingItems()

    var showAddDialog by remember { mutableStateOf(false) }
    var amountText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wallet") },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Money")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (errorMessage != null) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            }

            Text(text = "Balance: ${balance ?: "Loading..."} zł", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))

            walletHistory?.let { lazyItems ->
                LazyColumn(modifier = Modifier.fillMaxSize()) {

                    items(lazyItems.itemCount) { index ->
                        val transaction = lazyItems[index]
                        transaction?.let {
                            TransactionItem(transaction = it)
                        }
                    }

                    lazyItems.apply {
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
            } ?: run {
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
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Amount") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
                        // nieprawidłowa kwota
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
fun TransactionItem(transaction: MoneyDTO) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = transaction.title, style = MaterialTheme.typography.bodyLarge)
        Text(text = "${transaction.amount} zł", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Time: ${transaction.time}", style = MaterialTheme.typography.bodySmall)
    }
}
