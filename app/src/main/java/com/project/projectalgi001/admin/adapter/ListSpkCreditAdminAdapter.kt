package com.project.projectalgi001.admin.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.projectalgi001.admin.ui.listspk.DetailSpkCreditAdminActivity
import com.project.projectalgi001.databinding.ItemSpkListCreditBinding
import com.project.projectalgi001.user.model.FetchSpkCreditModel

class ListSpkCreditAdminAdapter(private val listSpkCredit : ArrayList<FetchSpkCreditModel>) : RecyclerView.Adapter<ListSpkCreditAdminAdapter.ListViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setData(items : ArrayList<FetchSpkCreditModel>) {
        listSpkCredit.clear()
        listSpkCredit.addAll(items)
        notifyDataSetChanged()
    }

    companion object {
        const val NAME_CAR = "name_car"
    }

    class ListViewHolder(val binding: ItemSpkListCreditBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(spkCredit: FetchSpkCreditModel) {
            binding.nameItemBuyerCredit.text = spkCredit.nameBuyerCredit
            binding.carSoldItemCredit.text = spkCredit.nameCarSoldCredit
            binding.additionalNotesCreditItem.text = spkCredit.creditAdditionalNotes
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemSpkListCreditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listSpkCredit[position])
        val spkCredit = listSpkCredit[position]
        holder.itemView.setOnClickListener {
            val spkCreditIntent = FetchSpkCreditModel(
                spkCredit.dateCredit,
                spkCredit.imageResourceCredit,
                spkCredit.nameBuyerCredit,
                spkCredit.addressBuyerCredit,
                spkCredit.mobileNumberBuyerCredit,
                spkCredit.emailBuyerCredit,
                spkCredit.nameCarSoldCredit,
                spkCredit.policeNumberCarSoldCredit,
                spkCredit.machineNumberCarSoldCredit,
                spkCredit.chassisNumberCarSoldCredit,
                spkCredit.priceCarSoldCredit,
                spkCredit.finance,
                spkCredit.creditDiscount,
                spkCredit.creditNetOfDiscount,
                spkCredit.creditPrepayment,
                spkCredit.creditDownPayment,
                spkCredit.downPaymentRemaining,
                spkCredit.creditTenor,
                spkCredit.creditMonthlyInstallment,
                spkCredit.creditAdditionalNotes
            )

            val intent = Intent(it.context, DetailSpkCreditAdminActivity::class.java)
            intent.putExtra(DetailSpkCreditAdminActivity.EXTRA_SPK_CREDIT_ADMIN, spkCreditIntent)
            intent.putExtra(DetailSpkCreditAdminActivity.NAME_CAR_ADMIN, spkCredit.nameCarSoldCredit)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listSpkCredit.size
}