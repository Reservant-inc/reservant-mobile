import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reservant_mobile.R
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.OrderItem
import reservant_mobile.ui.components.SearchBarWithFilter

@Composable
fun OrdersActivity() {
    val ordersViewModel = viewModel<OrdersViewModel>()
    val orders by ordersViewModel.filteredOrders.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<String?>(null) }

    val filterOptions = listOf(
        "Odebrano",
        "Anulowano"
    )

    Column(modifier = Modifier.padding(16.dp)) {
        IconWithHeader(
            icon = Icons.Rounded.History,
            text = stringResource(id = R.string.label_orders),
        )

        Spacer(modifier = Modifier.height(16.dp))

        SearchBarWithFilter(
            searchQuery = searchQuery,
            onSearchQueryChange = {
                searchQuery = it
                ordersViewModel.searchOrders(it)
            },
            onFilterSelected = { status ->
                selectedFilter = status ?: ""
                ordersViewModel.filterOrders(selectedFilter)
            },
            currentFilter = selectedFilter,
            filterOptions = filterOptions
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(orders) { order ->
                OrderItem(order = order)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}
