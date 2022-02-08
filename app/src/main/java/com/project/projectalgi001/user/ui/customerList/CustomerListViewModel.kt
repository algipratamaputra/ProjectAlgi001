package com.project.projectalgi001.user.ui.customerList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.projectalgi001.user.model.CustomerModel
import com.project.projectalgi001.user.model.FetchCustomerModel

class CustomerListViewModel : ViewModel() {
    private val listViewModelCustomer = MutableLiveData<ArrayList<FetchCustomerModel>>()
    private val listCustomer = ArrayList<FetchCustomerModel>()

    fun fetchCustomerViewModel() {
        val db = Firebase.firestore
        val auth = Firebase.auth
        listCustomer.clear()
        db.collection("customer")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("fetchCars", "${document.id} => ${document.data}" )
                    val customer = FetchCustomerModel()
                    val dataCustomer = document.toObject(CustomerModel::class.java)

                    if (auth.currentUser!!.uid.lowercase() != dataCustomer.idUser.lowercase())
                        continue

                    customer.customerId = dataCustomer.idCustomer
                    customer.userId = dataCustomer.idUser
                    customer.nameCustomer = dataCustomer.customerName
                    customer.addressCustomer = dataCustomer.customerAddress
                    customer.mobileNumberCustomer = dataCustomer.customerMobileNumber
                    customer.emailCustomer = dataCustomer.customerEmail
                    customer.brandCar = dataCustomer.carBrand
                    customer.descriptionCustomer = dataCustomer.description
                    listCustomer.add(customer)
                }
                listViewModelCustomer.postValue(listCustomer)
            }
            .addOnFailureListener { exception ->
                Log.d("fetchCars", "Error getting documents: ", exception)
            }
    }

    fun searchCustomer(input: String){
        val db = Firebase.firestore
        var countCostumer = 0
        listCustomer.clear()
        db.collection("customer")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("fetchCars", "${document.id} => ${document.data}" )
                    val customer = FetchCustomerModel()
                    val dataCustomer = document.toObject(CustomerModel::class.java)
                    val (inputText,searchedText)  = if (input.length > dataCustomer.customerName.length) Pair(input.substring(0, dataCustomer.customerName.length).lowercase() ,dataCustomer.customerName.lowercase() )else Pair(dataCustomer.customerName.substring(0,input.length).lowercase(), input.lowercase())
                    val (inputCar,searchedCar)  = if (input.length > dataCustomer.carBrand.length) Pair(input.substring(0, dataCustomer.carBrand.length).lowercase() ,dataCustomer.carBrand.lowercase() )else Pair(dataCustomer.carBrand.substring(0,input.length).lowercase(), input.lowercase())

                    if (inputText == searchedText || inputCar == searchedCar){

                        customer.customerId = dataCustomer.idCustomer
                        customer.userId = dataCustomer.idUser
                        customer.nameCustomer = dataCustomer.customerName
                        customer.addressCustomer = dataCustomer.customerAddress
                        customer.mobileNumberCustomer = dataCustomer.customerMobileNumber
                        customer.emailCustomer = dataCustomer.customerEmail
                        customer.brandCar = dataCustomer.carBrand
                        customer.descriptionCustomer = dataCustomer.description
                        countCostumer++

                        listViewModelCustomer.postValue(listCustomer)

                        listCustomer.add(customer)
                    }
                }
                if (countCostumer == 0) {
                    listCustomer.clear()
                }
                return@addOnSuccessListener
            }
            .addOnFailureListener { exception ->
                Log.d("fetchCars", "Error getting documents: ", exception)
            }
    }

    fun getCustomer():LiveData<ArrayList<FetchCustomerModel>> {
        return listViewModelCustomer
    }
}