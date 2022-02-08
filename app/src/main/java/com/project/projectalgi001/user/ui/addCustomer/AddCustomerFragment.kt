package com.project.projectalgi001.user.ui.addCustomer

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.project.projectalgi001.R
import com.project.projectalgi001.databinding.FragmentAddCustomerBinding
import com.project.projectalgi001.user.model.CustomerModel
import com.project.projectalgi001.user.ui.customerList.CustomerListFragment

class AddCustomerFragment : Fragment() {

    private lateinit var addCustomerViewModel: AddCustomerViewModel
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentAddCustomerBinding? = null
    private val binding get() = _binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAddCustomerBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        addCustomerViewModel = ViewModelProvider(this)[AddCustomerViewModel::class.java]

        binding?.buttonSaveCustomer?.setOnClickListener {

            if (binding?.textInputEditTextAkNameCustomer?.text.toString().isEmpty()) {
                binding?.textInputEditTextAkNameCustomer?.error = getString(R.string.field_empty)
                binding?.textInputEditTextAkNameCustomer?.requestFocus()
                return@setOnClickListener
            }

            if (binding?.textInputEditTextAkAddressCustomer?.text.toString().isEmpty()) {
                binding?.textInputEditTextAkAddressCustomer?.error = getString(R.string.field_empty)
                binding?.textInputEditTextAkAddressCustomer?.requestFocus()
                return@setOnClickListener
            }

            if (binding?.textInputEditTextAkMobileNumberCustomer?.text.toString().isEmpty()) {
                binding?.textInputEditTextAkMobileNumberCustomer?.error = getString(R.string.field_empty)
                binding?.textInputEditTextAkMobileNumberCustomer?.requestFocus()
                return@setOnClickListener
            }

            if (binding?.textInputEditTextAkBrandCar?.text.toString().isEmpty()) {
                binding?.textInputEditTextAkBrandCar?.error = getString(R.string.field_empty)
                binding?.textInputEditTextAkBrandCar?.requestFocus()
                return@setOnClickListener
            }

            if (binding?.textInputEditTextAkDescription?.text.toString().isEmpty()) {
                binding?.textInputEditTextAkDescription?.error = getString(R.string.field_empty)
                binding?.textInputEditTextAkDescription?.requestFocus()
                return@setOnClickListener
            }

            if (!isNetworkAvailable(requireContext())){
                showDialogConnection()
            } else{
                saveCustomer()
                val mFragmentManager = parentFragmentManager
                val mListCustomer = CustomerListFragment()
                mFragmentManager.beginTransaction().apply {
                    replace(R.id.nav_host_fragment, mListCustomer, CustomerListFragment::class.java.simpleName)
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }

    private fun saveCustomer() {
        val uid = auth.currentUser?.uid
        val customer = CustomerModel().apply {
            idCustomer
            idUser = uid.toString()
            customerName = binding?.textInputEditTextAkNameCustomer?.text.toString()
            customerAddress = binding?.textInputEditTextAkAddressCustomer?.text.toString()
            customerMobileNumber = binding?.textInputEditTextAkMobileNumberCustomer?.text.toString()
            customerEmail = binding?.textInputEditTextAkEmailCustomer?.text.toString()
            carBrand = binding?.textInputEditTextAkBrandCar?.text.toString()
            description = binding?.textInputEditTextAkDescription?.text.toString()
        }
        addCustomerViewModel.save(customer)
    }

    private fun showDialogConnection() {
        val alertBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        alertBuilder.setTitle(getString(R.string.no_connection_title))
        alertBuilder.setMessage(getString(R.string.no_connection))
        alertBuilder.setPositiveButton(getString(R.string.oke)) { _, _ ->
            startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        }
        alertBuilder.show()
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
}