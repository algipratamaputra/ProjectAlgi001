package com.project.projectalgi001.admin.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.projectalgi001.admin.ui.addcars.DetailCarsAdminActivity
import com.project.projectalgi001.databinding.ItemCarBinding
import com.project.projectalgi001.user.model.Cars
import java.text.NumberFormat
import java.util.*

class ListCarsAdapter(private val listCars: ArrayList<Cars>): RecyclerView.Adapter<ListCarsAdapter.ListViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setData(items : ArrayList<Cars>) {
        listCars.clear()
        listCars.addAll(items)
        notifyDataSetChanged()
    }

    companion object {
        const val EXTRA_NAME = "extra_name"
    }

    inner class ListViewHolder (val binding: ItemCarBinding) : RecyclerView.ViewHolder(binding.root) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListCarsAdapter.ListViewHolder {
        val binding = ItemCarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listCars[position])
        val cars = listCars[position]
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

                val intent = Intent(it.context, DetailCarsAdminActivity::class.java)
                intent.putExtra(DetailCarsAdminActivity.EXTRA_CARS_ADMIN, carsIntent)
                intent.putExtra(EXTRA_NAME, cars.nameCars)
                it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listCars.size
}