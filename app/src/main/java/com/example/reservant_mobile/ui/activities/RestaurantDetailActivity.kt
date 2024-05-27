import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.reservant_mobile.R

import com.example.reservant_mobile.ui.components.FloatingTabSwitch
import com.example.reservant_mobile.ui.components.FullscreenGallery
import com.example.reservant_mobile.ui.components.MenuItemCard
import com.example.reservant_mobile.ui.components.RatingBar
import com.example.reservant_mobile.ui.theme.secondaryLight
import com.example.reservant_mobile.ui.viewmodels.RestaurantDetailViewModel


@Composable
fun RestaurantDetailActivity(navControllerHome: NavHostController, restaurantId: Int) {
    val restaurantDetailVM: RestaurantDetailViewModel = viewModel()
    val restaurant = restaurantDetailVM.restaurant
    val isLoading = restaurantDetailVM.isLoading
    val errorMessage = restaurantDetailVM.errorMessage

    var showGallery by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(restaurantId) {
        restaurantDetailVM.loadRestaurant(restaurantId)
    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            restaurant != null -> {
                restaurant?.let { restaurant ->
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
                                tint = if (isFavorite) secondaryLight else Color.Gray
                            )
                        }
                    }

                    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                        RatingBar(rating = 3.9f)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("3.9 (200+ opinii)")
                    }

                    Text(
                        text = restaurant.restaurantType,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    Text(
                        text = stringResource(R.string.label_restaurant_address) + ": ${restaurant.address}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Text(
                        text = stringResource(R.string.label_delivery_cost) + " 5,70zł",
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
                            Card(
                                modifier = Modifier.size(100.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(8.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.restaurant_photo),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
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
                                    color = Color.White,
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }

                    FloatingTabSwitch(
                        pages = listOf(
                            stringResource(R.string.label_menu) to { MenuContent() },
                            stringResource(R.string.label_events) to { EventsContent() }
                        )
                    )
                }
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
//    menuItems: List<MenuItemDTO>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        repeat(3){
            MenuItemCard(
                name = "Position name",
                price = stringResource(R.string.label_menu_price) + ": 15zl",
                imageResource = R.drawable.pizza,
                description = "Position description",
                onInfoClick = { /* TODO: Handle info */ },
                onAddClick = { /* TODO: Handle add */ }
            )
        }

//        menuItems.forEach { menuItem ->
//            MenuItemCard(
//                name = menuItem.name,
//                price = stringResource(R.string.label_menu_price) + ": ${menuItem.price}zl",
//                imageResource = R.drawable.pizza, // Change to menuItem.imageResource if available
//                description = menuItem.description,
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