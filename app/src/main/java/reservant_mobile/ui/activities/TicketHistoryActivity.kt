package reservant_mobile.ui.activities

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.TicketDTO
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.navigation.RestaurantRoutes

@Composable
fun TicketHistoryActivity(navController: NavController) {
    val tickets = listOf(
        TicketDTO(
            "John Doe’s - Restaurant",
            "Report: Brak pozycji w menu",
            "Category: Technical",
            "2023-08-12"
        ),
        TicketDTO(
            "John Doe’s - Restaurant",
            "Report: Zgubiony przedmiot",
            "Category: Technical",
            "2023-08-12"
        ),
        TicketDTO(
            "John Doe’s - Restaurant",
            "Report: Skarga na rezerwację",
            "Category: Complaint",
            "2023-08-12"
        ),
        TicketDTO(
            "John Doe’s - Restaurant",
            "Report: Brak pozycji w menu",
            "Category: Technical",
            "2023-08-12"
        )
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(RestaurantRoutes.Ticket) },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Ticket")
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .padding(paddingValues)
            ) {
                IconWithHeader(
                    icon = Icons.Rounded.History,
                    text = stringResource(id = R.string.label_ticket_history),
                )

                Text(
                    text = "Current reports",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                TabRow(
                    selectedTabIndex = 0,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    listOf(
                        "Nierozpatrzone",
                        "Do rozpatrzenia",
                        "W trakcie rozpatrywania"
                    ).forEachIndexed { index, title ->
                        Tab(
                            selected = index == 0,
                            onClick = { /* TODO: Handle tab click */ },
                            text = { Text(title, fontSize = 12.sp) }
                        )
                    }
                }
                LazyColumn {
                    items(tickets) { ticket ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { /* TODO: Handle item click */ }
                        ) {
                            Text(text = ticket.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = ticket.report)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = ticket.category)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = ticket.date, fontWeight = FontWeight.Light, fontSize = 14.sp)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }
        }
    )
}
