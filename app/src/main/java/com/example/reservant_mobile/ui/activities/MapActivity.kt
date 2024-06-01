package com.example.reservant_mobile.ui.activities

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reservant_mobile.R
import com.example.reservant_mobile.ui.components.MainBottomSheet
import com.example.reservant_mobile.ui.components.MainMapView
import com.example.reservant_mobile.ui.components.RestaurantDetailBottomSheet
import com.example.reservant_mobile.ui.viewmodels.MapViewModel
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapActivity(){
    val mapViewModel = viewModel<MapViewModel>()
    var showRestaurantBottomSheet by remember { mutableStateOf(false) }

    // Init map
    val context = LocalContext.current
    val startPoint = GeoPoint(52.237049, 21.017532)
    val mv = mapViewModel.initMapView(context, startPoint)

    if (showRestaurantBottomSheet) {
        RestaurantDetailBottomSheet() {
            showRestaurantBottomSheet = false
        }
    }

    mapViewModel.addRestaurantMarker(
        GeoPoint(52.240055, 21.017532),
        context.getDrawable(R.drawable.restaurant_photo)!!,
        "Restaurant 1" +
                ""
    ) { _, _ ->
        showRestaurantBottomSheet = true
        true
    }

    mapViewModel.addRestaurantMarker(
        GeoPoint(52.240055, 21.027532),
        context.getDrawable(R.drawable.restaurant_photo)!!,
        "Restaurant 1" +
                ""
    ) { _, _ ->
        showRestaurantBottomSheet = true
        true
    }

    mapViewModel.addRestaurantMarker(
        GeoPoint(52.250055, 21.027532),
        context.getDrawable(R.drawable.restaurant_photo)!!,
        "Restaurant 1" +
                ""
    ) { _, _ ->
        showRestaurantBottomSheet = true
        true
    }

    mapViewModel.addRestaurantMarker(
        GeoPoint(52.210055, 21.007532),
        context.getDrawable(R.drawable.restaurant_photo)!!,
        "Restaurant 1" +
                ""
    ) { _, _ ->
        showRestaurantBottomSheet = true
        true
    }


    val sheetContent = listOf(
        "Restarant 1" to "Adres 1",
        "Restarant 2" to "Adres 2",
        "Restarant 3" to "Adres 3",
        "Restarant 4" to "Adres 4",)

    MainBottomSheet (
        body= { modifier -> MainMapView(mv, startPoint, modifier) },
        sheetContent = sheetContent)
}