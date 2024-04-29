package com.dldev.tcom.network.models

import android.os.Parcel
import android.os.Parcelable

data class Vehicle(
    val vehicleID: Int,
    val vehicleTypeID: Int,
    val imageURL: String,
    val name: String,
    val location: Location,
    val rating: Float,
    val price: Int,
    var isFavorite: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Location::class.java.classLoader) ?: Location(0.0, 0.0),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(vehicleID)
        parcel.writeInt(vehicleTypeID)
        parcel.writeString(imageURL)
        parcel.writeString(name)
        parcel.writeParcelable(location, flags)
        parcel.writeFloat(rating)
        parcel.writeInt(price)
        parcel.writeByte(if (isFavorite) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Vehicle> {
        override fun createFromParcel(parcel: Parcel): Vehicle {
            return Vehicle(parcel)
        }

        override fun newArray(size: Int): Array<Vehicle?> {
            return arrayOfNulls(size)
        }
    }
}
