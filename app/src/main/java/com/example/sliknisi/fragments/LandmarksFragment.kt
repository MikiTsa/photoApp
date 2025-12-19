package com.example.sliknisi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lib.Landmark
import com.example.sliknisi.MyApplication
import com.example.sliknisi.R
import com.example.sliknisi.adapters.LandmarkAdapter
import com.example.sliknisi.databinding.FragmentLandmarksBinding

class LandmarksFragment : Fragment() {

    private var _binding: FragmentLandmarksBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var app: MyApplication
    private lateinit var adapter: LandmarkAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLandmarksBinding.inflate(inflater, container, false)
        app = requireActivity().application as MyApplication
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupFAB()
    }

    private fun setupRecyclerView() {
        adapter = LandmarkAdapter(
            app = app,
            onItemClick = { landmark -> handleLandmarkClick(landmark) }
        )
        
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupFAB() {
        binding.fabAddLandmark.setOnClickListener {
            // Navigate to edit fragment in "add" mode
            findNavController().navigate(
                R.id.action_addFragment_to_landmarkEditFragment,
                bundleOf("isEditing" to false)
            )
        }
    }

    private fun handleLandmarkClick(landmark: Landmark) {
        // Navigate to edit fragment in "edit" mode
        findNavController().navigate(
            R.id.action_addFragment_to_landmarkEditFragment,
            bundleOf(
                "landmarkId" to landmark.id,
                "isEditing" to true
            )
        )
    }

    override fun onResume() {
        super.onResume()
        // Refresh the list when returning to this fragment
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
