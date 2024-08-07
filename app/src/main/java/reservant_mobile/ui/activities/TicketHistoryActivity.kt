package reservant_mobile.ui.activities

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.TicketDTO
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.SearchBarWithFilter

@Composable
fun TicketHistoryActivity() {
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

    Column(
        modifier = Modifier
            .padding(16.dp)
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
