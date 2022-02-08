package com.project.projectalgi001.user.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomerModel(
    var idCustomer: String = " ",
    var idUser: String = "",
    var customerName: String = "",
    var customerAddress: String = "",
    var customerMobileNumber: String = "",
    var customerEmail: String = "",
    var carBrand: String = "",
    var description: String = ""):Parcelable