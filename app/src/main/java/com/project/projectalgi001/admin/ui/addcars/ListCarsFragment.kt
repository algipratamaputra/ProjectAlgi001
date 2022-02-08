package com.project.projectalgi001.admin.ui.addcars

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.project.projectalgi001.admin.adapter.ListCarsAdapter
import com.project.projectalgi001.databinding.ListCarsFragmentBinding
import com.project.projectalgi001.user.model.Cars

class ListCarsFragment : Fragment() {
    private var _binding: ListCarsFragmentBinding? = null
    private val binding get() = _binding
    private lateinit var listViewModel: ListCarsViewModel
    private lateinit var adapter: ListCarsAdapter
    private var listViewHome: ArrayList<Cars> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ListCarsFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ListCarsAdapter(listViewHome)
        listViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ListCarsViewModel::class.java]

        getSearchCar()
        showRecyclerGrid()
        getListViewModel(adapter)
        getFetchCars()

        binding?.fabAddCars?.setOnClickListener {
            val intent = Intent(activity, AddCarsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLoading(view: Boolean){
        if (view){
            binding?.progressbarListAdmin?.visibility = View.VISIBLE
        }else{
            binding?.progressbarListAdmin?.visibility = View.GONE
        }
    }

    private fun getFetchCars() {
        listViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ListCarsViewModel::class.java]
        listViewModel.fetchCarsAgain()
        showLoading(true)
        getListViewModel(adapter)
        showRecyclerGrid()
    }

    private fun getListViewModel(adapter: ListCarsAdapter) {
        listViewModel.getCars().observe(viewLifecycleOwner, { Cars ->
            if (Cars != null) {
                adapter.setData(Cars)
                showLoading(false)
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showRecyclerGrid() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding?.recyclerViewCarAdmin?.layoutManager = GridLayoutManager(activity, 4)
        } else {
            binding?.recyclerViewCarAdmin?.layoutManager = GridLayoutManager(activity, 2)
        }
        binding?.recyclerViewCarAdmin?.setHasFixedSize(true)
        adapter.notifyDataSetChanged()
        binding?.recyclerViewCarAdmin?.adapter = adapter
    }

    private fun getSearchCar() {
        binding?.searchCarAdmin?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) {
                    listViewHome.clear()
                    listViewModel.searchCar(query)
                    getListViewModel(adapter)
                    showRecyclerGrid()
                }else {
                    return true
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }
}