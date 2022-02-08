package com.project.projectalgi001.user.ui.customerList

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.projectalgi001.R
import com.project.projectalgi001.databinding.ActivityDetailCustomerBinding
import com.project.projectalgi001.user.MainActivity
import com.project.projectalgi001.user.adapter.ListCustomerAdapter
import com.project.projectalgi001.user.model.FetchCustomerModel

class DetailCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailCustomerBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var model : FetchCustomerModel

    companion object {
        const val EXTRA_CUSTOMER = "extra_customer"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailCustomerBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val toolbar: androidx.appcompat.widget.Toolbar = binding.toolbarDetailCustomer
        setSupportActionBar(toolbar)

        val navBarTitle = intent.getStringExtra(ListCustomerAdapter.NAME_CUSTOMER)
        supportActionBar?.title = navBarTitle

        auth = Firebase.auth

        setDetailCustomer()

        binding.buttonUpdateCustomer.setOnClickListener {
            if (binding.textInputEditTextDcNameCustomer.text.toString().isEmpty()) {
                binding.textInputEditTextDcNameCustomer.error = getString(R.string.field_empty)
                binding.textInputEditTextDcNameCustomer.requestFocus()
                return@setOnClickListener
            }

            if (binding.textInputEditTextDcAddressCustomer.text.toString().isEmpty()) {
                binding.textInputEditTextDcAddressCustomer.error = getString(R.string.field_empty)
                binding.textInputEditTextDcAddressCustomer.requestFocus()
                return@setOnClickListener
            }

            if (binding.textInputEditTextDcMobileNumberCustomer.text.toString().isEmpty()) {
                binding.textInputEditTextDcAddressCustomer.error = getString(R.string.field_empty)
                binding.textInputEditTextDcMobileNumberCustomer.requestFocus()
                return@setOnClickListener
            }

            if (binding.textInputEditTextDcBrandCar.text.toString().isEmpty()) {
                binding.textInputEditTextDcBrandCar.error = getString(R.string.field_empty)
                binding.textInputEditTextDcBrandCar.requestFocus()
                return@setOnClickListener
            }

            if (binding.textInputEditTextDcDescription.text.toString().isEmpty()) {
                binding.textInputEditTextDcDescription.error = getString(R.string.field_empty)
                binding.textInputEditTextDcDescription.requestFocus()
                return@setOnClickListener
            }

            if (!isNetworkAvailable(this)){
                showDialogConnection()
            } else{
                updateCustomer()
            }
        }
    }

    private fun setDetailCustomer() {
        val customerParcelable = intent.getParcelableExtra<FetchCustomerModel>(EXTRA_CUSTOMER) as FetchCustomerModel
        model = customerParcelable
        val nameCustomer = binding.textInputEditTextDcNameCustomer as TextView
        val addressCustomer = binding.textInputEditTextDcAddressCustomer as TextView
        val mobileNumberCustomer = binding.textInputEditTextDcMobileNumberCustomer as TextView
        val emailCustomer = binding.textInputEditTextDcEmailCustomer as TextView
        val carBrand = binding.textInputEditTextDcBrandCar as TextView
        val description = binding.textInputEditTextDcDescription as TextView

        nameCustomer.text = customerParcelable.nameCustomer
        addressCustomer.text = customerParcelable.addressCustomer
        mobileNumberCustomer.text = customerParcelable.mobileNumberCustomer
        emailCustomer.text = customerParcelable.emailCustomer
        carBrand.text = customerParcelable.brandCar
        description.text = customerParcelable.descriptionCustomer
    }

    private fun updateCustomer() {

        val username = binding.textInputEditTextDcNameCustomer.text.toString()
        val addressCustomer = binding.textInputEditTextDcAddressCustomer.text.toString()
        val mobileNumberCustomer = binding.textInputEditTextDcMobileNumberCustomer.text.toString()
        val emailCustomer = binding.textInputEditTextDcEmailCustomer.text.toString()
        val carBrand = binding.textInputEditTextDcBrandCar.text.toString()
        val description = binding.textInputEditTextDcDescription.text.toString()

        val db = Firebase.firestore
        db.document("customer/" + model.customerId)
            .update("customerName", username,
                "customerAddress", addressCustomer,
                "customerMobileNumber", mobileNumberCustomer,
                "customerEmail", emailCustomer,
                "carBrand", carBrand,
                "description", description)
            .addOnSuccessListener {
                val alertBuilder = AlertDialog.Builder(this)
                alertBuilder.setTitle(getString(R.string.success_title))
                alertBuilder.setMessage(getString(R.string.updated_customer))
                alertBuilder.setPositiveButton(getString(R.string.oke)) { _, _ ->
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                alertBuilder.show()
                Log.d("ProfileActivity", "Update Success")
            }
            .addOnFailureListener {
                Log.d("ProfileActivity", "Update Failure")
            }
    }

    private fun showDialogConnection() {
        val alertBuilder = AlertDialog.Builder(this)
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