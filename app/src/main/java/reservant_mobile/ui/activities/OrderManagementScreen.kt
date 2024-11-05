package reservant_mobile.ui.activities

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.LazyPagingItems
import reservant_mobile.data.models.dtos.OrderDTO
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.FloatingTabSwitch
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.viewmodels.EmployeeOrderViewModel

@Composable
fun OrderManagementScreen(
    onReturnClick: () -> Unit,
    restaurantId: Int
) {
    val viewModel = viewModel<EmployeeOrderViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EmployeeOrderViewModel(restaurantId = restaurantId) as T
            }
        }
    )

    val currentOrdersFlow = viewModel.currentOrders.collectAsState().value
    val pastOrdersFlow = viewModel.pastOrders.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconWithHeader(icon = Icons.Outlined.Book, text = "Orders management", showBackButton = true, onReturnClick = onReturnClick)

        // FloatingTabSwitch for selecting current or past orders
        FloatingTabSwitch(
            pages = listOf(
                "Current" to {
                    currentOrdersFlow?.let { flow ->
                        val currentOrders = flow.collectAsLazyPagingItems()
                        OrderList(orders = currentOrders)
                    }
                },
                "Past" to {
                    pastOrdersFlow?.let { flow ->
                        val pastOrders = flow.collectAsLazyPagingItems()
                        OrderList(orders = pastOrders)
                    }
                }
            )
        )
    }
}

@Composable
fun OrderList(orders: LazyPagingItems<OrderDTO>?) {
    Column {
        Spacer(modifier = Modifier.height(90.dp))
        orders?.let {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(it.itemSnapshotList.items) { order ->
                    order?.let {
                        OrderCard(order = it)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: OrderDTO) {
    val formattedDate = order.date?.let { formatToDateTime(it, "HH:mm") }
    val formattedCost = order.cost?.let { "%.2f zł".format(it) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = formattedDate ?: "Unknown time",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = order.date?.substring(0, 10) ?: "Unknown date", style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    //Potencjalnie zmienić na użytkownika //TODO
                    text = order.note ?: "Unknown note",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = formattedCost ?: "Unknown cost", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                //Potencjalnie zmienić na stolik //TODO
                Text(text = "Visit ${order.visitId ?: "Unknown"}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}