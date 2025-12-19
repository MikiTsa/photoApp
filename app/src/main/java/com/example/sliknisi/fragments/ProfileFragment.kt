package com.example.sliknisi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sliknisi.MyApplication
import com.example.sliknisi.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var app: MyApplication

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
    }

    private fun displayProfileInfo() {
        val totalLandmarks = app.landmarksList.size
        val visitedLandmarks = app.landmarksList.count { it.isVisited }
        val totalPoints = app.landmarksList.filter { it.isVisited }.sumOf { it.pointValue }
        val level = (totalPoints / 100) + 1
        
        with(binding) {
            tvUsername.text = "Explorer"
            tvLevel.text = "Level $level"
            tvPoints.text = "$totalPoints points"
            tvLandmarksVisited.text = "$visitedLandmarks / $totalLandmarks landmarks visited"
            tvPhotosCount.text = "0 photos captured"
            
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

    override fun onResume() {
        super.onResume()
        displayProfileInfo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
