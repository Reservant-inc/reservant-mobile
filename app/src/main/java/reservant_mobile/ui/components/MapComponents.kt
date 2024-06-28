package reservant_mobile.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@Composable
fun rememberMapViewWithLifecycle(mapView: MapView): MapView {
    // Makes MapView follow the lifecycle of this composable
    val lifecycleObserver = remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
    }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }
    return mapView
}

@Composable
fun OsmMapView(
    mapView: MapView,
    startPoint: GeoPoint,
    modifier: Modifier = Modifier.fillMaxSize()
) {

    val geoPoint by remember { mutableStateOf(startPoint) }
    val mapViewState = rememberMapViewWithLifecycle(mapView)

    AndroidView(
        modifier = modifier,
        factory = { mapViewState },
        update = { view ->
            view.controller.setCenter(geoPoint)
        }
    )
}