package com.example.reservant_mobile.ui.activities

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reservant_mobile.R
import com.example.reservant_mobile.ui.components.OsmMapView
import com.example.reservant_mobile.ui.components.RestaurantDetailBottomSheet
import com.example.reservant_mobile.ui.components.RestaurantsBottomSheet
import com.example.reservant_mobile.ui.viewmodels.MapViewModel
import org.osmdroid.util.GeoPoint

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MapActivity(){
    val mapViewModel = viewModel<MapViewModel>()
    var showRestaurantBottomSheet by remember { mutableStateOf(false) }
    var showRestaurantId by remember { mutableIntStateOf(0) }


    // Init map
    val context = LocalContext.current
    val startPoint = GeoPoint(52.237049, 21.017532)
    val mv = mapViewModel.initMapView(context, startPoint)

    if (showRestaurantBottomSheet) {
        RestaurantDetailBottomSheet(showRestaurantId) {
            showRestaurantBottomSheet = false
        }
    }

    mapViewModel.addRestaurantMarker(
        GeoPoint(52.240055, 21.017532),
        context.getDrawable(R.drawable.restaurant_photo)!!,
        "Restaurant 1" +
                ""
    ) { _, _ ->
        showRestaurantId = 1
        showRestaurantBottomSheet = true
        true
    }

    mapViewModel.addRestaurantMarker(
        GeoPoint(52.240055, 21.027532),
        context.getDrawable(R.drawable.restaurant_photo)!!,
        "Restaurant 2" +
                ""
    ) { _, _ ->
        showRestaurantId = 2
        showRestaurantBottomSheet = true
        true
    }

    mapViewModel.addRestaurantMarker(
        GeoPoint(52.250055, 21.027532),
        context.getDrawable(R.drawable.restaurant_photo)!!,
        "Restaurant 3" +
                ""
    ) { _, _ ->
        showRestaurantId = 3
        showRestaurantBottomSheet = true
        true
    }

    mapViewModel.addRestaurantMarker(
        GeoPoint(52.210055, 21.007532),
        context.getDrawable(R.drawable.restaurant_photo)!!,
        "Restaurant 4" +
                ""
    ) { _, _ ->
        showRestaurantId = 4
        showRestaurantBottomSheet = true
        true
    }


    val sheetContent = listOf(
        "Restarant 1" to "Adres 1",
        "Restarant 2" to "Adres 2",
        "Restarant 3" to "Adres 3",
        "Restarant 4" to "Adres 4",)

    RestaurantsBottomSheet (
        body= { modifier -> OsmMapView(mv, startPoint, modifier) },
        sheetContent = sheetContent)
}