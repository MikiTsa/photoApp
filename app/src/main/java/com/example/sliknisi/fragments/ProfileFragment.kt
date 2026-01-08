package com.example.sliknisi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sliknisi.MyApplication
import com.example.sliknisi.adapters.PhotoAdapter
import com.example.sliknisi.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var app: MyApplication
    private lateinit var photoAdapter: PhotoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        app = requireActivity().application as MyApplication
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayProfileInfo()
        setupGallery()
    }

    private fun displayProfileInfo() {
        val totalLandmarks = app.landmarksList.size
        val visitedLandmarks = app.landmarksList.count { it.isVisited }
        val totalPoints = app.landmarksList.filter { it.isVisited }.sumOf { it.pointValue }
        val level = (totalPoints / 100) + 1
        val photosCount = app.photosList.size
        
        with(binding) {
            tvUsername.text = "Explorer"
            tvLevel.text = "Level $level"
            tvPoints.text = "$totalPoints points"
            tvLandmarksVisited.text = "$visitedLandmarks / $totalLandmarks landmarks visited"
            tvPhotosCount.text = "$photosCount photos captured"
            
            // Progress calculation
            val progress = if (totalLandmarks > 0) {
                (visitedLandmarks.toFloat() / totalLandmarks.toFloat() * 100).toInt()
            } else {
                0
            }
            progressBar.progress = progress
            tvProgressPercentage.text = "$progress%"
        }
    }

    private fun setupGallery() {
        photoAdapter = PhotoAdapter(app.photosList.reversed())
        
        binding.rvGallery.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = photoAdapter
        }

        if (app.photosList.isEmpty()) {
            binding.rvGallery.visibility = View.GONE
            binding.tvEmptyGallery.visibility = View.VISIBLE
        } else {
            binding.rvGallery.visibility = View.VISIBLE
            binding.tvEmptyGallery.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        displayProfileInfo()
        setupGallery()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
