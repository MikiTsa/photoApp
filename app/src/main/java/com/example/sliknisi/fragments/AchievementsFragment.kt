package com.example.sliknisi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lib.DataGenerator
import com.example.sliknisi.MyApplication
import com.example.sliknisi.adapters.AchievementAdapter
import com.example.sliknisi.databinding.FragmentAchievementsBinding

class AchievementsFragment : Fragment() {

    private var _binding: FragmentAchievementsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var app: MyApplication
    private lateinit var adapter: AchievementAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAchievementsBinding.inflate(inflater, container, false)
        app = requireActivity().application as MyApplication
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        displayAchievements()
    }

    private fun setupRecyclerView() {
        adapter = AchievementAdapter()
        binding.recyclerViewAchievements.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewAchievements.adapter = adapter
    }

    private fun displayAchievements() {
        val achievements = DataGenerator.generateAchievements()
        val visitedCount = app.landmarksList.count { it.isVisited }
        val totalPoints = app.landmarksList.filter { it.isVisited }.sumOf { it.pointValue }
        
        // Check which achievements are unlocked
        achievements.forEach { achievement ->
            achievement.isUnlocked = when {
                achievement.requiredLandmarks > 0 -> visitedCount >= achievement.requiredLandmarks
                achievement.requiredPoints > 0 -> totalPoints >= achievement.requiredPoints
                else -> false
            }
        }
        
        adapter.updateData(achievements.sortedByDescending { it.isUnlocked })
        
        val unlockedCount = achievements.count { it.isUnlocked }
        binding.tvAchievementCount.text = "$unlockedCount / ${achievements.size} achievements unlocked"
    }

    override fun onResume() {
        super.onResume()
        displayAchievements()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
