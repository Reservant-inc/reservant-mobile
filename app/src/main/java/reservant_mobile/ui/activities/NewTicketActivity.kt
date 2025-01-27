package reservant_mobile.ui.activities

import TicketViewModel
import VisitCard
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.FormInput
import reservant_mobile.ui.components.ComboBox
import reservant_mobile.ui.components.ButtonComponent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.reservant_mobile.R
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.services.UserService
import reservant_mobile.ui.components.FloatingTabSwitch
import reservant_mobile.ui.components.UserCard
import reservant_mobile.ui.viewmodels.RestaurantDetailViewModel

@Composable
fun NewTicketActivity(
    navController: NavController,
    restaurantId: Int
) {
    val reportsViewModel = viewModel<TicketViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                TicketViewModel() as T
        }
    )
    // Determine user role
    val isEmployee = Roles.RESTAURANT_EMPLOYEE in UserService.UserObject.roles

    val tabPages = buildList<Pair<String, @Composable () -> Unit>> {
        add(stringResource(R.string.report_bug) to { ReportBugTab(reportsViewModel) })
        if (!isEmployee) {
            add(stringResource(R.string.report_employee) to { ReportEmployeeTab(reportsViewModel) })
        }
        if (isEmployee) {
            add(stringResource(R.string.report_customer) to { ReportCustomerTab(reportsViewModel, restaurantId) })
        }
        if (!isEmployee) {
            add(stringResource(R.string.report_lost_item) to { ReportLostItemTab(reportsViewModel) })
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        IconWithHeader(
            icon = Icons.Rounded.Add,
            text = stringResource(R.string.label_ticket),
            showBackButton = true,
            onReturnClick = { navController.popBackStack() }
        )

        FloatingTabSwitch(
            pages = tabPages
        )
    }
}

