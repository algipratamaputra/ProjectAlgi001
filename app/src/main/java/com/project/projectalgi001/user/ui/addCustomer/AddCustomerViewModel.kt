package com.project.projectalgi001.user.ui.addCustomer

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.projectalgi001.user.model.CustomerModel

class AddCustomerViewModel : ViewModel() {

    fun save(customer: CustomerModel) {
        val  db = Firebase.firestore
        val newCustomer : DocumentReference = db.collection("customer").document()

        customer.idCustomer = newCustomer.id

        newCustomer.set(customer).addOnCompleteListener {
         Log.d("add", "Berhasil")
        }
            .addOnFailureListener {
                Log.d("add", "gagal")
            }
    }
}