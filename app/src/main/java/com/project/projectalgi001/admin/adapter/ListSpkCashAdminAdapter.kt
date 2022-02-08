package com.project.projectalgi001.admin.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.projectalgi001.admin.ui.listspk.DetailSpkCashAdminActivity
import com.project.projectalgi001.databinding.ItemSpkListCashBinding
import com.project.projectalgi001.user.adapter.ListSpkCashAdapter
import com.project.projectalgi001.user.model.FetchSpkCashModel

class ListSpkCashAdminAdapter(private val listSpkCash : ArrayList<FetchSpkCashModel>) : RecyclerView.Adapter<ListSpkCashAdminAdapter.ListViewHolder>() {
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items : ArrayList<FetchSpkCashModel>) {
        listSpkCash.clear()
        listSpkCash.addAll(items)
        notifyDataSetChanged()
    }

    companion object {
        const val NAME_CAR = "name_car"
    }

    class ListViewHolder(val binding: ItemSpkListCashBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(spkCash: FetchSpkCashModel) {
            binding.nameItemBuyer.text = spkCash.buyerNameCash
            binding.carSoldItem.text = spkCash.carNameCash
            binding.additionalNotesItem.text = spkCash.notesAdditional
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemSpkListCashBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listSpkCash[position])
        val spkCash = listSpkCash[position]
        holder.itemView.setOnClickListener {
            val spkCashIntent = FetchSpkCashModel(
                spkCash.dateCash,
                spkCash.resourceImageCash,
                spkCash.buyerNameCash,
                spkCash.buyerAddressCash,
                spkCash.buyerMobileNumberCash,
                spkCash.buyerEmailCash,
                spkCash.carNameCash,
                spkCash.carPoliceNumberCash,
                spkCash.carMachineNumberCash,
                spkCash.carChassisNumberCash,
                spkCash.carPriceCash,
                spkCash.cashDiscount,
                spkCash.netOfDiscountCash,
                spkCash.cashPrepayment,
                spkCash.plantDelivery,
                spkCash.paymentRemaining,
                spkCash.notesAdditional)

            val intent = Intent(it.context, DetailSpkCashAdminActivity::class.java)
            intent.putExtra(DetailSpkCashAdminActivity.EXTRA_SPK_CASH_ADMIN, spkCashIntent)
            intent.putExtra(ListSpkCashAdapter.NAME_CAR, spkCash.carNameCash)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listSpkCash.size
}