@Composable
fun ReportEmployeeTab(reportsViewModel: TicketViewModel) {
    var formSent by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(Modifier.height(72.dp))
        Text(text = stringResource(R.string.report_employee_title), style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        // Text field for the issue description
        FormInput(
            inputText = reportsViewModel.description,
            onValueChange = { reportsViewModel.description = it },
            label = stringResource(R.string.description_label),
            isError = reportsViewModel.isDescriptionError(),
            errorText = stringResource(R.string.description_error),
            formSent = formSent
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Button to pick a visit
        ButtonComponent(
            label = stringResource(R.string.select_visit_button),
            onClick = {
                reportsViewModel.loadVisitsForUserOrRestaurant()
                reportsViewModel.isPickVisitDialogOpen = true
            }
        )

        ErrorText(
            formSent = formSent,
            isError = reportsViewModel.isVisitError(),
            textError = stringResource(R.string.visit_error)
        )

        reportsViewModel.selectedVisit?.let { chosenVisit ->
            // Display chosen visit information
            Text(stringResource(R.string.chosen_visit, chosenVisit.visitId ?: 0))

            // Extract the first available employeeId from orders
            val assignedEmployee = chosenVisit.orders?.firstNotNullOfOrNull { it.assignedEmployee }

            ErrorText(
                formSent = formSent,
                isError = reportsViewModel.isEmplyeeError(),
                textError = stringResource(R.string.employee_error)
            )

            if (assignedEmployee != null) {
                reportsViewModel.selectedEmployee = assignedEmployee // Assign in ViewModel
                Text(stringResource(R.string.assigned_employee, assignedEmployee.firstName ?: "", assignedEmployee.lastName ?: ""))
            } else {
                Text(stringResource(R.string.no_employee_assigned))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val noEmployeeErrorMessage = stringResource(R.string.no_employee_report_error)
        ButtonComponent(
            label = stringResource(R.string.send_button),
            onClick = {
                formSent = true
                if (reportsViewModel.selectedEmployee == null) {
                    reportsViewModel.errorMessage = noEmployeeErrorMessage
                } else {
                    reportsViewModel.sendReportEmployee()
                }
            }
        )
    }

    // Show dialog for selecting a visit
    if (reportsViewModel.isPickVisitDialogOpen) {
        VisitSelectionPopup(
            reportsViewModel = reportsViewModel,
            onDismiss = { reportsViewModel.isPickVisitDialogOpen = false }
        )
    }

    // Show success dialog
    if (reportsViewModel.showSuccessDialog) {
        formSent = false
        AlertDialog(
            onDismissRequest = { reportsViewModel.showSuccessDialog = false },
            title = { Text(stringResource(R.string.report_sent_title)) },
            text = { Text(stringResource(R.string.report_sent_message)) },
            confirmButton = {
                ButtonComponent(
                    label = stringResource(R.string.ok_button),
                    onClick = { reportsViewModel.showSuccessDialog = false }
                )
            }
        )
    }

    // Show error dialog
    reportsViewModel.errorMessage?.let { err ->
        AlertDialog(
            onDismissRequest = { reportsViewModel.errorMessage = null },
            title = { Text(stringResource(R.string.error_title)) },
            text = { Text(err) },
            confirmButton = {
                ButtonComponent(
                    label = stringResource(R.string.ok_button),
                    onClick = { reportsViewModel.errorMessage = null }
                )
            }
        )
    }
}


@Composable
fun VisitSelectionPopup(
    reportsViewModel: TicketViewModel,
    onDismiss: () -> Unit
) {
    // Observe the paging flow from ViewModel:
    val visitsFlow = reportsViewModel.visitPagingFlow.collectAsState().value
    // If not null, collect as lazy items:
    val lazyVisits = visitsFlow?.collectAsLazyPagingItems()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_visit_title)) },
        text = {
            // If null => not loaded or an error
            if (lazyVisits == null) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.loading_visits))
                }
            } else {
                // We can handle load states:
                val loadState = lazyVisits.loadState.refresh
                when {
                    loadState is LoadState.Loading -> {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    loadState is LoadState.Error -> {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.error_loading_visits))
                        }
                    }
                    else -> {
                        if (lazyVisits.itemCount == 0) {
                            Text(stringResource(R.string.no_visits_found))
                        } else {
                            // Show them in a LazyColumn
                            LazyColumn {
                                items(lazyVisits.itemCount) { index ->
                                    val visit = lazyVisits[index]
                                    if (visit != null) {
                                        VisitCard(
                                            visit = visit,
                                            onClick = {
                                                // Use the selected visit
                                                reportsViewModel.selectedVisit = visit
                                                reportsViewModel.selectedParticipant = null
                                                onDismiss()
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            ButtonComponent(label = stringResource(R.string.close_button), onClick = onDismiss)
        }
    )
}


@Composable
fun ParticipantSelectionPopup(
    reportsViewModel: TicketViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_participant_title)) },
        text = {
            if (reportsViewModel.participantList.isEmpty()) {
                Text(stringResource(R.string.no_participants_found))
            } else {
                // Could be a LazyColumn if big list
                Column {
                    reportsViewModel.participantList.forEach { user ->
                        UserCard(
                            firstName = user.firstName,
                            lastName = user.lastName,
                            getImage = { null },
                            onClick = {
                                reportsViewModel.selectedParticipant = user
                                onDismiss()
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            ButtonComponent(label = stringResource(R.string.close_button), onClick = onDismiss)
        }
    )
}


@Composable
fun ReportCustomerTab(reportsViewModel: TicketViewModel, restaurantId: Int) {
    var formSent by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(Modifier.height(72.dp))
        Text(text = stringResource(R.string.report_customer_title), style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        // A text field for the description
        FormInput(
            inputText = reportsViewModel.description,
            onValueChange = { reportsViewModel.description = it },
            label = stringResource(R.string.description_label),
            isError = reportsViewModel.isDescriptionError(),
            errorText = stringResource(R.string.description_error),
            formSent = formSent
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Button to pick a visit
        ButtonComponent(
            label = stringResource(R.string.select_visit_button),
            onClick = {
                reportsViewModel.loadVisitsForUserOrRestaurant(restaurantId = restaurantId)
                reportsViewModel.isPickVisitDialogOpen = true
            }
        )
        ErrorText(
            formSent = formSent,
            isError = reportsViewModel.isVisitError(),
            textError = stringResource(R.string.visit_error)
        )

        // Once a visit is chosen, show more steps
        reportsViewModel.selectedVisit?.let { chosenVisit ->
            Text(stringResource(R.string.chosen_visit, chosenVisit.visitId ?: 0))

            Spacer(modifier = Modifier.height(8.dp))

            // Now pick the participant (the customer)
            ButtonComponent(
                label = stringResource(R.string.select_customer_button),
                onClick = {
                    reportsViewModel.loadParticipantsFromVisit(chosenVisit)
                    reportsViewModel.isPickParticipantDialogOpen = true
                }
            )

            ErrorText(
                formSent = formSent,
                isError = reportsViewModel.isParticipantsError(),
                textError = stringResource(R.string.participant_error)
            )

            // Show the currently selected user if any
            reportsViewModel.selectedParticipant?.let { user ->
                Text(stringResource(R.string.selected_customer, user.firstName, user.lastName))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Finally send the report
        ButtonComponent(
            label = stringResource(R.string.send_button),
            onClick = {
                formSent = true
                reportsViewModel.sendReportCustomer()
            }
        )
    }

    // Popups for picking the visit
    if (reportsViewModel.isPickVisitDialogOpen) {
        VisitSelectionPopup(
            reportsViewModel = reportsViewModel,
            onDismiss = { reportsViewModel.isPickVisitDialogOpen = false }
        )
    }
    // Popups for picking a participant
    if (reportsViewModel.isPickParticipantDialogOpen) {
        ParticipantSelectionPopup(
            reportsViewModel = reportsViewModel,
            onDismiss = { reportsViewModel.isPickParticipantDialogOpen = false }
        )
    }

    // If success
    if (reportsViewModel.showSuccessDialog) {
        formSent = false
        AlertDialog(
            onDismissRequest = { reportsViewModel.showSuccessDialog = false },
            title = { Text(stringResource(R.string.report_sent_title)) },
            text = { Text(stringResource(R.string.report_customer_success_message)) },
            confirmButton = {
                ButtonComponent(
                    label = stringResource(R.string.ok_button),
                    onClick = { reportsViewModel.showSuccessDialog = false }
                )
            }
        )
    }

    // If error
    reportsViewModel.errorMessage?.let { err ->
        AlertDialog(
            onDismissRequest = { reportsViewModel.errorMessage = null },
            title = { Text(stringResource(R.string.error_title)) },
            text = { Text(err) },
            confirmButton = {
                ButtonComponent(
                    label = stringResource(R.string.ok_button),
                    onClick = { reportsViewModel.errorMessage = null }
                )
            }
        )
    }
}

@Composable
fun ReportBugTab(reportsViewModel: TicketViewModel) {
    var formSent by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(Modifier.height(72.dp))
        Text(text = stringResource(R.string.report_bug_title), style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        FormInput(
            inputText = reportsViewModel.description,
            onValueChange = { reportsViewModel.description = it },
            label = stringResource(R.string.describe_bug_label),
            isError = reportsViewModel.isDescriptionError(),
            errorText = stringResource(R.string.description_error),
            formSent = formSent
        )

        Spacer(modifier = Modifier.height(16.dp))

        // No need to pick visit or participant, just send
        ButtonComponent(
            label = stringResource(R.string.send_button),
            onClick = {
                formSent = true
                reportsViewModel.sendReportBug()
            }
        )
    }

    // Show success
    if (reportsViewModel.showSuccessDialog) {
        formSent = false
        AlertDialog(
            onDismissRequest = { reportsViewModel.showSuccessDialog = false },
            title = { Text(stringResource(R.string.report_sent_title)) },
            text = { Text(stringResource(R.string.report_bug_success_message)) },
            confirmButton = {
                ButtonComponent(
                    label = stringResource(R.string.ok_button),
                    onClick = {
                        formSent = true
                        reportsViewModel.showSuccessDialog = false
                    }
                )
            }
        )
    }

    // Show error
    reportsViewModel.errorMessage?.let { err ->
        AlertDialog(
            onDismissRequest = { reportsViewModel.errorMessage = null },
            title = { Text(stringResource(R.string.error_title)) },
            text = { Text(err) },
            confirmButton = {
                ButtonComponent(
                    label = stringResource(R.string.ok_button),
                    onClick = { reportsViewModel.errorMessage = null }
                )
            }
        )
    }
}

@Composable
fun ReportLostItemTab(reportsViewModel: TicketViewModel) {
    var formSent by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(Modifier.height(72.dp))
        Text(text = stringResource(R.string.report_lost_item_title), style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        // A text field for the description
        FormInput(
            inputText = reportsViewModel.description,
            onValueChange = { reportsViewModel.description = it },
            label = stringResource(R.string.description_lost_item_label),
            isError = reportsViewModel.isDescriptionError(),
            errorText = stringResource(R.string.description_error),
            formSent = formSent
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Possibly pick a visit (where the item was lost)
        ButtonComponent(
            label = stringResource(R.string.select_visit_button),
            onClick = {
                reportsViewModel.loadVisitsForUserOrRestaurant()
                reportsViewModel.isPickVisitDialogOpen = true
            }
        )
        ErrorText(
            formSent = formSent,
            isError = reportsViewModel.isVisitError(),
            textError = stringResource(R.string.visit_error)
        )

        reportsViewModel.selectedVisit?.let { chosen ->
            Text(stringResource(R.string.chosen_visit, chosen.visitId ?: 0))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Send button
        ButtonComponent(
            label = stringResource(R.string.send_button),
            onClick = {
                formSent = true
                reportsViewModel.sendReportLostItem()
            }
        )
    }

    // Popup for picking a visit
    if (reportsViewModel.isPickVisitDialogOpen) {
        VisitSelectionPopup(
            reportsViewModel = reportsViewModel,
            onDismiss = { reportsViewModel.isPickVisitDialogOpen = false }
        )
    }

    // Show success
    if (reportsViewModel.showSuccessDialog) {
        formSent = false
        AlertDialog(
            onDismissRequest = { reportsViewModel.showSuccessDialog = false },
            title = { Text(stringResource(R.string.report_sent_title)) },
            text = { Text(stringResource(R.string.report_lost_item_success_message)) },
            confirmButton = {
                ButtonComponent(
                    label = stringResource(R.string.ok_button),
                    onClick = { reportsViewModel.showSuccessDialog = false }
                )
            }
        )
    }

    // Show error
    reportsViewModel.errorMessage?.let { err ->
        AlertDialog(
            onDismissRequest = { reportsViewModel.errorMessage = null },
            title = { Text(stringResource(R.string.error_title)) },
            text = { Text(err) },
            confirmButton = {
                ButtonComponent(
                    label = stringResource(R.string.ok_button),
                    onClick = { reportsViewModel.errorMessage = null }
                )
            }
        )
    }
}

@Composable
fun ErrorText(formSent: Boolean, isError: Boolean, textError: String) {
    if (formSent && isError) {
        Text(
            text = textError,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
