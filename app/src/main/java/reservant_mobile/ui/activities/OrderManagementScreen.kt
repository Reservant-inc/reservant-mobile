package reservant_mobile.ui.activities

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import reservant_mobile.data.utils.GetReservationStatus
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.LoadingScreenWithTimeout
import reservant_mobile.ui.navigation.RestaurantRoutes
import reservant_mobile.ui.viewmodels.EmployeeOrderViewModel
import java.time.LocalDateTime
import kotlin.time.Duration

@Composable
fun OrderManagementScreen(
    onReturnClick: () -> Unit,
    restaurantId: Int,
    isReservation: Boolean = false,
    navHostController: NavHostController
) {
    val viewModel: EmployeeOrderViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EmployeeOrderViewModel(restaurantId = restaurantId) as T
            }
        }
    )

    val innerNavController = rememberNavController()

    var selectedTabIndex by remember { mutableStateOf(0) }

    val reservationVisitsFlow by remember {
        mutableStateOf(viewModel.getVisitsFlow(
            dateStart = LocalDateTime.now(),
            reservationStatus = GetReservationStatus.ToBeReviewedByRestaurant
        ))
    }

    val currentVisitsFlow by remember {
        mutableStateOf(viewModel.getVisitsFlow(
            dateStart = LocalDateTime.now(),
            reservationStatus = GetReservationStatus.ApprovedByRestaurant
        ))
    }

    val pastVisitsFlow by remember {
        mutableStateOf(viewModel.getVisitsFlow(
            dateEnd = LocalDateTime.now(),
            reservationStatus = GetReservationStatus.ApprovedByRestaurant
        ))
    }

    val reservationVisits = if (isReservation) reservationVisitsFlow.collectAsLazyPagingItems() else null
    val currentVisits = if (!isReservation && selectedTabIndex == 0) currentVisitsFlow.collectAsLazyPagingItems() else null
    val pastVisits = if (!isReservation && selectedTabIndex == 1) pastVisitsFlow.collectAsLazyPagingItems() else null

    NavHost(
        navController = innerNavController,
        startDestination = RestaurantRoutes.ManageOrders(restaurantId = restaurantId),
    ) {
        composable<RestaurantRoutes.ManageOrders> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                IconWithHeader(
                    icon = if (isReservation) Icons.Outlined.Event else Icons.Outlined.Book,
                    text = if (isReservation) stringResource(R.string.reservations_management) else stringResource(R.string.orders_management),
                    showBackButton = true,
                    onReturnClick = onReturnClick
                )

                if (isReservation) {
                    Spacer(modifier = Modifier.height(16.dp))

                    reservationVisits?.let { visits ->
                        when (visits.loadState.refresh) {
                            is LoadState.Loading -> {
                                LoadingScreenWithTimeout(Duration.parse("10s"))
                            }
                            is LoadState.Error -> {
                                println((visits.loadState.refresh as LoadState.Error).error)
                                Text(
                                    text = stringResource(R.string.error_loading_reservations),
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            else -> {
                                if (visits.itemCount == 0) {
                                    Text(
                                        text = stringResource(R.string.no_pending_reservations),
                                        modifier = Modifier.padding(16.dp)
                                    )
                                } else {
                                    OrderList(
                                        visits = visits,
                                        homeNavController = innerNavController,
                                        viewModel = viewModel,
                                        isReservation = true
                                    )
                                }
                            }
                        }
                    }
                } else {
                    val tabTitles = listOf(
                        stringResource(R.string.current_orders),
                        stringResource(R.string.past_orders)
                    )

                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                text = { Text(title) },
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (selectedTabIndex) {
                        0 -> {
                            currentVisits?.let { visits ->
                                when (visits.loadState.refresh) {
                                    is LoadState.Loading -> {
                                        LoadingScreenWithTimeout(Duration.parse("10s"))
                                    }
                                    is LoadState.Error -> {
                                        Text(
                                            text = stringResource(R.string.error_orders),
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                    else -> {
                                        if (visits.itemCount == 0) {
                                            Text(
                                                text = stringResource(R.string.no_visits_available),
                                                modifier = Modifier.padding(16.dp)
                                            )
                                        } else {
                                            OrderList(
                                                visits = visits,
                                                homeNavController = innerNavController,
                                                viewModel = viewModel,
                                                isReservation = false
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        1 -> {
                            pastVisits?.let { visits ->
                                when (visits.loadState.refresh) {
                                    is LoadState.Loading -> {
                                        LoadingScreenWithTimeout(Duration.parse("10s"))
                                    }
                                    is LoadState.Error -> {
                                        Text(
                                            text = stringResource(R.string.error_orders),
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                    else -> {
                                        if (visits.itemCount == 0) {
                                            Text(
                                                text = stringResource(R.string.no_visits_available),
                                                modifier = Modifier.padding(16.dp)
                                            )
                                        } else {
                                            OrderList(
                                                visits = visits,
                                                homeNavController = innerNavController,
                                                viewModel = viewModel,
                                                isReservation = false
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        composable<RestaurantRoutes.OrderDetail> {
            OrderDetailsScreen(
                onReturnClick = { innerNavController.popBackStack() },
                visitId = it.toRoute<RestaurantRoutes.OrderDetail>().visitId,
                isReservation = isReservation,
                viewModel = viewModel,
                navHostController = navHostController,
                restaurantId = restaurantId
            )
        }
    }
}

@Composable
fun OrderList(
    visits: LazyPagingItems<VisitDTO>,
    homeNavController: NavHostController,
    viewModel: EmployeeOrderViewModel,
    isReservation: Boolean = false
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(visits.itemCount) { index ->
            val visit = visits[index]
            if (visit != null) {
                viewModel.cacheVisit(visit)
                VisitCard(visit = visit, homeNavController = homeNavController, isReservation = isReservation)
            } else {
                Text(text = stringResource(R.string.loading_visit))
            }
        }

        visits.apply {
            when {
                loadState.append is LoadState.Loading -> {
                    item { LoadingScreenWithTimeout(Duration.parse("10s")) }
                }
                loadState.append is LoadState.Error -> {
                    val e = loadState.append as LoadState.Error
                    item {
                        Text(
                            text = stringResource(R.string.error_orders),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun VisitCard(visit: VisitDTO, homeNavController: NavHostController, isReservation: Boolean = false) {
    val formattedDate = visit.date?.let { formatToDateTime(it, "HH:mm") }
    val formattedCost = visit.orders?.sumOf { it.cost ?: 0.0 }?.let { "%.2f zł".format(it) }

    val employee by remember {
        mutableStateOf(visit.orders?.get(0)?.assignedEmployee)
    }

    val employeeStr by remember {
        mutableStateOf(employee?.let { "${it.firstName} ${it.lastName}" })
    }

    val tableId = visit.tableId?.toString() ?: stringResource(R.string.unknown_table)

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
                    text = employeeStr ?: stringResource(id = R.string.label_no_employee_assigned),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (!isReservation) {
                    Text(
                        text = if ((visit.orders?.sumOf { it.cost ?: 0.0 } ?: 0.0) == 0.0) {
                            stringResource(R.string.reservation_label)
                        } else {
                            formattedCost ?: stringResource(R.string.unknown_cost)
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.table_label) + " " + tableId,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

