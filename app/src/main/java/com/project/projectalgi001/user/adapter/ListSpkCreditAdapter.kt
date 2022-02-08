package com.project.projectalgi001.user.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.projectalgi001.databinding.ItemSpkListCreditBinding
import com.project.projectalgi001.user.model.FetchSpkCreditModel
import com.project.projectalgi001.user.ui.spk.DetailSpkCreditActivity

class ListSpkCreditAdapter(private val listSpkCredit : ArrayList<FetchSpkCreditModel>) : RecyclerView.Adapter<ListSpkCreditAdapter.ListViewHolder>() {

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

            val intent = Intent(it.context, DetailSpkCreditActivity::class.java)
            intent.putExtra(DetailSpkCreditActivity.EXTRA_SPK_CREDIT, spkCreditIntent)
            intent.putExtra(NAME_CAR, spkCredit.nameCarSoldCredit)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listSpkCredit.size
}