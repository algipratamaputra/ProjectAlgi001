package com.project.projectalgi001.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.projectalgi001.databinding.ItemSlideBinding

class ImageSliderAdapter(private val items : HashMap<String,String>) : RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: ItemSlideBinding) : RecyclerView.ViewHolder(itemView.root) {
        private val binding = itemView
        fun bind(data: String?) {
            with(binding){
                Glide.with(itemView)
                    .load(data)
                    .into(imageSliderr)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(ItemSlideBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(items["imagesCar$position"])
    }

    override fun getItemCount(): Int = items.size
}
