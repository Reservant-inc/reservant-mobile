package reservant_mobile.ui.activities

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
fun OrderSummaryActivity(reservationViewModel: ReservationViewModel, navController: NavHostController) {
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

            // Display the summary of the order
            Text(text = "Order Note: ${reservationViewModel.note.value}")
            Text(text = "Order Total: ${reservationViewModel.orderCost}")
            // Add more details about the order as needed

            Spacer(modifier = Modifier.height(16.dp))

            ButtonComponent(
                onClick = {
                    reservationViewModel.viewModelScope.launch {
                        // Finalize the order by creating it
                        reservationViewModel.createOrder()
                    }
                    navController.popBackStack()
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
