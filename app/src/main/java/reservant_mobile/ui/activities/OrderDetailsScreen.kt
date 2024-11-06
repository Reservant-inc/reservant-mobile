package reservant_mobile.ui.activities

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.OrderDTO
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.ComboBox
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.LoadingScreenWithTimeout
import reservant_mobile.ui.viewmodels.EmployeeOrderViewModel
import reservant_mobile.ui.viewmodels.VisitDetailsUIState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration

@Composable
fun OrderDetailsScreen(
    visitId: Int,
    onReturnClick: () -> Unit,
    viewModel: EmployeeOrderViewModel,
    isReservation: Boolean = false
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
                icon = if (isReservation) Icons.Outlined.Event else Icons.Outlined.Book,
                text = stringResource(if (isReservation) R.string.reservation_details else R.string.order_details),
                showBackButton = true,
                onReturnClick = onReturnClick
            )

            Spacer(modifier = Modifier.height(8.dp))

            ClientInfoSection(details)

            Spacer(modifier = Modifier.height(16.dp))

            if (details.visit.participants?.isNotEmpty() == true) {
                ParticipantsList(participants = details.visit.participants.map { "${it.firstName} ${it.lastName}" })
            }

            if(details.visit.orders?.size != 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.orders_label),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(details.visit.orders?.size ?: 0) { index ->
                    val order = details.visit.orders?.get(index)
                    if (order != null) {
                        OrderCard(
                            order,
                            isReservation = isReservation,
                            visitDate = details.visit.date,
                            viewModel = viewModel
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            if(details.visit.orders?.size != 0) {
                Spacer(modifier = Modifier.height(16.dp))
            }

            NoteCard(note = "TO BE IMPLEMENTED XD") //TODO
            
            Spacer(modifier = Modifier.height(16.dp))

            if (isReservation) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        viewModel.approveVisit(visitId)
                        onReturnClick()
                    }) {
                        Text(text = stringResource(R.string.accept))
                    }
                    Button(onClick = {
                        viewModel.declineVisit(visitId)
                        onReturnClick()
                    }) {
                        Text(text = stringResource(R.string.decline))
                    }
                }
            }
        }
    } ?: run {
        LoadingScreenWithTimeout(Duration.parse("10s"), stringResource(R.string.error_orders))
    }
}

@Composable
fun OrderCard(
    order: OrderDTO,
    isReservation: Boolean = false,
    visitDate: String?,
    viewModel: EmployeeOrderViewModel
) {
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
                    text = order.status.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(R.string.order_total_label, order.cost ?: 0.0),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            order.items?.forEachIndexed { index, item ->
                DishCard(
                    item = item,
                    isReservation = isReservation,
                    visitDate = visitDate,
                    viewModel = viewModel,
                    orderId = order.orderId ?: 0,
                    visitId = order.visitId ?: 0
                )
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
        }
    }
}

@Composable
fun DishCard(
    item: OrderDTO.OrderItemDTO,
    isReservation: Boolean,
    visitDate: String?,
    viewModel: EmployeeOrderViewModel,
    orderId: Int,
    visitId: Int
) {
    val isToday = visitDate?.let {
        LocalDateTime.parse(it).toLocalDate().isEqual(LocalDate.now())
    } ?: false

    val showChangeStatusDialog = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.menuItem?.name ?: stringResource(R.string.unknown_dish),
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
                        text = "${item.amount} x ${stringResource(R.string.price_label, item.oneItemPrice ?: 0.0)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )

                    if (!isReservation && isToday) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { showChangeStatusDialog.value = true },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(text = stringResource(R.string.change_status_button))
                        }
                    }
                }
            }
        }
    }

    if (showChangeStatusDialog.value) {
        ChangeStatusDialog(
            onDismiss = { showChangeStatusDialog.value = false },
            onSubmit = { employeeId, status ->
                viewModel.changeOrderStatus(
                    orderId = orderId,
                    menuItemId = item.menuItemId ?: 0,
                    employeeId = employeeId,
                    status = status,
                    visitId = visitId
                )
                showChangeStatusDialog.value = false
            },
            viewModel = viewModel
        )
    }
}



