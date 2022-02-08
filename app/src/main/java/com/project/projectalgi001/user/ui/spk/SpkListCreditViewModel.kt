package com.project.projectalgi001.user.ui.spk

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.projectalgi001.user.model.FetchSpkCreditModel
import com.project.projectalgi001.user.model.SpkCreditModel

class SpkListCreditViewModel : ViewModel() {

    private val listViewModelSpkCredit = MutableLiveData<ArrayList<FetchSpkCreditModel>>()
    private val listSpkCredit = ArrayList<FetchSpkCreditModel>()

    fun fetchSpkCreditViewModel() {
        val db = Firebase.firestore
        val auth = Firebase.auth
        listSpkCredit.clear()
        db.collection("spkCredit")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("fetchSpkCredit", "${document.id} => ${document.data}" )
                    val spkCredit = FetchSpkCreditModel()
                    val dataSpkCredit = document.toObject(SpkCreditModel::class.java)

                    if (auth.currentUser!!.uid.lowercase() != dataSpkCredit.idUser?.lowercase())
                        continue

                    spkCredit.dateCredit = dataSpkCredit.date
                    spkCredit.imageResourceCredit = dataSpkCredit.imageResource
                    spkCredit.nameBuyerCredit = dataSpkCredit.nameBuyer
                    spkCredit.addressBuyerCredit = dataSpkCredit.addressBuyer
                    spkCredit.mobileNumberBuyerCredit = dataSpkCredit.mobileNumberBuyer
                    spkCredit.emailBuyerCredit = dataSpkCredit.emailBuyer
                    spkCredit.nameCarSoldCredit = dataSpkCredit.nameCarSold
                    spkCredit.policeNumberCarSoldCredit = dataSpkCredit.policeNumberCarSold
                    spkCredit.machineNumberCarSoldCredit = dataSpkCredit.machineNumberCarSold
                    spkCredit.chassisNumberCarSoldCredit = dataSpkCredit.chassisNumberCarSold
                    spkCredit.priceCarSoldCredit = dataSpkCredit.priceCarSold
                    spkCredit.finance = dataSpkCredit.finance
                    spkCredit.creditDiscount = dataSpkCredit.discountCredit
                    spkCredit.creditNetOfDiscount = dataSpkCredit.netOfDiscountCredit
                    spkCredit.creditPrepayment = dataSpkCredit.prepaymentCredit
                    spkCredit.creditDownPayment = dataSpkCredit.downPayment
                    spkCredit.downPaymentRemaining = dataSpkCredit.remainingDownPayment
                    spkCredit.creditTenor = dataSpkCredit.tenor
                    spkCredit.creditMonthlyInstallment = dataSpkCredit.monthlyInstallment
                    spkCredit.creditAdditionalNotes = dataSpkCredit.additionalNotes
                    spkCredit.userId = dataSpkCredit.idUser
                    listSpkCredit.add(spkCredit)
                }
                listViewModelSpkCredit.postValue(listSpkCredit)
            }
            .addOnFailureListener { exception ->
                Log.d("fetchCars", "Error getting documents: ", exception)
            }
    }

    fun searchSpkCreditViewModel(input : String) {

        val db = Firebase.firestore
        var countSpkCredit = 0
        listSpkCredit.clear()
        db.collection("spkCredit")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("fetchSpkCredit", "${document.id} => ${document.data}" )
                    val spkCredit = FetchSpkCreditModel()
                    val dataSpkCredit = document.toObject(SpkCreditModel::class.java)

                    val (inputName,searchedName)  = if (input.length > dataSpkCredit.nameBuyer!!.length) Pair(input.substring(0, dataSpkCredit.nameBuyer.length).lowercase() ,dataSpkCredit.nameBuyer.lowercase() )else Pair(dataSpkCredit.nameBuyer.substring(0,input.length).lowercase(), input.lowercase())
                    val (inputCar,searchedCar)  = if (input.length > dataSpkCredit.nameCarSold!!.length) Pair(input.substring(0, dataSpkCredit.nameCarSold.length).lowercase() ,dataSpkCredit.nameCarSold.lowercase() )else Pair(dataSpkCredit.nameCarSold.substring(0,input.length).lowercase(), input.lowercase())

                    if (inputName == searchedName || inputCar == searchedCar){

                        spkCredit.dateCredit = dataSpkCredit.date
                        spkCredit.imageResourceCredit = dataSpkCredit.imageResource
                        spkCredit.nameBuyerCredit = dataSpkCredit.nameBuyer
                        spkCredit.addressBuyerCredit = dataSpkCredit.addressBuyer
                        spkCredit.mobileNumberBuyerCredit = dataSpkCredit.mobileNumberBuyer
                        spkCredit.emailBuyerCredit = dataSpkCredit.emailBuyer
                        spkCredit.nameCarSoldCredit = dataSpkCredit.nameCarSold
                        spkCredit.policeNumberCarSoldCredit = dataSpkCredit.policeNumberCarSold
                        spkCredit.machineNumberCarSoldCredit = dataSpkCredit.machineNumberCarSold
                        spkCredit.chassisNumberCarSoldCredit = dataSpkCredit.chassisNumberCarSold
                        spkCredit.priceCarSoldCredit = dataSpkCredit.priceCarSold
                        spkCredit.finance = dataSpkCredit.finance
                        spkCredit.creditDiscount = dataSpkCredit.discountCredit
                        spkCredit.creditNetOfDiscount = dataSpkCredit.netOfDiscountCredit
                        spkCredit.creditPrepayment = dataSpkCredit.prepaymentCredit
                        spkCredit.creditDownPayment = dataSpkCredit.downPayment
                        spkCredit.downPaymentRemaining = dataSpkCredit.remainingDownPayment
                        spkCredit.creditTenor = dataSpkCredit.tenor
                        spkCredit.creditMonthlyInstallment = dataSpkCredit.monthlyInstallment
                        spkCredit.creditAdditionalNotes = dataSpkCredit.additionalNotes
                        spkCredit.userId = dataSpkCredit.idUser
                        countSpkCredit++
                        listViewModelSpkCredit.postValue(listSpkCredit)
                        listSpkCredit.add(spkCredit)
                    }
                }
                if (countSpkCredit == 0) {
                    listSpkCredit.clear()
                }
                return@addOnSuccessListener
            }
            .addOnFailureListener { exception ->
                Log.d("fetchCars", "Error getting documents: ", exception)
            }
    }

    fun getSpkCredit(): LiveData<ArrayList<FetchSpkCreditModel>> {
        return listViewModelSpkCredit
    }
}