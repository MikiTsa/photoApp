package com.example.sliknisi.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lib.Achievement
import com.example.sliknisi.R
import com.example.sliknisi.databinding.ItemAchievementBinding

class AchievementAdapter : RecyclerView.Adapter<AchievementAdapter.ViewHolder>() {

    private var achievements: List<Achievement> = emptyList()

    class ViewHolder(val binding: ItemAchievementBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAchievementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val achievement = achievements[position]
        
        with(holder.binding) {
            tvAchievementName.text = achievement.name
            tvAchievementDescription.text = achievement.description
            
            if (achievement.isUnlocked) {
                tvStatus.text = "✓ Unlocked"
                tvStatus.setTextColor(holder.itemView.context.getColor(R.color.green_dark))
                ivTrophy.alpha = 1.0f
                cardView.alpha = 1.0f
            } else {
                tvStatus.text = "🔒 Locked"
                tvStatus.setTextColor(holder.itemView.context.getColor(R.color.text_secondary))
                ivTrophy.alpha = 0.3f
                cardView.alpha = 0.7f
            }
            
            // Show requirements
            val requirement = when {
                achievement.requiredLandmarks > 0 -> 
                    "Visit ${achievement.requiredLandmarks} landmarks"
                achievement.requiredPoints > 0 -> 
                    "Earn ${achievement.requiredPoints} points"
                else -> "Complete challenge"
            }
            tvRequirement.text = requirement
        }
    }

    override fun getItemCount(): Int = achievements.size

    fun updateData(newAchievements: List<Achievement>) {
        achievements = newAchievements
        notifyDataSetChanged()
    }
}
