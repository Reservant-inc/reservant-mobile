package reservant_mobile.ui.activities

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.RestaurantGroupDTO
import reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import reservant_mobile.data.models.dtos.ReviewDTO
import reservant_mobile.data.services.UserService
import reservant_mobile.data.utils.DefaultResourceProvider
import reservant_mobile.data.utils.formatToDateTime
import reservant_mobile.ui.components.BadgeFloatingButton
import reservant_mobile.ui.components.ButtonComponent
import reservant_mobile.ui.components.CartItemCard
import reservant_mobile.ui.components.EventCard
import reservant_mobile.ui.components.EventsContent
import reservant_mobile.ui.components.FloatingTabSwitch
import reservant_mobile.ui.components.FullscreenGallery
import reservant_mobile.ui.components.FullscreenPhoto
import reservant_mobile.ui.components.ImageCard
import reservant_mobile.ui.components.LoadedPhotoComponent
import reservant_mobile.ui.components.MenuContent
import reservant_mobile.ui.components.MissingPage
import reservant_mobile.ui.components.OpeningHours
import reservant_mobile.ui.components.RatingBar
import reservant_mobile.ui.components.SearchBarWithFilter
import reservant_mobile.ui.components.ShowErrorToast
import reservant_mobile.ui.components.TagItem
import reservant_mobile.ui.navigation.EventRoutes
import reservant_mobile.ui.navigation.RestaurantRoutes
import reservant_mobile.ui.viewmodels.ReservationViewModel
import reservant_mobile.ui.viewmodels.RestaurantDetailViewModel
import reservant_mobile.ui.viewmodels.ReviewsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailActivity(
    restaurantId: Int = 1,
    onReturnClick: () -> Unit
) {

    val Context = LocalContext.current

    val navController = rememberNavController()

    val restaurantDetailVM = viewModel<RestaurantDetailViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                RestaurantDetailViewModel(restaurantId) as T
        }
    )

    val events by rememberUpdatedState(newValue = restaurantDetailVM.events.collectAsLazyPagingItems())

    val reviewsViewModel: ReviewsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReviewsViewModel(restaurantId = restaurantId) as T
            }
        }
    )

    val reservationViewModel: ReservationViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReservationViewModel(resourceProvider = DefaultResourceProvider(Context)) as T
            }
        }
    )

    val reviewsFlow =
        reviewsViewModel.reviewsFlow.collectAsState().value?.collectAsLazyPagingItems()


    var isCartVisible by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = RestaurantRoutes.Details(restaurantId)
    ) {
        composable<RestaurantRoutes.Details> {
            var showGallery by remember { mutableStateOf(false) }
            var showPhoto by remember { mutableStateOf(false) }
            var selectedPhoto: Bitmap? by remember { mutableStateOf(null) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 4.dp)
            ) {

                if (restaurantDetailVM.resultRestaurant.isError) {
                    ShowErrorToast(
                        context = LocalContext.current,
                        id = restaurantDetailVM.getToastError()
                    )
                }

                when {
                    restaurantDetailVM.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    restaurantDetailVM.restaurant != null && restaurantDetailVM.menus != null -> {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            ) {
                                restaurantDetailVM.restaurant?.let { restaurant ->

                                    LoadedPhotoComponent(
                                        photoModifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        placeholderModifier = Modifier.align(Alignment.Center),
                                        getPhoto = {
                                            restaurant.logo?.let { logo ->
                                                restaurantDetailVM.getPhoto(logo)
                                            }
                                        }
                                    )

                                    IconButton(
                                        onClick = onReturnClick,
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .align(Alignment.TopStart)
                                            .background(Color.White, CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = stringResource(R.string.label_back),
                                            tint = Color.Black
                                        )
                                    }
                                }
                            }

                            restaurantDetailVM.restaurant?.let { restaurant ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = restaurant.name,
                                        style = MaterialTheme.typography.headlineMedium,
                                        modifier = Modifier.weight(1f)
                                    )

                                }

                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                ) {
                                    val reviews = stringResource(R.string.label_detail_reviews)
                                    restaurant.rating?.let { it1 -> RatingBar(rating = it1.toFloat()) }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "${
                                            restaurant.rating.toString().substring(0, 3)
                                        } (${restaurant.numberReviews} $reviews)"
                                    )
                                }


                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .scale(0.9f)
                                        .horizontalScroll(rememberScrollState())
                                ) {
                                    for (tag in restaurant.tags) {
                                        TagItem(
                                            tag = tag,
                                            removable = false
                                        )
                                    }
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                ) {
                                    Text(
                                        text = restaurant.restaurantType,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    Text(
                                        text = stringResource(R.string.label_restaurant_address) + ": ${restaurant.address}",
                                        style = MaterialTheme.typography.bodySmall
                                    )

                                    restaurant.openingHours?.let {

                                        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

                                        OpeningHours(openingHours = restaurant.openingHours)

                                        Spacer(modifier = Modifier.width(8.dp))

                                        ButtonComponent(
                                            label = stringResource(id = R.string.reservation_make_reservation),
                                            onClick = {
                                            navController.navigate(
                                                RestaurantRoutes.Reservation(
                                                    restaurantId = restaurantId,
                                                    isReservation = true
                                                )
                                            )
                                        })
                                    }
                                }



                                if (restaurant.photos.isNotEmpty()) {
                                    var images by remember { mutableStateOf<List<Bitmap>>(emptyList()) }

                                    LaunchedEffect(restaurant.photos) {
                                        val loadedImages = restaurantDetailVM.getPhotos(
                                            restaurant.photos,
                                            limit = 5
                                        )
                                        images = loadedImages
                                    }

                                    Text(
                                        text = stringResource(R.string.label_gallery),
                                        style = MaterialTheme.typography.headlineMedium,
                                        modifier = Modifier.padding(16.dp)
                                    )

                                    if (restaurantDetailVM.isGalleryLoading) {
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    } else {

                                        Row(
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp)
                                                .horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            images.take(4).forEach { img ->
                                                ImageCard(
                                                    image = img.asImageBitmap(),
                                                    onClick = {
                                                        selectedPhoto = img
                                                        showPhoto = true
                                                    }
                                                )
                                            }
                                            if (images.size > 4) {
                                                Card(
                                                    modifier = Modifier
                                                        .size(100.dp)
                                                        .clickable { showGallery = true },
                                                    shape = RoundedCornerShape(16.dp),
                                                    elevation = CardDefaults.cardElevation(8.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .background(Color.Black.copy(alpha = 0.8f))
                                                    ) {
                                                        Image(
                                                            bitmap = images.last().asImageBitmap(),
                                                            contentDescription = null,
                                                            modifier = Modifier.fillMaxSize(),
                                                            contentScale = ContentScale.Crop,
                                                            alpha = 0.35f
                                                        )
                                                        Text(
                                                            text = stringResource(R.string.label_more),
                                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                            style = MaterialTheme.typography.headlineSmall,
                                                            modifier = Modifier.align(Alignment.Center)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                FloatingTabSwitch(
                                    pages = listOf(
                                        stringResource(R.string.label_menu) to {
                                            MenuContent(
                                                restaurantDetailVM.menus!!,
                                                onMenuClick = { menuId ->
                                                    restaurantDetailVM.viewModelScope.launch {
                                                        restaurantDetailVM.loadFullMenu(menuId)
                                                    }
                                                },
                                                menuItems = restaurantDetailVM.currentMenu?.menuItems,
                                                getMenuPhoto = { photoString ->
                                                    restaurantDetailVM.getPhoto(photoString)
                                                },
                                                onAddClick = { menuItem ->
                                                    // Add item to cart with quantity
                                                    val existingItem = reservationViewModel.addedItems.find { it.first.menuItemId == menuItem.menuItemId }
                                                    if (existingItem != null) {
                                                        val index = reservationViewModel.addedItems.indexOf(existingItem)
                                                        reservationViewModel.addedItems[index] = existingItem.copy(second = existingItem.second + 1)
                                                    } else {
                                                        reservationViewModel.addedItems.add(menuItem to 1)
                                                    }
                                                }
                                            )
                                        },
                                        stringResource(R.string.label_events) to {
                                            EventsContent(
                                                eventsFlow = events,
                                                restaurantDetailVM = restaurantDetailVM,
                                                navController = navController
                                            )
                                        },
                                        stringResource(R.string.label_reviews) to {
                                            ReviewsContent(
                                                reviewsFlow,
                                                navController,
                                                restaurantId,
                                                reviewsViewModel
                                            )
                                        }
                                    ),
                                    paneScroll = false
                                )

                            }
                        }
                    }

                    else -> {
                        MissingPage(errorStringId = R.string.error_not_found)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                BadgeFloatingButton(
                    icon = Icons.Default.ShoppingBag,
                    contentDescription = stringResource(id = R.string.cart),
                    itemCount = reservationViewModel.addedItems.sumOf { it.second },
                    onClick = { isCartVisible = true }
                )
            }

            var images by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
            if (showGallery) {
                if (images.isEmpty()) {
                    LaunchedEffect(restaurantDetailVM.restaurant) {
                        val loadedImages = restaurantDetailVM.restaurant?.let { it1 ->
                            restaurantDetailVM.getPhotos(
                                it1.photos,
                                withLoading = false
                            )
                        }
                        images = loadedImages.orEmpty()
                    }
                }
                FullscreenGallery(
                    onDismiss = { showGallery = false },
                    onPhotoClick = {
                        selectedPhoto = it
                        showPhoto = true
                    },
                    bitmaps = images
                )

            }
            if (showPhoto) {
                FullscreenPhoto(onDismiss = { showPhoto = false }, bitmap = selectedPhoto)
            }
            if (isCartVisible) {
                ModalBottomSheet(
                    onDismissRequest = { isCartVisible = false }
                ) {
                    CartContent(
                        cartItems = reservationViewModel.addedItems,
                        onRemoveItem = { item -> reservationViewModel.removeItemFromCart(item)},
                        onIncreaseQuantity = { item -> reservationViewModel.increaseItemQuantity(item)
                        },
                        onDecreaseQuantity = { item -> reservationViewModel.decreaseItemQuantity(item)
                        },
                        onSubmitOrder = {
                            // Proceed to Reservation screen
                            navController.navigate(RestaurantRoutes.Reservation(restaurantId = restaurantId, isReservation = false))
                            isCartVisible = false
                        },
                        getMenuPhoto = { photoString ->
                            restaurantDetailVM.getPhoto(photoString)
                        },
                    )
                }
            }
        }
        composable<RestaurantRoutes.Reservation> {
            RestaurantReservationActivity(
                restaurantId = it.toRoute<RestaurantRoutes.Reservation>().restaurantId,
                navController = navController,
                reservationViewModel = reservationViewModel,
                restaurantDetailVM = restaurantDetailVM,
                isReservation = it.toRoute<RestaurantRoutes.Reservation>().isReservation)
        }

        composable<RestaurantRoutes.AddReview> {
            AddReviewActivity(
                restaurantId = it.toRoute<RestaurantRoutes.AddReview>().restaurantId,
                navController = navController
            )
        }

        composable<RestaurantRoutes.EditReview> {
            EditReviewActivity(
                restaurantId = it.toRoute<RestaurantRoutes.AddReview>().restaurantId,
                reviewId = it.toRoute<RestaurantRoutes.EditReview>().reviewId,
                navController = navController
            )
        }
        composable<EventRoutes.Details> {
            EventDetailActivity(
                eventId = it.toRoute<EventRoutes.Details>().eventId,
                navController = navController
            )
        }
    }

}

@Composable
fun CartContent(
    cartItems: List<Pair<RestaurantMenuItemDTO, Int>>,
    onRemoveItem: (Pair<RestaurantMenuItemDTO, Int>) -> Unit,
    onIncreaseQuantity: (Pair<RestaurantMenuItemDTO, Int>) -> Unit,
    onDecreaseQuantity: (Pair<RestaurantMenuItemDTO, Int>) -> Unit,
    onSubmitOrder: () -> Unit,
    getMenuPhoto: suspend (String) -> Bitmap?,
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
                var menuPhoto by remember { mutableStateOf<Bitmap?>(null) }

                LaunchedEffect(item.first.photo) {
                    item.first.photo?.let { photo ->
                        menuPhoto = getMenuPhoto(photo)
                    }
                }
                CartItemCard(
                    item = item,
                    onIncreaseQuantity = { onIncreaseQuantity(item) },
                    onDecreaseQuantity = { onDecreaseQuantity(item) },
                    onRemove = { onRemoveItem(item) },
                    photo = menuPhoto
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (cartItems.isNotEmpty()) {
            ButtonComponent(
                modifier = Modifier.fillMaxWidth(),
                onClick = onSubmitOrder,
                label = stringResource(id = R.string.proceed_to_checkout)
            )
        }
    }
}


@Composable
fun ReviewsContent(
    reviewsFlow: LazyPagingItems<ReviewDTO>?,
    navController: NavController,
    restaurantId: Int,
    reviewsViewModel: ReviewsViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var currentFilterInt by remember { mutableStateOf<Int?>(null) }

    // Fetch reviews on screen enter
    LaunchedEffect(Unit) {
        reviewsViewModel.fetchReviews()
    }

    // Filter options for stars (integer values for the dropdown menu)
    val filterOptionsInt = listOf(5, 4, 3, 2, 1)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp, start = 24.dp, end = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        // Search Bar with Filter
        SearchBarWithFilter(
            searchQuery = searchQuery,
            onSearchQueryChange = { query -> searchQuery = query },
            onFilterSelectedInt = { filterInt -> currentFilterInt = filterInt },
            currentFilterInt = currentFilterInt,
            filterOptionsInt = filterOptionsInt,
            additionalButtonOnClick = {
                navController.navigate(RestaurantRoutes.AddReview(restaurantId = restaurantId))
            },
            additionalButtonIcon = Icons.Default.Add
        )

        Spacer(modifier = Modifier.height(16.dp))

        reviewsFlow?.let { lazyPagingItems ->
            val filteredReviews = lazyPagingItems.itemSnapshotList.items.filter { review ->
                val matchesQuery = review.contents.contains(searchQuery, ignoreCase = true)
                val matchesFilter = currentFilterInt == null || review.stars == currentFilterInt
                matchesQuery && matchesFilter
            }

            if (filteredReviews.isNotEmpty()) {
                Column {
                    filteredReviews.forEach { review ->
                        ReviewCard(
                            review = review,
                            onClick = {
                                if (review.authorId == UserService.UserObject.userId) {
                                    navController.navigate(
                                        RestaurantRoutes.EditReview(
                                            restaurantId = restaurantId,
                                            reviewId = review.reviewId!!
                                        )
                                    )
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
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(id = R.string.label_loading_reviews))
            }
        }
    }
}


@Composable
fun ReviewCard(review: ReviewDTO, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.ic_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Gray, CircleShape)
                        .padding(8.dp)
                )
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
        }
    }
}


@Composable
fun FilterOptionWithStars(stars: Int) {
    Row {
        repeat(stars) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )
        }

        repeat(5 - stars) {
            Icon(
                imageVector = Icons.Default.StarBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}