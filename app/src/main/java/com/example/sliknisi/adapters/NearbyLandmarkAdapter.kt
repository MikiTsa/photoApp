package com.example.sliknisi.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lib.Landmark
import com.example.sliknisi.databinding.ItemNearbyLandmarkBinding

class NearbyLandmarkAdapter : RecyclerView.Adapter<NearbyLandmarkAdapter.ViewHolder>() {

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
            tvDistance.text = String.format("%.1f km away", distance)
            tvPoints.text = "${landmark.pointValue} points"
            
            // Position indicator (1st, 2nd, 3rd, etc.)
            tvPosition.text = "${position + 1}"
        }
    }

    override fun getItemCount(): Int = landmarks.size

    fun updateData(newLandmarks: List<Pair<Landmark, Double>>) {
        landmarks = newLandmarks
        notifyDataSetChanged()
    }
}
