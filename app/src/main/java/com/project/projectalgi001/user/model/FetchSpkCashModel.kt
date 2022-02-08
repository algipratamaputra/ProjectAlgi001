package com.project.projectalgi001.user.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FetchSpkCashModel(
    var dateCash : String? = null,
    var resourceImageCash: HashMap<String, String>? = null,
    var buyerNameCash: String? = null,
    var buyerAddressCash: String? = null,
    var buyerMobileNumberCash: String? = null,
    var buyerEmailCash: String? = null,
    var carNameCash: String? = null,
    var carPoliceNumberCash: String? = null,
    var carMachineNumberCash: String? = null,
    var carChassisNumberCash: String? = null,
    var carPriceCash: String? = null,
    var cashDiscount: Int? = 0,
    var netOfDiscountCash: Int? = 0,
    var cashPrepayment: Int? = 0,
    var plantDelivery: String? = null,
    var paymentRemaining: Int? = 0,
    var notesAdditional: String? = null,
    var userId : String? = null
):Parcelable