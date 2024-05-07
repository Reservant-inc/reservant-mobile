import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reservant_mobile.data.models.dtos.RestaurantEmployeeDTO
import com.example.reservant_mobile.ui.components.AddEmployeeDialog
import com.example.reservant_mobile.ui.components.EditEmployeeDialog
import com.example.reservant_mobile.ui.components.EmployeeCard
import com.example.reservant_mobile.ui.components.MyFloatingActionButton
import com.example.reservant_mobile.ui.viewmodels.EmployeeViewModel

@Composable
fun EmployeeManagementActivity(restaurantId: Int) {
    val employeeViewModel = viewModel<EmployeeViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                EmployeeViewModel(restaurantId) as T
        }
    )
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var employee by remember { mutableStateOf(RestaurantEmployeeDTO()) }

    if (showAddDialog) {
        AddEmployeeDialog(onDismiss = { showAddDialog = false }, vm = employeeViewModel)
    }

    if (showEditDialog) {
        EditEmployeeDialog(employee = employee, onDismiss = { showEditDialog = false }, vm = employeeViewModel)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            items(employeeViewModel.employees) { e ->
                EmployeeCard(
                    employee = e,
                    onEditClick = {showEditDialog = true; employee = e},
                    onDeleteClick = {employeeViewModel.deleteEmployee(e)}
                )
            }
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            MyFloatingActionButton(onClick = { showAddDialog = true })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmployee() {
    EmployeeManagementActivity(1)
}