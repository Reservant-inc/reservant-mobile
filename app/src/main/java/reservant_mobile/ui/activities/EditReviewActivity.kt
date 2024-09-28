package reservant_mobile.ui.activities

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.ui.components.ButtonComponent
import reservant_mobile.ui.components.FormInput
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.navigation.RestaurantRoutes
import reservant_mobile.ui.viewmodels.ReviewsViewModel

@Composable
fun EditReviewActivity(reviewId: Int, restaurantId: Int, navController: NavHostController) {
    val reviewsViewModel = viewModel<ReviewsViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ReviewsViewModel(restaurantId) as T
        }
    )

    var stars by remember { mutableStateOf(0) }
    var contents by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        IconWithHeader(
            icon = Icons.Rounded.Edit,
            text = stringResource(id = R.string.label_edit_review),
            showBackButton = true,
            onReturnClick = { navController.navigate(RestaurantRoutes.Details(restaurantId = restaurantId)) }
        )

        Text(
            text = stringResource(id = R.string.label_rating),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= stars) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = "$i Star",
                    tint = if (i <= stars) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { stars = i }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        FormInput(
            inputText = contents,
            onValueChange = { contents = it },
            label = stringResource(id = R.string.label_review_content),
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            errorText = stringResource(id = R.string.error_duplicate_review)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ButtonComponent(
                onClick = {
                    if (contents.isNotBlank()) {
                        reviewsViewModel.viewModelScope.launch {
                            reviewsViewModel.editReview(reviewId, stars, contents)
                            if (reviewsViewModel.result.isError) {
                                isError = true
                            } else {
                                isError = false
                                navController.popBackStack()
                            }
                        }
                    }
                },
                label = stringResource(id = R.string.label_edit_review),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            ButtonComponent(
                onClick = {
                    reviewsViewModel.viewModelScope.launch {
                        reviewsViewModel.deleteReview(reviewId)
                        if (!reviewsViewModel.result.isError) {
                            navController.popBackStack()
                        }
                    }
                },
                label = stringResource(id = R.string.label_delete_review),
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Delete
            )
        }
    }
}
