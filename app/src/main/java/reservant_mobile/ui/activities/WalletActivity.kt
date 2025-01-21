import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.MoneyDTO
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.ButtonComponent
import reservant_mobile.ui.components.FormInput
import reservant_mobile.ui.components.IconWithHeader

@Composable
fun WalletActivity(
    onReturnClick: () -> Unit = {}
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
    val walletHistoryFlow = viewModel.walletHistoryFlow.value
    val walletHistory = walletHistoryFlow?.collectAsLazyPagingItems()
    var isError by remember { mutableStateOf(false) }

    var showAddDialog by remember { mutableStateOf(false) }
    var amountText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            IconWithHeader(
                icon = Icons.Rounded.Wallet,
                text = stringResource(id = R.string.wallet_title),
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
                text = stringResource(R.string.wallet_balance, balance ?: stringResource(R.string.loading)),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            ButtonComponent(
                onClick = { showAddDialog = true },
                label = stringResource(R.string.wallet_add_money),
                icon = Icons.Default.Add
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.wallet_transaction_history),
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
                                item { Text(stringResource(R.string.loading)) }
                            }
                            loadState.append is androidx.paging.LoadState.Loading -> {
                                item { Text(stringResource(R.string.loading_more)) }
                            }
                            loadState.refresh is androidx.paging.LoadState.Error -> {
                                item { Text(stringResource(R.string.error_loading_data)) }
                            }
                            loadState.append is androidx.paging.LoadState.Error -> {
                                item { Text(stringResource(R.string.error_loading_more_data)) }
                            }
                        }
                    }
                }
            } else {
                Text(stringResource(R.string.no_history_found), modifier = Modifier.padding(16.dp))
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(stringResource(R.string.wallet_add_money_title)) },
            text = {
                Column {
                    FormInput(
                        inputText = amountText,
                        onValueChange = { amountText = it },
                        label = stringResource(R.string.amount_label),
                        isError = isError,
                        errorText = stringResource(R.string.amount_error),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
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
                        isError = true
                    }
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    amountText = ""
                }) {
                    Text(stringResource(R.string.cancel))
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
            Text(
                text = stringResource(R.string.amount_with_currency, transaction.amount),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = stringResource(R.string.time_label, formattedTime),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
