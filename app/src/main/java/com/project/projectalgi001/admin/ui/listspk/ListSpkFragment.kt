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
import com.project.projectalgi001.admin.adapter.ListSpkCashAdminAdapter
import com.project.projectalgi001.databinding.FragmentListSpkBinding
import com.project.projectalgi001.user.model.FetchSpkCashModel

class ListSpkFragment : Fragment() {

    private lateinit var spkListViewModel: ListSpkViewModel
    private lateinit var adapter: ListSpkCashAdminAdapter
    private var listViewSpkCash: ArrayList<FetchSpkCashModel> = ArrayList()
    private var _binding: FragmentListSpkBinding? = null
    private val binding get() = _binding
    private var customProggress : Dialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentListSpkBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ListSpkCashAdminAdapter(listViewSpkCash)
        spkListViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ListSpkViewModel::class.java]

        customProggress = Dialog(requireContext())
        customProggress?.setContentView(R.layout.progress_custom)

        showRecyclerList()
        getSpkCashViewModel(adapter)
        fetchSpkCash()
        getSearchSpkCash()
    }

    private fun getSearchSpkCash() {
        binding?.searchSpkAdminCash?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) {
                    listViewSpkCash.clear()
                    spkListViewModel.searchSpkCashViewModel(query)
                    getSpkCashViewModel(adapter)
                    showRecyclerList()
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

    private fun fetchSpkCash() {
        spkListViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ListSpkViewModel::class.java]
        spkListViewModel.fetchSpkCashViewModel()
        customProggress?.show()
        getSpkCashViewModel(adapter)
        showRecyclerList()
    }

    private fun getSpkCashViewModel(adapter: ListSpkCashAdminAdapter) {
        spkListViewModel.getSpkCash().observe(viewLifecycleOwner, { FetchSpkCashModel ->
            if (FetchSpkCashModel != null)
                adapter.setData(FetchSpkCashModel)
            customProggress?.dismiss()
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showRecyclerList() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding?.recyclerViewSpkAdminCashList?.layoutManager = GridLayoutManager(activity, 2)
        } else {
            binding?.recyclerViewSpkAdminCashList?.layoutManager = GridLayoutManager(activity, 1)
        }
        binding?.recyclerViewSpkAdminCashList?.setHasFixedSize(true)
        adapter.notifyDataSetChanged()
        binding?.recyclerViewSpkAdminCashList?.adapter = adapter
    }
}