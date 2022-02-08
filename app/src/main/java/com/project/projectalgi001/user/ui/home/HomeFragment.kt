package com.project.projectalgi001.user.ui.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.project.projectalgi001.R
import com.project.projectalgi001.databinding.FragmentHomeBinding
import com.project.projectalgi001.user.adapter.GridCarsAdapter
import com.project.projectalgi001.user.model.Cars

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adapter: GridCarsAdapter
    private var listViewHome: ArrayList<Cars> = ArrayList()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private var customProggress : Dialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customProggress = Dialog(requireContext())
        customProggress?.setContentView(R.layout.progress_custom)

        adapter = GridCarsAdapter(listViewHome)
        homeViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[HomeViewModel::class.java]

        getSearchCar()
        showRecyclerGrid()
        getHomeViewModel(adapter)
        getFetchCars()
    }

    private fun getHomeViewModel(adapter: GridCarsAdapter) {
        homeViewModel.getCars().observe(viewLifecycleOwner, { Cars ->
            if (Cars != null) {
                adapter.setData(Cars)
                customProggress?.dismiss()
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showRecyclerGrid() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding?.recyclerViewCar?.layoutManager = GridLayoutManager(activity, 4)
        } else {
            binding?.recyclerViewCar?.layoutManager = GridLayoutManager(activity, 2)
        }
        binding?.recyclerViewCar?.setHasFixedSize(true)
        adapter.notifyDataSetChanged()
        binding?.recyclerViewCar?.adapter = adapter
    }

    private fun getSearchCar(){
        binding?.searchCar?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) {
                    listViewHome.clear()
                    homeViewModel.searchCar(query)
                    getHomeViewModel(adapter)
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

    private fun getFetchCars() {
        homeViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[HomeViewModel::class.java]
        homeViewModel.fetchCarsAgain()
        customProggress?.show()
        if (!isNetworkAvailable(requireContext())){
            showDialogConnection()
        }
        getHomeViewModel(adapter)
        showRecyclerGrid()
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw      = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }

        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }

    private fun showDialogConnection() {
        val alertBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        alertBuilder.setTitle(getString(R.string.no_connection_title))
        alertBuilder.setMessage(getString(R.string.no_connection))
        alertBuilder.setPositiveButton(getString(R.string.retry)) { _, _ ->
            startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        }
        alertBuilder.show()
    }
}