package reservant_mobile.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
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
            text = "Restaurant Tables",
            showBackButton = true,
            onReturnClick = onReturnClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (/**table.status**/"Occupied") {
                "Available" -> Color(0xFFDFFFD6) // Light green for available
                "Occupied" -> Color(0xFFFFD6D6) // Light red for occupied
                else -> Color.LightGray // Default color for other statuses
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Table ID: ${table.tableId}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Seats: ${table.capacity}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}