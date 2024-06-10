package com.example.reservant_mobile.ui.activities

import RestaurantDetailActivity
import android.graphics.Bitmap
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.data.services.FileService
import com.example.reservant_mobile.ui.components.ButtonComponent
import com.example.reservant_mobile.ui.components.ImageCard
import com.example.reservant_mobile.ui.components.MissingPage
import com.example.reservant_mobile.ui.components.OsmMapView
import com.example.reservant_mobile.ui.components.RatingBar
import com.example.reservant_mobile.ui.components.RestaurantsBottomSheet
import com.example.reservant_mobile.ui.components.ShowErrorToast
import com.example.reservant_mobile.ui.navigation.RestaurantRoutes
import com.example.reservant_mobile.ui.viewmodels.MapViewModel
import com.example.reservant_mobile.ui.viewmodels.RestaurantDetailViewModel
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

@Composable
fun MapActivity(){

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = RestaurantRoutes.Map){
        composable<RestaurantRoutes.Map> {
            val mapViewModel = viewModel<MapViewModel>()
            var showRestaurantBottomSheet by remember { mutableStateOf(false) }
            var showRestaurantId by remember { mutableIntStateOf(0) }

            // Init map
            val context = LocalContext.current
            val startPoint = GeoPoint(52.237049, 21.017532)
            val mv = mapViewModel.initMapView(context, startPoint)

            if (showRestaurantBottomSheet) {
                RestaurantDetailPreview(navController, showRestaurantId) {
                    showRestaurantBottomSheet = false
                }
            }

            mapViewModel.viewModelScope.launch {
                val img: Bitmap? = FileService().getImage("test-jd.png").value

                mapViewModel.addRestaurantMarker(
                    GeoPoint(52.240055, 21.017532),
                    img,
                    "Restaurant 1" +
                            ""
                ) { _, _ ->
                    showRestaurantId = 1
                    showRestaurantBottomSheet = true
                    true
                }

                mapViewModel.addRestaurantMarker(
                    GeoPoint(52.240055, 21.027532),
                    img,
                    "Restaurant 2" +
                            ""
                ) { _, _ ->
                    showRestaurantId = 2
                    showRestaurantBottomSheet = true
                    true
                }

                mapViewModel.addRestaurantMarker(
                    GeoPoint(52.250055, 21.027532),
                    img,
                    "Restaurant 3" +
                            ""
                ) { _, _ ->
                    showRestaurantId = 3
                    showRestaurantBottomSheet = true
                    true
                }

                mapViewModel.addRestaurantMarker(
                    GeoPoint(52.210055, 21.007532),
                    img,
                    "Restaurant 4" +
                            ""
                ) { _, _ ->
                    showRestaurantId = 4
                    showRestaurantBottomSheet = true
                    true
                }
            }


            val sheetContent = listOf(
                RestaurantDTO(
                    restaurantId = 1,
                    name = "John Doe's Restaurant",
                    address = "ul. Konstruktorska 15",
                    city = "Piaseczno"
                ),
                RestaurantDTO(
                    restaurantId = 2,
                    name = "Maciek's Pizza",
                    address = "ul. Bajeczna 15",
                    city = "Warszawa"
                ),
                RestaurantDTO(
                    restaurantId = 3,
                    name = "Best Thai",
                    address = "ul. Krótka 10",
                    city = "Kraków"
                )
            )

            RestaurantsBottomSheet(
                body= { modifier -> OsmMapView(mv, startPoint, modifier) },
                sheetContent = sheetContent,
                navController = navController
            )
        }
        composable<RestaurantRoutes.Details> {
            RestaurantDetailActivity(restaurantId = it.toRoute<RestaurantRoutes.Details>().restaurantId)
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

        Box(modifier = Modifier.fillMaxSize()) {

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
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        restaurantDetailVM.restaurant?.let { restaurant ->

                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                repeat(5) {
                                    ImageCard(
                                        painterResource(R.drawable.pizza)
                                    )
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
                                    navController.navigate(RestaurantRoutes.Details(restaurantId =  1))
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