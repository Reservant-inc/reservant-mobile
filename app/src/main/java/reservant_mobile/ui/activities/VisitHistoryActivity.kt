import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.IconWithHeader

@Composable
fun VisitHistoryActivity(navController: NavHostController) {
    val viewModel: VisitHistoryViewModel = viewModel()
    val visitHistoryFlow = viewModel.visitHistoryFlow
    val lazyVisits = visitHistoryFlow?.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        viewModel.loadVisitHistory()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        IconWithHeader(
            icon = Icons.Rounded.History,
            text = stringResource(R.string.visit_history_title), // Use resource instead of hard-coded
            showBackButton = true,
            onReturnClick = { navController.popBackStack() }
        )

        // If visits is null => not loaded yet (or error)
        if (lazyVisits == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // If loaded, we check itemCount
            if (lazyVisits.itemCount == 0) {
                // No visits => show a "no visits" message
                Text(
                    text = stringResource(R.string.no_visits_found),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                // We have visits => display them
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.padding(16.dp)
                ) {
                    items(lazyVisits.itemCount) { index ->
                        val visit = lazyVisits[index]
                        if (visit != null) {
                            VisitCard(
                                visit = visit,
                                onClick = {
                                    // e.g. navigate to detail if you have a detail screen
                                    // navController.navigate("visit_detail/${visit.visitId}")
                                }
                            )
                        }
                    }
                    // Optionally handle paging states
                }
            }
        }
    }
}


@Composable
fun VisitCard(
    visit: VisitDTO,
    onClick: () -> Unit
) {
    val restaurantName = visit.restaurant?.name ?: stringResource(R.string.unknown_restaurant)

    // Use the formatToDateTime util:
    // This returns a formatted string, e.g. "2025-01-21 06:50", or "" on parse error.
    val dateTimeString = formatToDateTime(visit.date.orEmpty(), "yyyy-MM-dd HH:mm")
    val finalDateString = if (dateTimeString.isEmpty()) "N/A" else dateTimeString

    // Localize "Mode"
    val modeLabel = if (visit.takeaway == true) {
        stringResource(R.string.takeaway_label)
    } else {
        stringResource(R.string.on_site_label)
    }

    // Localize "Accepted"
    val acceptedLabel = if (visit.isAccepted == true) {
        stringResource(R.string.accepted_yes)
    } else {
        stringResource(R.string.accepted_no)
    }

    // Calculate total guests
    val totalGuests = (visit.numberOfGuests ?: 0) + (visit.participants?.size ?: 0) + 1

    androidx.compose.material3.Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxSize()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.restaurant_colon, restaurantName),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.visit_date_colon, finalDateString)
            )
            Text(
                text = stringResource(R.string.total_guests_colon, totalGuests)
            )
            Text(
                text = stringResource(R.string.mode_colon, modeLabel)
            )
            Text(
                text = stringResource(R.string.accepted_colon, acceptedLabel)
            )
        }
    }
}