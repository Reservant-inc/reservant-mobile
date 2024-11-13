package reservant_mobile.ui.activities

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.ui.components.ButtonComponent
import reservant_mobile.ui.components.FormFileInput
import reservant_mobile.ui.components.FormInput
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.MyDatePickerDialog
import reservant_mobile.ui.components.MyTimePickerDialog
import reservant_mobile.ui.viewmodels.EventViewModel
import java.time.LocalDate

@Composable
fun AddEventActivity(navController: NavHostController) {
    val addEventViewModel: EventViewModel = viewModel()

    val restaurantsFlow by addEventViewModel.restaurantsFlow.collectAsState()
    val restaurants = restaurantsFlow?.collectAsLazyPagingItems()

    val searchQuery by addEventViewModel.searchQuery.collectAsState()

    val context = LocalContext.current

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconWithHeader(
            icon = Icons.Rounded.CalendarMonth,
            text = stringResource(id = R.string.label_add_event),
            showBackButton = true,
            onReturnClick = { navController.popBackStack() }
        )

        FormInput(
            inputText = addEventViewModel.eventName,
            onValueChange = { addEventViewModel.eventName = it },
            label = stringResource(id = R.string.label_event_name),
            isError = addEventViewModel.eventName.isBlank() && addEventViewModel.formSent,
            errorText = stringResource(id = R.string.error_field_required),
            modifier = Modifier.fillMaxWidth()
        )

        FormInput(
            inputText = addEventViewModel.description,
            onValueChange = { addEventViewModel.description = it },
            label = stringResource(id = R.string.label_event_description),
            isError = addEventViewModel.description.isBlank() && addEventViewModel.formSent,
            errorText = stringResource(id = R.string.error_field_required),
            modifier = Modifier.fillMaxWidth()
        )

        FormFileInput(
            label = stringResource(id = R.string.label_event_photo),
            onFilePicked = { file ->
                addEventViewModel.photo = file.toString()
            },
            context = context,
            isError = addEventViewModel.photo.isBlank(),
            formSent = addEventViewModel.formSent
        )

        Column {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(0.45f)) {
                    MyDatePickerDialog(
                        label = {
                            Text(stringResource(R.string.label_start_date))
                        },
                        onDateChange = {
                            addEventViewModel.eventDate = it
                        },
                        allowFutureDates = true,
                        startDate = LocalDate.now().toString()
                    )
                    if (addEventViewModel.eventDate.isBlank() && addEventViewModel.formSent) {
                        Text(
                            text = stringResource(id = R.string.error_field_required),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(0.55f).align(Alignment.CenterVertically)) {
                    MyTimePickerDialog(
                        onTimeSelected = { selectedTime ->
                            addEventViewModel.eventTime = selectedTime
                        },
                        modifier = Modifier
                            .scale(0.85f)
                    )
                }
            }
        }

        Column {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(0.45f)) {
                    MyDatePickerDialog(
                        label = {
                            Text(stringResource(R.string.label_event_join_until))
                        },
                        onDateChange = {
                            addEventViewModel.mustJoinDate = it
                        },
                        allowFutureDates = true,
                        startDate = LocalDate.now().toString()
                    )
                    if (addEventViewModel.mustJoinDate.isBlank() && addEventViewModel.formSent) {
                        Text(
                            text = stringResource(id = R.string.error_field_required),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(0.55f).align(Alignment.CenterVertically)) {
                    MyTimePickerDialog(
                        onTimeSelected = { selectedTime ->
                            addEventViewModel.mustJoinTime = selectedTime
                        },
                        modifier = Modifier
                            .scale(0.85f)
                    )
                }
            }
        }

        FormInput(
            inputText = addEventViewModel.maxPeople,
            onValueChange = { addEventViewModel.maxPeople = it },
            label = stringResource(id = R.string.label_event_max_people),
            isError = (addEventViewModel.maxPeople.isBlank() || addEventViewModel.maxPeople.toIntOrNull() == null) && addEventViewModel.formSent,
            errorText = stringResource(id = R.string.error_invalid_number),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
        )

        Text(
            text = stringResource(id = R.string.label_available_restaurants),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { addEventViewModel.searchQuery.value = it },
            label = { Text(stringResource(id = R.string.label_search_restaurants)) },
            trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        if (restaurants != null && restaurants.itemCount != 0) {
            LazyColumn(
                modifier = Modifier
                    .height(200.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            ) {
                items(restaurants.itemCount) { index ->
                    val restaurant = restaurants[index]
                    if (restaurant != null) {
                        val isSelected = restaurant.restaurantId == addEventViewModel.selectedRestaurant?.restaurantId
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                                .clickable {
                                    if (isSelected) {
                                        addEventViewModel.selectedRestaurant = null
                                    } else {
                                        addEventViewModel.selectedRestaurant = restaurant
                                    }
                                },
                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp),
                            tonalElevation = if (isSelected) 4.dp else 0.dp
                        ) {
                            Text(
                                text = restaurant.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 8.dp)
                            )
                        }
                    }
                }
            }

        } else if(restaurants != null && restaurants.itemCount == 0){
            Text("There are no restaurants to select.")
        } else {
            CircularProgressIndicator()
        }

        addEventViewModel.selectedRestaurant?.let {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.label_selected_restaurant) + " " + it.name,
                    style = MaterialTheme.typography.bodyMedium
                )
                IconButton(
                    onClick = { addEventViewModel.selectedRestaurant = null },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(id = R.string.label_delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        ButtonComponent(
            onClick = {
                addEventViewModel.formSent = true
                addEventViewModel.viewModelScope.launch {
                    addEventViewModel.addEvent(context)
                }
                if (addEventViewModel.result.isError) {
                    if (addEventViewModel.isFormValid()) {
                        Toast.makeText(context, R.string.error_add_event_failed, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, R.string.error_fill_all_fields, Toast.LENGTH_LONG).show()
                    }
                } else {
                    navController.popBackStack()
                }
            },
            label = stringResource(id = R.string.label_add_event),
            modifier = Modifier.fillMaxWidth()
        )
    }
}