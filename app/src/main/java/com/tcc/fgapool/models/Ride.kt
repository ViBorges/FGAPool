package com.tcc.fgapool.models

data class Ride(
    val rideKey: String? = null,
    val origin: String? = null,
    val destination: String? = null,
    val route: String? = null,
    val date: String? = null,
    val time: String? = null,
    val seatsAvailable: String? = null,
    val sameSexPassengers: Boolean? = null,
    val isActive: Boolean? = null,
    var userId: String,
    val driverName: String? = null
)
