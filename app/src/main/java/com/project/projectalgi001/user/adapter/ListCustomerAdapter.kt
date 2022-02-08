package com.project.projectalgi001.user.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.projectalgi001.databinding.ItemCustomerListBinding
import com.project.projectalgi001.user.model.FetchCustomerModel
import com.project.projectalgi001.user.ui.customerList.DetailCustomerActivity

class ListCustomerAdapter(private val listCustomer : ArrayList<FetchCustomerModel>) : RecyclerView.Adapter<ListCustomerAdapter.ListViewHolder>() {

    companion object {
        const val NAME_CUSTOMER = "name_customer"
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items : ArrayList<FetchCustomerModel>) {
        listCustomer.clear()
        listCustomer.addAll(items)
        notifyDataSetChanged()
    }

    class ListViewHolder(val binding: ItemCustomerListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(customer: FetchCustomerModel) {
            binding.nameItemCustomer.text = customer.nameCustomer
            binding.carBrandItem.text = customer.brandCar
            binding.descriptionItem.text = customer.descriptionCustomer
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemCustomerListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listCustomer[position])
        val customer = listCustomer[position]
        holder.itemView.setOnClickListener {
            val customerIntent = FetchCustomerModel(
                customer.customerId,
                customer.userId,
                customer.nameCustomer,
                customer.addressCustomer,
                customer.mobileNumberCustomer,
                customer.emailCustomer,
                customer.brandCar,
                customer.descriptionCustomer)

            val intent = Intent(it.context, DetailCustomerActivity::class.java)
            intent.putExtra(DetailCustomerActivity.EXTRA_CUSTOMER, customerIntent)
            intent.putExtra(NAME_CUSTOMER, customer.nameCustomer)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listCustomer.size
}