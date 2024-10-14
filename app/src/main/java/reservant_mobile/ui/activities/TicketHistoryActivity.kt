package reservant_mobile.ui.activities

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.TicketDTO
import reservant_mobile.ui.components.FloatingTabSwitch
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.navigation.UserRoutes

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
        ),
        TicketDTO(
            "John Doe’s - Restaurant",
            "Report: Brak pozycji w menu",
            "Category: Technical",
            "2023-08-12"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconWithHeader(
            icon = Icons.Rounded.History,
            text = stringResource(id = R.string.label_ticket_history),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Zakładki FloatingTabSwitch
                FloatingTabSwitch(
                    pages = listOf(
                        stringResource(id = R.string.label_unresolved) to {
                            TicketList(tickets)
                        },
                        stringResource(id = R.string.label_to_be_reviewed) to {
                            TicketList(tickets)
                        },
                        stringResource(id = R.string.label_in_progress) to {
                            TicketList(tickets)
                        }
                    )
                )
            }
        }
    }

    // FloatingActionButton na dolnym prawym rogu ekranu
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { navController.navigate(UserRoutes.Ticket) }
        ) {
            Icon(imageVector = Icons.Rounded.Add, contentDescription = "add")
        }
    }
}

@Composable
fun TicketList(tickets: List<TicketDTO>) {
    Column() {
        Spacer(modifier = Modifier.height(90.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(tickets) { ticket ->
                TicketCard(ticket = ticket, onClick = { /* Handle item click */ })
            }
        }
    }
}

@Composable
fun TicketCard(ticket: TicketDTO, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = ticket.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = ticket.report, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = ticket.category,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = ticket.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
