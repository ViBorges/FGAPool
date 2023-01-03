package com.tcc.fgapool.models

data class RideItem(
    val origin: String,
    val destination: String,
    val date: String,
    val time: String,
    val driverName: String,
    val driverCourse: String,
    val seatsAvailable: String
)
