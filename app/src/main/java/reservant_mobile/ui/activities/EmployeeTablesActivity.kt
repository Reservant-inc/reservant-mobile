package reservant_mobile.ui.activities

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TableChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.TableDTO
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.viewmodels.TablesViewModel


@Composable
fun EmployeeTablesActivity(
    restaurantId: Int,
    onReturnClick: () -> Unit
) {
    val tablesViewModel = viewModel<TablesViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                TablesViewModel(restaurantId) as T
        }
    )

    val tables by tablesViewModel.tables.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        IconWithHeader(
            icon = Icons.Rounded.TableChart,
            text = stringResource(id = R.string.restaurant_tables_header),
            showBackButton = true,
            onReturnClick = onReturnClick
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
                TableCard(table = table)
            }
        }
    }
}

@Composable
fun TableCard(table: TableDTO) {
    Card(
        modifier = Modifier.padding(4.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (/**table.status**/"Available") { //TODO
                "Available" -> Color(0xFFDFFFD6) // Light green for available
                "Occupied" -> Color(0xFFFFD6D6) // Light red for occupied
                else -> Color.LightGray // Default color for other statuses
            }
        )
    ) {
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
                    id = if (/**table.status**/"Available" == "Available") R.string.available_label else R.string.occupied_label //TODO
                ),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
