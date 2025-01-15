package reservant_mobile.ui.activities

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.TableDTO
import reservant_mobile.ui.components.ButtonComponent
import reservant_mobile.ui.components.FormInput
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.viewmodels.TablesViewModel


@Composable
fun EmployeeTablesActivity(
    restaurantId: Int,
    onReturnClick: () -> Unit,
    //isOwner: Boolean = false
) {
    val tablesViewModel = viewModel<TablesViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                TablesViewModel(restaurantId) as T
        }
    )

    val tables by tablesViewModel.tables.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {

        when {
            tablesViewModel.isAddSelected || tablesViewModel.isEditSelected -> {

                if (tablesViewModel.isEditSelected) {
                    tablesViewModel.numberOfPeople = tablesViewModel.selectedTable?.capacity
                }

                AlertDialog(
                    onDismissRequest = {
                        tablesViewModel.isAddSelected = false
                        tablesViewModel.isEditSelected = false
                        tablesViewModel.numberOfPeople = null
                    },
                    title = { Text(text = stringResource(id =
                        if (tablesViewModel.isAddSelected) R.string.label_add_table
                        else R.string.label_edit_table
                    )) },
                    text = {
                        Column {
                            
                            if (tablesViewModel.isEditSelected) {
                                val context = LocalContext.current
                                Button(onClick = {
                                    tablesViewModel.removeTable(tablesViewModel.selectedTable!!.tableId)

                                    tablesViewModel.viewModelScope.launch {
                                        tablesViewModel.updateTables()

                                    }

                                    Toast.makeText(context, "Table removed", Toast.LENGTH_SHORT).show()
                                    tablesViewModel.isEditSelected = false
                                    tablesViewModel.numberOfPeople = null

                                }, modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                                ) {
                                    Row {
                                        Icon(
                                            modifier = Modifier
                                                .align(Alignment.CenterVertically)
                                                .padding(4.dp),
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = stringResource(id = R.string.label_delete_table),
                                            tint = MaterialTheme.colorScheme.onPrimary
                                        )
                                        Text(
                                            modifier = Modifier
                                                .align(Alignment.CenterVertically)
                                                .padding(4.dp),
                                            text = stringResource(id = R.string.label_delete_table)
                                        )
                                    }
                                }
                            }
                            
                            FormInput(
                                label = stringResource(id = R.string.number_of_people_label).removeSuffix(":"),
                                inputText = (tablesViewModel.numberOfPeople ?: "").toString(),
                                onValueChange = {
                                    tablesViewModel.numberOfPeople = it.toIntOrNull()
                                },
                                formSent = true,
                                isError = tablesViewModel.numberOfPeople == null,
                                errorText = stringResource(id = R.string.error_invalid_number),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done,
                                    keyboardType = KeyboardType.Number
                                )
                            )
                        }
                    },
                    dismissButton = {
                        ButtonComponent(
                            onClick = {
                                tablesViewModel.isAddSelected = false
                                tablesViewModel.isEditSelected = false
                                tablesViewModel.numberOfPeople = null
                            },
                            label = stringResource(id = R.string.label_cancel)
                        )
                    },
                    confirmButton = {
                        val context = LocalContext.current
                        ButtonComponent(
                            onClick = {
                                if (tablesViewModel.numberOfPeople == null){
                                    return@ButtonComponent
                                }

                                if (tablesViewModel.isEditSelected){
                                    tablesViewModel.removeTable(tablesViewModel.selectedTable!!.tableId)
                                    tablesViewModel.addTable(tablesViewModel.selectedTable!!.tableId)
                                } else {
                                    tablesViewModel.addTable(tablesViewModel.generateTableId())
                                }

                                tablesViewModel.viewModelScope.launch {
                                    tablesViewModel.updateTables()
                                }

                                Toast.makeText(
                                    context,
                                    if (tablesViewModel.isEditSelected) "Table edited" else "Table added",
                                    Toast.LENGTH_SHORT
                                ).show()

                                tablesViewModel.isAddSelected = false
                                tablesViewModel.isEditSelected = false
                                tablesViewModel.numberOfPeople = null
                            },
                            label = stringResource(id = R.string.label_save),
                            isLoading = false
                        )
                    },
                )
            }
        }

        IconWithHeader(
            icon = Icons.Rounded.TableChart,
            text = stringResource(id = R.string.restaurant_tables_header),
            showBackButton = true,
            onReturnClick = onReturnClick,
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tables) { table ->
                TableCard(table = table, onClick = {
                    tablesViewModel.selectedTable = table
                    tablesViewModel.isEditSelected = true
                })
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = {
                tablesViewModel.isAddSelected = !tablesViewModel.isAddSelected
            }
        ){
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add table",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun TableCard(
    table: TableDTO,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when ( /**table.status**/ "Available") { //TODO
                "Available" -> Color(0xFFDFFFD6) // Light green for available
                "Occupied" -> Color(0xFFFFD6D6) // Light red for occupied
                else -> Color.LightGray // Default color for other statuses
            }
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize(),
                painter = if (/**table.status**/ "Available" == "Available") {
                    painterResource(id = R.drawable.table_available)
                } else {
                    painterResource(id = R.drawable.table_occupied)
                },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.25F
            )
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.table_label, table.tableId),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = R.string.seats_label, table.capacity),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(
                        id = if (
                        /**table.status**/
                            "Available" == "Available") R.string.available_label else R.string.occupied_label //TODO
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

