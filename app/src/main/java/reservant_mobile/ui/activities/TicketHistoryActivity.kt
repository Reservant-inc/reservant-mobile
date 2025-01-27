package reservant_mobile.ui.activities

import TicketViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.ReportDTO
import reservant_mobile.data.models.dtos.TicketDTO
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.ComboBox
import reservant_mobile.ui.components.FloatingTabSwitch
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.navigation.UserRoutes
import reservant_mobile.ui.viewmodels.ReviewsViewModel

@Composable
fun TicketHistoryActivity(navController: NavController, restaurantId: Int) {
    val reportViewModel: TicketViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TicketViewModel() as T
            }
        }
    )
    val statuses = ReportDTO.ReportStatus.values().toList()
    val context = LocalContext.current
    val statusesMap = remember {
        statuses.associateBy { context.getString(it.stringId) }
    }


    val expanded = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ) {
        IconWithHeader(
            icon = Icons.Rounded.History,
            text = stringResource(id = R.string.label_ticket_history),
            showBackButton = true,
            onReturnClick = {
                navController.popBackStack()
            }
        )

        ComboBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            expanded = expanded,
            value = stringResource(id = reportViewModel.selectedStatus.stringId),
            onValueChange = { newValue ->
                val foundStatus = statusesMap[newValue]
                if (foundStatus != null) {
                    reportViewModel.selectedStatus = foundStatus
                }
            },
            options = statuses.map { stringResource(id = it.stringId) },
            label = stringResource(R.string.label_select_report_status)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            ReportsTabContent(
                status = reportViewModel.selectedStatus,
                viewModel = reportViewModel,
                navController = navController
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { navController.navigate(UserRoutes.Ticket(restaurantId = restaurantId )) }
        ) {
            Icon(imageVector = Icons.Rounded.Add, contentDescription = "add")
        }
    }
}


@Composable
fun ReportsTabContent(
    status: ReportDTO.ReportStatus,
    viewModel: TicketViewModel,
    navController: NavController
) {
    LaunchedEffect(status) {
        viewModel.loadReports(status)
    }

    val currentFlow = viewModel.reportsFlow.collectAsState().value
    val lazyReports = currentFlow?.collectAsLazyPagingItems()

    // Display
    ReportsList(
        lazyReports = lazyReports,
        onRefresh = { viewModel.loadReports(status) },
        navController = navController
    )
}

@Composable
fun ReportsList(
    lazyReports: LazyPagingItems<ReportDTO>?,
    onRefresh: () -> Unit = {},
    navController: NavController
) {
    if (lazyReports == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val loadState = lazyReports.loadState.refresh
    when {
        loadState is LoadState.Loading -> {
            // Show spinner
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        loadState is LoadState.Error -> {
            // Show error
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Failed to load reports. Tap to retry.")
            }
        }

        else -> {
            // Not loading => show the list
            if (lazyReports.itemCount == 0) {
                Text("No reports found.")
            } else {
                LazyColumn {
                    items(lazyReports.itemCount) { index ->
                        val report = lazyReports[index]
                        if (report != null) {
                            ReportCard(report) { navController.navigate(UserRoutes.ReportDetails(report)) }
                        }
                    }
                }
            }
        }

    }
}


@Composable
fun ReportCard(report: ReportDTO, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_report),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.report_title, report.reportId ?: ""),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))

                val formattedDate = report.reportDate?.let { formatToDateTime(it, "dd MMMM yyyy | HH:mm") } ?: ""
                Text(
                    text = stringResource(R.string.date, formattedDate),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                if (!report.description.isNullOrEmpty()) {
                    Text(
                        text = stringResource(R.string.description, report.description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                report.visit?.let { visit ->
                    Text(
                        text = stringResource(R.string.visit_info, visit.visitId ?: 0, visit.restaurant?.name ?: "-"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }


                val statusLabel = report.reportStatus?.let {
                    stringResource(it.stringId)
                } ?: ""

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.status, statusLabel),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
