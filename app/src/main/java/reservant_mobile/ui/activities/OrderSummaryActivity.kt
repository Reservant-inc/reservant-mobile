package reservant_mobile.ui.activities

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.ui.components.ButtonComponent
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.viewmodels.ReservationViewModel

@Composable
fun OrderSummaryActivity(
    reservationViewModel: ReservationViewModel,
    navController: NavHostController,
    restaurantId: Int,
    isReservation: Boolean
) {
    val errorMessage = reservationViewModel.errorMessage
    val context = LocalContext.current

    val totalCost = reservationViewModel.addedItems.sumOf { (menuItem, quantity) ->
        menuItem.price * quantity
    } + reservationViewModel.tip

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        IconWithHeader(
            icon = Icons.Filled.Summarize,
            text = stringResource(
                id = if (isReservation) R.string.label_reservation_summary else R.string.label_order_summary
            ),
            showBackButton = true,
            onReturnClick = { navController.popBackStack() }
        )
        Text(
            text = stringResource(
                id = if (isReservation) R.string.label_reservation_details else R.string.label_order_details
            ),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (!isReservation) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.label_item),
                    modifier = Modifier.weight(2f),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.label_qty),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.label_price),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            reservationViewModel.addedItems.forEach { (menuItem, quantity) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = menuItem.name,
                        modifier = Modifier.weight(2f),
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = quantity.toString(),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${String.format("%.2f", menuItem.price)} zł",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.summary_label_date),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )
            Text(
                text = reservationViewModel.visitDate.value,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.summary_label_time),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )
            Text(
                text = "${reservationViewModel.startTime.value} - ${reservationViewModel.endTime.value}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }

        if (!isReservation) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.summary_label_note),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
                Text(
                    text = reservationViewModel.note.value,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.summary_label_tip),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )
            Text(
                text = String.format("%.2f", reservationViewModel.tip) + " zł",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }

        if (!isReservation) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.summary_label_total),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = String.format("%.2f zł", totalCost),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ButtonComponent(
            onClick = {
                reservationViewModel.viewModelScope.launch {
                    val visitResult = reservationViewModel.createVisit(restaurantId)

                    if (!visitResult.isError && !isReservation) {
                        reservationViewModel.createOrder()
                    }
                }
            },
            label = stringResource(
                id = if (isReservation) R.string.label_confirm_reservation else R.string.label_confirm_order
            ),
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
