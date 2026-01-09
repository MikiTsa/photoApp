package com.example.sliknisi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lib.Landmark
import com.example.lib.LandmarkCategory
import com.example.sliknisi.MyApplication
import com.example.sliknisi.R
import com.example.sliknisi.databinding.FragmentLandmarkEditBinding

class LandmarkEditFragment : Fragment() {

    private var _binding: FragmentLandmarkEditBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var app: MyApplication
    private var isEditMode = false
    private var landmarkId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLandmarkEditBinding.inflate(inflater, container, false)
        app = requireActivity().application as MyApplication
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupCategorySpinner()
        loadDataFromArguments()
        setupSaveButton()
        setupCancelButton()
    }

    private fun setupCategorySpinner() {
        val categories = LandmarkCategory.values().map { it.name }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    private fun loadDataFromArguments() {
        arguments?.let { args ->
            landmarkId = args.getString("landmarkId")
            isEditMode = args.getBoolean("isEditing", false)
            
            if (isEditMode) {
                binding.tvTitle.text = getString(R.string.edit_landmark_title)
                binding.btnSave.text = getString(R.string.action_update)
                
                landmarkId?.let { id ->
                    app.findLandmarkById(id)?.let { landmark ->
                        binding.etLandmarkName.setText(landmark.name)
                        binding.etDescription.setText(landmark.description)
                        binding.etAddress.setText(landmark.address)
                        binding.etCity.setText(landmark.city)
                        binding.etLatitude.setText(landmark.latitude.toString())
                        binding.etLongitude.setText(landmark.longitude.toString())
                        binding.etPoints.setText(landmark.pointValue.toString())
                        
                        val categoryPosition = LandmarkCategory.values().indexOfFirst { 
                            it.name == landmark.category.name 
                        }
                        if (categoryPosition != -1) {
                            binding.spinnerCategory.setSelection(categoryPosition)
                        }
                    }
                }
            } else {
                binding.tvTitle.text = getString(R.string.add_landmark_title)
                binding.btnSave.text = getString(R.string.action_save)
            }
        }
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            if (validateInput()) {
                saveLandmark()
            }
        }
    }

    private fun setupCancelButton() {
        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun validateInput(): Boolean {
        val name = binding.etLandmarkName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val city = binding.etCity.text.toString().trim()
        val latitudeStr = binding.etLatitude.text.toString().trim()
        val longitudeStr = binding.etLongitude.text.toString().trim()
        val pointsStr = binding.etPoints.text.toString().trim()

        if (name.isEmpty()) {
            binding.etLandmarkName.error = getString(R.string.msg_field_required)
            return false
        }

        if (description.isEmpty()) {
            binding.etDescription.error = getString(R.string.msg_field_required)
            return false
        }

        if (address.isEmpty()) {
            binding.etAddress.error = getString(R.string.msg_field_required)
            return false
        }

        if (city.isEmpty()) {
            binding.etCity.error = getString(R.string.msg_field_required)
            return false
        }

        val latitude = latitudeStr.toDoubleOrNull()
        if (latitude == null) {
            binding.etLatitude.error = "Valid latitude required"
            return false
        }

        val longitude = longitudeStr.toDoubleOrNull()
        if (longitude == null) {
            binding.etLongitude.error = "Valid longitude required"
            return false
        }

        val points = pointsStr.toIntOrNull()
        if (points == null || points <= 0) {
            binding.etPoints.error = "Valid points required"
            return false
        }

        return true
    }

    private fun saveLandmark() {
        val name = binding.etLandmarkName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val city = binding.etCity.text.toString().trim()
        val latitude = binding.etLatitude.text.toString().toDouble()
        val longitude = binding.etLongitude.text.toString().toDouble()
        val points = binding.etPoints.text.toString().toInt()
        val category = LandmarkCategory.valueOf(binding.spinnerCategory.selectedItem.toString())

        if (isEditMode && landmarkId != null) {
            val updatedLandmark = Landmark(
                id = landmarkId!!,
                name = name,
                description = description,
                latitude = latitude,
                longitude = longitude,
                category = category,
                pointValue = points,
                city = city,
                address = address,
                isVisited = app.findLandmarkById(landmarkId!!)?.isVisited ?: false
            )
            app.updateLandmark(updatedLandmark)
            Toast.makeText(requireContext(), "Landmark updated!", Toast.LENGTH_SHORT).show()
        } else {
            val newLandmark = Landmark(
                name = name,
                description = description,
                latitude = latitude,
                longitude = longitude,
                category = category,
                pointValue = points,
                city = city,
                address = address
            )
            app.addLandmark(newLandmark)
            Toast.makeText(requireContext(), "Landmark added!", Toast.LENGTH_SHORT).show()
        }

        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
