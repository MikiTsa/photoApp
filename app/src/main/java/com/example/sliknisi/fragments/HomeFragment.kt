package com.example.sliknisi.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sliknisi.MyApplication
import com.example.sliknisi.R
import com.example.sliknisi.adapters.NearbyLandmarkAdapter
import com.example.sliknisi.databinding.FragmentHomeBinding
import com.example.sliknisi.utils.DistanceUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var app: MyApplication
    private lateinit var adapter: NearbyLandmarkAdapter
    
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    
    companion object {
        private const val TAG = "HomeFragment"
    }
    
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        
        if (fineLocationGranted || coarseLocationGranted) {
            Log.d(TAG, "Location permissions granted")
            getLocationAndDisplayLandmarks()
        } else {
            Log.d(TAG, "Location permissions denied")
            showLocationRequiredMessage()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        app = requireActivity().application as MyApplication
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        
        setupRecyclerView()
        checkLocationAndDisplay()

        if (!app.isDataLoaded) {
            app.onDataLoadedCallback = {
                activity?.runOnUiThread {
                    checkLocationAndDisplay()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = NearbyLandmarkAdapter { landmark ->
            val bundle = bundleOf("landmark_id" to landmark.id)
            findNavController().navigate(R.id.mapFragment, bundle)
            Log.d(TAG, "Navigating to map with landmark: ${landmark.name}")
        }
        binding.recyclerViewNearby.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewNearby.adapter = adapter
    }
    
    private fun checkLocationAndDisplay() {
        val hasPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            getLocationAndDisplayLandmarks()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    
    @SuppressLint("MissingPermission")
    private fun getLocationAndDisplayLandmarks() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            currentLocation = location
            
            if (location != null) {
                Log.d(TAG, "Got user location: ${location.latitude}, ${location.longitude}")
                displayClosestLandmarks(location)
            } else {
                Log.d(TAG, "Location is null")
                showLocationUnavailableMessage()
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to get location", e)
            showLocationUnavailableMessage()
        }
    }
    
    private fun displayClosestLandmarks(userLocation: Location) {
        val landmarksWithDistance = app.landmarksList
            .filter { !it.isVisited }
            .map { landmark ->
                val distanceInMeters = DistanceUtils.calculateDistance(
                    userLocation.latitude,
                    userLocation.longitude,
                    landmark.latitude,
                    landmark.longitude
                )
                Pair(landmark, distanceInMeters.toDouble())
            }
            .sortedBy { it.second }
            .take(5)
        
        if (landmarksWithDistance.isEmpty()) {
            binding.tvEmptyMessage.text = "🎉 You've visited all landmarks!\nGreat job, explorer!"
            binding.tvEmptyMessage.visibility = View.VISIBLE
            binding.recyclerViewNearby.visibility = View.GONE
            binding.tvNearbyCount.visibility = View.GONE
        } else {
            binding.tvEmptyMessage.visibility = View.GONE
            binding.recyclerViewNearby.visibility = View.VISIBLE
            binding.tvNearbyCount.visibility = View.VISIBLE
            adapter.updateData(landmarksWithDistance)
            
            val closestDistance = DistanceUtils.formatDistance(landmarksWithDistance.first().second.toFloat())
            binding.tvNearbyCount.text = 
                "📍 ${landmarksWithDistance.size} nearby landmarks (closest: $closestDistance)"
        }
        
        Log.d(TAG, "Displayed ${landmarksWithDistance.size} landmarks with real distances")
    }

    private fun showLocationRequiredMessage() {
        binding.tvEmptyMessage.text = "📍 Location permission required\n\nPlease enable location to see nearby landmarks"
        binding.tvEmptyMessage.visibility = View.VISIBLE
        binding.recyclerViewNearby.visibility = View.GONE
        binding.tvNearbyCount.visibility = View.GONE
        
        Log.d(TAG, "Showing location required message")
    }
    
    private fun showLocationUnavailableMessage() {
        binding.tvEmptyMessage.text = "📍 Location unavailable\n\nPlease enable GPS/location services in your device settings"
        binding.tvEmptyMessage.visibility = View.VISIBLE
        binding.recyclerViewNearby.visibility = View.GONE
        binding.tvNearbyCount.visibility = View.GONE
        
        Log.d(TAG, "Showing location unavailable message")
    }

    override fun onResume() {
        super.onResume()
        checkLocationAndDisplay()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
