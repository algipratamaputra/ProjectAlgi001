package com.project.projectalgi001.user.model

data class SpkCreditModel(
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
    val finance: String? = null,
    val discountCredit: String? = null,
    val netOfDiscountCredit: String? = null,
    val prepaymentCredit: String? =null,
    val downPayment: String? = null,
    val remainingDownPayment: String? = null,
    val tenor: String? = null,
    val monthlyInstallment : String? = null,
    val additionalNotes: String? = null,
    val idUser : String? = null
)