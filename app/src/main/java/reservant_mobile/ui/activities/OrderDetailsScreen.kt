package reservant_mobile.ui.activities

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.reservant_mobile.R
import reservant_mobile.data.utils.formatDateTime
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.viewmodels.EmployeeOrderViewModel
import reservant_mobile.ui.viewmodels.OrderDetails
import reservant_mobile.ui.viewmodels.VisitDetailsUIState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun OrderDetailsScreen(
    visitId: Int,
    onReturnClick: () -> Unit,
    viewModel: EmployeeOrderViewModel
) {
    LaunchedEffect(visitId) {
        viewModel.fetchVisitDetailsById(visitId)
    }

    val visitDetails by viewModel.selectedVisitDetails.collectAsState()

    visitDetails?.let { details ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            IconWithHeader(
                icon = Icons.Outlined.Book,
                text = stringResource(R.string.order_details),
                showBackButton = true,
                onReturnClick = onReturnClick
            )

            Spacer(modifier = Modifier.height(8.dp))

            ClientInfoSection(details)

            Spacer(modifier = Modifier.height(16.dp))

            if(details.participants.isNotEmpty()) {
                ParticipantsList(participants = details.participants)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.orders_label),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            LazyColumn {
                items(details.orders.size) { index ->
                    OrderCard(order = details.orders[index])
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    } ?: run {
        Text(text = stringResource(R.string.loading_order_details), modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun ClientInfoSection(visitDetails: VisitDetailsUIState) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.client_label, visitDetails.clientName),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Text(
                    text = if (visitDetails.takeaway == true) stringResource(R.string.takeaway_label)
                    else stringResource(R.string.table_label),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (visitDetails.takeaway == true) "" else " " + visitDetails.tableId.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Row {
                Text(
                    text = stringResource(R.string.number_of_people_label),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text =  " " + visitDetails.numberOfPeople.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Text(
                    text = stringResource(R.string.date_label),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text =  " " + formatDateTime(visitDetails.date, "dd.MMM.yyyy HH:mm"),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row {
                Text(
                    text = stringResource(R.string.end_time_label),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text =  " " + formatDateTime(visitDetails.endTime, "dd.MMM.yyyy HH:mm"),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Text(
                    text = stringResource(R.string.reservation_date_label),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text =  " " + visitDetails.reservationDate,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row {
                Text(
                    text = stringResource(R.string.deposit_label),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text =  " " + if (visitDetails.deposit == -1.0) stringResource(R.string.unknown) else "%.2f".format(visitDetails.deposit),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Text(
                    text = stringResource(R.string.payment_time_label),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text =  " " + formatDateTime(visitDetails.paymentTime, "dd-MM-yyyy HH:mm"),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row {
                Text(
                    text = stringResource(R.string.tip_label),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text =  " " + if (visitDetails.tip == -1.0) stringResource(R.string.unknown) else "%.2f".format(visitDetails.tip),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.total_cost_label, "%.2f".format(visitDetails.totalCost)),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}


@Composable
fun ParticipantsList(participants: List<String>) {
    Text(
        text = stringResource(R.string.participants_label),
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )

    LazyColumn {
        items(participants.size) { index ->
            ParticipantCard(participantName = participants[index])
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ParticipantCard(participantName: String) {
    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
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
fun OrderCard(order: OrderDetails) {
    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.order_id_label, order.orderId),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(R.string.order_total_label, order.cost),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            order.items.forEach { item ->
                DishCard(item = item)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}


@Composable
fun DishCard(item: OrderDetails.MenuItemDetails) {
    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.quantity_label, item.amount),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(R.string.price_each_label, item.price),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(R.string.status_label, item.status ?: stringResource(R.string.unknown_status)),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = stringResource(R.string.price_label, item.cost),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        // Logika zmiany statusu zamówienia (do zaimplementowania)
                    },
                    modifier = Modifier
                        .padding(top = 8.dp)
                ) {
                    Text(text = stringResource(R.string.change_status_button))
                }
            }
        }
    }
}