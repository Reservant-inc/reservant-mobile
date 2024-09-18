package reservant_mobile.ui.activities

import android.graphics.Bitmap
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.reservant_mobile.R
import org.osmdroid.util.GeoPoint
import reservant_mobile.ui.components.ButtonComponent
import reservant_mobile.ui.components.FloatingTabSwitch
import reservant_mobile.ui.components.ImageCard
import reservant_mobile.ui.components.MissingPage
import reservant_mobile.ui.components.OsmMapView
import reservant_mobile.ui.components.RatingBar
import reservant_mobile.ui.components.RestaurantCard
import reservant_mobile.ui.components.ShowErrorToast
import reservant_mobile.ui.navigation.RestaurantRoutes
import reservant_mobile.ui.viewmodels.MapViewModel
import reservant_mobile.ui.viewmodels.RestaurantDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapActivity(){

    val navController = rememberNavController()


    NavHost(navController = navController, startDestination = RestaurantRoutes.Map){
        composable<RestaurantRoutes.Map> {
            val mapViewModel = viewModel<MapViewModel>()
            var showRestaurantBottomSheet by remember { mutableStateOf(false) }
            var showRestaurantId by remember { mutableIntStateOf(0) }
            val restaurants by rememberUpdatedState(newValue = mapViewModel.restaurants.collectAsLazyPagingItems())
            val events by rememberUpdatedState(newValue = mapViewModel.events.collectAsLazyPagingItems())


            // Init map
            val context = LocalContext.current
            val startPoint = GeoPoint(52.237049, 21.017532)
            val mv = mapViewModel.initMapView(context, startPoint)

            if (showRestaurantBottomSheet) {
                RestaurantDetailPreview(navController, showRestaurantId) {
                    showRestaurantBottomSheet = false
                }
            }


            val pages: List<Pair<String, @Composable () -> Unit>> = listOf(
                stringResource(id = R.string.label_restaurants) to {
                    if(restaurants.itemCount <= 0){
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    else {
                        LazyColumn(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 75.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(restaurants.itemCount) { index ->
                                val item = restaurants[index]
                                if(item != null){
                                    RestaurantCard(
                                        onClick = { navController.navigate(RestaurantRoutes.Details(restaurantId = item.restaurantId)) },
                                        name = item.name,
                                        location = item.address,
                                        city = item.city,
                                        image = item.logo?.asImageBitmap()
                                    )
                                        mapViewModel.addRestaurantMarker(item) { _, _ ->
                                            showRestaurantId = item.restaurantId
                                            showRestaurantBottomSheet = true
                                            true
                                        }

                                }

                            }
                        }
                    }
                },
                stringResource(id = R.string.label_events) to {
                    if(events.itemCount <= 0){
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    else {
                        if (events.itemCount < 1){
                            LaunchedEffect(key1 = true) {
                                mapViewModel.getEvents()
                            }
                        }
                        else
                            LazyColumn(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 75.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                items(events.itemCount) { index ->
                                    val item = events[index]
                                    if(item != null)
                                        /*RestaurantCard(
                                            onClick = { navController.navigate(RestaurantRoutes.Details(restaurantId = item.restaurantId)) },
                                            name = item.restaurantName!!,
                                            location = item.description,
                                            city = item.mustJoinUntil
                                        )*/
                                        Text(text = item.description)
                                }
                            }
                    }
                }
            )

            BottomSheetScaffold(
                scaffoldState = rememberBottomSheetScaffoldState(),
                sheetPeekHeight = 60.dp,
                sheetContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                sheetContent = {
                    Box(
                        modifier = Modifier
                            .height(550.dp)
                            .fillMaxWidth()
                    ){

                        FloatingTabSwitch(
                            pages = pages,
                            color = MaterialTheme.colorScheme.surface)
                    }
                },
                content = { innerPadding -> OsmMapView(mv, startPoint,
                    Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
                },
                contentColor = MaterialTheme.colorScheme.surface
            )
        }
        composable<RestaurantRoutes.Details> {
            RestaurantDetailActivity(restaurantId = it.toRoute<RestaurantRoutes.Details>().restaurantId)
        }
        composable<RestaurantRoutes.Reservation>{
            RestaurantReservationActivity(navController = navController)
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailPreview(
    navController: NavHostController,
    restaurantId: Int,
    onDismiss: () -> Unit
){
    val modalBottomSheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = { onDismiss()},
        sheetState = modalBottomSheetState,
        modifier = Modifier.height(450.dp)
    ) {
        val restaurantDetailVM = viewModel<RestaurantDetailViewModel>(
            factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    RestaurantDetailViewModel(restaurantId) as T
            }
        )

        LaunchedEffect(key1 = true) {
            restaurantDetailVM.loadRestaurant(restaurantId)
        }

        Box(modifier = Modifier.fillMaxSize()) {

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
                restaurantDetailVM.restaurant != null -> {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        restaurantDetailVM.restaurant?.let { restaurant ->

                            if(restaurant.photos.size > 1){
                                var images by remember { mutableStateOf<List<Bitmap>>(emptyList()) }

                                LaunchedEffect(restaurant.photos) {
                                    val loadedImages = restaurantDetailVM.getPhotos(restaurant.photos, limit = 6)
                                    images = loadedImages
                                }

                                if (restaurantDetailVM.isGalleryLoading){
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                                else {
                                    Row(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        images.forEach { img ->
                                            ImageCard(
                                                image = img.asImageBitmap()
                                            )
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier.width(220.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = restaurant.name,
                                        style = MaterialTheme.typography.headlineSmall,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )

                                    Text(
                                        text = restaurant.restaurantType,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
                                    )

                                    Row(
                                        modifier = Modifier.padding(bottom = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        Icon(
                                            modifier = Modifier
                                                .padding(start = 16.dp),
                                            imageVector = Icons.Rounded.LocationOn,
                                            contentDescription = "Restaurant location"

                                        )
                                        Text(
                                            text = "${restaurant.city}\n${restaurant.address}",
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }


                                    Text(
                                        text = stringResource(R.string.label_delivery_cost) + ": 5,70zł",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }

                                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                    RatingBar(rating = 3.9f)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("3.9 (200+ opinii)")
                                }
                            }

                            ButtonComponent(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .wrapContentHeight(align = Alignment.CenterVertically),
                                onClick = {
                                    onDismiss()
                                    navController.navigate(RestaurantRoutes.Details(restaurantId =  restaurant.restaurantId))
                                },

                                label = stringResource(id = R.string.label_show_more_details)
                            )
                        }
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        MissingPage(errorStringId = R.string.error_not_found)
                    }
                }
            }
        }
    }
}