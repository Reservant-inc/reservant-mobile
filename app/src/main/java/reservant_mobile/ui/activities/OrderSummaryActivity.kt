package reservant_mobile.ui.activities

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.OrderDTO
import reservant_mobile.ui.components.ButtonComponent
import reservant_mobile.ui.viewmodels.ReservationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSummaryActivity(orderId: Int, navController: NavHostController) {
    val reservationViewModel = viewModel<ReservationViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = ReservationViewModel() as T
        }
    )

    val orderResult by reservationViewModel.orderResult.collectAsState()

    LaunchedEffect(Unit) {
        reservationViewModel.getOrder(orderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.label_order_summary)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.label_back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            orderResult?.let { result ->
                if (!result.isError && result.value != null) {
                    OrderSummaryContent(order = result.value!!)
                } else {
                    Text(
                        text = stringResource(id = R.string.error_order_not_found),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ButtonComponent(
                onClick = {
                    reservationViewModel.cancelOrder(orderId)
                    navController.popBackStack()
                },
                label = stringResource(id = R.string.label_cancel_order),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun OrderSummaryContent(order: OrderDTO) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.label_order_details),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Wyświetlaj szczegóły zamówienia tutaj
        Text(text = "Order ID: ${order.orderId}")
        Text(text = "Order Total: ${order.cost}")
        // Dodaj więcej szczegółów zamówienia według potrzeb
    }
}
