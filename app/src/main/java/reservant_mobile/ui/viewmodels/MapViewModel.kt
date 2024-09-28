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
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.reservant_mobile.R
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import reservant_mobile.data.models.dtos.LocationDTO
import reservant_mobile.data.services.RestaurantService

class MapViewModel : ReservantViewModel() {
    private object OsmMap {
        lateinit var view:MapView
    }

    private val restaurantService = RestaurantService()
    private val _restaurantsState = MutableStateFlow<PagingData<RestaurantOnMap>>(PagingData.empty())
    private val _eventsState = MutableStateFlow<PagingData<EventOnMap>>(PagingData.empty())
    private var _addedRestaurants = mutableListOf<Int>()
    var restaurantTags = mutableListOf<String>()
    val restaurants: StateFlow<PagingData<RestaurantOnMap>> = _restaurantsState.asStateFlow()
    val events: StateFlow<PagingData<EventOnMap>> = _eventsState.asStateFlow()
    var areFiltersLoading: Boolean by mutableStateOf(false)

    private lateinit var poiMarkers:RadiusMarkerClusterer

    var search: String? = null
    var selectedTags: List<String>? = null
    var minRating: Int? = null

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

            poiMarkers = RadiusMarkerClusterer(context)
            overlays.add(poiMarkers)
        }

        _addedRestaurants = emptyList<Int>().toMutableList()
        OsmMap.view = mv
        addUserMarker(startPoint)
        getRestaurants(startPoint)

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

    fun refreshRestaurants(userLocation: GeoPoint) {
        _restaurantsState.value = PagingData.empty()
        getRestaurants(userLocation)
    }


    @OptIn(FlowPreview::class)
    fun getRestaurants(userLocation: GeoPoint){
        viewModelScope.launch {
            try {
                val res = restaurantService.getRestaurants(
                    origLat = userLocation.latitude,
                    origLon = userLocation.longitude,
                    name = search,
                    tags = selectedTags,
                    minRating = minRating,
                )

                if(res.isError || res.value == null)
                    throw Exception()

                res.value.cachedIn(viewModelScope).collectLatest { pagingData ->
                    _restaurantsState.value = pagingData.map { dto ->
                        RestaurantOnMap(
                            restaurantId = dto.restaurantId,
                            name = dto.name,
                            address = dto.address,
                            city = dto.city,
                            logo = getPhoto(dto.logo!!),
                            location = dto.location
                        )
                    }
                }

            } catch (e: Exception) {
                Log.d("[RESTAURANT]:", e.toString())
            }
        }

    }

    fun getEvents() {
        viewModelScope.launch {
            try {
                val res = restaurantService.getRestaurantEvents(1)
                if(res.isError || res.value == null)
                    throw Exception()

                res.value.cachedIn(viewModelScope).collectLatest { pagingData ->
                    _eventsState.value = pagingData.map { dto ->
                        EventOnMap(
                            eventId = dto.eventId!!,
                            time = dto.time,
                            creatorId = dto.creatorId!!,
                            creatorFullName = dto.creatorFullName!!,
                            restaurantId = dto.restaurantId,
                            restaurantName = dto.restaurantName!!,
                            participants = dto.participants!!.size,
                            numberInterested = dto.numberInterested!!
                        )
                    }
                }

            } catch (e: Exception) {
                Log.d("[EVENT]:", e.toString())

            }
        }
    }

    fun getRestaurantTags(){
        if(restaurantTags.isNotEmpty()){
            return
        }

        areFiltersLoading = true
        viewModelScope.launch {
            val res = restaurantService.getRestaurantTags()
            if(!res.isError){
                restaurantTags = res.value as MutableList<String>
            }
            areFiltersLoading = false
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun addRestaurantMarker(restaurant:RestaurantOnMap, onClick: (Marker, MapView) -> Boolean) {
        if(_addedRestaurants.contains(restaurant.restaurantId))
            return

        _addedRestaurants.add(restaurant.restaurantId)
        viewModelScope.launch {
            try {
                val position = GeoPoint(restaurant.location!!.latitude, restaurant.location.longitude)
                val restaurantMarker = CustomMarker(OsmMap.view)
                restaurantMarker.position = position
                restaurantMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

                val icon: Bitmap? = restaurant.logo

                restaurantMarker.icon = if(icon!= null)
                    getMarkerDrawable(icon)
                else
                    OsmMap.view.context.getDrawable(R.drawable.restaurant_template_icon)

                restaurantMarker.title = restaurant.name
                restaurantMarker.setInfoWindow(null)
                restaurantMarker.setOnMarkerClickListener(onClick)
                poiMarkers.add(restaurantMarker)
//                OsmMap.view.overlays.add(0,restaurantMarker)
            }
            catch (e: Exception){
                Log.d("[MAP MARKER]:", e.toString())
            }
        }
    }

    private suspend fun getPhoto(photoStr: String): Bitmap? {
        val result = fileService.getImage(photoStr)
        if (!result.isError){
            return  result.value!!
        }
        return null
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

data class RestaurantOnMap(
    val restaurantId: Int = Int.MIN_VALUE,
    val name: String,
    val address: String,
    val city: String,
    val logo: Bitmap?,
    val location: LocationDTO?
    )

data class EventOnMap(
    val eventId: Int,
    val time: String,
    val creatorId: String,
    val creatorFullName: String,
    val restaurantId: Int,
    val restaurantName:String,
    val participants: Int,
    val numberInterested: Int
)