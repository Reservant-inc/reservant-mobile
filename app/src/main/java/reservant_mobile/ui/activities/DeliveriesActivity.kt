package reservant_mobile.ui.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.reservant_mobile.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import reservant_mobile.data.models.dtos.DeliveryDTO
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.SearchBarWithFilter
import reservant_mobile.ui.viewmodels.DeliveriesViewModel

@Composable
fun DeliveriesActivity(
    navController: NavHostController,
    restaurantId: Int,
    onReturnClick: () -> Unit
) {
    val deliveriesViewModel: DeliveriesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                DeliveriesViewModel(restaurantId = restaurantId) as T
        }
    )

    val deliveriesFlow = deliveriesViewModel.deliveries.collectAsState()
    val lazyPagingItems = deliveriesFlow.value?.collectAsLazyPagingItems()

    val errorMessage by deliveriesViewModel.errorMessage.collectAsState()
    val isLoading by deliveriesViewModel.isLoading.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    var selectedStatus by remember { mutableStateOf<String?>(null) }

    val filterOptions = listOf("Delivered", "Canceled", "Pending")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        IconWithHeader(
            text = stringResource(R.string.label_deliveries),
            showBackButton = true,
            onReturnClick = { onReturnClick() },
            icon = Icons.Filled.DeliveryDining
        )

        // Wyświetlanie błędu
        errorMessage?.let { err ->
            Text(
                text = err,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        SearchBarWithFilter(
            searchQuery = searchQuery,
            onSearchQueryChange = { query ->
                searchQuery = query
            },
            currentFilter = selectedStatus,
            filterOptions = filterOptions,
            onFilterSelected = { status ->
                selectedStatus = status
            },
            modifier = Modifier
                .padding(16.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Gdybyś włączył pobieranie z lazyPagingItems – poniższy kod jest zakomentowany.
            // Tutaj używamy exampleDeliveries na potrzeby testów.
            //
            // lazyPagingItems?.let { pagingItems ->
            //     ...
            // }

            val exampleDeliveries = listOf(
                DeliveryDTO(
                    deliveryId = 1,
                    orderTime = "2025-01-21T10:00:00.000Z",
                    userFullName = "Jan Nowak",
                    cost = 135.50,
                    ingredients = listOf(
                        DeliveryDTO.DeliveryIngredientDTO(
                            deliveryId = 1,
                            ingredientId = 101,
                            amountOrdered = 5.0,
                            ingredientName = "Pomidory"
                        )
                    )
                ),
                DeliveryDTO(
                    deliveryId = 2,
                    orderTime = "2025-01-22T09:15:00.000Z",
                    userFullName = "Anna Kowalska",
                    cost = 64.99
                ),
                DeliveryDTO(
                    deliveryId = 3,
                    orderTime = "2025-01-23T15:05:00.000Z",
                    deliveredTime = "2025-01-23T16:02:00.000Z",
                    userFullName = "Restauracja Rondo",
                    cost = 205.99,
                    ingredients = listOf(
                        DeliveryDTO.DeliveryIngredientDTO(
                            deliveryId = 3,
                            ingredientId = 202,
                            amountOrdered = 10.0,
                            amountDelivered = 10.0,
                            ingredientName = "Makaron"
                        )
                    )
                ),
                DeliveryDTO(
                    deliveryId = 4,
                    orderTime = "2025-01-24T11:20:00.000Z",
                    canceledTime = "2025-01-24T12:00:00.000Z",
                    userFullName = "Firma Cateringowa",
                    cost = 380.10
                ),
                DeliveryDTO(
                    deliveryId = 5,
                    orderTime = "2025-01-25T08:45:00.000Z",
                    userFullName = "Stanisław Bąk",
                    cost = 99.00,
                    ingredients = listOf(
                        DeliveryDTO.DeliveryIngredientDTO(
                            deliveryId = 5,
                            ingredientId = 303,
                            amountOrdered = 2.0,
                            ingredientName = "Sos pomidorowy"
                        ),
                        DeliveryDTO.DeliveryIngredientDTO(
                            deliveryId = 5,
                            ingredientId = 404,
                            amountOrdered = 12.0,
                            ingredientName = "Ser mozzarella"
                        )
                    )
                )
            )

            // 4) Filtrujemy listę wg searchQuery + selectedStatus
            val filteredDeliveries = exampleDeliveries.filter { delivery ->
                // Filtr po statusie
                val statusMatches = when (selectedStatus) {
                    "Canceled" -> delivery.canceledTime != null
                    "Delivered" -> delivery.deliveredTime != null
                    "Pending" -> (delivery.canceledTime == null && delivery.deliveredTime == null)
                    else -> true // All albo null
                }
                // Filtr po wyszukiwaniu w userFullName (ignorujemy wielkość liter)
                val searchMatches = delivery.userFullName
                    ?.contains(searchQuery, ignoreCase = true) ?: false

                statusMatches && searchMatches
            }

            // 5) Wyświetlamy przefiltrowane rekordy
            LazyColumn {
                items(filteredDeliveries) { delivery ->
                    DeliveryItem(
                        delivery = delivery,
                        onCancelClick = {
                            delivery.deliveryId?.let {
                                deliveriesViewModel.markCanceled(it)
                            }
                        },
                        onMarkArrivedClick = {
                            delivery.deliveryId?.let {
                                deliveriesViewModel.confirmDelivered(it)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DeliveryItem(
    delivery: DeliveryDTO,
    onCancelClick: () -> Unit,
    onMarkArrivedClick: () -> Unit
) {

    val pastelRed = Color(0xFFFFC1C1)
    val pastelGreen = Color(0xFF84C58D)

    val darkRed = Color(0xFFD32F2F)
    val darkGreen = Color(0xFF28692B)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalShipping,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                            CircleShape
                        )
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Delivery #${delivery.deliveryId ?: "--"}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val userName = delivery.userFullName ?: "Anonim"
            Text(
                text = userName,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            val orderDateFormatted = delivery.orderTime?.let { formatToDateTime(it, "dd MMMM yyyy") }
            val orderTimeFormatted = delivery.orderTime?.let { formatToDateTime(it, "HH:mm") }

            Text(
                text = stringResource(R.string.label_ordered)+": $orderDateFormatted | ${orderTimeFormatted ?: "--"}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = stringResource(R.string.label_cost) + ": ${delivery.cost ?: 0.0} PLN",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {

                delivery.canceledTime != null -> {

                    val cancelDateFormatted = formatToDateTime(delivery.canceledTime, "dd MMMM yyyy")
                    val cancelTimeFormatted = formatToDateTime(delivery.canceledTime, "HH:mm")
                    Text(
                        text = stringResource(R.string.label_canceled_at)+": $cancelDateFormatted | $cancelTimeFormatted",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                delivery.deliveredTime != null -> {
                    val deliveredDateFormatted = formatToDateTime(delivery.deliveredTime, "dd MMMM yyyy")
                    val deliveredTimeFormatted = formatToDateTime(delivery.deliveredTime, "HH:mm")

                    Text(
                        text = stringResource(R.string.label_arrived_at)+": $deliveredDateFormatted | $deliveredTimeFormatted",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                else -> {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ElevatedButton(
                            onClick = onMarkArrivedClick,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = pastelGreen,
                                contentColor = darkGreen
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.label_mark_arrived),
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                        ElevatedButton(
                            onClick = onCancelClick,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = pastelRed,
                                contentColor = darkRed
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.label_cancel),
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}