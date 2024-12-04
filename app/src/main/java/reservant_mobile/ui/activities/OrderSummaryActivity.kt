package reservant_mobile.ui.activities

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.ui.components.ButtonComponent
import reservant_mobile.ui.viewmodels.ReservationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSummaryActivity(
    reservationViewModel: ReservationViewModel,
    navController: NavHostController,
    restaurantId: Int
) {
    val errorMessage = reservationViewModel.errorMessage
    var context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.label_order_summary)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.label_back)
                        )
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
            Text(
                text = stringResource(id = R.string.label_order_details),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Display ordered items
            reservationViewModel.addedItems.forEach { (menuItem, quantity) ->
                Text(text = "${menuItem.name}: $quantity x ${String.format("%.2f", menuItem.price)} zł")
            }

            // Display total cost
            val totalCost = reservationViewModel.addedItems.sumOf { (menuItem, quantity) ->
                (menuItem.price ?: 0.0) * quantity
            } + reservationViewModel.tip
            Text(
                text = "${stringResource(id = R.string.label_total_cost)}: ${String.format("%.2f", totalCost)} zł",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            // Display other details
            Text(text = "Date: ${reservationViewModel.visitDate.value}")
            Text(text = "Time: ${reservationViewModel.startTime.value} - ${reservationViewModel.endTime.value}")
            Text(text = "Note: ${reservationViewModel.note.value}")
            Text(text = "Tip: ${String.format("%.2f", reservationViewModel.tip)} zł")

            Spacer(modifier = Modifier.height(16.dp))

            ButtonComponent(
                onClick = {
                    reservationViewModel.viewModelScope.launch {
                        reservationViewModel.createVisitAndOrder(
                            restaurantId = restaurantId,
                            isTakeaway = reservationViewModel.isTakeaway,
                            isDelivery = reservationViewModel.isDelivery
                        )
                        if (reservationViewModel.isOrderError() || reservationViewModel.isVisitError()) {
                            // Display error message
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        } else {
                            // Clear the cart
                            reservationViewModel.addedItems.clear()
                            navController.popBackStack()
                        }
                    }
                },
                label = stringResource(id = R.string.label_confirm_order),
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(8.dp))

            ButtonComponent(
                onClick = {
                    navController.popBackStack()
                },
                label = stringResource(id = R.string.label_cancel),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
