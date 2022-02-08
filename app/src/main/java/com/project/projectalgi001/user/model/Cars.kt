package com.project.projectalgi001.user.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cars (
        var carStatus: Boolean? = null,
        var nameCars: String? = null,
        var numberPolice: String? = null,
        var numberMachine: String? = null,
        var numberChassis: String? = null,
        var statusBpkb: String? = null,
        var statusStnk: String? = null,
        var priceCars: String? = null,
        var creditPriceCars: String? = null,
        var dpMinimum: String? = null,
        var carsImage: HashMap<String, String>? = null) :Parcelable