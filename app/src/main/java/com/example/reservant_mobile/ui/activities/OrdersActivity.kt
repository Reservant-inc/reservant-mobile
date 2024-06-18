import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reservant_mobile.R
import com.example.reservant_mobile.ui.components.ButtonComponent
import com.example.reservant_mobile.ui.components.IconWithHeader
import com.example.reservant_mobile.ui.components.OrderItem

@Composable
fun OrdersActivity() {
    val ordersViewModel = viewModel<OrdersViewModel>()
    val orders by ordersViewModel.orders.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        IconWithHeader(
            icon = Icons.Rounded.History,
            text = stringResource(id = R.string.label_orders),
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = stringResource(id = R.string.label_search)) },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = null)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ButtonComponent(onClick = { /*TODO*/ }, label = stringResource(id = R.string.label_filters))

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(orders) { order ->
                OrderItem(order = order)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}