package com.project.projectalgi001.admin.adapter

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
import com.project.projectalgi001.admin.model.CarsModel
import com.project.projectalgi001.databinding.ItemGridCarsUploadedBinding

class AddCarsAdapter(val gridImagesUploaded : ArrayList<CarsModel>): RecyclerView.Adapter<AddCarsAdapter.AddCarsViewHolder>() {

    var tracker: SelectionTracker<CarsModel>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddCarsViewHolder {
        val binding = ItemGridCarsUploadedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddCarsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddCarsViewHolder, position: Int) {

        val parentOne = holder.parentCheck
        val parentTwo = holder.parentImage

        tracker?.let {
            holder.bind(gridImagesUploaded[position])
            if (it.isSelected(gridImagesUploaded[position])) {
                parentOne.visibility = View.VISIBLE
                parentTwo.background = ColorDrawable(Color.parseColor("#07575B"))
            } else {
                parentOne.visibility = View.GONE
                parentTwo.background = ColorDrawable(Color.TRANSPARENT)
            }
        }
    }

    override fun getItemCount(): Int = gridImagesUploaded.size

    inner class AddCarsViewHolder(val binding: ItemGridCarsUploadedBinding): RecyclerView.ViewHolder(binding.root){

        var parentCheck = binding.checkImage
        var parentImage = binding.imgUploadedCar

        fun bind(gridImagesUploaded: CarsModel){
            Glide.with(binding.imgUploadedCar)
                    .load(gridImagesUploaded.image)
                    .into(binding.imgUploadedCar)
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<CarsModel> =
            object : ItemDetailsLookup.ItemDetails<CarsModel>() {
                override fun getPosition(): Int = absoluteAdapterPosition
                override fun getSelectionKey(): CarsModel = gridImagesUploaded[position]
            }
    }

    fun getItem(position: Int) = gridImagesUploaded[position]
    fun getPosition(image: String?) = gridImagesUploaded.indexOfFirst { it.image == image }
    @SuppressLint("NotifyDataSetChanged")
    fun deleteImage(selectedPostItems: MutableList<CarsModel>?) {
        if (selectedPostItems != null) {
            for (i in 0 until selectedPostItems.size) {
                gridImagesUploaded.remove(selectedPostItems[i])
            }
        }
        notifyDataSetChanged()
        selectedPostItems?.clear()
    }
}