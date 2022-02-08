package com.project.projectalgi001.user.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FetchSpkCreditModel(
    var dateCredit : String? = null,
    var imageResourceCredit: HashMap<String, String>? = null,
    var nameBuyerCredit: String? = null,
    var addressBuyerCredit: String? = null,
    var mobileNumberBuyerCredit: String? = null,
    var emailBuyerCredit: String? = null,
    var nameCarSoldCredit: String? = null,
    var policeNumberCarSoldCredit: String? = null,
    var machineNumberCarSoldCredit: String? = null,
    var chassisNumberCarSoldCredit: String? = null,
    var priceCarSoldCredit: String? = null,
    var finance: String? = null,
    var creditDiscount: String? = null,
    var creditNetOfDiscount: String? = null,
    var creditPrepayment: String? = null,
    var creditDownPayment: String? = null,
    var downPaymentRemaining: String? = null,
    var creditTenor: String? = null,
    var creditMonthlyInstallment : String? = null,
    var creditAdditionalNotes: String? = null,
    var userId : String? = null
) : Parcelable