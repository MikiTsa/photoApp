@file:Suppress("DEPRECATION")
@file:SuppressLint("MissingPermission")

package com.example.sliknisi.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.lib.Landmark
import com.example.lib.LandmarkCategory
import com.example.lib.Photo
import com.example.sliknisi.MyApplication
import com.example.sliknisi.R
import com.example.sliknisi.databinding.FragmentMapBinding
import com.example.sliknisi.utils.DistanceUtils
import com.example.sliknisi.utils.NotificationUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var mapView: MapView
    private lateinit var app: MyApplication

    private val landmarkMarkers = mutableListOf<Marker>()
    private var userLocationMarker: Marker? = null
    private var accuracyCircle: Polygon? = null
    private var captureRadiusCircle: Polygon? = null
    private var selectedLandmarkId: String? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    private val mariborLocation = GeoPoint(46.5547, 15.6467)
    private val defaultZoom = 15.0
    private val captureRadius = 50.0

    private val notifiedLandmarks = mutableMapOf<String, Long>()
    private val notificationCooldown = 5 * 60 * 1000L

    private var photoUri: Uri? = null
    private var nearestLandmark: Landmark? = null

    companion object {
        private const val TAG = "MapFragment"
        private const val ARG_LANDMARK_ID = "landmark_id"
        private const val PREFS_NAME = "landmark_notifications"
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission required", Toast.LENGTH_SHORT).show()
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null && nearestLandmark != null) {
            handlePhotoCapture(photoUri!!, nearestLandmark!!)
        }
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            Log.d(TAG, "Location permissions granted")
            initLocationServices()
        } else {
            Log.d(TAG, "Location permissions denied")
            Toast.makeText(
                requireContext(),
                "Location permission required for user location features",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(requireContext(), "Notifications disabled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        app = requireActivity().application as MyApplication

        arguments?.getString(ARG_LANDMARK_ID)?.let {
            selectedLandmarkId = it
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NotificationUtils.createNotificationChannel(requireContext())
        checkNotificationPermission()
        initializeOsmdroidConfiguration()
        setupMap()
        setupMapControls()
        setupCameraButton()
        addLandmarkMarkers()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        setupLocationCallback()
        checkLocationPermissions()

        selectedLandmarkId?.let { centerOnLandmark(it) }

        Log.d(TAG, "Map initialized with ${landmarkMarkers.size} landmark markers")
    }

    private fun setupCameraButton() {
        binding.fabCapturePhoto.setOnClickListener {
            if (isWithinCaptureRadius()) {
                checkCameraPermissionAndCapture()
            } else {
                Toast.makeText(requireContext(), "Move within 50m of a landmark", Toast.LENGTH_SHORT).show()
            }
        }
        updateCameraButtonState()
    }

    private fun isWithinCaptureRadius(): Boolean {
        currentLocation ?: return false
        
        nearestLandmark = app.landmarksList
            .filterNot { it.isVisited }
            .minByOrNull { landmark ->
                DistanceUtils.calculateDistance(
                    currentLocation!!.latitude,
                    currentLocation!!.longitude,
                    landmark.latitude,
                    landmark.longitude
                )
            }

        nearestLandmark?.let { landmark ->
            val distance = DistanceUtils.calculateDistance(
                currentLocation!!.latitude,
                currentLocation!!.longitude,
                landmark.latitude,
                landmark.longitude
            )
            return distance <= captureRadius
        }

        return false
    }

    private fun updateCameraButtonState() {
        val withinRadius = isWithinCaptureRadius()
        binding.fabCapturePhoto.isEnabled = withinRadius
        binding.fabCapturePhoto.backgroundTintList = ContextCompat.getColorStateList(
            requireContext(),
            if (withinRadius) R.color.green_dark else R.color.accent
        )
    }

    private fun checkCameraPermissionAndCapture() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun launchCamera() {
        val timestamp = System.currentTimeMillis()
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${timestamp}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SlikniSi")
            }
        }

        photoUri = requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        photoUri?.let {
            takePictureLauncher.launch(it)
        }
    }

    private fun handlePhotoCapture(uri: Uri, landmark: Landmark) {
        currentLocation?.let { location ->
            val photo = Photo(
                landmarkId = landmark.id,
                filePath = uri.toString(),
                timestamp = System.currentTimeMillis(),
                latitude = location.latitude,
                longitude = location.longitude
            )

            app.addPhoto(photo)
            
            if (!landmark.isVisited) {
                landmark.isVisited = true
                app.updateLandmark(landmark)
                
                Toast.makeText(
                    requireContext(),
                    "Landmark visited! +${landmark.pointValue} points",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.msg_photo_captured),
                    Toast.LENGTH_SHORT
                ).show()
            }

            updateCameraButtonState()
            addLandmarkMarkers()
        }
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
            currentLocation?.let { location ->
                val userGeoPoint = GeoPoint(location.latitude, location.longitude)
                mapView.controller.animateTo(userGeoPoint)
                Log.d(TAG, "Centered map on user location")
            } ?: run {
                Toast.makeText(requireContext(), "Location not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                initLocationServices()
            }
            else -> {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun setupLocationCallback() {
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000
        ).apply {
            setMinUpdateIntervalMillis(2000)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    currentLocation = location
                    updateUserLocationMarker(location)
                    updateLandmarkDistances(location)
                    updateCameraButtonState()
                    checkProximityNotifications(location)
                    Log.d(TAG, "Location updated: ${location.latitude}, ${location.longitude}")
                }
            }
        }

        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        app.landmarksList.forEach { landmark ->
            notifiedLandmarks[landmark.id] = prefs.getLong(landmark.id, 0L)
        }
    }

    private fun initLocationServices() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                currentLocation = it
                updateUserLocationMarker(it)
                updateLandmarkDistances(it)
                updateCameraButtonState()
                if (selectedLandmarkId == null) {
                    val userGeoPoint = GeoPoint(it.latitude, it.longitude)
                    mapView.controller.animateTo(userGeoPoint)
                }
                Log.d(TAG, "Got last known location: ${it.latitude}, ${it.longitude}")
            }
        }

        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
        Log.d(TAG, "Started location updates")
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d(TAG, "Stopped location updates")
    }

    private fun updateUserLocationMarker(location: Location) {
        val position = GeoPoint(location.latitude, location.longitude)

        if (userLocationMarker == null) {
            userLocationMarker = Marker(mapView).apply {
                this.position = position
                title = "You are here"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_my_location)
            }
            mapView.overlays.add(userLocationMarker)
            Log.d(TAG, "Created user location marker")
        } else {
            userLocationMarker?.position = position
        }

        updateAccuracyCircle(location)
        mapView.invalidate()
    }

    private fun updateAccuracyCircle(location: Location) {
        val accuracy = location.accuracy
        val center = GeoPoint(location.latitude, location.longitude)

        accuracyCircle?.let { mapView.overlays.remove(it) }

        accuracyCircle = Polygon(mapView).apply {
            points = Polygon.pointsAsCircle(center, accuracy.toDouble())
            fillColor = 0x220000FF
            strokeColor = 0x660000FF
            strokeWidth = 2f
        }

        mapView.overlays.add(0, accuracyCircle)
    }

    private fun centerOnLandmark(landmarkId: String) {
        val landmark = app.landmarksList.find { it.id == landmarkId }
        landmark?.let {
            val position = GeoPoint(it.latitude, it.longitude)
            mapView.controller.setZoom(17.0)
            mapView.controller.animateTo(position)

            showCaptureRadius(it)
            highlightLandmark(landmarkId)

            landmarkMarkers.find { marker ->
                marker.position.latitude == it.latitude &&
                marker.position.longitude == it.longitude
            }?.showInfoWindow()

            Log.d(TAG, "Centered on landmark: ${it.name}")
        }
    }

    private fun showCaptureRadius(landmark: Landmark) {
        captureRadiusCircle?.let { mapView.overlays.remove(it) }

        val center = GeoPoint(landmark.latitude, landmark.longitude)
        captureRadiusCircle = Polygon(mapView).apply {
            points = Polygon.pointsAsCircle(center, captureRadius)
            fillColor = 0x2200FF00
            strokeColor = 0x8800FF00.toInt()
            strokeWidth = 3f
        }

        mapView.overlays.add(1, captureRadiusCircle)
        mapView.invalidate()
    }

    private fun highlightLandmark(landmarkId: String) {
        val landmark = app.landmarksList.find { it.id == landmarkId }
        landmark?.let {
            landmarkMarkers.find { marker ->
                marker.position.latitude == it.latitude &&
                marker.position.longitude == it.longitude
            }?.apply {
                alpha = 1.0f
            }
        }
    }

    private fun updateLandmarkDistances(userLocation: Location) {
        landmarkMarkers.forEach { marker ->
            val landmark = app.landmarksList.find {
                it.latitude == marker.position.latitude &&
                it.longitude == marker.position.longitude
            }

            landmark?.let {
                val distance = DistanceUtils.calculateDistance(
                    userLocation.latitude,
                    userLocation.longitude,
                    it.latitude,
                    it.longitude
                )

                marker.snippet = buildMarkerSnippet(it, distance)
            }
        }

        mapView.invalidate()
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

        val distance = currentLocation?.let {
            DistanceUtils.calculateDistance(it.latitude, it.longitude, landmark.latitude, landmark.longitude)
        }
        marker.snippet = buildMarkerSnippet(landmark, distance)

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
            LandmarkCategory.MONUMENT -> R.drawable.ic_marker_monument
            LandmarkCategory.PARK -> R.drawable.ic_marker_park
            LandmarkCategory.MUSEUM -> R.drawable.ic_marker_museum
            LandmarkCategory.VIEWPOINT -> R.drawable.ic_marker_viewpoint
            LandmarkCategory.HISTORICAL -> R.drawable.ic_marker_monument
            LandmarkCategory.CULTURAL -> R.drawable.ic_marker_museum
            LandmarkCategory.RELIGIOUS -> R.drawable.ic_marker_default
            LandmarkCategory.ARCHITECTURAL -> R.drawable.ic_marker_monument
            LandmarkCategory.OTHER -> R.drawable.ic_marker_default
        }

        return ContextCompat.getDrawable(requireContext(), iconResId)
    }

    private fun buildMarkerSnippet(landmark: Landmark, distance: Float? = null): String {
        val visitedStatus = if (landmark.isVisited) "✓ Visited" else "Not visited yet"
        val distanceText = distance?.let { "📏 ${DistanceUtils.formatDistanceWithSuffix(it)}\n" } ?: ""
        val captureText = if (selectedLandmarkId == landmark.id) "⭕ Capture radius: ${captureRadius.toInt()}m\n" else ""

        return """
            $distanceText$captureText📍 ${landmark.category.name}
            ⭐ ${landmark.pointValue} points
            $visitedStatus
            
            ${landmark.description}
        """.trimIndent()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun checkProximityNotifications(userLocation: Location) {
        val currentTime = System.currentTimeMillis()

        app.landmarksList.forEach { landmark ->
            if (landmark.isVisited) return@forEach

            val distance = DistanceUtils.calculateDistance(
                userLocation.latitude,
                userLocation.longitude,
                landmark.latitude,
                landmark.longitude
            )

            if (distance <= captureRadius) {
                val lastNotified = notifiedLandmarks[landmark.id] ?: 0L

                if (currentTime - lastNotified > notificationCooldown) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                        ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED) {

                        NotificationUtils.showProximityNotification(requireContext(), landmark, distance)
                        notifiedLandmarks[landmark.id] = currentTime
                        saveNotifiedLandmarks()
                        Log.d(TAG, "Notification: ${landmark.name} at ${distance.toInt()}m")
                    }
                }
            }
        }
    }

    private fun saveNotifiedLandmarks() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        notifiedLandmarks.forEach { (id, time) ->
            editor.putLong(id, time)
        }
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()

        addLandmarkMarkers()

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        }

        Log.d(TAG, "Map resumed, refreshed markers")
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()

        stopLocationUpdates()
        Log.d(TAG, "Map paused, stopped location updates")
    }

    override fun onDestroyView() {
        super.onDestroyView()

        stopLocationUpdates()

        userLocationMarker?.let { mapView.overlays.remove(it) }
        userLocationMarker = null

        accuracyCircle?.let { mapView.overlays.remove(it) }
        accuracyCircle = null

        captureRadiusCircle?.let { mapView.overlays.remove(it) }
        captureRadiusCircle = null

        landmarkMarkers.clear()
        selectedLandmarkId = null

        _binding = null
        Log.d(TAG, "Map view destroyed")
    }
}
