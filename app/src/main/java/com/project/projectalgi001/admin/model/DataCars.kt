package com.project.projectalgi001.admin.model

data class DataCars(
        var statusCars : Boolean? = null,
        val nameCar: String? = null,
        val policeNumber: String? = null,
        val machineNumber: String? = null,
        val chassisNumber: String? = null,
        val bpkbStatus: String? = null,
        val stnkStatus: String? = null,
        val cashPrice: String? = null,
        val creditPrice: String? = null,
        val minimumDp: String? = null,
        val imageCars: HashMap<String, String>? = null
)
