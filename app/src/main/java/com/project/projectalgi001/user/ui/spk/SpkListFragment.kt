package com.project.projectalgi001.user.ui.spk

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
import com.project.projectalgi001.databinding.FragmentSpkListBinding
import com.project.projectalgi001.user.adapter.ListSpkCashAdapter
import com.project.projectalgi001.user.model.FetchSpkCashModel

class SpkListFragment : Fragment() {

    private lateinit var spkListViewModel: SpkListViewModel
    private lateinit var adapter: ListSpkCashAdapter
    private var listViewSpkCash: ArrayList<FetchSpkCashModel> = ArrayList()
    private var _binding: FragmentSpkListBinding? = null
    private val binding get() = _binding
    private var customProggress : Dialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSpkListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customProggress = Dialog(requireContext())
        customProggress?.setContentView(R.layout.progress_custom)

        adapter = ListSpkCashAdapter(listViewSpkCash)
        spkListViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[SpkListViewModel::class.java]

        showRecyclerList()
        getSpkCashViewModel(adapter)
        fetchSpkCash()
        getSearchSpkCash()
    }

    private fun fetchSpkCash() {
        spkListViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[SpkListViewModel::class.java]
        spkListViewModel.fetchSpkCashViewModel()
        customProggress?.show()
        getSpkCashViewModel(adapter)
        showRecyclerList()
    }

    private fun getSearchSpkCash() {
        binding?.searchSpkCash?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
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

    private fun getSpkCashViewModel(adapter: ListSpkCashAdapter) {
        spkListViewModel.getSpkCash().observe(viewLifecycleOwner, { FetchSpkCashModel ->
            if (FetchSpkCashModel != null)
                adapter.setData(FetchSpkCashModel)
            customProggress?.dismiss()
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showRecyclerList() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding?.recyclerViewSpkCashList?.layoutManager = GridLayoutManager(activity, 2)
        } else {
            binding?.recyclerViewSpkCashList?.layoutManager = GridLayoutManager(activity, 1)
        }
        binding?.recyclerViewSpkCashList?.setHasFixedSize(true)
        adapter.notifyDataSetChanged()
        binding?.recyclerViewSpkCashList?.adapter = adapter
    }
}