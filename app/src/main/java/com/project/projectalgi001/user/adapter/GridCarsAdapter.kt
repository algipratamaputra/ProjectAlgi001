package com.project.projectalgi001.user.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.projectalgi001.databinding.ItemCarBinding
import com.project.projectalgi001.user.model.Cars
import com.project.projectalgi001.user.ui.home.CarsDetailsActivity
import java.text.NumberFormat
import java.util.*

class GridCarsAdapter(private val gridCars: ArrayList<Cars>): RecyclerView.Adapter<GridCarsAdapter.GridViewHolder>() {
    companion object {
        const val VIDEO_TITLE_KEY = "VIDEO_TITLE"
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(items : ArrayList<Cars>) {
        gridCars.clear()
        gridCars.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val binding = ItemCarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GridViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        holder.bind(gridCars[position])
        val cars = gridCars[position]
        holder.itemView.setOnClickListener {
            val carsIntent = Cars(
                cars.carStatus,
                cars.nameCars,
                cars.numberPolice,
                cars.numberMachine,
                cars.numberChassis,
                cars.statusBpkb,
                cars.statusStnk,
                cars.priceCars,
                cars.creditPriceCars,
                cars.dpMinimum,
                cars.carsImage)

                val intent = Intent(it.context, CarsDetailsActivity::class.java)
                intent.putExtra(CarsDetailsActivity.EXTRA_CARS, carsIntent)
                intent.putExtra(VIDEO_TITLE_KEY, cars.nameCars)
                it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = gridCars.size

    inner class GridViewHolder(val binding: ItemCarBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cars: Cars) {
            Glide.with(itemView.context)
                    .load(cars.carsImage?.get("imagesCar0"))
                    .into(binding.imgItemCar)

            if (cars.carStatus!!){
                binding.statusCar.visibility = View.GONE
            }else{
                binding.statusCar.visibility = View.VISIBLE
            }

            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

            binding.nameItemCar.text = cars.nameCars
            binding.priceItemCar.text = formatRupiah.format(cars.priceCars?.toInt())
        }
    }
}