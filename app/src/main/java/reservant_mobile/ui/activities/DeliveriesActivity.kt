package reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.reservant_mobile.R
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import reservant_mobile.data.models.dtos.DeliveryDTO
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.viewmodels.DeliveriesViewModel

@Composable
fun DeliveriesActivity(
    navController: NavHostController,
    restaurantId: Int,
    onReturnClick: () -> Unit
) {

    val deliveriesViewModel: DeliveriesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                DeliveriesViewModel(restaurantId = restaurantId) as T
        }
    )

    val deliveriesFlow = deliveriesViewModel.deliveries.collectAsState()
    val lazyPagingItems = deliveriesFlow.value?.collectAsLazyPagingItems()


    val errorMessage by deliveriesViewModel.errorMessage.collectAsState()

    val isLoading by deliveriesViewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        IconWithHeader(
            text = stringResource(R.string.label_deliveries),
            showBackButton = true,
            onReturnClick = { onReturnClick() },
            icon = Icons.Filled.DeliveryDining
        )

        errorMessage?.let { err ->
            Text(
                text = err,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            lazyPagingItems?.let { pagingItems ->
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(pagingItems.itemCount) { index ->
                        val delivery = pagingItems[index]
                        if (delivery != null) {
                            DeliveryItem(delivery = delivery)
                        }
                    }
                }
            } ?: run {
                Text(
                    text = stringResource(R.string.label_deliveries),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun DeliveryItem(delivery: DeliveryDTO) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = stringResource(R.string.label_delivery) + ": #${delivery.deliveryId}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text =  stringResource(R.string.label_delivery_date) + ": ${delivery.orderTime ?: "--"}"
        )
        Text(
            text = stringResource(R.string.label_delivery_cost) + ": ${delivery.cost ?: 0.0} PLN"
        )

    }
}
