import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Warehouse
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import reservant_mobile.data.models.dtos.DeliveryDTO
import reservant_mobile.data.models.dtos.IngredientDTO
import reservant_mobile.data.models.dtos.UnitOfMeasurement
import reservant_mobile.ui.components.ButtonComponent
import reservant_mobile.ui.components.ComboBox
import reservant_mobile.ui.components.FormInput
import reservant_mobile.ui.components.IconWithHeader

@OptIn(ExperimentalMaterial3Api::class)
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

    val ingredients by viewModel.ingredients.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadIngredients(restaurantId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            IconWithHeader(
                icon = Icons.Outlined.Warehouse,
                text = "Magazyn",
                showBackButton = true,
                onReturnClick = onReturnClick
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ButtonComponent(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    onClick = { viewModel.generateNewList() },
                    label = "Generuj nową listę"
                )
                ButtonComponent(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    onClick = { viewModel.isAddIngredientDialogVisible = true },
                    label = "Nowy składnik"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(ingredients.size) { index ->
                    val ingredient = ingredients[index]
                    ProductCard(
                        name = ingredient.publicName ?: "Brak nazwy",
                        quantity = ingredient.amount ?: 0.0,
                        minQuantity = ingredient.minimalAmount ?: 0.0,
                        unit = ingredient.unitOfMeasurement ?: UnitOfMeasurement.Unit,
                        defaultOrderQuantity = ingredient.amountToOrder,
                        onAddClick = { viewModel.showAddDeliveryDialog(ingredient) }
                    )
                }
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onClick = { viewModel.isCartVisible = true }
        ) {
            BadgedBox(
                badge = {
                    if (viewModel.cart.isNotEmpty()) {
                        Text(viewModel.cart.size.toString())
                    }
                }
            ) {
                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Koszyk")
            }
        }
    }

    if (viewModel.isCartVisible) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.isCartVisible = false }
        ) {
            CartContent(
                cartItems = viewModel.cart,
                onRemoveItem = { item -> viewModel.removeFromCart(item) },
                onSubmitOrder = { viewModel.submitOrder(restaurantId) }
            )
        }
    }

    if (viewModel.isAddDeliveryDialogVisible) {
        AddDeliveryDialog(
            onDismiss = { viewModel.isAddDeliveryDialogVisible = false },
            onSubmit = { storeName, amountOrdered ->
                val ingredient = viewModel.selectedIngredient
                if (ingredient != null) {
                    viewModel.addToCart(
                        DeliveryDTO.DeliveryIngredientDTO(
                            ingredientId = ingredient.ingredientId ?: 0,
                            amountOrdered = amountOrdered.toDouble(),
                            storeName = storeName
                        )
                    )
                }
                viewModel.isAddDeliveryDialogVisible = false
            },
            ingredient = viewModel.selectedIngredient
        )
    }

    if (viewModel.isAddIngredientDialogVisible) {
        AddIngredientDialog(
            onDismiss = { viewModel.isAddIngredientDialogVisible = false },
            onSubmit = { ingredient ->
                viewModel.addIngredient(ingredient)
            },
            restaurantId = restaurantId
        )
    }

    if (viewModel.showAddedToCartMessage) {
        AlertDialog(
            onDismissRequest = { viewModel.showAddedToCartMessage = false },
            title = { Text("Dodano do koszyka") },
            text = {
                Text(viewModel.addedToCartMessage)
            },
            confirmButton = {
                ButtonComponent(
                    onClick = { viewModel.showAddedToCartMessage = false },
                    label = "OK"
                )
            }
        )
    }

    if (viewModel.showMissingAmountToOrderDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showMissingAmountToOrderDialog = false },
            title = { Text("Brak domyślnej ilości do zamówienia") },
            text = {
                Column {
                    Text("Następujące składniki nie zostały dodane do koszyka, ponieważ nie mają ustawionej domyślnej ilości do zamówienia:")
                    Spacer(modifier = Modifier.height(8.dp))
                    viewModel.ingredientsWithoutAmountToOrderList.forEach { ingredient ->
                        Text("- ${ingredient.publicName}")
                    }
                }
            },
            confirmButton = {
                ButtonComponent(
                    onClick = { viewModel.showMissingAmountToOrderDialog = false },
                    label = "OK"
                )
            }
        )
    }
}

