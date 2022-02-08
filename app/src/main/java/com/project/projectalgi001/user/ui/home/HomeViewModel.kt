package com.project.projectalgi001.user.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.projectalgi001.admin.model.DataCars
import com.project.projectalgi001.user.model.Cars

class HomeViewModel : ViewModel() {

    private var listViewModel =MutableLiveData<ArrayList<Cars>>()
    private val listCars = ArrayList<Cars>()



    fun fetchCarsAgain() {
        val db = Firebase.firestore
        listCars.clear()
        db.collection("cars")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d("fetchCars", "${document.id} => ${document.data}" )
                        val cars = Cars()
                        val data = document.toObject(DataCars::class.java)
                        cars.carStatus = data.statusCars
                        cars.nameCars = data.nameCar
                        cars.priceCars = data.cashPrice.toString()
                        cars.numberPolice = data.policeNumber
                        cars.numberMachine = data.machineNumber
                        cars.numberChassis = data.chassisNumber
                        cars.statusBpkb = data.bpkbStatus
                        cars.statusStnk = data.stnkStatus
                        cars.creditPriceCars = data.creditPrice
                        cars.dpMinimum = data.minimumDp
                        cars.carsImage = data.imageCars
                        listCars.add(cars)
                    }

                    listViewModel.postValue(listCars)
                }
                .addOnFailureListener { exception ->
            Log.d("fetchCars", "Error getting documents: ", exception)
        }
    }

    fun searchCar(input : String) {
        val db = Firebase.firestore
        var cars: Cars?
        var countData = 0
        db.collection("cars").get().addOnSuccessListener { result ->
            listCars.clear()
            for (document in result){

                val data = document.toObject(DataCars::class.java)
                val (inputText,searchedText)  = if (input.length > data.nameCar?.length!!) Pair(input.substring(0, data.nameCar.length).lowercase() ,data.nameCar.lowercase() )else Pair(data.nameCar.substring(0,input.length).lowercase(), input.lowercase())
                val (priceInput,priceSearch) = if (input.length > data.cashPrice?.length!!) Pair(input.substring(0,data.cashPrice.length),data.cashPrice) else Pair(data.cashPrice.substring(0,input.length),input)

                if (inputText == searchedText || priceInput == priceSearch){
                    cars = Cars()
                    cars?.carStatus = data.statusCars
                    cars?.nameCars = data.nameCar
                    cars?.priceCars = data.cashPrice.toString()
                    cars?.numberPolice = data.policeNumber
                    cars?.numberMachine = data.machineNumber
                    cars?.numberChassis = data.chassisNumber
                    cars?.statusBpkb = data.bpkbStatus
                    cars?.statusStnk = data.stnkStatus
                    cars?.creditPriceCars = data.creditPrice
                    cars?.dpMinimum = data.minimumDp
                    cars?.carsImage = data.imageCars
                    countData++

                    listViewModel.postValue(listCars)
                    listCars.add(cars!!)
                }
            }
            if (countData == 0) {
                listCars.clear()
            }
            return@addOnSuccessListener
        }
    }

    fun getCars():LiveData<ArrayList<Cars>> {
        return listViewModel
    }
}