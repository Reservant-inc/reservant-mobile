import android.widget.Toast
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource // Importuj dla stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction.Companion.Done
import androidx.compose.ui.text.input.ImeAction.Companion.Next
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.DeliveryDTO
import reservant_mobile.data.models.dtos.IngredientDTO
import reservant_mobile.data.models.dtos.UnitOfMeasurement
import reservant_mobile.data.models.dtos.fields.Result
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

    val toastMessage by viewModel.toastMessage.collectAsState()

    val context = LocalContext.current

    // 2) Whenever toastMessage changes, show a toast
    LaunchedEffect(toastMessage) {
        toastMessage?.let { msgResId ->
            Toast.makeText(context, context.getString(msgResId), Toast.LENGTH_SHORT).show()
            // Clear the message so we don't show it again on recomposition
            viewModel.clearToastMessage()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadIngredients(restaurantId)
    }

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
                        onClick = {navHostController.navigate(RestaurantRoutes.IngredientHistory(ingredient = ingredient))}
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
            isEmployee = isEmployee,
            onAddNewIngredient = { ingredient ->
                // This is what we do to add a new ingredient
                // e.g. call viewModel.addIngredient(ingredient)
                viewModel.addIngredient(ingredient)
            },
            onSuccessRefresh = {
                // If you want to refresh your ingredients list or do something else
                viewModel.loadIngredients(restaurantId)
            }
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
            isEmployee = isEmployee,
            onAddNewIngredient = { Result(isError = false, value = null) },
            onSuccessRefresh = {}
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
    val context = LocalContext.current

    var storeName by remember { mutableStateOf("") }
    var amountOrdered by remember { mutableStateOf("") }

    // For toggling the error display after user presses "Add"
    var formSent by remember { mutableStateOf(false) }

    // Simple check: amount must be > 0
    fun isAmountInvalid(): Boolean {
        val value = amountOrdered.toDoubleOrNull()
        return (value == null || value <= 0.0)
    }

    LaunchedEffect(ingredient) {
        // If there's a default "amountToOrder," prefill it
        amountOrdered = ingredient?.amountToOrder?.toString().orEmpty()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.add_to_cart)) },
        text = {
            Column {
                Text(text = stringResource(id = R.string.ingredient_colon, ingredient?.publicName.orEmpty()))
                Spacer(modifier = Modifier.height(8.dp))

                // Store name is optional => no error if blank
                FormInput(
                    inputText = storeName,
                    onValueChange = { storeName = it },
                    label = stringResource(id = R.string.store_name),
                    optional = true,
                    // Not logically "required", so isError = false
                    isError = false,
                    // We do still pass formSent so it can highlight if you like,
                    // but here it’s optional.
                    formSent = formSent,
                    keyboardOptions = KeyboardOptions(imeAction = Next)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Required "amountOrdered"
                FormInput(
                    inputText = amountOrdered,
                    onValueChange = { amountOrdered = it },
                    label = stringResource(id = R.string.amount_to_order),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = Done),
                    maxLines = 1,
                    isError = isAmountInvalid(),
                    errorText = stringResource(R.string.error_invalid_number),
                    formSent = formSent
                )
            }
        },
        confirmButton = {
            ButtonComponent(
                onClick = {
                    // Set formSent = true => triggers error on invalid fields
                    formSent = true
                    // If amount is invalid, do NOT dismiss
                    if (isAmountInvalid()) {
                        return@ButtonComponent
                    }
                    // Otherwise, proceed
                    onSubmit(
                        storeName.takeIf { it.isNotBlank() } ?: "",
                        amountOrdered
                    )
                    onDismiss()
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
    isEmployee: Boolean = false,
    // For adding a brand new ingredient
    onAddNewIngredient: suspend (IngredientDTO) -> Result<IngredientDTO?>,
    // Called after a successful add, to refresh your data
    onSuccessRefresh: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Ingredient fields
    var publicName by remember { mutableStateOf(existingIngredient?.publicName ?: "") }
    var unitOfMeasurement by remember {
        mutableStateOf(existingIngredient?.unitOfMeasurement ?: UnitOfMeasurement.Gram)
    }
    var minimalAmount by remember { mutableStateOf(existingIngredient?.minimalAmount?.toString() ?: "") }
    var amountToOrder by remember { mutableStateOf(existingIngredient?.amountToOrder?.toString() ?: "") }
    var amount by remember { mutableStateOf(existingIngredient?.amount?.toString() ?: "") }

    // Correction fields (used if isEdit == true for "newAmount" & "comment")
    var newAmount by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }

    // This flag, once set to true, shows errors in all FormInputs at once.
    var formSent by remember { mutableStateOf(false) }
    var showCorrectionError by remember { mutableStateOf(false) }

    val unitOptions = UnitOfMeasurement.values().map { it.name }
    val expanded = remember { mutableStateOf(false) }

    // Validation checks
    fun isNameInvalid() = publicName.isBlank()
    fun isMinimalInvalid() = minimalAmount.toDoubleOrNull() == null
    fun isAmountToOrderInvalid() = amountToOrder.toDoubleOrNull() == null
    fun isInitialAmountInvalid() = amount.toDoubleOrNull() == null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (isEdit) stringResource(R.string.edit_ingredient)
                else stringResource(R.string.add_new_ingredient)
            )
        },
        text = {
            Column {
                // 1) Public Name
                FormInput(
                    inputText = publicName,
                    onValueChange = { publicName = it },
                    label = stringResource(R.string.name),
                    isDisabled = isEmployee,
                    // We'll supply the logic "is field invalid?" to isError
                    isError = isNameInvalid(),
                    errorText = stringResource(R.string.error_field_required),
                    // Crucial: pass formSent down, so error can show instantly on "Add"
                    formSent = formSent,
                    keyboardOptions = KeyboardOptions(imeAction = Next)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // 2) Unit of Measurement ComboBox
                ComboBox(
                    expanded = expanded,
                    value = unitOfMeasurement.name,
                    onValueChange = { newValue ->
                        unitOfMeasurement = UnitOfMeasurement.valueOf(newValue)
                    },
                    options = unitOptions,
                    label = stringResource(R.string.unit_of_measurement),
                    isDisabled = isEmployee
                )
                Spacer(modifier = Modifier.height(8.dp))

                // 3) Minimal Amount
                FormInput(
                    inputText = minimalAmount,
                    onValueChange = { minimalAmount = it },
                    label = stringResource(R.string.min_quantity),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = Next),
                    isDisabled = isEmployee,
                    isError = isMinimalInvalid(),
                    errorText = stringResource(R.string.error_field_required),
                    formSent = formSent
                )
                Spacer(modifier = Modifier.height(8.dp))

                // 4) Amount to Order
                FormInput(
                    inputText = amountToOrder,
                    onValueChange = { amountToOrder = it },
                    label = stringResource(R.string.amount_to_order),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = if (isEdit) Done else Next
                    ),
                    isDisabled = isEmployee,
                    isError = isAmountToOrderInvalid(),
                    errorText = stringResource(R.string.error_field_required),
                    formSent = formSent
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (!isEdit) {
                    // --- ADD NEW INGREDIENT MODE ---
                    FormInput(
                        inputText = amount,
                        onValueChange = { amount = it },
                        label = stringResource(R.string.initial_amount),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = Done),
                        isDisabled = isEmployee,
                        isError = isInitialAmountInvalid(),
                        errorText = stringResource(R.string.error_field_required),
                        formSent = formSent
                    )
                } else {
                    // --- EDIT MODE => Correction fields
                    FormInput(
                        inputText = newAmount,
                        onValueChange = { newAmount = it },
                        label = stringResource(R.string.new_amount),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = Next),
                        isDisabled = false,
                        // We typically do not show an error unless partial fill → handled below
                        isError = false,
                        formSent = false
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    FormInput(
                        inputText = comment,
                        onValueChange = { comment = it },
                        label = stringResource(R.string.comment),
                        isDisabled = false,
                        isError = false,
                        formSent = false,
                        keyboardOptions = KeyboardOptions(imeAction = Done)
                    )

                    if (showCorrectionError) {
                        Text(
                            text = stringResource(R.string.fill_both_for_correction),
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            ButtonComponent(
                onClick = {
                    // Once the user clicks Add/Save => show all errors for invalid fields
                    formSent = true

                    if (!isEdit) {
                        // Creating a brand-new ingredient
                        val isFormInvalid = isNameInvalid() ||
                                isMinimalInvalid() ||
                                isAmountToOrderInvalid() ||
                                isInitialAmountInvalid()

                        if (isFormInvalid) {
                            // Do not dismiss => user sees red errors on each invalid field
                            return@ButtonComponent
                        }
                        // If no errors, call the service
                        scope.launch {
                            val newIngredient = IngredientDTO(
                                publicName = publicName,
                                unitOfMeasurement = unitOfMeasurement,
                                minimalAmount = minimalAmount.toDoubleOrNull(),
                                amountToOrder = amountToOrder.toDoubleOrNull(),
                                amount = amount.toDoubleOrNull(),
                                restaurantId = restaurantId
                            )
                            val result = onAddNewIngredient(newIngredient)
                            if (!result.isError && result.value != null) {
                                // success => show toast, refresh, dismiss
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.success_ingredient_added),
                                    Toast.LENGTH_LONG
                                ).show()
                                onSuccessRefresh()
                                onDismiss()
                            } else {
                                // show error, but remain in dialog
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.add_ingredient_failed),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {
                        // EDIT or CORRECTION
                        val updatedIngredient = IngredientDTO(
                            ingredientId = existingIngredient?.ingredientId,
                            publicName = publicName,
                            unitOfMeasurement = unitOfMeasurement,
                            minimalAmount = minimalAmount.toDoubleOrNull(),
                            amountToOrder = amountToOrder.toDoubleOrNull(),
                            amount = existingIngredient?.amount,
                            restaurantId = restaurantId
                        )
                        val newAmountValue = newAmount.toDoubleOrNull()
                        val commentValue = comment.takeIf { it.isNotBlank() }

                        val bothEmpty = (newAmountValue == null && commentValue == null)
                        val bothFilled = (newAmountValue != null && commentValue != null)

                        if (bothEmpty) {
                            // Plain edit, no correction
                            onSubmit(updatedIngredient, null, null)
                            onDismiss()
                        } else if (bothFilled) {
                            // Correction
                            onSubmit(updatedIngredient, newAmountValue, commentValue)
                            onDismiss()
                        } else {
                            // Partial => show the correction error text
                            showCorrectionError = true
                        }
                    }
                },
                label = if (isEdit)
                    stringResource(R.string.save_changes)
                else
                    stringResource(R.string.add_ingredient)
            )
        },
        dismissButton = {
            ButtonComponent(
                onClick = onDismiss,
                label = stringResource(R.string.cancel)
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
