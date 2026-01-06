@file:Suppress("DEPRECATION")

package com.example.sliknisi.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sliknisi.databinding.FragmentMapBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var mapView: MapView

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeOsmdroidConfiguration()
        setupMap()
        setupMapControls()
        
        Log.d(TAG, "Map initialized successfully")
    }

    private fun initializeOsmdroidConfiguration() {
        // Load/initialize the osmdroid configuration
        // sets the user agent and manages tile caching
        Configuration.getInstance().load(
            requireContext(),
            requireContext().getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )

        Configuration.getInstance().userAgentValue = requireContext().packageName
        
        Log.d(TAG, "osmdroid configuration initialized")
    }

    private fun setupMap() {
        mapView = binding.mapView
        
        // Set tile source to MAPNIK (default OpenStreetMap tiles)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        
        // Enable multi-touch controls (pinch to zoom, rotate, etc.)
        mapView.setMultiTouchControls(true)

        mapView.setBuiltInZoomControls(false)
        
        // Set initial map center to Maribor
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

    override fun onResume() {
        super.onResume()
        mapView.onResume() // Needed for compass, my location overlays, v6.0.0 and up
        Log.d(TAG, "Map resumed")
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()      // istiot komentar od gore
        Log.d(TAG, "Map paused")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "Map view destroyed")
    }
}
