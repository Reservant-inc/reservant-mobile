package reservant_mobile.ui.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.reservant_mobile.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import reservant_mobile.data.models.dtos.EventDTO
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.services.RestaurantService

class MapViewModel(): ViewModel() {
    private object OsmMap {
        lateinit var view:MapView
    }

    private val restaurantService = RestaurantService()
    private val _restaurantsState = MutableStateFlow<PagingData<RestaurantDTO>>(PagingData.empty())
    private val _eventsState = MutableStateFlow<PagingData<EventDTO>>(PagingData.empty())
    val restaurants: StateFlow<PagingData<RestaurantDTO>> = _restaurantsState.asStateFlow()
    val events: StateFlow<PagingData<EventDTO>> = _eventsState.asStateFlow()
    var isLoading: Boolean by mutableStateOf(false)


    fun initMapView(context: Context, startPoint: GeoPoint): MapView{

        val mv = MapView(context).apply {

            val customTiles = object : XYTileSource(
                "openstreetmap.fr", 1, 20, 256, ".png",
                arrayOf("https://a.tile.openstreetmap.fr/hot/")
            ) {
                override fun getTileURLString(pMapTileIndex: Long): String {
                    return baseUrl + MapTileIndex.getZoom(pMapTileIndex) + "/" +
                            MapTileIndex.getX(pMapTileIndex) + "/" +
                            MapTileIndex.getY(pMapTileIndex) + ".png"
                }
            }
            setTileSource(customTiles)
            setMultiTouchControls(true)
//            val rotationGestureOverlay = RotationGestureOverlay(this)
//            rotationGestureOverlay.isEnabled
//            overlays.add(rotationGestureOverlay)

            minZoomLevel = 4.0
            maxZoomLevel = 20.0
            controller.setZoom(17.0)
            controller.setCenter(startPoint)
        }

        OsmMap.view = mv
        addUserMarker(startPoint)

        return OsmMap.view
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addUserMarker(startPoint: GeoPoint){
        val startMarker = Marker(OsmMap.view)
        startMarker.position = startPoint
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.icon = OsmMap.view.context.getDrawable( R.drawable.user)
        startMarker.setInfoWindow(null)
        OsmMap.view.overlays.add(startMarker)
    }

    fun getRestaurantsInArea(lat1:Double, lon1:Double, lat2:Double, lon2:Double){
        viewModelScope.launch {
            try {
                isLoading = true
                val res = restaurantService.getRestaurantsInArea(lat1, lon1, lat2, lon2)
                if(res.isError || res.value == null)
                    throw Exception()

                res.value.collectLatest { pagingData ->
                    _restaurantsState.value = pagingData
                    isLoading = false
                }

            } catch (e: Exception) {
                Log.d("[RESTAURANT]:", e.toString())

            }
        }
    }

    suspend fun getEvents() {
        viewModelScope.launch {
            try {
                isLoading = true
                val res = restaurantService.getRestaurantEvents(1)
                if(res.isError || res.value == null)
                    throw Exception()

                res.value.collectLatest { pagingData ->
                    _eventsState.value = pagingData
                    isLoading = false
                }

            } catch (e: Exception) {
                Log.d("[EVENT]:", e.toString())

            }
        }
    }

    fun addRestaurantMarker(position: GeoPoint, icon: Bitmap?, title: String, onClick: (Marker, MapView) -> Boolean) {
        val restaurantMarker = CustomMarker(OsmMap.view)
        restaurantMarker.position = position
        restaurantMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

        restaurantMarker.icon = if(icon!= null)
            getMarkerDrawable(icon)
        else
            OsmMap.view.context.getDrawable(R.drawable.restaurant_template_icon)

        restaurantMarker.title = title
        restaurantMarker.setInfoWindow(null)
        restaurantMarker.setOnMarkerClickListener(onClick)
        OsmMap.view.overlays.add(0,restaurantMarker)
    }

    private fun getMarkerDrawable(icon: Bitmap): Drawable{
        val res = OsmMap.view.context.resources
        val roundedDrawable = RoundedBitmapDrawableFactory.create(res, icon)
        roundedDrawable.isCircular = true

        val borderWidth = 6f
        val width = 100
        val height = 100

        val borderedBitmap = Bitmap.createBitmap(width + 2 * borderWidth.toInt(), height + 2 * borderWidth.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(borderedBitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        canvas.drawCircle((width / 2 + borderWidth), (height / 2 + borderWidth), (width / 2 + borderWidth), paint)
        roundedDrawable.setBounds(borderWidth.toInt(), borderWidth.toInt(), width + borderWidth.toInt(), height + borderWidth.toInt())
        roundedDrawable.draw(canvas)

        return borderedBitmap.toDrawable(res)
    }

}

class CustomMarker(mapView: MapView) : Marker(mapView) {

    private val zoomLimit = 15.0


    private val textPaint = Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = 40f
        isAntiAlias = true
    }

    override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
        if(mapView.zoomLevelDouble < zoomLimit) return
        super.draw(canvas, mapView, shadow)
        if (shadow || title == null) return

        val point = Point()
        mapView.projection.toPixels(position, point)

        val textBounds = Rect()
        textPaint.getTextBounds(title, 0, title!!.length, textBounds)
        val xOffset = -textBounds.width() / 2
        val yOffset = icon.bounds.height() / 2 + 10

        canvas.drawText(
            title!!,
            point.x + xOffset.toFloat(),
            point.y + yOffset + textBounds.height().toFloat(),
            textPaint
        )
    }
}