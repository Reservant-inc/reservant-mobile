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
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.LoadingScreenWithTimeout
import reservant_mobile.ui.viewmodels.EmployeeOrderViewModel
import reservant_mobile.ui.viewmodels.OrderDetails
import reservant_mobile.ui.viewmodels.VisitDetailsUIState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration

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

            if (details.participants.isNotEmpty()) {
                ParticipantsList(participants = details.participants)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.orders_label),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(details.orders.size) { index ->
                    OrderCard(order = details.orders[index])
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    } ?: run {
        LoadingScreenWithTimeout(Duration.parse("10s"), stringResource(R.string.error_orders))
    }
}

@Composable
fun ClientInfoSection(visitDetails: VisitDetailsUIState) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        // Wyświetlanie klienta
        Text(
            text = stringResource(R.string.client_label, visitDetails.clientName),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val visitDateTime = LocalDateTime.parse(visitDetails.date, formatter)

        val isToday = visitDateTime.toLocalDate().isEqual(LocalDate.now())

        val formattedDateRange = if (isToday) {
            "${formatToDateTime(visitDetails.date, "HH:mm")} - ${
                formatToDateTime(
                    visitDetails.endTime,
                    "HH:mm"
                )
            }"
        } else {
            "${formatToDateTime(visitDetails.date, "dd.MM.yyyy HH:mm")} - ${
                formatToDateTime(
                    visitDetails.endTime,
                    "HH:mm"
                )
            }"
        }
        Text(
            text = formattedDateRange,
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
                    text = " " + visitDetails.numberOfPeople.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (visitDetails.reservationDate != "Unknown") {
                val reservationFormatter = DateTimeFormatter.ISO_LOCAL_DATE
                val reservationDate =
                    LocalDate.parse(visitDetails.reservationDate, reservationFormatter)

                val isReservationToday = reservationDate.isEqual(LocalDate.now())

                Row {
                    if (!isReservationToday) {
                        Text(
                            text = stringResource(R.string.reservation_date_label),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = " " + visitDetails.reservationDate,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Row {
                // Wyświetlanie depozytu, jeśli jest różny od -1.0
                if (visitDetails.deposit != -1.0) {
                    Text(
                        text = stringResource(R.string.deposit_label),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " " + "%.2f".format(visitDetails.deposit) + " zł",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                if (visitDetails.paymentTime != "Unknown") {
                    val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                    val paymentDateTime =
                        LocalDateTime.parse(visitDetails.paymentTime, dateTimeFormatter)

                    val isPaymentToday = paymentDateTime.toLocalDate().isEqual(LocalDate.now())
                    Text(
                        text = stringResource(R.string.payment_time_label),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (isPaymentToday) {
                            " " + formatToDateTime(visitDetails.paymentTime, "HH:mm")
                        } else {
                            " " + formatToDateTime(visitDetails.paymentTime, "dd.MM.yyyy HH:mm")
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Row {
                // Wyświetlanie napiwku, jeśli jest różny od -1.0
                if (visitDetails.tip != -1.0) {
                    Text(
                        text = stringResource(R.string.tip_label),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " " + "%.2f".format(visitDetails.tip) + " zł",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Wyświetlanie całkowitego kosztu
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
    Spacer(modifier = Modifier.height(8.dp))
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
                    text = order.status,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary,
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.status ?: stringResource(R.string.unknown_status),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${item.amount} x ${stringResource(R.string.price_label, item.cost)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        // Logika zmiany statusu zamówienia (do zaimplementowania)
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.change_status_button),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
