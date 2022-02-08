package com.project.projectalgi001.admin.ui.listspk

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.projectalgi001.user.model.FetchSpkCashModel
import com.project.projectalgi001.user.model.SpkModel

class ListSpkViewModel : ViewModel() {
    private val listViewModelSpkCash = MutableLiveData<ArrayList<FetchSpkCashModel>>()
    private val listSpkCash = ArrayList<FetchSpkCashModel>()

    fun fetchSpkCashViewModel() {
        val db = Firebase.firestore
        listSpkCash.clear()
        db.collection("spkCash")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("fetchSpkCash", "${document.id} => ${document.data}" )
                    val spkCash = FetchSpkCashModel()
                    val dataSpkCash = document.toObject(SpkModel::class.java)

                    spkCash.dateCash = dataSpkCash.date
                    spkCash.resourceImageCash = dataSpkCash.imageResource
                    spkCash.buyerNameCash = dataSpkCash.nameBuyer
                    spkCash.buyerAddressCash = dataSpkCash.addressBuyer
                    spkCash.buyerMobileNumberCash = dataSpkCash.mobileNumberBuyer
                    spkCash.buyerEmailCash = dataSpkCash.emailBuyer
                    spkCash.carNameCash = dataSpkCash.nameCarSold
                    spkCash.carPoliceNumberCash = dataSpkCash.policeNumberCarSold
                    spkCash.carMachineNumberCash = dataSpkCash.machineNumberCarSold
                    spkCash.carChassisNumberCash = dataSpkCash.chassisNumberCarSold
                    spkCash.carPriceCash = dataSpkCash.priceCarSold
                    spkCash.cashDiscount = dataSpkCash.discountCash
                    spkCash.netOfDiscountCash = dataSpkCash.netOfDiscount
                    spkCash.cashPrepayment = dataSpkCash.prepaymentCash
                    spkCash.plantDelivery = dataSpkCash.deliveryPlant
                    spkCash.paymentRemaining = dataSpkCash.remainingPayment
                    spkCash.notesAdditional = dataSpkCash.additionalNotes
                    spkCash.userId = dataSpkCash.idUser
                    listSpkCash.add(spkCash)
                }
                listViewModelSpkCash.postValue(listSpkCash)
            }
            .addOnFailureListener { exception ->
                Log.d("fetchCars", "Error getting documents: ", exception)
            }
    }

    fun searchSpkCashViewModel(input: String) {
        val db = Firebase.firestore
        var countSpkCash = 0
        listSpkCash.clear()
        db.collection("spkCash")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("fetchSpkCash", "${document.id} => ${document.data}" )
                    val spkCash = FetchSpkCashModel()
                    val dataSpkCash = document.toObject(SpkModel::class.java)
                    val (inputName,searchedName)  = if (input.length > dataSpkCash.nameBuyer!!.length) Pair(input.substring(0, dataSpkCash.nameBuyer.length).lowercase() ,dataSpkCash.nameBuyer.lowercase() )else Pair(dataSpkCash.nameBuyer.substring(0,input.length).lowercase(), input.lowercase())
                    val (inputCar,searchedCar)  = if (input.length > dataSpkCash.nameCarSold!!.length) Pair(input.substring(0, dataSpkCash.nameCarSold.length).lowercase() ,dataSpkCash.nameCarSold.lowercase() )else Pair(dataSpkCash.nameCarSold.substring(0,input.length).lowercase(), input.lowercase())

                    if (inputName == searchedName || inputCar == searchedCar){

                        spkCash.dateCash = dataSpkCash.date
                        spkCash.resourceImageCash = dataSpkCash.imageResource
                        spkCash.buyerNameCash = dataSpkCash.nameBuyer
                        spkCash.buyerAddressCash = dataSpkCash.addressBuyer
                        spkCash.buyerMobileNumberCash = dataSpkCash.mobileNumberBuyer
                        spkCash.buyerEmailCash = dataSpkCash.emailBuyer
                        spkCash.carNameCash = dataSpkCash.nameCarSold
                        spkCash.carPoliceNumberCash = dataSpkCash.policeNumberCarSold
                        spkCash.carMachineNumberCash = dataSpkCash.machineNumberCarSold
                        spkCash.carChassisNumberCash = dataSpkCash.chassisNumberCarSold
                        spkCash.carPriceCash = dataSpkCash.priceCarSold
                        spkCash.cashDiscount = dataSpkCash.discountCash
                        spkCash.netOfDiscountCash = dataSpkCash.netOfDiscount
                        spkCash.cashPrepayment = dataSpkCash.prepaymentCash
                        spkCash.plantDelivery = dataSpkCash.deliveryPlant
                        spkCash.paymentRemaining = dataSpkCash.remainingPayment
                        spkCash.notesAdditional = dataSpkCash.additionalNotes
                        spkCash.userId = dataSpkCash.idUser
                        countSpkCash++
                        listViewModelSpkCash.postValue(listSpkCash)
                        listSpkCash.add(spkCash)
                    }
                }
                if (countSpkCash == 0) {
                    listSpkCash.clear()
                }
                return@addOnSuccessListener
            }
            .addOnFailureListener { exception ->
                Log.d("fetchCars", "Error getting documents: ", exception)
            }
    }

    fun getSpkCash(): LiveData<ArrayList<FetchSpkCashModel>> {
        return listViewModelSpkCash
    }
}