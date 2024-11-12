import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.IngredientDTO
import reservant_mobile.ui.components.IconWithHeader

@Composable
fun WarehouseActivity(
    onReturnClick: () -> Unit,
    restaurantId: Int,
) {
    val viewModel: WarehouseViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WarehouseViewModel() as T
            }
        }
    )

    // Pobieramy dane z LazyPagingItems
    val ingredients = viewModel.getIngredientsFlow(restaurantId).collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        IconWithHeader(
            icon = Icons.Outlined.AddBox,
            text = "Magazyn",
            showBackButton = true,
            onReturnClick = onReturnClick
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { /* Action for generating new list */ }) {
                Text(text = "Generate New List")
            }
            Button(onClick = { viewModel.isAddDeliveryDialogVisible = true }) {
                Text(text = "New Ingredient")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(ingredients.itemCount) { index ->
                val ingredient = ingredients[index]
                if (ingredient != null) {
                    ProductCard(
                        name = ingredient.publicName ?: "no name",
                        quantity = ingredient.amount ?: 0.0,
                        minQuantity = ingredient.minimalAmount ?: 0.0,
                        unit = ingredient.unitOfMeasurement.toString(),
                        daysTillExpiration = 1,
                        defaultOrderQuantity = 40,
                        onAddClick = { viewModel.showAddDeliveryDialog(ingredient) }
                    )
                } else {
                    // Możemy wyświetlić wskaźnik ładowania, gdy dane są ładowane
//                    Text(text = stringResource(R.string.loading_ingredient))
                    Text(text = "ładowanie")
                }
            }

            // Obsługa stanu ładowania i błędów
            ingredients.apply {
                when {
                    loadState.append is androidx.paging.LoadState.Loading -> {
                        item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
                    }
                    loadState.append is androidx.paging.LoadState.Error -> {
                        item { Text("Error loading more data", color = MaterialTheme.colorScheme.error) }
                    }
                }
            }
        }
    }

    if (viewModel.isAddDeliveryDialogVisible) {
        AddDeliveryDialog(
            onDismiss = { viewModel.isAddDeliveryDialogVisible = false },
            onSubmit = { storeName, amountOrdered ->
                viewModel.addDelivery(restaurantId, storeName, amountOrdered)
            },
            ingredient = viewModel.selectedIngredient,
            restaurantId = restaurantId
        )
    }
}

@Composable
fun ProductCard(
    name: String,
    quantity: Double,
    minQuantity: Double,
    unit: String,
    daysTillExpiration: Int,
    defaultOrderQuantity: Int,
    onAddClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Name: $name",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Quantity: $quantity",
                        color = if (quantity < minQuantity) Color.Red else Color.Black,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Minimal Quantity: $minQuantity",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Unit: $unit",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Days till expiration: $daysTillExpiration",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Default Order Quantity: $defaultOrderQuantity",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onAddClick,
                        border = BorderStroke(1.dp, Color(0xFF955E71)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "ADD",
                            color = Color(0xFF955E71),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddDeliveryDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, Int) -> Unit,
    ingredient: IngredientDTO?,
    restaurantId: Int
) {
    var storeName by remember { mutableStateOf("") }
    var amountOrdered by remember { mutableStateOf(40) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Delivery") },
        text = {
            Column {
                Text(text = "Ingredient: ${ingredient?.publicName}")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = storeName,
                    onValueChange = { storeName = it },
                    label = { Text("Store Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amountOrdered.toString(),
                    onValueChange = { amountOrdered = it.toIntOrNull() ?: 0 },
                    label = { Text("Amount Ordered") },
                    modifier = Modifier.width(150.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (storeName.isNotBlank() && amountOrdered > 0) {
                    onSubmit(storeName, amountOrdered)
                    onDismiss()
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
