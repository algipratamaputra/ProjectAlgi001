package com.project.projectalgi001.user.model

data class SpkModel(
    val date : String? = null,
    val imageResource: HashMap<String, String>? = null,
    val nameBuyer: String? = null,
    val addressBuyer: String? = null,
    val mobileNumberBuyer: String? = null,
    val emailBuyer: String? = null,
    val nameCarSold: String? = null,
    val policeNumberCarSold: String? = null,
    val machineNumberCarSold: String? = null,
    val chassisNumberCarSold: String? = null,
    val priceCarSold: String? = null,
    val discountCash: Int? = 0,
    val netOfDiscount: Int? = 0,
    val prepaymentCash: Int? = 0,
    val deliveryPlant: String? = null,
    val remainingPayment: Int? = 0,
    val additionalNotes: String? = null,
    val idUser : String? = null
)