package reservant_mobile.ui.activities
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.models.dtos.RestaurantMenuDTO
import reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import reservant_mobile.ui.components.FloatingTabSwitch
import reservant_mobile.ui.components.FullscreenGallery
import reservant_mobile.ui.components.ImageCard
import reservant_mobile.ui.components.MenuItemCard
import reservant_mobile.ui.components.MenuTypeButton
import reservant_mobile.ui.components.MissingPage
import reservant_mobile.ui.components.RatingBar
import reservant_mobile.ui.components.ShowErrorToast
import reservant_mobile.ui.components.TagItem
import reservant_mobile.ui.navigation.RestaurantRoutes
import reservant_mobile.ui.viewmodels.RestaurantDetailViewModel


@Composable
fun RestaurantDetailActivity(restaurantId: Int = 1) {

    val restaurantDetailVM = viewModel<RestaurantDetailViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                RestaurantDetailViewModel(restaurantId) as T
        }
    )

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = RestaurantRoutes.Details(restaurantId)){
        composable<RestaurantRoutes.Details>{
            var showGallery by remember { mutableStateOf(false) }
            var isFavorite by remember { mutableStateOf(false) }

            Box(modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 4.dp)) {

                if (restaurantDetailVM.resultRestaurant.isError) {
                    ShowErrorToast(context = LocalContext.current, id = restaurantDetailVM.getToastError())
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
                            Box {
                                restaurantDetailVM.restaurant?.let { restaurant ->

                                    var restaurantLogo: Bitmap? by remember { mutableStateOf(null) }
                                    LaunchedEffect(key1 = Unit) {
                                        restaurantLogo = restaurant.logo?.let { logo ->
                                            restaurantDetailVM.getPhoto(
                                                logo
                                            )
                                        }
                                    }
                                    if(restaurantLogo != null){
                                        Image(
                                            bitmap = restaurantLogo!!.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    }else{
                                        Image(
                                            painter = painterResource(R.drawable.unknown_image),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    }


                                    IconButton(
                                        onClick = { navController.popBackStack() },
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
                                    IconButton(
                                        onClick = { isFavorite = !isFavorite },
                                    ) {
                                        Icon(
                                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                            contentDescription = null,
                                            tint = if (isFavorite) MaterialTheme.colorScheme.secondary else LocalContentColor.current
                                        )
                                    }
                                }

                                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                                    val reviews = stringResource(R.string.label_detail_reviews)
                                    RatingBar(rating = 3.9f)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "${restaurant.rating.toString().substring(0,3)} (${restaurant.numberReviews} $reviews)")
                                }

                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .scale(0.9f)
                                ) {
                                    for(tag in restaurant.tags){
                                        TagItem(
                                            tag = tag,
                                            removable = false
                                        )
                                    }
                                }

                                Text(
                                    text = restaurant.restaurantType,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )

                                Text(
                                    text = stringResource(R.string.label_restaurant_address) + ": ${restaurant.address}",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                Text(
                                    text = stringResource(R.string.label_delivery_cost) + ": 5,70zł",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                Text(
                                    text = stringResource(R.string.label_gallery),
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier.padding(16.dp)
                                )

                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    repeat(3) {
                                        ImageCard(
                                            painterResource(R.drawable.pizza)
                                        )
                                    }
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
                                                painter = painterResource(R.drawable.restaurant_photo),
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
                                                }
                                            )
                                        },
                                        stringResource(R.string.label_events) to { EventsContent() },
                                        stringResource(R.string.label_reviews) to { ReviewsContent() }
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
                FloatingActionButton(
                    onClick = { navController.navigate(RestaurantRoutes.Reservation) }
                ) {
                    Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = "Plecak")
                }
            }

            if (showGallery) {
                FullscreenGallery(onDismiss = { showGallery = false })
            }
        }
        composable<RestaurantRoutes.Reservation>{
            RestaurantReservationActivity(navController = navController)
        }
    }

}


@Composable
fun MenuContent(
    menus: List<RestaurantMenuDTO>,
    menuItems: List<RestaurantMenuItemDTO>?,
    onMenuClick: (Int) -> Unit,
    getMenuPhoto: suspend (String) -> Bitmap?
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            for (menu in menus) {
                MenuTypeButton(
                    menuType = menu.name,
                    onMenuClick = { menu.menuId?.let { onMenuClick(it) } }
                )
            }
        }

        menuItems?.forEach { menuItem ->
            var menuPhoto by remember { mutableStateOf<Bitmap?>(null) }

            LaunchedEffect(menuItem.photo) {
                menuItem.photo?.let { photo ->
                    menuPhoto = getMenuPhoto(photo)
                }
            }

            MenuItemCard(
                menuItem = menuItem,
                role = Roles.CUSTOMER,
                photo = menuPhoto,
                onInfoClick = { /* TODO: Handle info */ },
                onAddClick = { /* TODO: Handle add */ }
            )
        }

    }
}

@Composable
fun EventCard(
    eventName: String,
    eventDate: String,
    eventLocation: String,
    interestedCount: Int,
    takePartCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box {
            Column(modifier = Modifier.padding(16.dp)) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(bottom = 16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.restaurant_photo),
                        contentDescription = "Event Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }

                Text(
                    text = eventDate,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = eventName,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = eventLocation,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "$interestedCount - interested")
                    Text(text = "$takePartCount - take part")
                }
            }

        }
    }
}

@Composable
fun EventsContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 80.dp, end = 16.dp, bottom = 16.dp)
    ) {
        repeat(3) {
            EventCard(
                eventName = "Name of event",
                eventDate = "Saturday, 2024-06-22",
                eventLocation = "John's Doe - Warsaw",
                interestedCount = 20,
                takePartCount = 45,
                onClose = { /* Handle close action */ }
            )
            Modifier.padding(bottom = 16.dp)
        }
    }
}


@Composable
fun ReviewsContent(
    reviews: List<ReviewDTO> = sampleReviews(),
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp, start = 24.dp, end = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        //TODO wypełnić zgodnie z możliwymi opcjami filtrowania przykład w OrdersActivity
        //SearchBarWithFilter()
        Spacer(modifier = Modifier.height(16.dp))

        reviews.forEach { review ->
            ReviewCard(review)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ReviewCard(review: ReviewDTO) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp),
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
                    text = review.date,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = review.content,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                val fullStars = review.rating.toInt()
                val halfStar = (review.rating % 1 >= 0.5)

                repeat(fullStars) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (halfStar) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.StarHalf,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                val emptyStars = 5 - fullStars - if (halfStar) 1 else 0
                repeat(emptyStars) {
                    Icon(
                        imageVector = Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

data class ReviewDTO(
    val date: String,
    val content: String,
    val rating: Float
)

fun sampleReviews(): List<ReviewDTO> {
    return listOf(
        ReviewDTO("12/01/2024", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam tincidunt in ipsum vehicula commodo. Etiam tincidunt, odio et ultrices dapibus...", 5.0f),
        ReviewDTO("12/01/2024", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam tincidunt in ipsum vehicula commodo. Etiam tincidunt, odio et ultrices dapibus...", 4.5f),
        ReviewDTO("12/01/2024", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam tincidunt in ipsum vehicula commodo. Etiam tincidunt, odio et ultrices dapibus...", 2.1f)
    )
}