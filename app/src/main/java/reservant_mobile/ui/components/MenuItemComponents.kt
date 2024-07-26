package reservant_mobile.ui.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.reservant_mobile.R
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import reservant_mobile.data.models.dtos.fields.FormField

@Composable
fun MenuItemCard(
    menuItem: RestaurantMenuItemDTO,
    role: String,
    photo: ImageBitmap? = null,
    onInfoClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    name: FormField? = null,
    altName: FormField? = null,
    price: FormField? = null,
    alcoholPercentage: FormField? = null,
    photoField: FormField? = null,
    clearFields: () -> Unit = {},
    context: Context? = null
) {
    var showConfirmDeletePopup by remember { mutableStateOf(false) }
    var showEditPopup by remember { mutableStateOf(false) }

    when {
        showConfirmDeletePopup && role == Roles.RESTAURANT_OWNER -> {
            DeleteCountdownPopup(
                icon = Icons.Filled.DeleteForever,
                title = stringResource(id = R.string.confirm_delete_title),
                text = stringResource(id = R.string.confirm_delete_text),
                onConfirm = {
                    onDeleteClick()
                    showConfirmDeletePopup = false
                },
                onDismissRequest = { showConfirmDeletePopup = false },
                confirmText = stringResource(id = R.string.label_yes_capital),
                dismissText = stringResource(id = R.string.label_cancel)
            )
        }

        showEditPopup && role == Roles.RESTAURANT_OWNER -> {
            name?.value = menuItem.name
            altName?.value = menuItem.alternateName ?: ""
            price?.value = menuItem.price.toString()
            alcoholPercentage?.value = menuItem.alcoholPercentage?.toString() ?: ""
            photoField?.value = menuItem.photoFileName ?: ""

            MenuItemPopup(
                title = { Text(text = stringResource(id = R.string.label_edit_menu_item)) },
                hide = { showEditPopup = false },
                onConfirm = onEditClick,
                clear = clearFields,
                name = name!!,
                altName = altName!!,
                price = price!!,
                alcoholPercentage = alcoholPercentage!!,
                photo = photoField!!,
                context = context!!
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = menuItem.name,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    if (menuItem.alternateName != null) {
                        Text(
                            text = menuItem.alternateName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = stringResource(R.string.label_menu_price) + ": ${menuItem.price} zł",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (menuItem.alcoholPercentage != null) {
                        Text(
                            text = "Alcohol Percentage: ${menuItem.alcoholPercentage}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        when (role) {
                            Roles.CUSTOMER -> {
                                IconButton(
                                    onClick = onInfoClick,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = "Info",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(
                                    onClick = onAddClick,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddShoppingCart,
                                        contentDescription = "Add to Cart",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Roles.RESTAURANT_OWNER -> {
                                SecondaryButton(
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .size(50.dp),
                                    onClick = { showEditPopup = true },
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "EditMenuItem"
                                )
                                SecondaryButton(
                                    modifier = Modifier
                                        .size(50.dp),
                                    onClick = { showConfirmDeletePopup = true },
                                    imageVector = Icons.Filled.DeleteForever,
                                    contentDescription = "DeleteMenuItem"
                                )
                            }
                        }
                    }
                }

                Image(
                    painter = painterResource(R.drawable.pizza),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(start = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun MenuItemPopup(
    title: @Composable (() -> Unit),
    hide: () -> Unit,
    onConfirm: () -> Unit,
    clear: () -> Unit,
    name: FormField,
    altName: FormField,
    price: FormField,
    alcoholPercentage: FormField,
    photo: FormField,
    context: Context
) {
    AlertDialog(
        onDismissRequest = {
            hide()
            clear()
        },
        title = title,
        text = {
            Column {
                FormInput(
                    label = stringResource(id = R.string.label_restaurant_name),
                    inputText = name.value,
                    onValueChange = { name.value = it }
                )
                FormInput(
                    label = stringResource(id = R.string.label_alternate_name),
                    optional = true,
                    inputText = altName.value,
                    onValueChange = { altName.value = it }
                )
                FormInput(
                    label = stringResource(id = R.string.label_price),
                    inputText = price.value,
                    onValueChange = { price.value = it },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                FormInput(
                    label = stringResource(id = R.string.label_alcohol),
                    inputText = alcoholPercentage.value,
                    onValueChange = { alcoholPercentage.value = it },
                    optional = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                FormFileInput(
                    label = stringResource(id = R.string.label_menu_item_photo),
                    onFilePicked = { file -> photo.value = file.toString() },
                    context = context
                )

            }
        },
        dismissButton = {
            ButtonComponent(
                onClick = {
                    hide()
                    clear()
                },
                label = stringResource(id = R.string.label_cancel)
            )
        },
        confirmButton = {
            ButtonComponent(
                onClick = {
                    hide()
                    onConfirm()
                    clear()
                },
                label = stringResource(id = R.string.label_save)
            )
        },

        )
}

@Composable
fun AddMenuItemButton(
    name: FormField,
    altName: FormField,
    price: FormField,
    alcoholPercentage: FormField,
    photo: FormField,
    clearFields: () -> Unit,
    addMenu: () -> Unit,
    context: Context
) {
    var showAddDialog by remember { mutableStateOf(false) }

    when {
        showAddDialog -> {
            MenuItemPopup(
                title = { Text(text = stringResource(id = R.string.label_edit_menu_item)) },
                hide = { showAddDialog = false },
                onConfirm = addMenu,
                clear = clearFields,
                name = name,
                altName = altName,
                price = price,
                alcoholPercentage = alcoholPercentage,
                photo = photo,
                context = context
            )
        }
    }

    MyFloatingActionButton(
        onClick = { showAddDialog = true }
    )
}