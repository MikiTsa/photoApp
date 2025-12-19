package com.example.sliknisi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sliknisi.MyApplication
import com.example.sliknisi.adapters.NearbyLandmarkAdapter
import com.example.sliknisi.databinding.FragmentHomeBinding
import kotlin.random.Random

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var app: MyApplication
    private lateinit var adapter: NearbyLandmarkAdapter

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
        
        setupRecyclerView()
        displayClosestLandmarks()
    }

    private fun setupRecyclerView() {
        adapter = NearbyLandmarkAdapter()
        binding.recyclerViewNearby.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewNearby.adapter = adapter
    }

    private fun displayClosestLandmarks() {
        // Get landmarks and add mock distances
        val landmarksWithDistance = app.landmarksList
            .filter { !it.isVisited } // Show only unvisited landmarks
            .take(10) // Take max 10 for randomization
            .map { landmark ->
                // Generate mock distance between 0.1 and 3.0 km
                val distance = Random.nextDouble(0.1, 3.0)
                Pair(landmark, distance)
            }
            .sortedBy { it.second } // Sort by distance
            .take(5) // Take closest 5
        
        if (landmarksWithDistance.isEmpty()) {
            // If all visited, show message
            binding.tvEmptyMessage.visibility = View.VISIBLE
            binding.recyclerViewNearby.visibility = View.GONE
        } else {
            binding.tvEmptyMessage.visibility = View.GONE
            binding.recyclerViewNearby.visibility = View.VISIBLE
            adapter.updateData(landmarksWithDistance)
        }
        
        // Update count
        binding.tvNearbyCount.text = "Showing ${landmarksWithDistance.size} nearby landmarks"
    }

    override fun onResume() {
        super.onResume()
        // Refresh when returning to this fragment
        displayClosestLandmarks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
