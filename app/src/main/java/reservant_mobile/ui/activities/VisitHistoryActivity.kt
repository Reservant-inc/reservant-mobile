import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.services.UserService
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.FloatingTabSwitch
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.navigation.UserRoutes

@Composable
fun VisitHistoryActivity(
    navController: NavHostController
) {
    val viewModel: VisitHistoryViewModel = viewModel()

    // Load both flows once
    LaunchedEffect(Unit) {
        viewModel.loadUpcomingVisits()  // For future visits
        viewModel.loadVisitHistory()    // For past visits
    }

    // Convert them to lazyPagingItems
    val futureVisits = viewModel.futureVisitsFlow?.collectAsLazyPagingItems()
    val historyVisits = viewModel.visitHistoryFlow?.collectAsLazyPagingItems()

    Column(modifier = Modifier.fillMaxSize()) {
        IconWithHeader(
            icon = Icons.Rounded.History,
            text = stringResource(R.string.visit_history_title),
            showBackButton = true,
            onReturnClick = { navController.popBackStack() }
        )

        FloatingTabSwitch(
            pages = listOf(
                Pair(stringResource(R.string.future_visits_tab)) {
                    Column{
                        Spacer(modifier = Modifier.height(56.dp))
                        VisitsList(lazyVisits = futureVisits, navController = navController)
                    }
                },
                Pair(stringResource(R.string.past_visits_tab)) {
                    Column{
                        Spacer(modifier = Modifier.height(56.dp))
                        VisitsList(lazyVisits = historyVisits, navController = navController)
                    }
                }
            )
        )
    }
}


@Composable
fun VisitsList(
    lazyVisits: LazyPagingItems<VisitDTO>?,
    navController: NavHostController
) {
    if (lazyVisits == null) {
        // No flow yet => treat as loading
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        val loadState = lazyVisits.loadState.refresh
        when {
            // 1) Still loading the initial page
            loadState is LoadState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // 2) Initial load error
            loadState is LoadState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.error_loading_data),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // 3) Not loading => check item count
            else -> {
                if (lazyVisits.itemCount == 0) {
                    Text(
                        text = stringResource(R.string.no_visits_found),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        items(lazyVisits.itemCount) { index ->
                            val visit = lazyVisits[index]
                            if (visit != null) {
                                VisitCard(
                                    visit = visit,
                                    onClick = {
                                        navController.navigate(UserRoutes.VisitDetails(visitId = visit.visitId!!))
                                    }
                                )
                            }
                        }
                    }
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
    val dateTimeString = formatToDateTime(visit.date.orEmpty(), "yyyy-MM-dd HH:mm")
    val finalDateString = if (dateTimeString.isEmpty()) "N/A" else dateTimeString

    val modeLabel = if (visit.takeaway == true) {
        stringResource(R.string.takeaway_label)
    } else {
        stringResource(R.string.on_site_label)
    }

    val acceptedLabel = if (visit.isAccepted == true) {
        stringResource(R.string.accepted_yes)
    } else {
        stringResource(R.string.accepted_no)
    }

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
            Text(stringResource(R.string.visit_date_colon, finalDateString))
            Text(stringResource(R.string.total_guests_colon, totalGuests))
            Text(stringResource(R.string.mode_colon, modeLabel))
            Text(stringResource(R.string.accepted_colon, acceptedLabel))
        }
    }
}