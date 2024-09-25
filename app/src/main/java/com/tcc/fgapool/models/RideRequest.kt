package com.tcc.fgapool.models

data class RideRequest(
    val rideKey: String? = null,
    val passengerID: String? = null,
    val driverID: String? = null,
    val passengerName: String? = null,
    val passengerNumber: String? = null,
    val requestKey: String? = null
)
