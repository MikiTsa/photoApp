package com.example.sliknisi.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lib.Landmark
import com.example.sliknisi.databinding.ItemNearbyLandmarkBinding
import com.example.sliknisi.utils.DistanceUtils

class NearbyLandmarkAdapter(
    private val onLandmarkClick: (Landmark) -> Unit
) : RecyclerView.Adapter<NearbyLandmarkAdapter.ViewHolder>() {

    private var landmarks: List<Pair<Landmark, Double>> = emptyList()

    class ViewHolder(val binding: ItemNearbyLandmarkBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNearbyLandmarkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (landmark, distance) = landmarks[position]
        
        with(holder.binding) {
            tvLandmarkName.text = landmark.name
            tvCategory.text = landmark.category.name
            tvDistance.text = DistanceUtils.formatDistanceWithSuffix(distance.toFloat())
            tvPoints.text = "${landmark.pointValue} points"
            tvPosition.text = "${position + 1}"
            
            root.setOnClickListener {
                onLandmarkClick(landmark)
            }
        }
    }

    override fun getItemCount(): Int = landmarks.size

    fun updateData(newLandmarks: List<Pair<Landmark, Double>>) {
        landmarks = newLandmarks
        notifyDataSetChanged()
    }
}
