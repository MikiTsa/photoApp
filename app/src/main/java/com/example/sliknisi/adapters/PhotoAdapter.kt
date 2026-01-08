package com.example.sliknisi.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lib.Photo
import com.example.sliknisi.databinding.ItemPhotoBinding

class PhotoAdapter(
    private val photos: List<Photo>
) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = photos[position]
        
        try {
            val uri = Uri.parse(photo.filePath)
            holder.binding.ivPhoto.setImageURI(uri)
        } catch (e: Exception) {
            // If image fails to load, keep default placeholder
        }
    }

    override fun getItemCount(): Int = photos.size
}
