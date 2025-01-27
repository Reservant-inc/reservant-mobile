package reservant_mobile.ui.activities

import VisitHistoryViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.OrderDTO
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.IconWithHeader
import java.time.LocalDate

@Composable
fun VisitDetailActivity(
    visitId: Int,
    onReturnClick: () -> Unit
) {
    val viewModel: VisitHistoryViewModel = viewModel()
    val visit by viewModel.visit.collectAsState()
    val orderMap by viewModel.ordersMap.collectAsState()

    // Load the visit once
    LaunchedEffect(visitId) {
        viewModel.loadVisit(visitId)
    }

    if (visit == null) {
        // Show a basic loading or fallback message
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // Once loaded, show the detail layout
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            IconWithHeader(
                icon = Icons.Filled.Summarize,
                text = stringResource(R.string.visit_detail_title),
                showBackButton = true,
                onReturnClick = onReturnClick
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoSectionReadOnly(visit!!)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (visit!!.participants?.isNotEmpty() == true) {
                    item {
                        visit!!.participants?.let {
                            ParticipantsListReadOnly(participants = it.map {
                                "${it.firstName} ${it.lastName}"
                            })
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (visit!!.orders?.isNotEmpty() == true) {
                    item {
                        Text(
                            text = stringResource(R.string.orders_label),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(visit!!.orders!!.size) { index ->
                        val partialOrder = visit!!.orders!![index]
                        val orderId = partialOrder.orderId!!
                        val loadedOrder = orderMap[orderId]

                        // Trigger load if not present
                        LaunchedEffect(orderId) {
                            if (loadedOrder == null) {
                                viewModel.loadSingleOrder(orderId)
                            }
                        }

                        if (loadedOrder == null) {
                            CircularProgressIndicator()
                        } else {
                            OrderCardReadOnly(loadedOrder)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoSectionReadOnly(visit: VisitDTO) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        // Format date/time range
        val isToday = visit.date?.let {
            // Compare date portion to today's date
            val parsed = formatToDateTime(it, "yyyy-MM-dd")
            parsed.isNotEmpty() && parsed == formatToDateTime(LocalDate.now().toString(), "yyyy-MM-dd")
        } ?: false

        val startTimePattern = if (isToday) "HH:mm" else "dd.MM.yyyy HH:mm"
        val startTime = formatToDateTime(visit.date.orEmpty(), startTimePattern)
        val endTime = formatToDateTime(visit.endTime.orEmpty(), "HH:mm")

        Text(
            text = visit.restaurant!!.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        val formattedDateRange = "$startTime - $endTime"
        Text(
            text = formattedDateRange,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Table or takeaway + total guests
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (visit.takeaway == true) {
                Text(
                    text = stringResource(R.string.takeaway_label),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = stringResource(R.string.table_label) + " ${visit.tableId}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            val totalGuests = (visit.numberOfGuests ?: 0) + (visit.participants?.size ?: 0) + 1
            Row {
                Text(
                    text = stringResource(R.string.number_of_people_label),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = " $totalGuests",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Show deposit, tip, etc.
        Spacer(modifier = Modifier.height(8.dp))

        // If there's a deposit, etc.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (!visit.reservationDate.isNullOrEmpty()) {
                val dateOnly = formatToDateTime(visit.reservationDate!!, "dd.MM.yyyy")
                if (dateOnly.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.reservation_date_label) + " $dateOnly",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (visit.deposit != null && visit.deposit != -1.0) {
                Text(
                    text = stringResource(R.string.deposit_label) + " %.2f zł".format(visit.deposit),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            visit.paymentTime?.let { paymentTimeStr ->
                val payTime = formatToDateTime(paymentTimeStr, "dd.MM.yyyy HH:mm")
                if (payTime.isNotEmpty()) {
                    Row {
                        Text(
                            text = stringResource(R.string.payment_time_label),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " $payTime",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            if (visit.tip != null && visit.tip != 0.0) {
                Text(
                    text = stringResource(R.string.tip_label) + " %.2f zł".format(visit.tip),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // If there's an orders array, sum the cost
        val totalCost by remember {
            mutableStateOf(visit.orders?.sumOf { it.cost ?: 0.0 } ?: 0.0)
        }
        if (visit.orders?.size == 0) {
            Text(
                text = stringResource(R.string.reservation_label),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            /*Text(
                text = stringResource(R.string.total_cost_label, "%.2f".format(totalCost)),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )*/
        }
    }
}

@Composable
fun ParticipantsListReadOnly(participants: List<String>) {
    Column {
        Text(
            text = stringResource(R.string.participants_label),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        participants.forEach { participantName ->
            ParticipantCardReadOnly(participantName = participantName)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ParticipantCardReadOnly(participantName: String) {
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = participantName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun OrderCardReadOnly(order: OrderDTO) {
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Show order status & cost
            val status = order.status?.toString() ?: stringResource(R.string.unknown_status)
            Text(
                text = stringResource(R.string.order_status_colon, status),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.order_total_label, order.cost ?: 0.0),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Show each item
            val quantityLabel = stringResource(R.string.visit_quantity_label)  // e.g. "Qty"
            val priceLabel = stringResource(R.string.visit_price_label)        // e.g. "Price"
            val statusLabel = stringResource(R.string.visit_status_label)      // e.g. "Status"

            order.items?.forEachIndexed { index, item ->
                val dishName = item.menuItem?.name ?: stringResource(R.string.unknown_dish)
                val quantity = item.amount ?: 0
                val price = item.oneItemPrice ?: 0.0
                val itemStatus = item.status ?: stringResource(R.string.unknown_status)

                // Dish name in bold
                Text(
                    text = dishName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                // Single line for "Qty", "Price", "Status"
                Text(
                    text = buildString {
                        append("$quantityLabel: $quantity   ")
                        append("$priceLabel: ${"%.2f".format(price)}   ")
                        append("$statusLabel: $itemStatus")
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                // Divider if not the last item
                if (index < order.items.size - 1) {
                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.primary,
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            // If there's a note
            order.note?.let { noteStr ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.note_label),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = noteStr,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}