@Composable
fun ClientInfoSection(visitDetails: VisitDetailsUIState) {
    val visit = visitDetails.visit
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

        val isToday = visit.date?.let {
            LocalDateTime.parse(it).toLocalDate().isEqual(LocalDate.now())
        } ?: false

        val formattedDateRange = if (isToday) {
            "${formatToDateTime(visit.date ?: "", "HH:mm")} - ${
                formatToDateTime(
                    visit.endTime ?: "", "HH:mm"
                )
            }"
        } else {
            "${formatToDateTime(visit.date ?: "", "dd.MM.yyyy HH:mm")} - ${
                formatToDateTime(
                    visit.endTime ?: "", "HH:mm"
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
                    text = if (visit.takeaway == true) stringResource(R.string.takeaway_label)
                    else stringResource(R.string.table_label),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (visit.takeaway == true) "" else " ${visit.tableId}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Row {
                Text(
                    text = stringResource(R.string.number_of_people_label),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                val totalGuests = (visit.numberOfGuests ?: 0) + (visit.participants?.size ?: 0) + 1

                Text(
                    text = " $totalGuests",
                    style = MaterialTheme.typography.bodyLarge
                )

            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (visit.reservationDate != null) {
                val reservationFormatter = DateTimeFormatter.ISO_LOCAL_DATE
                val reservationDate = LocalDate.parse(visit.reservationDate, reservationFormatter)

                val isReservationToday = reservationDate.isEqual(LocalDate.now())

                if (!isReservationToday) {
                    Text(
                        text = stringResource(R.string.reservation_date_label) + " ${reservationDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}",
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
            if (visit.paymentTime != null) {
                val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                val paymentDateTime = LocalDateTime.parse(visit.paymentTime, dateTimeFormatter)

                val isPaymentToday = paymentDateTime.toLocalDate().isEqual(LocalDate.now())
                val formattedPaymentTime = if (isPaymentToday) {
                    formatToDateTime(visit.paymentTime, "HH:mm")
                } else {
                    formatToDateTime(visit.paymentTime, "dd.MM.yyyy HH:mm")
                }

                Row {
                    Text(
                        text = stringResource(R.string.payment_time_label),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " $formattedPaymentTime",
                        style = MaterialTheme.typography.bodyMedium
                    )
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

        Text(
            text = if ((visit.orders?.sumOf { it.cost ?: 0.0 } ?: 0.0) == 0.0) {
                stringResource(R.string.reservation_label)
            } else {
                stringResource(R.string.total_cost_label, "%.2f".format(visit.orders?.sumOf { it.cost ?: 0.0 } ?: 0.0))
            },
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
fun NoteCard(note: String) {
    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.note_label),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = note,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ChangeStatusDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit,
    viewModel: EmployeeOrderViewModel
) {
    val employeeList by viewModel.employees.collectAsState()
    val employeeNames = employeeList.map { "${it.firstName} ${it.lastName}" }
    val employeeIdMap = employeeList.associateBy({ "${it.firstName} ${it.lastName}" }, { it.employeeId })

    var selectedEmployeeName by remember { mutableStateOf("") }
    val expandedEmployee = remember { mutableStateOf(false) }

    val statusOptions = listOf("Ordered", "InProgress", "Ready", "Delivered", "Cancelled")
    var selectedStatus by remember { mutableStateOf("") }
    val expandedStatus = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchEmployees()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Change Order Item Status") },
        text = {
            Column {
                ComboBox(
                    expanded = expandedEmployee,
                    value = selectedEmployeeName,
                    onValueChange = { selectedEmployeeName = it },
                    options = employeeNames,
                    label = "Select Employee"
                )
                Spacer(modifier = Modifier.height(8.dp))
                ComboBox(
                    expanded = expandedStatus,
                    value = selectedStatus,
                    onValueChange = { selectedStatus = it },
                    options = statusOptions,
                    label = "Select Status"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val employeeId = employeeIdMap[selectedEmployeeName]
                    if (employeeId != null && selectedStatus.isNotEmpty()) {
                        onSubmit(employeeId, selectedStatus)
                    }
                }
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}
