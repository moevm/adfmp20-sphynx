package com.moevm.geoquest.models

import com.google.android.gms.maps.model.LatLng

enum class AttractionStatus {
    Warmer,
    Colder,
    Success,
    Nothing
}

data class AttractionModel(
    val coordinates: LatLng,
    val triggerZone: Float,
    val completed: Boolean = false
)
