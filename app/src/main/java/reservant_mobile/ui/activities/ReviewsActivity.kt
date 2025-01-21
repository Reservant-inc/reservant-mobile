package reservant_mobile.ui.activities

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.models.dtos.ReviewDTO
import reservant_mobile.data.models.dtos.UserSummaryDTO
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.LoadedPhotoComponent
import reservant_mobile.ui.components.SearchBarWithFilter
import reservant_mobile.ui.viewmodels.ReviewsViewModel

@Composable
fun ReviewsActivity(
    navController: NavController = rememberNavController(),
    restaurantId: Int,
    isOwner: Boolean
) {
    val reviewsViewModel: ReviewsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ReviewsViewModel(restaurantId) as T
        }
    )

    val lazyReviews = reviewsViewModel.reviewsFlow.collectAsState(initial = null).value?.collectAsLazyPagingItems()

    var searchQuery by remember { mutableStateOf("") }
    var currentFilterInt by remember { mutableStateOf<Int?>(null) }

    val filterOptionsInt = listOf(5,4,3,2,1)

    LaunchedEffect(Unit) {
        reviewsViewModel.fetchReviews()
    }

    Scaffold(
        topBar = {
            IconWithHeader(
                icon = Icons.Default.Star,
                text = stringResource(R.string.label_reviews),
                showBackButton = true,
                onReturnClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            SearchBarWithFilter(
                searchQuery = searchQuery,
                onSearchQueryChange = { query -> searchQuery = query },
                onFilterSelectedInt = { filterInt -> currentFilterInt = filterInt },
                currentFilterInt = currentFilterInt,
                filterOptionsInt = filterOptionsInt
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (lazyReviews == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val filteredReviews = lazyReviews.itemSnapshotList.items.filter { review ->
                    val contentMatchesQuery = review.contents.contains(searchQuery, ignoreCase = true)
                    val usernameMatchesQuery = review.authorFullName?.contains(searchQuery, ignoreCase = true) ?: false
                    val matchesFilter = currentFilterInt == null || review.stars == currentFilterInt
                    (contentMatchesQuery || usernameMatchesQuery) && matchesFilter
                }

                if (filteredReviews.isNotEmpty()) {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        filteredReviews.forEach { review ->

                            var userDto: UserSummaryDTO? by remember { mutableStateOf(null) }

                            LaunchedEffect(review.authorId) {
                                review.authorId?.let {
                                    userDto = reviewsViewModel.getUser(review.authorId)
                                }
                            }

                            ReviewCardWithReply(
                                review = review,
                                isOwner = isOwner,
                                reviewsViewModel = reviewsViewModel,
                                userDto = userDto,
                                onReplySubmitted = { replyContent ->
                                    reviewsViewModel.viewModelScope.launch {
                                        reviewsViewModel.postReply(reviewId = review.reviewId!!, replyContent = replyContent)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                } else {
                    Text(
                        text = stringResource(id = R.string.label_no_reviews),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewCardWithReply(
    review: ReviewDTO,
    isOwner: Boolean,
    userDto: UserSummaryDTO?,
    reviewsViewModel: ReviewsViewModel,
    onReplySubmitted: (String) -> Unit
) {
    var showReplyField by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LoadedPhotoComponent(
                    photoModifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    placeholderModifier = Modifier.size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    placeholder = R.drawable.unknown_profile_photo
                ) {
                    if(userDto != null){
                        userDto.photo?.let { photo ->
                            reviewsViewModel.getPhoto(photo)
                        }
                    }else{
                        null
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = review.authorFullName ?: "Gall Anonim",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = review.contents,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                repeat(review.stars) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                repeat(5 - review.stars) {
                    Icon(
                        imageVector = Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd) {
                    Text(
                        text = review.createdAt?.let { formatToDateTime(it, "dd.MM.yyyy") } ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            review.restaurantResponse?.let { reply ->
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = stringResource(id = R.string.label_owner_reply),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = reply,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } ?: run {
                if (isOwner) {
                    Spacer(modifier = Modifier.height(8.dp))
                    if (!showReplyField) {
                        TextButton(
                            onClick = { showReplyField = true }
                        ) {
                            Text(text = stringResource(id = R.string.label_reply))
                        }
                    } else {
                        Column {
                            OutlinedTextField(
                                value = replyText,
                                onValueChange = { replyText = it },
                                label = { Text(stringResource(id = R.string.label_write_reply)) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextButton(onClick = { showReplyField = false }) {
                                    Text(text = stringResource(id = R.string.label_cancel))
                                }
                                Button(
                                    onClick = {
                                        if (replyText.isNotBlank()) {
                                            onReplySubmitted(replyText)
                                            showReplyField = false
                                        }
                                    }
                                ) {
                                    Text(text = stringResource(id = R.string.label_send))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
