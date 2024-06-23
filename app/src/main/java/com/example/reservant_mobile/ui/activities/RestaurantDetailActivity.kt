
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.constants.Roles
import com.example.reservant_mobile.data.models.dtos.RestaurantMenuItemDTO
import com.example.reservant_mobile.ui.components.FloatingTabSwitch
import com.example.reservant_mobile.ui.components.FullscreenGallery
import com.example.reservant_mobile.ui.components.ImageCard
import com.example.reservant_mobile.ui.components.MenuItemCard
import com.example.reservant_mobile.ui.components.MissingPage
import com.example.reservant_mobile.ui.components.RatingBar
import com.example.reservant_mobile.ui.components.ShowErrorToast
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
                                stringResource(R.string.label_events) to { EventsContent() }
                            )
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