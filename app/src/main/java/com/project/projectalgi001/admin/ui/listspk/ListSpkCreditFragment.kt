package com.project.projectalgi001.admin.ui.listspk

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.project.projectalgi001.R
import com.project.projectalgi001.admin.adapter.ListSpkCreditAdminAdapter
import com.project.projectalgi001.databinding.FragmentListSpkCreditBinding
import com.project.projectalgi001.user.model.FetchSpkCreditModel

class ListSpkCreditFragment : Fragment() {

    private lateinit var spkListCreditViewModel: ListSpkCreditViewModel
    private lateinit var adapter: ListSpkCreditAdminAdapter
    private var listViewSpkCredit: ArrayList<FetchSpkCreditModel> = ArrayList()
    private var _binding: FragmentListSpkCreditBinding? = null
    private val binding get() = _binding
    private var customProggress : Dialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentListSpkCreditBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customProggress = Dialog(requireContext())
        customProggress?.setContentView(R.layout.progress_custom)

        adapter = ListSpkCreditAdminAdapter(listViewSpkCredit)
        spkListCreditViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ListSpkCreditViewModel::class.java]

        showRecyclerListCredit()
        getSpkCreditViewModel(adapter)
        fetchSpkCredit()
        getSearchSpkCredit()
    }

    private fun fetchSpkCredit() {
        spkListCreditViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ListSpkCreditViewModel::class.java]
        spkListCreditViewModel.fetchSpkCreditViewModel()
        customProggress?.show()
        getSpkCreditViewModel(adapter)
        showRecyclerListCredit()
    }

    private fun getSearchSpkCredit() {
        binding?.searchSpkAdminCredit?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) {
                    listViewSpkCredit.clear()
                    spkListCreditViewModel.searchSpkCreditViewModel(query)
                    getSpkCreditViewModel(adapter)
                    showRecyclerListCredit()
                } else{
                    return true
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }

    private fun getSpkCreditViewModel(adapter: ListSpkCreditAdminAdapter) {
        spkListCreditViewModel.getSpkCredit().observe(viewLifecycleOwner, { FetchSpkCreditModel ->
            if (FetchSpkCreditModel != null)
                adapter.setData(FetchSpkCreditModel)
            customProggress?.dismiss()
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showRecyclerListCredit() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding?.recyclerViewSpkAdminCreditList?.layoutManager = GridLayoutManager(activity, 2)
        } else {
            binding?.recyclerViewSpkAdminCreditList?.layoutManager = GridLayoutManager(activity, 1)
        }
        binding?.recyclerViewSpkAdminCreditList?.setHasFixedSize(true)
        adapter.notifyDataSetChanged()
        binding?.recyclerViewSpkAdminCreditList?.adapter = adapter
    }
}