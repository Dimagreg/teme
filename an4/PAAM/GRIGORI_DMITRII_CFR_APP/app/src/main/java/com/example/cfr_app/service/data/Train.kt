package com.example.cfr_app.service.data

import com.google.firebase.Timestamp

data class Train(
    val trainNumber: String = "",
    val originCity: String = "",
    val destinationCity: String = "",
    val departureTimestamp: Timestamp? = null,
    val arrivalTimestamp: Timestamp? = null,
    val lateTimeMinutes: Int = 0,
) {
    constructor() : this("", "", "", null, null, 0)
}