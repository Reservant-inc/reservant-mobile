package reservant_mobile.ui.activities

import WarehouseViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.reservant_mobile.R
import reservant_mobile.data.models.dtos.IngredientDTO
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.IconWithHeader

@Composable
fun IngredientDetailsActivity(
    ingredient: IngredientDTO,
    onReturnClick: () -> Unit,
    warehouseViewModel: WarehouseViewModel = viewModel()
) {
    LaunchedEffect(ingredient) {
        warehouseViewModel.loadIngredientHistory(ingredient.ingredientId ?: 0)
    }

    val ingredientHistoryFlow = warehouseViewModel.ingredientHistoryFlow
    val ingredientHistoryItems = ingredientHistoryFlow?.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconWithHeader(
            icon = Icons.Default.History,
            text = ingredient.publicName ?: stringResource(id = R.string.no_name),
            showBackButton = true,
            onReturnClick = onReturnClick
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(
                id = R.string.quantity_colon,
                "${ingredient.amount ?: 0.0} ${ingredient.unitOfMeasurement?.name ?: ""}"
            ),
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (ingredientHistoryItems != null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                val itemCount = ingredientHistoryItems.itemCount
                items(itemCount) { index ->
                    val correction = ingredientHistoryItems[index]
                    if (correction != null) {
                        CorrectionCard(
                            correction = correction
                        )
                    }
                }
                ingredientHistoryItems.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item {
                                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                            }
                        }
                        loadState.append is LoadState.Loading -> {
                            item {
                                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                            }
                        }
                        loadState.refresh is LoadState.Error -> {
                            val e = ingredientHistoryItems.loadState.refresh as LoadState.Error
                            item {
                                Text(
                                    text = stringResource(id = R.string.error_loading_ingredient, e.error.localizedMessage ?: ""),
                                    color = Color.Red,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun CorrectionCard(
    correction: IngredientDTO.CorrectionDTO,
    onClickDetail: () -> Unit = {}
) {
    val changeAmount = correction.newAmount - (correction.oldAmount ?: 0.0)
    val amountText = "${if (changeAmount > 0) "+" else ""}${changeAmount}g"
    val color = when {
        changeAmount > 0 -> Color(0xFF4CAF50)
        correction.comment != null -> Color.Red
        else -> Color.Yellow
    }

    val actionType = when {
        changeAmount > 0 -> stringResource(R.string.delivery)
        correction.comment != null -> stringResource(R.string.manual_change)
        else -> stringResource(R.string.order)
    }

    val formattedDate = formatToDateTime(correction.correctionDate ?: "", "dd.MM.yyyy HH:mm")

    val textStyle = TextStyle(
        color = color,
        fontWeight = FontWeight.Bold,
        textDecoration = if (actionType == stringResource(R.string.order)) TextDecoration.Underline else null
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(enabled = actionType == stringResource(R.string.order)) { onClickDetail() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = amountText,
                    style = textStyle,
                    fontSize = 20.sp
                )
                Text(
                    text = actionType,
                    style = textStyle.copy(fontSize = 16.sp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = correction.comment ?: "",
                style = TextStyle(color = color, fontSize = 14.sp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.ingredient_old_amount, correction.oldAmount ?: 0.0),
                    style = TextStyle(fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                )
                Text(
                    text = stringResource(R.string.ingredient_new_amount, correction.newAmount),
                    style = TextStyle(fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            correction.user?.let { user ->
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    style = TextStyle(fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formattedDate,
                style = TextStyle(fontSize = 12.sp, color = Color.Gray)
            )
        }
    }
}
