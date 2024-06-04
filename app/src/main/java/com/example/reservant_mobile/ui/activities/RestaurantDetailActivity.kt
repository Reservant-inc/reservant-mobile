import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.constants.Roles
import com.example.reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import com.example.reservant_mobile.ui.components.FloatingTabSwitch
import com.example.reservant_mobile.ui.components.FullscreenGallery
import com.example.reservant_mobile.ui.components.ImageCard
import com.example.reservant_mobile.ui.components.MenuItemCard
import com.example.reservant_mobile.ui.components.MissingPage
import com.example.reservant_mobile.ui.components.MenuTypeButton
import com.example.reservant_mobile.ui.components.RatingBar
import com.example.reservant_mobile.ui.components.ShowErrorToast
import com.example.reservant_mobile.ui.components.TagItem
import com.example.reservant_mobile.ui.viewmodels.RestaurantDetailViewModel


@Composable
fun RestaurantDetailActivity(restaurantId: Int) {
    val restaurantDetailVM = viewModel<RestaurantDetailViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                RestaurantDetailViewModel(restaurantId) as T
        }
    )

    var showGallery by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().padding(bottom = 4.dp)) {

        if (restaurantDetailVM.result.isError) {
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
            restaurantDetailVM.restaurant != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    restaurantDetailVM.restaurant?.let { restaurant ->
                        Image(
                            painter = painterResource(R.drawable.restaurant_photo),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )

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
                            RatingBar(rating = 3.9f)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("3.9 (200+ opinii)")
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
                                stringResource(R.string.label_menu) to { MenuContent() },
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

    if (showGallery) {
        FullscreenGallery(onDismiss = { showGallery = false })
    }
}


// TODO: MenuItemDTO
@Composable
fun MenuContent(
//    menuItems: List<RestaurantMenuItemDTO>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            //.verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        
        // Sample data to demonstrate the use of MenuItemCard
        val sampleMenuItems = listOf(
            RestaurantMenuItemDTO(
                menuItemId = 1,
                restaurantId = 1,
                price = 15.0,
                name = "Position name 11111111111",
                alternateName = null,
                alcoholPercentage = null,
                photo = "imageResource"
            ),
            RestaurantMenuItemDTO(
                menuItemId = 2,
                restaurantId = 1,
                price = 20.0,
                name = "Position name 2222",
                alternateName = null,
                alcoholPercentage = 5.0,
                photo = "imageResource"
            ),
            RestaurantMenuItemDTO(
                menuItemId = 3,
                restaurantId = 1,
                price = 25.0,
                name = "Position name 3",
                alternateName = null,
                alcoholPercentage = null,
                photo = "imageResource"
            )
        )

        sampleMenuItems.forEach { menuItem ->
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ){
            repeat(5){
                MenuTypeButton(
                    menuType = "Menu",
                    onClick = {}
                )
            }
        }

        repeat(3){
            MenuItemCard(
                menuItem = menuItem,
                role = Roles.CUSTOMER,
                onInfoClick = { /* TODO: Handle info */ },
                onAddClick = { /* TODO: Handle add */ }
            )
        }

//        menuItems.forEach { menuItem ->
//            MenuItemCard(
//                menuItem = menuItem,
//                role = MenuItemCardRole.DETAIL,
//                onInfoClick = { /* TODO: Handle info */ },
//                onAddClick = { /* TODO: Handle add */ }
//            )
//        }
    }
}

// TODO: EventDTO
@Composable
fun EventsContent(
//    events: List<EventDTO>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        repeat(3){
            Text("Event name", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Event details")
            Spacer(modifier = Modifier.height(16.dp))
        }

//        events.forEach { event ->
//            Text(event.name, style = MaterialTheme.typography.headlineSmall)
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(event.details)
//            Spacer(modifier = Modifier.height(16.dp))
//        }
    }
}

@Composable
fun ReviewsContent(
    reviews: List<ReviewDTO> = sampleReviews()
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp, start = 24.dp, end = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        SearchBarWithFilter()
        Spacer(modifier = Modifier.height(16.dp))

        reviews.forEach { review ->
            ReviewCard(review)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// TODO: verify colors
@Composable
fun SearchBarWithFilter() {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 36.dp),
                singleLine = true,
                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp)
            )
            if (text.isEmpty()) {
                Text(
                    text = "Szukaj...",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
        IconButton(
            onClick = { /* TODO: Handle filter action */ },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter",
                tint = MaterialTheme.colorScheme.secondary
            )
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