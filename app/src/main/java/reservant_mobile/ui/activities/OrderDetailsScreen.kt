package reservant_mobile.ui.activities

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.TableBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.models.dtos.OrderDTO
import reservant_mobile.data.services.UserService
import reservant_mobile.data.utils.StatusUtils
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.ComboBox
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.LoadingScreenWithTimeout
import reservant_mobile.ui.navigation.RestaurantRoutes
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
    isReservation: Boolean = false,
    navHostController: NavHostController
) {
    LaunchedEffect(visitId) {
        viewModel.fetchVisitDetailsById(visitId)
        viewModel.fetchTables()
    }

    val visitDetails by viewModel.selectedVisitDetails.collectAsState()

    var showChangeTableDialog by remember { mutableStateOf(false) }

    if (showChangeTableDialog) {
        ChangeTableDialog(
            onDismiss = { showChangeTableDialog = false },
            onSubmit = { newTableId ->
                viewModel.updateTable(visitId, newTableId)
                showChangeTableDialog = false
            },
            viewModel = viewModel
        )
    }

    visitDetails?.let { details ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            IconWithHeader(
                icon = if (isReservation) Icons.Outlined.Event else Icons.Outlined.Book,
                text = stringResource(if (isReservation) R.string.reservation_details else R.string.order_details),
                showBackButton = true,
                onReturnClick = onReturnClick,
                actions = {

                    IconButton(
                        onClick = { showChangeTableDialog = true }
                    ) {
                        Icon(
                            Icons.Outlined.TableBar,
                            contentDescription = "Change Table"
                        )
                    }

                }
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    ClientInfoSection(details, navHostController)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (details.visit.participants?.isNotEmpty() == true) {
                    item {
                        ParticipantsList(participants = details.visit.participants.map { "${it.firstName} ${it.lastName}" })
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (details.visit.orders?.isNotEmpty() == true) {
                    item {
                        Text(
                            text = stringResource(R.string.orders_label),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(details.visit.orders.size) { index ->
                        val order = details.visit.orders[index]
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

            if (isReservation) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            viewModel.approveVisit(visitId)
                            onReturnClick()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = stringResource(R.string.accept))
                    }
                    Button(
                        onClick = {
                            viewModel.declineVisit(visitId)
                            onReturnClick()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
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
                    text = StatusUtils.getStatusDisplayName(
                        order.status?.toString() ?: "",
                        LocalContext.current
                    ),
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
            if (order.note != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.note_label),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = order.note,
                    style = MaterialTheme.typography.bodyLarge
                )
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

    val context = LocalContext.current

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
                        text = StatusUtils.getStatusDisplayName(
                            item.status ?: "",
                            LocalContext.current
                        ),
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
                        text = "${item.amount} x ${
                            stringResource(
                                R.string.price_label,
                                item.oneItemPrice ?: 0.0
                            )
                        }",
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
        val changedText = stringResource(R.string.label_status_changed)
        val errorText = stringResource(R.string.error_status_change)

        ChangeStatusDialog(
            onDismiss = { showChangeStatusDialog.value = false },
            onSubmit = { employeeId, status ->
                viewModel.viewModelScope.launch {
                    val success = viewModel.changeOrderStatus(
                        orderId = orderId,
                        menuItemId = item.menuItemId ?: 0,
                        employeeId = employeeId,
                        status = status,
                        visitId = visitId
                    )

                    if(success){
                        Toast
                            .makeText(
                                context,
                                changedText,
                                Toast.LENGTH_SHORT
                            )
                            .show()
                        showChangeStatusDialog.value = false
                    }else{
                        Toast
                            .makeText(
                                context,
                                errorText,
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }

                }
            },
            viewModel = viewModel,
            status = item.status ?: ""
        )
    }
}


@Composable
fun ClientInfoSection(visitDetails: VisitDetailsUIState, navHostController: NavHostController) {
    val visit = visitDetails.visit
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = visitDetails.clientName,
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
        }


        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                if (visit.takeaway == true) {
                    Text(
                        text = stringResource(R.string.takeaway_label),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Box(
                        modifier = Modifier.clickable {
                            navHostController.navigate(
                                RestaurantRoutes.Tables(
                                    restaurantId = visit.restaurant!!.restaurantId
                                )
                            )
                        }
                    ) {
                        Row {
                            Text(
                                text = stringResource(id = R.string.table_label),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline
                                ),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = " ${visit.tableId}",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline
                                )
                            )
                        }

                    }
                }
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
                val reservationFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                val reservationDate = LocalDate.parse(visit.reservationDate, reservationFormatter)

                val isReservationToday = reservationDate.isEqual(LocalDate.now())

                if (!isReservationToday) {
                    Text(
                        text = stringResource(R.string.reservation_date_label) + " ${
                            reservationDate.format(
                                DateTimeFormatter.ofPattern("dd.MM.yyyy")
                            )
                        }",
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
                stringResource(
                    R.string.total_cost_label,
                    "%.2f".format(visit.orders?.sumOf { it.cost ?: 0.0 } ?: 0.0))
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ParticipantsList(participants: List<String>) {
    Column {
        Text(
            text = stringResource(R.string.participants_label),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        participants.forEach { participantName ->
            ParticipantCard(participantName = participantName)
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
fun ChangeTableDialog(
    onDismiss: () -> Unit,
    onSubmit: (Int) -> Unit,
    viewModel: EmployeeOrderViewModel
) {
    val tables = viewModel.tables.collectAsState()
    val expandedTables = remember { mutableStateOf(false) }

    val tablesList = tables.value
    val tableOptions = tablesList.associate { table ->
        "${stringResource(R.string.label_table_number)}${table.tableId} | ${stringResource(R.string.label_seats)}: ${table.capacity}" to table.tableId
    }
    val optionsList = tableOptions.keys.toList()

    var selectedTableId by remember {
        mutableStateOf(tablesList.firstOrNull()?.tableId ?: 0)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.label_change_table)) },
        text = {
            Column {
                ComboBox(
                    expanded = expandedTables,
                    value = tablesList.firstOrNull { it.tableId == selectedTableId }?.let {
                        "${stringResource(R.string.label_table_number)}${it.tableId} | ${stringResource(R.string.label_seats)}: ${it.capacity}"
                    } ?: "",
                    onValueChange = { selectedString ->
                        selectedTableId = tableOptions[selectedString] ?: selectedTableId
                    },
                    options = optionsList,
                    label = stringResource(R.string.label_select_table)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(selectedTableId) }
            ) {
                Text(text = stringResource(R.string.submit))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}


@Composable
fun ChangeStatusDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit,
    viewModel: EmployeeOrderViewModel,
    status: String
) {
    val context = LocalContext.current

    val allEmployees by viewModel.employees.collectAsState()


    val filteredEmployees by remember {
        mutableStateOf(
            allEmployees.filterNot { employee ->
        employee.isBackdoorEmployee }
        )
    }

    val employeeNames = filteredEmployees.map { "${it.firstName} ${it.lastName}" }

    val employeeIdMap = filteredEmployees.associateBy(
        keySelector = { "${it.firstName} ${it.lastName}" },
        valueTransform = { it.employeeId }
    )

    var selectedEmployeeName by remember { mutableStateOf(UserService.UserObject.firstName + " " + UserService.UserObject.lastName) }
    val expandedEmployee = remember { mutableStateOf(false) }

    val statusOptions = StatusUtils.statusOptions
    val statusDisplayNames = statusOptions.map { context.getString(it.displayNameResId) }

    val statusMap =
        statusOptions.associateBy({ it.statusString }, { context.getString(it.displayNameResId) })
    val reverseStatusMap =
        statusOptions.associateBy({ context.getString(it.displayNameResId) }, { it.statusString })

    var selectedStatusDisplayName by remember { mutableStateOf(statusMap[status] ?: status) }
    val expandedStatus = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchEmployees()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.change_order_item_status)) },
        text = {
            Column {
                ComboBox(
                    expanded = expandedEmployee,
                    value = selectedEmployeeName,
                    onValueChange = { selectedEmployeeName = it },
                    options = employeeNames,
                    label = stringResource(R.string.select_employee)
                )
                Spacer(modifier = Modifier.height(8.dp))
                ComboBox(
                    expanded = expandedStatus,
                    value = selectedStatusDisplayName,
                    onValueChange = { selectedStatusDisplayName = it },
                    options = statusDisplayNames,
                    label = stringResource(R.string.select_status)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val employeeId = employeeIdMap[selectedEmployeeName]
                    val selectedStatusString = reverseStatusMap[selectedStatusDisplayName]
                    if (employeeId != null && selectedStatusString != null) {
                        onSubmit(employeeId, selectedStatusString)
                    }
                }
            ) {
                Text(text = stringResource(R.string.submit))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}
