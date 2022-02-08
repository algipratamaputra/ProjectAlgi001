package com.project.projectalgi001.user.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FetchCustomerModel(
    var customerId: String = " ",
    var userId: String = "",
    var nameCustomer: String = "",
    var addressCustomer: String = "",
    var mobileNumberCustomer: String = "",
    var emailCustomer: String = "",
    var brandCar: String = "",
    var descriptionCustomer: String = ""): Parcelable