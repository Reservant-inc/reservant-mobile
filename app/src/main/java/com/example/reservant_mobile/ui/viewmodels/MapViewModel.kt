package com.example.reservant_mobile.ui.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import com.example.reservant_mobile.R
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect

class MapViewModel(): ViewModel() {
    private object OsmMap {
        lateinit var view:MapView
    }


    fun initMapView(context: Context, startPoint: GeoPoint): MapView{

        val mv = MapView(context).apply {
            setTileSource(TileSourceFactory.OpenTopo)
            setMultiTouchControls(true)
            // Enable rotation
            val rotationGestureOverlay = RotationGestureOverlay(this)
            rotationGestureOverlay.isEnabled
            overlays.add(rotationGestureOverlay)

            minZoomLevel = 4.0
            maxZoomLevel = 20.0
            controller.setZoom(17.0)
            controller.setCenter(startPoint)
        }

        OsmMap.view = mv
        addUserMarker(startPoint)

        return OsmMap.view
    }

    private fun addUserMarker(startPoint: GeoPoint){
        val startMarker = Marker(OsmMap.view)
        startMarker.position = startPoint
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.icon = OsmMap.view.context.getDrawable( R.drawable.user)
        startMarker.setInfoWindow(null)
        OsmMap.view.overlays.add(startMarker)
    }

    fun addRestaurantMarker(position: GeoPoint, icon: Drawable, title: String, onClick: (Marker, MapView) -> Boolean) {
        val restaurantMarker = CustomMarker(OsmMap.view)
        restaurantMarker.position = position
        restaurantMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        restaurantMarker.icon = BitmapDrawable(OsmMap.view.context.resources, icon.toBitmap(60,60))
        restaurantMarker.title = title
        restaurantMarker.setInfoWindow(null)
        restaurantMarker.setOnMarkerClickListener(onClick)
        OsmMap.view.overlays.add(0,restaurantMarker)
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