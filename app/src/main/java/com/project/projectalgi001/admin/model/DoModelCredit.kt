package com.project.projectalgi001.admin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DoModelCredit(
    var dateCreditDo : String? = null,
    var imageResourceCreditDo: HashMap<String, String>? = null,
    var nameBuyerCreditDo: String? = null,
    var addressBuyerCreditDo: String? = null,
    var mobileNumberBuyerCreditDo: String? = null,
    var emailBuyerCreditDo: String? = null,
    var nameCarSoldCreditDo: String? = null,
    var policeNumberCarSoldCreditDo: String? = null,
    var machineNumberCarSoldCreditDo: String? = null,
    var chassisNumberCarSoldCreditDo: String? = null,
    var priceCarSoldCreditDo: String? = null,
    var financeDo: String? = null,
    var creditDiscountDo: String? = null,
    var creditNetOfDiscountDo: String? = null,
    var creditPrepaymentDo: String? = null,
    var creditDownPaymentDo: String? = null,
    var downPaymentRemainingDo: String? = null,
    var creditTenorDo: String? = null,
    var creditMonthlyInstallmentDo : String? = null,
    var creditAdditionalNotesDo: String? = null,
    var userId : String? = null
):Parcelable