package reservant_mobile.ui.activities

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.reservant_mobile.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.MutableStateFlow
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

    val context = LocalContext.current

    val deliveriesFlow = deliveriesViewModel.deliveries.collectAsState()
    val lazyPagingItems = deliveriesFlow.value?.collectAsLazyPagingItems()

    val errorMessage by deliveriesViewModel.errorMessage.collectAsState()
    val isLoading by deliveriesViewModel.isLoading.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    val filterOptions by remember { mutableStateOf(listOf("Not delivered", "Delivered")) }
    var currentFilter by remember { mutableStateOf("Not delivered") }


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Nagłówek
        IconWithHeader(
            text = stringResource(R.string.label_deliveries),
            showBackButton = true,
            onReturnClick = { onReturnClick() },
            icon = Icons.Filled.DeliveryDining
        )

        // Błąd z ViewModelu (toast)
        errorMessage?.let { err ->
            Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
            deliveriesViewModel.errorMessage.value = null
        }

        SearchBarWithFilter(
            searchQuery = searchQuery,
            onSearchQueryChange = { query ->
                searchQuery = query
            },
            currentFilter = currentFilter,
            filterOptions = filterOptions,
            onFilterSelected = { selected ->
                currentFilter = selected ?: "Not delivered"
                val isDelivered = (currentFilter == "Delivered")

                deliveriesViewModel.setReturnDelivered(isDelivered)
            },
            modifier = Modifier.padding(16.dp),
            hideAllOption = true
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {

            lazyPagingItems?.let { pagingItems ->

                val allDeliveries = pagingItems.itemSnapshotList.items

                val filteredDeliveries = if (searchQuery.isBlank()) {
                    allDeliveries
                } else {
                    allDeliveries.filter { delivery ->
                        delivery.userFullName
                            ?.contains(searchQuery, ignoreCase = true) ?: false
                    }
                }

                if (filteredDeliveries.isNotEmpty()) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(filteredDeliveries) { delivery ->

                            var ingredients by remember { mutableStateOf<List<DeliveryDTO.DeliveryIngredientDTO>?>(null) }

                            LaunchedEffect(delivery.deliveryId){
                               ingredients = delivery.deliveryId?.let {
                                    deliveriesViewModel.getDeliveryIngredients(
                                        it
                                    )
                                }
                            }

                            DeliveryItem(
                                delivery = delivery,
                                onCancelClick = {
                                    delivery.deliveryId?.let { id ->
                                        deliveriesViewModel.markCanceled(id)
                                    }
                                },
                                onMarkArrivedClick = {
                                    delivery.deliveryId?.let { id ->
                                        deliveriesViewModel.confirmDelivered(id)
                                    }
                                },
                                ingredients
                            )
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.label_no_deliveries),
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } ?: run {
                // Jeśli lazyPagingItems == null
                Text(
                    text = stringResource(R.string.label_no_deliveries),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun DeliveryItem(
    delivery: DeliveryDTO,
    onCancelClick: () -> Unit,
    onMarkArrivedClick: () -> Unit,
    ingredientsList: List<DeliveryDTO.DeliveryIngredientDTO>?
) {
    // Kolory i inne zmienne – bez zmian
    val pastelRed = Color(0xFFFFC1C1)
    val pastelGreen = Color(0xFF67B873)
    val darkRed = Color(0xFFD32F2F)
    val darkGreen = Color(0xFF2E7D32)
    val darkGreenLabel = Color(58, 148, 16)

    // Dodajemy tu jedną linijkę, aby móc skorzystać z listy restaurantIngredients:
    val deliveriesViewModel = viewModel<DeliveriesViewModel>()

    var isIngredientsExpanded by remember { mutableStateOf(false) }

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
                    text = "${stringResource(R.string.label_delivery)} #${delivery.deliveryId ?: "--"}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nazwa zamawiającego
            val userName = delivery.userFullName
            if(userName != null){
                Text(
                    text = userName,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Data zamówienia
            val orderDateFormatted = delivery.orderTime?.let { formatToDateTime(it, "dd MMMM yyyy") }
            val orderTimeFormatted = delivery.orderTime?.let { formatToDateTime(it, "HH:mm") }

            Text(
                text = "${stringResource(R.string.label_ordered_at)}: $orderDateFormatted | ${orderTimeFormatted ?: "--"}",
                style = MaterialTheme.typography.bodyMedium
            )
            // Koszt
            Text(
                text = "${stringResource(R.string.label_cost)}: ${delivery.cost ?: 0.0} PLN",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Obsługa listy składników
            if (!ingredientsList.isNullOrEmpty()) {
                Text(
                    text = if (isIngredientsExpanded)
                        stringResource(R.string.label_hide_ingredients)
                    else
                        stringResource(R.string.label_show_ingredients),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable {
                            isIngredientsExpanded = !isIngredientsExpanded
                        }
                        .padding(vertical = 4.dp)
                )

                if (isIngredientsExpanded && ingredientsList.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))


                    ingredientsList.forEach { ing ->

                        val matchedIngredient = deliveriesViewModel.restaurantIngredients
                            ?.find { it.ingredientId == ing.ingredientId }

                        val finalIngredientName = matchedIngredient?.publicName
                            ?: ing.ingredientName
                            ?: "Unknown"

                        Text(
                            text = "• $finalIngredientName" +
                                    " (${stringResource(R.string.label_ordered)}: ${ing.amountOrdered ?: 0.0})",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            when {
                delivery.canceledTime != null -> {
                    val cancelDateFormatted = formatToDateTime(delivery.canceledTime, "dd MMMM yyyy")
                    val cancelTimeFormatted = formatToDateTime(delivery.canceledTime, "HH:mm")

                    Text(
                        text = "${stringResource(R.string.label_canceled_at)} $cancelDateFormatted | $cancelTimeFormatted",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                delivery.deliveredTime != null -> {
                    val deliveredDateFormatted = formatToDateTime(delivery.deliveredTime, "dd MMMM yyyy")
                    val deliveredTimeFormatted = formatToDateTime(delivery.deliveredTime, "HH:mm")

                    Text(
                        text = "${stringResource(R.string.label_arrived_at)} $deliveredDateFormatted | $deliveredTimeFormatted",
                        color = darkGreenLabel,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                else -> {
                    // Dwa przyciski: Mark arrived, Cancel
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
                                fontSize = 12.sp
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
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
