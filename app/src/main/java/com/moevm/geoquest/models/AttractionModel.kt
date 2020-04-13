package com.moevm.geoquest.models

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.android.gms.maps.model.LatLng

enum class AttractionStatus {
    Warmer,
    Colder,
    Success,
    Nothing,
    QuestCompleted
}

data class AttractionModel(val nm: String = "", val coord: LatLng = LatLng(0.0, 0.0), val trig: Float = 0.0f) : Parcelable {
    var name: String = nm
    var coordinates: LatLng = coord
    var triggerZone: Float = trig
    var completed: Boolean = false

    constructor(parcel: Parcel) : this() {
        this.name = parcel.readString().toString()
        this.coordinates = LatLng(parcel.readDouble(), parcel.readDouble())
        this.triggerZone = parcel.readFloat()
        this.completed = parcel.readBoolean()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(this.name)
        parcel.writeDouble(this.coordinates.latitude)
        parcel.writeDouble(this.coordinates.longitude)
        parcel.writeFloat(this.triggerZone)
        parcel.writeBoolean(this.completed)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AttractionModel> {
        override fun createFromParcel(parcel: Parcel): AttractionModel {
            Log.d("parcelable", "AttractionModel call createFromParcel")
            return AttractionModel(parcel)
        }

        override fun newArray(size: Int): Array<AttractionModel?> {
            Log.d("parcelable", "call newArray")
            return arrayOfNulls(size)
        }
    }
}