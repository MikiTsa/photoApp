package com.example.sliknisi.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lib.Landmark
import com.example.sliknisi.MyApplication
import com.example.sliknisi.databinding.ItemLandmarkBinding

class LandmarkAdapter(
    private val app: MyApplication,
    private val onItemClick: (Landmark) -> Unit
) : RecyclerView.Adapter<LandmarkAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemLandmarkBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLandmarkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val landmark = app.landmarksList[position]

        with(holder.binding) {
            tvLandmarkName.text = landmark.name
            tvCategory.text = landmark.category.name
            tvAddress.text = "${landmark.address}, ${landmark.city}"
            tvPoints.text = "${landmark.pointValue} points"

            if (landmark.isVisited) {
                tvVisitedStatus.text = "✓ Visited"
                tvVisitedStatus.setTextColor(
                    holder.itemView.context.getColor(android.R.color.holo_green_dark)
                )
            } else {
                tvVisitedStatus.text = "Not visited"
                tvVisitedStatus.setTextColor(
                    holder.itemView.context.getColor(android.R.color.darker_gray)
                )
            }

            cvLandmarkItem.setOnClickListener {
                onItemClick(landmark)
            }

            cvLandmarkItem.setOnLongClickListener {
                showDeleteConfirmation(landmark, holder.bindingAdapterPosition, holder.itemView)
                true
            }
        }
    }

    override fun getItemCount(): Int = app.landmarksList.size

    private fun showDeleteConfirmation(landmark: Landmark, position: Int, view: View) {
        AlertDialog.Builder(view.context)
            .setTitle("Delete Landmark")
            .setMessage("Are you sure you want to delete ${landmark.name}?")
            .setPositiveButton("Delete") { dialog, _ ->
                app.deleteLandmark(landmark.id)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, app.landmarksList.size)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }
}