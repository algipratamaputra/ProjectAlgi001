package com.project.projectalgi001.user.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.projectalgi001.databinding.ItemGridResourceSpkBinding
import com.project.projectalgi001.user.model.ResourceModel

class ResourceSpkAdapter(val gridImagesUploadedResource : ArrayList<ResourceModel>): RecyclerView.Adapter<ResourceSpkAdapter.ResourceSpkViewHolder>() {

    var trackerResource: SelectionTracker<ResourceModel>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceSpkViewHolder {
        val binding = ItemGridResourceSpkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResourceSpkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResourceSpkViewHolder, position: Int) {

        val parentOne = holder.parentCheck
        val parentTwo = holder.parentImage

        trackerResource?.let {
            holder.bind(gridImagesUploadedResource[position])
            if (it.isSelected(gridImagesUploadedResource[position])) {
                parentOne.visibility = View.VISIBLE
                parentTwo.background = ColorDrawable(Color.parseColor("#07575B"))
            } else {
                parentOne.visibility = View.GONE
                parentTwo.background = ColorDrawable(Color.TRANSPARENT)
            }
        }
    }

    override fun getItemCount(): Int = gridImagesUploadedResource.size

    inner class ResourceSpkViewHolder(val binding : ItemGridResourceSpkBinding): RecyclerView.ViewHolder(binding.root) {

        var parentCheck = binding.checkImageResource
        var parentImage = binding.imgUploadedResource

        fun bind(gridImagesUploadedResource: ResourceModel){
            Glide.with(binding.imgUploadedResource)
                .load(gridImagesUploadedResource.imageResource)
                .into(binding.imgUploadedResource)
        }

        fun getItemResourceDetails(): ItemDetailsLookup.ItemDetails<ResourceModel> =
            object : ItemDetailsLookup.ItemDetails<ResourceModel>() {
                override fun getPosition(): Int = absoluteAdapterPosition
                override fun getSelectionKey(): ResourceModel = gridImagesUploadedResource[position]
            }
    }

    fun getItemResource(position: Int) = gridImagesUploadedResource[position]
    fun getPositionResource(image: String?) = gridImagesUploadedResource.indexOfFirst { it.imageResource == image }
    @SuppressLint("NotifyDataSetChanged")
    fun deleteImage(selectedPostItems: MutableList<ResourceModel>?) {
        if (selectedPostItems != null) {
            for (i in 0 until selectedPostItems.size) {
                gridImagesUploadedResource.remove(selectedPostItems[i])
            }
        }
        notifyDataSetChanged()
        selectedPostItems?.clear()
    }
}