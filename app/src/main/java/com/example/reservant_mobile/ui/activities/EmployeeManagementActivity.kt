import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reservant_mobile.R
import com.example.reservant_mobile.ui.components.AddEmployeeDialog
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
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AddEmployeeDialog(onDismiss = { showDialog = false }, vm = employeeViewModel)
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
            items(employeeViewModel.employees) { employee ->
                EmployeeCard(employee = employee)
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
            MyFloatingActionButton(onClick = { showDialog = true })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmployee() {
    EmployeeManagementActivity(1)
}