package com.tcc.fgapool.models

data class Ride(
    val origin: String? = null,
    val destination: String? = null,
    val route: String? = null,
    val date: String? = null,
    val time: String? = null,
    val seatsAvailable: String? = null,
    val sameSexPassengers: Boolean? = null,
    val isActive: Boolean? = null,
    val userId: String? = null,
    val driverName: String? = null
)
