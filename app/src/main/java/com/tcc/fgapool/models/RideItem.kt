package com.tcc.fgapool.models

data class RideItem(
    val origin: String? = null,
    val destination: String? = null,
    val date: String? = null,
    val time: String? = null,
    val driverName: String? = null,
    val driverCourse: String? = null,
    val seatsAvailable: String? = null
)
