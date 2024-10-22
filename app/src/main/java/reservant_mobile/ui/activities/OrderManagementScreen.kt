package reservant_mobile.ui.activities

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.LazyPagingItems
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.VisitDTO
import reservant_mobile.data.utils.formatDateTime
import reservant_mobile.ui.components.FloatingTabSwitch
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.navigation.RestaurantRoutes
import reservant_mobile.ui.viewmodels.EmployeeOrderViewModel

@Composable
fun OrderManagementScreen(
    onReturnClick: () -> Unit,
    restaurantId: Int
) {
    val viewModel = viewModel<EmployeeOrderViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EmployeeOrderViewModel(restaurantId = restaurantId) as T
            }
        }
    )

    val innerNavController = rememberNavController()

    NavHost(
        navController = innerNavController,
        startDestination = RestaurantRoutes.ManageOrders(restaurantId = restaurantId),
    ) {
        composable<RestaurantRoutes.ManageOrders> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                IconWithHeader(
                    icon = Icons.Outlined.Book,
                    text = stringResource(R.string.orders_management),
                    showBackButton = true,
                    onReturnClick = onReturnClick
                )

                FloatingTabSwitch(
                    pages = listOf(
                        stringResource(R.string.current_orders) to {
                            val currentVisits = viewModel.currentVisits.collectAsLazyPagingItems()
                            Column {
                                Spacer(modifier = Modifier.height(90.dp))
                                when (currentVisits.loadState.refresh) {
                                    is LoadState.Loading -> {
                                        Text(
                                            text = stringResource(R.string.loading_current_visits),
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }

                                    is LoadState.Error -> {
                                        Text(
                                            text = stringResource(R.string.error_loading_visits),
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }

                                    else -> {
                                        if (currentVisits.itemCount == 0) {
                                            Text(
                                                text = stringResource(R.string.no_current_visits),
                                                modifier = Modifier.padding(16.dp)
                                            )
                                        } else {
                                            OrderList(
                                                visits = currentVisits,
                                                homeNavController = innerNavController,
                                                viewModel = viewModel
                                            )
                                        }
                                    }
                                }
                            }

                        },
                        stringResource(R.string.past_orders) to {
                            val pastVisits = viewModel.pastVisits.collectAsLazyPagingItems()
                            Column {
                                Spacer(modifier = Modifier.height(90.dp))
                                when (pastVisits.loadState.refresh) {
                                    is LoadState.Loading -> {
                                        Text(
                                            text = stringResource(R.string.loading_past_visits),
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }

                                    is LoadState.Error -> {
                                        Text(
                                            text = stringResource(R.string.error_loading_visits),
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }

                                    else -> {
                                        if (pastVisits.itemCount == 0) {
                                            Text(
                                                text = stringResource(R.string.no_past_visits),
                                                modifier = Modifier.padding(16.dp)
                                            )
                                        } else {
                                            OrderList(
                                                visits = pastVisits,
                                                homeNavController = innerNavController,
                                                viewModel = viewModel
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )
                )
            }
        }
        composable<RestaurantRoutes.OrderDetail> {
            OrderDetailsScreen(
                onReturnClick = { innerNavController.popBackStack() },
                visitId = it.toRoute<RestaurantRoutes.OrderDetail>().visitId,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun OrderList(visits: LazyPagingItems<VisitDTO>?, homeNavController: NavHostController, viewModel: EmployeeOrderViewModel) {
    if (visits == null || visits.itemCount == 0) {
        Text(text = stringResource(R.string.no_visits_available))
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(visits.itemCount) { index ->
            val visit = visits[index]
            if (visit != null) {
                viewModel.cacheVisit(visit)
                VisitCard(visit = visit, homeNavController = homeNavController)
            } else {
                Text(text = stringResource(R.string.loading_visit))
            }
        }
    }

}


@Composable
fun VisitCard(visit: VisitDTO, homeNavController: NavHostController) {
    val formattedDate = visit.date?.let { formatDateTime(it, "HH:mm") }
    val formattedCost = visit.orders?.sumOf { it.cost ?: 0.0 }?.let { "%.2f zł".format(it) }

    val clientId = "Pracownik - brakuje w Backu"
    val tableId = visit.tableId ?: stringResource(R.string.unknown_table)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = {
                homeNavController.navigate(
                    RestaurantRoutes.OrderDetail(
                        visitId = visit.visitId!!
                    )
                )
            }),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = formattedDate ?: stringResource(R.string.unknown_time),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = visit.date?.substring(0, 10) ?: stringResource(R.string.unknown_date),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = clientId,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formattedCost ?: stringResource(R.string.unknown_cost),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = stringResource(R.string.table_label, tableId), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
