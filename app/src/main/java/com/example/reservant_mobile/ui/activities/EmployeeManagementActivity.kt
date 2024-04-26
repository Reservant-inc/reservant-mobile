import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reservant_mobile.ui.components.EmployeeCard
import com.example.reservant_mobile.ui.viewmodels.EmployeeViewModel

@Composable
fun EmployeeManagementActivity() {
    val employeeViewModel = viewModel<EmployeeViewModel>()

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
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* Dodaj pracownika */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Employee")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmployee() {
    EmployeeManagementActivity()
}