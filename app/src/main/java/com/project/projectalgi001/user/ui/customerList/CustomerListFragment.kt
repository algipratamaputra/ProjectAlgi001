package com.project.projectalgi001.user.ui.customerList

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
import com.project.projectalgi001.databinding.FragmentCustomerListBinding
import com.project.projectalgi001.user.adapter.ListCustomerAdapter
import com.project.projectalgi001.user.model.FetchCustomerModel

class CustomerListFragment : Fragment() {

    private lateinit var customerListViewModel: CustomerListViewModel
    private lateinit var adapter: ListCustomerAdapter
    private var listViewCustomer: ArrayList<FetchCustomerModel> = ArrayList()
    private var _binding: FragmentCustomerListBinding? = null
    private val binding get() = _binding
    private var customProggress : Dialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCustomerListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customProggress = Dialog(requireContext())
        customProggress?.setContentView(R.layout.progress_custom)

        adapter = ListCustomerAdapter(listViewCustomer)
        customerListViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[CustomerListViewModel::class.java]

        getSearchCustomer()
        showRecyclerList()
        getCustomerViewModel(adapter)
        fetchCustomer()
    }

    private fun fetchCustomer() {
        customerListViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[CustomerListViewModel::class.java]
        customerListViewModel.fetchCustomerViewModel()
        customProggress?.show()
        getCustomerViewModel(adapter)
        showRecyclerList()
    }

    private fun getSearchCustomer() {
        binding?.searchCustomer?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) {
                    listViewCustomer.clear()
                    customerListViewModel.searchCustomer(query)
                    getCustomerViewModel(adapter)
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

    private fun getCustomerViewModel(adapter: ListCustomerAdapter) {
        customerListViewModel.getCustomer().observe(viewLifecycleOwner, { FetchCustomerModel ->
            if (FetchCustomerModel != null)
                adapter.setData(FetchCustomerModel)
                customProggress?.dismiss()
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showRecyclerList() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding?.recyclerViewCustomerList?.layoutManager = GridLayoutManager(activity, 2)
        } else {
            binding?.recyclerViewCustomerList?.layoutManager = GridLayoutManager(activity, 1)
        }
        binding?.recyclerViewCustomerList?.setHasFixedSize(true)
        adapter.notifyDataSetChanged()
        binding?.recyclerViewCustomerList?.adapter = adapter
    }
}