@Composable
fun ProductCard(
    name: String,
    quantity: Double,
    minQuantity: Double,
    unit: UnitOfMeasurement,
    defaultOrderQuantity: Double?,
    onAddClick: () -> Unit
) {
    val unitAbbreviation = when (unit) {
        UnitOfMeasurement.Gram -> "g"
        UnitOfMeasurement.Liter -> "l"
        UnitOfMeasurement.Unit -> ""
    }

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
                        text = "Nazwa: $name",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ilość: $quantity$unitAbbreviation",
                        color = if (quantity < minQuantity) Color.Red else Color.Black,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Minimalna ilość: $minQuantity$unitAbbreviation",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    if (defaultOrderQuantity != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Domyślna ilość do zamówienia: $defaultOrderQuantity$unitAbbreviation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
                OutlinedButton(
                    onClick = onAddClick,
                    border = BorderStroke(1.dp, Color(0xFF955E71)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Dodaj",
                        color = Color(0xFF955E71),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AddDeliveryDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit,
    ingredient: IngredientDTO?
) {
    var storeName by remember { mutableStateOf("") }
    var amountOrdered by remember { mutableStateOf("") }

    LaunchedEffect(ingredient) {
        amountOrdered = ingredient?.amountToOrder?.toString() ?: ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dodaj do koszyka") },
        text = {
            Column {
                Text(text = "Składnik: ${ingredient?.publicName}")
                Spacer(modifier = Modifier.height(8.dp))
                FormInput(
                    inputText = storeName,
                    onValueChange = { storeName = it },
                    label = "Nazwa sklepu",
                    optional = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                FormInput(
                    inputText = amountOrdered,
                    onValueChange = { amountOrdered = it },
                    label = "Ilość do zamówienia",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    maxLines = 1
                )
            }
        },
        confirmButton = {
            ButtonComponent(
                onClick = {
                    if (storeName.isNotBlank() && amountOrdered.toDoubleOrNull() ?: 0.0 > 0.0) {
                        onSubmit(storeName.takeIf { it.isNotBlank() } ?: "", amountOrdered)
                        onDismiss()
                    }
                },
                label = "Dodaj do koszyka"
            )
        },
        dismissButton = {
            ButtonComponent(
                onClick = onDismiss,
                label = "Anuluj"
            )
        }
    )
}

@Composable
fun AddIngredientDialog(
    onDismiss: () -> Unit,
    onSubmit: (IngredientDTO) -> Unit,
    restaurantId: Int
) {
    var publicName by remember { mutableStateOf("") }
    var unitOfMeasurement by remember { mutableStateOf(UnitOfMeasurement.Gram) }
    var minimalAmount by remember { mutableStateOf("") }
    var amountToOrder by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    val unitOptions = UnitOfMeasurement.values().map { it.name }
    val expanded = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dodaj nowy składnik") },
        text = {
            Column {
                FormInput(
                    inputText = publicName,
                    onValueChange = { publicName = it },
                    label = "Nazwa"
                )
                Spacer(modifier = Modifier.height(8.dp))
                ComboBox(
                    expanded = expanded,
                    value = unitOfMeasurement.name,
                    onValueChange = {
                        unitOfMeasurement = UnitOfMeasurement.valueOf(it)
                    },
                    options = unitOptions,
                    label = "Jednostka miary"
                )
                Spacer(modifier = Modifier.height(8.dp))
                FormInput(
                    inputText = minimalAmount,
                    onValueChange = { minimalAmount = it },
                    label = "Minimalna ilość",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                FormInput(
                    inputText = amountToOrder,
                    onValueChange = { amountToOrder = it },
                    label = "Ilość do zamówienia",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                FormInput(
                    inputText = amount,
                    onValueChange = { amount = it },
                    label = "Początkowa ilość",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            ButtonComponent(
                onClick = {
                    val ingredient = IngredientDTO(
                        publicName = publicName,
                        unitOfMeasurement = unitOfMeasurement,
                        minimalAmount = minimalAmount.toDoubleOrNull(),
                        amountToOrder = amountToOrder.toDoubleOrNull(),
                        amount = amount.toDoubleOrNull(),
                        restaurantId = restaurantId
                    )
                    onSubmit(ingredient)
                },
                label = "Dodaj składnik"
            )
        },
        dismissButton = {
            ButtonComponent(
                onClick = onDismiss,
                label = "Anuluj"
            )
        }
    )
}

@Composable
fun CartContent(
    cartItems: List<DeliveryDTO.DeliveryIngredientDTO>,
    onRemoveItem: (DeliveryDTO.DeliveryIngredientDTO) -> Unit,
    onSubmitOrder: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight(0.8f)
            .padding(16.dp)
    ) {
        Text(text = "Twój koszyk", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(cartItems.size) { index ->
                val item = cartItems[index]
                CartItemCard(
                    item = item,
                    onRemove = { onRemoveItem(item) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        ButtonComponent(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSubmitOrder,
            label = "Złóż zamówienie"
        )
    }
}

@Composable
fun CartItemCard(
    item: DeliveryDTO.DeliveryIngredientDTO,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "ID składnika: ${item.ingredientId}")
                Text(text = "Nazwa sklepu: ${item.storeName}")
                Text(text = "Ilość zamówiona: ${item.amountOrdered}")
            }
            IconButton(onClick = onRemove) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Usuń")
            }
        }
    }
}
