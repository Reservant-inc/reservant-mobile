package com.example.reservant_mobile.ui.activities

import RestaurantDetailActivity
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reservant_mobile.R
import com.example.reservant_mobile.ui.components.BottomNavigation
import com.example.reservant_mobile.ui.components.FloatingTabSwitch
import com.example.reservant_mobile.ui.components.MainMapView
import com.example.reservant_mobile.ui.navigation.MainRoutes
import com.example.reservant_mobile.ui.navigation.RegisterRestaurantRoutes
import com.example.reservant_mobile.ui.navigation.RestaurantDetailRoutes
import com.example.reservant_mobile.ui.navigation.RestaurantManagementRoutes
import com.example.reservant_mobile.ui.theme.AppTheme
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeActivity() {
    val innerNavController = rememberNavController()

    val isSystemInDarkMode = isSystemInDarkTheme()

    val darkTheme = remember {
        mutableStateOf(isSystemInDarkMode)
    }

    AppTheme (darkTheme = darkTheme.value) {
        Scaffold(
            bottomBar = {
                BottomNavigation(innerNavController)
            }
        ){
            NavHost(navController = innerNavController, startDestination = MainRoutes.Home, modifier = Modifier.padding(it)){
                composable<MainRoutes.Home>{
                    

                    val startPoint = GeoPoint(52.237049, 21.017532)
                    // Init map
                    val mv = MapView(LocalContext.current).apply {

                        setTileSource(TileSourceFactory.OpenTopo)
                        setMultiTouchControls(true)
                        // Enable rotation
                        val rotationGestureOverlay = RotationGestureOverlay(this)
                        rotationGestureOverlay.isEnabled
                        overlays.add(rotationGestureOverlay)

                        minZoomLevel = 3.0
                        maxZoomLevel = 20.0
                        controller.setZoom(15.0)
                        controller.setCenter(startPoint)

                        val startMarker = Marker(this)
                        startMarker.position = startPoint
                        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        //Fixme: proper mark icons
                        val bitmap =  context.getDrawable( R.drawable.ic_logo)?.toBitmap(50, 50)
                        startMarker.icon = BitmapDrawable(context.resources, bitmap)
                        startMarker.title = "You are here"
                        overlays.add(startMarker)

                    }

                    val map: List< Pair<String, @Composable () -> Unit>> = listOf(
                         "Test 1" to {
                             MainMapView(mv, startPoint)
                         },
                        "Test 2" to {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ){
                                Text(text="Page 1")
                            }
                        })


                    //tab layout
                    FloatingTabSwitch(map)
                }
                composable<RestaurantManagementRoutes.Restaurant>{
                    RestaurantManagementActivity()
                }
                composable<RegisterRestaurantRoutes.Register>{
                    RegisterRestaurantActivity(navControllerHome = innerNavController)
                }
                composable<MainRoutes.Profile>{
                    RestaurantOwnerProfileActivity(navController = innerNavController, darkTheme = darkTheme)
                }
                composable<RestaurantDetailRoutes.Details>{
                    RestaurantDetailActivity(navControllerHome = innerNavController)
                }
            }
        }
    }
}