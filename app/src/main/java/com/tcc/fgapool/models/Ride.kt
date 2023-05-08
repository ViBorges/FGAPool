package com.tcc.fgapool.models

import android.os.Parcel
import android.os.Parcelable

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
    val userId: String? = null,
    val driverName: String? = null,
    val passenger1: String? = null,
    val passenger2: String? = null,
    val passenger3: String? = null,
    val passenger4: String? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(rideKey)
        parcel.writeString(origin)
        parcel.writeString(destination)
        parcel.writeString(route)
        parcel.writeString(date)
        parcel.writeString(time)
        parcel.writeString(seatsAvailable)
        parcel.writeValue(sameSexPassengers)
        parcel.writeValue(isActive)
        parcel.writeString(userId)
        parcel.writeString(driverName)
        parcel.writeString(passenger1)
        parcel.writeString(passenger2)
        parcel.writeString(passenger3)
        parcel.writeString(passenger4)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Ride> {
        override fun createFromParcel(parcel: Parcel): Ride {
            return Ride(parcel)
        }

        override fun newArray(size: Int): Array<Ride?> {
            return arrayOfNulls(size)
        }
    }
}