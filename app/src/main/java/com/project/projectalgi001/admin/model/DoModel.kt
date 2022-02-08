package com.project.projectalgi001.admin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DoModel(
    var dateCashDo : String? = null,
    var resourceImageCashDo: HashMap<String, String>? = null,
    var buyerNameCashDo: String? = null,
    var buyerAddressCashDo: String? = null,
    var buyerMobileNumberCashDo: String? = null,
    var buyerEmailCashDo: String? = null,
    var carNameCashDo: String? = null,
    var carPoliceNumberCashDo: String? = null,
    var carMachineNumberCashDo: String? = null,
    var carChassisNumberCashDo: String? = null,
    var carPriceCashDo: String? = null,
    var cashDiscountDo: Int? = 0,
    var netOfDiscountCashDo: Int? = 0,
    var cashPrepaymentDo: Int? = 0,
    var plantDeliveryDo: String? = null,
    var paymentRemainingDo: Int? = 0,
    var notesAdditionalDo: String? = null,
    var userIdDo : String? = null
):Parcelable