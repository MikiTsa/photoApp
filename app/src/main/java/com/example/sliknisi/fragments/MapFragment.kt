@file:Suppress("DEPRECATION")

package com.example.sliknisi.fragments

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.lib.Landmark
import com.example.lib.LandmarkCategory
import com.example.sliknisi.MyApplication
import com.example.sliknisi.R
import com.example.sliknisi.databinding.FragmentMapBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var mapView: MapView
    private lateinit var app: MyApplication
    
    private val landmarkMarkers = mutableListOf<Marker>()

    private val mariborLocation = GeoPoint(46.5547, 15.6467)
    private val defaultZoom = 15.0

    companion object {
        private const val TAG = "MapFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        app = requireActivity().application as MyApplication
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeOsmdroidConfiguration()
        setupMap()
        setupMapControls()
        addLandmarkMarkers()
        
        Log.d(TAG, "Map initialized with ${landmarkMarkers.size} landmark markers")
    }

    private fun initializeOsmdroidConfiguration() {
        Configuration.getInstance().load(
            requireContext(),
            requireContext().getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = requireContext().packageName
        Log.d(TAG, "osmdroid configuration initialized")
    }

    private fun setupMap() {
        mapView = binding.mapView
        
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.setBuiltInZoomControls(false)
        
        val mapController = mapView.controller
        mapController.setZoom(defaultZoom)
        mapController.setCenter(mariborLocation)

        addCompassOverlay()
        addRotationGestureOverlay()
        
        Log.d(TAG, "Map centered on Maribor: lat=${mariborLocation.latitude}, lon=${mariborLocation.longitude}")
    }

    private fun addCompassOverlay() {
        val compassOverlay = CompassOverlay(
            requireContext(),
            InternalCompassOrientationProvider(requireContext()),
            mapView
        )
        compassOverlay.enableCompass()
        mapView.overlays.add(compassOverlay)
    }

    private fun addRotationGestureOverlay() {
        val rotationGestureOverlay = RotationGestureOverlay(mapView)
        rotationGestureOverlay.isEnabled = true
        mapView.overlays.add(rotationGestureOverlay)
    }

    private fun setupMapControls() {
        binding.btnZoomIn.setOnClickListener {
            mapView.controller.zoomIn()
            Log.d(TAG, "Zoomed in, current zoom: ${mapView.zoomLevelDouble}")
        }

        binding.btnZoomOut.setOnClickListener {
            mapView.controller.zoomOut()
            Log.d(TAG, "Zoomed out, current zoom: ${mapView.zoomLevelDouble}")
        }

        binding.btnCenterLocation.setOnClickListener {
            mapView.controller.animateTo(mariborLocation)
            Log.d(TAG, "Centered map on Maribor")
        }
    }

    private fun addLandmarkMarkers() {
        landmarkMarkers.forEach { mapView.overlays.remove(it) }
        landmarkMarkers.clear()

        app.landmarksList.forEach { landmark ->
            val marker = createLandmarkMarker(landmark)
            landmarkMarkers.add(marker)
            mapView.overlays.add(marker)
        }

        mapView.invalidate()
        Log.d(TAG, "Added ${landmarkMarkers.size} landmark markers to map")
    }

    private fun createLandmarkMarker(landmark: Landmark): Marker {
        val marker = Marker(mapView)
        val position = GeoPoint(landmark.latitude, landmark.longitude)

        marker.position = position
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.icon = getMarkerIconForCategory(landmark.category)
        marker.title = landmark.name
        marker.snippet = buildMarkerSnippet(landmark)

        marker.setOnMarkerClickListener { clickedMarker, _ ->
            clickedMarker.showInfoWindow()
            mapView.controller.animateTo(clickedMarker.position)
            
            Log.d(TAG, "Marker clicked: ${landmark.name}")
            true
        }
        
        return marker
    }

    private fun getMarkerIconForCategory(category: LandmarkCategory): Drawable? {
        val iconResId = when (category) {
            LandmarkCategory.MUSEUM -> R.drawable.ic_marker_museum
            LandmarkCategory.CULTURAL -> R.drawable.ic_marker_museum
            LandmarkCategory.VIEWPOINT -> R.drawable.ic_marker_viewpoint
            LandmarkCategory.PARK -> R.drawable.ic_marker_park
            LandmarkCategory.MONUMENT -> R.drawable.ic_marker_monument
            LandmarkCategory.HISTORICAL -> R.drawable.ic_marker_monument
            LandmarkCategory.ARCHITECTURAL -> R.drawable.ic_marker_monument
            LandmarkCategory.RELIGIOUS -> R.drawable.ic_marker_default
            LandmarkCategory.OTHER -> R.drawable.ic_marker_default
        }
        
        return ContextCompat.getDrawable(requireContext(), iconResId)
    }

    private fun buildMarkerSnippet(landmark: Landmark): String {
        val visitedStatus = if (landmark.isVisited) "Visited" else "Not visited yet"
        return """
            📍 ${landmark.category.name}
            ⭐ ${landmark.pointValue} points
            $visitedStatus
            
            ${landmark.description}
        """.trimIndent()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()

        addLandmarkMarkers()
        
        Log.d(TAG, "Map resumed, refreshed markers")
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        Log.d(TAG, "Map paused")
    }

    override fun onDestroyView() {
        super.onDestroyView()

        landmarkMarkers.clear()
        
        _binding = null
        Log.d(TAG, "Map view destroyed")
    }
}
