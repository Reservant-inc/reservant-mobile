import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.res.stringResource // Importuj dla stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.DeliveryDTO
import reservant_mobile.data.models.dtos.IngredientDTO
import reservant_mobile.data.models.dtos.UnitOfMeasurement
import reservant_mobile.ui.components.BadgeFloatingButton
import reservant_mobile.ui.components.ButtonComponent
import reservant_mobile.ui.components.ComboBox
import reservant_mobile.ui.components.FormInput
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.navigation.RestaurantRoutes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarehouseActivity(
    onReturnClick: () -> Unit,
    restaurantId: Int,
    isEmployee: Boolean,
    navHostController: NavHostController
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
                text = stringResource(id = R.string.warehouse_title),
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
                    label = stringResource(id = R.string.generate_new_list)
                )
                if(!isEmployee) {
                    ButtonComponent(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        onClick = { viewModel.isAddIngredientDialogVisible = true },
                        label = stringResource(id = R.string.new_ingredient)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(ingredients.size) { index ->
                    val ingredient = ingredients[index]
                    ProductCard(
                        name = ingredient.publicName ?: stringResource(id = R.string.no_name),
                        quantity = ingredient.amount ?: 0.0,
                        minQuantity = ingredient.minimalAmount ?: 0.0,
                        unit = ingredient.unitOfMeasurement ?: UnitOfMeasurement.Unit,
                        defaultOrderQuantity = ingredient.amountToOrder,
                        onAddClick = { viewModel.showAddDeliveryDialog(ingredient) },
                        onEditClick = { viewModel.showEditIngredientDialog(ingredient) },
                        onClick = {navHostController.navigate(RestaurantRoutes.IngredientHistory(ingredientId = ingredient.ingredientId ?: 0))}
                    )
                }
            }
        }

        BadgeFloatingButton(
            icon = Icons.Default.ShoppingCart,
            contentDescription = stringResource(id = R.string.cart),
            itemCount = viewModel.cart.size,
            onClick = { viewModel.isCartVisible = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
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
                            storeName = storeName.takeIf { it.isNotBlank() },
                            ingredientName = ingredient.publicName
                        )
                    )
                }
                viewModel.isAddDeliveryDialogVisible = false
            },
            ingredient = viewModel.selectedIngredient
        )
    }

    if (viewModel.isAddIngredientDialogVisible) {
        AddOrEditIngredientDialog(
            onDismiss = { viewModel.isAddIngredientDialogVisible = false },
            onSubmit = { ingredient, _, _ ->
                viewModel.addIngredient(ingredient)
            },
            restaurantId = restaurantId,
            isEdit = false,
            isEmployee = isEmployee
        )
    }


    if (viewModel.isEditIngredientDialogVisible && viewModel.ingredientToEdit != null) {
        AddOrEditIngredientDialog(
            onDismiss = { viewModel.isEditIngredientDialogVisible = false },
            onSubmit = { updatedIngredient, newAmount, comment ->
                viewModel.submitEditIngredient(updatedIngredient, newAmount, comment)
            },
            restaurantId = restaurantId,
            isEdit = true,
            existingIngredient = viewModel.ingredientToEdit,
            isEmployee = isEmployee
        )
    }

    if (viewModel.showAddedToCartMessage) {
        AlertDialog(
            onDismissRequest = { viewModel.showAddedToCartMessage = false },
            title = { Text(stringResource(id = R.string.added_to_cart)) },
            text = {
                Text(stringResource(id = viewModel.addedToCartMessageResId, *viewModel.addedToCartMessageArgs))
            },
            confirmButton = {
                ButtonComponent(
                    onClick = { viewModel.showAddedToCartMessage = false },
                    label = stringResource(id = R.string.ok)
                )
            }
        )
    }

    if (viewModel.showAlreadyInCartMessage) {
        AlertDialog(
            onDismissRequest = { viewModel.showAlreadyInCartMessage = false },
            title = { Text(stringResource(id = R.string.already_in_cart)) },
            text = {
                if (viewModel.alreadyInCartIngredientNames.isNotEmpty()) {
                    Text(stringResource(id = viewModel.alreadyInCartMessageResId, viewModel.alreadyInCartIngredientNames))
                } else {
                    Text(stringResource(id = viewModel.alreadyInCartMessageResId))
                }
            },
            confirmButton = {
                ButtonComponent(
                    onClick = { viewModel.showAlreadyInCartMessage = false },
                    label = stringResource(id = R.string.ok)
                )
            }
        )
    }

    if (viewModel.showMissingAmountToOrderDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showMissingAmountToOrderDialog = false },
            title = { Text(stringResource(id = R.string.missing_default_order_quantity)) },
            text = {
                Column {
                    Text(stringResource(id = R.string.following_ingredients_not_added))
                    Spacer(modifier = Modifier.height(8.dp))
                    viewModel.ingredientsWithoutAmountToOrderList.forEach { ingredient ->
                        Text("- ${ingredient.publicName}")
                    }
                }
            },
            confirmButton = {
                ButtonComponent(
                    onClick = { viewModel.showMissingAmountToOrderDialog = false },
                    label = stringResource(id = R.string.ok)
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
    onAddClick: () -> Unit,
    onEditClick: () -> Unit,
    onClick: () -> Unit
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
            .clickable(onClick = onClick)
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
                        text = name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.quantity_colon, "$quantity$unitAbbreviation"),
                        color = if (quantity < minQuantity) Color.Red else Color.Black,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.min_quantity_colon, "$minQuantity$unitAbbreviation"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    if (defaultOrderQuantity != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(id = R.string.default_order_quantity_colon, "$defaultOrderQuantity$unitAbbreviation"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
                Column{
                    OutlinedButton(
                        onClick = onAddClick,
                        border = BorderStroke(1.dp, Color(0xFF955E71)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.add),
                            color = Color(0xFF955E71),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(id = R.string.edit)
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
        title = { Text(stringResource(id = R.string.add_to_cart)) },
        text = {
            Column {
                Text(text = stringResource(id = R.string.ingredient_colon, ingredient?.publicName ?: ""))
                Spacer(modifier = Modifier.height(8.dp))
                FormInput(
                    inputText = storeName,
                    onValueChange = { storeName = it },
                    label = stringResource(id = R.string.store_name),
                    optional = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                FormInput(
                    inputText = amountOrdered,
                    onValueChange = { amountOrdered = it },
                    label = stringResource(id = R.string.amount_to_order),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    maxLines = 1
                )
            }
        },
        confirmButton = {
            ButtonComponent(
                onClick = {
                    if ((amountOrdered.toDoubleOrNull() ?: 0.0) > 0.0) {
                        onSubmit(storeName.takeIf { it.isNotBlank() } ?: "", amountOrdered)
                        onDismiss()
                    }
                },
                label = stringResource(id = R.string.add_to_cart)
            )
        },
        dismissButton = {
            ButtonComponent(
                onClick = onDismiss,
                label = stringResource(id = R.string.cancel)
            )
        }
    )
}

@Composable
fun AddOrEditIngredientDialog(
    onDismiss: () -> Unit,
    onSubmit: (IngredientDTO, Double?, String?) -> Unit,
    restaurantId: Int,
    isEdit: Boolean = false,
    existingIngredient: IngredientDTO? = null,
    isEmployee: Boolean = false
) {
    var publicName by remember { mutableStateOf(existingIngredient?.publicName ?: "") }
    var unitOfMeasurement by remember { mutableStateOf(existingIngredient?.unitOfMeasurement ?: UnitOfMeasurement.Gram) }
    var minimalAmount by remember { mutableStateOf(existingIngredient?.minimalAmount?.toString() ?: "") }
    var amountToOrder by remember { mutableStateOf(existingIngredient?.amountToOrder?.toString() ?: "") }
    var amount by remember { mutableStateOf(existingIngredient?.amount?.toString() ?: "") }
    var newAmount by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    val unitOptions = UnitOfMeasurement.values().map { it.name }
    val expanded = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEdit) stringResource(id = R.string.edit_ingredient) else stringResource(id = R.string.add_new_ingredient)) },
        text = {
            Column {
                FormInput(
                    inputText = publicName,
                    onValueChange = { publicName = it },
                    label = stringResource(id = R.string.name),
                    isDisabled = isEmployee
                )
                Spacer(modifier = Modifier.height(8.dp))
                ComboBox(
                    expanded = expanded,
                    value = unitOfMeasurement.name,
                    onValueChange = {
                        unitOfMeasurement = UnitOfMeasurement.valueOf(it)
                    },
                    options = unitOptions,
                    label = stringResource(id = R.string.unit_of_measurement),
                    isDisabled = isEmployee
                )
                Spacer(modifier = Modifier.height(8.dp))
                FormInput(
                    inputText = minimalAmount,
                    onValueChange = { minimalAmount = it },
                    label = stringResource(id = R.string.min_quantity),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isDisabled = isEmployee
                )
                Spacer(modifier = Modifier.height(8.dp))
                FormInput(
                    inputText = amountToOrder,
                    onValueChange = { amountToOrder = it },
                    label = stringResource(id = R.string.amount_to_order),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isDisabled = isEmployee
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (!isEdit) {
                    FormInput(
                        inputText = amount,
                        onValueChange = { amount = it },
                        label = stringResource(id = R.string.initial_amount),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isDisabled = isEmployee
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    FormInput(
                        inputText = newAmount,
                        onValueChange = { newAmount = it },
                        label = stringResource(id = R.string.new_amount),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isDisabled = false
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FormInput(
                        inputText = comment,
                        onValueChange = { comment = it },
                        label = stringResource(id = R.string.comment),
                        keyboardOptions = KeyboardOptions.Default,
                        isDisabled = false
                    )
                }
            }
        },
        confirmButton = {
            ButtonComponent(
                onClick = {
                    val ingredient = IngredientDTO(
                        ingredientId = existingIngredient?.ingredientId,
                        publicName = publicName,
                        unitOfMeasurement = unitOfMeasurement,
                        minimalAmount = minimalAmount.toDoubleOrNull(),
                        amountToOrder = amountToOrder.toDoubleOrNull(),
                        amount = if (isEdit) existingIngredient?.amount else amount.toDoubleOrNull(),
                        restaurantId = restaurantId
                    )
                    val newAmountValue = if (isEdit) newAmount.toDoubleOrNull() else null
                    val commentValue = if (isEdit) comment.takeIf { it.isNotBlank() } else null
                    onSubmit(ingredient, newAmountValue, commentValue)
                    onDismiss()
                },
                label = if (isEdit) stringResource(id = R.string.save_changes) else stringResource(id = R.string.add_ingredient)
            )
        },
        dismissButton = {
            ButtonComponent(
                onClick = onDismiss,
                label = stringResource(id = R.string.cancel)
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
        Text(text = stringResource(id = R.string.your_cart), style = MaterialTheme.typography.headlineSmall)
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
            label = stringResource(id = R.string.submit_order)
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
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = stringResource(id = R.string.ingredient_colon, item.ingredientName ?: ""))
                if (!item.storeName.isNullOrBlank()) {
                    Text(text = stringResource(id = R.string.store_name_colon, item.storeName))
                }
                Text(text = stringResource(id = R.string.amount_ordered_colon, item.amountOrdered ?: 0.0))
            }
            IconButton(onClick = onRemove) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = stringResource(id = R.string.remove))
            }
        }
    }
}
